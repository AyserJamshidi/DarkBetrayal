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
public class SM_QUESTION_WINDOW_CLOSE extends AionServerPacket {

    @Override
    protected void writeImpl(AionConnection con) {
        //writeH(0x00); // Successfully closes window but returns "???" in chat too...

        //writeC(0x00); // Bad, sends "???" in chat only..

        //writeQ(0x00); // Successfully closes window as it should but prevents further dialogs
        // Must decline THEN send this packet.


        //writeD(0x00);
        //writeDF(0x00);
        //writeF(0x00);
    }
}
