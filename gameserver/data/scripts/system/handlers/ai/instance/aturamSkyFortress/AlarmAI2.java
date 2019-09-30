/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.aturamSkyFortress;

import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("alarm")
public class AlarmAI2 extends AggressiveNpcAI2 {

    private boolean canThink = true;
    private final AtomicBoolean startedEvent = new AtomicBoolean(false);

    @Override
    public boolean canThink() {
        return canThink;
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            if (MathUtil.getDistance(getOwner(), player) <= 23) {
                if (startedEvent.compareAndSet(false, true)) {
                    canThink = false;
                    NpcShoutsService.getInstance().sendMsg(getOwner(), 1500380, getObjectId(), 0, 0);
                    NpcShoutsService.getInstance().sendMsg(getOwner(), 1401350, 0);
                    getSpawnTemplate().setWalkerId("3002400002");
                    WalkManager.startWalking(this);
                    getOwner().setState(1);
                    PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
                    getPosition().getWorldMapInstance().getDoors().get(128).setOpen(true);
                    getPosition().getWorldMapInstance().getDoors().get(138).setOpen(true);
                    ThreadPoolManager.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            if (!isAlreadyDead()) {
                                despawn();
                            }
                        }

                    }, 3000);
                }
            }
        }
    }

    private void despawn() {
        AI2Actions.deleteOwner(this);
    }

}
