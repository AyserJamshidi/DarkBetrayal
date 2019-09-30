/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.siege;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AI2Request;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.PositionUtil;

/**
 * @author Source
 */
@AIName("fortressgate")
public class SiegeFortressGateAI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        AI2Actions.addRequest(this, player, 160017, 0, new AI2Request() {

            @Override
            public void acceptRequest(Creature requester, Player responder) {
                if (MathUtil.isInRange(requester, responder, 10)) {
                    TeleportService.moveToTargetWithDistance(requester, responder, PositionUtil.isBehind(requester, responder) ? 0 : 1, 3);
                } else {
                    PacketSendUtility.sendBrightYellowMessageOnCenter(responder, "You too far away");
                }
            }
        });
    }

    @Override
    protected void handleDialogFinish(Player player) {
    }

    @Override
    protected AIAnswer pollInstance(AIQuestion question) {
        switch (question) {
            case SHOULD_DECAY:
                return AIAnswers.NEGATIVE;
            case SHOULD_RESPAWN:
                return AIAnswers.NEGATIVE;
            default:
                return null;
        }
    }
}
