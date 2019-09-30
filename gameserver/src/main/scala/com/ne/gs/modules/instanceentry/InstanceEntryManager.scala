/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.modules.instanceentry

import akka.actor.{Actor, ActorRef, Props}
import com.ne.commons.Sys
import com.ne.commons.util.Enum.EnumValue
import com.ne.commons.util.StatActor.{Message, ActorData}
import com.ne.commons.util.{StatActor, PlayAi}
import com.ne.gs.model.gameobjects.player.Player
import com.ne.gs.network.aion.serverpackets.SM_AUTO_GROUP
import com.ne.gs.utils.Implicits._
import com.ne.gs.world.World
import org.slf4j.LoggerFactory
import scala.collection.mutable

/**
 * @author hex1r0
 */
object InstanceEntryManager {
  private val _process = PlayAi.system.actorOf(Props(new Process), "InstanceEntryManager")
  private val _log = LoggerFactory.getLogger(InstanceEntryManager.getClass)

  def !(msg: Any)(implicit sender: ActorRef = Actor.noSender) {
    _process ! (msg)
  }

  /**
   * Java API
   * @param msg
   */
  def tell(msg: Any) {
    this.!(msg)
  }

  class Data extends ActorData {
    val handlers = new mutable.HashMap[Class[_], DungeonRegistrationHandler]
    val requests = new mutable.HashMap[Int, DungeonRegistrationHandler]
  }

  private class Process extends StatActor {
    val data: ActorData = new Data
  }

  // --

  def findHandler(data: Data, dungeonId: Int) = data.handlers.values.find(h => h.dungeonType.id == dungeonId)

  // --

  class AddHandler(entryHandler: DungeonRegistrationHandler) extends Msg {
    def execute(data: Data) {
      data.handlers.put(entryHandler.getClass, entryHandler)
    }
  }

  /**
   * Java API
   * @param player
   * @param dungeonId
   * @param entryTypeId
   */
  class RequestRegister2(player: Player, dungeonId: java.lang.Byte, entryTypeId: java.lang.Byte)
    extends RequestRegister(player, dungeonId.intValue(), EntryType.of(entryTypeId.intValue()).get) {}

  class RequestRegister(player: Player, dungeonId: Int, entryType: EntryType) extends Msg {
    val FAILED = "RequestEntry failed."

    def execute(data: Data) {
      val o = findHandler(data, dungeonId)
      if (o.isEmpty) {
        _log.warn(s"$FAILED Missing dungeonId=$dungeonId")
        return
      }

      val handler = o.get

      if (!check(player)) {
        return
      }

      entryType match {
        case EntryType.GROUP_ENTRY =>
          val party = player.getPlayerGroup2
          if (party == null || !party.isLeader(player)) {
            player.sendMsg(s"Error: Only leader can apply.")
            return
          }

          import collection.JavaConversions._
          for (m: Player <- party.getMembers.toSeq) {
            if (!check(player)) {
              return
            }
          }
        case _ =>
      }

      val r = handler.register(player, entryType)
      val msg = "[player=%s, dungeonId=%s, entryType=%s msg=%s]".
        format(player.getObjectId, dungeonId, entryType, r.msg)

      if (r.res) {
        handler.handle()
      } else {
        player.sendMsg(r.msg)
        _log.warn(s"$FAILED $msg")
      }
    }

    private def check(player: Player) = {
      if (player.isInInstance) {
        player.sendMsg(s"Error: You are already in instance.")
        false
      }

      if (player.isInPrison) {
        player.sendMsg(s"Error: You are in prison.")
        false
      }

      true
    }
  }

  /**
   * Java API
   * @param player
   * @param dungeonId
   * @param entryTypeId
   */
  class RequestEnter2(player: Player, dungeonId: java.lang.Byte, entryTypeId: java.lang.Byte)
    extends RequestEnter(player, dungeonId.intValue(), EntryType.of(entryTypeId.intValue()).get) {}

  class RequestEnter(player: Player, dungeonId: Int, entryType: EntryType) extends Msg {
    def execute(data: Data) {
      findHandler(data, dungeonId).foreach(_.requestEnter(player))
    }
  }

