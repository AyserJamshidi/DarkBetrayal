/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.rentusBase;

import java.util.concurrent.atomic.AtomicBoolean;
import ai.GeneralNpcAI2;

import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.walker.WalkerTemplate;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("imprisoned_reian")
public class ImprisonedReianAI2 extends GeneralNpcAI2 {

    private final AtomicBoolean isSaved = new AtomicBoolean(false);
    private final AtomicBoolean isAsked = new AtomicBoolean(false);
    private String walkerId;
    private WalkerTemplate template;

    @Override
    protected void handleSpawned() {
        walkerId = getSpawnTemplate().getWalkerId();
        getSpawnTemplate().setWalkerId(null);
        if (walkerId != null) {
            template = DataManager.WALKER_DATA.getWalkerTemplate(walkerId);
        }
        super.handleSpawned();
    }

    @Override
    protected void handleMoveArrived() {
        int point = getOwner().getMoveController().getCurrentPoint();
        super.handleMoveArrived();
        if (template.getRouteSteps().size() - 4 == point) {
            getSpawnTemplate().setWalkerId(null);
            WalkManager.stopWalking(this);
            AI2Actions.deleteOwner(this);
        }
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        if (walkerId != null) {
            if (creature instanceof Player) {
                Player player = (Player) creature;
                if (MathUtil.getDistance(getOwner(), player) <= 21) {
                    if (isAsked.compareAndSet(false, true)) {
                        switch (Rnd.get(1, 10)) {
                            case 1:
                                sendMsg(390563);
                                break;
                            case 2:
                                sendMsg(390567);
                                break;
                        }
                    }
                }
                if (MathUtil.getDistance(getOwner(), player) <= 6) {
                    if (isSaved.compareAndSet(false, true)) {
                        getSpawnTemplate().setWalkerId(walkerId);
                        WalkManager.startWalking(this);
                        getOwner().setState(1);
                        PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
                        switch (Rnd.get(1, 10)) {
                            case 1:
                                sendMsg(342410);
                                break;
                            case 2:
                                sendMsg(342411);
                                break;
                        }
                    }
                }
            }
        }
    }

    private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
}
