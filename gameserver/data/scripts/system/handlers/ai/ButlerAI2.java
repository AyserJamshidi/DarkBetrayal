/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.DialogPage;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;

/**
 * @author Rolandas
 */
@AIName("butler")
public class ButlerAI2 extends GeneralNpcAI2 {

    private static final Logger log = LoggerFactory.getLogger(ButlerAI2.class);

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        return kickDialog(player, DialogPage.getPageByAction(dialogId));
    }

    private boolean kickDialog(Player player, DialogPage page) {
        if (page == DialogPage.NULL) {
            return false;
        }

        player.sendPck(new SM_DIALOG_WINDOW(getOwner().getObjectId(), page.id()));
        return true;
    }

    @Override
    protected void handleCreatureSee(Creature creature) {
        //		if (creature instanceof Player) {
        //			final Player player = (Player) creature;
        //			final House house = (House) getCreator();
        //			if (player.getObjectId() == house.getOwnerId()) {
        //				// DO SOMETHING SPECIAL
        //			}
        //
        //			final Map<Integer, PlayerScript> scriptMap = house.getPlayerScripts().getScripts();
        //			try {
        //				// protect against writing
        //				for (int position = 0; position < 8; position++) {
        //					scriptMap.get(position).writeLock();
        //				}
        //				int totalSize = 0;
        //				int position = 0;
        //				int from = 0;
        //				while (position != 7) {
        //					for (; position < 8; position++) {
        //						final PlayerScript script = scriptMap.get(position);
        //						final byte[] bytes = script.getCompressedBytes();
        //						if (bytes == null) {
        //							continue;
        //						}
        //						if (bytes.length > 8141) {
        //							log.warn("Player " + player.getObjectId() + " has too big script at position " + position);
        //							return;
        //						}
        //						if (totalSize + bytes.length > 8141) {
        //							position--;
        //							PacketSendUtility.sendPacket(player, new SM_HOUSE_SCRIPTS(house.getAddress().getId(), house.getPlayerScripts(), from, position));
        //							from = position + 1;
        //							totalSize = 0;
        //							continue;
        //						}
        //						totalSize += bytes.length + 8;
        //					}
        //					position--;
        //					if (totalSize > 0 || from == 0 && position == 7) {
        //						PacketSendUtility.sendPacket(player, new SM_HOUSE_SCRIPTS(house.getAddress().getId(), house.getPlayerScripts(), from, position));
        //					}
        //				}
        //			} finally {
        //				// remove write locks finally
        //				for (int position = 0; position < 8; position++) {
        //					scriptMap.get(position).writeUnlock();
        //				}
        //			}
        //}
    }
}
