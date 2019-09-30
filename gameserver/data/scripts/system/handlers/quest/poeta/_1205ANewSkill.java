/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.poeta;

import com.ne.gs.model.PlayerClass;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author MrPoke
 */
public class _1205ANewSkill extends QuestHandler {

    private final static int questId = 1205;

    public _1205ANewSkill() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(203087).addOnTalkEvent(questId); // Warrior
        qe.registerQuestNpc(203088).addOnTalkEvent(questId); // Scout
        qe.registerQuestNpc(203089).addOnTalkEvent(questId); // Mage
        qe.registerQuestNpc(203090).addOnTalkEvent(questId); // Priest
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        boolean lvlCheck = QuestService.checkLevelRequirement(questId, player.getCommonData().getLevel());
        if (!lvlCheck) {
            return false;
        }
        if (qs != null) {
            return false;
        }
        env.setQuestId(questId);
        if (QuestService.startQuest(env)) {
            qs = player.getQuestStateList().getQuestState(questId);
            qs.setStatus(QuestStatus.REWARD);
            PlayerClass playerClass = player.getPlayerClass();
            if (!playerClass.isStartingClass()) {
                playerClass = PlayerClass.getStartingClassFor(playerClass);
            }
            switch (playerClass) {
                case WARRIOR:
                    qs.setQuestVar(1);
                    break;
                case SCOUT:
                    qs.setQuestVar(2);
                    break;
                case MAGE:
                    qs.setQuestVar(3);
                    break;
                case PRIEST:
                    qs.setQuestVar(4);
                    break;
            }
            updateQuestStatus(env);
        }
        return true;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.REWARD) {
            return false;
        }

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        PlayerClass playerClass = PlayerClass.getStartingClassFor(player.getCommonData().getPlayerClass());
        switch (targetId) {
            case 203087:
                if (playerClass == PlayerClass.WARRIOR) {
                    if (env.getDialog() == QuestDialog.USE_OBJECT) {
                        return sendQuestDialog(env, 1011);
                    } else if (env.getDialogId() == 1009) {
                        return sendQuestDialog(env, 5);
                    } else {
                        return this.sendQuestEndDialog(env);
                    }
                }
                return false;
            case 203088:
                if (playerClass == PlayerClass.SCOUT) {
                    if (env.getDialog() == QuestDialog.USE_OBJECT) {
                        return sendQuestDialog(env, 1352);
                    } else if (env.getDialogId() == 1009) {
                        return sendQuestDialog(env, 6);
                    } else {
                        return this.sendQuestEndDialog(env);
                    }
                }
                return false;
            case 203089:
                if (playerClass == PlayerClass.MAGE) {
                    if (env.getDialog() == QuestDialog.USE_OBJECT) {
                        return sendQuestDialog(env, 1693);
                    } else if (env.getDialogId() == 1009) {
                        return sendQuestDialog(env, 7);
                    } else {
                        return this.sendQuestEndDialog(env);
                    }
                }
                return false;
            case 203090:
                if (playerClass == PlayerClass.PRIEST) {
                    if (env.getDialog() == QuestDialog.USE_OBJECT) {
                        return sendQuestDialog(env, 2034);
                    } else if (env.getDialogId() == 1009) {
                        return sendQuestDialog(env, 8);
                    } else {
                        return this.sendQuestEndDialog(env);
                    }
                }
                return false;
        }

        return false;
    }
}
