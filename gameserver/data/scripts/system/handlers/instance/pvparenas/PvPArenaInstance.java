/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance.pvparenas;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.controllers.attack.AggroInfo;
import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.model.DescId;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.StaticDoor;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.instance.InstanceScoreType;
import com.ne.gs.model.instance.instancereward.InstanceReward;
import com.ne.gs.model.instance.instancereward.PvPArenaReward;
import com.ne.gs.model.instance.playerreward.InstancePlayerReward;
import com.ne.gs.model.instance.playerreward.PvPArenaPlayerReward;
import com.ne.gs.model.templates.spawns.SpawnTemplate;
import com.ne.gs.network.aion.AionServerPacket;
import com.ne.gs.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.questEngine.QuestEngine;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.services.abyss.AbyssPointsService;
import com.ne.gs.services.item.ItemService;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.skillengine.model.Skill;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.knownlist.Visitor;
import com.ne.gs.controllers.effect.PlayerEffectController;

import java.util.Arrays;
import java.util.List;

/**
 * @author xTz
 */
public class PvPArenaInstance extends GeneralInstanceHandler {

    protected PvPArenaReward instanceReward;
    protected int killBonus;
    protected int deathFine;
    private boolean isTimerExpired;
    private boolean isInstanceDestroyed;
    private boolean isInstanceStarted = false;
    private List<Integer> deniedSkillList = Arrays.asList(1462, 1463, 816, 817, 818, 861, 2515, 1634, 1627, 1635, 1759, 1773, 1780, 1664, 1789, 1793, 2218, 1666, 2224, 2225, 2230, 2233, 2238, 2404, 2634, 2641, 2651, 2655, 2660, 1359, 1360, 1361, 1389, 2009, 2661, 1631, 1632, 1633, 1716, 2647, 2407, 1465, 1466, 1467, 1527, 2281, 2282, 2283, 2284, 2373, 1034, 1035, 1036, 1037, 2140, 2561, 1170, 1181, 2559, 1447, 1455, 2609, 2857, 1043, 1044, 2144, 2145, 2551, 2552, 2570, 2571, 1027, 1028, 2565, 980, 981, 982, 993, 976, 977, 978, 988, 2547, 1066, 1083, 1067, 1084, 1068, 1085, 1131, 1132, 2373, 2278, 803, 804, 870, 928, 938, 2126, 640, 2068, 2076, 2091, 2483, 2508, 704, 2273, 678, 705, 661, 2274, 699, 679, 706, 662, 2275, 717, 680, 721, 719, 2276, 2074, 736, 630, 2478, 2497, 2504, 671, 672, 2089, 2090, 1572, 1451, 2854, 2394, 1178, 2148, 2147, 1177);

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PvPArenaPlayerReward ownerReward = getPlayerReward(player);
        ownerReward.endBoostMoraleEffect();
        ownerReward.applyBoostMoraleEffect();

        PacketSendUtility.broadcastPacket(player,
                new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);

        player.sendPck(new SM_DIE(false, false, 0, 8));

        if (!lastAttacker.equals(player)) {
            if (lastAttacker instanceof Player) {
                Player winner = (Player) lastAttacker;
                PvPArenaPlayerReward reward = getPlayerReward(winner);
                reward.addPvPKillToPlayer();

                // notify Kill-Quests
                int worldId = winner.getWorldId();
                QuestEngine.getInstance().onKillInWorld(new QuestEnv(player, winner, 0, 0), worldId);
            }
        }

        updatePoints(player);

