/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.crucibleChallenge;

import ai.GeneralNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("poppyontherun")
public class PoppyOnTheRunAI2 extends GeneralNpcAI2 {

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));
        return true;
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        getOwner().setState(1);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
            }

        }, 1000);

    }

}
