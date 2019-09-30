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
import com.ne.gs.controllers.attack.AggroInfo;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Gatherable;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.siege.SiegeNpc;
import com.ne.gs.model.templates.walker.WalkerTemplate;
import com.ne.gs.restrictions.RestrictionsManager;
import com.ne.gs.spawnengine.ClusteredNpc;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Nemiroff Date: 28.12.2009
 */
public class Info extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        VisibleObject target = admin.getTarget();

        if (target == null || target.equals(admin)) {
            admin.sendMsg("[Info about you]" + "\nPlayer Id: " + admin.getObjectId() + "\nMap ID: " + admin.getWorldId() + "\nX: "
                + admin.getCommonData().getPosition().getX() + " / Y: " + admin.getCommonData().getPosition().getY() + " / Z: "
                + admin.getCommonData().getPosition().getZ() + " / Heading: " + admin.getCommonData().getPosition().getH());
        } else if (target instanceof Npc) {
            Npc npc = (Npc) admin.getTarget();
            admin.sendMsg("[Info about target]" + "\nName: " + npc.getName() + "\nId: " + npc.getNpcId() + " / ObjectId: "
                + admin.getTarget().getObjectId() + " / StaticId: " + npc.getSpawn().getStaticId() + "\nMap ID: " + admin.getTarget().getWorldId()
                + "\nX: " + admin.getTarget().getX() + " / Y: " + admin.getTarget().getY() + " / Z: " + admin.getTarget().getZ() + " / Heading: "
                + admin.getTarget().getHeading());
            if (npc instanceof SiegeNpc) {
                SiegeNpc siegeNpc = (SiegeNpc) npc;
                admin.sendMsg("[Siege info]" + "\nSiegeId: " + siegeNpc.getSiegeId() + "\nSiegeRace: " + siegeNpc.getSiegeRace());
            }
            admin.sendMsg("Tribe: " + npc.getTribe() + "\nRace: " + npc.getObjectTemplate().getRace() + "\nNpcType: "
                + npc.getObjectTemplate().getNpcType().name() + "\nTemplateType: " + npc.getObjectTemplate().getNpcTemplateType().name() + "\nAbyssType: "
                + npc.getObjectTemplate().getAbyssNpcType().name() + "\nAI: " + npc.getAi2().getName());
            admin.sendMsg("[Relations to target]" + "\nisEnemy: " + admin.isEnemy(npc) + "\ncanAttack: " + RestrictionsManager.canAttack(admin, target)
                + "\n[Relations to you]" + "\nisEnemy: " + npc.isEnemy(admin) + "\nisAggressive: " + npc.isAggressiveTo(admin) + "\nisAggroIcon: "
                + admin.isAggroIconTo(npc));
            admin.sendMsg("[Life stats]" + "\nCur. HP: " + npc.getLifeStats().getCurrentHp() + " / Cur. MP: "
                + npc.getLifeStats().getCurrentMp() + "\nMax. HP: " + npc.getLifeStats().getMaxHp() + " / Max. MP: " + npc.getLifeStats().getMaxMp());
            int asmoDmg = 0;
            int elyDmg = 0;
            admin.sendMsg("[AgroList]");
            for (AggroInfo ai : npc.getAggroList().getList()) {
                if (!(ai.getAttacker() instanceof Creature)) {
                    continue;
                }
                Creature master = ((Creature) ai.getAttacker()).getMaster();
                if (master == null) {
                    continue;
                }
                if (master instanceof Player) {
                    Player player = (Player) master;
                    admin.sendMsg("Name: " + player.getName() + " Dmg: " + ai.getDamage());
                    if (player.getRace() == Race.ASMODIANS) {
                        asmoDmg += ai.getDamage();
                    } else {
                        elyDmg += ai.getDamage();
                    }
                }
            }
            admin.sendMsg("[TotalDmg]" + "\n(A) Dmg: " + asmoDmg + "\n(E) Dmg: " + elyDmg);
            if (npc.getSpawn().getWalkerId() != null) {
                WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(npc.getSpawn().getWalkerId());
                if (template != null) {
                    admin.sendMsg("[Route]" + "\nRouteId: " + npc.getSpawn().getWalkerId() + " (Reversed: " + template.isReversed()
                        + ")" + "\nRandomWalk: " + npc.getSpawn().getRandomWalk());
                    if (npc.getWalkerGroup() != null) {
                        ClusteredNpc snpc = npc.getWalkerGroup().getClusterData(npc);
                        admin.sendMsg("[Group]" + "\nType: " + npc.getWalkerGroup().getWalkType() + " / XDelta: " + snpc.getXDelta()
                            + " / YDelta: " + snpc.getYDelta() + " / Index: " + snpc.getWalkerIndex());
                    }
                }
            }
        } else if (target instanceof Gatherable) {
            Gatherable gather = (Gatherable) target;
            admin.sendMsg("[Info about gather]\n" + "Name: " + gather.getName() + "\nId: " + gather.getObjectTemplate().getTemplateId()
                + " / ObjectId: " + admin.getTarget().getObjectId() + "\nMap ID: " + admin.getTarget().getWorldId() + "\nX: " + admin.getTarget().getX()
                + " / Y: " + admin.getTarget().getY() + " / Z: " + admin.getTarget().getZ() + " / Heading: " + admin.getTarget().getHeading());
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub
    }
}
