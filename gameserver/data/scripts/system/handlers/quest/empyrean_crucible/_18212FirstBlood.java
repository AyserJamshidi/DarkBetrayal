package quest.empyrean_crucible;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;


/**
 * @author Romanz
 *
 */
public class _18212FirstBlood extends QuestHandler {

	private final static int questId = 18212;
	
	public _18212FirstBlood() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(205985).addOnQuestStart(questId);
		qe.registerQuestNpc(205985).addOnTalkEvent(questId);
		qe.registerOnKillInWorld(300350000, questId);
		qe.registerOnKillInWorld(300360000, questId);
	}
	
	@Override
	public boolean onKillInWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			giveQuestItem(env, 182212218, 1);
		return defaultOnKillRankedEvent(env, 0, 1, true); 
		}
		return false;
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (env.getTargetId() == 205985) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
			else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
				if (env.getDialog() == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else {
					removeQuestItem(env, 182212218, 1);
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

}

