package ai.instance.darkPoeta;

import ai.AggressiveNpcAI2;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.commons.utils.Rnd;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.EmotionType;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;

@AIName("calindiflamelord")
public class CalindiFlamelordAI2 extends AggressiveNpcAI2 {

	private List<Integer> percents = new ArrayList<Integer>();
	private AtomicBoolean isStart = new AtomicBoolean(false);

	@Override
	protected void handleSpawned() {
		addPercent();
		super.handleSpawned();
	}
	

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
		if (isStart.compareAndSet(false, true)) {
			checkTimer();
		}

	}

	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				if (percent == 60) {
					EmoteManager.emoteStopAttacking(getOwner());
					SkillEngine.getInstance().getSkill(getOwner(), 18233, 50, getOwner()).useSkill();
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							sp(281267);
						}
					}, 3000);
				}
				else {
					EmoteManager.emoteStopAttacking(getOwner());
					SkillEngine.getInstance().getSkill(getOwner(), 18233, 50, getOwner()).useSkill();
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							sp(281268);
							sp(281268);
						}
					}, 3000);
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void sp(int npcId) {
		if (npcId == 281267) {
			attackPlayer((Npc) spawn(npcId, 1191.2714f, 1220.5795f, 144.2901f, (byte) 36));
			attackPlayer((Npc) spawn(npcId, 1188.3695f, 1257.1322f, 139.66028f, (byte) 80));
			attackPlayer((Npc) spawn(npcId, 1177.1423f, 1253.9136f, 140.58705f, (byte) 97));
			attackPlayer((Npc) spawn(npcId, 1163.5889f, 1231.9149f, 145.40042f, (byte) 118));
		}
		else {
			float direction = Rnd.get(0, 199) / 100f;
			int distance = Rnd.get(0, 2);
			float x1 = (float) (Math.cos(Math.PI * direction) * distance);
			float y1 = (float) (Math.sin(Math.PI * direction) * distance);
			WorldPosition p = getPosition();
			attackPlayer((Npc) spawn(npcId,  p.getX() + x1, p.getY() + y1, p.getZ(), (byte)0));
		}
	}

	private void checkTimer() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1400259);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					EmoteManager.emoteStopAttacking(getOwner());
					SkillEngine.getInstance().getSkill(getOwner(), 19679, 50, getTarget()).useSkill();
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							if (!isAlreadyDead()) {
								getOwner().getController().onDelete();
								NpcShoutsService.getInstance().sendMsg(getOwner(), 1400260);
							}
						}
					}, 2000);
				}
			}
		}, 600000);
	}
	
	private void attackPlayer(final Npc npc) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                npc.setTarget(getTarget());
                ((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
                npc.setState(1);
                npc.getMoveController().moveToTargetObject();
                PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
            }
        }, 1000);
    }

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{60, 30});
	}

	@Override
	protected void handleBackHome() {
		addPercent();
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned() {
		percents.clear();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		percents.clear();
		super.handleDied();
	}
}