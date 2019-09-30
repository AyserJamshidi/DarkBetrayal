package com.ne.gs.modules

import akka.actor.{ TypedProps, TypedActor }
import com.ne.commons.util.PlayAi
import com.ne.gs.configs.main.CustomConfig
import com.ne.gs.model.events.{ PlayerLeftGame, PlayerTeleported }
import com.ne.gs.model.gameobjects.player.{ DeniedStatus, RequestResponseHandler, Player }
import com.ne.gs.model.gameobjects.state.CreatureState
import com.ne.gs.model.gameobjects.{ AionObject, Creature }
import com.ne.gs.model.handlers.PlayerDieHandler
import com.ne.gs.model.summons.{ SummonMode, UnsummonType }
import com.ne.gs.network.aion.serverpackets.{ SM_DUEL, SM_SYSTEM_MESSAGE, SM_QUESTION_WINDOW }
import com.ne.gs.services.summons.SummonsService
import com.ne.gs.skillengine.model.SkillTargetSlot
import com.ne.gs.utils.Implicits._
import com.ne.gs.utils.ThreadPoolManager
import com.ne.gs.world.World
import java.lang
import java.util.concurrent.{ Future, TimeUnit }
import scala.collection.mutable
import com.ne.gs.skillengine.model.TransformType;

/**
 * @author hex1r0
 */
class DuelRecorderImpl extends DuelRecorder {
  private val _data = new mutable.HashMap[Int, Int]()
  private var time: Future[_] = null

  override def request(actor: Player, opponentId: Int) {
    val actorId = uid(actor)
    val opponent = actor.getKnownList.getObject(opponentId) match {
      case p: Player => p
      case _ => null
    }

    if (!CustomConfig.INSTANCE_DUEL_ENABLE && actor.isInInstance) {
      return
    }

    if (opponent == null || actor == opponent) {
      actor.sendPck(SM_SYSTEM_MESSAGE.STR_DUEL_PARTNER_INVALID(""))
      return
    }

    if (actor.isInState(CreatureState.DUELING)) {
      actor.sendPck(SM_SYSTEM_MESSAGE.STR_DUEL_YOU_ARE_IN_DUEL_ALREADY)
      return
    }

    if (opponent.isInState(CreatureState.DUELING)) {
      actor.sendPck(SM_SYSTEM_MESSAGE.STR_DUEL_PARTNER_IN_DUEL_ALREADY(opponent.getName))
      return
    }

    if (opponent.getPlayerSettings.isInDeniedStatus(DeniedStatus.DUEL)) {
      actor.sendPck(SM_SYSTEM_MESSAGE.STR_MSG_REJECTED_DUEL(opponent.getName))
      return
    }

    if (_data.contains(actorId) || _data.contains(opponentId)) {
      actor.sendPck(SM_SYSTEM_MESSAGE.STR_DUEL_PARTNER_IN_DUEL_ALREADY(opponent.getName))
      return
    }

    //    if (actor.getTransformModel() != null && actor.getTransformModel().getType() == TransformType.AVATAR) {
    //      actor.sendPck(new SM_SYSTEM_MESSAGE(1300091))
    //      return
    //    }

    setState(actorId, opponentId, RegState.THINKING)
    askCancel(actor, opponent)
    askConfirm(actor, opponent)
  }

  override def confirm(actor: Player, opponent: Player) {
    val (uid1, uid2) = uids(actor, opponent)
    if (!stateCheck(uid1, uid2, RegState.THINKING)) {
      removeUid(actor, opponent)
      return
    }

    setState(uid1, uid2, RegState.DUELING)

    actor.setState(CreatureState.DUELING, uid2.asInstanceOf[Integer])
    opponent.setState(CreatureState.DUELING, uid1.asInstanceOf[Integer])

    actor.sendPck(SM_DUEL.start(opponent.getObjectId))
    opponent.sendPck(SM_DUEL.start(actor.getObjectId))

    attachAll(actor)
    attachAll(opponent)

    time = ThreadPoolManager.getInstance().schedule(() => {
      DuelRecorderInstance.get.timeout(actor, opponent)
    }, TimeUnit.MINUTES.toMillis(5))
  }

