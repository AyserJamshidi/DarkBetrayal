/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.drop.DropItem;
import com.ne.gs.model.flyring.FlyRing;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.StaticDoor;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.flyring.FlyRingTemplate;
import com.ne.gs.model.utils3d.Point3D;
import com.ne.gs.network.aion.AionServerPacket;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.knownlist.Visitor;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.utils.PacketSendUtility;

import java.util.Map;
import java.util.Set;

@InstanceID(300470000)
public class SatraTreasureHoardInstance extends GeneralInstanceHandler {

    private Map<Integer, StaticDoor> doors;
    private boolean isStartTimer = false;

    @Override
    public void onInstanceDestroy() {
        doors.clear();
    }

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        doors = instance.getDoors();
        doors.get(77).setOpen(true);
        spawnTimerRing();
    }

    private void spawnTimerRing() {
        FlyRing f1 = new FlyRing(new FlyRingTemplate("SATRAS_01", mapId, new Point3D(501.13412, 672.4659, 177.10771), new Point3D(492.13412, 672.4659,
                177.10771), new Point3D(496.54834, 671.5966, 184.10771), 8), instanceId);
        f1.spawn();
    }

    @Override
    public boolean onPassFlyingRing(Player player, String flyingRing) {
        if (flyingRing.equals("SATRAS_01")) {
            if (!isStartTimer) {
                isStartTimer = true;
                System.currentTimeMillis();
                sendPacket(new SM_QUEST_ACTION(0, 600));
                doors.get(77).setOpen(false);
                // Spawn Chest When the timer begin
                final Npc chest1 = (Npc) spawn(701461, 466.246f, 716.57f, 176.398f, (byte) 0);
                final Npc chest2 = (Npc) spawn(701461, 528.156f, 715.66f, 176.398f, (byte) 60);
                final Npc chest3 = (Npc) spawn(701461, 469.17f, 701.632f, 176.398f, (byte) 11);
                final Npc chest4 = (Npc) spawn(701461, 524.292f, 701.063f, 176.398f, (byte) 50);
                final Npc chest5 = (Npc) spawn(701461, 515.439f, 691.87f, 176.398f, (byte) 45);
                spawn(701461, 478.623f, 692.772f, 176.398f, (byte) 15);
                Npc muzzledPunisher = getNpc(219347);
                if (muzzledPunisher == null) {
                    spawnElaborateChest();
                } else {
                    spawnHeavyChest();
                }
                // Delete chest according with a timer (5 Min > 10) http://static.plaync.co.kr/powerbook/aion/23/83/3cbde3efd2422e9249efcfea.png
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        chest1.getController().delete();
                    }

                }, 300000);
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        chest2.getController().delete();
                    }

                }, 360000);
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        chest3.getController().delete();
                    }

                }, 420000);
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        chest4.getController().delete();
                    }

                }, 480000);
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        chest5.getController().delete();
                    }

                }, 540000);
            }
        }
        return false;
    }

    private void spawnHeavyChest() {
        spawn(701462, 446.962f, 744.254f, 178.071f, (byte) 0, 206);
        spawn(701462, 459.856f, 759.960f, 178.071f, (byte) 0, 81);
        spawn(701462, 533.697f, 760.551f, 178.071f, (byte) 0, 80);
        spawn(701462, 477.382f, 770.049f, 178.071f, (byte) 0, 83);
        spawn(701462, 497.030f, 773.931f, 178.071f, (byte) 0, 85);
        spawn(701462, 516.508f, 770.646f, 178.071f, (byte) 0, 122);
    }

    private void spawnElaborateChest() {
        spawn(701463, 446.962f, 744.254f, 178.071f, (byte) 0, 206);
        spawn(701463, 459.856f, 759.960f, 178.071f, (byte) 0, 81);
        spawn(701463, 533.697f, 760.551f, 178.071f, (byte) 0, 80);
        spawn(701463, 477.382f, 770.049f, 178.071f, (byte) 0, 83);
        spawn(701463, 497.030f, 773.931f, 178.071f, (byte) 0, 85);
        spawn(701463, 516.508f, 770.646f, 178.071f, (byte) 0, 122);
    }

    @Override
    public void onDie(Npc npc) {
        switch (npc.getNpcId()) {
            case 219347: // muzzled punisher
            case 219348: // punisher unleashed
                spawn(730588, 496.600f, 685.600f, 176.400f, (byte) 30); // Spawn Exit
                break;
            case 701464: // artifact spawn stronger boss
                Npc boss = getNpc(219347);
                if (boss != null && !boss.getLifeStats().isAlreadyDead()) {
                    spawn(219348, boss.getX(), boss.getY(), boss.getZ(), boss.getHeading());
                    boss.getController().onDelete();
                }
                break;        
            case 219344:
                if (Rnd.chance(50)) {
                    doors.get(88).setOpen(true);
                } else {
                    doors.get(84).setOpen(true);
                }
                break;
            case 219346:
                doors.get(118).setOpen(true);
                doors.get(108).setOpen(true);
                break;
            case 219345:
                doors.get(117).setOpen(true);
                doors.get(86).setOpen(true);
                break;
        }
    }

    private void sendPacket(final AionServerPacket packet) {
        instance.doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                player.sendPck(packet);
            }

        });
    }
	
    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
            true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }
	
    @Override
    public boolean onReviveEvent(Player player) {
        PlayerReviveService.revive(player, 25, 25, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        player.sendPck(SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        TeleportService.teleportTo(player, mapId, instanceId, 510.0055f, 187.5f, 158.6f, (byte) 30);
        return true;
    }

}
