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
import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.PersistentState;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.ne.gs.services.custom.ChangeColorService;
import com.ne.gs.services.item.ItemPacketService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author loleron pieced together from DyeAction.java, Set.java
 */
public class Dye extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        Player target;

        // Add a check to prevent players to dye other people
        if (admin.getAccessLevel() > 0 && admin.getTarget() instanceof Player) {
            target = (Player) admin.getTarget();
        } else {
            target = admin;
        }

        if (target == null) {
            admin.sendMsg("You should select a target first!");
            return;
        }

        if (params.length == 0 || params.length > 2) {
            admin.sendMsg("syntax //dye <dye color|hex color|no>");
            return;
        }

        long price = CustomConfig.DYE_PRICE;
        if (admin.getInventory().getKinah() < price && !admin.isGM()) {
            admin.sendMsg("You need " + CustomConfig.DYE_PRICE + " kinah to dye yourself.");
            return;
        }

        String color;
        if (params.length == 2) {
            if (params[1].equalsIgnoreCase("petal")) {
                color = params[0];
            } else {
                color = params[0] + " " + params[1];
            }
        } else {
            color = params[0];
        }

        int rgb;
        int bgra = 0;
        String retcolor = ChangeColorService.getInstance().colors.get(color);
        if(retcolor != null)
        	color = retcolor;

        try {
            rgb = Integer.parseInt(color, 16);
            bgra = 0xFF | (rgb & 0xFF) << 24 | (rgb & 0xFF00) << 8 | (rgb & 0xFF0000) >>> 8;
        } catch (NumberFormatException e) {
            if (!color.equalsIgnoreCase("no")) {
                admin.sendMsg("[Dye] Can't understand: " + color);
                return;
            }
        }

        if (!admin.isGM()) {
            admin.getInventory().decreaseKinah(price);
        }

        for (Item targetItem : target.getEquipment().getEquippedItemsWithoutStigma()) {
            if (color.equals("no")) {
                targetItem.setItemColor(0);
            } else {
                targetItem.setItemColor(bgra);
            }
            ItemPacketService.updateItemAfterInfoChange(target, targetItem);
        }
        PacketSendUtility.broadcastPacket(target, //
            new SM_UPDATE_PLAYER_APPEARANCE(target.getObjectId(), target.getEquipment().getEquippedItemsWithoutStigma()), true);
        target.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
        if (!target.getObjectId().equals(admin.getObjectId())) {
            target.sendMsg("You have been dyed by " + admin.getName() + "!");
        }
        admin.sendMsg("Dyed " + target.getName() + " successfully!");
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub
    }
}
