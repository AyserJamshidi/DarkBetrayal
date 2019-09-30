/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.crucibleChallenge;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author xTz
 */
@AIName("administratorarminos")
public class AdministratorArminosAI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 10000) {
            if (player.isInsideZone(ZoneName.get("ILLUSION_STADIUM_2_300320000"))) {
                spawn(217827, 1250.1598f, 237.97736f, 405.3968f, (byte) 0);
                spawn(217828, 1250.1598f, 239.97736f, 405.3968f, (byte) 0);
                spawn(217829, 1250.1598f, 235.97736f, 405.3968f, (byte) 0);
            } else if (player.isInsideZone(ZoneName.get("ILLUSION_STADIUM_7_300320000"))) {
                spawn(217827, 1265.9661f, 793.5348f, 436.64008f, (byte) 0);
                spawn(217828, 1265.9661f, 789.5348f, 436.6402f, (byte) 0);
                spawn(217829, 1265.9661f, 791.5348f, 436.64014f, (byte) 0);
            }
            AI2Actions.deleteOwner(this);
        }
        return true;
    }
}
