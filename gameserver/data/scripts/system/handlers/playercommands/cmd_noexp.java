/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Wakizashi
 */
public class cmd_noexp extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String command, @NotNull String... params) {
        if (player.getCommonData().getNoExp()) {
            player.getCommonData().setNoExp(false);
            player.sendMsg("Получение опыта восстановлено !");
        } else {
            player.getCommonData().setNoExp(true);
            player.sendMsg("Получение опыта остановлено !");
        }
    }
}
