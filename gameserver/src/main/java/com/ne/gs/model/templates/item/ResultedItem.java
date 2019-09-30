/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model.templates.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.slf4j.LoggerFactory;

import com.ne.commons.utils.Rnd;
import com.ne.gs.model.PlayerClass;
import com.ne.gs.model.Race;

/**
 * @author antness
 */
@XmlType(name = "ResultedItem")
public class ResultedItem {

    @XmlAttribute(name = "id")
    public int itemId;
    @XmlAttribute(name = "count")
    public int count;
    @XmlAttribute(name = "rnd_min")
    public int rndMin;
    @XmlAttribute(name = "rnd_max")
    public int rndMax;
    @XmlAttribute(name = "race")
    public Race race = Race.PC_ALL;

    @XmlAttribute(name = "player_class")
    public PlayerClass playerClass = PlayerClass.ALL;

    public int getItemId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }

    public int getRndMin() {
        return rndMin;
    }

    public int getRndMax() {
        return rndMax;
    }

    public final Race getRace() {
        return race;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public final int getResultCount() {
        if (count == 0 && rndMin == 0 && rndMax == 0) {
            return 1;
        } else if (rndMin > 0 || rndMax > 0) {
            if (rndMax < rndMin) {
                LoggerFactory.getLogger(ResultedItem.class).warn("Wronte rnd result item definition {} {}", rndMin, rndMax);
                return 1;
            } else {
                return Rnd.get(rndMin, rndMax);
            }
        } else {
            return count;
        }
    }
}