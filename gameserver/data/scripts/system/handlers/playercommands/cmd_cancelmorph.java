package playercommands;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_TRANSFORM;
import com.ne.gs.skillengine.model.TransformType;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;

public class cmd_cancelmorph extends ChatCommand
{
    @Override
    protected void runImpl(Player player, String alias, String... params) throws Exception {

        int modelId = player.getTransformModel().getModelId();

        if(player.getTransformModel().getType() == TransformType.PC && modelId != 0){
            player.getTransformModel().setModelId(0);
            PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, player.getTransformModel().getPanelId(), true));
        }
    }
}
