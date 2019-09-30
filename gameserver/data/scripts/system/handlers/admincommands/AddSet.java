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
import com.ne.gs.model.templates.itemset.ItemPart;
import com.ne.gs.model.templates.itemset.ItemSetTemplate;
import com.ne.gs.services.item.ItemService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author Antivirus
 */
public class AddSet extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length == 0 || params.length > 2) {
            onError(player, null);
            return;
        }

        int itemSetId;
        Player receiver;

        try {
            itemSetId = Integer.parseInt(params[0]);
            receiver = player;
        } catch (NumberFormatException e) {
            receiver = World.getInstance().findPlayer(params[0]);

            if (receiver == null) {
                player.sendMsg("Could not find a player by that name.");
                return;
            }

            try {
                itemSetId = Integer.parseInt(params[1]);
            } catch (NumberFormatException ex) {

                player.sendMsg("You must give number to itemset ID.");
                return;
            } catch (Exception ex2) {
                player.sendMsg("Occurs an error.");
                return;
            }
        }

        ItemSetTemplate itemSet = DataManager.ITEM_SET_DATA.getItemSetTemplate(itemSetId);
        if (itemSet == null) {
            player.sendMsg("ItemSet does not exist with id " + itemSetId);
            return;
        }

        if (receiver.getInventory().getFreeSlots() < itemSet.getItempart().size()) {
            player.sendMsg("Inventory needs at least " + itemSet.getItempart().size() + " free slots.");
            return;
        }

        for (ItemPart setPart : itemSet.getItempart()) {
            long count = ItemService.addItem(receiver, setPart.getItemid(), 1);

            if (count != 0) {
                player.sendMsg("Item " + setPart.getItemid() + " couldn't be added");
                return;
            }
        }

        player.sendMsg("Item Set added successfully");
        receiver.sendMsg("admin gives you an item set");
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //addset <player> <itemset ID>");
        player.sendMsg("syntax //addset <itemset ID>");
    }

}
