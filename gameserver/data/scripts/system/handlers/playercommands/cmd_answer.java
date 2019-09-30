/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.Wedding;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.WeddingService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author synchro2
 */
public class cmd_answer extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String command, @NotNull String... params) {
        Wedding wedding = WeddingService.getInstance().getWedding(player);

        if (params == null || params.length != 1) {
            player.sendMsg("syntax .answer yes/no.");
            return;
        }

        if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
			player.sendMsg("Этой командой нельзя пользоваться в тюрьме.");            return;
        }

        if (wedding == null) {
            player.sendMsg("Свадьба не началась.");
        }

        if (params[0].toLowerCase().equals("yes")) {
            player.sendMsg("Вы согласились.");
            WeddingService.getInstance().acceptWedding(player);
        }

        if (params[0].toLowerCase().equals("no")) {
            player.sendMsg("Вы отказались.");
            WeddingService.getInstance().cancelWedding(player);
        }

    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax .answer yes/no.");
    }
}
