/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.portals;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.modules.housing.Housing;

/**
 * TODO implement ability to store/register AIs from any place
 *
 * @author hex1r0
 */
@AIName("RelationshipCrystal")
public class RelationshipCrystal extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        Housing.housing().tell(new Housing.QuerryRelationshipCrystal(player, getOwner()));
    }

    @Override
    protected void handleDialogFinish(Player player) {
    }
}
