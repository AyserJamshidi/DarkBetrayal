/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.skillengine.effect;

import com.ne.gs.skillengine.action.DamageType;
import com.ne.gs.skillengine.change.Change;
import com.ne.gs.skillengine.model.Effect;

import com.ne.commons.utils.Rnd;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import static com.ne.gs.controllers.attack.AttackUtil.calculateMagicalSkillResult;

/**
 * @author ATracer, hex1r0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignetBurstEffect")
public class SignetBurstEffect extends DamageEffect {

    @XmlAttribute
    protected int signetlvl;
    @XmlAttribute
    protected String signet;

    private int level2 = 35;
    private int level3 = 95;

    private void tryRemoveSignedEffect(Effect effect){
        Effect signetEffect = effect.getEffected().getEffectController().getAnormalEffect(signet);
        if (signetEffect != null && (effect.getSubEffect() == null || !effect.getSuccessSubEffects().isEmpty())) {
            signetEffect.endEffect();
        }
    }

    @Override
    public void applyEffect(Effect effect) {
        super.applyEffect(effect);
        tryRemoveSignedEffect(effect);
    }

    @Override
    public void calculate(Effect effect) {
        // FIXME temp solution, causes problems with other skills

        if (effect.getSkillId() == 946) {
            if (!super.calculate(effect, DamageType.MAGICAL, true)) {
                return;
            }
        } else {
            if (!super.calculate(effect, DamageType.MAGICAL)) {
                tryRemoveSignedEffect(effect);
                return;
            }
        }

        Effect signetEffect = effect.getEffected().getEffectController().getAnormalEffect(signet);

        int valueWithDelta = value + delta * effect.getSkillLevel();
        int critAddDmg = this.critAddDmg2 + this.critAddDmg1 * effect.getSkillLevel();

        if (signetEffect == null) {
            valueWithDelta *= 0.05f;
            calculateMagicalSkillResult(effect, valueWithDelta, null, getElement(), true, true, false, getMode(), this.critProbMod2, critAddDmg);
            effect.setLaunchSubEffect(false);
        } else {
            int level = signetEffect.getSkillLevel();
            effect.setSignetBurstedCount(level);
            switch (level) {
                case 1:
                    valueWithDelta *= 0.2f;
                    break;
                case 2:
                    valueWithDelta *= 0.5f;
                    break;
                case 3:
                    valueWithDelta *= 1.0f;
                    break;
                case 4:
                    valueWithDelta *= 1.2f;
                    break;
                case 5:
                    valueWithDelta *= 1.5f;
                    break;
            }

            /**
             * custom bonuses for magical accurancy according to rune level and effector level follows same logic as damage
             */
            int accmod = 0;
            int mAccurancy = effect.getEffector().getGameStats().getMAccuracy().getCurrent();
            switch (level) {
                case 1:
                    accmod = (int) (-0.8f * mAccurancy);
                    break;
                case 2:
                    accmod = (int) (-0.5f * mAccurancy);
                    break;
                case 3:
                    accmod = 0;
                    break;
                case 4:
                    accmod = (int) (0.2f * mAccurancy);
                    break;
                case 5:
                    accmod = (int) (0.5f * mAccurancy);
                    break;
            }
            if(level < 2) {
                effect.setLaunchSubEffect(false);
            }
            if(level == 2){
                if (Rnd.get(0, 100) > level2) {
                    effect.setLaunchSubEffect(false);
                }
            }
            if(level >= 3){
                if (Rnd.get(0, 100) > level3) {
                    effect.setLaunchSubEffect(false);
                }
            }
            effect.setAccModBoost(accmod);
            calculateMagicalSkillResult(effect, valueWithDelta, null, getElement(), true, true, false, getMode(), this.critProbMod2, critAddDmg);
        }
    }
}
