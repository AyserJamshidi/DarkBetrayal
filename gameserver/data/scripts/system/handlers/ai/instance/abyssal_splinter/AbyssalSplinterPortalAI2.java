package ai.instance.abyssal_splinter;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.teleport.TeleportService;



/**
 * @author Romanz
 *
 */
@AIName("teleportation_device")//281905
public class AbyssalSplinterPortalAI2 extends ActionItemNpcAI2
{

	@Override
	protected void handleUseItemFinish(Player player)
	{
		Npc npc =getOwner();
		if(npc.getX() == 302.201f)
			TeleportService.teleportTo(player, 300220000, 294.632f, 732.189f, 215.854f);
		else if(npc.getX() == 334.001f)
			TeleportService.teleportTo(player, 300220000, 338.475f, 701.417f, 215.916f);
		else if(npc.getX() == 362.192f)
			TeleportService.teleportTo(player, 300220000, 373.611f, 739.125f, 215.903f);
	}

}
