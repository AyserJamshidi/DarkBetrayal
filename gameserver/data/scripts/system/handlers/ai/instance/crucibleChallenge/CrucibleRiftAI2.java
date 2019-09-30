/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.crucibleChallenge;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.world.knownlist.Visitor;

/**
 * @author xTz
 */
@AIName("cruciblerift")
public class CrucibleRiftAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleUseItemFinish(Player player) {
        switch (getNpcId()) {
            case 730459:
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
                break;
            case 730460:
                TeleportService.teleportTo(player, 300320000, getPosition().getInstanceId(), 1759.5004f, 1273.5414f, 389.11743f, (byte) 10);
                spawn(205679, 1765.522f, 1282.1051f, 389.11743f, (byte) 0);
                AI2Actions.deleteOwner(this);
                break;
        }
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        if (getNpcId() == 730459) {
            announceRift();
        }
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 10000 && getNpcId() == 730459) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));
            TeleportService.teleportTo(player, 300320000, getPosition().getInstanceId(), 1759.5946f, 1768.6449f, 389.11758f, (byte) 16);
            spawn(218190, 1760.8701f, 1774.7711f, 389.11743f, (byte) 110);
            spawn(218185, 1762.6906f, 1773.863f, 389.11743f, (byte) 80);
            spawn(218191, 1763.9441f, 1775.2466f, 389.1175f, (byte) 80);
            AI2Actions.deleteOwner(this);
        }
        return true;
    }

    private void announceRift() {
        getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                if (player.isOnline()) {
                    player.sendPck(new SM_SYSTEM_MESSAGE(false, 1111482, player.getObjectId(), 2));
                }
            }

        });
    }

}
