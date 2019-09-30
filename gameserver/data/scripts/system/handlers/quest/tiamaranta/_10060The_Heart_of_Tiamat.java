package quest.tiamaranta;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author Romanz
 */
public class _10060The_Heart_of_Tiamat extends QuestHandler {

    private final static int questId = 10060;

    public _10060The_Heart_of_Tiamat() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerQuestItem(182212555, questId);
        qe.registerQuestNpc(205842).addOnTalkEvent(questId);
        qe.registerQuestNpc(800018).addOnTalkEvent(questId);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
        QuestDialog dialog = env.getDialog();

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 205842) {
                switch (dialog) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
                    }
                    case STEP_TO_1: {
                        return defaultCloseDialog(env, 0, 1);
                    }
                }
            } else if (targetId == 800018) {
                switch (dialog) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        } else if (player.getInventory().getItemCountByItemId(182212591) == 1) {
                            return sendQuestDialog(env, 1693);
                        }
                    }
                    case CHECK_COLLECTED_ITEMS: {
                        if (QuestService.collectItemCheck(env, true)) {
                            if (!giveQuestItem(env, 182212555, 1)) {
                                return true;
                            }
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 10000);
                        }
                    }
                    case STEP_TO_2: {
                        return defaultCloseDialog(env, 1, 2);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 800018) {
                if (dialog == QuestDialog.USE_OBJECT) {
                    player.getInventory().decreaseByItemId(182212591, 1);
                    return sendQuestDialog(env, 10002);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (player.isInsideZone(ZoneName.get("LDF4B_ITEMUSEAREA_Q10060A"))) {
                QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 701119, player.getX(), player.getY(), player.getZ(), (byte) 100, 1);
                QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 218822, player.getX() + 3, player.getY() - 3, player.getZ(), (byte) 100, 1);
                QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 218822, player.getX() - 3, player.getY() + 3, player.getZ(), (byte) 100, 1);
                return HandlerResult.fromBoolean(useQuestItem(env, item, 3, 4, true));
            }
        }
        return HandlerResult.FAILED;
    }
}