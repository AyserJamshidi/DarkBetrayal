/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.dataholders;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

import com.ne.gs.model.items.RandomBonusResult;
import com.ne.gs.model.templates.bonus.StatBonusType;
import gnu.trove.map.hash.TIntObjectHashMap;

import com.ne.commons.utils.Rnd;
import com.ne.gs.model.templates.item.bonuses.RandomBonus;
import com.ne.gs.model.templates.stats.ModifiersTemplate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"randomBonuses"})
@XmlRootElement(name = "random_bonuses")
public class ItemRandomBonusData {

    @XmlElement(name = "random_bonus", required = true)
    protected List<RandomBonus> randomBonuses;

    @XmlTransient
    private TIntObjectHashMap<RandomBonus> inventoryRandomBonusData = new TIntObjectHashMap<>();

    @XmlTransient
    private final TIntObjectHashMap<RandomBonus> randomBonusData = new TIntObjectHashMap<>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (RandomBonus bonus : randomBonuses) {
            randomBonusData.put(bonus.getId(), bonus);
        }
        randomBonuses = null;
    }

    private TIntObjectHashMap<RandomBonus> getBonusMap(StatBonusType bonusType) {
        if (bonusType == StatBonusType.INVENTORY)
            return inventoryRandomBonusData;
        return randomBonusData;
    }

    /**
     * Gets a randomly chosen modifiers from bonus list.
     *
     * @return null if not a chance
     */
    public RandomBonusResult getRandomModifiers(StatBonusType bonusType, int rndOptionSet) {
        RandomBonus bonus = getBonusMap(bonusType).get(rndOptionSet);
        if (bonus == null)
            return null;

        List<ModifiersTemplate> modifiersGroup = bonus.getModifiers();

        int chance = Rnd.get(10000);
        int current = 0;
        ModifiersTemplate template = null;
        int number = 0;

        for (int i = 0; i < modifiersGroup.size(); i++) {
            ModifiersTemplate modifiers = modifiersGroup.get(i);

            current += modifiers.getChance() * 100;
            if (current >= chance) {
                template = modifiers;
                number = i + 1;
                break;
            }
        }
        return template == null ? null : new RandomBonusResult(template, number);
    }

    /*public ModifiersTemplate getRandomModifiers(int rndOptionSet) {
        RandomBonus bonus = randomBonusData.get(rndOptionSet);
        if (bonus == null) {
            return null;
        }
        List<ModifiersTemplate> modifiersGroup = bonus.getModifiers();

        int chance = Rnd.get(10000);
        int current = 0;
        ModifiersTemplate template = null;
        for (ModifiersTemplate modifiers : modifiersGroup) {
            current = (int) (current + modifiers.getChance() * 100);
            if (current >= chance) {
                template = modifiers;
                break;
            }
        }
        return template;
    }*/

    public ModifiersTemplate getTemplate(StatBonusType bonusType, int rndOptionSet, int number) {
        RandomBonus bonus = getBonusMap(bonusType).get(rndOptionSet);
        if (bonus == null)
            return null;
        return bonus.getModifiers().get(number - 1);
    }

    public int size() {
        return randomBonusData.size();
    }
}
