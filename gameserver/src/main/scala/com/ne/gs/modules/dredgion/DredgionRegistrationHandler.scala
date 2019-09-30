/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.modules.dredgion

import com.ne.commons.services.CronService
import com.ne.gs.configs.main.DredgionConfig
import com.ne.gs.dataholders.DataManager
import com.ne.gs.instance.InstanceEngine
import com.ne.gs.model.Race
import com.ne.gs.world.World
import com.ne.gs.model.gameobjects.player.Player
import com.ne.gs.model.geometry.Point3D
import com.ne.gs.modules.instanceentry.InstanceEntryManager
import com.ne.gs.modules.instanceentry.InstanceEntryManager._
import com.ne.gs.network.aion.AionServerPacket
import com.ne.gs.network.aion.serverpackets.SM_AUTO_GROUP
import com.ne.gs.services.instance.InstanceService
import com.ne.gs.services.ZaebalaScala
import com.ne.gs.services.teleport.TeleportService
import com.ne.gs.spawnengine.SpawnEngine
import com.ne.gs.utils.Implicits._
import com.ne.gs.utils.ThreadPoolManager
import com.ne.gs.world.{ WorldMapInstanceFactory, World }
import java.io.FileWriter
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import scala.collection.mutable

/**
 * @author hex1r0
 */
object DredgionRegistrationHandler {

  class Baranath(override val entryHandler: ChannelEntryHandler) extends DredgionRegistrationHandler(entryHandler) {
    def dungeonType: DredgionType = DredgionType.BARANATH

    protected def checkLevel(players: Traversable[Player]): Boolean = {
      for (p: Player <- players) {
        if (!(p.getLevel >= 46 && p.getLevel <= 50)) {
          return false
        }
      }
      true
    }
  }

  class Chantra(override val entryHandler: ChannelEntryHandler) extends DredgionRegistrationHandler(entryHandler) {
    def dungeonType: DredgionType = DredgionType.CHANTRA

    protected def checkLevel(players: Traversable[Player]): Boolean = {
      for (p: Player <- players) {
        if (!(p.getLevel >= 51 && p.getLevel <= 55)) {
          return false
        }
      }
      true
    }
  }

  class Terath(override val entryHandler: ChannelEntryHandler) extends DredgionRegistrationHandler(entryHandler) {
    def dungeonType: DredgionType = DredgionType.TERATH

    protected def checkLevel(players: Traversable[Player]): Boolean = {
      for (p: Player <- players) {
        if (!(p.getLevel >= 56 && p.getLevel <= 60)) {
          return false
        }
      }
      true
    }
  }

  class DredgionChannelEntryHandler extends ChannelEntryHandler {
    private val _log = LoggerFactory.getLogger(getClass)

    private val POINT_1 = new Point3D(570.46f, 166.89f, 432.28f)
    private val POINT_2 = new Point3D(400.74f, 166.71f, 432.29f)

    def createChannel(mapId: Int): Int = {
      val map = World.getInstance.getWorldMap(mapId)
      val nextInstanceId = map.getNextInstanceId
      val ch = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId)
      map.addInstance(nextInstanceId, ch)
      SpawnEngine.spawnInstance(mapId, ch.getInstanceId, /*difficultId*/ 0)
      InstanceEngine.getInstance.onInstanceCreate(ch)

      _log.info("Created new channel id=" + ch.getInstanceId)

      ch.getInstanceId
    }

    def enterChannel(mapId: Int, chId: Int, player: Player) {
      val loc = if (player.getRace == Race.ELYOS) {
        POINT_1
      } else {
        POINT_2
      }
      TeleportService.teleportTo(player, mapId, chId, loc.getX, loc.getY, loc.getZ)
      val instanceCooldownRate = InstanceService.getInstanceRate(player, mapId)
      val instanceCoolTime = DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(mapId)
      player.getPortalCooldownList.addPortalCooldown(mapId, instanceCoolTime * 60 * 1000 / instanceCooldownRate)
    }
  }

}

abstract class DredgionRegistrationHandler(val entryHandler: ChannelEntryHandler) extends DungeonRegistrationHandler {
  private val _log = LoggerFactory.getLogger(getClass)

  private val _waitingRequests = new mutable.TreeSet()(new Order)
  private val _waitingTeams = new mutable.ArrayBuffer[Team]
  private val _readyTeams = new mutable.ArrayBuffer[(Team, Team)]
  private val _dungeons = new mutable.HashMap[(Team, Team), Dungeon]

