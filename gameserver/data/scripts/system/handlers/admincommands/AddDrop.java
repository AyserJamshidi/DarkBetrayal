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
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author ATracer
 */
public class AddDrop extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        player.sendMsg("Now this is not implemented.");
    /*
         * if (params.length != 5) {
		 * onError(player, null);
		 * return;
		 * }
		 * try {
		 * final int mobId = Integer.parseInt(params[0]);
		 * final int itemId = Integer.parseInt(params[1]);
		 * final int min = Integer.parseInt(params[2]);
		 * final int max = Integer.parseInt(params[3]);
		 * final float chance = Float.parseFloat(params[4]);
		 * DropList dropList = DropRegistration.getInstance().getDropList();
		 * DropTemplate dropTemplate = new DropTemplate(mobId, itemId, min, max, chance, false);
		 * dropList.addDropTemplate(mobId, dropTemplate);
		 * DB.insertUpdate("INSERT INTO droplist (" + "`mob_id`, `item_id`, `min`, `max`, `chance`)" + " VALUES "
		 * + "(?, ?, ?, ?, ?)", new IUStH() {
		 * @Override
		 * public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
		 * ps.setInt(1, mobId);
		 * ps.setInt(2, itemId);
		 * ps.setInt(3, min);
		 * ps.setInt(4, max);
		 * ps.setFloat(5, chance);
		 * ps.execute();
		 * }
		 * });
		 * }
		 * catch (Exception ex) {
		 * PacketSendUtility.sendMessage(player, "Only numbers are allowed");
		 * return;
		 * }
		 */
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //adddrop <mobid> <itemid> <min> <max> <chance>");
    }
}