  override def cancel(actor: Player, opponent: Player) {
    removeUid(actor, opponent)
    actor.sendPck(SM_SYSTEM_MESSAGE.STR_DUEL_WITHDRAW_REQUEST(opponent.getName))
    opponent.sendPck(SM_SYSTEM_MESSAGE.STR_DUEL_REQUESTER_WITHDRAW_REQUEST(actor.getName))
  }

  override def reject(actor: Player, opponent: Player) {
    removeUid(actor, opponent)
    opponent.sendPck(SM_SYSTEM_MESSAGE.STR_DUEL_REJECT_DUEL(actor.getName))
    actor.sendPck(SM_SYSTEM_MESSAGE.STR_DUEL_HE_REJECT_DUEL(opponent.getName))
  }

  override def timeout(player1: Player, player2: Player) {
    if (removeUid(player1).isDefined) {
      player1.unsetState(CreatureState.DUELING)
      detachAll(player1)
      player1.sendPck(SM_DUEL.draw(player2.getName))
    }
    if (removeUid(player2).isDefined) {
      player2.unsetState(CreatureState.DUELING)
      detachAll(player2)
      player2.sendPck(SM_DUEL.draw(player1.getName))
    }
  }

  override def finish(loser: Player, winner: Player) {
    recoverPlayer(loser, limitHp = true)
    recoverPlayer(winner)

    winner.sendPck(SM_DUEL.win(loser.getName))
    loser.sendPck(SM_DUEL.lose(winner.getName))
    time.cancel(true)
  }

  override def abort(loser: Player) {
    val winner = World.getInstance().findPlayer(loser.getStateEnv(CreatureState.DUELING).asInstanceOf[Integer])
    if (winner != null) {
      recoverPlayer(winner)

      winner.sendPck(SM_DUEL.win(loser.getName))
    }

    loser.unsetState(CreatureState.DUELING)
    detachAll(loser)
    removeUid(loser)
  }

  private def recoverPlayer(player: Player, limitHp: Boolean = false) {
    player.getEffectController.removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF)
    player.getController.cancelCurrentSkill()
    if (limitHp) {
      player.getLifeStats.setCurrentHp(player.getLifeStats.getMaxHp / 3)
    } else if (player.getEffectController.getTransformType != TransformType.AVATAR) {
      player.getLifeStats.setCurrentHp(player.getLifeStats.getMaxHp)
    }

    if (player.getSummon != null) {
      SummonsService.doMode(SummonMode.GUARD, player.getSummon, UnsummonType.UNSPECIFIED)
    }
    if (player.getSummonedObj != null) {
      player.getSummonedObj.getController.cancelCurrentSkill()
    }

    player.unsetState(CreatureState.DUELING)

    detachAll(player)

