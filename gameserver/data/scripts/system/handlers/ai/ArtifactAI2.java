/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import org.slf4j.LoggerFactory;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AI2Request;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.controllers.ItemUseObserver;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.DescId;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.TaskId;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.siege.SiegeNpc;
import com.ne.gs.model.siege.ArtifactLocation;
import com.ne.gs.model.siege.ArtifactStatus;
import com.ne.gs.model.team.legion.LegionPermissionsMask;
import com.ne.gs.model.templates.siegelocation.ArtifactActivation;
import com.ne.gs.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.ne.gs.network.aion.serverpackets.SM_ABYSS_ARTIFACT_INFO3;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.network.aion.serverpackets.SM_USE_OBJECT;
import com.ne.gs.services.SiegeService;
import com.ne.gs.skillengine.model.SkillTemplate;
import com.ne.gs.skillengine.properties.TargetSpeciesAttribute;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.world.knownlist.Visitor;

/**
 * @author ATracer, Source
 */
@AIName("artifact")
public class ArtifactAI2 extends NpcAI2 {

    private final Map<Integer, ItemUseObserver> observers = new HashMap<>();

    @Override
    protected SiegeSpawnTemplate getSpawnTemplate() {
        return (SiegeSpawnTemplate) super.getSpawnTemplate();
    }

