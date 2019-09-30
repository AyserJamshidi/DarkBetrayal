package quest.pandaemonium;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

//By Romanz
public class _4912AgrifsFury extends QuestHandler {

    private final static int questId = 4912;

    public _4912AgrifsFury() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798317).addOnQuestStart(questId);
        qe.registerQuestNpc(798317).addOnTalkEvent(questId);
        qe.registerQuestNpc(700514).addOnTalkEvent(questId);
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
            if (targetId == 798317) {
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
                case 700514: {
                    switch (env.getDialog()) {
                        case USE_OBJECT:
                            Npc npc = (Npc) env.getVisibleObject();
                            npc.getController().onDelete();
                            QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 215385, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
                            return true;
                    }
                    break;
                }
                case 798317: {
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 0) {
                                return sendQuestDialog(env, 1011);
                            }
                        case STEP_TO_1:
                            return sendQuestDialog(env, 1011);
                        case CHECK_COLLECTED_ITEMS:
                            if (player.getInventory().getItemCountByItemId(182207084) >= 1) {
                                qs.setStatus(QuestStatus.REWARD);
                                updateQuestStatus(env);
                                removeQuestItem(env, 182207084, 1);
                                return sendQuestDialog(env, 5);
                            } else {
                                return sendQuestDialog(env, 10001);
                            }
                    }
                    break;
                }

            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798317) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
