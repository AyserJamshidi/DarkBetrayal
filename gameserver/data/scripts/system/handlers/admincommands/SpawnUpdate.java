/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.io.IOException;
import java.util.List;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.spawns.SpawnGroup2;
import com.ne.gs.model.templates.spawns.SpawnTemplate;
import com.ne.gs.model.templates.walker.WalkerTemplate;
import com.ne.gs.network.aion.serverpackets.SM_DELETE;
import com.ne.gs.network.aion.serverpackets.SM_NPC_INFO;
import com.ne.gs.utils.chathandlers.ChatCommand;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author KID
 * @modified Rolandas
 */
public class SpawnUpdate extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params[0].equalsIgnoreCase("set")) {
            Npc npc = null;
            if (admin.getTarget() != null && admin.getTarget() instanceof Npc) {
                npc = (Npc) admin.getTarget();
            }

            if (npc == null) {
                admin.sendMsg("you need to target Npc type.");
                return;
            }

            SpawnTemplate spawn = npc.getSpawn();

            if (params[1].equalsIgnoreCase("x")) {
                float x;
                if (params.length < 3) {
                    x = admin.getX();
                } else {
                    x = Float.parseFloat(params[2]);
                }
                npc.getPosition().setX(x);
                admin.sendPck(new SM_DELETE(npc, 0));
                admin.sendPck(new SM_NPC_INFO(npc, admin));
                admin.sendMsg("updated npcs x to " + x + ".");
                try {
                    DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
                } catch (IOException e) {
                    e.printStackTrace();
                    admin.sendMsg("Could not save spawn");
                }
                return;
            }

            if (params[1].equalsIgnoreCase("y")) {
                float y;
                if (params.length < 3) {
                    y = admin.getY();
                } else {
                    y = Float.parseFloat(params[2]);
                }
                npc.getPosition().setY(y);
                admin.sendPck(new SM_DELETE(npc, 0));
                admin.sendPck(new SM_NPC_INFO(npc, admin));
                admin.sendMsg("updated npcs y to " + y + ".");
                try {
                    DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
                } catch (IOException e) {
                    e.printStackTrace();
                    admin.sendMsg("Could not save spawn");
                }
                return;
            }

            if (params[1].equalsIgnoreCase("z")) {
                float z;
                if (params.length < 3) {
                    z = admin.getZ();
                } else {
                    z = Float.parseFloat(params[2]);
                }
                npc.getPosition().setZ(z);
                admin.sendPck(new SM_DELETE(npc, 0));
                admin.sendPck(new SM_NPC_INFO(npc, admin));
                admin.sendMsg("updated npcs z to " + z + ".");
                try {
                    DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
                } catch (IOException e) {
                    e.printStackTrace();
                    admin.sendMsg("Could not save spawn");
                }
                return;
            }

            if (params[1].equalsIgnoreCase("h")) {
                int h;
                if (params.length < 3) {
                    int heading = admin.getHeading();
                    if (heading > 60) {
                        heading -= 60;
                    } else {
                        heading += 60;
                    }
                    h = heading;
                } else {
                    h = Byte.parseByte(params[2]);
                }
                npc.getPosition().setH(h);
                admin.sendPck(new SM_DELETE(npc, 0));
                admin.sendPck(new SM_NPC_INFO(npc, admin));
                admin.sendMsg("updated npcs heading to " + h + ".");
                try {
                    DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
                } catch (IOException e) {
                    e.printStackTrace();
                    admin.sendMsg("Could not save spawn");
                }
                return;
            }

            if (params[1].equalsIgnoreCase("xyz")) {
                admin.sendPck(new SM_DELETE(npc, 0));
                npc.getPosition().setX(admin.getX());
                try {
                    DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
                    admin.sendPck(new SM_NPC_INFO(npc, admin));
                    npc.getPosition().setY(admin.getY());
                    DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
                    admin.sendPck(new SM_NPC_INFO(npc, admin));
                    npc.getPosition().setZ(admin.getZ());
                    DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
                    admin.sendPck(new SM_NPC_INFO(npc, admin));
                    admin.sendMsg("updated npcs coordinates to " + admin.getX() + ", " + admin.getY() + ", " + admin.getZ() + ".");
                } catch (IOException e) {
                    e.printStackTrace();
                    admin.sendMsg("Could not save spawn");
                }
                return;
            }

            if (params[1].equalsIgnoreCase("w")) {
                String walkerId = null;
                if (params.length == 3) {
                    walkerId = params[2].toLowerCase();
                }
                if (walkerId != null) {
                    WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(walkerId);
                    if (template == null) {
                        admin.sendMsg("No such template exists in npc_walker.xml.");
                        return;
                    }
                    List<SpawnGroup2> allSpawns = DataManager.SPAWNS_DATA2.getSpawnsByWorldId(npc.getWorldId());
                    List<SpawnTemplate> allSpots = flatten(extractIterator(allSpawns, on(SpawnGroup2.class).getSpawnTemplates()));
                    List<SpawnTemplate> sameIds = filter(having(on(SpawnTemplate.class).getWalkerId(), equalTo(walkerId)), allSpots);
                    if (sameIds.size() >= template.getPool()) {
                        admin.sendMsg("Can not assign, walker pool reached the limit.");
                        return;
                    }
                }
                spawn.setWalkerId(walkerId);
                admin.sendPck(new SM_DELETE(npc, 0));
                admin.sendPck(new SM_NPC_INFO(npc, admin));
                if (walkerId == null) {
                    admin.sendMsg("removed npcs walker_id for " + npc.getNpcId() + ".");
                } else {
                    admin.sendMsg("updated npcs walker_id to " + walkerId + ".");
                }
                try {
                    DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
                } catch (IOException e) {
                    e.printStackTrace();
                    admin.sendMsg("Could not save spawn");
                }
            }
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("<usage //spawnu set (x | y | z | h | w | xyz)");
    }
}
