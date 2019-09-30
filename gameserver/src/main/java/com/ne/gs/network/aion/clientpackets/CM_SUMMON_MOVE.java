/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.clientpackets;

import com.ne.gs.controllers.movement.MovementMask;
import com.ne.gs.controllers.movement.SummonMoveController;
import com.ne.gs.model.gameobjects.Summon;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.AionClientPacket;
import com.ne.gs.network.aion.serverpackets.SM_MOVE;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.World;

/**
 * @author ATracer
 */
public class CM_SUMMON_MOVE extends AionClientPacket {

    private byte type;
    private byte heading;
    private float x = 0f, y = 0f, z = 0f, x2 = 0f, y2 = 0f, z2 = 0f, vehicleX = 0f, vehicleY = 0f, vehicleZ = 0f;
    private final float vectorX = 0f;
    private final float vectorY = 0f;
    private final float vectorZ = 0f;
    private byte glideFlag;
    private int unk1, unk2;

    @Override
    protected void readImpl() {
        Player player = getConnection().getActivePlayer();

        if (player == null || !player.isSpawned()) {
            return;
        }

        readD();// object id

        x = readF();
        y = readF();
        z = readF();

        heading = (byte) readC();
        type = (byte) readC();

        if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
            if ((type & MovementMask.MOUSE) == 0) {
                /*
				 * [xTz] in packet is missed this for type 0xC0 vectorX = readF(); vectorY = readF(); vectorZ = readF();
				 */
                x2 = vectorX + x;
                y2 = vectorY + y;
                z2 = vectorZ + z;
            } else {
                x2 = readF();
                y2 = readF();
                z2 = readF();
            }
        }
        if ((type & MovementMask.GLIDE) == MovementMask.GLIDE) {
            glideFlag = (byte) readC();
        }
        if ((type & MovementMask.VEHICLE) == MovementMask.VEHICLE) {
            unk1 = readD();
            unk2 = readD();
            vehicleX = readF();
            vehicleY = readF();
            vehicleZ = readF();
        }
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        Summon summon = player.getSummon();
        if (summon == null) {
            return;
        }
        if (summon.getEffectController().isUnderFear()) {
            return;
        }
        SummonMoveController m = summon.getMoveController();
        m.movementMask = type;

        if ((type & MovementMask.GLIDE) == MovementMask.GLIDE) {
            m.glideFlag = glideFlag;
        }

        if (type == 0) {
            summon.getController().onStopMove();
        } else if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
            if ((type & MovementMask.MOUSE) == 0) {
                m.vectorX = vectorX;
                m.vectorY = vectorY;
                m.vectorZ = vectorZ;
            }
            summon.getMoveController().setNewDirection(x2, y2, z2, heading);
            summon.getController().onStartMove();
        } else {
            summon.getController().onMove();
        }

        if ((type & MovementMask.VEHICLE) == MovementMask.VEHICLE) {
            m.unk1 = unk1;
            m.unk2 = unk2;
            m.vehicleX = vehicleX;
            m.vehicleY = vehicleY;
            m.vehicleZ = vehicleZ;
        }
        World.getInstance().updatePosition(summon, x, y, z, heading);

        if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE || type == 0) {
            PacketSendUtility.broadcastPacket(summon, new SM_MOVE(summon));
        }
    }
}