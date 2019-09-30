/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance.dredgion2;

import com.ne.gs.instance.handlers.InstanceID;
import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.model.DescId;
import com.ne.gs.model.Race;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.network.aion.AionServerPacket;

@InstanceID(300110000)
public class BaranathDredgionInstance2 extends DredgionInstance2 {

    @Override
    public void onEnterInstance(Player player) {
        if (isInstanceStarted.compareAndSet(false, true)) {
            startInstanceTask();
        }
        super.onEnterInstance(player);
    }
	
    private void onDieSurkan(Npc npc, Player mostPlayerDamage, int points) {
        Race race = mostPlayerDamage.getRace();
        captureRoom(race, npc.getNpcId() + 14 - 700498);
        for (Player player : instance.getPlayersInside()) {
            AionServerPacket packet = new SM_SYSTEM_MESSAGE(1400199, DescId.of(race.equals(Race.ASMODIANS) ? 1800483 : 1800481),
                DescId.of(npc.getObjectTemplate().getNameId() * 2 + 1));
            player.sendPck(packet);
        }
        getPlayerReward(mostPlayerDamage).captureZone();
        if (++surkanKills == 5) {
            spawn(214823, 485.33f, 832.26f, 416.64f, (byte) 55);
            sendMsgByRace(1400632, Race.PC_ALL, 0);
        }
        updateScore(mostPlayerDamage, npc, points, false);
        CreatureActions.delete(npc);
    }

    @Override
    public void onDie(Npc npc) {
        Player mostPlayerDamage = npc.getAggroList().getMostPlayerDamage();
        if (mostPlayerDamage == null) {
            return;
        }
        Race race = mostPlayerDamage.getRace();
        switch (npc.getNpcId()) {
            case 215391:
			case 215082:
			case 215083:
			case 215084:
			case 215093:
			case 215090:
			case 215089:
			case 215427:
			case 215085:
			case 215087:
			case 215088:
				updateScore(mostPlayerDamage, npc, 158, false);
                return;
			case 215086:
				updateScore(mostPlayerDamage, npc, 458, false);
                CreatureActions.delete(npc);
			    return;
			case 700508:
                CreatureActions.delete(npc);
				spawn(700502, 520.876f, 493.401f, 395.342f, (byte) 28, 16);
				sendMsgByRace(1400226, Race.ELYOS, 0);
                return;
			case 700507:
                CreatureActions.delete(npc);
				spawn(700501, 448.392f, 493.642f, 395.041f, (byte) 108, 12);
				sendMsgByRace(1400227, Race.ASMODIANS, 0);
                return;
			case 700506:
                CreatureActions.delete(npc);
				spawn(730214, 554.64f, 173.535f, 433.940f, (byte) 0);
                sendMsgByRace(1400229, Race.ELYOS, 0);
                return;
			case 700505:
                CreatureActions.delete(npc);
				spawn(730213, 396.979f, 184.392f, 433.940f, (byte) 0);
                sendMsgByRace(1400228, Race.ASMODIANS, 0);
                return;
			case 700496:
			case 700495:
			case 700485:
			case 700486:
				onDieSurkan(npc, mostPlayerDamage, 500);
                return;
			case 700487:
				onDieSurkan(npc, mostPlayerDamage, 900);
                return;	
			case 700488:
			case 700489:
				onDieSurkan(npc, mostPlayerDamage, 1100);
                return;
			case 700490:
			case 700491:
				onDieSurkan(npc, mostPlayerDamage, 600);
                return;
			case 700493:
			case 700492:
				onDieSurkan(npc, mostPlayerDamage, 80);
                return;
			case 700494:
			case 700498:
			case 700497:
				onDieSurkan(npc, mostPlayerDamage, 700);
                return;
			case 798324:
			case 798328:
			case 798330:
			case 798326:
				updateScore(mostPlayerDamage, npc, 100, false);
                CreatureActions.delete(npc);
                return;
        }
		
        switch (npc.getNpcId()) {
            case 214823:
                updateScore(mostPlayerDamage, npc, 1000, false);
                stopInstance(race);
                if (race == Race.ELYOS) {
                    sendMsgByRace(1400230, Race.ELYOS, 0);
                } else {
                    sendMsgByRace(1400231, Race.ASMODIANS, 0);
                }
                return;
        }
        super.onDie(npc);
    }
	
    private void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }

    @Override
    protected void openFirstDoors() {
        openDoor(17);
        openDoor(18);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                despawnNpc(instance.getNpc(700599));
				despawnNpc(instance.getNpc(700598));
            }

        }, 180000);
    }

}
