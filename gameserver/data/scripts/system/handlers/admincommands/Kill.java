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
import com.ne.gs.utils.MathUtil;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author ATracer, Wakizashi
 */
public class Kill extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length > 1) {
            admin.sendMsg("syntax //kill <target | all | <range>>");
            return;
        }

        if (params.length == 0) {
            VisibleObject target = admin.getTarget();
            if (target == null) {
                admin.sendMsg("No target selected");
                return;
            }
            if (target instanceof Creature) {
                Creature creature = (Creature) target;
                creature.getController().onAttack(admin, creature.getLifeStats().getMaxHp() + 1, true);
            }
        } else {
            int range;
            if (params[0].equals("all")) {
                range = -1;
            } else {
                try {
                    range = Integer.parseInt(params[0]);
                } catch (NumberFormatException ex) {
                    admin.sendMsg("<range> must be a number.");
                    return;
                }
            }
            for (VisibleObject obj : admin.getKnownList().getKnownObjects().values()) {
                if (obj instanceof Creature) {
                    Creature creature = (Creature) obj;
                    if (range < 0 || MathUtil.isIn3dRange(admin, obj, range)) {
                        creature.getController().onAttack(admin, creature.getLifeStats().getMaxHp() + 1, true);
                    }
                }
            }
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //kill <target | all | <range>>");
    }
}
