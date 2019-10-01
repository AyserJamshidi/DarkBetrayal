/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance.dredgion2;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ne.gs.model.team2.TeamType;
import org.apache.commons.lang3.mutable.MutableInt;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.configs.main.GroupConfig;
import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.model.DescId;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.StaticDoor;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.instance.InstanceScoreType;
import com.ne.gs.model.instance.instancereward.DredgionReward;
import com.ne.gs.model.instance.instancereward.InstanceReward;
import com.ne.gs.model.instance.playerreward.DredgionPlayerReward;
import com.ne.gs.model.instance.playerreward.InstancePlayerReward;
import com.ne.gs.model.team2.group.PlayerGroup;
import com.ne.gs.model.team2.group.PlayerGroupService;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_INSTANCE_SCORE;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.questEngine.QuestEngine;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.services.abyss.AbyssPointsService;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.knownlist.Visitor;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author xTz
 */
public class DredgionInstance2 extends GeneralInstanceHandler {

    protected int surkanKills;
    private Map<Integer, StaticDoor> doors;
    protected DredgionReward dredgionReward;
    private float loosingGroupMultiplier = 1;
    private boolean isInstanceDestroyed = false;
    protected AtomicBoolean isInstanceStarted = new AtomicBoolean(false);
    private long instanceTime;
    private Future<?> instanceTask;

    protected DredgionPlayerReward getPlayerReward(Player player) {
        Integer object = player.getObjectId();
        if (dredgionReward.getPlayerReward(object) == null) {
            addPlayerToReward(player);
        }
        return (DredgionPlayerReward) dredgionReward.getPlayerReward(object);
    }

    protected void captureRoom(Race race, int roomId) {
        dredgionReward.getDredgionRoomById(roomId).captureRoom(race);
    }

    private void addPlayerToReward(Player player) {
        dredgionReward.addPlayerReward(new DredgionPlayerReward(player));
    }

    private boolean containPlayer(Integer object) {
        return dredgionReward.containPlayer(object);
    }

    protected void startInstanceTask() {
        instanceTime = System.currentTimeMillis();
        ThreadPoolManager.getInstance().schedule(() -> {
            openFirstDoors();
            dredgionReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
            sendPacket();
        }, 120000);

        instanceTask = ThreadPoolManager.getInstance().schedule(() ->
                stopInstance(dredgionReward.getWinningRaceByScore()), 2520000);
    }

    private List<Player> getPlayersByRace(Race race) {
        return select(instance.getPlayersInside(), having(on(Player.class).getRace(), equalTo(race)));
    }

