/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.aturamSkyFortress;

import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("ashunatal_shadowslip")
public class AshunatalShadowslipAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean isHome = new AtomicBoolean(true);
    private boolean isSummoned;
    private boolean canThink = true;

    @Override
    public boolean canThink() {
        return canThink;
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
            getPosition().getWorldMapInstance().getDoors().get(17).setOpen(true);
            getPosition().getWorldMapInstance().getDoors().get(2).setOpen(true);
        }
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 60 && !isSummoned) {
            isSummoned = true;
            SkillEngine.getInstance().getSkill(getOwner(), 19428, 1, getOwner()).useNoAnimationSkill();
            doSchedule();
        }
    }

    @Override
    protected void handleBackHome() {
        isHome.set(true);
        isSummoned = false;
        super.handleBackHome();
        getPosition().getWorldMapInstance().getDoors().get(17).setOpen(true);
        getPosition().getWorldMapInstance().getDoors().get(2).setOpen(false);
        Npc npc = getPosition().getWorldMapInstance().getNpc(219186);
        if (npc != null) {
            npc.getController().onDelete();
        }
    }

    private void doSchedule() {
        if (!isAlreadyDead()) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (!isAlreadyDead()) {
                        SkillEngine.getInstance().getSkill(getOwner(), 19417, 49, getOwner()).useNoAnimationSkill();
                        ThreadPoolManager.getInstance().schedule(new Runnable() {

                            @Override
                            public void run() {
                                if (!isAlreadyDead()) {
                                    WorldPosition p = getPosition();
                                    spawn(219186, p.getX(), p.getY(), p.getZ(), p.getH());
                                    canThink = false;
                                    getSpawnTemplate().setWalkerId("3002400001");
                                    setStateIfNot(AIState.WALKING);
                                    think();
                                    getOwner().setState(1);
                                    PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
                                    ThreadPoolManager.getInstance().schedule(new Runnable() {

                                        @Override
                                        public void run() {
                                            if (!isAlreadyDead()) {
                                                despawn();
                                            }
                                        }

                                    }, 4000);
                                }
                            }

                        }, 3000);
                    }
                }

            }, 2000);

        }
    }

    private void despawn() {
        AI2Actions.deleteOwner(this);
    }
}
