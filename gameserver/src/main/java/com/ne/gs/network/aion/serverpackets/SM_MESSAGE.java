/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.serverpackets;

import com.ne.commons.func.tuple.Tuple;
import com.ne.gs.model.ChatType;
import com.ne.gs.model.Race;
import com.ne.gs.model.conds.CanReadChatMessageCond;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.AionConnection;
import com.ne.gs.network.aion.AionServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Massage [chat, etc]
 *
 * @author -Nemesiss-, Sweetkr
 */
public class SM_MESSAGE extends AionServerPacket {

    /**
     * Player.
     */
    private Player speaker;
    /**
     * Object that is saying smth or null.
     */
    private final int senderObjectId;

    /**
     * Message.
     */
    private final String message;

    /**
     * Name of the sender
     */
    private final String senderName;

    /**
     * Sender race
     */
    private Race race;

    /**
     * Chat type
     */
    private final ChatType chatType;

    /**
     * Sender coordinates
     */
    private float x;
    private float y;
    private float z;
    private static final Logger log = LoggerFactory.getLogger(SM_MESSAGE.class);

    /**
     * Constructs new <tt>SM_MESSAGE </tt> packet
     *
     * @param speaker who sent message
     * @param message actual message
     * @param chatType what chat type should be used
     */
    public SM_MESSAGE(Player speaker, String message, ChatType chatType) {
        this.speaker = speaker;
        senderObjectId = speaker.getObjectId();
        senderName = speaker.getName();
        this.message = message;
        race = speaker.getRace();
        this.chatType = chatType;
        x = speaker.getX();
        y = speaker.getY();
        z = speaker.getZ();
    }

    /**
     * Manual creation of chat message.<br>
     *
     * @param senderObjectId can be 0 if system message(like announcements)
     * @param senderName used for shout ATM, can be null in other cases
     * @param message actual text
     * @param chatType type of chat, Normal, Shout, Announcements, Etc...
     */
    public SM_MESSAGE(int senderObjectId, String senderName, String message, ChatType chatType) {
        this.senderObjectId = senderObjectId;
        this.senderName = senderName;
        this.message = message;
        this.chatType = chatType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(AionConnection con) {
        boolean canRead = true;
        // TODO consider moving this to constructor
        Player actor = con.getActivePlayer();
        if (actor != null) {
            canRead = actor.getConditioner().check(CanReadChatMessageCond.class, Tuple.of(race, chatType, speaker, actor));
        }

        writeC(chatType.toInteger()); // Chat type

		/*
         * 0 : all
         * 1 : elyos
         * 2 : asmodians
		 */
        writeC(canRead ? 0 : race.getRaceId() + 1); // Can read
        writeD(senderObjectId); // Sender object id

        switch (chatType) {
            case NORMAL:
            case GOLDEN_YELLOW:
            case WHITE:
            case YELLOW:
            case BRIGHT_YELLOW:
            case WHITE_CENTER:
            case YELLOW_CENTER:
            case BRIGHT_YELLOW_CENTER:
                writeH(0x00); // Spacer maybe?
                writeS(message);
                break;
            case SHOUT:
                writeS(senderName);
                writeS(message);
                writeF(x);
                writeF(y);
                writeF(z);
                break;
            case ALLIANCE:
            case GROUP:
            case GROUP_LEADER:
            case LEGION:
            case WHISPER:
            case LEAGUE:
            case LEAGUE_ALERT:
                writeS(senderName);
                writeS(message);
                break;
            case CH1:
            case CH2:
            case CH3:
            case CH4:
            case CH5:
            case CH6:
            case CH7:
            case CH8:
            case CH9:
            case CH10:
            case COMMAND:
                writeS(senderName);
                writeS(message);
                break;
        }
    }
}