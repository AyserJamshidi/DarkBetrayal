/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.configs.main.GeoDataConfig;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import mw.engines.geo.GeoEngine;
import mw.engines.geo.GeoHelper;

/**
 * @author Source
 */
public class Warp extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        String locS, first, last;
        float xF, yF, zF;
        locS = "";
        int mapL = 0;
        int layerI = -1;

        first = params[0];
        xF = Float.parseFloat(params[1]);
        yF = Float.parseFloat(params[2]);
        //zF = Float.parseFloat(params[3]);
        last = params[4];

        Pattern f = Pattern.compile("\\[pos:([^;]+);\\s*+(\\d{9})");
        Pattern l = Pattern.compile("(\\d)\\]");
        Matcher fm = f.matcher(first);
        Matcher lm = l.matcher(last);

        if (fm.find()) {
            locS = fm.group(1);
            mapL = Integer.parseInt(fm.group(2));
        }
        if (lm.find()) {
            layerI = Integer.parseInt(lm.group(1));
        }

        zF = GeoEngine.getZ(mapL, 0, xF, yF);
        player.sendMsg("MapId (" + mapL + ")\n" + "x:" + xF + " y:" + yF + " z:" + zF + " l(" + layerI + ")");

        if (mapL == 400010000) {
            player.sendMsg("Sorry you can't warp at abyss");
        } else {
            TeleportService.teleportTo(player, mapL, xF, yF, zF);
            player.sendMsg("You have successfully warp -> " + locS);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //warp <@link>");
    }

}
