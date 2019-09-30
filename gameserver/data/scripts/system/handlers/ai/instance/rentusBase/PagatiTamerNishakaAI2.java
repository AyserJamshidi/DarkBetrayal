/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.rentusBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.MathUtil;

@AIName("pagati_tamer_nishaka")
public class PagatiTamerNishakaAI2 extends AggressiveNpcAI2 {

    private Future<?> hideTask;
	private int stage = 0;

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
    }
	
	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 99) {
			stage++;
			if (stage == 1) {
				sendMsg(1500397);
				startHideTask();
			}
        }
    }

    private void startHideTask() {
        hideTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelPhaseTask();
                } else {
                    SkillEngine.getInstance().getSkill(getOwner(), 19660, 60, getOwner()).useNoAnimationSkill();
                    sendMsg(1500398);
					startEvent();
                }
            }
        }, 17000, 29000);
    }
	
	

    private void startEvent() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
				getEffectController().removeEffect(19660);
				SkillEngine.getInstance().getSkill(getOwner(), 19661, 60, getTargetPlayer()).useNoAnimationSkill();
				sendMsg(1500399);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						SkillEngine.getInstance().getSkill(getOwner(), 19662, 60, getOwner()).useNoAnimationSkill();
						sendMsg(1500400);
					}
				}, 4000);
            }

        }, 3500);
    }

    private Player getTargetPlayer() {
        List<Player> players = new ArrayList<>();
        for (Player player : getKnownList().getKnownPlayers().values()) {
            if (!CreatureActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 40) && player != getTarget()) {
                players.add(player);
            }
        }
        return !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
    }
	
	private void cancelPhaseTask() {
        if (hideTask != null && !hideTask.isCancelled()) {
            hideTask.cancel(true);
        }
    }

    private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }

    @Override
    protected void handleDied() {
        getPosition().getWorldMapInstance().getDoors().get(98).setOpen(true);
        cancelPhaseTask();
        sendMsg(1500401);
		stage = 0;
        super.handleDied();
    }

    @Override
    protected void handleDespawned() {
        cancelPhaseTask();
		stage = 0;
        super.handleDespawned();
    }

    @Override
    protected void handleBackHome() {
        getEffectController().removeEffect(19660);
        cancelPhaseTask();
		stage = 0;
        super.handleBackHome();
    }
}
