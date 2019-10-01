package com.ne.gs.model.items;

import com.ne.commons.database.dao.DAOManager;
import com.ne.gs.controllers.observer.ActionObserver;
import com.ne.gs.controllers.observer.ObserverType;
import com.ne.gs.database.dao.ItemStoneListDAO;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.DescriptionId;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.PersistentState;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.bonus.StatBonusType;
import com.ne.gs.model.templates.item.ItemTemplate;
import com.ne.gs.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;

public class IdianStone extends ItemStone {

    private ActionObserver actionListener;
    private int polishCharge;
    private final int polishSetId;
    private final int polishNumber;
    private final Item item;
    private final ItemTemplate template;
    private final int burnDefend;
    private final int burnAttack;
    private final RandomBonusEffect rndBonusEffect;

    public IdianStone(int itemId, PersistentState persistentState, Item item, int polishNumber, int polishCharge) {
        super(item.getObjectId(), itemId, 0, persistentState);
        this.item = item;
        burnDefend = item.getItemTemplate().getIdianAction().getBurnDefend();
        burnAttack = item.getItemTemplate().getIdianAction().getBurnAttack();
        this.polishCharge = polishCharge;
        this.template = DataManager.ITEM_DATA.getItemTemplate(itemId);
        this.polishNumber = polishNumber;

        //LMFAOOWN fix
        polishSetId = 0;
        //polishSetId = template.getActions().getPolishAction().getPolishSetId();
        rndBonusEffect = new RandomBonusEffect(StatBonusType.POLISH, polishSetId, polishNumber);
    }

    public void onEquip(final Player player) {
        if (polishCharge > 0) {
            actionListener = new ActionObserver(ObserverType.ALL) {
                @Override
                public void attacked(Creature creature) {
                    decreasePolishCharge(player, true);
                }

                @Override
                public void attack(Creature creature) {
                    decreasePolishCharge(player, false);
                }

            };
            player.getObserveController().addObserver(actionListener);
            rndBonusEffect.applyEffect(player);
        }
    }

    private synchronized void decreasePolishCharge(Player player, boolean isAttacked) {
        decreasePolishCharge(player, isAttacked, 0);
    }

    public synchronized void decreasePolishCharge(Player player, int skillValue) {
        decreasePolishCharge(player, false, skillValue);
    }

    private synchronized void decreasePolishCharge(Player player, boolean isAttacked, int skillValue) {
        int result = 0;
        if (polishCharge <= 0) {
            return;
        }
        if (skillValue == 0) {
            result = isAttacked ? burnDefend : burnAttack;
        } else {
            result = skillValue;
        }
        if (polishCharge - result < 0) {
            polishCharge = 0;
        } else {
            polishCharge -= result;
        }
        if (polishCharge == 0) {
            onUnEquip(player);
            player.sendPck(new SM_INVENTORY_UPDATE_ITEM(player, item));
            player.sendPck(new SM_SYSTEM_MESSAGE(1401652, new DescriptionId(item.getNameID())));
            item.setIdianStone(null);
            setPersistentState(PersistentState.DELETED);
            DAOManager.getDAO(ItemStoneListDAO.class).storeIdianStones(this);
        }
    }

    public int getPolishNumber() {
        return polishNumber;
    }

    public int getPolishSetId() {
        return polishSetId;
    }

    public int getPolishCharge() {
        return polishCharge;
    }

    public void onUnEquip(Player player) {
        if (actionListener != null) {
            rndBonusEffect.endEffect(player);
            player.getObserveController().removeObserver(actionListener);
            actionListener = null;
        }
    }

}