/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.serverpackets;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.PlayerCommonData;
import com.ne.gs.model.stats.container.PlayerGameStats;
import com.ne.gs.model.stats.container.PlayerLifeStats;
import com.ne.gs.model.stats.container.StatEnum;
import com.ne.gs.network.aion.AionConnection;
import com.ne.gs.network.aion.AionServerPacket;
import com.ne.gs.utils.gametime.GameTimeManager;

/**
 * In this packet Server is sending User Info?
 *
 * @author -Nemesiss-
 * @author Luno
 */
public class SM_STATS_INFO extends AionServerPacket {

    /**
     * Player that stats info will be send
     */
    private final Player player;
    private final PlayerGameStats pgs;
    private final PlayerLifeStats pls;
    private final PlayerCommonData pcd;

    /**
     * Constructs new <tt>SM_UI</tt> packet
     *
     * @param player
     */
    public SM_STATS_INFO(Player player) {
        this.player = player;
        pcd = player.getCommonData();
        pgs = player.getGameStats();
        pls = player.getLifeStats();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(AionConnection con) {
        writeD(player.getObjectId());// Player ObjectId
        writeD(GameTimeManager.getGameTime().getTime()); // Minutes since 1/1/00 00:00:00

        writeH(pgs.getPower().getCurrent());// [current power]
        writeH(pgs.getHealth().getCurrent());// [current health]
        writeH(pgs.getAccuracy().getCurrent());// [current accuracy]
        writeH(pgs.getAgility().getCurrent());// [current agility]
        writeH(pgs.getKnowledge().getCurrent());// [current knowledge]
        writeH(pgs.getWill().getCurrent());// [current will]

        writeH(pgs.getStat(StatEnum.WATER_RESISTANCE, 0).getCurrent());// [current water]
        writeH(pgs.getStat(StatEnum.WIND_RESISTANCE, 0).getCurrent());// [current wind]
        writeH(pgs.getStat(StatEnum.EARTH_RESISTANCE, 0).getCurrent());// [current earth]
        writeH(pgs.getStat(StatEnum.FIRE_RESISTANCE, 0).getCurrent());// [current fire]
        writeH(pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_LIGHT, 0).getCurrent());// [current light resistance]
        writeH(pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_DARK, 0).getCurrent());// [current dark resistance]

        writeH(player.getLevel()); // [level]

        // something like very dynamic
        writeH(0);// [unk]
        writeH(0);// [unk]
        writeH(0);// [unk]

        writeQ(pcd.getExpNeed());// [xp till next lv]
        writeQ(pcd.getExpRecoverable());// [recoverable exp]
        writeQ(pcd.getExpShown());// [current xp]

        writeD(0);

        writeD(pgs.getMaxHp().getCurrent());// [max hp]
        writeD(pls.getCurrentHp());// [current hp]

        writeD(pgs.getMaxMp().getCurrent());// [max mana]
        writeD(pls.getCurrentMp());// [current mana]

        writeH(pgs.getMaxDp().getCurrent()); // [max dp]
        writeH(pls.getCurrentDp()); // [current dp]

        writeD(pgs.getFlyTime().getCurrent());
        writeD(pls.getCurrentFp());

        writeH(player.getFlyState());// [fly state]

        writeH(pgs.getMainHandPAttack().getCurrent());// [current main hand attack]
        writeH(pgs.getOffHandPAttack().getCurrent());// [off hand attack]

        writeH(0);// unk 3.0

        writeD(pgs.getPDef().getCurrent());// [current pdef]

        // LMFAOOWN fix
        writeH(12345);
        writeH(67890);
        //writeH(pgs.getMainHandMAttack().getCurrent());// [current magic main hand attack]
        //writeH(pgs.getOffHandMAttack().getCurrent()); // [current magic off hand attack]
        writeD(pgs.getMDef().getCurrent()); // [Current magic def]
        writeH(pgs.getMResist().getCurrent());
        writeH(0);// unk 3.0
        writeF(pgs.getAttackRange().getCurrent() / 1000f);// attack range
        writeH(pgs.getAttackSpeed().getCurrent());// attack speed
        writeH(pgs.getEvasion().getCurrent());// [current evasion]
        writeH(pgs.getParry().getCurrent());// [current parry]
        writeH(pgs.getBlock().getCurrent());// [current block]
        writeH(pgs.getMainHandPCritical().getCurrent());// [current main hand crit rate]
        writeH(pgs.getOffHandPCritical().getCurrent());// [current off hand crit rate]
        writeH(pgs.getMainHandPAccuracy().getCurrent());// [current main_hand_accuracy]
        writeH(pgs.getOffHandPAccuracy().getCurrent());// [current off_hand_accuracy]

        // 4.0
        writeH(1);// [unk]
        // 3.0??
        //writeH(0);

        writeH(pgs.getMAccuracy().getCurrent()); // [current magic accuracy]
        writeH(pgs.getMCritical().getCurrent());// [current crit spell]

        writeH(0);

