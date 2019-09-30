/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.DialogPage;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;

/**
 * @author Rolandas
 */
@AIName("housesign")
public class HouseSignAI2 extends GeneralNpcAI2 {

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        DialogPage page = DialogPage.getPageByAction(dialogId);
        if (page == DialogPage.NULL) {
            return false;
        }

        player.sendPck(new SM_DIALOG_WINDOW(getOwner().getObjectId(), page.id()));
        return true;
    }
}
