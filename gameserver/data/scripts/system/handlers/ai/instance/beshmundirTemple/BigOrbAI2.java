package ai.instance.beshmundirTemple;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;
import com.ne.gs.questEngine.QuestEngine;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import java.util.List;

/**
 * @author Romanz
 */
@AIName("bigorb")
public class BigOrbAI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        if (!isSpawned(730276)) { // Portal isn't spawned
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
        } else { // Portal is already spawned
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 10));
        }
    }

    @Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
        List<Integer> relatedQuests = QuestEngine.getInstance().getQuestNpc(getOwner().getNpcId()).getOnTalkEvent();

        if (dialogId == QuestDialog.STEP_TO_1.id()) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));
            spawn(730276, 1604.6683f, 1606.5886f, 306.8665f, (byte) 90);

            if (!relatedQuests.isEmpty()) {
                for (int questId2 : relatedQuests) {
                    if (questId2 == 30211 || questId2 == 30213 || questId2 == 30311 || questId2 == 30313) {
                        QuestState qs = player.getQuestStateList().getQuestState(questId2);
                        if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
                            player.getInventory().decreaseByItemId(182209614, 1);
                            player.getInventory().decreaseByItemId(182209617, 1);
                            qs.setStatus(QuestStatus.REWARD);
                            player.sendPck(new SM_QUEST_ACTION(questId2, qs.getStatus(), qs.getQuestVars().getQuestVars()));
                        }
                    }
                }
            }
        } else if (dialogId == QuestDialog.START_DIALOG.id()) {
            if (player.getInventory().getItemCountByItemId(182209614) > 0 || player.getInventory().getItemCountByItemId(182209617) > 0) {
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
            } else {
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 27));
            }
        }
        return true;
    }

    private boolean isSpawned(int npcId) {
        return !getPosition().getWorldMapInstance().getNpcs(npcId).isEmpty();
    }
}
