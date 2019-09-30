/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.tallocsHollow;

import ai.SummonerAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.Summon;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.summons.SummonMode;
import com.ne.gs.model.summons.UnsummonType;
import com.ne.gs.model.templates.spawns.SpawnTemplate;
import com.ne.gs.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.summons.SummonsService;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.knownlist.Visitor;

/**
 * @author xTz
 */
@AIName("queenmosqua")
public class QueenMosquaAI2 extends SummonerAI2 {

    private boolean isHome = true;

    @Override
    protected void handleCreatureAggro(Creature creature) {
        super.handleCreatureAggro(creature);
        if (isHome) {
            isHome = false;
            getPosition().getWorldMapInstance().getDoors().get(7).setOpen(false);
        }
    }

    @Override
    protected void handleBackHome() {
        isHome = true;
        getPosition().getWorldMapInstance().getDoors().get(7).setOpen(true);
        super.handleBackHome();
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        WorldMapInstance instance = getPosition().getWorldMapInstance();
        getPosition().getWorldMapInstance().getDoors().get(7).setOpen(true);

        Npc npc = instance.getNpc(700738);
        if (npc != null) {
            SpawnTemplate template = npc.getSpawn();
            spawn(700739, template.getX(), template.getY(), template.getZ(), template.getHeading(), 11);
            npc.getKnownList().doOnAllPlayers(new Visitor<Player>() {

                @Override
                public void visit(Player player) {
                    player.sendPck(new SM_SYSTEM_MESSAGE(1400476));
                    Summon summon = player.getSummon();
                    if (summon != null) {
                        if (summon.getNpcId() == 799500 || summon.getNpcId() == 799501) {
                            SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
                            player.sendPck(new SM_PLAY_MOVIE(0, 435));
                        }
                    }
                }
            });
            npc.getController().onDelete();
        }
    }

}
