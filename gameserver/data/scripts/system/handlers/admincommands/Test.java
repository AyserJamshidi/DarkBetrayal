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
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.team2.common.events.TeamCommand;
import com.ne.gs.model.team2.common.service.PlayerTeamCommandService;
import com.ne.gs.network.aion.serverpackets.SM_QUESTION_WINDOW_CLOSE;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Luno
 */
public class Test extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {

        PlayerTeamCommandService.executeCommand(player,  TeamCommand.getCommand(2), player.getObjectId());
        //player.setLookingForGroup(player.getObjectId() == 2);

        //player.getFlyController().endFly(false);


        //player.sendPck(new SM_QUESTION_WINDOW_CLOSE());
    }
}