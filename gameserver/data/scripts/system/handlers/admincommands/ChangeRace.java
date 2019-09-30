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
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_PLAYER_INFO;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author ginho1
 */
public class ChangeRace extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {

        if (admin.getCommonData().getRace() == Race.ELYOS) {
            admin.getCommonData().setRace(Race.ASMODIANS);
        } else {
            admin.getCommonData().setRace(Race.ELYOS);
        }

        admin.clearKnownlist();
        admin.sendPck(new SM_PLAYER_INFO(admin, false));
        admin.updateKnownlist();
    }
}
