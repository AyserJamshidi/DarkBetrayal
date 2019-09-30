/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import akka.dispatch.sysmsg.SystemMessage;
import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.controllers.ItemUseObserver;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.TaskId;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.network.aion.serverpackets.SM_USE_OBJECT;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author xTz
 * @modified vlog
 */
@AIName("useitem")
public class ActionItemNpcAI2 extends NpcAI2 {

    protected boolean isInUse;
    protected int startBarAnimation = 1;
    protected int cancelBarAnimation = 2;

    @Override
    protected void handleDialogStart(Player player) {
        handleUseItemStart(player);
    }

    protected void handleUseItemStart(final Player player) {
        int delay = getTalkDelay();
        if (delay != 0) {

            if(isInUse && isForSinglePlayerUse()){
                player.sendPck(SM_SYSTEM_MESSAGE.STR_LOOT_FAIL_ONLOOTING);
                return;
            }

            isInUse = true;
            final ItemUseObserver observer = new ItemUseObserver() {

                @Override
                public void abort() {
                    isInUse = false;
                    player.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
                    PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
                    player.sendPck(new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 0, cancelBarAnimation));
                    player.getObserveController().removeObserver(this);
                }

            };

            player.getObserveController().attach(observer);
            player.sendPck(new SM_USE_OBJECT(player.getObjectId(), getObjectId(), getTalkDelay(), startBarAnimation));
            PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, getObjectId()), true);
            player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {

                    isInUse = false;
                    PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
                    player.sendPck(new SM_USE_OBJECT(player.getObjectId(), getObjectId(), getTalkDelay(), cancelBarAnimation));
                    player.getObserveController().removeObserver(observer);
                    handleUseItemFinish(player);
                }

            }, delay));

        } else {
            handleUseItemFinish(player);
        }
    }

    protected void handleUseItemFinish(Player player) {
        if (getOwner().isInInstance()) {
            AI2Actions.handleUseItemFinish(this, player);
        }
    }

    protected int getTalkDelay() {
        return getObjectTemplate().getTalkDelay() * 1000;
    }

    /**
     * Determines, can multiple players use an item at one time
     * @return
     */
    protected boolean isForSinglePlayerUse(){
        return false;
    }

}
