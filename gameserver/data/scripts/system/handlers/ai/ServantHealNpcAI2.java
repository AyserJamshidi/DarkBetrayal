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
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.TaskId;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.NpcObjectType;
import com.ne.gs.model.skill.NpcSkillEntry;
import com.ne.gs.utils.ThreadPoolManager;
import java.util.concurrent.Future;

/**
 * @author ATracer
 */
@AIName("servant_heal")
public class ServantHealNpcAI2 extends GeneralNpcAI2 {

	@Override
	public void think() {
		// servants are not thinking
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		if (getCreator() != null) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					if (getOwner().getNpcObjectType() != NpcObjectType.TOTEM)
						AI2Actions.targetCreature(ServantHealNpcAI2.this, (Creature) getCreator().getTarget());
					else 
						AI2Actions.targetSelf(ServantHealNpcAI2.this);
					healOrAttack();
				}
			}, 200);
		}
	}
	
	
	private void healOrAttack() {
		if (skillId == 0) {
			NpcSkillEntry npcSkill = getSkillList().getRandomSkill();
			if (npcSkill == null)
				return;
			skillId = npcSkill.getSkillId();
		}
		int duration = getOwner().getNpcObjectType() == NpcObjectType.TOTEM ? 3000 : 5000;
		Future<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				getOwner().getController().useSkill(skillId, 1);
			}
		}, 1000, duration);
		getOwner().getController().addTask(TaskId.SKILL_USE, task);
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

}
