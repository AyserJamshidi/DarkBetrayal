/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import com.ne.gs.database.GDB;
import org.apache.commons.lang3.StringUtils;

import com.ne.commons.Util;
import com.ne.commons.annotations.NotNull;
import com.ne.gs.database.dao.InGameShopDAO;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.ingameshop.InGameShopEn;
import com.ne.gs.model.templates.item.ItemTemplate;
import com.ne.gs.network.aion.serverpackets.SM_TOLL_INFO;
import com.ne.gs.network.loginserver.LoginServer;
import com.ne.gs.network.loginserver.serverpackets.SM_ACCOUNT_TOLL_INFO;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.utils.idfactory.IDFactory;
import com.ne.gs.world.World;

/**
 * @author xTz
 */
public class Gameshop extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects.player.Player, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) throws Exception {
        if (params.length == 0) {
            throw new Exception();
        }
        int itemId = 0;
        int list;
        byte category, subCategory, itemType, gift;
        long count, price, toll;
        Player player = null;
        String titleDescription;
        if ("delete".startsWith(params[0])) {
            try {
                itemId = Integer.parseInt(params[1]);
                category = Byte.parseByte(params[2]);
                subCategory = Byte.parseByte(params[3]);
                list = Integer.parseInt(params[4]);
            } catch (NumberFormatException e) {
                admin.sendMsg("<itemId, category, subCategory, list> values must be int, byte, byte, int.");
                return;
            }
            GDB.get(InGameShopDAO.class).deleteIngameShopItem(itemId, category, subCategory, list - 1);
            admin.sendMsg("You remove [item:" + itemId + "]");
        } else if ("add".startsWith(params[0])) {
            try {
                itemId = Integer.parseInt(params[1]);
                count = Long.parseLong(params[2]);
                price = Long.parseLong(params[3]);
                category = Byte.parseByte(params[4]);
                subCategory = Byte.parseByte(params[5]);
                itemType = Byte.parseByte(params[6]);
                gift = Byte.parseByte(params[7]);
                list = Integer.parseInt(params[8]);
                titleDescription = Util.convertName(params[9]);
            } catch (NumberFormatException e) {
                admin.sendMsg("<itemId, count, price, category, subCategory, itemType, gift, list, description> values must be int, long, long, byte, byte, byte, byte, int, string, Object... .");
                return;
            }

            ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
            if (itemTemplate == null) {
                admin.sendMsg("Item id is incorrect: " + itemId);
                return;
            }

            String description = "";

            for (int i = 10; i < params.length; i++) {
                description += Util.convertName(params[i]) + " ";
            }
            description = description.trim();

            if (list < 1) {
                admin.sendMsg("<list> : minium is 1.");
                return;
            }
            if (gift < 0 || gift > 1) {
                admin.sendMsg("<gift> : minimum is 0, maximum is 1.");
                return;
            }
            if (itemType < 0 || itemType > 2) {
                admin.sendMsg("<itemType> : minimum is 0, maximum is 2.");
                return;
            }
            if (subCategory < 3 || subCategory > 19) {
                admin.sendMsg("<category> : minimum is 3, maximum is 19.");
                return;
            }
            if (titleDescription.length() > 20) {
                admin.sendMsg("<title description> : maximum length is 20.");
                return;
            }
            if (titleDescription.equals("empty")) {
                titleDescription = StringUtils.EMPTY;
            }
            GDB.get(InGameShopDAO.class).saveIngameShopItem(IDFactory.getInstance().nextId(), itemId, count, price, category, subCategory, list - 1,
                1, itemType, gift, titleDescription, description);
            admin.sendMsg("You add [item:" + itemId + "]");
        } else if ("deleteranking".startsWith(params[0])) {
            try {
                itemId = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("<itemId> value must be an integer.");
            }
            GDB.get(InGameShopDAO.class).deleteIngameShopItem(itemId, (byte) -1, (byte) -1, -1);
            admin.sendMsg("You remove from Ranking Sales [item:" + itemId + "]");
        } else if ("addranking".startsWith(params[0])) {
            try {
                itemId = Integer.parseInt(params[1]);
                count = Long.parseLong(params[2]);
                price = Long.parseLong(params[3]);
                itemType = Byte.parseByte(params[4]);
                gift = Byte.parseByte(params[5]);
                titleDescription = Util.convertName(params[6]);
            } catch (NumberFormatException e) {
                admin.sendMsg("<itemId, count, price, itemType, gift, description> value must be int, long, long, byte, byte, string, Object... .");
                return;
            }
            String description = "";
            for (int i = 7; i < params.length; i++) {
                description += Util.convertName(params[i]) + " ";
            }
            description = description.trim();

            ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);

