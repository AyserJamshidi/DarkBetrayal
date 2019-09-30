/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.tallocsHollow;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.controllers.SummonController;
import com.ne.gs.controllers.effect.EffectController;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Summon;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_TRANSFORM_IN_SUMMON;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("tallocssummon")
public class TallocsSummonAI2 extends NpcAI2 {

    private final AtomicBoolean isTransformed = new AtomicBoolean(false);

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 59 && isTransformed.compareAndSet(false, true)) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));
            if (player.getSummon() != null) { // to do remove
                player.sendMsg("please dismiss your summon first.");
                return true;
            }

            Summon summon = new Summon(getObjectId(), new SummonController(), getSpawnTemplate(), getObjectTemplate(), getObjectTemplate().getLevel(), 0);
            player.setSummon(summon);
            summon.setMaster(player);
            summon.setTarget(player.getTarget());
            summon.setKnownlist(getKnownList());
            summon.setEffectController(new EffectController(summon));
            summon.setPosition(getPosition());
            summon.setLifeStats(getLifeStats());
            player.sendPck(new SM_TRANSFORM_IN_SUMMON(player, getObjectId()));
            player.sendPck(new SM_CUSTOM_SETTINGS(getObjectId(), 0, 38, 0));
            summon.setState(1);
            PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, EmotionType.START_EMOTE2, 0, summon.getObjectId()));
        }
        return true;
    }

    @Override
    protected void handleDialogStart(Player player) {
        if (!isTransformed.get()) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 10));
        }
    }
}
