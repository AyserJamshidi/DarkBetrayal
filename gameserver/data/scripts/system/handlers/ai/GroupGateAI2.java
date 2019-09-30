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
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.teleport.TeleportService;

/**
 * @author ATracer, nrg
 */
@AIName("groupgate")
public class GroupGateAI2 extends NpcAI2 {

    private final int CANCEL_DIALOG_METERS = 9;

    @Override
    protected void handleDialogStart(Player player) {

        boolean isMember = false;
        int creatorId = getCreatorId();
        if (player.getObjectId().equals(creatorId)) {
            isMember = true;
        } else if (player.isInGroup2()) {
            isMember = player.getPlayerGroup2().hasMember(creatorId);
        }

        if (isMember && player.getLevel() >= 10) {

            AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_ASK_GROUP_GATE_DO_YOU_ACCEPT_MOVE, getOwner().getObjectId(), CANCEL_DIALOG_METERS,
                new AI2Request() {

                    private boolean decisionTaken = false;

                    @Override
                    public void acceptRequest(Creature requester, Player responder) {
                        if (!decisionTaken) {
                            switch (getNpcId()) {
                                // Group Gates
                                case 749017:
                                    TeleportService.teleportTo(responder, 110010000, 1444.9f, 1577.2f, 572.9f, (byte) 0, TeleportAnimation.JUMP_AIMATION);
                                    break;
                                case 749083:
                                    TeleportService.teleportTo(responder, 120010000, 1657.5f, 1398.7f, 194.7f, (byte) 0, TeleportAnimation.JUMP_AIMATION);
                                    break;
                                // Binding Group Gates
                                case 749131:
                                case 749132:
                                    TeleportService.moveToBindLocation(responder, true);
                                    break;
                            }
                            decisionTaken = true;
                        }
                    }

                    @Override
                    public void denyRequest(Creature requester, Player responder) {
                        decisionTaken = true;
                    }

                });

        } else {
            player.sendPck(SM_SYSTEM_MESSAGE.STR_SKILL_CAN_NOT_USE_GROUPGATE_NO_RIGHT);
        }
    }

    @Override
    protected void handleDialogFinish(Player player) {
    }

}
