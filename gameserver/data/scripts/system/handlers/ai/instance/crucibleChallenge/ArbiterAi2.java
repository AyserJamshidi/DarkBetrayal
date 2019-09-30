/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.crucibleChallenge;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.services.teleport.TeleportService;

/**
 * @author xTz
 */
@AIName("arbiter")
public class ArbiterAi2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        int instanceId = getPosition().getInstanceId();
        if (dialogId == 10000) {
            switch (getNpcId()) {
                case 205682:
                    TeleportService.teleportTo(player, 300320000, instanceId, 357.10208f, 1662.702f, 95.9803f, (byte) 60);
                    break;
                case 205683:
                    TeleportService.teleportTo(player, 300320000, instanceId, 1796.5513f, 306.9967f, 469.25f, (byte) 60);
                    break;
                case 205684:
                    TeleportService.teleportTo(player, 300320000, instanceId, 1324.433f, 1738.2279f, 316.476f, (byte) 70);
                    break;
                case 205663:
                    TeleportService.teleportTo(player, 300320000, instanceId, 1270.8877f, 237.93307f, 405.38028f, (byte) 60);
                    break;
                case 205686:
                    TeleportService.teleportTo(player, 300320000, instanceId, 357.98798f, 349.19116f, 96.09108f, (byte) 60);
                    break;
                case 205687:
                    TeleportService.teleportTo(player, 300320000, instanceId, 1759.5004f, 1273.5414f, 389.11743f, (byte) 10);
                    break;
                case 205685:
                    TeleportService.teleportTo(player, 300320000, instanceId, 1283.1246f, 791.6683f, 436.6403f, (byte) 60);
                    break;
            }
        }
        return true;
    }

}
