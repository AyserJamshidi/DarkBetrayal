/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import org.apache.commons.lang3.ArrayUtils;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author ATracer
 */
public class cmd_questauto extends ChatCommand {

    /**
     * put quests for automation here (new int[]{1245,1345,7895})
     */
    private final int[] questIds = new int[]{};

    @Override
    public void runImpl(@NotNull Player player, @NotNull String command, @NotNull String... params) {
        if (params == null || params.length < 1) {
            player.sendMsg("syntax .questauto <questid>");
            return;
        }

        int questId = 0;
        try {
            questId = Integer.parseInt(params[0]);
        } catch (Exception ex) {
            player.sendMsg("Неправильный ID квеста");
            return;
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            player.sendMsg("Квест не запущен");
            return;
        }

        if (!ArrayUtils.contains(questIds, questId)) {
            player.sendMsg("Этот квест не поддерживается");
            return;
        }

        qs.setStatus(QuestStatus.REWARD);
        player.sendPck(new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
    }
}
