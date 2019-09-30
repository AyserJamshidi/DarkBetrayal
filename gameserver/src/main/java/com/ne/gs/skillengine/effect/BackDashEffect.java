/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.ne.gs.controllers.movement.PlayerMoveController;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.skillengine.action.DamageType;
import com.ne.gs.skillengine.model.DashStatus;
import com.ne.gs.skillengine.model.Effect;
import com.ne.gs.skillengine.model.Skill;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.world.World;
import mw.engines.geo.GeoEngine;
import mw.engines.geo.GeoHelper;
import mw.engines.geo.collision.CollidableType;
import mw.engines.geo.collision.CollisionResults;
import mw.engines.geo.math.Vector3f;
import mw.utils.GeomUtil;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BackDashEffect")
public class BackDashEffect extends DamageEffect {

    @XmlAttribute(name = "distance")
    private float distance;
    // backward = 1, forward = 0
    private final float direction = 1;

    @Override
    public void applyEffect(Effect effect) {
        super.applyEffect(effect);
        Player effector = (Player) effect.getEffector();

        Skill skill = effect.getSkill();

        effector.getMoveController().setBegin(skill.getX(), skill.getY(), skill.getZ());
        World.getInstance().updatePosition(effector, skill.getX(), skill.getY(), skill.getZ(), skill.getH());
    }

    @Override
    public void calculate(Effect effect) {
        if (!super.calculate(effect, DamageType.PHYSICAL)) {
            return;
        }

        effect.setDashStatus(DashStatus.BACKDASH);

        Player effector = (Player) effect.getEffector();
        double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
        float x1 = (float) (Math.cos(Math.PI * direction + radian) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction + radian) * distance);

        byte intentions = (byte) (CollidableType.PHYSICAL.getId() | CollidableType.DOOR.getId());

        Vector3f target = GeoEngine.getAvailablePoint(effector, x1, y1, intentions);

        effect.getSkill().setTargetPosition(target.getX(), target.getY(), target.getZ(),
                effector.getHeading());
    }
}
