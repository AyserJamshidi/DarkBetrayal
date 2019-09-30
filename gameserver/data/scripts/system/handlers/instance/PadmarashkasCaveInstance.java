/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance;

import com.ne.gs.ai2.event.AIEventType;
import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.skillengine.effect.AbnormalState;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.world.knownlist.Visitor;
import com.ne.gs.world.zone.ZoneInstance;
import com.ne.gs.world.zone.ZoneName;

import java.lang.Runnable;


@InstanceID(320150000)
public class PadmarashkasCaveInstance extends GeneralInstanceHandler {

    boolean moviePlayed = false;
    private int killedPadmarashkaProtector = 0;
    private int killedEggs = 0;

    @Override
    public void onDie(Npc npc) {
        final int npcId = npc.getNpcId();
        switch (npcId) {
            case 218670:
            case 218671:
            case 218673:
            case 218674:
                if (++killedPadmarashkaProtector == 4) {
                    killedPadmarashkaProtector = 0;
                    final Npc padmarashka = getNpc(216580);
                    if (padmarashka != null && !padmarashka.getLifeStats().isAlreadyDead()) {
                        padmarashka.getEffectController().unsetAbnormal(AbnormalState.SLEEP.getId());
                        padmarashka.getEffectController().broadCastEffectsImp();
                        SkillEngine.getInstance().getSkill(padmarashka, 19187, 55, padmarashka).useNoAnimationSkill();
						sendMsg(1400728);
                        padmarashka.getEffectController().removeEffect(19186); //skill should handle this
                        ThreadPoolManager.getInstance().schedule(new Runnable() {

                            @Override
                            public void run() {
                                padmarashka.getAi2().onCreatureEvent(AIEventType.CREATURE_AGGRO, instance.getPlayersInside().get(0));
                            }
                        }, 1000);
                    }
                }
                break;
            case 282613:
            case 282614:
				final Npc padmarashka = getNpc(216580);
				final Npc tutor = getNpc(218675);
				final Npc tutor2 = getNpc(218676);
				padmarashka.getEffectController().removeEffect(18727);
				if (tutor != null && !tutor.getLifeStats().isAlreadyDead()) {
					sendMsg(1500417);
					SkillEngine.getInstance().applyEffectDirectly(20176, tutor, tutor, 0);
				}
				if (tutor2 != null && !tutor2.getLifeStats().isAlreadyDead()) {
					sendMsg(1500417);
					SkillEngine.getInstance().applyEffectDirectly(20176, tutor2, tutor2, 0);
				}
				if (++killedEggs == 5) { //TODO: find value
                    if (padmarashka != null && !padmarashka.getLifeStats().isAlreadyDead()) {
						sendMsg(1401213);
                        SkillEngine.getInstance().applyEffectDirectly(20101, padmarashka, padmarashka, 0);
                    }
                }
                break;
        }
    }

    @Override
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("PADMARASHKAS_NEST_320150000")) {
            if (!moviePlayed)
                sendMovie();
        }
    }

    @Override
    public boolean onDie(final Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
                : lastAttacker.getObjectId()), true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }

    private void sendMovie() {
        instance.doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                player.sendPck(new SM_PLAY_MOVIE(0, 488));
                moviePlayed = true;
            }
        });
    }

    @Override
    public void onInstanceDestroy() {
        moviePlayed = false;
        killedPadmarashkaProtector = 0;
    }
}

