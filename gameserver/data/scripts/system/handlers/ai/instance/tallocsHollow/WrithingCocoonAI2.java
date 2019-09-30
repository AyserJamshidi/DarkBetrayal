/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.tallocsHollow;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;

/**
 * @author xTz
 */
@AIName("writhingcocoon")
public class WrithingCocoonAI2 extends NpcAI2 {

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 1012 && player.getInventory().decreaseByItemId(185000088, 1)) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));
            switch (getNpcId()) {
                case 730232:
                    Npc npc = getPosition().getWorldMapInstance().getNpc(730233);
                    if (npc != null) {
                        npc.getController().onDelete();
                    }
                    spawn(799500, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getH());
                    player.sendPck(new SM_SYSTEM_MESSAGE(390510));
                    break;
                case 730233:
                    Npc npc1 = getPosition().getWorldMapInstance().getNpc(730232);
                    if (npc1 != null) {
                        npc1.getController().onDelete();
                    }
                    spawn(799501, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getH());
                    player.sendPck(new SM_SYSTEM_MESSAGE(390511));
                    break;
            }
            AI2Actions.deleteOwner(this);
        } else if (dialogId == 1012) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1097));
        }
        return true;
    }

    @Override
    protected void handleDialogStart(Player player) {
        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
}
