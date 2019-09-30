package quest.pandaemonium;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Romanz
 */
public class _4210MissingHaorunerk extends QuestHandler {
	private final static int questId = 4210;

	public _4210MissingHaorunerk() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204283).addOnQuestStart(questId);
		qe.registerQuestNpc(798331).addOnTalkEvent(questId);
		qe.registerQuestNpc(798333).addOnTalkEvent(questId);
		qe.registerQuestNpc(215056).addOnKillEvent(questId);
		qe.registerQuestNpc(215080).addOnKillEvent(questId);
	}


	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204283) { 
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 798333) {
				if (dialog == QuestDialog.START_DIALOG) {
					if(qs.getQuestVarById(0) == 0) {
						return sendQuestDialog(env, 1011);
					}
				}
				else if (dialog == QuestDialog.STEP_TO_1) {
					return defaultCloseDialog(env, 0, 1);
				}
			}
			else if (targetId == 798331) {
				if (dialog == QuestDialog.USE_OBJECT) {
					if(qs.getQuestVarById(1) == 1 && qs.getQuestVarById(2) == 1) {
						return sendQuestDialog(env, 10002);
					}
				}
				else if (dialog == QuestDialog.SELECT_REWARD) {
					return defaultCloseDialog(env, 1, 1, true, true);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798331) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
			
	@Override
	public boolean onKillEvent(QuestEnv env) {
		if (defaultOnKillEvent(env, 215056, 0, 1, 1) || defaultOnKillEvent(env, 215080, 0, 1, 2)) {
			return true;
		}
		return false;
	}
}