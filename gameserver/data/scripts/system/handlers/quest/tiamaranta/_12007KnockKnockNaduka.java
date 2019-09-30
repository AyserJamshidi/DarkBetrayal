package quest.tiamaranta;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

public class _12007KnockKnockNaduka extends QuestHandler {

    private final static int questId = 12007;

    public _12007KnockKnockNaduka() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(205920).addOnQuestStart(questId);
        qe.registerQuestNpc(205920).addOnTalkEvent(questId);
        qe.registerQuestNpc(205956).addOnTalkEvent(questId);
        qe.registerQuestNpc(218317).addOnKillEvent(questId);
        qe.registerQuestNpc(218318).addOnKillEvent(questId);
        qe.registerQuestNpc(218319).addOnKillEvent(questId);
        qe.registerQuestNpc(218320).addOnKillEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 205920) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    case ACCEPT_QUEST_SIMPLE:
                        return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 205956 && qs.getQuestVarById(0) == 0) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1352);
                    case STEP_TO_1:
                        return defaultCloseDialog(env, 0, 1);
                }
            } else if (targetId == 205956 && qs.getQuestVarById(0) == 10) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 2375);
                    case SELECT_REWARD:
                        changeQuestStep(env, 10, 10, true);
                        return sendQuestDialog(env, 5);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205956) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            switch (env.getTargetId()) {
                case 218317:
                case 218318:
                case 218319:
                case 218320:
                    if (qs.getQuestVarById(0) < 10) {
                        qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                        updateQuestStatus(env);
                        return true;
                    }
            }
        }
        return false;
    }
}