  class PlayerEnterWorld(player: Player) extends Msg {
    def execute(data: Data) {
      data.handlers.values.foreach(_.onPlayerEnterWorld(player))
    }
  }

  class PlayerLeaveInstance(player: Player) extends Msg {
    def execute(data: Data) {
      //data.requests.remove(player.getObjectId)
      //_log.error("PlayerLeaveInstance TODO")
    }
  }

  class CancelEntry(player: Player, dungeonId: Int) extends Msg {
    def execute(data: Data) {
      findHandler(data, dungeonId).foreach(_.cancel(player.getObjectId))
    }
  }

  // --

  class ScheduleDungeon(val dungeonId: Int) extends Msg {
    def execute(data: Data) {
      findHandler(data, dungeonId).foreach(_.schedule())
    }
  }

  class TriggerDungeon(val dungeonId: Int, val open: Boolean) extends Msg {
    def execute(data: Data) {
      findHandler(data, dungeonId).foreach {
        h =>
          h.open = open
          open match {
            // show "icon"
            case true => World.getInstance().doOnAllPlayers((p: Player) => h.onPlayerEnterWorld(p))
            // hide "icon"
            case false =>
              World.getInstance().doOnAllPlayers((p: Player) => p.sendPck(new SM_AUTO_GROUP(h.dungeonType.id, 6, true)))
          }
      }
    }
  }

  // --

  class Init extends Msg {
    def execute(data: Data) {
      data.handlers.values.foreach(h => h.init())
    }
  }

  // --

  class DumpStats extends Msg {
    def execute(data: Data) {
      data.handlers.values.foreach(h => h.dumpStats())
    }
  }

  abstract class Msg extends Message[Data] {}

  // --

  trait DungeonRegistrationHandler {
    var open: Boolean

    def init()

    def schedule()

    def onPlayerEnterWorld(player: Player)

    def handle()

    def register(player: Player, entryType: EntryType): EntryResult

    def requestEnter(player: Player)

    def cancel(playerUid: Int)

    def dungeonType: DungeonType

    def dumpStats()
  }

  trait ChannelEntryHandler {
    def createChannel(mapId: Int): Int

    def enterChannel(mapId: Int, chId: Int, player: Player)
  }

  object ChannelEntryHandler {
    val DUMMY = new ChannelEntryHandler {
      var c = 0

      def createChannel(mapId: Int): Int = {
        c += 1
        LoggerFactory.getLogger(ChannelEntryHandler.getClass).info(s"Created channel id=$c")
        c
      }

      def enterChannel(mapId: Int, chId: Int, player: Player) {
        LoggerFactory.getLogger(ChannelEntryHandler.getClass).info(s"player=$player enteded channel id=$chId")
      }
    }
  }

  trait DungeonType {
    def id: Int
  }

  class EntryRequest(val playerUid: Int, val entryType: EntryType) {
    val requestTimeMs = Sys.millis()
    var isInTeam = false
    var isInDungeon = false
  }

  class EntryResult(val res: Boolean, val msg: String) {
    def this(res: Boolean) = this(res, "")

    override def toString: String = res + msg
  }

  //case class EntryRequest(val objectUid: Int)

  // --

  object EntryType extends com.ne.commons.util.Enum[EntryType] {
    val NEW_ENTRY = ::(0)
    val QUICK_ENTRY = ::(1)
    val GROUP_ENTRY = ::(2)
    val GROUP_ENTRY_MEMBER = ::(-1) // internal usage only

    def of(id: Int) = {
      values.find(e => e.id == id)
    }

    private def ::(id: Int) = create(EntryType(id))
  }

  case class EntryType(id: Int) extends EnumValue

  private object EntryProgress {
    val WAIT = 0
    val WAIT_FOR_OPPONENT = 1
  }

  // FIXME fix constants
  private object EntryAction {
    val SHOW_ENTRY_OPTIONS = 0
    val SHOW_LIST = 1
    val UNK = 4
    val CLOSE_LIST = 5
    val SHOW_BUTTON = 6
    val ENTRY_FAILED = 7
  }

}
