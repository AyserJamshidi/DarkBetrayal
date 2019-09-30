/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.toypet.PetAdoptionService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author ATracer
 */
public class Pet extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        String cmd = params[0];
        if ("add".equals(cmd)) {
            int petId = Integer.parseInt(params[1]);
            String name = params[2];
            PetAdoptionService.addPet(player, petId, 0, name, 0);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //pet <add [petid name]>");
    }
}
