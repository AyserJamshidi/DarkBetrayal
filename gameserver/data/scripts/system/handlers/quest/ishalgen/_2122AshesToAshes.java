package quest.ishalgen;

import com.ne.gs.model.DialogAction;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.services.QuestService;
import com.ne.gs.utils.PacketSendUtility;

public class _2122AshesToAshes extends QuestHandler {

	private final static int questId = 2122;
    private final static int[] npc_ids = {203551, 700148, 730029 };

	public _2122AshesToAshes() {
		super(questId);
	}

    @Override
    public void register() {
        qe.registerQuestItem(182203120, questId);
        for (int npc_id : npc_ids) {
            qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
        }
    }

	@Override
	public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
		int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		
		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203551) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 2375);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
        }else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203551) {
				switch (dialog) {
					case START_DIALOG: {
							return sendQuestDialog(env, 1011);
					}
					case SELECT_ACTION_1012: {
						removeQuestItem(env, 182203120, 1);
						return sendQuestDialog(env, 1012);
					}
					case STEP_TO_1: {	
					return defaultCloseDialog(env, 0, 1);
					}
				}
			}else if (qs.getStatus() != QuestStatus.START) {
				return false;
			}
			else if (targetId == 730029) {
				switch (dialog) {
					case USE_OBJECT: {
						if (player.getInventory().getItemCountByItemId(182203133) >= 1)
							return sendQuestDialog(env, 1352);
						else
							return sendQuestDialog(env, 1693);
					}
					case SELECT_ACTION_1353: {
						removeQuestItem(env, 182203133, 1);
						return sendQuestDialog(env, 1353);
					}
					case FINISH_DIALOG: {	
						return closeDialogWindow(env);
					}
					case STEP_TO_2: {	
						return defaultCloseDialog(env, 1, 1, true, false);
					}
				}
			}
			else if (targetId == 700148) {
				return true; // дроп
			}
		}
		return false;
	}
	
    @Override
    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (QuestService.startQuest(env)) {
                return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
            }
        }
        return HandlerResult.FAILED;
    }
}