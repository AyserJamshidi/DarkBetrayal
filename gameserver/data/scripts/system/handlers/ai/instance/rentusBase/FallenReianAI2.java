/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.rentusBase;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.MathUtil;

/**
 * @author xTz
 */
@AIName("fallen_reian")
public class FallenReianAI2 extends NpcAI2 {

    private final AtomicBoolean isCollapsed = new AtomicBoolean(false);

    private int doorId;

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        doorId = getNpcId() == 799661 ? 16 : 54;
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            if (MathUtil.getDistance(getOwner(), player) <= doorId) {
                if (MathUtil.getDistance(getOwner(), getPosition().getWorldMapInstance().getDoors().get(doorId)) <= 30) {
                    if (isCollapsed.compareAndSet(false, true)) {
                        getPosition().getWorldMapInstance().getDoors().get(doorId).setOpen(true);
                    }
                }
            }
        }
    }

}
