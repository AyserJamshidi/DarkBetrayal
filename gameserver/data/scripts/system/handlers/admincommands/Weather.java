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
import com.ne.gs.services.WeatherService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.WorldMapType;

/**
 * Admin command allowing to change weathers of the world.
 *
 * @author Kwazar
 */
public class Weather extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length == 0 || params.length > 2) {
            onError(admin, null);
            return;
        }

        String regionName;
        int weatherType = -1;

        regionName = params[0];

        if (params.length == 2) {
            try {
                weatherType = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("weather type parameter need to be an integer [0-8].");
                return;
            }
        }

        if (regionName.equals("reset")) {
            WeatherService.getInstance().resetWeather();
            return;
        }

        // Retrieving regionId by name
        WorldMapType region = null;
        for (WorldMapType worldMapType : WorldMapType.values()) {
            if (worldMapType.name().toLowerCase().equals(regionName.toLowerCase())) {
                region = worldMapType;
            }
        }

        if (region != null) {
            if (weatherType > -1 && weatherType < 9) {
                WeatherService.getInstance().changeRegionWeather(region.getId(), weatherType);
            } else {
                admin.sendMsg("Weather type must be between 0 and 8");
            }
        } else {
            admin.sendMsg("Region " + regionName + " not found");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //weather <regionName(poeta, ishalgen, etc ...)> <value(0->8)> OR //weather reset");
    }

}
