/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.io.File;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_CUSTOM_PACKET;
import com.ne.gs.network.aion.serverpackets.SM_CUSTOM_PACKET.PacketElementType;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * Send packet in raw format.
 *
 * @author Luno
 * @author Aquanox
 */
public class Raw extends ChatCommand {

    private static final File ROOT = new File("data/packets/");

    private static final Logger logger = LoggerFactory.getLogger(Raw.class);

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length != 1) {
            admin.sendMsg("Usage: //raw [name]");
            return;
        }

        File file = new File(ROOT, params[0] + ".txt");

        if (!file.exists() || !file.canRead()) {
            admin.sendMsg("Wrong file selected.");
            return;
        }

        try {
            List<String> lines = FileUtils.readLines(file);

            SM_CUSTOM_PACKET packet = null;
            admin.sendMsg("lines " + lines.size());
            boolean init = false;
            for (int r = 0; r < lines.size(); r++) {
                String row = lines.get(r);
                String[] tokens = row.substring(0, 48).trim().split(" ");
                int len = tokens.length;

                for (int i = 0; i < len; i++) {
                    if (!init) {
                        if (i == 1) {
                            packet = new SM_CUSTOM_PACKET(Integer.decode("0x" + tokens[i] + tokens[i - 1]));
                            init = true;
                        }
                    } else if (r > 0 || i > 4) {
                        packet.addElement(PacketElementType.C, "0x" + tokens[i]);
                    }
                }
            }
            if (packet != null) {
                admin.sendMsg("Packet send..");
                admin.sendPck(packet);
            }
        } catch (Exception e) {
            admin.sendMsg("An error has occurred.");
            logger.warn("IO Error.", e);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Usage: //raw [name]");
    }
}