            if (itemTemplate == null) {
                admin.sendMsg("Item id is incorrect: " + itemId);
                return;
            }
            if (titleDescription.equals("empty")) {
                titleDescription = StringUtils.EMPTY;
            }
            GDB.get(InGameShopDAO.class).saveIngameShopItem(IDFactory.getInstance().nextId(), itemId, count, price, (byte) -1, (byte) -1, -1, 0,
                itemType, gift, titleDescription, description);
            admin.sendMsg("You remove from Ranking Sales [item:" + itemId + "]");
        } else if ("settoll".startsWith(params[0])) {
            if (params.length == 3) {
                try {
                    toll = Integer.parseInt(params[2]);
                } catch (NumberFormatException e) {
                    admin.sendMsg("<toll> value must be an integer.");
                    return;
                }

                String name = params[1];

                player = World.getInstance().findPlayer(name);
                if (player == null) {
                    admin.sendMsg("The specified player is not online.");
                    return;
                }

                if (LoginServer.getInstance().sendPacket(new SM_ACCOUNT_TOLL_INFO(toll, player.getAcountName()))) {
                    player.sendPck(new SM_TOLL_INFO(toll));
                    admin.sendMsg("Tolls setted to " + toll + ".");
                } else {
                    admin.sendMsg("ls communication error.");
                }
            }
            if (params.length == 2) {
                try {
                    toll = Integer.parseInt(params[1]);
                } catch (NumberFormatException e) {
                    admin.sendMsg("<toll> value must be an integer.");
                    return;
                }

                if (toll < 0) {
                    admin.sendMsg("<toll> must > 0.");
                    return;
                }

                VisibleObject target = admin.getTarget();
                if (target == null) {
                    admin.sendMsg("You should select a target first!");
                    return;
                }

                if (target instanceof Player) {
                    player = (Player) target;
                }

                if (LoginServer.getInstance().sendPacket(new SM_ACCOUNT_TOLL_INFO(toll, player.getAcountName()))) {
                    player.sendPck(new SM_TOLL_INFO(toll));
                    admin.sendMsg("Tolls setted to " + toll + ".");
                } else {
                    admin.sendMsg("ls communication error.");
                }
            }
        } else if ("addtoll".startsWith(params[0])) {
            if (params.length == 3) {
                try {
                    toll = Integer.parseInt(params[2]);
                } catch (NumberFormatException e) {
                    admin.sendMsg("<toll> value must be an integer.");
                    return;
                }

                if (toll < 0) {
                    admin.sendMsg("<toll> must > 0.");
                    return;
                }

                String name = params[1];

                player = World.getInstance().findPlayer(name);
                if (player == null) {
                    admin.sendMsg("The specified player is not online.");
                    return;
                }

                admin.sendMsg("You added " + toll + " tolls to Player: " + name);
                InGameShopEn.getInstance().addToll(player, toll);
            }
            if (params.length == 2) {
                try {
                    toll = Integer.parseInt(params[1]);
                } catch (NumberFormatException e) {
                    admin.sendMsg("<toll> value must be an integer.");
                    return;
                }

                VisibleObject target = admin.getTarget();
                if (target == null) {
                    admin.sendMsg("You should select a target first!");
                    return;
                }

                if (target instanceof Player) {
                    player = (Player) target;
                }

                admin.sendMsg("You added " + toll + " tolls to Player: " + player.getName());
                InGameShopEn.getInstance().addToll(player, toll);
            }
        } else {
            admin.sendMsg("You can use only, addtoll, settoll, deleteranking, addranking, delete or add.");
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#onError(com.ne.gs.model.gameobjects.player.Player, java.lang.Exception)
     */
    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("No parameters detected please use:\n"
            + "//gameshop add <itemId> <count> <price> <category> <subCategory> <itemType> <gift> <list> <title description|empty> <item description|null>\n"
            + "//gameshop delete <itemId> <category> <subCategory> <list>\n"
            + "//gameshop addranking <itemId> <count> <price> <itemType> <gift> <title description|empty> <item description|null>\n"
            + "//gameshop deleteranking <itemId>\n" + "//gameshop settoll <target|player> <toll>\n"
            + "//gameshop addtoll <target|player> <toll>");
    }
}
