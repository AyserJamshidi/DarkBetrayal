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

import com.ne.commons.annotations.NotNull;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.spawns.SpawnTemplate;
import com.ne.gs.spawnengine.SpawnEngine;
import com.ne.gs.utils.chathandlers.ChatCommand;
import mw.processors.movement.motor.FollowMotor;

/**
 * @author Luno
 */
public class SpawnNpc extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            admin.sendMsg("syntax //spawn <template_id> <respawn_time> {temp}");
            return;
        }

        int respawnTime = 295;
        boolean permanent = true;

        if (params.length >= 2) {
            respawnTime = Integer.valueOf(params[1]);
        }
        if (params.length == 3 && "temp".equalsIgnoreCase(params[2])) {
            permanent = false;
        }

        int templateId = Integer.parseInt(params[0]);
        float x = admin.getX();
        float y = admin.getY();
        float z = admin.getZ();
        int heading = admin.getHeading();
        int worldId = admin.getWorldId();

        SpawnTemplate spawn = SpawnEngine.addNewSpawn(worldId, templateId, x, y, z, heading, respawnTime);

        if (spawn == null) {
            admin.sendMsg("There is no template with id " + templateId);
            return;
        }

        VisibleObject visibleObject = SpawnEngine.spawnObject(spawn, admin.getInstanceId());

        if (visibleObject == null) {
            admin.sendMsg("Spawn id " + templateId + " was not found!");
        } else if (permanent) {

            try {
                DataManager.SPAWNS_DATA2.saveSpawn(admin, visibleObject, false);
            } catch (IOException e) {
                e.printStackTrace();
                admin.sendMsg("Could not save spawn");
            }
        }

        String objectName = visibleObject.getObjectTemplate().getName();
        admin.sendMsg(objectName + " spawned");
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //spawn <template_id> <respawn_time> {temp}");
    }
}
