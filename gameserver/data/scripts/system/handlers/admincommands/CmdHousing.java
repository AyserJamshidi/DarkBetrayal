/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.Map;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import com.ne.commons.DateUtil;
import com.ne.commons.annotations.NotNull;
import com.ne.gs.database.GDB;
import com.ne.gs.database.dao.PlayerDAO;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.PlayerCommonData;
import com.ne.gs.modules.housing.House;
import com.ne.gs.modules.housing.HouseAuction;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.utils.chathandlers.ChatCommand;

import static com.ne.gs.modules.housing.HouseAuction.*;
import static com.ne.gs.modules.housing.Housing.*;

/**
 * @author hex1r0
 */
public class CmdHousing extends ChatCommand {

    private static final String[] SYNTAX = {
        "--- Syntax ",
        "<info|chi|goto|give|take|auction>",
        "  info <target|player id|player name>",
        "  chi",
        "  goto <house id>",
        "  give <house id> <target|player id>",
        "  take <house id> <target|player id>",
        "  auction <reg|unreg|info|setup|shutdown|process|truncate>",
        "   reg <id> [price]",
        "   unreg <id> [price]",
        "--- Help ",
        "  info     - show information about player houses",
        "  chi      - show information about closest to player house",
        "  goto     - teleport to house",
        "  give     - give house",
        "  take     - take house",
        "  auction  - auction commands",
        "    reg      - register house in auction",
        "    unreg    - unregister house in auction",
        "    info     - show general auction information",
        "    setup    - create lots and start auction",
        "    shutdown - shutdown auction & remove all bids (data in GDB is unchanged)",
        "    process  - calculate winners & loosers",
        "    truncate - removes lot data from GDB & shutdown auction",
    };

