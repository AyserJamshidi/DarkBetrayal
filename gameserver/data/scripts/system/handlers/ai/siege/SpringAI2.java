/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.siege;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.siege.SiegeNpc;
import com.ne.gs.model.stats.container.CreatureLifeStats;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author xTz
 */
@AIName("spring")
public class SpringAI2 extends NpcAI2 {

    @Override
    public void handleSpawned() {
        startSchedule();
    }

    private void startSchedule() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                checkForHeal();
            }
        }, 5000);
    }

    private void checkForHeal() {
        if (!isAlreadyDead() && getPosition().isSpawned()) {
            for (VisibleObject object : getKnownList().getKnownObjects().values()) {
                Creature creature = (Creature) object;
                CreatureLifeStats<?> lifeStats = creature.getLifeStats();
                if (isInRange(creature, 10) && !creature.getEffectController().hasAbnormalEffect(19116) && !lifeStats.isAlreadyDead()
                    && (lifeStats.getCurrentHp() < lifeStats.getMaxHp())) {
                    if (creature instanceof SiegeNpc) {
                        SiegeNpc npc = (SiegeNpc) creature;
                        if (getObjectTemplate().getRace() == npc.getObjectTemplate().getRace()) {
                            doHeal();
                            break;
                        }
                    } else if (creature instanceof Player) {
                        Player player = (Player) creature;
                        if (getObjectTemplate().getRace() == player.getRace() && player.isOnline()) {
                            doHeal();
                            break;
                        }
                    }
                }
            }
            startSchedule();
        }
    }

    private void doHeal() {
        AI2Actions.targetSelf(this);
        AI2Actions.useSkill(this, 19116);
    }
}
