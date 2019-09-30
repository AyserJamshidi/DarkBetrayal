/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance.dredgion2;

import com.ne.commons.utils.Rnd;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.DescId;
import com.ne.gs.model.Race;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.AionServerPacket;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;

/**
 * @author xTz
 */
@InstanceID(300440000)
public class TerathDredgionInstance2 extends DredgionInstance2 {

    @Override
    public void onEnterInstance(Player player) {
        if (isInstanceStarted.compareAndSet(false, true)) {
            sp(730558, 415.034f, 174.004f, 433.940f, (byte) 0, 34, 720000);
            sp(730559, 572.038f, 185.252f, 433.940f, (byte) 0, 10, 720000);
            sendMsgByRace(1401424, Race.PC_ALL, 720000);
            if (Rnd.chance(21)) {
                sp(219265, 480.686f, 307.090f, 402.696f, (byte) 113, 720000);
            }
            if (Rnd.chance(51)) {
                sp(219266, 485.403f, 596.602f, 390.944f, (byte) 90, 720000);
            }
            if (Rnd.chance(21)) {
                spawn(219333, 486.26382f, 906.011f, 405.24463f, (byte) 90);
            }
            if (Rnd.chance(51)) {
                switch (Rnd.get(2)) {
                    case 0:
                        spawn(219255, 416.3429f, 282.32785f, 409.7311f, (byte) 80);
                        break;
                    default:
                        spawn(219255, 552.07446f, 289.058f, 409.7311f, (byte) 80);
                        break;
                }
            }
            int spawnTime = Rnd.get(10, 15) * 60 * 1000 + 120000;
            sendMsgByRace(1401417, Race.PC_ALL, spawnTime);
            sp(219270, 484.664f, 314.207f, 403.715f, (byte) 30, spawnTime);
            startInstanceTask();
        }
        super.onEnterInstance(player);
    }

    private void onDieSurkan(Npc npc, Player mostPlayerDamage, int points) {
        Race race = mostPlayerDamage.getRace();
        captureRoom(race, npc.getNpcId() + 14 - 701454);
        for (Player player : instance.getPlayersInside()) {
            AionServerPacket packet = new SM_SYSTEM_MESSAGE(1400199, DescId.of(race.equals(Race.ASMODIANS) ? 1800483 : 1800481),
                DescId.of(npc.getObjectTemplate().getNameId() * 2 + 1));
            player.sendPck(packet);
        }
        if (++surkanKills == 5) {
            spawn(219264, 485.423f, 808.826f, 416.868f, (byte) 30);
            sendMsgByRace(1401416, Race.PC_ALL, 0);
        }
        getPlayerReward(mostPlayerDamage).captureZone();
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
            case 701441:
            case 701442:
                onDieSurkan(npc, mostPlayerDamage, 500);
                return;
            case 701443:
				onDieSurkan(npc, mostPlayerDamage, 900);
                return;
            case 701451:
            case 701452:
				onDieSurkan(npc, mostPlayerDamage, 500);
                return;
            case 701453:
            case 701454:
                onDieSurkan(npc, mostPlayerDamage, 700);
                return;
            case 701448:
            case 701449:
                onDieSurkan(npc, mostPlayerDamage, 500);
                return;
            case 701450:
                onDieSurkan(npc, mostPlayerDamage, 900);
                return;
            case 701444:
            case 701445:
                onDieSurkan(npc, mostPlayerDamage, 1080);
                return;
            case 701446:
            case 701447:
                onDieSurkan(npc, mostPlayerDamage, 600);
                return;
            case 730572:
                spawn(730566, 446.729f, 493.224f, 395.938f, (byte) 0, 12);
                CreatureActions.delete(npc);
                return;
            case 730573:
                spawn(730567, 520.404f, 493.261f, 395.938f, (byte) 0, 133);
                CreatureActions.delete(npc);
                return;
            case 730570:
                sendMsgByRace(1401418, race, 0);
                spawn(730560, 396.979f, 184.392f, 433.940f, (byte) 0, 42);
                break;
            case 730571:
                sendMsgByRace(1401418, race, 0);
                spawn(730561, 554.64f, 173.535f, 433.940f, (byte) 0, 9);
                break;
            case 219271:
                sendMsgByRace(1401419, Race.PC_ALL, 0);
                if (race.equals(Race.ASMODIANS)) {
                    spawn(730563, 496.178f, 761.770f, 390.805f, (byte) 0, 186);
                } else {
                    spawn(730562, 473.759f, 761.864f, 390.805f, (byte) 0, 33);
                }
                return;
            case 219270:
                updateScore(mostPlayerDamage, npc, 1000, false);
                return;
            case 219264:
                if (!dredgionReward.isRewarded()) {
                    updateScore(mostPlayerDamage, npc, 1000, false);
                    Race winningRace = dredgionReward.getWinningRaceByScore();
                    stopInstance(winningRace);
                }
                return;
            case 701439:
                updateScore(mostPlayerDamage, npc, 100, false);
                CreatureActions.delete(npc);
                return;
        }
        super.onDie(npc);
    }

    @Override
    protected void openFirstDoors() {
        openDoor(173);
        openDoor(4);
    }

}
