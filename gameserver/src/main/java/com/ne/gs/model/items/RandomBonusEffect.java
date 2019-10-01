package com.ne.gs.model.items;

import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.stats.calc.StatOwner;
import com.ne.gs.model.templates.bonus.StatBonusType;
import com.ne.gs.model.templates.stats.ModifiersTemplate;

public class RandomBonusEffect implements StatOwner {

    private final ModifiersTemplate template;

    public RandomBonusEffect(StatBonusType type, int polishSetId, int polishNumber) {
        template = DataManager.ITEM_RANDOM_BONUSES.getTemplate(type, polishSetId, polishNumber);
    }

    public void applyEffect(Player player) {
        player.getGameStats().addEffect(this, template.getModifiers());
    }

    public void endEffect(Player player) {
        player.getGameStats().endEffect(this);
    }
}
