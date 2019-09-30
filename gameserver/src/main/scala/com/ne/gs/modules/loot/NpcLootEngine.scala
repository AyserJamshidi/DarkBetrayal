/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.modules.loot

import akka.actor.Props
import com.ne.commons.util.StatActor.ActorData
import com.ne.commons.util.{StatActor, PlayAi}
import com.ne.commons.utils.{XMath, Rnd}
import com.ne.gs.ai2.event.AIEventType
import com.ne.gs.configs.main.DropConfig
import com.ne.gs.model.drop.{NpcDrop, Drop, DropGroup, DropItem}
import com.ne.gs.model.gameobjects.player.Player
import com.ne.gs.model.gameobjects.state.CreatureState
import com.ne.gs.model.gameobjects.{Npc, Item}
import com.ne.gs.model.stats.container.StatEnum
import com.ne.gs.model.team2.common.legacy.{LootRuleType, LootGroupRules}
import com.ne.gs.model.{EmotionType, Race}
import com.ne.gs.modules.housing.House.HouseType
import com.ne.gs.modules.housing.HouseInfo
import com.ne.gs.network.aion.serverpackets.{SM_LOOT_STATE, SM_EMOTION, SM_LOOT_ITEM_LIST}
import com.ne.gs.services.item.ItemPacketService.ItemUpdateType
import com.ne.gs.services.item.ItemService.ItemAddPredicate
import com.ne.gs.services.item.{ItemService, ItemPacketService}
import com.ne.gs.services.{RespawnService, QuestService}
import com.ne.gs.utils.PacketSendUtility
import gnu.trove.map.hash.THashMap
import java.util.concurrent.TimeUnit
import scala.collection.mutable.ArrayBuffer
import collection.JavaConversions._
import com.ne.gs.utils.stats.LootLevelDiffReducer

/**
 * TODO implement loot visibility only to certain players (ROUNDROBIN, FREEFORALL, etc)
 * @author hex1r0
 */
object NpcLootEngine {
  val self = PlayAi.system.actorOf(Props(new StatActor {
    val data: ActorData = new NpcLootEngineData
  }), "NpcLootEngine")

  val dropPredicate = new ItemAddPredicate {
    override def getUpdateType(input: Item): ItemPacketService.ItemUpdateType = {
      if (input.getItemTemplate.isKinah) {
        ItemUpdateType.INC_KINAH_LOOT
      } else {
        ItemUpdateType.INC_LOOT
      }
    }
  }
  private val DECAY_WITHOUT_LOOT_MS = TimeUnit.SECONDS.toMillis(90)
  private val DECAY_WITH_LOOT_MS = TimeUnit.MINUTES.toMillis(5)

  /**
   * Makes NPC to loot items from it's loot list
   *
   * @param npc
   * @param player
   * @param highestLevel
   * @param members
   */
  def doDrop(npc: Npc, player: Player, highestLevel: Int, members: java.util.Collection[Player]) {
    doDrop(npc, player, highestLevel, members.toSeq)
  }

  /**
   * Makes NPC to loot items from it's loot list
   *
   * @param npc
   * @param player
   * @param highestLevel
   * @param members
   */
  def doDrop(npc: Npc, player: Player, highestLevel: Int, members: Seq[Player]) {
    self ! new RegisterLoot(npc, player, highestLevel, members)
  }

  /**
   * Shows NPC loot list for player & aquires lock until closed
   *
   * @param player
   * @param npcUid
   */
  def showLootList(player: Player, npcUid: Int) {
    self ! new ShowLootList(player, npcUid)
  }

  /**
   * Hides NPC loot list for player & releases lock
   *
   * @param player
   * @param npcUid
   */
  def hideLootList(player: Player, npcUid: Int) {
    self ! new HideLootList(player, npcUid)
  }

  /**
   * Loot (pick) one item from NPC loot list
   *
   * @param player
   * @param npcUid
   * @param index
   */
  def doLootItem(player: Player, npcUid: Int, index: Int) {
    self ! new LootItem(player, npcUid, index)
  }

