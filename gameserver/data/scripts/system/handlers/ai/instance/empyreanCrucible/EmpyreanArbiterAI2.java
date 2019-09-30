/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.empyreanCrucible;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.instance.instancereward.InstanceReward;
import com.ne.gs.model.instance.playerreward.CruciblePlayerReward;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.world.knownlist.Visitor;

/**
 * @author xTz
 */
@AIName("empyreanarbiter")
public class EmpyreanArbiterAI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        if (player.getInventory().getFirstItemByItemId(186000124) != null) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
        } else {
            // to do
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));
        }
    }

    @Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
        int instanceId = getPosition().getInstanceId();

        if (dialogId == 10000 && player.getInventory().decreaseByItemId(186000124, 1)) {
            switch (getNpcId()) {
                case 799573:
                    TeleportService.teleportTo(player, 300300000, instanceId, 358.2547f, 349.26443f, 96.09108f, (byte) 59);
                    break;
                case 205426:
                    TeleportService.teleportTo(player, 300300000, instanceId, 1260.15f, 812.34f, 358.6056f, (byte) 90);
                    break;
                case 205427:
                    TeleportService.teleportTo(player, 300300000, instanceId, 1616.0248f, 154.43837f, 126f, (byte) 10);
                    break;
                case 205428:
                    TeleportService.teleportTo(player, 300300000, instanceId, 1793.9233f, 796.92f, 469.36542f, (byte) 60);
                    break;
                case 205429:
                    TeleportService.teleportTo(player, 300300000, instanceId, 1776.4169f, 1749.9952f, 303.69553f, (byte) 0);
                    break;
                case 205430:
                    TeleportService.teleportTo(player, 300300000, instanceId, 1328.935f, 1742.0771f, 316.74188f, (byte) 0);
                    break;
                case 205431:
                    TeleportService.teleportTo(player, 300300000, instanceId, 1760.9441f, 1278.033f, 394.23764f, (byte) 0);
                    break;
            }
            InstanceReward<?> instance = getPosition().getWorldMapInstance().getInstanceHandler().getInstanceReward();
            if (instance != null) {
                CruciblePlayerReward reward = (CruciblePlayerReward) instance.getPlayerReward(player.getObjectId());
                if (reward != null) {
                    reward.setPlayerDefeated(false);
                }
            }
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));

            getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

                @Override
                public void visit(Player p) {
                    p.sendPck(new SM_SYSTEM_MESSAGE(1400964, player.getName()));
                }

            });
        }
        return true;
    }
}