    @Override
    public void onEnterInstance(Player player) {

        if (!containPlayer(player.getObjectId())) {
            addPlayerToReward(player);
        } else {
            getPlayerReward(player).setPlayer(player);
        }
        sendPacket();
        List<Player> playersByRace = getPlayersByRace(player.getRace());
        playersByRace.remove(player);
        if (playersByRace.size() == 1 && !playersByRace.get(0).isInGroup2()) {
            PlayerGroup newGroup = PlayerGroupService.createGroup(playersByRace.get(0), player, TeamType.AUTO_GROUP);
            int groupId = newGroup.getObjectId();
            if (!instance.isRegistered(groupId)) {
                instance.register(groupId);
            }
        } else if (!playersByRace.isEmpty() && playersByRace.get(0).isInGroup2()) {
            PlayerGroupService.addPlayer(playersByRace.get(0).getPlayerGroup2(), player);
        }
        Integer object = player.getObjectId();
        if (!instance.isRegistered(object)) {
            instance.register(object);
        }
    }

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        dredgionReward = new DredgionReward(mapId, instanceId);
        dredgionReward.setInstanceScoreType(InstanceScoreType.PREPARING);
        doors = instance.getDoors();
    }

    protected void stopInstance(Race race) {
        stopInstanceTask();
        dredgionReward.setWinningRace(race);
        dredgionReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
        doReward();
        sendPacket();
    }

    public void doReward() {
        for (InstancePlayerReward playerReward : dredgionReward.getPlayersInsideByRace(Race.PC_ALL)) {
            float abyssPoint = playerReward.getPoints() * 1.6f; // to do finde on what depend this modifier
            Player player = playerReward.getPlayer();
            if (player.getRace().equals(dredgionReward.getWinningRace())) {
                abyssPoint += dredgionReward.getWinnerPoints();
            } else {
                abyssPoint += dredgionReward.getLooserPoints();
            }
            AbyssPointsService.addAp(player, (int) abyssPoint);
            QuestEnv env = new QuestEnv(null, player, 0, 0);
            QuestEngine.getInstance().onDredgionReward(env);
        }
        for (Npc npc : instance.getNpcs()) {
            npc.getController().onDelete();
        }
    }

    @Override
    public boolean onReviveEvent(Player player) {
        player.sendPck(SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        PlayerReviveService.revive(player, 100, 100, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        dredgionReward.portToPosition(player);
        return true;
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
                true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), false, 0, 8));
        int points = 60;
        if (lastAttacker instanceof Player) {
            if (lastAttacker.getRace() != player.getRace()) {
                InstancePlayerReward playerReward = getPlayerReward(player);

                if (getPointsByRace(lastAttacker.getRace()).compareTo(getPointsByRace(player.getRace())) < 0) {
                    points *= loosingGroupMultiplier;
                } else if (loosingGroupMultiplier == 10 || playerReward.getPoints() == 0) {
                    points = 0;
                }

                updateScore((Player) lastAttacker, player, points, true);
            }
        }
        updateScore(player, null, -points, false);
        return true;
    }

    private MutableInt getPointsByRace(Race race) {
        return dredgionReward.getPointsByRace(race);
    }

    private void addPointsByRace(Race race, int points) {
        dredgionReward.addPointsByRace(race, points);
    }

    private void addPointToPlayer(Player player, int points) {
        getPlayerReward(player).addPoints(points);
    }

    private void addPvPKillToPlayer(Player player) {
        getPlayerReward(player).addPvPKillToPlayer();
    }

    private void addBalaurKillToPlayer(Player player) {
        getPlayerReward(player).addMonsterKillToPlayer();
    }

    protected void updateScore(Player player, Creature target, int points, boolean pvpKill) {
        if (points == 0) {
            return;
        }

        // group score
        addPointsByRace(player.getRace(), points);

        // player score
        List<Player> playersToGainScore = new ArrayList<>();

        if (target != null && player.isInGroup2()) {
            for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
                if (member.getLifeStats().isAlreadyDead()) {
                    continue;
                }
                if (MathUtil.isIn3dRange(member, target, GroupConfig.GROUP_MAX_DISTANCE)) {
                    playersToGainScore.add(member);
                }
            }
        } else {
            playersToGainScore.add(player);
        }

        for (Player playerToGainScore : playersToGainScore) {
            addPointToPlayer(playerToGainScore, points / playersToGainScore.size());
            if (target instanceof Npc) {
                playerToGainScore.sendPck(new SM_SYSTEM_MESSAGE(1400237, DescId.of(
                        ((Npc) target).getObjectTemplate().getNameId() * 2 + 1), points));
            } else if (target instanceof Player) {
                playerToGainScore.sendPck(new SM_SYSTEM_MESSAGE(1400237, target.getName(), points));
            }
        }

        // recalculate point multiplier
        int pointDifference = getPointsByRace(Race.ASMODIANS).intValue() - getPointsByRace(Race.ELYOS).intValue();
        if (pointDifference < 0) {
            pointDifference *= -1;
        }
        if (pointDifference >= 3000) {
            loosingGroupMultiplier = 10;
        } else if (pointDifference >= 1000) {
            loosingGroupMultiplier = 1.5f;
        } else {
            loosingGroupMultiplier = 1;
        }

        // pvpKills for pvp and balaurKills for pve
        if (pvpKill && points > 0) {
            addPvPKillToPlayer(player);
        } else if (target instanceof Npc && target.getRace().equals(Race.DRAKAN)) {
            addBalaurKillToPlayer(player);
        }
        sendPacket();
    }

    @Override
    public void onDie(Npc npc) {
        int hpGauge = npc.getObjectTemplate().getHpGauge();
        Player mostPlayerDamage = npc.getAggroList().getMostPlayerDamage();
        if (hpGauge <= 5) {
            updateScore(mostPlayerDamage, npc, 12, false);
        } else if (hpGauge <= 9) {
            updateScore(mostPlayerDamage, npc, 32, false);
        } else {
            updateScore(mostPlayerDamage, npc, 42, false);
        }
    }

    @Override
    public void onInstanceDestroy() {
        stopInstanceTask();
        isInstanceDestroyed = true;
        dredgionReward.clear();
        doors.clear();
    }

    protected void openFirstDoors() {
    }

    protected void openDoor(int doorId) {
        StaticDoor door = doors.get(doorId);
        if (door != null) {
            door.setOpen(true);
        }
    }

    private void sendPacket() {
        instance.doOnAllPlayers(player -> player.sendPck(new SM_INSTANCE_SCORE(getTime(), dredgionReward)));
    }
	
	
    @Override
    public void handleUseItemFinish(Player player, Npc npc) {
        switch (npc.getNpcId()) {
			case 798330:
            case 798328:
					dredgionReward.addPointsByRace(Race.ASMODIANS, 100);
					CreatureActions.delete(npc);
					player.sendPck(new SM_SYSTEM_MESSAGE(1400277, 100));
					sendPacket();
                return;
			case 798324:
			case 798326:
					dredgionReward.addPointsByRace(Race.ELYOS, 100);
					CreatureActions.delete(npc);
					player.sendPck(new SM_SYSTEM_MESSAGE(1400277, 100));
					sendPacket();
                return;
        }
    }

    private int getTime() {
        long result = System.currentTimeMillis() - instanceTime;
        if (result < 120000) {
            return (int) (120000 - result);
        } else if (result < 2520000) {
            return (int) (2400000 - (result - 120000));
        }
        return 0;
    }

    protected void sp(int npcId, float x, float y, float z, byte h, int time) {
        sp(npcId, x, y, z, h, 0, time);
    }

    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int staticId, int time) {
        ThreadPoolManager.getInstance().schedule(() -> {
            if (!isInstanceDestroyed) {
                spawn(npcId, x, y, z, h, staticId);
            }
        }, time);
    }

    protected void sendMsgByRace(final int msg, final Race race, int time) {
        ThreadPoolManager.getInstance().schedule(() -> instance.doOnAllPlayers(player -> {
            if (player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
                player.sendPck(new SM_SYSTEM_MESSAGE(msg));
            }
        }), time);

    }

    private void stopInstanceTask() {
        if (instanceTask != null) {
            instanceTask.cancel(true);
        }
    }

    @Override
    public InstanceReward<?> getInstanceReward() {
        return dredgionReward;
    }

    @Override
    public void onExitInstance(Player player) {
        TeleportService.moveToInstanceExit(player, mapId, player.getRace());
    }

}
