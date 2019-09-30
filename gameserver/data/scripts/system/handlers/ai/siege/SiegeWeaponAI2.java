/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.siege;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AISummon;
import com.ne.gs.controllers.SiegeWeaponController;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.summons.SummonMode;
import com.ne.gs.model.templates.npcskill.NpcSkillTemplate;
import com.ne.gs.model.templates.npcskill.NpcSkillTemplates;
import com.ne.gs.services.summons.SummonsService;

/**
 * @author xTz
 */
@AIName("siege_weapon")
public class SiegeWeaponAI2 extends AISummon {

    private long lastAttackTime;
    private int skill;
    private int skillLvl;
    private int duration;

    @Override
    protected void handleSpawned() {
        setStateIfNot(AIState.IDLE);
        SummonsService.doMode(SummonMode.GUARD, getOwner());
        NpcSkillTemplate skillTemplate = getNpcSkillTemplates().getNpcSkills().get(0);
        skill = skillTemplate.getSkillid();
        skillLvl = skillTemplate.getSkillLevel();
        duration = DataManager.SKILL_DATA.getSkillTemplate(skill).getDuration();
    }

    @Override
    protected void handleFollowMe(Creature creature) {
        setStateIfNot(AIState.FOLLOWING);
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
    }

    @Override
    protected void handleStopFollowMe(Creature creature) {
        setStateIfNot(AIState.IDLE);
        getOwner().getMoveController().abortMove();
    }

    @Override
    protected void handleTargetTooFar() {
        getOwner().getMoveController().moveToDestination();
    }

    @Override
    protected void handleMoveArrived() {
        getOwner().getController().onMove();
        getOwner().getMoveController().abortMove();
    }

    @Override
    protected void handleMoveValidate() {
        getOwner().getController().onMove();
        getMoveController().moveToTargetObject();
    }

    @Override
    protected SiegeWeaponController getController() {
        return (SiegeWeaponController) super.getController();
    }

    private NpcSkillTemplates getNpcSkillTemplates() {
        return getController().getNpcSkillTemplates();
    }

    @Override
    protected void handleAttack(Creature creature) {
        if (creature == null) {
            return;
        }
        Race race = creature.getRace();
        Player master = getOwner().getMaster();
        if (master == null) {
            return;
        }

        int npc = getOwner().getNpcId();
        switch (npc) {
            case 201054:
            case 201055:
            case 201056:
            case 201057:
            case 201058:
            case 201059:
                Race masterRace = master.getRace();
                if (masterRace.equals(Race.ASMODIANS) && !race.equals(Race.PC_LIGHT_CASTLE_DOOR) && !race.equals(Race.DRAGON_CASTLE_DOOR)) {
                    return;
                } else if (masterRace.equals(Race.ELYOS) && !race.equals(Race.PC_DARK_CASTLE_DOOR) && !race.equals(Race.DRAGON_CASTLE_DOOR)) {
                    return;
                }
                break;
        }


        if (!getOwner().getMode().equals(SummonMode.ATTACK)) {
            return;
        }

        if (System.currentTimeMillis() - lastAttackTime > duration + 2000) {
            lastAttackTime = System.currentTimeMillis();
            getOwner().getController().useSkill(skill, skillLvl);
        }
    }
}
