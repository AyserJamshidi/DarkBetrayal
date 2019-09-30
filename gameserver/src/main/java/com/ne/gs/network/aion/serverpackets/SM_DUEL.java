/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.serverpackets;

import com.ne.gs.model.DuelResult;
import com.ne.gs.network.aion.AionConnection;
import com.ne.gs.network.aion.AionServerPacket;

/**
 * @author xavier, hex1r0
 */
public class SM_DUEL extends AionServerPacket {

    private final int _type;

    private final int _requesterUid;

    private final String _playerName;
    private final DuelResult _result;

    private SM_DUEL(int type, int requesterUid, String playerName, DuelResult result) {
        _type = type;
        _requesterUid = requesterUid;
        _playerName = playerName;
        _result = result;
    }

    public static SM_DUEL start(int requesterObjId) {
        return new SM_DUEL(0x00, requesterObjId, "", null);
    }

    public static SM_DUEL win(String playerName) {
        return finish(DuelResult.DUEL_WON, playerName);
    }

    public static SM_DUEL lose(String playerName) {
        return finish(DuelResult.DUEL_LOST, playerName);
    }

    public static SM_DUEL draw(String playerName) {
        return finish(DuelResult.DUEL_DRAW, playerName);
    }

    private static SM_DUEL finish(DuelResult result, String playerName) {
        return new SM_DUEL(0x01, 0, playerName, result);
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeC(_type);

        switch (_type) {
            case 0x00:
                writeD(_requesterUid);
                break;
            case 0x01:
                writeC(_result.getResultId()); // unknown
                writeD(_result.getMsgId());
                writeS(_playerName);
                break;
            case 0xE0:
                break;
        }
    }
}
