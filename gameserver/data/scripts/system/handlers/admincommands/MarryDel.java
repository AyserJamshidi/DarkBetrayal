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
import com.ne.gs.services.WeddingService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author SheppeR
 */
public class MarryDel extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length != 2) {
            admin.sendMsg("syntax //marrydel <characterName> <characterName>");
            return;
        }

        Player partner1 = World.getInstance().findPlayer(params[0]);
        Player partner2 = World.getInstance().findPlayer(params[1]);

        if (partner1 == null || partner2 == null) {
            admin.sendMsg("The specified player is not online.");
            return;
        }
        if (partner1.equals(partner2)) {
            admin.sendMsg("You can't cancel marry player on himself.");
            return;
        }
        if (partner1.getWorldId() == 510010000 || partner1.getWorldId() == 520010000 || partner2.getWorldId() == 510010000
            || partner2.getWorldId() == 520010000) {
            admin.sendMsg("One of the players is in prison.");
            return;
        }

        WeddingService.getInstance().unDoWedding(partner1, partner2);
        admin.sendMsg("Married canceled.");
    }

    @Override
    public void onError(Player admin, Exception e) {
        admin.sendMsg("syntax //marrydel <characterName> <characterName>");
    }
}
