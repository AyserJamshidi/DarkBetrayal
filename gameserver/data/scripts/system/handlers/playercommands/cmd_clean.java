/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Source
 */
public class cmd_clean extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects.player.Player, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) throws Exception {
        String msg = "syntax .clean <item ID> or <item @link>";

        if (params.length == 0) {
            throw new Exception();
        }

        int itemId = 0;

        try {
            String item = params[0];
            // Some item links have space before Id
            if (item.equals("[item:")) {
                item = params[1];
                Pattern id = Pattern.compile("(\\d{9})");
                Matcher result = id.matcher(item);
                if (result.find()) {
                    itemId = Integer.parseInt(result.group(1));
                }
            } else {
                Pattern id = Pattern.compile("\\[item:(\\d{9})");
                Matcher result = id.matcher(item);

                if (result.find()) {
                    itemId = Integer.parseInt(result.group(1));
                } else {
                    itemId = Integer.parseInt(params[0]);
                }
            }
        } catch (NumberFormatException e) {
            try {
                String item = params[1];
                // Some item links have space before Id
                if (item.equals("[item:")) {
                    item = params[2];
                    Pattern id = Pattern.compile("(\\d{9})");
                    Matcher result = id.matcher(item);
                    if (result.find()) {
                        itemId = Integer.parseInt(result.group(1));
                    }
                } else {
                    Pattern id = Pattern.compile("\\[item:(\\d{9})");
                    Matcher result = id.matcher(item);

                    if (result.find()) {
                        itemId = Integer.parseInt(result.group(1));
                    } else {
                        itemId = Integer.parseInt(params[1]);
                    }
                }
            } catch (NumberFormatException ex) {
                player.sendMsg("Вы должны использовать ID или ссылку на вещь.");
                return;
            } catch (Exception ex2) {
                throw new Exception(msg);
            }
        }

        Storage bag = player.getInventory();
        Item item = bag.getFirstItemByItemId(itemId);
        if (item != null || itemId == 0) {
            bag.decreaseByObjectId(item.getObjectId(), 1);
            player.sendMsg("Предмет успешно удален");
        } else {
            player.sendMsg("У вас нет указанного предмета");
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#onError(com.ne.gs.model.gameobjects.player.Player, java.lang.Exception)
     */
    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg(e.getMessage());
    }
}
