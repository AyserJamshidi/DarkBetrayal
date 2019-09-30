package ai.instance.lowerUdas;

import com.ne.gs.ai2.NpcAI2;

import java.util.concurrent.Future;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.skillengine.SkillEngine;


@AIName("heal_debilkarim")
public class DebilkarimHealAI2 extends NpcAI2 {

	private Future<?> heal;
	
    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        heal();
    }
	
    @Override
    public void handleDied() {
        super.handleDied();
		cancelheal();
    }
	
    private void heal() {
        heal = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelheal();
                } else {
					Npc boss = getPosition().getWorldMapInstance().getNpc(215795);
					SkillEngine.getInstance().getSkill(getOwner(), 18650, 53, boss).useSkill();
                }
            }

        }, 5000, 10000);
    }
	
    private void cancelheal() {
        if (heal != null && !heal.isCancelled()) {
            heal.cancel(true);
        }
    }
}
