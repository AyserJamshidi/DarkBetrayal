/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.drop.Drop;
import com.ne.gs.model.drop.DropGroup;
import com.ne.gs.model.drop.NpcDrop;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.npc.NpcTemplate;
import com.ne.gs.utils.chathandlers.ChatCommand;

public class cmd_droplist extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String command, @NotNull String... params) {
        NpcDrop npcDrop = null;
        if (params.length > 0) {
            int npcId = Integer.parseInt(params[0]);
            NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
            if (npcTemplate == null) {
                player.sendMsg("Неверный ID npc: " + npcId);
                return;
            }
            npcDrop = npcTemplate.getNpcDrop();
        } else {
            VisibleObject visibleObject = player.getTarget();

            if (visibleObject == null) {
                player.sendMsg("Возьмите в таргет нужного вам НПЦ !");
                return;
            }

            if (visibleObject instanceof Npc) {
                npcDrop = ((Npc) visibleObject).getNpcDrop();
            }
        }
        if (npcDrop == null) {
            player.sendMsg("В выбранном нпц нет дропа");
            return;
        }

        int count = 0;
        player.sendMsg("[Дроплист с выбранного НПЦ: " + npcDrop.getNpcId() + "]");
        player.sendMsg("[Шансы указаны с учетом ваших рейтов (без учета усилителей дропа) ]\n");
        for (DropGroup dropGroup : npcDrop.getDropGroup()) {
            player.sendMsg("DropGroup: " + dropGroup.getGroupName());
            for (Drop drop : dropGroup.getDrop()) {
                player.sendMsg("[item:" + drop.getItemId() + "]" + "	Шанс: " + drop.getChance() * player.getRates().getDropRate());
                count++;
            }
        }
        player.sendMsg(count + " предметов доступны из выбранного НПЦ");
    }
}
