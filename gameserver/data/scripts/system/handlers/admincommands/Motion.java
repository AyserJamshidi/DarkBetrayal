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
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.stats.calc.StatOwner;
import com.ne.gs.model.stats.calc.functions.IStatFunction;
import com.ne.gs.model.stats.calc.functions.StatAddFunction;
import com.ne.gs.model.stats.container.StatEnum;
import com.ne.gs.services.MotionLoggingService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author kecimis
 */
public class Motion extends ChatCommand implements StatOwner {

    /*
     * (non-Javadoc)
     * @see gameserver.utils.chathandlers.ChatCommand#execute(gameserver.model.gameobjects.player.Player, java.lang.String[])
     */
    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) throws Exception {
        if (params.length == 0) {
            throw new Exception();

        }
        if (params[0].equalsIgnoreCase("help")) {
            player.sendMsg("syntax: //motion <HELP|analyze|savetosql|advanced|as>");
            player.sendMsg("//motion start - starts MotionLoggingService, plus loads data from db");
            player.sendMsg("//motion advanced - turns on/of advanced logging info");
            player.sendMsg("//motion as (value) - adds attack speed");
            player.sendMsg("//motion analyze - creats .txt files in SERVER_DIR/motions with detailed info about motions");
            player.sendMsg("//motion savetosql - saves content of MotionLoggingService to database");
            player.sendMsg("//motion createxml - create new_motion_times.xml in static_data/skills");
        } else if (params[0].equalsIgnoreCase("start")) {
            MotionLoggingService.getInstance().start();
            player.sendMsg("MotionLogginService was started!\nData loaded from GDB.");
        } else if (params[0].equalsIgnoreCase("analyze")) {
            MotionLoggingService.getInstance().createAnalyzeFiles();
            player.sendMsg("Created testing files!");
        } else if (params[0].equalsIgnoreCase("createxml")) {
            MotionLoggingService.getInstance().createFinalFile();
            player.sendMsg("Created new_motion_times.xml in data/static_data/skills!");
        } else if (params[0].equalsIgnoreCase("savetosql")) {
            MotionLoggingService.getInstance().saveToSql();
            player.sendMsg("MotionLog data saved to sql!");
        } else if (params[0].equalsIgnoreCase("advanced")) {
            MotionLoggingService.getInstance().setAdvancedLog(!MotionLoggingService.getInstance().getAdvancedLog());
            player.sendMsg("AdvancedLog set to: " + MotionLoggingService.getInstance().getAdvancedLog());
        } else if (params[0].equalsIgnoreCase("as")) {
            int parameter = 10000;
            if (params.length == 2) {
                try {
                    parameter = Integer.parseInt(params[1]);
                } catch (NumberFormatException e) {
                    player.sendMsg("Parameter should number");
                    return;
                }
            }
            addAttackSpeed(player, -parameter);
            player.sendMsg("Attack Speed updated");
        } else {
            throw new Exception();
        }
    }

    private void addAttackSpeed(Player player, int i) {
        if (i == 0) {
            player.getGameStats().endEffect(this);
        } else {
            List<IStatFunction> modifiers = new ArrayList<>();
            modifiers.add(new StatAddFunction(StatEnum.ATTACK_SPEED, i, true));
            player.getGameStats().endEffect(this);
            player.getGameStats().addEffect(this, modifiers);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax: //motion <HELP|analyze|savetosql|advanced|as>");
    }
}
