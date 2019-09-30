package ai.henchman;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.AI2Actions;

@AIName("efirnoaction")
public class EfirNoActionAI2 extends NpcAI2 {
	
    @Override
    protected void handleDied() {
        super.handleDied();
		AI2Actions.deleteOwner(EfirNoActionAI2.this);
    }
}
