/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AttackIntention;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.event.AIEventType;
import com.ne.gs.ai2.handler.*;
import com.ne.gs.ai2.manager.SkillAttackManager;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.skill.NpcSkillEntry;
import com.ne.gs.model.templates.npcshout.ShoutEventType;

/**
 * @author ATracer
 */
@AIName("general")
public class GeneralNpcAI2 extends NpcAI2 {

    @Override
    public void think() {
        ThinkEventHandler.onThink(this);
    }

    @Override
    protected void handleDied() {
        DiedEventHandler.onDie(this);
    }

    @Override
    protected void handleAttack(Creature creature) {
        AttackEventHandler.onAttack(this, creature);
    }

    @Override
    protected boolean handleCreatureNeedsSupport(Creature creature) {
        return AggroEventHandler.onCreatureNeedsSupport(this, creature);
    }

    @Override
    protected void handleDialogStart(Player player) {
        TalkEventHandler.onTalk(this, player);
    }

    @Override
    protected void handleDialogFinish(Player creature) {
        TalkEventHandler.onFinishTalk(this, creature);
    }

    @Override
    protected void handleFinishAttack() {
        AttackEventHandler.onFinishAttack(this);
    }

    @Override
    protected void handleAttackComplete() {
        AttackEventHandler.onAttackComplete(this);
    }

    @Override
    protected void handleTargetReached() {
        TargetEventHandler.onTargetReached(this);
    }

    @Override
    protected void handleNotAtHome() {
        ReturningEventHandler.onNotAtHome(this);
    }

    @Override
    protected void handleBackHome() {
        ReturningEventHandler.onBackHome(this);
    }

    @Override
    protected void handleTargetTooFar() {
        TargetEventHandler.onTargetTooFar(this);
    }

    @Override
    protected void handleTargetGiveup() {
        TargetEventHandler.onTargetGiveup(this);
    }

    @Override
    protected void handleTargetChanged(Creature creature) {
        super.handleTargetChanged(creature);
        TargetEventHandler.onTargetChange(this, creature);
    }

    @Override
    protected void handleMoveValidate() {
        MoveEventHandler.onMoveValidate(this);
    }

    @Override
    protected void handleMoveArrived() {
        super.handleMoveArrived();
        MoveEventHandler.onMoveArrived(this);
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        CreatureEventHandler.onCreatureMoved(this, creature);
    }

    @Override
    protected void handleDespawned() {
        super.handleDespawned();
    }

    @Override
    protected boolean canHandleEvent(AIEventType eventType) {
        boolean canHandle = super.canHandleEvent(eventType);

        switch (eventType) {
            case CREATURE_MOVED:
                return canHandle || DataManager.NPC_SHOUT_DATA.hasAnyShout(getOwner().getWorldId(), getOwner().getNpcId(), ShoutEventType.SEE);
            case CREATURE_NEEDS_SUPPORT:
                return canHandle && isNonFightingState() && DataManager.TRIBE_RELATIONS_DATA.hasSupportRelations(getOwner().getTribe());
        }
        return canHandle;
    }

    @Override
    public AttackIntention chooseAttackIntention() {
        VisibleObject currentTarget = getTarget();
        Creature mostHated = getAggroList().getMostHated();

        if (mostHated == null || mostHated.getLifeStats().isAlreadyDead()) {
            return AttackIntention.FINISH_ATTACK;
        }

        if (currentTarget == null || !currentTarget.getObjectId().equals(mostHated.getObjectId())) {
            onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
            return AttackIntention.SWITCH_TARGET;
        }

        NpcSkillEntry skill = SkillAttackManager.chooseNextSkill(this);
        if (skill != null) {
            skillId = skill.getSkillId();
            skillLevel = skill.getSkillLevel();
            return AttackIntention.SKILL_ATTACK;
        }
        return AttackIntention.SIMPLE_ATTACK;
    }
}
