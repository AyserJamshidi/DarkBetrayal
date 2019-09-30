/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.steelRake;

import java.util.concurrent.atomic.AtomicBoolean;
import ai.GeneralNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("tamer_anikiki")
public class TamerAnikikiAI2 extends GeneralNpcAI2 {

    private final AtomicBoolean isStartedWalkEvent = new AtomicBoolean(false);

    @Override
    public boolean canThink() {
        return false;
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        super.handleCreatureMoved(creature);
        if (getNpcId() == 219040 && isInRange(creature, 10) && creature instanceof Player) {
            if (isStartedWalkEvent.compareAndSet(false, true)) {
                getSpawnTemplate().setWalkerId("3004600001");
                WalkManager.startWalking(this);
                getOwner().setState(1);
                PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
                // Key Box
                spawn(700553, 611, 481, 936, (byte) 90);
                spawn(700553, 657, 482, 936, (byte) 60);
                spawn(700553, 626, 540, 936, (byte) 1);
                spawn(700553, 645, 534, 936, (byte) 75);
                ((Player) creature).sendPck(new SM_QUEST_ACTION(0, 180));
                NpcShoutsService.getInstance().sendMsg(getOwner(), 1400262);
                NpcShoutsService.getInstance().sendMsg(getOwner(), 1400262, getObjectId(), 0, 0);
            }
        }
    }

    @Override
    protected void handleMoveArrived() {
        int point = getOwner().getMoveController().getCurrentPoint();
        super.handleMoveArrived();
        if (getNpcId() == 219040) {
            if (point == 8) {
                getOwner().setState(64);
                PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
            }
            if (point == 12) {
                getSpawnTemplate().setWalkerId(null);
                WalkManager.stopWalking(this);
                AI2Actions.deleteOwner(this);
            }
        }
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        if (getNpcId() != 219040) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    SkillEngine.getInstance().getSkill(getOwner(), 18189, 20, getOwner()).useNoAnimationSkill();
                    getLifeStats().setCurrentHp(getLifeStats().getMaxHp());
                }

            }, 5000);
        }
    }

    @Override
    public int modifyDamage(int damage) {
        return 1;
    }
}
