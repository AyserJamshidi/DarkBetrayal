/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import java.util.ArrayList;
import java.util.List;

import com.ne.commons.annotations.NotNull;
import com.ne.commons.utils.Callback;
import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.PersistentState;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.ingameshop.InGameShopEn;
import com.ne.gs.model.items.ItemSlot;
import com.ne.gs.network.aion.serverpackets.SM_TOLL_INFO;
import com.ne.gs.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.ne.gs.network.loginserver.LoginServer;
import com.ne.gs.network.loginserver.serverpackets.SM_ACCOUNT_TOLL_INFO;
import com.ne.gs.services.custom.ChangeColorService;
import com.ne.gs.services.item.ItemPacketService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author KID
 * 
 */
public class cmd_dye extends ChatCommand {

	@Override
	protected void runImpl(@NotNull final Player player, @NotNull String alias, @NotNull String... params) throws Exception {
		if (!CustomConfig.DYE_REPAINT) {
			player.sendMsg("Команда отключена.");
			return;
		}

		if (params.length == 0) {
			player.sendMsg("Синтаксис команды .paint КодЦвета");
			return;
		}

		ItemSlot[] slots = new ItemSlot[] {ItemSlot.TORSO, ItemSlot.GLOVES, ItemSlot.BOOTS, ItemSlot.PANTS, ItemSlot.SHOULDER, ItemSlot.HELMET};
		final List<Item> items = new ArrayList<>(0);
		for(ItemSlot slot: slots) {
			Item item = player.getEquipment().getItemBySlot(slot);
			if(item != null)
				items.add(item);
		}
		
		if(items.isEmpty()) {
			player.sendMsg("Нет предметов для покраски.");
			return;
		}
		
		final int total = items.size() * CustomConfig.DYE_REPAINT_COST;
		String color = params[0];
		int rgb;
		int bgra = 0;
		String retcolor = ChangeColorService.getInstance().colors.get(color);
		if (retcolor != null)
			color = retcolor;

		try {
			rgb = Integer.parseInt(color, 16);
			bgra = 0xFF | (rgb & 0xFF) << 24 | (rgb & 0xFF00) << 8 | (rgb & 0xFF0000) >>> 8;
		} catch (NumberFormatException e) {
			if (!color.equalsIgnoreCase("no")) {
				player.sendMsg("[Dye] Неправильный цвет: " + color);
				return;
			}
		}

		try {
			rgb = Integer.parseInt(color, 16);
			bgra = 0xFF | (rgb & 0xFF) << 24 | (rgb & 0xFF00) << 8 | (rgb & 0xFF0000) >>> 8;
		} catch (NumberFormatException e) {
			if (!color.equalsIgnoreCase("no")) {
				player.sendMsg("[Dye] Неправильный цвет: " + color);
				return;
			}
		}

		final int targetColor = bgra;
		final String targetColorStr = color;
		InGameShopEn.getInstance().querryToll(player, new InGameShopEn.TollQuerry() {
			@Override
			public Object onEvent(@NotNull InGameShopEn.TollQuerryResult env) {
				long playerToll = env.toll;
				if (playerToll >= total) {
					long toll = playerToll - total;
					if (LoginServer.getInstance().sendPacket(new SM_ACCOUNT_TOLL_INFO(toll, player.getAcountName()))) {
						player.sendPck(new SM_TOLL_INFO(toll));
					}
				} else {
					player.sendMsg("У вас недостаточно донат монет для покраски.");
					return null;
				}
				for (Item item : items) {
					if (targetColorStr.equals("no")) {
						item.setItemColor(0);
					} else {
						item.setItemColor(targetColor);
					}
					ItemPacketService.updateItemAfterInfoChange(player, item);
				}

				PacketSendUtility.broadcastPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), items), true);
				player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
				player.sendMsg("Покраска в " + targetColorStr + " завершена!");
				com.ne.gs.services.teleport.TeleportService.teleportTo(player, player.getWorldId(), player.getInstanceId(), player.getX(), player.getY(), player.getZ());
                return null;
			}
		});

	}
}
