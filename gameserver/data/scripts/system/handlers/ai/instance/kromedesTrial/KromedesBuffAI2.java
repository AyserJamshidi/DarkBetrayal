/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.kromedesTrial;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.skillengine.SkillEngine;

/**
 * @author Tiger0319, Gigi, xTz
 */
@AIName("krbuff")
public class KromedesBuffAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleUseItemFinish(Player player) {
        switch (getNpcId()) {
            case 730336:
                SkillEngine.getInstance().getSkill(player, 19216, 1, player).useSkill();
                player.sendPck(new SM_SYSTEM_MESSAGE(1400655));
                break;
            case 730337:
                SkillEngine.getInstance().getSkill(player, 19217, 1, player).useSkill();
                player.sendPck(new SM_SYSTEM_MESSAGE(1400656));
                AI2Actions.deleteOwner(this);
                break;
            case 730338:
                SkillEngine.getInstance().getSkill(player, 19218, 1, player).useSkill();
                player.sendPck(new SM_SYSTEM_MESSAGE(1400657));
                AI2Actions.deleteOwner(this);
                break;
            case 730339:
                SkillEngine.getInstance().getSkill(player, 19219, 1, player).useSkill();
                player.sendPck(new SM_SYSTEM_MESSAGE(1400658));
                break;
        }
    }
}
