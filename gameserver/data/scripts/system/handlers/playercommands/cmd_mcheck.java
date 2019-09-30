/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import java.util.Collection;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.QuestEngine;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * Checks all LOCKED missions for start conditions immediately And starts them, if conditions are
 * fulfilled
 *
 * @author vlog
 */
public class cmd_mcheck extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String command, @NotNull String... params) {
        Collection<QuestState> qsl = player.getQuestStateList().getAllQuestState();
        for (QuestState qs : qsl) {
            if (qs.getStatus() == QuestStatus.LOCKED) {
                int questId = qs.getQuestId();
                QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, questId, 0));
            }
        }
        player.sendMsg("Missions checked successfully");
    }
}
