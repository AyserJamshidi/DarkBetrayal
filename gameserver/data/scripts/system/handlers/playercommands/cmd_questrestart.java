/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author ginho1
 */
public class cmd_questrestart extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String command, @NotNull String... params) {

        if (params == null || params.length < 1) {
            player.sendMsg("синтаксис .questrestart <quest id>");
            return;
        }

        int id;
        try {
            id = Integer.valueOf(params[0]);
        } catch (NumberFormatException e) {
            player.sendMsg("синтаксис .questrestart <quest id>");
            return;
        }

        QuestState qs = player.getQuestStateList().getQuestState(id);

        if (qs == null || id == 1006 || id == 2008) {
            player.sendMsg("Квест [quest: " + id + "] не может быть перезапущен.");
            return;
        }

        if (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD) {
            if (qs.getQuestVarById(0) != 0) {
                qs.setStatus(QuestStatus.START);
                qs.setQuestVar(0);
                player.sendPck(new SM_QUEST_ACTION(id, qs.getStatus(), qs.getQuestVars().getQuestVars()));
                player.sendMsg("Квест [quest: " + id + "] перезапущен.");
            } else {
                player.sendMsg("Квест [quest: " + id + "] не может быть перезапущен.");
            }
        } else {
            player.sendMsg("Квест [quest: " + id + "] не может быть перезапущен.");
        }
    }
}