    @Override
    protected void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) throws Exception {
        String cmd = params[0].toLowerCase();

        if (cmd.startsWith("go") || cmd.startsWith("gi")
            || cmd.startsWith("t") || cmd.startsWith("c") || cmd.startsWith("i")) {
            handle(player, cmd, params);
        } else if (cmd.startsWith("a")) {
            handleAuction(player, params[1].toLowerCase(), params);
        }
    }

    private void handle(final Player player, String cmd, String... params) {
        if (cmd.startsWith("c")) {
            float x = player.getX(), y = player.getY(), z = player.getZ();

            House.HouseTemplate template = null;

            double distance = Double.MAX_VALUE;
            for (House.HouseTemplate tpl : HOUSE_TEMPLATES.values()) {
                if (tpl.getMapId() != player.getWorldId()) {
                    continue;
                }

                double d = MathUtil.getDistance(x, y, z, tpl.getX(), tpl.getY(), tpl.getZ());
                if (d < distance) {
                    distance = d;
                    template = tpl;
                }
            }

            if (template == null) {
                player.sendMsg("Unable to find house here!");
                return;
            }

            String format = "houseId: %d";
            player.sendMsg(String.format(format, template.getHouseId()));
            return;
        }

        if (cmd.startsWith("i")) {
            final Integer targetPlayerUid;

            if (params.length > 2) {
                targetPlayerUid = parsePlayerUid(params[1]);
            } else {
                VisibleObject target = player.getTarget();
                if (target != null && target instanceof Player) {
                    targetPlayerUid = target.getObjectId();
                } else {
                    throw new RuntimeException("Player not found!");
                }
            }

            housing().tell(new Runnable() {
                @Override
                public void run() {
                    Iterable<String> it = Iterables.transform(housing().getPlayerHouses(targetPlayerUid),
                        new Function<House, String>() {
                            @Override
                            public String apply(House house) {
                                return String.format("HouseId: %d, state: %s", house.getHouseId(), house.getState());
                            }
                        });

                    player.sendMsg("--- [Player info]");
                    for (String s : it) {
                        player.sendMsg(s);
                    }
                    player.sendMsg("---");
                }
            });
            return;
        }

        int houseId = Integer.parseInt(params[1]);
        House.HouseTemplate tpl = HOUSE_TEMPLATES.get(houseId);
        if (tpl == null) {
            throw new RuntimeException("Wrong house id!");
        }

        if (cmd.startsWith("go")) { // goto
            float x = (tpl.getManagerX() + tpl.getPortalX()) / 2;
            float y = (tpl.getManagerY() + tpl.getPortalY()) / 2;
            float z = (tpl.getManagerZ() + tpl.getPortalZ()) / 2;

            TeleportService.teleportBeam(player, tpl.getMapId(), x, y, z);
        } else {
            PlayerCommonData target;
            if (params.length > 2) {
                target = PlayerCommonData.get(Integer.parseInt(params[2]));
            } else {
                target = ((Player) player.getTarget()).getCommonData();
            }

            if (target == null) {
                throw new RuntimeException("Target is not player or playerId does not exist!");
            }

            if (cmd.startsWith("gi")) { // give
                housing().tell(new GiveHouse(target, tpl));
            } else if (cmd.startsWith("t")) { // take
                housing().tell(new TakeHouse(target, tpl));
            }
        }
    }

    private void handleAuction(final Player player, String cmd, String... params) {
        if (cmd.startsWith("r") || cmd.startsWith("u")) { // reg | unreg
            int houseId = Integer.parseInt(params[2]);
            House.HouseTemplate tpl = HOUSE_TEMPLATES.get(houseId);
            if (tpl == null) {
                throw new RuntimeException("Wrong house id!");
            }

            Long price;
            if (params.length > 3) {
                price = Long.parseLong(params[3]);
            } else {
                price = tpl.getPrice();
            }

            if (cmd.startsWith("r")) {
                housing().tell(new PutForSale(tpl, price));
            } else if (cmd.startsWith("u"))  // unreg
            {
                housing().tell(new RemoveFromSale(tpl));
            }
        } else if (cmd.startsWith("i")) {
            housing().tell(new Runnable() {
                @Override
                public void run() {
                    Iterable<String> it = Iterables.transform(auction().getInfo().entrySet(),
                        new Function<Map.Entry<HouseAuction.Info.Arg, String>, String>() {
                            @Override
                            public String apply(Map.Entry<HouseAuction.Info.Arg, String> e) {
                                switch (e.getKey()) {
                                    case TIME:
                                        return "Current Time: " + DateUtil.format("HH:mm:ss", e.getValue());
                                    case STATE:
                                        return "State: " + e.getValue();
                                    case BID_COUNT:
                                        return "Total bid count: " + e.getValue();
                                    case LOT_COUNT:
                                        return "Total lot count: " + e.getValue();
                                    case PROCESS_TIME:
                                        return "Process time: " + DateUtil.format("E dd-M-yyyy", e.getValue());
                                }

                                return "";
                            }
                        });

                    player.sendMsg("--- [Auction info]");
                    for (String s : it) {
                        player.sendMsg(s);
                    }
                    player.sendMsg("---");
                }
            });
        } else if (cmd.startsWith("se")) {
            HouseAuction.schedule(HouseAuction.State.START, 0);
            player.sendMsg("Auction started.");
        } else if (cmd.startsWith("sh")) {
            HouseAuction.schedule(HouseAuction.State.STOPPED, 0);
            player.sendMsg("Auction stopped.");
        } else if (cmd.startsWith("p")) {
            HouseAuction.schedule(HouseAuction.State.PROCESS, 0);
            player.sendMsg("Auction processing.");
        } else if (cmd.startsWith("t")) {
            housing().tell(new Truncate());
            player.sendMsg("Auction truncated.");
        }
    }

    private Integer parsePlayerUid(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            try {
                return GDB.get(PlayerDAO.class).loadPlayerCommonDataByName(input).getPlayerObjId();
            } catch (Exception e2) {
                throw new RuntimeException("Player not found!");
            }
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        for (String s : SYNTAX) {
            player.sendMsg(s);
        }

        player.sendMsg("Error:" + e.getMessage());
    }
}
