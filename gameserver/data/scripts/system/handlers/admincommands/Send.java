/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_CUSTOM_PACKET;
import com.ne.gs.network.aion.serverpackets.SM_CUSTOM_PACKET.PacketElementType;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * This admin command is used for sending custom packets from server to client.
 * <p/>
 * Sends packets based on xml mappings in folder "./data/packets".<br />
 * Command details: "//send [1]<br />
 * * 1 - packet mappings name.<br />
 * * - 'demo' for file './data/packets/demo.xml'<br />
 * * - 'test' for file './data/packets/test.xml'<br />
 * * Reciever is a targetted by admin player. If target is 'null' or not a Player - sends to admin. <br />
 * <p/>
 * Created on: 14.07.2009 13:54:46
 *
 * @author Aquanox
 */
public class Send extends ChatCommand {

    private static Logger logger = LoggerFactory.getLogger(Send.class);

    private static File FOLDER = new File("./data/packets");

    private Unmarshaller unmarshaller;

    public Send() {
        try {
            unmarshaller = JAXBContext.newInstance(Packets.class, Packet.class, Part.class).createUnmarshaller();
        } catch (JAXBException e) {
            logger.error("", e);
        }
    }

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length != 1) {
            admin.sendMsg("Example: //send [file] ");
            return;
        }

        String mappingName = params[0];
        Player target = getTargetPlayer(admin);

        // logger.debug("Mapping: " + mappingName);
        // logger.debug("Target: " + target);

        File packetsData = new File(FOLDER, mappingName + ".xml");

        if (!packetsData.exists()) {
            admin.sendMsg("Mapping with name " + mappingName + " not found");
            return;
        }

        Packets packetsTemplate;

        try {
            packetsTemplate = (Packets) unmarshaller.unmarshal(packetsData);
        } catch (JAXBException e) {
            logger.error("Unmarshalling error", e);
            return;
        }

        if (packetsTemplate.getPackets().isEmpty()) {
            admin.sendMsg("No packets to send.");
            return;
        }

        send(admin, target, packetsTemplate);
    }

    private void send(Player sender, final Player target, Packets packets) {
        String senderObjectId = String.valueOf(sender.getObjectId());
        String targetObjectId = String.valueOf(target.getObjectId());

        long delay = 0;
        for (Packet packetTemplate : packets) {
            // logger.debug("Processing: " + packetTemplate);

            final SM_CUSTOM_PACKET packet = new SM_CUSTOM_PACKET(packetTemplate.getOpcode());

            for (Part part : packetTemplate.getParts()) {
                PacketElementType byCode = PacketElementType.getByCode(part.getType());

                String value = part.getValue();

                if (value.contains("${objectId}")) {
                    value = value.replace("${objectId}", targetObjectId);
                }
                if (value.contains("${senderObjectId}")) {
                    value = value.replace("${senderObjectId}", senderObjectId);
                }
                if (value.contains("${targetObjectId}")) {
                    value = value.replace("${targetObjectId}", targetObjectId);
                }

                if (part.getRepeatCount() == 1) {
                    packet.addElement(byCode, value);
                } else {
                    for (int i = 0; i < part.getRepeatCount(); i++) {
                        packet.addElement(byCode, value);
                    }
                }
            }

            delay += packetTemplate.getDelay();

            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    // logger.debug("Sending: " + packetTemplate);
                    target.sendPck(packet);
                }
            }, delay);

            delay += packets.getDelay();
        }
    }

    private Player getTargetPlayer(Player admin) {
        if (admin.getTarget() instanceof Player) {
            return (Player) admin.getTarget();
        }
        return admin;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "packets")
    private static class Packets implements Iterable<Packet> {

        @XmlElement(name = "packet")
        private List<Packet> packets = new ArrayList<>();

        @XmlAttribute(name = "delay")
        private long delay = -1;

        public long getDelay() {
            return delay;
        }

        public List<Packet> getPackets() {
            return packets;
        }

        @SuppressWarnings("unused")
        public boolean add(Packet packet) {
            return packets.add(packet);
        }

        @Override
        public Iterator<Packet> iterator() {
            return packets.iterator();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Packets");
            sb.append("{delay=").append(delay);
            sb.append(", packets=").append(packets);
            sb.append('}');
            return sb.toString();
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "packet")
    private static class Packet {

        @XmlElement(name = "part")
        private Collection<Part> parts = new ArrayList<>();

        @XmlAttribute(name = "opcode")
        private String opcode = "-1";

        @XmlAttribute(name = "delay")
        private long delay = 0;

        public int getOpcode() {
            return Integer.decode(opcode);
        }

        public Collection<Part> getParts() {
            return parts;
        }

        public long getDelay() {
            return delay;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Packet");
            sb.append("{opcode=").append(opcode);
            sb.append(", parts=").append(parts);
            sb.append('}');
            return sb.toString();
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "part")
    private static class Part {

        @XmlAttribute(name = "type", required = true)
        private String type = null;

        @XmlAttribute(name = "value", required = true)
        private String value = null;

        @XmlAttribute(name = "repeat", required = true)
        private int repeatCount = 1;

        public char getType() {
            return type.charAt(0);
        }

        public String getValue() {
            return value;
        }

        public int getRepeatCount() {
            return repeatCount;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Part");
            sb.append("{type='").append(type).append('\'');
            sb.append(", value='").append(value).append('\'');
            sb.append(", repeatCount=").append(repeatCount);
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub
    }
}