  /**
   * Builds NPC loot item list
   *
   * @param npc dead NPC
   * @param player last effector
   * @param highestLevel
   * @param members effectors
   * @return <tt>null</tt> if NPC does not have loot or NpcLootList with droppped items
   */
  def buildLootList(npc: Npc, player: Player, highestLevel: Int, members: Seq[Player]): NpcLootList = {
    val npcLoot = npc.getObjectTemplate.getNpcDrop
    if (npcLoot == null) {
      return null
    }

    // [1] calculate loot candidates
    val (effector, lootCandidates) = buildLootCandidates(player, members)
    val lootList = new NpcLootList(npc.getObjectId)
    lootList.candidateIds = lootCandidates
    // set players from group being able to loot
    if (effector.isInGroup2) {
      lootList.inRangeIds = members.map(p => p.getObjectId)
      lootList.groupSize = members.size
    }

    // [2] calculate loot modifier
    val mod = calcMod(effector, npc, highestLevel)

    // [3] calculate loot items from quests
    if (!effector.isInAlliance2) {
      val questLootItems = new collection.mutable.HashSet[DropItem]()
      QuestService.getQuestDrop(questLootItems, 0, npc, members, effector)
      lootList.items = lootList.items ++ questLootItems
    }

    // [4] calculate loot items from npc loot
    lootList.items = calcLootItems(npcLoot, mod, effector.getRace, members)

    // [5] TODO apply filters
    // ...

    // [6] build item indexes
    lootList.items.view.zipWithIndex.foreach {
      case (d, i) => d.setIndex(i)
    }

    lootList
  }

  def buildLootCandidates(player: Player, members: Seq[Player]) = {
    val inGroup = player.isInGroup2 || player.isInAlliance2
    if (inGroup) {
      buildGroupLootCandidates(player, members)
    } else {
      (player, Seq(player.getObjectId))
    }
  }

  def buildGroupLootCandidates(player: Player, members: Seq[Player]): (Player, Seq[Integer]) = {
    val rules: LootGroupRules = player.getLootGroupRules
    rules.getLootRule match {
      case LootRuleType.ROUNDROBIN =>
        if (members.size > rules.getNrRoundRobin) {
          rules.setNrRoundRobin(rules.getNrRoundRobin + 1)
        } else {
          rules.setNrRoundRobin(1)
        }

        for (p <- members; i <- 0 to rules.getNrRoundRobin) {
          if (i == rules.getNrRoundRobin) {
            return (p, Seq(p.getObjectId))
          }
        }
        (player, Seq())
      case LootRuleType.FREEFORALL =>
        (player, members.map(p => p.getObjectId))
      case LootRuleType.LEADER =>
        val leader: Player = if (player.isInGroup2) {
          player.getPlayerGroup2.getLeaderObject
        } else {
          player.getPlayerAlliance2.getLeaderObject
        }

        (leader, Seq(leader.getObjectId))
    }
  }

  def calcMod(player: Player, npc: Npc, highestLevel: Int) = {
    var lootChance = 100
    val isChest = npc.getAi2.getName == "chest"
    if (!DropConfig.DISABLE_DROP_REDUCTION) {
      if (isChest && npc.getLevel != 1 || !isChest) {
        lootChance = LootLevelDiffReducer.of(npc.getLevel - highestLevel); // reduce chance depending on level
      }
    }

    var boost = npc.getGameStats.getStat(StatEnum.BOOST_DROP_RATE, 100).getCurrent / 100f

    if (player.getCommonData.getCurrentReposteEnergy > 0)
      boost += 0.1f

    if (player.getCommonData.getCurrentSalvationPercent > 0)
      boost += 0.05f

    if (HouseInfo.of(player).typeIs(HouseType.PALACE))
      boost += 0.05f

    boost += player.getGameStats.getStat(StatEnum.BOOST_DROP_RATE, 100).getCurrent / 100f - 1f

    player.getRates.getDropRate * boost * lootChance / 100f
  }

  /**
   * First filters loot for specified race then starts group loot calculations
   *
   * @param npcLoot
   * @param mod
   * @param race
   * @param members
   * @return
   */
  def calcLootItems(npcLoot: NpcDrop, mod: Float, race: Race, members: Seq[Player]): Seq[DropItem] = {
    npcLoot.getDropGroup
      .filter(g => g.getRace == Race.PC_ALL || g.getRace == race)
      .map(g => calcLootGroup(g, mod, race, members))
      .flatMap(x => x)
  }

  def calcLootGroup(lootGroup: DropGroup, mod: Float, race: Race, members: Seq[Player]): Seq[DropItem] = {
    if (DropConfig.EXPERIMENTAL_MODIFIER > 0) {
      val lootItems = lootGroup.getDrop
      val maxCount = lootItems.size()
      val count = XMath.limit(1, maxCount / 100f * DropConfig.EXPERIMENTAL_MODIFIER, maxCount)
      val pool = new ArrayBuffer[Drop] ++= lootItems

      return (1 to count).map(d => calcLootItem(Rnd.take(pool), mod, members)).flatMap(x => x)
    }
    Seq()
  }

  def calcLootGroup2(lootGroup: DropGroup, mod: Float, race: Race, members: Seq[Player]): Seq[DropItem] = {
    lootGroup.getDrop.map(d => calcLootItem(d, mod, members)).flatMap(x => x)
  }

