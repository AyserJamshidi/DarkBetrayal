/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.abyssal_splinter;

import ai.SummonerAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.ai.Percentage;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author Luzien
 */
@AIName("kaluva")
public class KaluvaAI2 extends SummonerAI2 {

    private boolean canThink = true;

    @Override
    protected void handleIndividualSpawnedSummons(Percentage percent) {
        spawn();
        canThink = false;
        EmoteManager.emoteStopAttacking(getOwner());
        setStateIfNot(AIState.FOLLOWING);
        getOwner().setState(1);
        PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
        AI2Actions.targetCreature(this, getPosition().getWorldMapInstance().getNpc(281902));
        getMoveController().moveToTargetObject();
    }

    @Override
    protected void handleMoveArrived() {
        if (canThink == false) {
            Npc egg = getPosition().getWorldMapInstance().getNpc(281902);
            if (egg != null) {
                SkillEngine.getInstance().getSkill(getOwner(), 19223, 55, egg).useNoAnimationSkill();
            }

            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    canThink = true;
                    Creature creature = getAggroList().getMostHated();
                    if (creature == null || !getOwner().canSee(creature) || CreatureActions.isAlreadyDead(creature)) {
                        setStateIfNot(AIState.FIGHT);
                        think();
                    } else {
                        getOwner().setTarget(creature);
                        getOwner().getGameStats().renewLastAttackTime();
                        getOwner().getGameStats().renewLastAttackedTime();
                        getOwner().getGameStats().renewLastChangeTargetTime();
                        getOwner().getGameStats().renewLastSkillTime();
                        setStateIfNot(AIState.FIGHT);
                        think();
                    }
                }
            }, 2000);
        }
        super.handleMoveArrived();
    }

    private void spawn() {
        switch (Rnd.get(1, 4)) {
            case 1:
                spawn(281902, 663.322021f, 556.731995f, 424.295013f, (byte) 64);
                break;
            case 2:
                spawn(281902, 644.0224f, 523.9641f, 423.09103f, (byte) 32);
                break;
            case 3:
                spawn(281902, 611.008f, 539.73395f, 423.25034f, (byte) 119);
                break;
            case 4:
                spawn(281902, 628.4426f, 585.4443f, 424.31854f, (byte) 93);
                break;
        }
    }

    @Override
    public boolean canThink() {
        return canThink;
    }

}
