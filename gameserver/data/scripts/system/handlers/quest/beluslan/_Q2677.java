package quest.beluslan;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Romanz
 */
public class _Q2677 extends QuestHandler {

    private final static int questId = 2677;
    private int rewardGroup;

    public _Q2677() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204817).addOnQuestStart(questId);
        qe.registerQuestNpc(204817).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 204817) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 204817) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    case CHECK_COLLECTED_ITEMS: {
                        long collect1 = player.getInventory().getItemCountByItemId(186000035);
                        long collect2 = player.getInventory().getItemCountByItemId(186000036);
                        if (collect1 >= 2 && collect2 >= 10) {
                            removeQuestItem(env, 186000035, 2);
                            removeQuestItem(env, 186000036, 10);
                            return sendQuestDialog(env, 1352); // choose your reward
                        } else {
                            return sendQuestDialog(env, 1097);
                        }
                    }
                    case FINISH_DIALOG:
                        return defaultCloseDialog(env, var, var);
                    case STEP_TO_10: {
                        rewardGroup = 0;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
                    }
                    case STEP_TO_20: {
                        rewardGroup = 1;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 6);
                    }
                    case STEP_TO_30: {
                        rewardGroup = 2;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 7);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204817) {
                return sendQuestEndDialog(env, rewardGroup);
            }
        }
        return false;
    }
}
