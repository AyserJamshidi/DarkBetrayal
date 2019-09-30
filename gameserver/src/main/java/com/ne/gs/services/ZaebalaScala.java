package com.ne.gs.services;

import com.ne.gs.configs.main.DredgionConfig;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.autogroup.AutoGroup;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.modules.dredgion.DredgionType;

/**
 *
 * @author Alex
 */
public class ZaebalaScala {

    public static void showAnnounce(int ely, int asmo, Player player, Player pl, DredgionType dredgionType) {
        String dredgion = "";
        String minMaxLvl = "";
        byte id = 0;
        if (dredgionType == DredgionType.BARANATH()) {
            dredgion = "Дерадикон";
            id = 1;

        } else if (dredgionType == DredgionType.CHANTRA()) {
            dredgion = "Дерадикон Джантры";
            id = 2;
        } else if (dredgionType == DredgionType.TERATH()) {
            dredgion = "Дерадикон Садха";
            id = 3;
        }

        String t = "";
        AutoGroup autoGroup = DataManager.AUTO_GROUP.getTemplateByInstaceMaskId(id);
        minMaxLvl = "(" + autoGroup.getMinLvl() + "-" + autoGroup.getMaxLvl() + ")";
        int pSize = DredgionConfig.DREDGION_MIN_TEAM_SIZE;
        if (ely == pSize && asmo == pSize) {
            t = "\nВсе участники собраны. Дерадикон запущен. Проводится набор новых участников";
        }
        pl.sendMsg("Игрок " + player.getName() + " расы " + player.getRace().getRusname()
                + " зарегестрировался на " + dredgion + minMaxLvl + ". Элийцы(" + ely + " из "
                + pSize + ") "
                + "Асмо(" + asmo + " из " + pSize + ")" + t);
    }
}
