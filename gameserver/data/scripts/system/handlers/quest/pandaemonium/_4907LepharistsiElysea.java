package quest.pandaemonium;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

//By Romanz
public class _4907LepharistsiElysea extends QuestHandler {

    private final static int questId = 4907;

    public _4907LepharistsiElysea() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204208).addOnQuestStart(questId);
        qe.registerQuestNpc(204208).addOnTalkEvent(questId);
        qe.registerOnEnterWorld(questId);
        qe.registerQuestItem(700511, questId);

    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204208) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                }
                if (env.getDialog() == QuestDialog.ASK_ACCEPTION) {
                    return sendQuestDialog(env, 4);
                }
                if (env.getDialog() == QuestDialog.REFUSE_QUEST) {
                    return sendQuestDialog(env, 1004);
                }
                if (env.getDialog() == QuestDialog.ACCEPT_QUEST) {
                    return sendQuestStartDialog(env);
                }
            }

        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 204208: {
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 2) {
                                return sendQuestDialog(env, 1352);
                            }
                        case STEP_TO_1:
                            return sendQuestDialog(env, 1352);
                        case CHECK_COLLECTED_ITEMS:
                            if (player.getInventory().getItemCountByItemId(182207079) >= 1 && player.getInventory().getItemCountByItemId(182207080) >= 1 && player.getInventory().getItemCountByItemId(182207081) >= 1) {
                                qs.setStatus(QuestStatus.REWARD);
                                updateQuestStatus(env);
                                removeQuestItem(env, 182207079, 1);
                                removeQuestItem(env, 182207080, 1);
                                removeQuestItem(env, 182207081, 1);
                                return sendQuestDialog(env, 5);
                            } else {
                                return sendQuestDialog(env, 10001);
                            }
                    }
                    break;
                }

            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204208) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onEnterWorldEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (player.getWorldId() == 310050000 && qs.getQuestVarById(0) == 0) {
                qs.setQuestVar(1);
                updateQuestStatus(env);
            }
        }
        return false;
    }
}
