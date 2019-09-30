/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.handler.FollowEventHandler;
import com.ne.gs.model.TaskId;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.npcshout.NpcShout;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author -Nemesiss-
 */
@AIName("deliveryman")
public class DeliveryManAI2 extends FollowingNpcAI2 {

    public static int EVENT_SET_CREATOR = 1;
    private static final int SERVICE_TIME = 5 * 60 * 1000;
    private static final int SPAWN_ACTION_DELAY = 1500;
    private static final int POSTMAN_REUSE_DELAY = 5 * 30 * 1000;
    private Player owner;

    @Override
    protected void handleSpawned() {
        ThreadPoolManager.getInstance().schedule(new DeleteDeliveryMan(), SERVICE_TIME);
        ThreadPoolManager.getInstance().schedule(new DeliveryManSpawnAction(), SPAWN_ACTION_DELAY);
        ThreadPoolManager.getInstance().schedule(new DeliveryManReuseTask(), POSTMAN_REUSE_DELAY);

        super.handleSpawned();
    }

    @Override
    protected void handleDespawned() {
        PacketSendUtility.broadcastPacket(getOwner(), new SM_SYSTEM_MESSAGE(true, 390267, getObjectId(), 1, new NpcShout().getParam()));

        super.handleDespawned();
    }

    @Override
    protected void handleDialogStart(Player player) {
        if (player.equals(owner)) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 18));
            player.getMailbox().sendMailList(true);
        }
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature == owner) {
            FollowEventHandler.creatureMoved(this, creature);
        }
    }

    @Override
    protected void handleCustomEvent(int eventId, Object... args) {
        if (eventId == EVENT_SET_CREATOR) {
            owner = (Player) args[0];
        }
    }

    private final class DeleteDeliveryMan implements Runnable {
        @Override
        public void run() {
            AI2Actions.deleteOwner(DeliveryManAI2.this);
        }
    }

    private final class DeliveryManSpawnAction implements Runnable {
        @Override
        public void run() {
            PacketSendUtility.broadcastPacket(getOwner(), new SM_SYSTEM_MESSAGE(true, 390266, getObjectId(), 1, new NpcShout().getParam()));
            handleFollowMe(owner);
            handleCreatureMoved(owner);
        }

    }
    
    private final class DeliveryManReuseTask implements Runnable {
        @Override
        public void run() {
            owner.getController().cancelTask(TaskId.EXPRESS_MAIL_USE);
        }
    }

}
