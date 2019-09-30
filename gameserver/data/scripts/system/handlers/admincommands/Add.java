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
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.AdminService;
import com.ne.gs.services.item.ItemService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author Phantom, ATracer
 */
public class Add extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        int itemId;
        long itemCount = 1;
        Player receiver;

        try {
            itemId = Integer.parseInt(params[0]);

            if (params.length == 2) {
                itemCount = Long.parseLong(params[1]);
            }
            receiver = player;
        } catch (NumberFormatException e) {
            receiver = World.getInstance().findPlayer(params[0]);
            if (receiver == null) {
                player.sendMsg("Could not find a player by that name.");
                return;
            }

            try {
                itemId = Integer.parseInt(params[1]);

                if (params.length == 3) {
                    itemCount = Long.parseLong(params[2]);
                }
            } catch (NumberFormatException ex) {
                player.sendMsg("You must give number to itemid.");
                return;
            } catch (Exception ex2) {
                player.sendMsg("Occurs an error.");
                return;
            }
        }

        if (DataManager.ITEM_DATA.getItemTemplate(itemId) == null) {
            player.sendMsg("Item id is incorrect: " + itemId);
            return;
        }

        if (!AdminService.getInstance().canOperate(player, receiver, itemId, "command //add")) {
            return;
        }

        long count = ItemService.addItem(receiver, itemId, itemCount);

        if (count == 0) {
            if (params.length == 3) {
                player.sendMsg("You successfully gave" + params[2] + " x " + "[item:" + itemId + "]" + " to " + params[0] + ".");
                receiver.sendMsg("You received an item " + params[2] + " x " + "[item:" + itemId + "]" + " from the admin " + player.getName() + ".");
            } else {
                player.sendMsg("You successfully gave 1 " + "[item:" + itemId + "]" + " to " + params[0] + ".");
            }
        } else {
            player.sendMsg("Item couldn't be added");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //add <player> <item ID> <quantity>");
        player.sendMsg("syntax //add <item ID> <quantity>");
        player.sendMsg("syntax //add <item ID>");
    }
}
