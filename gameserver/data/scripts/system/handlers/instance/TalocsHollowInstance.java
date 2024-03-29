/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.flyring.FlyRing;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.StaticDoor;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.model.templates.flyring.FlyRingTemplate;
import com.ne.gs.model.templates.spawns.SpawnTemplate;
import com.ne.gs.model.utils3d.Point3D;
import com.ne.gs.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.item.ItemService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.knownlist.Visitor;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;

/**
 * @author xTz
 */
@InstanceID(300190000)
public class TalocsHollowInstance extends GeneralInstanceHandler {

    private final List<Integer> movies = new ArrayList<>();
    private Map<Integer, StaticDoor> doors;

    @Override
    public void onEnterInstance(Player player) {
        addItems(player);
    }

    @Override
    public void onLeaveInstance(Player player) {
        removeItems(player);
    }

    private void addItems(Player player) {
        QuestState qs1 = player.getQuestStateList().getQuestState(10021);
        QuestState qs2 = player.getQuestStateList().getQuestState(20021);
        if ((qs1 != null && qs1.getStatus() == QuestStatus.START) || (qs2 != null && qs2.getStatus() == QuestStatus.START)) {
            return;
        }
        switch (player.getRace()) {
            case ELYOS:
                ItemService.addItem(player, 160001286, 1);
                ItemService.addItem(player, 164000099, 1);
                break;
            case ASMODIANS:
                ItemService.addItem(player, 160001287, 1);
                ItemService.addItem(player, 164000099, 1);
                break;
        }
    }

    private void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(164000099, storage.getItemCountByItemId(164000099));
        storage.decreaseByItemId(160001286, storage.getItemCountByItemId(160001286));
        storage.decreaseByItemId(160001287, storage.getItemCountByItemId(160001287));
        player.getEffectController().removeEffect(10251);
        player.getEffectController().removeEffect(10252);
    }

    @Override
    public void onDie(Npc npc) {
        switch (npc.getNpcId()) {
            case 215467:
                openDoor(48);
                openDoor(49);
                break;
            case 215457:
                Npc newNpc = getNpc(700633);
                if (newNpc != null) {
                    newNpc.getController().onDelete();
                }
                break;
            case 700739:
                npc.getKnownList().doOnAllPlayers(new Visitor<Player>() {
                    @Override
                    public void visit(Player player) {
                        player.sendPck(new SM_SYSTEM_MESSAGE(1400477));
                    }
                });
                SpawnTemplate template = npc.getSpawn();
                spawn(281817, template.getX(), template.getY(), template.getZ(), template.getHeading(), 9);
                npc.getController().onDelete();
                break;
            case 215488:
                Player player = npc.getAggroList().getMostPlayerDamage();
                if (player != null) {
                    player.sendPck(new SM_PLAY_MOVIE(0, 10021, 437, 0));
                }
                Npc newNpc2 = getNpc(700740);
                if (newNpc2 != null) {
                    SpawnTemplate template2 = newNpc2.getSpawn();
                    spawn(700741, template2.getX(), template2.getY(), template2.getZ(), template2.getHeading(), 92);
                    newNpc2.getController().onDelete();
                }
                spawn(799503, 548f, 811f, 1375f, (byte) 0);
                break;
        }
    }

    @Override
    public void handleUseItemFinish(Player player, Npc npc) {
        switch (npc.getNpcId()) {
            case 700940:
                player.getLifeStats().increaseHp(SM_ATTACK_STATUS.TYPE.HP, 20000);
                CreatureActions.delete(npc);
                break;
            case 700941:
                player.getLifeStats().increaseHp(SM_ATTACK_STATUS.TYPE.HP, 30000);
                CreatureActions.delete(npc);
                break;
        }
    }

    @Override
    public void onInstanceDestroy() {
        movies.clear();
        doors.clear();
    }

    private void sendMovie(Player player, int movie) {
        if (!movies.contains(movie)) {
            movies.add(movie);
            player.sendPck(new SM_PLAY_MOVIE(0, movie));
        }
    }

    @Override
    public void onPlayerLogOut(Player player) {
        removeItems(player);
    }

    private void openDoor(int doorId) {
        StaticDoor door = doors.get(doorId);
        if (door != null) {
            door.setOpen(true);
        }
    }

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        doors = instance.getDoors();
        doors.get(48).setOpen(true);
        doors.get(7).setOpen(true);
        spawnRings();
    }

    private void spawnRings() {
        FlyRing f1 = new FlyRing(new FlyRingTemplate("TALOCS_1", mapId, new Point3D(253.85039, 649.23535, 1171.8772), new Point3D(253.85039, 649.23535,
                1177.8772), new Point3D(262.84872, 649.4091, 1171.8772), 8), instanceId);
        f1.spawn();
        FlyRing f2 = new FlyRing(new FlyRingTemplate("TALOCS_2", mapId, new Point3D(592.32275, 844.056, 1295.0966), new Point3D(592.32275, 844.056,
                1301.0966), new Point3D(595.2305, 835.5387, 1295.0966), 8), instanceId);
        f2.spawn();
    }

    @Override
    public boolean onPassFlyingRing(Player player, String flyingRing) {
        if (flyingRing.equals("TALOCS_1")) {
            sendMovie(player, 463);
        } else if (flyingRing.equals("TALOCS_2")) {
            sendMovie(player, 464);
        }
        return false;
    }
	
    @Override
    public boolean onReviveEvent(Player player) {
        PlayerReviveService.revive(player, 25, 25, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        player.sendPck(SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        TeleportService.teleportTo(player, mapId, instanceId, 196.3f, 227.2366f, 1098.715f, (byte) 35);
        return true;
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
                true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }
}
