package ai.instance.padmarashkasCave;


import ai.NoActionAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.skillengine.SkillEngine;

@AIName("summonrock")
public class SummonRockAI2 extends NoActionAI2{
	
    @Override
    protected void handleSpawned(){
        super.handleSpawned();
        doSchedule();
    }

    private void doSchedule(){
        ThreadPoolManager.getInstance().schedule(new Runnable(){

            public void run(){
				SkillEngine.getInstance().getSkill(getOwner(), 19180, 55, getOwner()).useNoAnimationSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable(){
					
					public void run(){
						AI2Actions.deleteOwner(SummonRockAI2.this);
					}
				}, 9000);
            }
        }, 5000);
    }
	
}