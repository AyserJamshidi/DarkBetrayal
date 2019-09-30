/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.raksang;

import java.util.concurrent.atomic.AtomicBoolean;
import ai.GeneralNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.world.WorldMapInstance;

/**
 * @author xTz
 */
@AIName("raksha_sealing_wall")
public class RakshaSealingWallAI2 extends GeneralNpcAI2 {

    private final AtomicBoolean startedEvent = new AtomicBoolean(false);

    @Override
    public boolean canThink() {
        return false;
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            if (MathUtil.getDistance(getOwner(), player) <= 35) {
                if (startedEvent.compareAndSet(false, true)) {
                    WorldMapInstance instance = getPosition().getWorldMapInstance();
                    Npc sharik = instance.getNpc(217425);
                    Npc flamelord = instance.getNpc(217451);
                    Npc sealguard = instance.getNpc(217456);
                    int bossId;
                    if ((sharik == null || CreatureActions.isAlreadyDead(sharik)) && (flamelord == null || CreatureActions.isAlreadyDead(flamelord))
                        && (sealguard == null || CreatureActions.isAlreadyDead(sealguard))) {
                        bossId = 217475;
                    } else {
                        bossId = 217647;
                    }
                    spawn(bossId, 1063.08f, 903.13f, 138.744f, (byte) 29);
                    AI2Actions.deleteOwner(this);
                }
            }
        }
    }

}
