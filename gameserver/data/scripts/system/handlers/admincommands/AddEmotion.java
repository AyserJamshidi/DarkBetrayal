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
import com.ne.gs.cache.HTMLCache;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.HTMLService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author ginho1, Damon
 */
public class AddEmotion extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {

        long expireMinutes = 0;
        int emotionId;
        VisibleObject target;
        Player finalTarget = null;

        if (params.length < 1 || params.length > 2) {
            admin.sendMsg("syntax: //addemotion <emotion id [expire time] || html>\nhtml to show html with names.");
            return;
        }

        try {
            emotionId = Integer.parseInt(params[0]);
            if (params.length == 2) {
                expireMinutes = Long.parseLong(params[1]);
            }
        } catch (NumberFormatException ex) {
            if (params[0].equalsIgnoreCase("html")) {
                HTMLService.showHTML(admin, HTMLCache.getInstance().getHTML("emote.xhtml"));
            }
            return;
        }

        if (emotionId < 1 || emotionId > 35 && emotionId < 64 || emotionId > 129) {
            admin.sendMsg("Invalid <emotion id>, must be in intervals : [1-35]U[64-129]");
            return;
        }

        target = admin.getTarget();

        if (target == null) {
            finalTarget = admin;
        } else if (target instanceof Player) {
            finalTarget = (Player) target;
        }

        if (finalTarget.getEmotions().contains(emotionId)) {
            admin.sendMsg("Target has aldready this emotion !");
            return;
        }

        if (params.length == 2) {
            finalTarget.getEmotions().add(emotionId, (int) (System.currentTimeMillis() / 1000 + expireMinutes * 60), true);
        } else {
            finalTarget.getEmotions().add(emotionId, 0, true);
        }
    }
}
