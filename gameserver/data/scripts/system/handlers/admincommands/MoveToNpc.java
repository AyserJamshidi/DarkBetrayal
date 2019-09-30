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
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.npc.NpcTemplate;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author MrPoke, lord_rex and ginho1
 */
public class MoveToNpc extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        int npcId = 0;
        String message = "";
        try {
            npcId = Integer.valueOf(params[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            onError(player, e);
        } catch (NumberFormatException e) {
            String npcName = "";

            for (String param : params) {
                npcName += param + " ";
            }
            npcName = npcName.substring(0, npcName.length() - 1);

            for (NpcTemplate template : DataManager.NPC_DATA.getNpcData().valueCollection()) {
                if (template.getName().equalsIgnoreCase(npcName)) {
                    if (npcId == 0) {
                        npcId = template.getTemplateId();
                    } else {
                        if (message.equals("")) {
                            message += "Found others (" + npcName + "): \n";
                        }
                        message += "Id: " + template.getTemplateId() + "\n";
                    }
                }
            }
            if (npcId == 0) {
                player.sendMsg("NPC " + npcName + " cannot be found");
            }
        }

        if (npcId > 0) {
            message = "Teleporting to Npc: " + npcId + "\n" + message;
            player.sendMsg(message);
            TeleportService.teleportToNpc(player, npcId);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //movetonpc <npc_id|npc name>");
    }
}
