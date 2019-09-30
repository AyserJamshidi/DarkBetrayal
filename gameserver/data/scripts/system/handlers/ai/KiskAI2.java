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
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Kisk;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.KiskService;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author ATracer, Source
 */
@AIName("kisk")
public class KiskAI2 extends NpcAI2 {

    private final int CANCEL_DIALOG_METERS = 5;

    @Override
    public Kisk getOwner() {
        return (Kisk) super.getOwner();
    }

    @Override
    protected void handleAttack(Creature creature) {
        if (getLifeStats().isFullyRestoredHp()) {
            for (Player member : getOwner().getCurrentMemberList()) {
                member.sendPck(SM_SYSTEM_MESSAGE.STR_BINDSTONE_IS_ATTACKED);
            }
        }
    }

    @Override
    protected void handleDied() {
        if (isAlreadyDead()) {
            PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.DIE, 0, 0));
            getOwner().broadcastPacket(SM_SYSTEM_MESSAGE.STR_BINDSTONE_IS_DESTROYED);
        }

        super.handleDied();
    }

    @Override
    protected void handleDespawned() {
        KiskService.getInstance().removeKisk(getOwner());
        if (!isAlreadyDead()) {
            getOwner().broadcastPacket(SM_SYSTEM_MESSAGE.STR_BINDSTONE_IS_REMOVED);
        }

        super.handleDespawned();
    }

    @Override
    protected void handleDialogStart(Player player) {
        if (player.getKisk() == getOwner()) {
            player.sendPck(SM_SYSTEM_MESSAGE.STR_BINDSTONE_ALREADY_REGISTERED);
            return;
        }

        if (getOwner().canBind(player)) {
            AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_ASK_REGISTER_BINDSTONE, getOwner().getObjectId(), CANCEL_DIALOG_METERS,
                new AI2Request() {

                    private boolean decisionTaken = false;

                    @Override
                    public void acceptRequest(Creature requester, Player responder) {
                        if (!decisionTaken) {
                            // Check again if it's full (If they waited to press OK)
                            if (!getOwner().canBind(responder)) {
                                responder.sendPck(SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_HAVE_NO_AUTHORITY);
                                return;
                            }
                            KiskService.getInstance().onBind(getOwner(), responder);
                        }
                    }

                    @Override
                    public void denyRequest(Creature requester, Player responder) {
                        decisionTaken = true;
                    }
                });

        } else if (getOwner().getCurrentMemberCount() >= getOwner().getMaxMembers()) {
            player.sendPck(SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_FULL);
        } else {
            player.sendPck(SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_HAVE_NO_AUTHORITY);
        }
    }

    @Override
    protected AIAnswer pollInstance(AIQuestion question) {
        switch (question) {
            case SHOULD_DECAY:
                return AIAnswers.NEGATIVE;
            case SHOULD_RESPAWN:
                return AIAnswers.NEGATIVE;
            case SHOULD_REWARD:
                return AIAnswers.NEGATIVE;
            default:
                return null;
        }
    }

}
