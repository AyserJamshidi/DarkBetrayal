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
import com.ne.gs.model.items.ItemId;
import com.ne.gs.services.item.ItemService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author Sarynth Simple admin assistance command for adding kinah to self, named player or target
 *         player. Based on //add command. Kinah Item Id - 182400001 (Using ItemId.KINAH.value())
 */
public class Kinah extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        long kinahCount;
        Player receiver;

        if (params.length == 1) {
            receiver = admin;
            try {
                kinahCount = Long.parseLong(params[0]);
            } catch (NumberFormatException e) {
                admin.sendMsg("Kinah value must be an integer.");
                return;
            }
        } else {
            receiver = World.getInstance().findPlayer(params[0]);

            if (receiver == null) {
                admin.sendMsg("Could not find a player by that name.");
                return;
            }

            try {
                kinahCount = Long.parseLong(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("Kinah value must be an integer.");
                return;
            }
        }

        long count = ItemService.addItem(receiver, ItemId.KINAH.value(), kinahCount);

        if (count == 0) {
            admin.sendMsg("Kinah given successfully.");
            receiver.sendMsg("An admin gives you some kinah.");
        } else {
            admin.sendMsg("Kinah couldn't be given.");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //kinah [player] <quantity>");
    }
}
