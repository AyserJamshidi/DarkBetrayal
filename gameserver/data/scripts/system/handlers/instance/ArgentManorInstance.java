/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance;

import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author xTz
 */
@InstanceID(300270000)
public class ArgentManorInstance extends GeneralInstanceHandler {

    @Override
    public void onDie(Npc npc) {
        switch (npc.getNpcId()) {
            case 217243:
                Npc prison = instance.getNpc(205498);
                if (prison != null) {
                    NpcShoutsService.getInstance().sendMsg(prison, 1500263, prison.getObjectId(), 0, 0);
                    prison.getSpawn().setWalkerId("69B73541CCBF9F7BAB484BA68FF4BE0D2A9B6AD6");
                    WalkManager.startWalking((NpcAI2) prison.getAi2());
                }
                spawn(701011, 955.91956f, 1240.153f, 54.090305f, (byte) 90);
                break;
        }
    }

    @Override
    public void handleUseItemFinish(Player player, Npc npc) {
        switch (npc.getNpcId()) {
            case 701001:
                SkillEngine.getInstance().getSkill(npc, 19316, 60, player).useNoAnimationSkill();
                break;
            case 701002:
                SkillEngine.getInstance().getSkill(npc, 19317, 60, player).useNoAnimationSkill();
                break;
            case 701003:
                SkillEngine.getInstance().getSkill(npc, 19318, 60, player).useNoAnimationSkill();
                break;
            case 701004:
                SkillEngine.getInstance().getSkill(npc, 19319, 60, player).useNoAnimationSkill();
                break;
        }
    }

    @Override
    public void onPlayerLogOut(Player player) {
        removeEffects(player);
    }

    @Override
    public void onLeaveInstance(Player player) {
        removeEffects(player);
    }

    private void removeEffects(Player player) {
        player.getEffectController().removeEffect(19316);
        player.getEffectController().removeEffect(19317);
        player.getEffectController().removeEffect(19318);
        player.getEffectController().removeEffect(19319);
    }
    
    @Override
    public boolean onReviveEvent(Player player) {
        PlayerReviveService.revive(player, 25, 25, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        player.sendPck(SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        TeleportService.teleportTo(player, mapId, instanceId, 995.0055f, 1207.2366f, 65.640015f, (byte) 90);
        return true;
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
            true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }
}
