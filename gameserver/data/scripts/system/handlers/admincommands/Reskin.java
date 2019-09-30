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
import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.ingameshop.InGameShopEn;
import com.ne.gs.network.aion.serverpackets.SM_TOLL_INFO;
import com.ne.gs.network.loginserver.LoginServer;
import com.ne.gs.network.loginserver.serverpackets.SM_ACCOUNT_TOLL_INFO;
import com.ne.gs.utils.chathandlers.ChatCommand;

import java.util.Iterator;
import java.util.List;

/**
 * @author Wakizashi, Imaginary, Alexsis (Added Toll payment)
 */
public class Reskin extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects.player.Player, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull final Player admin, @NotNull String alias, @NotNull String... params) throws Exception {
        if (params.length != 2) {
            throw new Exception();
        }

        Player target = admin;
        VisibleObject creature = admin.getTarget();
        if (admin.getTarget() instanceof Player) {
            target = (Player) creature;
        }

        int oldItemId;
        final int newItemId;
        try {
            oldItemId = Integer.parseInt(params[0]);
            newItemId = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            admin.sendMsg("<" +
                    "старый item ID> & <новый item ID> must be an integer.");
            return;
        }

        List<Item> items = target.getInventory().getItemsByItemId(oldItemId);
        List<Item> newitems = target.getInventory().getItemsByItemId(newItemId);
        if (items.size() == 0 || newitems.size() == 0) {
            admin.sendMsg("Предмет должен находится в инвентаре!");
            return;
        }

        final Iterator<Item> iter = items.iterator();
        final Item item = iter.next();

        final int total = CustomConfig.RESKIN_COST;
        InGameShopEn.getInstance().querryToll(admin, new InGameShopEn.TollQuerry() {
            @Override
            public Object onEvent(@NotNull InGameShopEn.TollQuerryResult env) {
                Player target = admin;
                VisibleObject creature = admin.getTarget();
                if (admin.getTarget() instanceof Player) {
                    target = (Player) creature;
                }
                long playerToll = env.toll;
                if (playerToll >= total) {
                    long toll = playerToll - total;
                    if (LoginServer.getInstance().sendPacket(new SM_ACCOUNT_TOLL_INFO(toll, admin.getAcountName()))) {
                        admin.sendPck(new SM_TOLL_INFO(toll));
                    }
                } else {
                    admin.sendMsg("У вас недостаточно кредитов!");
                    return null;
                }
                item.setItemSkinTemplate(DataManager.ITEM_DATA.getItemTemplate(newItemId));
                //Удаляем предмет с которого брали скин=)
                target.getInventory().decreaseByItemId(newItemId, 1);
                admin.sendMsg("Успешно!");
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#onError(com.ne.gs.model.gameobjects.player.Player, java.lang.Exception)
     */
    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("синтакс //reskin <Old Item ID> <New Item ID>");
    }

}