  var open = false

  def init() {
    schedule()
  }

  def schedule() {
    val res = CronService.getInstance().schedule(() => {
      _log.info(s"Dredgion ${dungeonType.mapId} is open for ${DredgionConfig.DREDGION_TIMER} min")
      InstanceEntryManager ! new InstanceEntryManager.TriggerDungeon(dungeonType.id, true)

      scheduleCloseAndRestart()
    }, DredgionConfig.DREDGION_TIMES)

    _log.info("Dredgion scheduled at " + res.getDate)
  }

  def scheduleCloseAndRestart() {
    ThreadPoolManager.getInstance().schedule(() => {
      _log.info(s"Dredgion ${dungeonType.mapId} is closed")
      InstanceEntryManager ! new InstanceEntryManager.TriggerDungeon(dungeonType.id, false)
      InstanceEntryManager ! new InstanceEntryManager.ScheduleDungeon(dungeonType.id)
    }, TimeUnit.MINUTES.toMillis(DredgionConfig.DREDGION_TIMER))
  }

  def dungeonType: DredgionType

  def onPlayerEnterWorld(player: Player) {
    if (!DredgionConfig.DREDGION2_ENABLE) {
      return
    }

    if (player.isInInstance) {
      return
    }

    if (!open) {
      return
    }

    if (!checkLevel(Seq(player))) {
      return
    }

    def seekTeam(t: Team) = t.requests.find(r => isRequestor(r, player)).isDefined

    if (_waitingRequests.find(r => isRequestor(r, player) && !r.isInTeam).isDefined) {
      player.sendPck(new SM_AUTO_GROUP(dungeonType.id, 1))
      return
    }

    if (_readyTeams.find(tt => seekTeam(tt._1) || seekTeam(tt._2)).isDefined) {
      player.sendPck(new SM_AUTO_GROUP(dungeonType.id, 4))
      return
    }

    player.sendPck(new SM_AUTO_GROUP(dungeonType.id, 6))
  }

  def register(player: Player, entryType: EntryType): EntryResult = {
    if (!DredgionConfig.DREDGION2_ENABLE) {
      return new EntryResult(false)
    }

    if (!open) {
      return new EntryResult(false, "Error: Invalid time.")
    }

    if (_waitingRequests.find(r => r.playerUid == player.getObjectId).isDefined) {
      // close old dialog & send new dialog
      player.sendPck(new SM_AUTO_GROUP(dungeonType.id, 5))
      player.sendPck(new SM_AUTO_GROUP(dungeonType.id, 1))
      _log.warn("player=" + player.getName)
      return new EntryResult(false, "Error: Already registered.")
    }

    val participants =
      if (entryType == EntryType.GROUP_ENTRY) {
        import scala.collection.JavaConversions._
        player.getPlayerGroup2.getMembers.toSeq
      } else {
        Seq(player)
      }

    if (!checkLevel(participants)) {
      return new EntryResult(false, "Error: Invalid player level.")
    }

    if (!checkCooldown(participants)) {
      return new EntryResult(false, "Error: Cooldown.") // FIXME retail msg?
    }

    val requests = {
      if (entryType == EntryType.GROUP_ENTRY) {
        val memberReqs = participants.
          filterNot(p => player.getObjectId.equals(p.getObjectId)).
          map(p => new Request(p.getObjectId, EntryType.GROUP_ENTRY_MEMBER, p.getRace))

        val r = new Request(player.getObjectId, entryType, player.getRace, new mutable.HashSet ++= memberReqs)
        memberReqs ++ Seq(r)
      } else {
        Seq(new Request(player.getObjectId, entryType, player.getRace))
      }
    }

    _log.info("Registration: players=" + requests)

    // remove any requests that are already in queue (group members)
    requests.foreach(r => cancel(r.playerUid))

    _waitingRequests ++= requests

    participants.foreach(p => p.sendPck(new SM_AUTO_GROUP(dungeonType.id, 1)))
    participants.foreach(p => p.sendPck(new SM_AUTO_GROUP(dungeonType.id, 6, true))) // hide "icon"
    val it = World.getInstance().getAllPlayers().iterator
    while (it.hasNext) {
      val pl = it.next();
      val ely = _waitingRequests.filter(r => r.race == Race.ELYOS).size;
      val asmo = _waitingRequests.filter(r => r.race == Race.ASMODIANS).size;
      ZaebalaScala.showAnnounce(ely, asmo, player, pl, dungeonType);
    }
    new EntryResult(true)
  }

