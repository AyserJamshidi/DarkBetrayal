/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.List;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.CoreVersion;
import com.ne.gs.ShutdownHook;
import com.ne.gs.ShutdownHook.ShutdownMode;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author lord_rex
 *         //sys info - System Informations
 *         //sys memory - Memory Informations
 *         //sys gc - Garbage Collector
 *         //sys shutdown <seconds> <announceInterval> - Call shutdown
 *         //sys restart <seconds> <announceInterval> - Call restart
 *         //sys threadpool-Thread pools info
 */

public class Sys extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            player.sendMsg("Usage: //sys info | //sys memory | //sys gc | //sys restart <countdown time> <announce delay> | //sys shutdown <countdown time> <announce delay>");
            return;
        }

        if (params[0].equals("info")) {
            // Time
            player.sendMsg("System Informations at: " + com.ne.commons.Sys.getRealTime());

            // Version Infos
            for (String line : CoreVersion.getBuildInfo()) {
                player.sendMsg(line);
            }

            // OS Infos
            for (String line : com.ne.commons.Sys.getOSInfo()) {
                player.sendMsg(line);
            }

            // CPU Infos
            for (String line : com.ne.commons.Sys.getCPUInfo()) {
                player.sendMsg(line);
            }

            // JRE Infos
            for (String line : com.ne.commons.Sys.getJREInfo()) {
                player.sendMsg(line);
            }

            // JVM Infos
            for (String line : com.ne.commons.Sys.getJVMInfo()) {
                player.sendMsg(line);
            }
        } else if (params[0].equals("memory")) {
            // Memory Infos
            for (String line : com.ne.commons.Sys.getMemoryInfo()) {
                player.sendMsg(line);
            }
        } else if (params[0].equals("gc")) {
            long time = System.currentTimeMillis();
            player.sendMsg("RAM Used (Before): " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576);
            System.gc();
            player.sendMsg("RAM Used (After): " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576);
            System.runFinalization();
            player.sendMsg("RAM Used (Final): " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576);
            player.sendMsg("Garbage Collection and Finalization finished in: " + (System.currentTimeMillis() - time)
                + " milliseconds...");
        } else if (params[0].equals("shutdown")) {
            try {
                int val = Integer.parseInt(params[1]);
                int announceInterval = Integer.parseInt(params[2]);
                ShutdownHook.getInstance().doShutdown(val, announceInterval, ShutdownMode.SHUTDOWN);
                player.sendMsg("Server will shutdown in " + val + " seconds.");
            } catch (ArrayIndexOutOfBoundsException e) {
                player.sendMsg("Numbers only!");
            } catch (NumberFormatException e) {
                player.sendMsg("Numbers only!");
            }
        } else if (params[0].equals("restart")) {
            try {
                int val = Integer.parseInt(params[1]);
                int announceInterval = Integer.parseInt(params[2]);
                ShutdownHook.getInstance().doShutdown(val, announceInterval, ShutdownMode.RESTART);
                player.sendMsg("Server will restart in " + val + " seconds.");
            } catch (ArrayIndexOutOfBoundsException e) {
                player.sendMsg("Numbers only!");
            } catch (NumberFormatException e) {
                player.sendMsg("Numbers only!");
            }
        } else if (params[0].equals("threadpool")) {
            List<String> stats = ThreadPoolManager.getInstance().getStats();
            for (String stat : stats) {
                player.sendMsg(stat.replaceAll("\t", ""));
            }
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Usage: //sys info | //sys memory | //sys gc | //sys restart <countdown time> <announce delay> | //sys shutdown <countdown time> <announce delay>");
    }

}
