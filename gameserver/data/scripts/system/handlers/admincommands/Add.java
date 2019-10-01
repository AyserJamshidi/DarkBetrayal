/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import com.ne.commons.Util;
import com.ne.commons.annotations.NotNull;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.AdminService;
import com.ne.gs.services.item.ItemService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Phantom, ATracer
 */
public class Add extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if ((params.length < 0) || (params.length < 1)) {
            onError(player, null);
            return;
        }

        int itemId = 0;
        long itemCount = 1;
        Player receiver;

        try {
            String item = params[0];
            // Some item links have space before Id
            if (item.equals("[item:")) {
                item = params[1];
                Pattern id = Pattern.compile("(\\d{9})");
                Matcher result = id.matcher(item);
                if (result.find())
                    itemId = Integer.parseInt(result.group(1));

                if (params.length == 3)
                    itemCount = Long.parseLong(params[2]);
            } else {
                Pattern id = Pattern.compile("\\[item:(\\d{9})");
                Matcher result = id.matcher(item);

                if (result.find())
                    itemId = Integer.parseInt(result.group(1));
                else
                    itemId = Integer.parseInt(params[0]);

                if (params.length == 2)
                    itemCount = Long.parseLong(params[1]);
            }
            receiver = player;
        } catch (NumberFormatException e) {
            receiver = World.getInstance().findPlayer(Util.convertName(params[0]));
            if (receiver == null) {
                player.sendMsg("Could not find a player by that name.");
                return;
            }

            try {
                String item = params[1];
                // Some item links have space before Id
                if (item.equals("[item:")) {
                    item = params[2];
                    Pattern id = Pattern.compile("(\\d{9})");
                    Matcher result = id.matcher(item);
                    if (result.find())
                        itemId = Integer.parseInt(result.group(1));

                    if (params.length == 4)
                        itemCount = Long.parseLong(params[3]);
                } else {
                    Pattern id = Pattern.compile("\\[item:(\\d{9})");
                    Matcher result = id.matcher(item);

                    if (result.find())
                        itemId = Integer.parseInt(result.group(1));
                    else
                        itemId = Integer.parseInt(params[1]);

                    if (params.length == 3)
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

        if (!AdminService.getInstance().canOperate(player, receiver, itemId, "command //add"))
            return;

        long count = ItemService.addItem(receiver, itemId, itemCount);

        if (count == 0) {
            if (player != receiver) {
                player.sendMsg("You successfully gave " + itemCount + " x [item:"
                        + itemId + "] to " + receiver.getName() + ".");
                receiver.sendMsg("You successfully received " + itemCount + " x [item:"
                        + itemId + "] from " + player.getName() + ".");
            } else
                player.sendMsg("You successfully received " + itemCount + " x [item:" + itemId + "]");
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