  def calcLootItem(loot: Drop, mod: Float, members: Seq[Player]): Seq[DropItem] = {
    var chance = loot.getChance
    if (!loot.isNoReduction && chance < 100f) {
      chance *= mod
    }

    if (Rnd.chance(chance)) {
      if (loot.isEachMember && !members.isEmpty) {
        return members.map(m => {
          val i = new DropItem(loot)
          i.calculateCount()
          i.setPlayerObjId(m.getObjectId)
          i.setWinningPlayer(m)
          i.isDistributeItem(true)
          i
        })
      } else {
        val i = new DropItem(loot)
        i.calculateCount()
        return Seq(i)
      }
    }
    Seq()
  }

  protected class NpcLootEngineData extends ActorData {
    val allLoot = new THashMap[Int, NpcLootList]
  }

  protected class NpcLootList(val npcUid: Int) {
    var lootPlayerUid: Int = 0 // player uid is used as a lock
    var candidateIds: Seq[Integer] = Seq()
    var inRangeIds: Seq[Integer] = Seq()
    var groupSize: Int = 0
    var items: Seq[DropItem] = Seq()
  }

  // -- messages

  abstract class Msg extends StatActor.Message[NpcLootEngineData]

  protected class RegisterLoot(npc: Npc,
                               player: Player,
                               highestLevel: Int,
                               members: Seq[Player]) extends Msg {
    val lootList = buildLootList(npc, player, highestLevel, members)

    def execute(data: NpcLootEngineData) {
      // unexisting loot
      if (lootList == null) {
        RespawnService.decayNow(npc)
        return
      }

      if (!lootList.items.isEmpty) {
        data.allLoot.put(lootList.npcUid, lootList)

        // FIXME send to members instead of broadcast
        PacketSendUtility.broadcastPacket(player, SM_LOOT_STATE.doHasLoot(lootList.npcUid), true)

        if (npc.getPosition.isInstanceMap) {
          npc.getPosition.getWorldMapInstance.getInstanceHandler.onDropRegistered(npc)
        }
        npc.getAi2.onGeneralEvent(AIEventType.DROP_REGISTERED)
        RespawnService.scheduleDecayTask(npc, DECAY_WITH_LOOT_MS)
      } else {
        RespawnService.scheduleDecayTask(npc, DECAY_WITHOUT_LOOT_MS)
      }
    }
  }

  protected class ShowLootList(player: Player, npcUid: Int) extends Msg {
    def execute(data: NpcLootEngineData) {
      val lootList = data.allLoot.get(npcUid)
      if (lootList == null) {
        return
      } // unexisting loot

      lootList.lootPlayerUid = player.getObjectId // acquire loot list

      player.unsetState(CreatureState.ACTIVE)
      player.setState(CreatureState.LOOTING)
      player.setLootingNpcOid(npcUid)

      player.sendPck(new SM_LOOT_ITEM_LIST(npcUid, lootList.items))
      player.sendPck(SM_LOOT_STATE.showList(npcUid))

      PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, npcUid), true)
    }
  }

  def doEndLoot(player: Player, npcUid: Int) {
    player.unsetState(CreatureState.LOOTING)
    player.setState(CreatureState.ACTIVE)
    player.setLootingNpcOid(0)

    PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_LOOT, 0, npcUid), true)
  }

  protected class HideLootList(player: Player, npcUid: Int) extends Msg {
    def execute(data: NpcLootEngineData) {
      val lootList = data.allLoot.get(npcUid)
      if (lootList == null) {
        return
      } // unexisting loot

      lootList.lootPlayerUid = 0 // release loot list

      doEndLoot(player, npcUid)
      player.sendPck(SM_LOOT_STATE.hideList(npcUid))
    }
  }

  protected class LootItem(player: Player, npcUid: Int, index: Int) extends Msg {
    def execute(data: NpcLootEngineData) {
      val lootList = data.allLoot.get(npcUid)
      if (lootList == null) {
        return
      } // unexisting loot
      if (lootList.lootPlayerUid != player.getObjectId) {
        return
      } // check loot status

      // TODO check access to loot
      def withIndex(x: DropItem) = x.getIndex == index
      lootList.items.find(x => withIndex(x)).map(o => {
        val itemId = o.getDropTemplate.getItemId
        val leftCount = ItemService.addItem(player, itemId, o.getCount, dropPredicate)

        if (leftCount <= 0) {
          lootList.items = lootList.items.filterNot(x => withIndex(x))
        } else {
          o.setCount(leftCount)
        }

        if (lootList.items.size == 0) {
          doEndLoot(player, npcUid)
          player.sendPck(SM_LOOT_STATE.hideList(npcUid))
          player.sendPck(SM_LOOT_STATE.doLacksLoot(npcUid))

          RespawnService.decayNow(npcUid)
        } else {
          player.sendPck(new SM_LOOT_ITEM_LIST(npcUid, lootList.items))
          //player.sendPck(SM_LOOT_STATE.showList(npcUid))
        }
      })
    }
  }
}


object Calculator {

}