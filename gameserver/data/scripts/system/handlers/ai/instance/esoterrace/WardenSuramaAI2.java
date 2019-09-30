/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.esoterrace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.stats.container.NpcGameStats;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.knownlist.Visitor;
import com.ne.gs.services.NpcShoutsService;

@AIName("wardensurama")
public class WardenSuramaAI2 extends AggressiveNpcAI2 {

    private final List<Integer> percents = new ArrayList<>();
	private boolean think = true;

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
		sendMsg(1500201);
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private void checkPercentage(int hpPercentage) {

        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 50:
                    case 25:
                    case 5:
                        spawnGeysers();
						think = false;
						EmoteManager.emoteStopAttacking(getOwner());
						startThinkTask();
						sendMsg(1500203);
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }

    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 50, 25, 5);
    }

    private void spawnGeysers() {
        SkillEngine.getInstance().getSkill(getOwner(), 19332, 50, getOwner()).useNoAnimationSkill();
        spawn(282425, 1305.310059f, 1159.337769f, 53.203529f, (byte) 0, 721);
        spawn(282173, 1316.953979f, 1196.861328f, 53.203529f, (byte) 0, 598);
        spawn(282428, 1305.083130f, 1182.424927f, 53.203529f, (byte) 0, 719);
        spawn(282427, 1328.613770f, 1182.369873f, 53.203529f, (byte) 0, 722);
        spawn(282172, 1343.426147f, 1170.675293f, 53.203529f, (byte) 0, 596);
        spawn(282171, 1317.097656f, 1145.419556f, 53.203529f, (byte) 0, 595);
        spawn(282426, 1328.446289f, 1159.062500f, 53.203529f, (byte) 0, 718);
        spawn(282174, 1290.778442f, 1170.730957f, 53.203529f, (byte) 0, 597);

        getKnownList().doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                if (player.isOnline()) {
                    player.sendPck(new SM_SYSTEM_MESSAGE(1400998));
                    player.sendPck(new SM_SYSTEM_MESSAGE(1400997));
                }
            }
        });
        doSchedule();
    }
	
    private void startThinkTask() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    think = true;
                    Creature creature = getAggroList().getMostHated();
                    if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
                        setStateIfNot(AIState.FIGHT);
                        think();
                    } else {
                        getMoveController().abortMove();
                        getOwner().setTarget(creature);
                        getOwner().getGameStats().renewLastAttackTime();
                        getOwner().getGameStats().renewLastAttackedTime();
                        getOwner().getGameStats().renewLastChangeTargetTime();
                        getOwner().getGameStats().renewLastSkillTime();
                        setStateIfNot(AIState.FIGHT);
                        handleMoveValidate();
                    }
                }
            }

        }, 5500);
    }

    private void deSpawnGeysers() {
        despawnNpc(282425);
        despawnNpc(282173);
        despawnNpc(282428);
        despawnNpc(282427);
        despawnNpc(282172);
        despawnNpc(282171);
        despawnNpc(282426);
        despawnNpc(282174);
    }

    private void despawnNpc(int npcId) {
        Npc npc = getPosition().getWorldMapInstance().getNpc(npcId);
        if (npc != null) {
            npc.getController().onDelete();
        }
    }

    private void doSchedule() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                deSpawnGeysers();
				sendMsg(1500202);
            }

        }, 13000);
    }
	
    private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        addPercent();
    }
	
    @Override
    public void handleDied() {
        super.handleDied();
		sendMsg(1500204);
    }
	
	@Override
    public boolean canThink() {
        return think;
    }

}
