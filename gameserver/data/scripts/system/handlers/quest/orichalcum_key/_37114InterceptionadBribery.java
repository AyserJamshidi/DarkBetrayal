package quest.orichalcum_key;

import com.ne.gs.configs.main.GroupConfig;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.team2.group.PlayerGroup;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.utils.PacketSendUtility;

//By Romanz

public class _37114InterceptionadBribery extends QuestHandler {

	private final static int questId = 37114;

	public _37114InterceptionadBribery() {
		super(questId);
	}

        @Override
	public void register() {
		qe.registerQuestNpc(799906).addOnQuestStart(questId);
		qe.registerQuestNpc(799906).addOnTalkEvent(questId);
		qe.registerQuestNpc(700973).addOnTalkEvent(questId);
		qe.registerQuestNpc(799932).addOnTalkEvent(questId);
	}


    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (targetId == 799906) {
                    if (dialog == QuestDialog.START_DIALOG) {
                        return sendQuestDialog(env, 1011);
                    }
                    else {
                        return sendQuestStartDialog(env);
                    }
                }
            }

        }
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 700973) {
				if (dialog == QuestDialog.USE_OBJECT) {
					if (player.isInGroup2()){
						PlayerGroup group = player.getPlayerGroup2();
						for (Player member : group.getMembers()) {
							if (member.isMentor() && MathUtil.getDistance(player, member) < 40)  {
								SkillEngine.getInstance().applyEffectDirectly(300, player, player, 600000);
								return true;
							}
							else
								PacketSendUtility.sendPck(player, SM_SYSTEM_MESSAGE.STR_MSG_DailyQuest_Ask_Mentee);
						}
					}
				}
			}

            if (targetId == 799932) {
                if (dialog == QuestDialog.USE_OBJECT) {
                    if (player.isInGroup2()){
                        PlayerGroup group = player.getPlayerGroup2();
                        for (Player member : group.getMembers()) {
                            if (member.isMentor() && MathUtil.getDistance(player, member) < 40)  {
                     if(qs.getQuestVarById(0) == 0) {
                        if (player.getInventory().getItemCountByItemId(182210041) == 0 && player.getEffectController().hasAbnormalEffect(300))   {
                            QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 700974, player.getX()+2, player.getY()+2, player.getZ(), player.getHeading());
                            player.getEffectController().removeEffect(300);
                            return false;
                        }
                        else
                        {
//                            PacketSendUtility.sendMessage(player, "Transformation of Blaur required");
                            return closeDialogWindow(env);
                        }

                     }
                    }
                            else       {
                                PacketSendUtility.sendPck(player, SM_SYSTEM_MESSAGE.STR_MSG_DailyQuest_Ask_Mentee);
                                return closeDialogWindow(env);
                            }
                  }
                }
            }
            }
            if (targetId == 799906) {
                if (dialog == QuestDialog.START_DIALOG) {
                    if(qs.getQuestVarById(0) == 0) {
                        return sendQuestDialog(env, 2375);
                    }
                }
                else if (dialog == QuestDialog.CHECK_COLLECTED_ITEMS) {
                    return checkQuestItems(env, 0, 1, true, 5, 2716);
                }
            }
        }
        else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799906) {
                if (dialog == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 5);
                }
                else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}