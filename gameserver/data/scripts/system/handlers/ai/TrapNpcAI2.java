/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.skill.NpcSkillEntry;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.world.knownlist.Visitor;

@AIName("trap")
public class TrapNpcAI2 extends NpcAI2 {

    public static int EVENT_SET_TRAP_RANGE = 1;

    private int trapRange = 0;
	private int trapCount = 0;
    private int trapPlay = 0;

    @Override
    protected void handleCreatureMoved(Creature creature) {
        if (trapPlay != 0) {
            tryActivateTrap(creature);
        }
    }

    @Override
    protected void handleSpawned() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            public void run() {
                trapPlay++;

                getKnownList().doUpdate();
                getKnownList().doOnAllObjects(new Visitor<VisibleObject>() {

                    @Override
                    public void visit(VisibleObject object) {
                        if (!(object instanceof Creature)) {
                            return;
                        }
                        Creature creature = (Creature) object;
                        tryActivateTrap(creature);
                    }
                });
            }
		}, 500);
		super.handleSpawned();
    }

    private void tryActivateTrap(Creature creature) {
        if (!creature.getLifeStats().isAlreadyDead() && isInRange(creature, trapRange)) {

            Creature creator = (Creature) getCreator();
            if (!creator.isEnemy(creature)) {
                return;
            }
			if (skillId == 0) {
				NpcSkillEntry npcSkill = getSkillList().getRandomSkill();
				if (npcSkill == null)
					return;
				skillId = npcSkill.getSkillId();
			}

            if (setStateIfNot(AIState.FIGHT)) {
				if (trapCount == 0) {
					trapCount++;
					AI2Actions.targetCreature(this, creature);
					getOwner().getController().useSkill(skillId, 1);
					ThreadPoolManager.getInstance().schedule(new TrapDelete(this), 5000);
				}
            }
        }
    }

    @Override
    protected void handleCustomEvent(int eventId, Object... args) {
        if (eventId == EVENT_SET_TRAP_RANGE) {
            trapRange = (Integer) args[0];
        }
    }

    @Override
    public boolean isMoveSupported() {
        return false;
    }

    @Override
    protected AIAnswer pollInstance(AIQuestion question) {
        switch (question) {
            case SHOULD_DECAY:
                return AIAnswers.NEGATIVE;
            case SHOULD_RESPAWN:
                return AIAnswers.NEGATIVE;
            case SHOULD_REWARD:
                return AIAnswers.NEGATIVE;
            default:
                return null;
        }
    }

    private static final class TrapDelete implements Runnable {

        private TrapNpcAI2 ai;

        TrapDelete(TrapNpcAI2 ai) {
            this.ai = ai;
        }

        @Override
        public void run() {
            AI2Actions.deleteOwner(ai);
            ai = null;
        }

    }

}
