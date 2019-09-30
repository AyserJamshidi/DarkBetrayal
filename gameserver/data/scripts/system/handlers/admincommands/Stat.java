/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.stats.calc.StatOwner;
import com.ne.gs.model.stats.calc.functions.IStatFunction;
import com.ne.gs.model.stats.calc.functions.StatFunctionProxy;
import com.ne.gs.model.stats.container.StatEnum;
import com.ne.gs.skillengine.model.Effect;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author MrPoke
 */
public class Stat extends ChatCommand {

    private static final Logger log = LoggerFactory.getLogger(Stat.class);

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if(params.length == 0) {
        	admin.sendMsg("Syntax:\n  //stat <stat_name>");
        	return;
        }
    	if (params.length >= 1) {
            VisibleObject target = admin.getTarget();
            if (target == null) {
                admin.sendMsg("No target selected");
                return;
            }
            if (target instanceof Creature) {
                Creature creature = (Creature) target;              
                TreeSet<IStatFunction> stats = creature.getGameStats().getStatsByStatEnum(StatEnum.valueOf(params[0]));
                if (params.length == 1) {
                	if(stats.isEmpty()) {
                		admin.sendMsg("Target haven't stat "+params[0]);
                		return;
                	}
                	else {
                		for (IStatFunction stat : stats) {
                            admin.sendMsg(stat.toString());
                        }	
                	}
                } else if ("details".equals(params[1])) {
                    for (IStatFunction stat : stats) {
                        String details = collectDetails(stat);
                        admin.sendMsg(details);
                        log.info(details);
                    }
                }
            }
        }
    }

    private String collectDetails(IStatFunction stat) {
        StringBuilder sb = new StringBuilder();
        sb.append(stat.toString()).append("\n");
        if (stat instanceof StatFunctionProxy) {
            StatFunctionProxy proxy = (StatFunctionProxy) stat;
            sb.append(" -- ").append(proxy.getProxiedFunction().toString());
        }
        StatOwner owner = stat.getOwner();
        if (owner instanceof Effect) {
            Effect effect = (Effect) owner;
            sb.append("\n -- skillId: ").append(effect.getSkillId());
            sb.append("\n -- skillName: ").append(effect.getSkillName());
        }
        return sb.toString();
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub

    }

}
