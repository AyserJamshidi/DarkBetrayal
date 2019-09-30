/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.ArrayList;
import java.util.List;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.stats.calc.Stat2;
import com.ne.gs.model.stats.calc.StatOwner;
import com.ne.gs.model.stats.calc.functions.IStatFunction;
import com.ne.gs.model.stats.calc.functions.StatFunction;
import com.ne.gs.model.stats.container.StatEnum;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author ATracer
 */
public class Speed extends ChatCommand implements StatOwner {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            admin.sendMsg("Syntax //speed <percent>");
            return;
        }

        int parameter;
        try {
            parameter = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            admin.sendMsg("Parameter should number");
            return;
        }

        if (parameter < 0 || parameter > 1000) {
            admin.sendMsg("Valid values are in 0-1000 range");
            return;
        }

        admin.getGameStats().endEffect(this);
        List<IStatFunction> functions = new ArrayList<>();
        functions.add(new SpeedFunction(StatEnum.SPEED, parameter));
        functions.add(new SpeedFunction(StatEnum.FLY_SPEED, parameter));
        admin.getGameStats().addEffect(this, functions);

        PacketSendUtility.broadcastPacket(admin, new SM_EMOTION(admin, EmotionType.START_EMOTE2, 0, 0), true);
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax //speed <percent>");
    }

    class SpeedFunction extends StatFunction {

        static final int speed = 6000;
        static final int flyspeed = 9000;
        int modifier = 1;

        SpeedFunction(StatEnum stat, int modifier) {
            this.stat = stat;
            this.modifier = modifier;
        }

        @Override
        public void apply(Stat2 stat) {
            switch (this.stat) {
                case SPEED:
                    stat.setBase(speed + speed * modifier / 100);
                    break;
                case FLY_SPEED:
                    stat.setBase(flyspeed + flyspeed * modifier / 100);
                    break;
            }
        }

        @Override
        public int getPriority() {
            return 60;
        }
    }
}