  def requestEnter(player: Player) {
    def seekTeam(t: Team) = t.requests.find(r => isRequestor(r, player)).isDefined

    // TODO check time to enter (2 min)
    _log.info("Entering: 1. player=" + player.getObjectId)
    val teams = _readyTeams.find(tt => seekTeam(tt._1) || seekTeam(tt._2))
    if (teams.isDefined) {
      val tt = teams.get
      // remove request from queue & mark request as in dungeon
      _waitingRequests --= _waitingRequests.filter(r => {
        if (isRequestor(r, player)) {
          _log.info("Entering: 2. player=" + player.getObjectId)
          r.isInDungeon = true
          true
        } else {
          false
        }
      })

      // if whole team is in dungeon clear team from queue
      if ((tt._1.requests ++ tt._2.requests).forall(r => r.isInDungeon)) {
        _readyTeams -= tt
        _log.info("Entering: 3. team=" + tt)
      }

      player.sendPck(new SM_AUTO_GROUP(dungeonType.id, 5))
      player.sendPck(new SM_AUTO_GROUP(dungeonType.id, 6, true))

      val dungeon = _dungeons.getOrElseUpdate(tt, new Dungeon(entryHandler.createChannel(dungeonType.mapId)))
      entryHandler.enterChannel(dungeonType.mapId, dungeon.channelId, player)
    } else {
      player.sendPck(new SM_AUTO_GROUP(dungeonType.id, 5))
      player.sendPck(new SM_AUTO_GROUP(dungeonType.id, 0))
    }
  }

  def cancel(playerUid: Int) {
    _log.info("Cancelation: 1. player=" + playerUid)

    // remove player from all possible requests / teams / team pairs
    def clear(c: mutable.Set[Request]) {
      val rq = c.filter(r => r.playerUid == playerUid)
      rq.foreach(r => clear(r.attachedRequests))
      c --= rq

      _log.info("Cancelation: 2. requests=" + rq)
    }

    clear(_waitingRequests)
    _waitingTeams.foreach(t => clear(t.requests))
    _readyTeams.foreach(p => {
      clear(p._1.requests)
      clear(p._2.requests)
    })

    // remove empty teams
    val teams = _waitingTeams.filter(t => t.requests.size == 0)
    _waitingTeams --= teams
    _log.info("Cancelation: 3. empty teams=" + teams)

    // remove empty team pairs
    val pairs = _readyTeams.filter(t => t._1.requests.size == 0 || t._2.requests.size == 0)
    _readyTeams --= pairs
    _log.info("Cancelation: 3. empty pairs=" + pairs)

    def unwrap(t: Team) = t.requests.map(r => r.playerUid)

    val ids = (teams.map(t => unwrap(t)).flatMap(x => x)
      ++ pairs.map(tt => unwrap(tt._1) ++ unwrap(tt._2)).flatMap(x => x)
      ++ Seq(playerUid))

    // hide "Confirm Entry" dialog for removed players
    ids.foreach(id => sendPck(id, new SM_AUTO_GROUP(dungeonType.id, 5)))

    // show "icon"
    ids.foreach(id => sendPck(id, new SM_AUTO_GROUP(dungeonType.id, 6)))
  }

  def handle() {
    //val min30ms = TimeUnit.MINUTES.toMillis(30)
    // TODO send packet if canceled to let player know about cancelation
    //_requests.retain((k, v) => XSystem.millis() - v.requestTimeMs < min30ms)

    handleGroupEntry()
    handleNewEntry()
    handleQuickEntry()
  }

  def handleNewEntry() {
    def nextUncompleteTeam(race: Race): Team = {
      for (t: Team <- _waitingTeams.filter(t => t.race == race)) {
        if (!t.ready) {
          return t
        }
      }

      newTeam(race)
    }

    remainingRequests(EntryType.NEW_ENTRY).map(r => {
      val team = nextUncompleteTeam(r.race)
      team.requests += r
      r.isInTeam = true
      checkAndMarkTeam(team)
    })

    sendEntryMsg(groupTeams)
  }

