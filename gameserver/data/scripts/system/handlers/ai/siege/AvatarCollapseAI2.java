/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2014, Neon-Eleanor Team. All rights reserved.
 */
package ai.siege;

import java.util.concurrent.Future;

import ai.NoActionAI2;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.commons.network.util.ThreadPoolManager;

@AIName("avatar_collapse")
public class AvatarCollapseAI2 extends NoActionAI2 {

    private Future<?> skillTask;

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        startpower();
    }

    private void startpower() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), getGravSkill(), 60, getOwner()).useSkill();
			}	
			
		}, 5000, 3000);	
    }
	
    private int getGravSkill() {
        switch (getNpcId()) {
            case 282727:
                return 20155;
            case 282729:
                return 20156;
			case 282731:
                return 20168;
			case 282733:
                return 20164;
            case 282735:
                return 20159;
            case 282737:
                return 20160;
            default:
                return 0;
        }
    }
	
	private void cancelskillTask() {
        if (skillTask != null && !skillTask.isCancelled()) {
            skillTask.cancel(true);
        }
    }

    @Override
    protected void handleDied() {
        cancelskillTask();
        super.handleDied();
	}

    @Override
    protected void handleDespawned() {
        super.handleDespawned();
		cancelskillTask();
		AI2Actions.deleteOwner(AvatarCollapseAI2.this);
    }

}
