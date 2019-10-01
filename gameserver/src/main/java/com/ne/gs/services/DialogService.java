/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.services;

import com.ne.gs.configs.administration.AdminConfig;
import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.DialogAction;
import com.ne.gs.model.Race;
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.RequestResponseHandler;
import com.ne.gs.model.team.legion.Legion;
import com.ne.gs.model.team.legion.LegionWarehouse;
import com.ne.gs.model.templates.portal.PortalPath;
import com.ne.gs.model.templates.teleport.TeleportLocation;
import com.ne.gs.model.templates.teleport.TeleporterTemplate;
import com.ne.gs.model.templates.tradelist.TradeListTemplate;
import com.ne.gs.network.aion.serverpackets.*;
import com.ne.gs.questEngine.QuestEngine;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.restrictions.RestrictionsManager;
import com.ne.gs.services.craft.CraftSkillUpdateService;
import com.ne.gs.services.craft.RelinquishCraftStatus;
import com.ne.gs.services.item.ItemChargeService;
import com.ne.gs.services.teleport.PortalService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.services.trade.PricesService;
import com.ne.gs.skillengine.model.SkillTargetSlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author VladimirZ
 */
public final class DialogService {

    private static final Logger log = LoggerFactory.getLogger(DialogService.class);

    public static void onCloseDialog(Npc npc, Player player) {
        // LMFAOOWN figure out what these magic numbers are

        switch (npc.getObjectTemplate().getTitleId()) {
            case 350409:
            case 315073:
                player.sendPck(new SM_DIALOG_WINDOW(npc.getObjectId(), 0));
                Legion legion = player.getLegion();
                if (legion != null) {
                    LegionWarehouse lwh = player.getLegion().getLegionWarehouse();
                    if (lwh.getWhUser() == player.getObjectId()) {
                        lwh.setWhUser(0);
                    }
                }
                break;
        }
    }

