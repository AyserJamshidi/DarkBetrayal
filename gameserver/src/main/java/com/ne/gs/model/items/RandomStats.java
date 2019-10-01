package com.ne.gs.model.items;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.bonus.StatBonusType;

public class RandomStats {

    private final RandomBonusEffect rndBonusEffect;

    public RandomStats(int setId, int setNumber) {
        rndBonusEffect = new RandomBonusEffect(StatBonusType.INVENTORY, setId, setNumber);
    }

    public void onEquip(final Player player) {
        rndBonusEffect.applyEffect(player);
    }

    public void onUnEquip(Player player) {
        rndBonusEffect.endEffect(player);
    }
}