        return true;
    }

    private void updatePoints(Creature victim) {

        if (!instanceReward.isStartProgress()) {
            return;
        }

        int bonus = 0;
        int rank = 0;

        // Decrease victim points
        if (victim instanceof Player) {
            PvPArenaPlayerReward victimFine = getPlayerReward((Player) victim);
            victimFine.addPoints(deathFine);
            bonus = killBonus;
            rank = instanceReward.getRank(victimFine.getPoints());
        } else {
            bonus = getNpcBonus(((Npc) victim).getNpcId());
        }

        if (bonus == 0) {
            return;
        }

        // Reward all damagers
        for (AggroInfo damager : victim.getAggroList().getList()) {
            if (!(damager.getAttacker() instanceof Creature)) {
                continue;
            }
            Creature master = ((Creature) damager.getAttacker()).getMaster();
            if (master == null) {
                continue;
            }
            if (master instanceof Player) {
                Player attaker = (Player) master;
                int rewardPoints
                        = (victim instanceof Player && instanceReward.getRound() == 3 && rank == 0
                                ? bonus * 3
                                : bonus) * damager.getDamage() / Math.max(1, victim.getAggroList().getTotalDamage());
                getPlayerReward(attaker).addPoints(rewardPoints);
                sendSystemMsg(attaker, victim, rewardPoints);
            }
        }
        if (instanceReward.hasCapPoints()) {
            instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
            reward();
        }
        sendPacket();
    }

    protected void sendSystemMsg(Player player, Creature creature, int rewardPoints) {
        int nameId = creature.getObjectTemplate().getNameId();
        DescId name = DescId.of(nameId * 2 + 1);
        AionServerPacket packet = new SM_SYSTEM_MESSAGE(1400237, nameId == 0 ? creature.getName() : name, rewardPoints);
        player.sendPck(packet);
    }

    @Override
    public void onDie(Npc npc) {
        if (npc.getAggroList().getMostPlayerDamage() == null) {
            return;
        }
        updatePoints(npc);
        int npcId = npc.getNpcId();
        if (npcId == 701187 || npcId == 701188) {
            spawnRndRelics(30000);
        }
    }

    private boolean isNoValidateRound() {
        return instanceReward.getPlayersInside().size() == 1;
    }

    @Override
    public void onEnterInstance(Player player) {
        if (!isInstanceStarted) {
            isInstanceStarted = true;
            instanceReward.setInstanceStartTime();
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
                        sendPacket(new SM_SYSTEM_MESSAGE(1401058));
                        if (isNoValidateRound()) {
                            instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
                            // to do reward
                            reward();
                            sendPacket();
                        }
                    }
                }

            }, 111000);
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    // start round 1
                    if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
                        if (isNoValidateRound()) {
                            instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
                            // to do reward
                            reward();
                            sendPacket();
                            return;
                        }
                        openDoors();
                        sendPacket(new SM_SYSTEM_MESSAGE(1401058));
                        instanceReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
                        sendPacket();
                        isTimerExpired = true;
                    }
                }

            }, 120000);
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
					removeEffects(player);
                    // start round 2
                    if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
                        if (isNoValidateRound()) {
                            instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
                            // to do reward
                            reward();
                            sendPacket();
                            return;
                        }
                        instanceReward.setRound(2);
                        instanceReward.setRndZone();
                        sendPacket();
                        changeZone();
                    }
                }

            }, 300000);
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
					removeEffects(player);
                    // start round 3
                    if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
                        if (isNoValidateRound()) {
                            instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
                            // to do reward
                            reward();
                            sendPacket();
                            return;
                        }
                        instanceReward.setRound(3);
                        instanceReward.setRndZone();
                        sendPacket(new SM_SYSTEM_MESSAGE(1401203));
                        sendPacket();
                        changeZone();
                    }
                }

            }, 480000);
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    // end
                    if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
                        instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
                        // to do reward
                        reward();
                        sendPacket();
                    }
                }

            }, 660000);
        }
        if (!containPlayer(player.getObjectId())) {
            instanceReward.regPlayerReward(player);
            instanceReward.setRndPosition(player.getObjectId());
        } else {
            getPlayerReward(player).setPlayer(player);
        }
        sendPacket();
    }

    private void sendPacket(final AionServerPacket packet) {
        instance.doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                player.sendPck(packet);
            }

        });
    }

    private void spawnRndRelics(int time) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
                    spawn(Rnd.get(1, 2) == 1 ? 701187 : 701188, 1841.951f, 1733.968f, 300.242f, (byte) 0);
                }
            }

        }, time);
    }
	
    private void removeEffects(Player player) {
        PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(20048);
		effectController.removeEffect(20049);
		effectController.removeEffect(20050);
    }

    private int getNpcBonus(int npcId) {
        return instanceReward.getNpcBonus(npcId);
    }

    @Override
    public InstanceReward<?> getInstanceReward() {
        return instanceReward;
    }

    @Override
    public void onPlayerLogOut(Player player) {
        getPlayerReward(player).updateLogOutTime();
    }

    @Override
    public void onPlayerLogin(Player player) {
        // instanceReward.portToPosition(player, false); to do another
        getPlayerReward(player).updateBonusTime();
    }

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        instanceReward = new PvPArenaReward(mapId, instanceId, instance);
        instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
        spawnRings();
        if (!instanceReward.isSoloArena()) {
            spawnRndRelics(0);
        }
    }

    @Override
    public void onExitInstance(Player player) {
        PvPArenaPlayerReward playerReward = getPlayerReward(player);
        playerReward.endBoostMoraleEffect();
        instanceReward.clearPosition(playerReward.getPosition(), Boolean.FALSE);
        playerReward.setPosition(0);
		removeEffects(player);
        TeleportService.moveToInstanceExit(player, mapId, player.getRace());
    }
	
    private void openDoors() {
        for (StaticDoor door : instance.getDoors().values()) {
            if (door != null) {
                door.setOpen(true);
            }
        }
    }

    private boolean containPlayer(Integer object) {
        return instanceReward.containPlayer(object);
    }

    protected PvPArenaPlayerReward getPlayerReward(Player player) {
        instanceReward.regPlayerReward(player);
        return instanceReward.getPlayerReward(player.getObjectId());
    }

    @Override
    public boolean onReviveEvent(Player player) {
        player.sendPck(SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        PlayerReviveService.revive(player, 100, 100, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        if (!isInstanceDestroyed) {
            instanceReward.portToPosition(player, false);
        }
        return true;
    }

    protected void sendPacket() {
        instanceReward.sendPacket();
    }

    @Override
    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
        instanceReward.clear();
    }

    private void changeZone() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                for (Player player : instance.getPlayersInside()) {
                    instanceReward.portToPosition(player, true);
                }
            }

        }, 1000);
    }

    protected void reward() {
        if (instanceReward.canRewarded()) {
            for (InstancePlayerReward playerReward : instanceReward.getPlayersInside()) {
                PvPArenaPlayerReward reward = (PvPArenaPlayerReward) playerReward;
                if (!reward.isRewarded()) {
                    reward.setRewarded();
                    int points = reward.getPoints();
                    int totalPoints = points + reward.getTimeBonus() + instanceReward.getRankBonus(instanceReward.getRank(points));
                    Player player = reward.getPlayer();
                    float rate = instanceReward.isSoloArena()
                            ? player.getRates().getArenaSoloRewardRate()
                            : player.getRates().getArenaFFARewardRate();
                    int abyssPoints = (int) ((0.0125 * totalPoints + 300) * rate);
                    int crucibleInsignia = (int) ((0.006 * totalPoints + 170) * rate);
                    int courageInsignia = (int) ((0.0008 * totalPoints + 2.5) * rate);
                    reward.setAbyssPoints(abyssPoints);
                    reward.setCrucibleInsignia(crucibleInsignia);
                    reward.setCourageInsignia(courageInsignia);
                    AbyssPointsService.addAp(player, abyssPoints);

                    ItemService.addItem(player, 186000137, courageInsignia);
                    if (instanceReward.canRewardOpportunityToken(reward)) {
                        ItemService.addItem(player, 186000165, instanceReward.isSoloArena() ? 4 : 5);
                    }
                    ItemService.addItem(player, 186000130, crucibleInsignia);
                }
            }
        }
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    for (Player player : instance.getPlayersInside()) {
                        if (!CreatureActions.isAlreadyDead(player)) {
                            onExitInstance(player);
                        }
                    }
                }
            }

        }, 10000);
    }

    protected void spawnRings() {
    }

    protected Npc getNpc(float x, float y, float z) {
        if (!isInstanceDestroyed) {
            for (Npc npc : instance.getNpcs()) {
                SpawnTemplate st = npc.getSpawn();
                if (st.getX() == x && st.getY() == y && st.getZ() == z) {
                    return npc;
                }
            }
        }
        return null;
    }

    @Override
    public void handleUseItemFinish(Player player, Npc npc) {
        if (!instanceReward.isStartProgress()) {
            return;
        }
        int rewardetPoints = instanceReward.getNpcBonus(npc.getNpcId());
        int skill = instanceReward.getNpcBonusSkill(npc.getNpcId());
        if (skill != 0) {
            useSkill(npc, player, skill >> 8, skill & 0xFF);
        }

        switch (npc.getNpcId()) {
            case 701226:
                player.getLifeStats().increaseHp(TYPE.HP, (int) (player.getLifeStats().getMaxHp() * .3));
                player.getLifeStats().increaseMp(TYPE.MP, (int) (player.getLifeStats().getMaxMp() * .3));
                break;
        }

        getPlayerReward(player).addPoints(rewardetPoints);
        sendSystemMsg(player, npc, rewardetPoints);
        sendPacket();
    }

    protected void useSkill(Npc npc, Player player, int skillId, int level) {
        SkillEngine.getInstance().getSkill(npc, skillId, level, player).useNoAnimationSkill();
    }

    @Override
    public boolean canUseSkill(Player player, Skill skill) {
        if (isTimerExpired) {
            return true;
        }
        // TODO maybe filter skill type? allow only self buffs...
        return !deniedSkillList.contains(skill.getSkillId());
    }
}
