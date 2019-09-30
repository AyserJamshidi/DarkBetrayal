/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.siege;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.DescId;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.RequestResponseHandler;
import com.ne.gs.network.aion.serverpackets.SM_QUESTION_WINDOW;

/**
 * @author Source
 */
@AIName("siege_gaterepair")
public class GateRepairAI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(final Player player) {
        RequestResponseHandler gaterepair = new RequestResponseHandler(player) {

            @Override
            public void acceptRequest(Creature requester, Player responder) {
                RequestResponseHandler repairstone = new RequestResponseHandler(player) {

                    @Override
                    public void acceptRequest(Creature requester, Player responder) {
                        onActivate(player);
                    }

                    @Override
                    public void denyRequest(Creature requester, Player responder) {
                        // Nothing Happens
                    }

                };
                if (player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_DOOR_REPAIR_DO_YOU_ACCEPT_REPAIR, repairstone)) {
                    player.sendPck(new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_DOOR_REPAIR_DO_YOU_ACCEPT_REPAIR, player.getObjectId(), 5, DescId.of(
                        2 * 716568 + 1)));
                }
            }

            @Override
            public void denyRequest(Creature requester, Player responder) {
                // Nothing Happens
            }

        };

        if (player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_DOOR_REPAIR_POPUPDIALOG, gaterepair)) {
            player.sendPck(new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_DOOR_REPAIR_POPUPDIALOG, player.getObjectId(), 5));
        }
    }

    @Override
    protected void handleDialogFinish(Player player) {
    }

    public void onActivate(Player player) {
        // Stert repair process
    }

}
