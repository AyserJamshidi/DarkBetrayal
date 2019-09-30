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
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Mrakobes, Loxo
 */
public class Heal extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        VisibleObject target = player.getTarget();
        if (target == null) {
            player.sendMsg("No target selected");
            return;
        }

        Creature creature = (Creature) target;
        if (params.length < 1) {
            creature.getLifeStats().increaseHp(TYPE.HP, creature.getLifeStats().getMaxHp() + 1);
            creature.getLifeStats().increaseMp(TYPE.MP, creature.getLifeStats().getMaxMp() + 1);
            creature.getEffectController().removeEffect(8291);
            player.sendMsg(creature.getName() + " has been refreshed !");
        } else if (params[0].equals("dp") && target instanceof Player) {
            Player targetPlayer = (Player) creature;
            targetPlayer.getLifeStats().increaseDp(targetPlayer.getGameStats().getMaxDp().getCurrent());
            player.sendMsg(creature.getName() + " is now full of DP !");
        } else {
            onError(player, null);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        String syntax = "//heal : Full HP and MP\n" + "//heal dp : Full DP, must be used on a player !";
        player.sendMsg(syntax);
    }
}
