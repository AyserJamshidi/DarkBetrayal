/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package ai;

import java.util.List;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AI2Actions.SelectDialogResult;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.QuestEngine;
import com.ne.gs.questEngine.model.QuestDialog;

@AIName("quest_start_use")
public class QuestStartItemNpcAi2 extends ActionItemNpcAI2 {
	
	@Override
	protected void handleDialogStart(Player player) {
		super.handleDialogStart(player);
	}
	
	@Override
	protected void handleUseItemFinish(Player player) {
		List<Integer> relatedQuests = QuestEngine.getInstance().getQuestNpc(getOwner().getNpcId()).getOnQuestStart();
		int dialogId = relatedQuests.isEmpty() ? -1 : 26;
		SelectDialogResult dialogResult = AI2Actions.selectDialog(this, player, 0, dialogId);
		if (!dialogResult.isSuccess()) {
			if (isDialogNpc()) {
				player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), QuestDialog.SELECT_ACTION_1011.id()));
			}
			return;
		}
	}

  private boolean isDialogNpc() {
	  return getObjectTemplate().isDialogNpc();
  }
}
