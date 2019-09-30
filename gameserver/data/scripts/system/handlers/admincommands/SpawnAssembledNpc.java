/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.Iterator;
import javolution.util.FastList;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.assemblednpc.AssembledNpc;
import com.ne.gs.model.assemblednpc.AssembledNpcPart;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.assemblednpc.AssembledNpcTemplate;
import com.ne.gs.network.aion.serverpackets.SM_NPC_ASSEMBLER;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.utils.idfactory.IDFactory;
import com.ne.gs.world.World;

/**
 * @author xTz
 */
public class SpawnAssembledNpc extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length != 1) {
            onError(player, null);
            return;
        }
        int spawnId;
        try {
            spawnId = Integer.parseInt(params[0]);
        } catch (Exception e) {
            onError(player, null);
            return;
        }

        AssembledNpcTemplate template = DataManager.ASSEMBLED_NPC_DATA.getAssembledNpcTemplate(spawnId);
        if (template == null) {
            player.sendMsg("This spawnId is Wrong.");
            return;
        }
        FastList<AssembledNpcPart> assembledPatrs = new FastList<>();
        for (AssembledNpcTemplate.AssembledNpcPartTemplate npcPart : template.getAssembledNpcPartTemplates()) {
            assembledPatrs.add(new AssembledNpcPart(IDFactory.getInstance().nextId(), npcPart));
        }
        AssembledNpc npc = new AssembledNpc(template.getRouteId(), template.getMapId(), template.getLiveTime(), assembledPatrs);
        Iterator<Player> iter = World.getInstance().getPlayersIterator();
        Player findedPlayer;
        while (iter.hasNext()) {
            findedPlayer = iter.next();
            findedPlayer.sendPck(new SM_NPC_ASSEMBLER(npc));
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //spawnAssembledNpc <sapwnId>");
    }
}