    public static void onDialogSelect(int dialogId, final Player player, final Npc npc, int questId, int extendedRewardIndex) {

        QuestEnv env = new QuestEnv(npc, player, questId, dialogId);
        env.setExtendedRewardIndex(extendedRewardIndex);

        if (QuestEngine.getInstance().onDialog(env))
            return;

        int targetObjectId = npc.getObjectId();
        int titleId = npc.getObjectTemplate().getTitleId();
        // LMFAOOWN fix (add titleId and add getTitleId)

        if (player.getAccessLevel() >= 3 && CustomConfig.ENABLE_SHOW_DIALOGID) {
            player.sendMsg("dialogId: " + dialogId);
            player.sendMsg("questId: " + questId);
        }

        switch (Objects.requireNonNull(DialogAction.getActionByDialogId(dialogId))) {
            case BUY: {
                TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
                if (tradeListTemplate == null) {
                    player.sendMsg("Buy list is missing for NPC ID " + npc.getNpcId());
                    break;
                }
                int tradeModifier = tradeListTemplate.getSellPriceRate();
                player.sendPck(new SM_TRADELIST(player, npc, tradeListTemplate,
                        PricesService.getVendorBuyModifier() * tradeModifier / 100));
                break;
            }
            case SELL:
            case TRADE_SELL_LIST: {
                TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getPurchaseTemplate(npc.getNpcId());
                player.sendPck(new SM_SELL_ITEM(targetObjectId, PricesService.getVendorSellModifier(player.getRace())));
                break;
            }
            case OPEN_STIGMA_WINDOW: { // stigma
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 1));
                break;
            }
            case CREATE_LEGION: { // create legion
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 2));
                break;
            }
            case DISPERSE_LEGION: { // disband legion
                LegionService.getInstance().requestDisbandLegion(npc, player);
                break;
            }
            case RECREATE_LEGION: { // recreate legion
                LegionService.getInstance().recreateLegion(npc, player);
                break;
            }
            case DEPOSIT_CHAR_WAREHOUSE: { // warehouse (2.5)
                switch (titleId) {
                    case 315008:
                    case 350417:
                    case 462878:
                    case 0:
                        if (!RestrictionsManager.canUseWarehouse(player))
                            return;
                        player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 26));
                        WarehouseService.sendWarehouseInfo(player, true);
                        break;
                }

                /*
                if (!RestrictionsManager.canUseWarehouse(player)) {
                    return;
                }

                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 26));
                WarehouseService.sendWarehouseInfo(player, true);*/
                break;
            }
            // LMFAOOWN fix this.
            /* digIds:
             * 0: Close
             * 1: Stigma Change Window
             * 2: Create Legion Dialog
             * 3-9: ???
             * 10: Main dialog
             * 11-12: ???
             * 13: Broker Window
             * 14-17: ???
             * 18: Mailbox Window
             * 19: Item Appearance Window
             * 20: Manastone Removal Window
             * 21: Godstone Socketing Window
             * 22-23: ???
             * 24: Close??
             * 25: Legion Warehouse
             * 26: Private Warehouse
             * 29: Armsfusion Window
             * 30: Armsbreaking Window
             * 35: Conditioning Window
             * 36: Relationship Crystal Window
             * 37: ???
             * 38: House Auction Window
             * 39: Maintenance Paying Dialog
             * 40: House Kick Visitor Window
             * 41: Something, but closes too quick
             * 42: Augment Window
             * 43: Village Tasks
             */
            case CHECK_USER_HAS_QUEST_ITEM: {
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 1004));
                /*if (questId != 0) {
                    QuestState qs = player.getQuestStateList().getQuestState(questId);
                    if (qs != null) {
                        if (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD) {
                            if (AdminConfig.QUEST_DIALOG_LOG) {
                                log.info("Error in the quest " + questId + ". No response from " + npc.getNpcId() + " on the step " + qs.getQuestVarById(0));
                            }
                            player.sendMsg("Start or Reward.");
                            if (!"useitem".equals(npc.getAi2().getName())) {
                                player.sendMsg("!UseItem");
                                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 10));
                            } else {
                                player.sendMsg("UseItem.");
                                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 0));
                            }
                        }
                    } else {
                        if (AdminConfig.QUEST_DIALOG_LOG) {
                            log.info("Quest " + questId + " is not implemented.");
                        }
                        player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 10));
                    }
                } else {
                    player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 0));
                }*/
                break;
            }
            case OPEN_VENDOR: { // Consign trade?? npc karinerk, koorunerk (2.5)
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 13));
                break;
            }
            case RECOVERY: { // soul healing (2.5)
                final long expLost = player.getCommonData().getExpRecoverable();
                if (expLost == 0) {
                    player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
                    player.getCommonData().setDeathCount(0);
                }
                double factor = (expLost < 1000000 ? 0.25 - (0.00000015 * expLost) : 0.1);
                final int price = (int) (expLost * factor);

                RequestResponseHandler responseHandler = new RequestResponseHandler(npc) {

                    @Override
                    public void acceptRequest(Creature requester, Player responder) {
                        if (player.getInventory().getKinah() >= price) {
                            player.sendPck(SM_SYSTEM_MESSAGE.STR_GET_EXP2(expLost));
                            player.sendPck(SM_SYSTEM_MESSAGE.STR_SUCCESS_RECOVER_EXPERIENCE);
                            player.getCommonData().resetRecoverableExp();
                            player.getInventory().decreaseKinah(price);
                            player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
                            player.getCommonData().setDeathCount(0);
                        } else {
                            player.sendPck(SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(price));
                        }
                    }

                    @Override
                    public void denyRequest(Creature requester, Player responder) {
                        // no message
                    }
                };
                if (player.getCommonData().getExpRecoverable() > 0) {
                    boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_RECOVER_EXPERIENCE, responseHandler);
                    if (result) {
                        player.sendPck(new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_RECOVER_EXPERIENCE, 0, 0, String.valueOf(price)));
                    }
                } else {
                    player.sendPck(SM_SYSTEM_MESSAGE.STR_DONOT_HAVE_RECOVER_EXPERIENCE);
                }
                break;
            }
            case ENTER_HOME_PVP_ARENA: { // (2.5)
                switch (npc.getNpcId()) {
                    case 204089: // pvp arena in pandaemonium.
                        TeleportService.teleportTo(player, 120010000, 1, 984f, 1543f, 222.1f);
                        break;
                    case 203764: // pvp arena in sanctum.
                        TeleportService.teleportTo(player, 110010000, 1, 1462.5f, 1326.1f, 564.1f);
                        break;
                    case 203981:
                        TeleportService.teleportTo(player, 210020000, 1, 439.3f, 422.2f, 274.3f);
                        break;
                }
                break;
            }
            case LEAVE_HOME_PVP_ARENA: { // (2.5)
                switch (npc.getNpcId()) {
                    case 204087:
                        TeleportService.teleportTo(player, 120010000, 1, 1005.1f, 1528.9f, 222.1f);
                        break;
                    case 203875:
                        TeleportService.teleportTo(player, 110010000, 1, 1470.3f, 1343.5f, 563.7f);
                        break;
                    case 203982:
                        TeleportService.teleportTo(player, 210020000, 1, 446.2f, 431.1f, 274.5f);
                        break;
                }
                break;
            }
            case GIVE_ITEM_PROC: { // Godstone socketing (2.5)
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 21));
                break;
            }
            case REMOVE_MANASTONE: { // remove mana stone (2.5)
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 20));
                break;
            }
            case CHANGE_ITEM_SKIN: { // modify appearance (2.5)
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 19));
                break;
            }
            case AIRLINE_SERVICE: { // flight and teleport (2.5)
                if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
                    int level = player.getLevel();
                    if (level < 9) {
                        player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 27));
                    } else {
                        TeleportService.showMap(player, targetObjectId, npc.getNpcId());
                    }
                } else {
                    switch (npc.getNpcId()) {
                        // LMFAOOWN make sure this fix is actually good.
                        case 203194:
                        case 203679: {
                            QuestState qs = player.getQuestStateList().getQuestState((player.getRace() == Race.ELYOS) ? 1006 : 2008);
                            if (qs == null || qs.getStatus() != QuestStatus.COMPLETE) {
                                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 27));
                            } else {
                                TeleportService.showMap(player, targetObjectId, npc.getNpcId());
                            }
                            break;
                        }
                        /*case 203194: {
                            if (player.getRace() == Race.ELYOS) {
                                QuestState qs = player.getQuestStateList().getQuestState(1006);
                                if (qs == null || qs.getStatus() != QuestStatus.COMPLETE) {
                                    player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 27));
                                } else {
                                    TeleportService.showMap(player, targetObjectId, npc.getNpcId());
                                }
                            }
                            break;
                        }
                        case 203679: {
                            if (player.getRace() == Race.ASMODIANS) {
                                QuestState qs = player.getQuestStateList().getQuestState(2008);
                                if (qs == null || qs.getStatus() != QuestStatus.COMPLETE) {
                                    player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 27));
                                } else {
                                    TeleportService.showMap(player, targetObjectId, npc.getNpcId());
                                }
                            }
                            break;
                        }*/
                        default: {
                            TeleportService.showMap(player, targetObjectId, npc.getNpcId());
                        }
                    }
                }
                break;
            }
            case GATHER_SKILL_LEVELUP: // improve extraction (2.5)
            case COMBINE_SKILL_LEVELUP: { // learn tailoring armor smithing etc. (2.5)
                CraftSkillUpdateService.getInstance().learnSkill(player, npc);
                break;
            }
            case EXTEND_INVENTORY: { // expand cube (2.5)
                CubeExpandService.expandCube(player, npc);
                break;
            }
            case EXTEND_CHAR_WAREHOUSE: { // (2.5)
                WarehouseService.expandWarehouse(player, npc);
                break;
            }
            case OPEN_LEGION_WAREHOUSE: { // legion warehouse (2.5)
                if (npc.getObjectTemplate().getTitleId() == 350409) {
                    LegionService.getInstance().openLegionWarehouse(player, npc);
                }
                break;
            }
            case CLOSE_LEGION_WAREHOUSE: { // WTF??? Quest dialog packet (2.5)
                player.sendMsg("You found CLOSE_LEGION_WAREHOUSE.");
                break;
            }
            case CRAFT: { // (2.5)
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 28));
                break;
            }
            case OPEN_PERSONAL_WAREHOUSE: { // coin reward (2.5)
                player.sendMsg("You found OPEN_PERSONAL_WAREHOUSE.");
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 3, 0));
                // PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "This feature is not available yet",
                // ChatType.GOLDEN_YELLOW));
                break;
            }
            case EDIT_CHARACTER:
            case EDIT_GENDER: { // (2.5)
                byte changesex = 0; // 0 plastic surgery, 1 gender switch
                byte check_ticket = 2; // 2 no ticket, 1 have ticket
                if (dialogId == DialogAction.EDIT_GENDER.id()) {
                    // Gender Switch
                    changesex = 1;
                    if (player.getInventory().getItemCountByItemId(169660000) > 0 || player.getInventory().getItemCountByItemId(169660001) > 0) {
                        check_ticket = 1;
                    }
                } else // Plastic Surgery
                    if (player.getInventory().getItemCountByItemId(169650000) > 0 || player.getInventory().getItemCountByItemId(169650001) > 0) {
                        check_ticket = 1;
                    }
                player.sendPck(new SM_PLASTIC_SURGERY(player, check_ticket, changesex));
                player.setEditMode(true);
                break;
            }
            case MATCH_MAKER: // dredgion
                // FIXME
                /*if (DredgionService2.getInstance().isDredgionAvialable()) {
                    AutoGroupsType agt = AutoGroupsType.getAutoGroup(npc.getNpcId());
                    if (agt != null) {
                        player.sendPck(new SM_AUTO_GROUP(agt.getInstanceMaskId()));
                    } else {
                        player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 0));
                    }
                } else {
                    player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 1011));
                }*/
                break;
            case INSTANCE_ENTRY: { // (2.5)
                player.sendMsg("INSTANCE_ENTRY called.");
                log.info("INSTANCE_ENTRY called.");
                break;
            }
            case COMPOUND_WEAPON: { // armsfusion (2.5)
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 29));
                break;
            }
            case DECOMPOUND_WEAPON: { // armsbreaking (2.5)
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 30));
                break;
            }
            case FACTION_JOIN: { // join npcFaction (2.5)
                player.getNpcFactions().enterGuild(npc);
                break;
            }
            case FACTION_LEAVE: { // leave npcFaction (2.5)
                player.getNpcFactions().leaveNpcFaction(npc);
                break;
            }
            case BUY_AGAIN: { // repurchase (2.5)
                player.sendPck(new SM_REPURCHASE(player, npc.getObjectId()));
                break;
            }
            case PET_ADOPT: { // adopt pet (2.5)
                player.sendPck(new SM_PET(6));
                break;
            }
            case PET_ABANDON: { // abandon pet (2.5)
                player.sendPck(new SM_PET(7));
                break;
            }
            case HOUSING_BUILD: { // housing build
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 32));
                break;
            }
            case HOUSING_DESTRUCT: { // housing destruct
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 33));
                break;
            }
            case CHARGE_ITEM_SINGLE: { // condition an individual item
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 35));
                break;
            }
            case CHARGE_ITEM_MULTI: { // condition all equiped items
                ItemChargeService.startChargingEquippedItems(player, targetObjectId, 1);
                break;
            }
            case TRADE_IN: {
                TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getTradeInListTemplate(npc.getNpcId());
                if (tradeListTemplate == null) {
                    player.sendMsg("Buy list is missing!!");
                    break;
                }
                player.sendPck(new SM_TRADE_IN_LIST(npc, tradeListTemplate, 100));
                break;
            }
            case GIVEUP_CRAFT_EXPERT: {
                // LMFAOOWN make sure this doesn't need getInstance.
                //RelinquishCraftStatus.getInstance();
                RelinquishCraftStatus.relinquishExpertStatus(player, npc);
                break;
            }
            case GIVEUP_CRAFT_MASTER: {
                // LMFAOOWN make sure this doesn't need getInstance.
                //RelinquishCraftStatus.getInstance();
                RelinquishCraftStatus.relinquishMasterStatus(player, npc);
                break;
            }
            case HOUSING_PERSONAL_AUCTION: {
                // LMFAOOWN fix this.
                /*if ((player.getBuildingOwnerStates() & PlayerHouseOwnerFlags.BIDDING_ALLOWED.getId()) == 0) {
                    player.sendPck(SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST((player.getRace() == Race.ELYOS) ? 18802 : 28802));
                }*/
                player.sendMsg("You find HOUSING_PERSONAL_AUCTION.");
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 38));
                break;
            }
            case FUNC_PET_H_ADOPT: { // adopt pet (2.5)
                player.sendPck(new SM_PET(16));
                break;
            }
            case FUNC_PET_H_ABANDON: { // surrender pet (2.5)
                player.sendPck(new SM_PET(17));
                break;
            }
            case CHARGE_ITEM_SINGLE2: { // augmenting an individual item
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 42));
                break;
            }
            case CHARGE_ITEM_MULTI2: { // augmenting all equiped items
                ItemChargeService.startChargingEquippedItems(player, targetObjectId, 2);
                break;
            }
            case HOUSING_RECREATE_PERSONAL_INS: { // recreate personal house instance (studio)
                // TODO
                // LMFAOOWN fix
                player.sendMsg("You found HOUSING_RECREATE_PERSONAL_INS.");
                //HousingService.getInstance().recreatePlayerStudio(player);
                break;
            }
            case TOWN_CHALLENGE: { // town improvement
                player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 43));
                break;
            }
            case SETPRO1:
            case SETPRO2:
            case SETPRO3: {
                if (questId == 0) { // generic npc reply (most are teleporters)
                    TeleporterTemplate template = DataManager.TELEPORTER_DATA.getTeleporterTemplateByNpcId(npc.getNpcId());
                    PortalPath portalPath = DataManager.PORTAL2_DATA.getPortalDialog(npc.getNpcId(), dialogId, player.getRace());
                    if (portalPath != null) {
                        PortalService.port(portalPath, player, targetObjectId);
                    } else if (template != null) {
                        TeleportLocation loc = template.getTeleLocIdData().getTelelocations().get(0);
                        if (loc != null) {
                            TeleportService.teleport(template, loc.getLocId(), player, npc,
                                    npc.getAi2().getName().equals("general") ? TeleportAnimation.JUMP_AIMATION : TeleportAnimation.BEAM_ANIMATION);
                        }
                    }
                } else {
                    player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
                }
                break;
            }
            default: {
                if (questId > 0) {
                    if (dialogId == 18 && player.getInventory().isFull()) {
                        player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, 0));
                    } else {
                        player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
                    }
                } else {
                    player.sendPck(new SM_DIALOG_WINDOW(targetObjectId, dialogId));
                }
                break;
            }
        }
    }
}
