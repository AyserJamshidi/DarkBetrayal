/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.esoterrace;

import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.ne.gs.world.knownlist.Visitor;

/**
 * @author xTz
 */
@AIName("kexkraprototype")
public class KexkraPrototypeAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean isStartEvent = new AtomicBoolean(false);

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 75) {
            if (isStartEvent.compareAndSet(false, true)) {
                getKnownList().doOnAllPlayers(new Visitor<Player>() {

                    @Override
                    public void visit(Player player) {
                        if (player.isOnline() && !player.getLifeStats().isAlreadyDead()) {
                            player.sendPck(new SM_PLAY_MOVIE(0, 472));
                        }
                    }

                });
                spawn(217206, 1320.639282f, 1171.063354f, 51.494003f, (byte) 0);
                AI2Actions.deleteOwner(this);
            }
        }
    }

}
