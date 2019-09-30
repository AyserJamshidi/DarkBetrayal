/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AI2Request;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.modules.housing.Housing;
import com.ne.gs.network.aion.serverpackets.SM_QUESTION_WINDOW;

/**
 * @author hex1r0
 */
@AIName("housegate")
public class HouseGateAi extends NpcAI2 {

    @Override
    protected void handleDialogStart(final Player requestor) {
        final int houseOwnerUid = getCreatorId();
        // Only group member and creator may use gate
        if (!requestor.getObjectId().equals(houseOwnerUid)) {
            if (requestor.getCurrentGroup() == null || !requestor.getCurrentGroup().hasMember(houseOwnerUid)) {
                return;
            }
        }

        AI2Actions.addRequest(this, requestor, SM_QUESTION_WINDOW.STR_ASK_GROUP_GATE_DO_YOU_ACCEPT_MOVE, 0, 9,
            new AI2Request() {
                @Override
                public void acceptRequest(Creature requester, Player responder) {
                    Housing.housing().tell(new Housing.UseGate(houseOwnerUid, requestor));
                }
            });
    }
}
