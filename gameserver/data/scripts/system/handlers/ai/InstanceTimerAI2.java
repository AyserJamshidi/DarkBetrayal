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
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Summon;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.team2.group.PlayerFilters.SameInstanceFilter;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;

/**
 * @author xTz
 */
@AIName("instancetimer")
public class InstanceTimerAI2 extends AggressiveNpcAI2 {

    private boolean isInTimer = false;
    private long curentTime;

    protected void setInstanceTimer(Creature creature) {

        int time = 0;
        switch (getNpcId()) {
            case 215222:
            case 215221:
            case 215179:
            case 215178:
            case 215136:
            case 215135:
                time = 600000;
                break;
        }

        Player player;
        if (creature instanceof Player) {
            player = (Player) creature;
        } else if (creature instanceof Summon) {
            player = (Player) creature.getMaster();
        } else {
            return;
        }
        isInTimer = true;
        curentTime = System.currentTimeMillis();
        sendTime(player, time);
    }

    private void sendTime(Player player, int time) {
        if (player.isInTeam()) {
            player.getCurrentTeam().sendPacket(new SM_QUEST_ACTION(0, time / 1000), new SameInstanceFilter(player));
        } else {
            player.sendPck(new SM_QUEST_ACTION(0, time / 1000));
        }
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (!isInTimer) {
            setInstanceTimer(creature);
        }
    }

    @Override
    public long getRemainigTime() {
        long time = System.currentTimeMillis() - curentTime;
        if (time < 0) {
            return 0;
        }
        return time > 600000 ? 0 : time;
    }
}
