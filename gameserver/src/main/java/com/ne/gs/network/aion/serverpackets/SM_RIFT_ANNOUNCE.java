/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.serverpackets;

import com.ne.gs.controllers.RiftController;
import com.ne.gs.model.Race;
import com.ne.gs.network.aion.AionConnection;
import com.ne.gs.network.aion.AionServerPacket;

/**
 * @author Sweetkr
 */
public class SM_RIFT_ANNOUNCE extends AionServerPacket {

    private final int actionId;
    private Race race;
    private RiftController rift;
    private int objectId;
    private int gelkmaros;
    private int inggison;
    private int cl;
    private int cr;
    private int tl;
    private int tr;

    public SM_RIFT_ANNOUNCE(Race race) {

        this.race = race;
        actionId = 0;
    }

    public SM_RIFT_ANNOUNCE(boolean gelkmaros, boolean inggison) {
        this.gelkmaros = (gelkmaros ? 1 : 0);
        this.inggison = (inggison ? 1 : 0);
        actionId = 1;
    }

    public SM_RIFT_ANNOUNCE(RiftController rift, boolean isMaster) {

        this.rift = rift;
        if (isMaster) {
            actionId = 2;
        } else {
            actionId = 3;
        }
    }

    /**
     * Rift despawn
     *
     * @param objectId
     */
    public SM_RIFT_ANNOUNCE(int objectId) {

        this.objectId = objectId;
        actionId = 4;
    }

    public SM_RIFT_ANNOUNCE(boolean cl, boolean cr, boolean tl, boolean tr) {
        this.cl = (cl ? 1 : 0);
        this.cr = (cr ? 1 : 0);
        this.tl = (tl ? 1 : 0);
        this.tr = (tr ? 1 : 0);
        actionId = 5;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        switch (actionId) {
            case 0: // announce
                //writeH(0x09); // 3.0 code
                //writeH(0x11); // 3.0
                //writeC(actionId); // 3.0

                writeH(0x19);//4.0 // old -->writeH(0x11); // 0x11
                writeC(actionId);
                //for (int value : rifts.values()) {
                //    writeD(value);
                //}

                // LMFAOOWN see if we need to update this switch case to 4.0 code
                switch (race) {
                    case ASMODIANS:
                        writeD(1);
                        writeD(0);
                        break;
                    case ELYOS:
                        writeD(1);
                        writeD(0);
                }
                break;
            case 1:
                writeH(0x09);
                writeC(actionId);
                writeD(gelkmaros);
                writeD(inggison);
                break;
            /*case 2:
                //writeH(0x21); // 3.0 code
                writeH(0x23); // 0x23
                writeC(actionId);
                writeD(rift.getOwner().getObjectId());
                writeD(rift.getMaxEntries() - rift.getUsedEntries()); // LMFAOOWN make sure this is right, could just be MaxEntries
                writeD(rift.getRemainTime());
                writeD(rift.getMinLevel());
                writeD(rift.getMaxLevel());
                writeF(rift.getOwner().getX());
                writeF(rift.getOwner().getY());
                writeF(rift.getOwner().getZ());
                writeC(rift.isVortex() ? 1 : 0); // red | blue
                writeC(rift.isMaster() ? 1 : 0); // display | hide
                break;
            case 3: // LMFAOOWN add isVortex method.
                //writeH(0x0D); // 3.0 code
                writeH(0x0f); // 0x0f
                writeC(actionId);
                writeD(rift.getOwner().getObjectId());
                writeD(rift.getUsedEntries());
                writeD(rift.getRemainTime());
                writeC(rift.isVortex() ? 0x01 : 0x00);
                writeC(0x00); // unk
                break;*/
            case 4:
                writeH(0x05);
                writeC(actionId);
                writeD(objectId);
                break;
            case 5:
                writeH(0x05);
                writeC(actionId);
                writeC(cl);
                writeC(cr);
                writeC(tl);
                writeC(tr);
                break;
        }
    }
}
