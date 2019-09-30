/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.kromedesTrial;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.gameobjects.Npc;

@AIName("kaligacontroller")
public class KaligaControllerAI2 extends NpcAI2 {

    private Npc getBoss() {
        Npc npc = null;
        switch (getNpcId()) {
            case 282093:
            case 282095:
                npc = getPosition().getWorldMapInstance().getNpc(217006);
                break;
        }
        return npc;
    }

    private void apllyEffect(boolean remove) {
        Npc boss = getBoss();
        if (boss != null && !boss.getLifeStats().isAlreadyDead()) {
            switch (getNpcId()) {
                case 282093:
                    if (remove) {
                        boss.getEffectController().removeEffect(19788);
						boss.getEffectController().removeEffect(19248);
                    }
                    break;
                case 282095:
                    if (remove) {
                        boss.getEffectController().removeEffect(19787);
						boss.getEffectController().removeEffect(19247);
                    }
                    break;
            }
        }
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        apllyEffect(true);
        AI2Actions.deleteOwner(this);
    }

    @Override
    protected AIAnswer pollInstance(AIQuestion question) {
        switch (question) {
            case SHOULD_DECAY:
                return AIAnswers.NEGATIVE;
            case SHOULD_RESPAWN:
                return AIAnswers.NEGATIVE;
            case SHOULD_REWARD:
                return AIAnswers.NEGATIVE;
            default:
                return null;
        }
    }
}