        writeF(pgs.getReverseStat(StatEnum.BOOST_CASTING_TIME, 1000).getCurrent() / 1000f);// [current casting speed]
        writeH(0); // [unk 3.5]
        writeH(pgs.getStat(StatEnum.CONCENTRATION, 0).getCurrent());// [current concetration]
        writeH(pgs.getMBoost().getCurrent());// [current magic boost]
        writeH(pgs.getMBResist().getCurrent());// [current magic suppression]
        writeH(pgs.getStat(StatEnum.HEAL_BOOST, 0).getCurrent());// [current heal_boost]

        writeH(pgs.getPCDef().getCurrent());// [current strike resist]
        writeH(pgs.getStat(StatEnum.MAGICAL_CRITICAL_RESIST, 0).getCurrent());// [current spell resist]

        writeH(pgs.getStat(StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE, 0).getCurrent());// [current strike fortitude]
        writeH(pgs.getStat(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE, 0).getCurrent());// [current spell fortitude]

        writeH(0); // [unk 3.5]

        writeD(player.getInventory().getLimit());
        writeD(player.getInventory().size());
        writeD(0);// [unk]
        writeD(0);// [unk]
        writeD(pcd.getPlayerClass().getClassId());// [Player Class id]

        writeH(0);// unk 3.0
        writeH(0);// unk 3.0
        writeH(0); // [unk 3.5]
        writeH(0); // [unk 3.5]

        writeQ(pcd.getCurrentReposteEnergy());
        writeQ(pcd.getMaxReposteEnergy());
        writeQ(pcd.getCurrentSalvationPercent());

        writeH(0); //4.3 NA
        writeH(0); //4.3 NA
        writeH(1); //4.3 NA
        writeH(0); //4.3 NA

        writeH(pgs.getPower().getBase());// [base power]
        writeH(pgs.getHealth().getBase());// [base health]
        writeH(pgs.getAccuracy().getBase());// [base accuracy]
        writeH(pgs.getAgility().getBase());// [base agility]
        writeH(pgs.getKnowledge().getBase());// [base knowledge]
        writeH(pgs.getWill().getBase());// [base will]
        writeH(pgs.getStat(StatEnum.WATER_RESISTANCE, 0).getBase());// [base water res]
        writeH(pgs.getStat(StatEnum.WIND_RESISTANCE, 0).getBase());// [base water res]
        writeH(pgs.getStat(StatEnum.EARTH_RESISTANCE, 0).getBase());// [base earth resist]
        writeH(pgs.getStat(StatEnum.FIRE_RESISTANCE, 0).getBase());// [base water res]
        writeH(pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_LIGHT, 0).getBase());// [base light resistance]
        writeH(pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_DARK, 0).getBase());// [base dark resistance]
        writeD(pgs.getMaxHp().getBase());// [base hp]
        writeD(pgs.getMaxMp().getBase());// [base mana]
        writeD(pgs.getMaxDp().getBase());// [base dp]
        writeD(pgs.getFlyTime().getBase());// [fly time]
        writeH(pgs.getMainHandPAttack().getBase());// [base main hand attack]
        writeH(pgs.getOffHandPAttack().getBase());// [base off hand attack]
        writeD(pgs.getMAttack().getBase());// [base magic attack]
        writeD(pgs.getPDef().getBase());// [base pdef]
        writeD(pgs.getMDef().getBase());// [base magic def]
        writeD(pgs.getMResist().getBase());// [base magic res]
        writeF(pgs.getAttackRange().getBase() / 1000.0f);// [base attack range]
        writeH(0); // [unk 3.5]
        writeH(pgs.getEvasion().getBase());// [base evasion]
        writeH(pgs.getParry().getBase());// [base parry]
        writeH(pgs.getBlock().getBase());// [base block]
        writeH(pgs.getMainHandPCritical().getBase());// [base main hand crit rate]
        writeH(pgs.getOffHandPCritical().getBase());// [base off hand crit rate]
        writeH(pgs.getMCritical().getBase());// [base magical crit rate]

        writeH(0);// [unk]
        writeH(pgs.getMainHandPAccuracy().getBase());// [base main hand accuracy]
        writeH(pgs.getOffHandPAccuracy().getBase());// [base off hand accuracy]

        // 3.0??
        //writeH(0);
        writeH(pgs.getMAccuracy().getBase());// [base magic main hand accuracy]
        writeH(pgs.getOffHandMAccuracy().getBase());// [base magic off hand accuracy]
        writeH(pgs.getStat(StatEnum.CONCENTRATION, 0).getBase());
        writeH(pgs.getMBoost().getBase());
        writeH(pgs.getMBResist().getBase());
        writeH(pgs.getStat(StatEnum.HEAL_BOOST, 0).getBase());
        writeH(pgs.getPCDef().getBase());
        writeH(pgs.getStat(StatEnum.MAGICAL_CRITICAL_RESIST, 0).getBase());
        writeH(pgs.getStat(StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE, 0).getBase());
        writeH(pgs.getStat(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE, 0).getBase());
        writeH(0);
    }
}
