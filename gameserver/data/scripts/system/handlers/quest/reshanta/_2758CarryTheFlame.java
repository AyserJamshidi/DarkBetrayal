package quest.reshanta;

import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.SystemMessageId;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author Romanz
 *
 */
public class _2758CarryTheFlame extends QuestHandler {

    private final static int questId = 2758;

    public _2758CarryTheFlame() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(279000).addOnQuestStart(questId);
        qe.registerQuestNpc(790016).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        int targetId = 0;
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 279000) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 279000) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                }
                if (env.getDialog() == QuestDialog.STEP_TO_1) {
                    giveQuestItem(env, 182205645, 1);
                    QuestService.questTimerStart(env, 900);
                    return defaultCloseDialog(env, 0, 1);
                }
            } else if (targetId == 790016) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1352);
                } else if (env.getDialogId() == 10255) {
                    QuestService.questTimerEnd(env);
                    removeQuestItem(env, 182205645, 1);
                    qs.setStatus(QuestStatus.REWARD);
                    qs.setQuestVar(1);
                    updateQuestStatus(env);
                    return sendQuestDialog(env, 5);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 790016) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onQuestTimerEndEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var > 1) {
                removeQuestItem(env, 182205645, 1);
                qs.setQuestVar(0);
                updateQuestStatus(env);
                player.sendPck(new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId).getName()));
                return true;
            }
        }
        return false;
    }
}