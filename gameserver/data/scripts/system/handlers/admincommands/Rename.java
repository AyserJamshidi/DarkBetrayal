/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.database.GDB;
import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.database.dao.OldNamesDAO;
import com.ne.gs.database.dao.PlayerDAO;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Friend;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.PlayerCommonData;
import com.ne.gs.network.aion.serverpackets.SM_LEGION_UPDATE_MEMBER;
import com.ne.gs.network.aion.serverpackets.SM_MOTION;
import com.ne.gs.network.aion.serverpackets.SM_PLAYER_INFO;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.NameRestrictionService;
import com.ne.gs.services.player.PlayerService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author xTz
 */
public class Rename extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects.player.Player, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) throws Exception {
        if (params.length < 1 || params.length > 2) {
            admin.sendMsg("No parameters detected.\n" + "Please use //rename <Player name> <rename>\n"
                + "or use //rename [target] <rename>");
            return;
        }

        Player player = null;
        String recipient;
        String rename;

        if (params.length == 2) {
            recipient = params[0];
            rename = params[1];

            if (!GDB.get(PlayerDAO.class).isNameUsed(recipient)) {
                admin.sendMsg("Could not find a Player by that name.");
                return;
            }
            PlayerCommonData recipientCommonData = GDB.get(PlayerDAO.class).loadPlayerCommonDataByName(recipient);
            player = recipientCommonData.getPlayer();

            if (!check(admin, rename)) {
                return;
            }

            if (!CustomConfig.OLD_NAMES_COMMAND_DISABLED) {
                GDB.get(OldNamesDAO.class).insertNames(player.getObjectId(), player.getName(), rename);
            }
            recipientCommonData.setName(rename);
            GDB.get(PlayerDAO.class).storePlayerName(recipientCommonData);
            if (recipientCommonData.isOnline()) {
                player.sendPck(new SM_PLAYER_INFO(player, false));
                player.sendPck(new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
                sendPacket(admin, player, rename, recipient);
            } else {
                admin.sendMsg("Player " + recipient + " has been renamed to " + rename);
            }
        }
        if (params.length == 1) {
            rename = params[0];

            VisibleObject target = admin.getTarget();
            if (target == null) {
                admin.sendMsg("You should select a target first!");
                return;
            }

            if (target instanceof Player) {
                player = (Player) target;
                if (!check(admin, rename)) {
                    return;
                }

                if (!CustomConfig.OLD_NAMES_COMMAND_DISABLED) {
                    GDB.get(OldNamesDAO.class).insertNames(player.getObjectId(), player.getName(), rename);
                }
                player.getCommonData().setName(rename);
                player.sendPck(new SM_PLAYER_INFO(player, false));
                GDB.get(PlayerDAO.class).storePlayerName(player.getCommonData());
            } else {
                admin.sendMsg("The command can be applied only on the player.");
            }

            recipient = target.getName();
            sendPacket(admin, player, rename, recipient);
        }
    }

    private static boolean check(Player admin, String rename) {
        if (!NameRestrictionService.isValidName(rename)) {
            admin.sendPck(new SM_SYSTEM_MESSAGE(1400151));
            return false;
        }
        if (!PlayerService.isFreeName(rename)) {
            admin.sendPck(new SM_SYSTEM_MESSAGE(1400155));
            return false;
        }
        if (!CustomConfig.OLD_NAMES_COMMAND_DISABLED && PlayerService.isOldName(rename)) {
            admin.sendPck(new SM_SYSTEM_MESSAGE(1400155));
            return false;
        }
        return true;
    }

    public void sendPacket(Player admin, Player player, String rename, String recipient) {

        for (Friend f : player.getFriendList()) {
            if (f.getPlayer() != null && f.getPlayer().isOnline()) {
                f.getPlayer().sendPck(new SM_PLAYER_INFO(player, false));
            }
        }

        if (player.isLegionMember()) {
            PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
        }
        player.sendMsg("You have been renamed to " + rename);
        admin.sendMsg("Player " + recipient + " has been renamed to " + rename);
    }

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#onError(com.ne.gs.model.gameobjects.player.Player, java.lang.Exception)
     */
    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("No parameters detected.\n" + "Please use //rename <Player name> <rename>\n"
            + "or use //rename [target] <rename>");
    }
}
