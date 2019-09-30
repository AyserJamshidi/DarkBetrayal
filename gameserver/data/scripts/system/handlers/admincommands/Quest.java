/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.List;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.PersistentState;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.QuestStateList;
import com.ne.gs.model.templates.QuestTemplate;
import com.ne.gs.model.templates.quest.FinishedQuestCond;
import com.ne.gs.model.templates.quest.XMLStartCondition;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author MrPoke
 */
public class Quest extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            admin.sendMsg("syntax //quest <start|set|show|delete>");
            return;
        }
        Player target = null;
        VisibleObject creature = admin.getTarget();
        if (admin.getTarget() instanceof Player) {
            target = (Player) creature;
        }

        if (target == null) {
            admin.sendMsg("Incorrect target!");
            return;
        }

        if (params[0].equals("start")) {
            if (params.length != 2) {
                admin.sendMsg("syntax //quest start <questId>");
                return;
            }
            int id;
            try {
                id = Integer.valueOf(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("syntax //quest start <questId>");
                return;
            }

            QuestEnv env = new QuestEnv(null, target, id, 0);
            
            if (QuestService.startQuest(env, QuestStatus.START, false, admin.getAccessLevel() >= 3)) {
                admin.sendMsg("Quest started.");
            } else {
                QuestTemplate template = DataManager.QUEST_DATA.getQuestById(id);
                List<XMLStartCondition> preconditions = template.getXMLStartConditions();
                if (preconditions != null && preconditions.size() > 0) {
                    for (XMLStartCondition condition : preconditions) {
                        List<FinishedQuestCond> finisheds = condition.getFinishedPreconditions();
                        if (finisheds != null && finisheds.size() > 0) {
                            for (FinishedQuestCond fcondition : finisheds) {
                                QuestState qs1 = admin.getQuestStateList().getQuestState(fcondition.getQuestId());
                                if (qs1 == null || qs1.getStatus() != QuestStatus.COMPLETE) {
                                    admin.sendMsg("You have to finish " + fcondition.getQuestId() + " first!");
                                }
                            }
                        }
                    }
                }
                admin.sendMsg("Quest not started. Some preconditions failed");
            }
        } else if (params[0].equals("set")) {
            int questId, var;
            int varNum = 0;
            QuestStatus questStatus;
            try {
                questId = Integer.valueOf(params[1]);
                String statusValue = params[2];
                if ("START".equals(statusValue)) {
                    questStatus = QuestStatus.START;
                } else if ("NONE".equals(statusValue)) {
                    questStatus = QuestStatus.NONE;
                } else if ("COMPLETE".equals(statusValue)) {
                    questStatus = QuestStatus.COMPLETE;
                } else if ("REWARD".equals(statusValue)) {
                    questStatus = QuestStatus.REWARD;
                } else {
                    admin.sendMsg("<status is one of START, NONE, REWARD, COMPLETE>");
                    return;
                }
                var = Integer.valueOf(params[3]);
                if (params.length == 5 && params[4] != null && !params[4].equals("")) {
                    varNum = Integer.valueOf(params[4]);
                }
            } catch (NumberFormatException e) {
                admin.sendMsg("syntax //quest set <questId status var [varNum]>");
                return;
            }
            QuestState qs = target.getQuestStateList().getQuestState(questId);
            if (qs == null) {
                admin.sendMsg("<QuestState wasn't initialized for this quest>");
                return;
            }
            qs.setStatus(questStatus);
            if (varNum != 0) {
                qs.setQuestVarById(varNum, var);
            } else {
                qs.setQuestVar(var);
            }
            target.sendPck(new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
            if (questStatus == QuestStatus.COMPLETE) {
                qs.setCompleteCount(qs.getCompleteCount() + 1);
                target.getController().updateNearbyQuests();
            }
        }
        if (params[0].equals("delete")) {
            if (params.length != 2) {
                admin.sendMsg("syntax //quest delete <quest id>");
                return;
            }
            int id;
            try {
                id = Integer.valueOf(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("syntax //quest delete <quest id>");
                return;
            }

            QuestStateList list = admin.getQuestStateList();
            if (list == null || list.getQuestState(id) == null) {
                admin.sendMsg("Quest not deleted.");
            } else {
                QuestState qs = list.getQuestState(id);
                qs.setQuestVar(0);
                qs.setCompleteCount(0);
                qs.setStatus(null);
                if (qs.getPersistentState() != PersistentState.NEW) {
                    qs.setPersistentState(PersistentState.DELETED);
                }
                admin.sendMsg("Quest deleted. Please logout.");
            }
        } else if (params[0].equals("show")) {
            if (params.length != 2) {
                admin.sendMsg("syntax //quest show <quest id>");
                return;
            }
            ShowQuestInfo(target, admin, params[1]);
        } else {
            admin.sendMsg("syntax //quest <start|set|show|delete>");
        }
    }

    private void ShowQuestInfo(Player player, Player admin, String param) {
        int id;
        try {
            id = Integer.valueOf(param);
        } catch (NumberFormatException e) {
            admin.sendMsg("syntax //quest show <quest id>");
            return;
        }
        QuestState qs = player.getQuestStateList().getQuestState(id);
        if (qs == null) {
            admin.sendMsg("Quest state: NULL");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                sb.append(Integer.toString(qs.getQuestVarById(i))).append(" ");
            }
            admin.sendMsg("Quest state: " + qs.getStatus().toString() + "; vars: " + sb.toString() + qs.getQuestVarById(5));
            sb.setLength(0);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //quest <start|set|show|delete>");
    }
}
