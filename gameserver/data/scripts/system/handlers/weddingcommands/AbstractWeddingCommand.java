/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package weddingcommands;

import java.util.Map;

import gnu.trove.map.hash.THashMap;

import com.ne.commons.Sys;
import com.ne.commons.annotations.NotNull;
import com.ne.commons.annotations.Nullable;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.WorldMapType;

/**
 * @author hex1r0
 */
public abstract class AbstractWeddingCommand extends ChatCommand {

    protected final Map<Integer, Long> _cooldowns = new THashMap<>();

    protected boolean checkPreconditions(@NotNull Player player, @Nullable Player partner) {

        if(player.getPartnerId() == 0){
            player.sendMsg("Эта команда доступна только тем, у кого есть супруг.");
            return false;
        }

        if (partner == null) {
            player.sendMsg("Не в сети.");
            return false;
        }

        int userMapId = player.getWorldId();
        int partnerMapId = partner.getWorldId();

        if (WorldMapType.DE_PRISON.equals(userMapId) || WorldMapType.DF_PRISON.equals(userMapId)) {
            player.sendMsg("Вы не можете использовать эту команду в Тюрьме.");
            return false;
        }

        if (WorldMapType.DE_PRISON.equals(partnerMapId) || WorldMapType.DF_PRISON.equals(partnerMapId)) {
            player.sendMsg("Вы не можете переместиться к " + partner.getName() + ", ваш партнер в Тюрьме.");
            return false;
        }

        if (player.getInstanceId() != 1 || partner.getInstanceId() != 1) {
            player.sendMsg("Вы не можете использовать эту команду в Данже.");
            return false;
        }

        if (player.isAttackMode() || partner.isAttackMode()) {
            player.sendMsg("Вы не можете использовать эту команду во время боя.");
            return false;
        }

        synchronized (_cooldowns) {
            Long timestamp = _cooldowns.get(player.getObjectId());
            if (timestamp != null) {
                long cooldown = Math.max(0, (timestamp + 20 * 60000) - Sys.millis());
                if (cooldown > 0) {
                    player.sendMsg("Откат " + (cooldown / 1000) + " секунд.");
                    return false;
                }
            }
        }

        return true;
    }

    protected void storeCooldown(Player player) {
        synchronized (_cooldowns) {
            _cooldowns.put(player.getObjectId(), Sys.millis());
        }
    }
}
