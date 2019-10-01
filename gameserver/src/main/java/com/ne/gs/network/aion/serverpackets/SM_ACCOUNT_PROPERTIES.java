/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.serverpackets;

import com.ne.gs.configs.administration.AdminConfig;
import com.ne.gs.model.account.Account;
import com.ne.gs.network.aion.AionConnection;
import com.ne.gs.network.aion.AionServerPacket;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsis, thanks pixfid
 * Date: 14.12.12
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class SM_ACCOUNT_PROPERTIES extends AionServerPacket {

    public SM_ACCOUNT_PROPERTIES() {
    }

    @Override
    protected void writeImpl(AionConnection con) {
        /*writeH(0x03);
        writeH(0x00);
        writeD(0x00);
        writeD(0x00);
        writeD(0x8000);
        writeD(0x00);
        writeC(0x00);
        writeD(0x08);
        writeD(0x04);*/

        //writeC(account.getAccessLevel() >= AdminConfig.GM_PANEL ? 1 : 0);
        writeC(con.getAccount().getAccessLevel() >= 1 ? 1 : 0); // Builder_level

        // LMFAOOWN everything below returns 0 on retail 4.6
        writeH(0x00);//1
        writeC(0x00);//2
        writeD(0x00);//3
        writeH(0);//0x04);//4
        writeH(0);//0x04);//5
        writeC(0x00);//6
        writeH(0);//0x10);//7
        writeD(0x00);//8
        writeH(0x00);//9
        writeD(0x00);//10
        writeH(0);//0x04);//11
        writeH(0x00);//12
        /*
         ----------------------{0}---------------------
         accessLevel and gm panel
         ----------------------{1}---------------------
         unk
         ----------------------{12}--------------------
		0x00 - Special User - (Full Subscription)
		0x01 - Freemium - (TRIAL)
		0x02 - Wise user - (Free)
		0x03 - Experienced user - (Bronze)
		0x04 - Skilled user - (Silver)
         */
    }
}
