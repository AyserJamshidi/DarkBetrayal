/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.Arrays;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.PlayerClass;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_TITLE_INFO;
import com.ne.gs.services.abyss.AbyssPointsService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Nemiroff, ATracer, IceReaper Date: 11.12.2009
 * @author Sarynth - Added AP
 */
public class Set extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        Player target = null;
        VisibleObject creature = admin.getTarget();

        if (admin.getTarget() instanceof Player) {
            target = (Player) creature;
        }

        if (target == null) {
            admin.sendMsg("You should select a target first!");
            return;
        }

        if (params[1] == null) {
            admin.sendMsg("You should enter second params!");
            return;
        }
        String paramValue = params[1];

        if (params[0].equals("class")) {
            byte newClass;
            try {
                newClass = Byte.parseByte(paramValue);
            } catch (NumberFormatException e) {
                admin.sendMsg("You should enter valid second params!");
                return;
            }

            PlayerClass oldClass = target.getPlayerClass();
            setClass(target, oldClass, newClass);
        } else if (params[0].equals("exp")) {
            long exp;
            try {
                exp = Long.parseLong(paramValue);
            } catch (NumberFormatException e) {
                admin.sendMsg("You should enter valid second params!");
                return;
            }

            target.getCommonData().setExp(exp);
            admin.sendMsg("Set exp of target to " + paramValue);
        } else if (params[0].equals("ap")) {
            int ap;
            try {
                ap = Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
                admin.sendMsg("You should enter valid second params!");
                return;
            }

            AbyssPointsService.setAp(target, ap);
            if (target == admin) {
                admin.sendMsg("Set your Abyss Points to " + ap + ".");
            } else {
                admin.sendMsg("Set " + target.getName() + " Abyss Points to " + ap + ".");
                target.sendMsg("Admin set your Abyss Points to " + ap + ".");
            }
        } else if (params[0].equals("level")) {
            int level;
            try {
                level = Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
                admin.sendMsg("You should enter valid second params!");
                return;
            }

            Player player = target;

            if (level <= 61) {
                player.getCommonData().setLevel(level);
            }
            admin.sendMsg("Set " + player.getCommonData().getName() + " level to " + level);
        } else if (params[0].equals("title")) {
            int titleId;
            try {
                titleId = Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
                admin.sendMsg("You should enter valid second params!");
                return;
            }

            Player player = target;
            if (titleId <= 234) {
                setTitle(player, titleId);
            }
            admin.sendMsg("Set " + player.getCommonData().getName() + " title to " + titleId);

        }
    }

    private void setTitle(Player player, int value) {
        player.sendPck(new SM_TITLE_INFO(value));
        PacketSendUtility.broadcastPacket(player, new SM_TITLE_INFO(player, value));
        player.getCommonData().setTitleId(value);
    }

    private void setClass(Player player, PlayerClass oldClass, byte value) {
        PlayerClass playerClass = PlayerClass.getPlayerClassById(value);
        int level = player.getLevel();
        if (level < 9) {
            player.sendMsg("You can only switch class after reach level 9");
            return;
        }
        if (Arrays.asList(1, 2, 4, 5, 7, 8, 10, 11).contains(oldClass.ordinal())) {
            player.sendMsg("You already switched class");
            return;
        }
        int newClassId = playerClass.ordinal();
        switch (oldClass.ordinal()) {
            case 0:
                if (newClassId == 1 || newClassId == 2) {
                    break;
                }
            case 3:
                if (newClassId == 4 || newClassId == 5) {
                    break;
                }
            case 6:
                if (newClassId == 7 || newClassId == 8) {
                    break;
                }
            case 9:
                if (newClassId == 10 || newClassId == 11) {
                    break;
                }
            default:
                player.sendMsg("Invalid class switch chosen");
                return;
        }
        player.getCommonData().setPlayerClass(playerClass);
        player.getController().upgradePlayer();
        player.sendMsg("You have successfuly switched class");
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //set <class|exp|ap|level|title>");
    }
}