  def handleQuickEntry() {
    // TODO
  }

  def handleGroupEntry() {
    remainingRequests(EntryType.GROUP_ENTRY).map(r => {
      val team = newTeam(r.race)
      team.requests ++= (Seq(r) ++ r.attachedRequests)
      team.requests.foreach(r0 => r0.isInTeam = true)
      checkAndMarkTeam(team)
    })
    sendEntryMsg(groupTeams)
  }

  private def newTeam(r: Race) = {
    val t = new Team(r)
    _waitingTeams += t
    t
  }

  private def checkCooldown(players: Traversable[Player]): Boolean = {
    for (p: Player <- players) {
      if (p.getPortalCooldownList.isPortalUseDisabled(dungeonType.mapId)) {
        return false
      }
    }
    true
  }

  protected def checkLevel(players: Traversable[Player]): Boolean

  def dumpStats() {
    val fw = new FileWriter("dredgion-stats.txt")

    fw.write("WaitingRequests:\n")
    _waitingRequests.foreach(x => fw.write("> " + x.toString + "\n"))

    fw.write("WaitingTeams:\n")
    _waitingTeams.foreach(x => fw.write("> " + x.toString + "\n"))

    fw.write("ReadyTeams:\n")
    _readyTeams.foreach(x => fw.write("> " + x.toString + "\n"))

    fw.write("Instances:\n")
    _dungeons.foreach(x => fw.write("> " + x.toString + "\n"))

    fw.close()
  }

  private def sendPck(playerUid: Int, pck: AionServerPacket) {
    val p = World.getInstance().findPlayer(playerUid)
    if (p != null) {
      p.sendPck(pck)
    }
  }

  private def sendEntryMsg(teams: Traversable[(Team, Team)]) {
    def f(t: Team) {
      t.requests.map(r =>
        Option(World.getInstance().findPlayer(r.playerUid))).foreach(o =>
        o.map(p =>
          p.sendPck(new SM_AUTO_GROUP(dungeonType.id, 4))))
    }
    teams.foreach(x => {
      f(x._1)
      f(x._2)
    })
  }

  private def isRequestor(r: Request, p: Player) = r.playerUid == p.getObjectId

  private def remainingRequests(et: EntryType) = _waitingRequests.filter(r => r.entryType == et && !r.isInTeam)

  private def checkAndMarkTeam(team: Team) {
    if (team.requests.size >= DredgionConfig.DREDGION_MIN_TEAM_SIZE) {
      team.ready = true
    }
  }

  private def groupTeams = {
    val elyos = _waitingTeams.filter(t => t.race == Race.ELYOS && t.ready)
    val asmos = _waitingTeams.filter(t => t.race == Race.ASMODIANS && t.ready)

    val ei = elyos.iterator
    val ai = asmos.iterator

    val teamPairs = new mutable.ArrayBuffer[(Team, Team)]
    while (ei.hasNext && ai.hasNext) {
      val et = ei.next()
      val at = ai.next()

      teamPairs += ((et, at))

      _waitingTeams -= et
      _waitingTeams -= at
    }

    _readyTeams ++= teamPairs
    teamPairs
  }
}

object Request {
  private var _uidGen = Int.MinValue

  private def nextId = {
    _uidGen += 1
    _uidGen
  }
}

class Request(
  override val playerUid: Int,
  override val entryType: EntryType,
  val race: Race,
  val attachedRequests: mutable.Set[Request]
)
  extends EntryRequest(playerUid, entryType) {
  val uid = Request.nextId

  def this(requestor: Int, entryType: EntryType, race: Race) = this(requestor, entryType, race, new mutable.HashSet)

  override def toString: String =
    s"Request [" +
      s"playerUid=$playerUid, " +
      s"entryType=$entryType, " +
      s"race=$race, " +
      s"isInTeam=$isInTeam, " +
      s"isInDungeon=$isInDungeon, " +
      s"attachedRequests=$attachedRequests" +
      "]"
}

class Team(val race: Race) {
  val requests = new mutable.HashSet[Request]
  var ready = false

  override def toString: String =
    s"Team [" +
      s"requests=$requests, " +
      s"ready=$ready, " +
      "]"
}

class Order extends Ordering[Request] {
  def compare(x: Request, y: Request): Int = x.uid compare y.uid
}

class Dungeon(val channelId: Int) {}
