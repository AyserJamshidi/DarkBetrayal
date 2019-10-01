/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.loginserver.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ne.commons.utils.ExitCode;
import com.ne.gs.network.NetworkController;
import com.ne.gs.network.loginserver.LoginServer;
import com.ne.gs.network.loginserver.LoginServerConnection.State;
import com.ne.gs.network.loginserver.LsClientPacket;
import com.ne.gs.network.loginserver.serverpackets.SM_ACCOUNT_LIST;
import com.ne.gs.network.loginserver.serverpackets.SM_GS_AUTH;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * This packet is response for SM_GS_AUTH its notify Gameserver if registration was ok or what was wrong.
 *
 * @author -Nemesiss-
 */
public class CM_GS_AUTH_RESPONSE extends LsClientPacket {

    public CM_GS_AUTH_RESPONSE(int opCode) {
        super(opCode);
    }

    /**
     * Logger for this class.
     */
    protected static final Logger log = LoggerFactory.getLogger(CM_GS_AUTH_RESPONSE.class);

    /**
     * Response: 0=Authed,1=NotAuthed,2=AlreadyRegistered
     */
    private int response;

    private byte serverCount;

    /**
     * {@inheritDoc}
     */
    @Override
    public void readImpl() {
        response = readC();
        if (response == 0)
            serverCount = (byte) readC();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runImpl() {
        /**
         * Authed
         */
        switch (response) {
            case 0: // Successful Connection
                getConnection().setState(State.AUTHED);
                sendPacket(new SM_ACCOUNT_LIST(LoginServer.getInstance().getLoggedInAccounts()));
                NetworkController.getInstance().setServerCount(serverCount);
                break;
            case 1: // Not Authed
                log.error("GameServer is not authenticated at LoginServer side, shutting down!");
                System.exit(ExitCode.CODE_ERROR);
                break;
            case 2: // Already Registered
                log.info("GameServer is already registered at LoginServer side! trying again...");
                // Try again after 10s
                ThreadPoolManager.getInstance().schedule(() -> CM_GS_AUTH_RESPONSE.this.sendPacket(new SM_GS_AUTH()), 10000);
                break;
            default:
                log.info("Default has been triggered in CM_GS_AUTH_RESPONSE");
                break;
        }
    }
}
