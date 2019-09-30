/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.crucibleChallenge;

import java.util.Set;
import ai.ChestAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.drop.DropItem;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.drop.DropRegistrationService;

/**
 * @author xTz
 */
@AIName("worthinessticketbox")
public class WorthinessTicketBoxAI2 extends ChestAI2 {

    @Override
    protected void handleUseItemFinish(Player player) {
        super.handleUseItemFinish(player);
        spawn(205674, 345.52954f, 1662.6697f, 95.25f, (byte) 0);
    }

    @Override
    public void handleDropRegistered() {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(getObjectId());
        dropItems.clear();
        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, getPosition().getWorldMapInstance().getSoloPlayerObj(), getNpcId(), 186000134, 1));
    }
}
