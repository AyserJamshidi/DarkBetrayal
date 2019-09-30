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
import com.ne.gs.cache.HTMLCache;
import com.ne.gs.configs.main.WeddingsConfig;
import com.ne.gs.model.Gender;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.ingameshop.InGameShopEn;
import com.ne.gs.services.HTMLService;
import com.ne.gs.services.WeddingService;
import com.ne.gs.services.abyss.AbyssPointsService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author Tiger
 */
public class cmd_marry extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String command, @NotNull String... params) {

        if (!WeddingsConfig.WEDDINGS_ENABLE) {
            player.sendMsg("Свадьбы отключены.");
            return;
        }

        if (params.length != 1) {
            player.sendMsg("Команда введена не верно: .marry <characterName>");
            return;
        }

        Player partner1 = player;
        Player partner2 = World.getInstance().findPlayer(params[0]);
        if (partner1 == null || partner2 == null) {
            player.sendMsg("Персонаж " + partner2 + " не найден.");
            return;
        }
        if (partner1.equals(partner2)) {
            player.sendMsg("Вы не можете стать супругом самому себе.");
            return;
        }
        if (partner1.getWorldId() == 510010000 || partner1.getWorldId() == 520010000 || partner2.getWorldId() == 510010000
                || partner2.getWorldId() == 520010000) {
            player.sendMsg("Один из персонажей находится в тюрьме.");
            return;
        }

        partner1.sendMsg("Согласны ли вы, взять в " + (partner2.getGender() == Gender.FEMALE ? "жены " : "мужъя ") + partner2.getName() + "?");
        HTMLService.showHTML(partner1, HTMLCache.getInstance().getHTML("weddings.xhtml"));
        partner2.sendMsg("Согласны ли вы, взять в " + (partner1.getGender() == Gender.FEMALE ? "жены " : "мужъя ") + partner1.getName() + "?");
        HTMLService.showHTML(partner2, HTMLCache.getInstance().getHTML("weddings.xhtml"));

        WeddingService.getInstance().registerOffer(partner1, partner2, player);
    }
}