    @Override
    protected void handleDialogStart(final Player player) {
        ArtifactLocation loc = SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId());
        AI2Actions.addRequest(this, player, 160028, new AI2Request() {

            @Override
            public void acceptRequest(Creature requester, Player responder) {

                AI2Actions.addRequest(ArtifactAI2.this, player, 160016, new AI2Request() {

                    @Override
                    public void acceptRequest(Creature requester, Player responder) {
                        onActivate(responder);
                    }

                }, DescId.of(2 * 716570 + 1), SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId()).getTemplate().getActivation()
                    .getCount());

            }

        }, loc);
    }

    @Override
    protected void handleDialogFinish(Player player) {
    }

    public void onActivate(final Player player) {
        final ArtifactLocation loc = SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId());

        // Get Skill id, item, count and target defined for each artifact.
        ArtifactActivation activation = loc.getTemplate().getActivation();
        int skillId = activation.getSkillId();
        final int itemId = activation.getItemId();
        final int count = activation.getCount();
        final SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);

        if (skillTemplate == null) {
            LoggerFactory.getLogger(ArtifactAI2.class).error("No skill template for artifact effect id : " + skillId);
            return;
        }

        if (loc.getCoolDown() > 0 || !loc.getStatus().equals(ArtifactStatus.IDLE)) {
            player.sendPck(SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ARTIFACT_OUT_OF_ORDER);
            return;
        }

        if (loc.getLegionId() != 0) {
            if (!player.isLegionMember() || player.getLegion().getLegionId() != loc.getLegionId()
                || !player.getLegionMember().hasRights(LegionPermissionsMask.ARTIFACT)) {
                player.sendPck(SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ARTIFACT_HAVE_NO_AUTHORITY);
                return;
            }
        }

        if (player.getInventory().getItemCountByItemId(itemId) < count) {
            return;
        }

        LoggerFactory.getLogger(ArtifactAI2.class).debug("Artifact {} actived by {}.", getSpawnTemplate().getSiegeId(), player.getName());
        if (!loc.getStatus().equals(ArtifactStatus.IDLE)) {
            return;
        }
        // Brodcast start activation.
        final SM_SYSTEM_MESSAGE startMessage = SM_SYSTEM_MESSAGE.STR_ARTIFACT_CASTING(player.getRace().getRaceDescriptionId(), player.getName(),
            DescId.of(skillTemplate.getNameId()));
        loc.setStatus(ArtifactStatus.ACTIVATION);
        final SM_ABYSS_ARTIFACT_INFO3 artifactInfo = new SM_ABYSS_ARTIFACT_INFO3(loc.getLocationId());
        player.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                player.sendPck(startMessage);
                player.sendPck(artifactInfo);
            }

        });

        player.sendPck(new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 10000, 1));
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, getObjectId()), true);

        ItemUseObserver observer = new ItemUseObserver() {

            @Override
            public void abort() {
                player.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
                PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
                player.sendPck(new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 10000, 0));
                final SM_SYSTEM_MESSAGE message = SM_SYSTEM_MESSAGE.STR_ARTIFACT_CANCELED(loc.getRace().getDescId(),
                    DescId.of(skillTemplate.getNameId()));
                loc.setStatus(ArtifactStatus.IDLE);
                final SM_ABYSS_ARTIFACT_INFO3 artifactInfo = new SM_ABYSS_ARTIFACT_INFO3(loc.getLocationId());
                getOwner().getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

                    @Override
                    public void visit(Player player) {
                        player.sendPck(message);
                        player.sendPck(artifactInfo);
                    }

                });
            }

        };
        observers.put(player.getObjectId(), observer);
        player.getObserveController().attach(observer);
        player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                ItemUseObserver observer = observers.remove(player.getObjectId());
                if (observer != null) {
                    player.getObserveController().removeObserver(observer);
                }

                player.sendPck(new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 10000, 0));
                PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
                if (!player.getInventory().decreaseByItemId(itemId, count)) {
                    return;
                }
                final SM_SYSTEM_MESSAGE message = SM_SYSTEM_MESSAGE.STR_ARTIFACT_CORE_CASTING(loc.getRace().getDescId(),
                    DescId.of(skillTemplate.getNameId()));
                loc.setStatus(ArtifactStatus.CASTING);
                final SM_ABYSS_ARTIFACT_INFO3 artifactInfo = new SM_ABYSS_ARTIFACT_INFO3(loc.getLocationId());

                player.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

                    @Override
                    public void visit(Player player) {
                        player.sendPck(message);
                        player.sendPck(artifactInfo);
                    }

                });

                loc.setLastActivation(System.currentTimeMillis());
                if (loc.getTemplate().getRepeatCount() == 1) {
                    ThreadPoolManager.getInstance().schedule(new ArtifactUseSkill(loc, player, skillTemplate), 13000);
                } else {
                    final ScheduledFuture<?> s = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ArtifactUseSkill(loc, player, skillTemplate), 13000,
                        loc.getTemplate().getRepeatInterval() * 1000);
                    ThreadPoolManager.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            s.cancel(true);
                            loc.setStatus(ArtifactStatus.IDLE);
                        }

                    }, 13000 + (loc.getTemplate().getRepeatInterval() * loc.getTemplate().getRepeatCount() * 1000));
                }

            }

        }, 10000));
    }

    class ArtifactUseSkill implements Runnable {

        private final ArtifactLocation artifact;
        private final Player player;
        private final SkillTemplate skill;
        private int runCount = 1;
        private final SM_ABYSS_ARTIFACT_INFO3 pkt;
        private final SM_SYSTEM_MESSAGE message;

        /**
         * @param artifact
         */
        private ArtifactUseSkill(ArtifactLocation artifact, Player activator, SkillTemplate skill) {
            this.artifact = artifact;
            player = activator;
            this.skill = skill;
            pkt = new SM_ABYSS_ARTIFACT_INFO3(artifact.getLocationId());
            message = SM_SYSTEM_MESSAGE.STR_ARTIFACT_FIRE(activator.getRace().getRaceDescriptionId(), player.getName(), DescId.of(skill.getNameId()));
        }

        @Override
        public void run() {
            if (artifact.getTemplate().getRepeatCount() < runCount) {
                return;
            }

            final boolean start = (runCount == 1);
            final boolean end = (runCount == artifact.getTemplate().getRepeatCount());

            runCount++;
            player.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

                @Override
                public void visit(Player player) {
                    if (start) {
                        player.sendPck(message);
                    }
                    artifact.setStatus(ArtifactStatus.ACTIVATED);
                    player.sendPck(pkt);
                    if (end) {
                        artifact.setStatus(ArtifactStatus.IDLE);
                        player.sendPck(pkt);
                    }
                }

            });
            boolean pc = skill.getProperties().getTargetSpecies() == TargetSpeciesAttribute.PC;
            for (Creature creature : artifact.getCreatures().values()) {
                if (creature.getActingCreature() instanceof Player || (creature instanceof SiegeNpc && !pc)) {
                    switch (skill.getProperties().getTargetRelation()) {
                        case FRIEND:
                            if (player.isEnemy(creature)) {
                                continue;
                            }
                            break;
                        case ENEMY:
                            if (!player.isEnemy(creature)) {
                                continue;
                            }
                            break;
                    }
                    AI2Actions.applyEffect(ArtifactAI2.this, skill, creature);
                }
            }
        }

    }

}
