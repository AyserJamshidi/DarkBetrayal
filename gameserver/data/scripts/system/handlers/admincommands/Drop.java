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
import com.ne.gs.model.drop.DropLists;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Phantom, ATracer
 */
public class Drop extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.commons.utils.AbstractCommand#runImpl(java.lang.Object, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) throws Exception {
        int num = Integer.parseInt(params[0]);
        int min = 0;
        int max = 0;
        switch (num) {
            case 1:
                min = 200000;
                max = 212500;
                break;
            case 2:
                min = 212501;
                max = 215000;
                break;
            case 3:
                min = 215001;
                max = 217500;
                break;
            case 4:
                min = 217501;
                max = 260000;
                break;
            case 5:
                min = 260001;
                max = 840000;
                break;
        }
        DropLists.Xmlmian(min, max);
    }
}
