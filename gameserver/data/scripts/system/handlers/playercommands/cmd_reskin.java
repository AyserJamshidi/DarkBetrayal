package playercommands;



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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexsius on 28.08.2016.
 */
public class cmd_reskin extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects.player.Player, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull final Player admin, @NotNull String alias, @NotNull String... params) throws Exception {

        if(params.length == 4){

            String it1 = params[0] + params[1];
            String it2 = params[2] + params[3];

            params = new String[]{it1,it2};
        }

        if (params.length != 2) {
            throw new Exception();
        }

        Player target = admin;
        VisibleObject creature = admin.getTarget();
        if (admin.getTarget() instanceof Player) {
            target = (Player) creature;
        }

        int oldItemId = 0;
        int newItemId = 0;
        try {
            String item = params[0];
            if (item.equals("[item:")) {
                item = params[1];
                Pattern id = Pattern.compile("(\\d{9})");
                Matcher result = id.matcher(item);
                if (result.find()) {
                    oldItemId = Integer.parseInt(result.group(1));
                }
            } else {
                Pattern id = Pattern.compile("\\[item:(\\d{9})");
                Matcher result = id.matcher(item);

                if (result.find()) {
                    oldItemId = Integer.parseInt(result.group(1));
                } else {
                    oldItemId = Integer.parseInt(params[0]);
                }
            }
            try {
                String items = params[1];
                if (items.equals("[item:")) {
                    items = params[2];
                    Pattern id = Pattern.compile("(\\d{9})");
                    Matcher result = id.matcher(items);
                    if (result.find()) {
                        newItemId = Integer.parseInt(result.group(1));
                    }
                } else {
                    Pattern id = Pattern.compile("\\[item:(\\d{9})");
                    Matcher result = id.matcher(items);

                    if (result.find()) {
                        newItemId = Integer.parseInt(result.group(1));
                    } else {
                        newItemId = Integer.parseInt(params[1]);
                    }
                }
            } catch (NumberFormatException ex) {
                admin.sendMsg("1 " + (admin.isGM() ? ex : ""));
                return;
            } catch (Exception ex2) {
                admin.sendMsg("2 " + (admin.isGM() ? ex2 : ""));
                return;
            }
        } catch (NumberFormatException ex) {
            admin.sendMsg("3 " + (admin.isGM() ? ex : ""));
            return;
        } catch (Exception ex2) {
            admin.sendMsg("4 " + (admin.isGM() ? ex2 : ""));
            return;
        }
        if (DataManager.ITEM_DATA.getItemTemplate(newItemId) == null) {
            admin.sendMsg("Не верный Id предмета: " + newItemId);
            return;
        }
        if (DataManager.ITEM_DATA.getItemTemplate(newItemId).getItemSlot() != DataManager.ITEM_DATA.getItemTemplate(oldItemId).getItemSlot()){
            admin.sendMsg("Предметы должны быть одного слота");
            return;
        }
        if (DataManager.ITEM_DATA.getItemTemplate(newItemId).isWeapon() & DataManager.ITEM_DATA.getItemTemplate(newItemId).getWeaponType() != DataManager.ITEM_DATA.getItemTemplate(oldItemId).getWeaponType()){
            admin.sendMsg("Оружие должно быть одного типа");
            return;
        }
        final int _newitem = newItemId;
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
                    admin.sendMsg("У вас недостаточно кредитов! Стоимость: "+ total);
                    return null;
                }
                item.setItemSkinTemplate(DataManager.ITEM_DATA.getItemTemplate(_newitem));
                //Удаляем предмет с которого брали скин=)
                target.getInventory().decreaseByItemId(_newitem, 1);
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
        player.sendMsg("синтакс .reskin1 <Old Item ID> <New Item ID>");
    }

}

