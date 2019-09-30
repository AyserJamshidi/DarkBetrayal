package quest.pandaemonium;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

//By Romanz


public class _29047SecretsandLies extends QuestHandler
{
    private final static int questId = 29047;

    public _29047SecretsandLies() {
        super(questId);
    }

    @Override
    public void register() {

        qe.registerQuestNpc(204052).addOnQuestStart(questId);
        qe.registerQuestNpc(204052).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        int targetId = 0;

        if (env.getVisibleObject() instanceof Npc)
        targetId = ((Npc) env.getVisibleObject()).getNpcId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204052) {
               if (env.getDialog() == QuestDialog.START_DIALOG) {
                  return sendQuestDialog(env, 1011);
               }
               else
                 return sendQuestStartDialog(env);
            }
        }
        if (qs == null)
            return false;

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 204052:
                    switch (env.getDialogId()) {
                       case 26:
                           return sendQuestDialog(env, 2375);
                   case 1009:
                       qs.setStatus(QuestStatus.REWARD);
                       updateQuestStatus(env);
                       return sendQuestEndDialog(env);

            }
        }
        }

        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204052)
                return sendQuestEndDialog(env);
       }
        return false;
    }
}