    removeUid(player)
  }

  def attachAll(p: Player) {
    p.getChainer.attach(DIE_HANDLER)
    p.getNotifier.attach(TELEPORT_LISTENER)
    p.getNotifier.attach(LEAVE_GAME_LISTENER)
  }

  def detachAll(p: Player) {
    p.getChainer.detach(DIE_HANDLER)
    p.getNotifier.detach(TELEPORT_LISTENER)
    p.getNotifier.detach(LEAVE_GAME_LISTENER)
  }

  private def askConfirm(actor: Player, opponent: Player) {
    val rrh = new RequestResponseHandler[Player](actor) {
      def acceptRequest(actor: Player, opponent: Player) {
        // opponent pressed "Yes" - I wan't to start duel
        DuelRecorderInstance.get.confirm(actor, opponent)
      }
      def denyRequest(actor: Player, opponent: Player) {
        // opponent pressed "No" - I don't want to start duel
        DuelRecorderInstance.get.reject(actor, opponent)
      }
    }
    val id = SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_REQUEST
    if (opponent.getResponseRequester.putRequest(id, rrh)) {
      opponent.sendPck(new SM_QUESTION_WINDOW(id, 0, 0, actor.getName))
      opponent.sendPck(SM_SYSTEM_MESSAGE.STR_DUEL_REQUESTED(actor.getName))
    }
  }

  private def askCancel(actor: Player, opponent: Player) {
    val rrh = new RequestResponseHandler[Player](opponent) {
      def acceptRequest(opponent: Player, actor: Player) {
        // actor pressed "Yes" - I want to cancel
        DuelRecorderInstance.get.cancel(actor, opponent)
      }
      def denyRequest(opponent: Player, actor: Player) {
        // actor pressed "No" - I don't want to cancel
      }
    }
    val id = SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_WITHDRAW_REQUEST
    if (actor.getResponseRequester.putRequest(id, rrh)) {
      actor.sendPck(new SM_QUESTION_WINDOW(id, 0, 0, opponent.getName))
      actor.sendPck(SM_SYSTEM_MESSAGE.STR_DUEL_REQUEST_TO_PARTNER(opponent.getName))
    }
  }

  private def removeUid(uid: Int): Option[Int] = _data.remove(uid)

  private def removeUid(player: Player): Option[Int] = removeUid(uid(player))

  private def removeUid(player1: Player, player2: Player) {
    removeUid(player1)
    removeUid(player2)
  }

  def stateCheck(uid1: Int, uid2: Int, state: Int) = {
    val s1 = _data.get(uid1)
    val s2 = _data.get(uid2)

    s1.isDefined && s2.isDefined && s1.get == state && s2.get == state
  }

  def setState(uid1: Int, uid2: Int, state: Int) {
    _data.put(uid1, state)
    _data.put(uid2, state)
  }

  private def uids(p1: AionObject, p2: AionObject) = (uid(p1), uid(p2))

  private object RegState {
    val THINKING = 0
    val DUELING = 1
  }

  private val DIE_HANDLER = new PlayerDieHandler {
    def onEvent(e: com.ne.commons.func.tuple.Tuple3[Player, Creature, lang.Boolean]): lang.Boolean = {
      val loser = e._1
      val winner = e._2.getActingCreature match {
        case p: Player => p
        case _ => null
      }

      if (winner == null) {
        if (loser.isInState(CreatureState.DUELING)) {
          DuelRecorderInstance.get.abort(loser)
        }

        return false // continue
      }

      val loserUid: Integer = winner.getStateEnv(CreatureState.DUELING)
      val winnerUid: Integer = loser.getStateEnv(CreatureState.DUELING)

      if (loserUid == null || winnerUid == null || loserUid != loser.getObjectId || winnerUid != winner.getObjectId) {
        return false // continue
      }

      DuelRecorderInstance.get.finish(loser, winner)

      true // break
    }
  }

  private val TELEPORT_LISTENER = new PlayerTeleported {
    def onEvent(p: Player): AnyRef = {
      // synchronous call! required for client to not show strange numbers
      p.sendPck(SM_DUEL.lose(""))
      DuelRecorderInstance.get.abort(p)
      null
    }
  }

  private val LEAVE_GAME_LISTENER = new PlayerLeftGame {
    def onEvent(p: Player): AnyRef = {
      // synchronous call! required for client to not show strange numbers
      p.sendPck(SM_DUEL.lose(""))
      DuelRecorderInstance.get.abort(p)
      null
    }
  }
}

trait DuelRecorder {
  def request(actor: Player, opponentId: Int)
  def reject(actor: Player, opponent: Player)
  def confirm(actor: Player, opponent: Player)
  def cancel(actor: Player, opponent: Player)
  def timeout(actor: Player, opponent: Player)
  def finish(loser: Player, winner: Player)
  def abort(loser: Player)
}

object DuelRecorderInstance {
  val get: DuelRecorder = TypedActor(PlayAi.system).typedActorOf(TypedProps[DuelRecorderImpl]())
}
