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
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author Phantom, ATracer
 */
public class Remove extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 2) {
            admin.sendMsg("syntax //remove <player> <item ID> <quantity>");
            return;
        }

        int itemId;
        long itemCount = 1;
        Player target = World.getInstance().findPlayer(params[0]);
        if (target == null) {
            admin.sendMsg("Player isn't online.");
            return;
        }

        try {
            itemId = Integer.parseInt(params[1]);
            if (params.length == 3) {
                itemCount = Long.parseLong(params[2]);
            }
        } catch (NumberFormatException e) {
            admin.sendMsg("Parameters need to be an integer.");
            return;
        }

        Storage bag = target.getInventory();

        long itemsInBag = bag.getItemCountByItemId(itemId);
        if (itemsInBag == 0) {
            admin.sendMsg("Items with that id are not found in the player's bag.");
            return;
        }

        Item item = bag.getFirstItemByItemId(itemId);
        bag.decreaseByObjectId(item.getObjectId(), itemCount);

        admin.sendMsg("Item(s) removed succesfully");
        target.sendMsg("Admin removed an item from your bag");
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //remove <player> <item ID> <quantity>");
    }
}
