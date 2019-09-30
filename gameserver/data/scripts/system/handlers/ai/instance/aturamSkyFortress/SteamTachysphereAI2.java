/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.aturamSkyFortress;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.skillengine.SkillEngine;

/**
 * @author xTz
 */
@AIName("steam_tachysphere")
public class SteamTachysphereAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleUseItemFinish(Player player) {
        QuestState qs = player.getQuestStateList().getQuestState(player.getRace().equals(Race.ELYOS) ? 18302 : 28302);
        if (qs == null) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 27));
        } else if (qs != null && qs.getStatus() != QuestStatus.COMPLETE) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 10));
        } else {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
        }
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 10000) {
            QuestState qs = player.getQuestStateList().getQuestState(player.getRace().equals(Race.ELYOS) ? 18302 : 28302);
            if (qs != null && qs.getStatus() == QuestStatus.COMPLETE) {
                TeleportService.teleportTo(player, 300240000, 175.28925f, 625.1088f, 901.009f, (byte) 33);
                player.sendPck(new SM_PLAY_MOVIE(0, 0, 471, 16777216));
                player.getController().stopProtectionActiveTask();
                SkillEngine.getInstance().getSkill(player, 19502, 1, player).useNoAnimationSkill();
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));
            }
        }
        return true;
    }
}
