/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import java.util.Date;

import com.ne.commons.DateUtil;
import com.ne.commons.annotations.NotNull;
import com.ne.commons.utils.L10N;
import com.ne.gs.CoreVersion;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author hex1r0
 */
public class CmdVersion extends ChatCommand {
    @Override
    protected void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) throws Exception {
        String v = CoreVersion.VERSION.getVersion();
        String r = CoreVersion.VERSION.getRevision();
        Date d = DateUtil.date(CoreVersion.VERSION.getBuildTime());

        player.sendMsg(L10N.translate(Messages.VERSION, L10N.Lang.RU));
        player.sendMsg(String.format("%s [%s] - %s", v, r, d));
    }

    public static enum Messages implements L10N.Translatable {
        VERSION("Version:");

        private final String _defaultValue;
        private Messages(String defaultValue) {
            _defaultValue = defaultValue;
        }

        @Override
        public String id() {
            return toString();
        }

        @Override
        public String defaultValue() {
            return _defaultValue;
        }
    }
}
