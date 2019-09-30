/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs;

import java.util.Iterator;

import com.mw.GlobalConst;
import com.ne.commons.configs.CommonsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ne.commons.services.CronService;
import com.ne.commons.utils.ExitCode;
import com.ne.commons.utils.concurrent.RunnableStatsManager;
import com.ne.commons.utils.concurrent.RunnableStatsManager.SortBy;
import com.ne.gs.configs.main.ShutdownConfig;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.network.loginserver.LoginServer;
import com.ne.gs.services.PeriodicSaveService;
import com.ne.gs.services.player.PlayerLeaveWorldService;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.gametime.GameTimeManager;
import com.ne.gs.world.World;

/**
 * @author lord_rex
 */
public class ShutdownHook extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ShutdownHook.class);

    public static ShutdownHook getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void run() {
        if (ShutdownConfig.HOOK_MODE == 1) {
            shutdownHook(ShutdownConfig.HOOK_DELAY, ShutdownConfig.ANNOUNCE_INTERVAL, ShutdownMode.SHUTDOWN);
        } else if (ShutdownConfig.HOOK_MODE == 2) {
            shutdownHook(ShutdownConfig.HOOK_DELAY, ShutdownConfig.ANNOUNCE_INTERVAL, ShutdownMode.RESTART);
        }
    }

    public static enum ShutdownMode {
        NONE("terminating"),
        SHUTDOWN("shutting down"),
        RESTART("restarting");

        private final String text;

        private ShutdownMode(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private void sendShutdownMessage(int seconds) {
        try {
            Iterator<Player> onlinePlayers = World.getInstance().getPlayersIterator();
            if (!onlinePlayers.hasNext()) {
                return;
            }
            while (onlinePlayers.hasNext()) {
                Player player = onlinePlayers.next();
                if (player != null && player.getClientConnection() != null) {
                    player.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.STR_SERVER_SHUTDOWN(String.valueOf(seconds)));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void sendShutdownStatus(boolean status) {
        try {
            Iterator<Player> onlinePlayers = World.getInstance().getPlayersIterator();
            if (!onlinePlayers.hasNext()) {
                return;
            }
            while (onlinePlayers.hasNext()) {
                Player player = onlinePlayers.next();
                if (player != null && player.getClientConnection() != null) {
                    player.getController().setInShutdownProgress(status);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void shutdownHook(int duration, int interval, ShutdownMode mode) {
        for (int i = duration; i >= interval; i -= interval) {
            try {
                if (World.getInstance().getPlayersIterator().hasNext()) {
                    log.info("Runtime is " + mode.getText() + " in " + i + " seconds.");
                    sendShutdownMessage(i);
                    sendShutdownStatus(ShutdownConfig.SAFE_REBOOT);
                } else {
                    log.info("Runtime is " + mode.getText() + " now ...");
                    break; // fast exit.
                }

                if (i > interval) {
                    sleep(interval * 1000);
                } else {
                    sleep(i * 1000);
                }
            } catch (InterruptedException e) {
                return;
            }
        }

        // Disconnect login server from game.
        LoginServer.getInstance().gameServerDisconnected();

        // Disconnect all players.
        Iterator<Player> onlinePlayers;
        onlinePlayers = World.getInstance().getPlayersIterator();
        while (onlinePlayers.hasNext()) {
            Player activePlayer = onlinePlayers.next();
            try {
                PlayerLeaveWorldService.startLeaveWorld(activePlayer);
            } catch (Exception e) {
                log.error("Error while saving player " + e.getMessage());
            }
        }
        log.info("All players are disconnected...");

        if (GlobalConst.EnableMethodStatsLog)
            RunnableStatsManager.dumpClassStats(SortBy.AVG);

        PeriodicSaveService.getInstance().onShutdown();

        // Save game time.
        GameTimeManager.saveTime();
        // Shutdown of cron service
        CronService.getInstance().shutdown();
        // ThreadPoolManager shutdown
        ThreadPoolManager.getInstance().shutdown();

        // Do system exit.
        if (mode == ShutdownMode.RESTART) {
            Runtime.getRuntime().halt(ExitCode.CODE_RESTART);
        } else {
            Runtime.getRuntime().halt(ExitCode.CODE_NORMAL);
        }

        log.info("Runtime is " + mode.getText() + " now...");
    }

    /**
     * @param delay
     * @param announceInterval
     * @param mode
     */
    public void doShutdown(int delay, int announceInterval, ShutdownMode mode) {
        shutdownHook(delay, announceInterval, mode);
    }

    private static final class SingletonHolder {

        private static final ShutdownHook INSTANCE = new ShutdownHook();
    }
}
