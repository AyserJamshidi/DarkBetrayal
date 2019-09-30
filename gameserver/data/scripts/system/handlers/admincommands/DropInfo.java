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
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.drop.DropGroup;
import com.ne.gs.model.drop.NpcDrop;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.npc.NpcTemplate;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Oliver
 */
public class DropInfo extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        NpcDrop npcDrop = null;
        if (params.length > 0) {
            int npcId = Integer.parseInt(params[0]);
            NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
            if (npcTemplate == null) {
                player.sendMsg("Incorrect npcId: " + npcId);
                return;
            }
            npcDrop = npcTemplate.getNpcDrop();
        } else {
            VisibleObject visibleObject = player.getTarget();

            if (visibleObject == null) {
                player.sendMsg("You should target some NPC first !");
                return;
            }

            if (visibleObject instanceof Npc) {
                npcDrop = ((Npc) visibleObject).getNpcDrop();
            }
        }
        if (npcDrop == null) {
            player.sendMsg("No drops for the selected NPC");
            return;
        }

        int count = 0;
        player.sendMsg("[Drop Info for the specified NPC]\n");
        for (DropGroup dropGroup : npcDrop.getDropGroup()) {
            player.sendMsg("DropGroup: " + dropGroup.getGroupName());
            for (com.ne.gs.model.drop.Drop drop : dropGroup.getDrop()) {
                player.sendMsg("[item:" + drop.getItemId() + "]" + "	Rate: " + drop.getChance());
                count++;
            }
        }
        player.sendMsg(count + " drops available for the selected NPC");
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub
    }
}
