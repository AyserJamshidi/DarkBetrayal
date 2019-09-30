/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.beshmundirTemple;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.world.knownlist.Visitor;

/**
 * @author Tiger0319, xTz, Gigi
 */
@AIName("plegeton")
public class PlegetonAI2 extends NpcAI2 {

    private boolean isStartTimer = false;

    @Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 10000) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));
            switch (getNpcId()) {
                case 799517:
                    player.sendPck(new SM_PLAY_MOVIE(0, 0, 448, 16777216));
                    if (!isStartTimer) {
                        isStartTimer = true;
                        sendTimer();
                        ThreadPoolManager.getInstance().schedule(new Runnable() {

                            @Override
                            public void run() {
                                Npc npc = getPosition().getWorldMapInstance().getNpc(216586);
                                if (npc != null && !npc.getLifeStats().isAlreadyDead()) {
                                    npc.getController().onDelete();
                                    player.sendPck(new SM_QUEST_ACTION(0, 0));
                                    getPosition().getWorldMapInstance().getDoors().get(467).setOpen(true);
                                }
                            }
                        }, 420000);

                    }
                    TeleportService.teleportTo(player, 300170000, 958.45233f, 430.4892f, 219.80301f);
                    break;
                case 799518:
                    player.sendPck(new SM_PLAY_MOVIE(0, 0, 449, 16777216));
                    TeleportService.teleportTo(player, 300170000, 822.0199f, 465.1819f, 220.29918f);
                    break;
                case 799519:
                    player.sendPck(new SM_PLAY_MOVIE(0, 0, 450, 16777216));
                    TeleportService.teleportTo(player, 300170000, 777.1054f, 300.39005f, 219.89926f);
                    break;
                case 799520:
                    player.sendPck(new SM_PLAY_MOVIE(0, 0, 451, 16777216));
                    TeleportService.teleportTo(player, 300170000, 942.3092f, 270.91855f, 219.86185f);
                    break;
            }
        }
        return true;
    }

    private void sendTimer() {
        getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                player.sendPck(new SM_QUEST_ACTION(0, 420));
            }

        });
    }

    @Override
    protected void handleDialogStart(Player player) {
        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
}
