/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model.team2.league.events;

import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.RequestResponseHandler;
import com.ne.gs.model.team2.league.League;
import com.ne.gs.model.team2.league.LeagueService;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;

/**
 * @author ATracer
 */
public class LeagueInvite extends RequestResponseHandler {

    private final Player inviter;
    private final Player invited;

    public LeagueInvite(Player inviter, Player invited) {
        super(inviter);
        this.inviter = inviter;
        this.invited = invited;
    }

    @Override
    public void acceptRequest(Creature requester, Player responder) {
        if (LeagueService.canInvite(inviter, invited)) {

            League league = inviter.getPlayerAlliance2().getLeague();

            if (league == null) {
                league = LeagueService.createLeague(inviter, invited);
            } else if (league.size() == 8) {
                invited.sendMsg("That league is already full.");
                inviter.sendMsg("Your league is already full.");
                return;
            }

            if (!invited.isInLeague()) {
                LeagueService.addAlliance(league, invited.getPlayerAlliance2());
            }
        }
    }

    @Override
    public void denyRequest(Creature requester, Player responder) {
        // TODO correct message
        inviter.sendPck(SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_HE_REJECT_INVITATION(responder.getName()));
    }

}
