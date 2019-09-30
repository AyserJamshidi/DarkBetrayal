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

import com.ne.gs.database.GDB;
import javolution.util.FastList;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.database.dao.PlayerDAO;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.PlayerCommonData;
import com.ne.gs.model.team.legion.Legion;
import com.ne.gs.model.team.legion.LegionRank;
import com.ne.gs.network.aion.serverpackets.SM_LEGION_UPDATE_MEMBER;
import com.ne.gs.services.LegionService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author KID
 */
public class LegionCommand extends ChatCommand {

    private final LegionService service = LegionService.getInstance();

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {

        if (params[0].equalsIgnoreCase("disband")) {
            if (!verifyLenght(player, 2, params)) {
                return;
            }

            Legion legion = verifyLegionExists(player, params[1]);
            if (legion == null) {
                return;
            }

            service.disbandLegion(legion);
            player.sendMsg("legion " + legion.getLegionName() + " was disbanded.");
        } else if (params[0].equalsIgnoreCase("setlevel")) {
            if (!verifyLenght(player, 3, params)) {
                return;
            }

            Legion legion = verifyLegionExists(player, params[1]);
            if (legion == null) {
                return;
            }

            byte level = -1;
            try {
                level = Byte.parseByte(params[2]);
            } catch (Exception e) {
            }

            if (level < 1 || level > 5) {
                player.sendMsg("1-5 legion level is allowed.");
                return;
            } else if (level == legion.getLegionLevel()) {
                player.sendMsg("legion " + params[1] + " is already with that level.");
                return;
            }

            int old = legion.getLegionLevel();
            service.changeLevel(legion, level, true);
            player.sendMsg("legion " + legion.getLegionName() + " has raised from " + old + " to " + level + " level.");
        } else if (params[0].equalsIgnoreCase("setpoints")) {
            if (!verifyLenght(player, 3, params)) {
                return;
            }

            Legion legion = verifyLegionExists(player, params[1]);
            if (legion == null) {
                return;
            }

            int points = -1;
            try {
                points = Integer.parseInt(params[2]);
            } catch (Exception e) {
            }

            if (points < 1) {
                player.sendMsg("1-2.1bil points allowed.");
                return;
            }

            long old = legion.getContributionPoints();
            service.setContributionPoints(legion, points, true);
            player.sendMsg("legion " + legion.getLegionName() + " has raised from " + old + " to " + points + " contributiong points.");
        } else if (params[0].equalsIgnoreCase("setname")) {
            if (!verifyLenght(player, 3, params)) {
                return;
            }

            Legion legion = verifyLegionExists(player, params[1]);
            if (legion == null) {
                return;
            }

            if (!service.isValidName(params[2])) {
                player.sendMsg(params[2] + " is incorrect for legion name!");
                return;
            }
            String old = legion.getLegionName();
            service.setLegionName(legion, params[2], true);
            player.sendMsg("legion " + old + " has changed name from " + old + " to " + params[2] + ".");
        } else if (params[0].equalsIgnoreCase("info")) {
            if (!verifyLenght(player, 2, params)) {
                return;
            }

            Legion legion = verifyLegionExists(player, params[1]);
            if (legion == null) {
                return;
            }

            FastList<String> message = FastList.newInstance(), online = FastList.newInstance(), offline = FastList.newInstance();
            message.add("name: " + legion.getLegionName());
            message.add("contrib points: " + legion.getContributionPoints());
            message.add("level: " + legion.getLegionLevel());
            message.add("id: " + legion.getLegionId());
            List<Integer> members = legion.getLegionMembers();
            message.add("members: " + members.size());

            PlayerDAO dao = null;
            for (int memberId : members) {
                Player pl = World.getInstance().findPlayer(memberId);
                if (pl != null) {
                    online.add(pl.getName() + " (lv" + pl.getLevel() + ") classId " + pl.getPlayerClass().getClassId());
                } else {
                    if (dao == null) {
                        dao = GDB.get(PlayerDAO.class);
                    }

                    PlayerCommonData pcd = dao.loadPlayerCommonData(memberId);
                    offline.add(pcd.getName() + " (lv" + pcd.getLevel() + ") classId " + pcd.getPlayerClass().getClassId());
                }
            }

            message.add("--ONLINE-------- " + online.size());
            message.addAll(online);
            FastList.recycle(online);
            message.add("--OFFLINE-------- " + offline.size());
            message.addAll(offline);
            FastList.recycle(offline);

            for (String msg : message) {
                player.sendMsg(msg);
            }

            FastList.recycle(message);
        } else if (params[0].equalsIgnoreCase("kick")) {
            if (!verifyLenght(player, 2, params)) {
                return;
            }

            Player target = World.getInstance().findPlayer(params[1]);
            if (target == null) {
                player.sendMsg("player " + params[1] + " not exists.");
                return;
            } else if (target.getLegionMember().getRank() == LegionRank.BRIGADE_GENERAL) {
                player.sendMsg("player " + target.getName() + " is a brigade general. Disband legion!");
                return;
            }

            if (service.removePlayerFromLegionAsItself(target)) {
                player.sendMsg("player " + target.getName() + " was kicked from legion.");
            } else {
                player.sendMsg("You have failed to kick player " + target.getName() + " from legion.");
            }
        } else if (params[0].equalsIgnoreCase("invite")) {
            if (!verifyLenght(player, 3, params)) {
                return;
            }

            Legion legion = verifyLegionExists(player, params[1]);
            if (legion == null) {
                return;
            }

            Player target = World.getInstance().findPlayer(params[2]);
            if (target == null) {
                player.sendMsg("player " + params[2] + " not exists.");
                return;
            }

            if (target.isLegionMember()) {
                player.sendMsg("player " + target.getName() + " is a already member of " + target.getLegion().getLegionName() + "!");
                return;
            }

            if (service.directAddPlayer(legion, target)) {
                player.sendMsg("player " + target.getName() + " was added to " + legion.getLegionName());
            } else {
                player.sendMsg("probably legion " + legion.getLegionName() + " is full");
            }
        } else if (params[0].equalsIgnoreCase("bg")) {
            if (!verifyLenght(player, 3, params)) {
                return;
            }

            Legion legion = verifyLegionExists(player, params[1]);
            if (legion == null) {
                return;
            }

            Player target = World.getInstance().findPlayer(params[2]);
            if (target == null) {
                player.sendMsg("player " + params[2] + " not exists.");
                return;
            }

            if (!legion.isMember(target.getObjectId())) {
                player.sendMsg("player " + target.getName() + " is not a member of " + legion.getLegionName() + ", invite them!");
                return;
            }

            List<Integer> members = legion.getLegionMembers();
            Player bgplayer = null;
            for (int memberId : members) {
                Player pl = World.getInstance().findPlayer(memberId);
                if (pl != null) {
                    if (pl.getLegionMember().getRank() == LegionRank.BRIGADE_GENERAL) {
                        bgplayer = pl;
                        break;
                    }
                }
            }
            if (bgplayer == null) { // TODO
                player.sendMsg("You can't assign a new general while old is offline.");
                return;
            }

            bgplayer.getLegionMember().setRank(LegionRank.LEGIONARY);
            PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(bgplayer, 0, ""));
            player.sendMsg("You have sucessfully demoted " + bgplayer.getName() + " to Legionary rank.");
            target.getLegionMember().setRank(LegionRank.BRIGADE_GENERAL);
            PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
            player.sendMsg("You have sucessfully promoted " + target.getName() + " to BG rank.");
        } else if (params[0].equalsIgnoreCase("help")) {
            onError(player, null);
        } else if (params[0].equalsIgnoreCase("setrank")) {
            if (!verifyLenght(player, 3, params)) {
                return;
            }

            Player target = World.getInstance().findPlayer(params[1]);
            if (target == null) {
                player.sendMsg("player " + params[1] + " not exists.");
                return;
            }

            if (!target.isLegionMember()) {
                player.sendMsg("player " + target.getName() + " is not a member of legion.");
                return;
            }

            if (params[2].equalsIgnoreCase("centurion")) {
                target.getLegionMember().setRank(LegionRank.CENTURION);
                PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
                player.sendMsg("you have promoted player " + target.getName() + " as centurion.");
            } else if (params[2].equalsIgnoreCase("deputy")) {
                target.getLegionMember().setRank(LegionRank.DEPUTY);
                PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
                player.sendMsg("you have promoted player " + target.getName() + " as deputy.");
            } else if (params[2].equalsIgnoreCase("legionary")) {
                target.getLegionMember().setRank(LegionRank.LEGIONARY);
                PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
                player.sendMsg("you have promoted player " + target.getName() + " as legionary.");
            } else if (params[2].equalsIgnoreCase("volunteer")) {
                target.getLegionMember().setRank(LegionRank.VOLUNTEER);
                PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
                player.sendMsg("you have promoted player " + target.getName() + " as volunteer.");
            } else {
                player.sendMsg("rank " + params[2] + " is not supported.");
            }
        }
    }

    private Legion verifyLegionExists(Player player, String name) {
        if (name.contains("_")) {
            name = name.replaceAll("_", " ");
        }
        Legion legion = service.getLegion(name.toLowerCase());
        if (legion == null) {
            player.sendMsg("legion " + name + " not exists.");
            return null;
        }
        return legion;
    }

    private boolean verifyLenght(Player player, int size, String... cmd) {
        boolean ok = cmd.length >= size;
        if (!ok) {
            onError(player, new Exception(size + " parameters required for element //legion " + cmd[0] + "."));
        }

        return ok;
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("FailReason: " + e.getMessage());

        player.sendMsg("//legion info <legion name> : get list of legion members");
        player.sendMsg("//legion bg <legion name> <new bg name> : set a new brigade general to the legion");
        player.sendMsg("//legion kick <player name> : kick player to this legion");
        player.sendMsg("//legion invite <legion name> <player name> : add player to legion");
        player.sendMsg("//legion disband <legion name> : disbands legion");
        player.sendMsg("//legion setlevel <legion name> <level> : sets legion level");
        player.sendMsg("//legion setpoints <legion name> <points> : set contributing points");
        player.sendMsg("//legion setname <legion name> <new name> : change legion name");
    }
}
