package quest.sarpan;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

public class _41254SafestScoutMission extends QuestHandler {

    private final static int questId = 41254;

    public _41254SafestScoutMission() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(205756).addOnQuestStart(questId);
        qe.registerQuestNpc(205756).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        int targetId = env.getTargetId();
		
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 205756) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 205756) {
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				} else if (env.getDialogId() == 10255) {
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    return true;
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205756) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
