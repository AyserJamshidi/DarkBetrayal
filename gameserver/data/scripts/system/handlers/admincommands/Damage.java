/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Source
 */
public class Damage extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects.player.Player, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) throws Exception {
        if (params.length > 1) {
            throw new Exception();
        }

        VisibleObject target = player.getTarget();
        if (target == null) {
            player.sendMsg("No target selected");
        } else if (target instanceof Creature) {
            Creature creature = (Creature) target;
            int dmg;

            String percent = params[0];
            Pattern damage = Pattern.compile("([^%]+)%");
            Matcher result = damage.matcher(percent);

            if (result.find()) {
                dmg = Integer.parseInt(result.group(1));

                if (dmg < 100) {
                    creature.getController().onAttack(player, (int) (dmg / 100f * creature.getLifeStats().getMaxHp()), true);
                } else {
                    creature.getController().onAttack(player, creature.getLifeStats().getMaxHp() + 1, true);
                }
            } else {
                creature.getController().onAttack(player, Integer.parseInt(params[0]), true);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#onError(com.ne.gs.model.gameobjects.player.Player, java.lang.Exception)
     */
    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //damage <dmg | dmg%>" + "\n<dmg> must be a number.");
    }

}
