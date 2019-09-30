package ai.henchman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import com.ne.commons.network.util.ThreadPoolManager;

import ai.AggressiveNpcAI2;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.EmotionType;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;


@AIName("mastarius")
public class MastariusAI2 extends AggressiveNpcAI2 {

	private boolean think = true;
    private int start = 0;
    private Future<?> skillStart;
    private Future<?> skillEfir;
    protected List<Integer> percents = new ArrayList<>();

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    public void handleCreatureAggro(Creature creature) {
        super.handleCreatureAggro(creature);
		start++;
			if (start == 1) {
				startSkill();
			}
    }

    private void checkPercentage(int hpPercentage) {
        for (Iterator<Integer> it = percents.iterator(); it.hasNext(); ) {
            int percent = it.next();
			int npcId = 282890 + Rnd.get(3);
			int npcId2 = 282890 + Rnd.get(3);
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 75:
						spawnEfir();
                        break;
					case 60:
						spawnHelp(npcId);
						spawnHelp(npcId2);
                        break;
                    case 50:
						sendMsg(1500138);
						NpcShoutsService.getInstance().sendMsg(getOwner(), 1401208);
                        break;
					case 40:
						spawnHelp(npcId);
						spawnHelp(npcId2);
                        break;
					case 20:
						spawnHelp(npcId);
						spawnHelp(npcId2);
                        break;
                    case 10:
						NpcShoutsService.getInstance().sendMsg(getOwner(), 1401209);
                        break;
					case 5:
						sendMsg(1500139);
                        break;
                }
                it.remove();
                break;
            }
        }
    }

    private void startSkill() {
		sendMsg(1500132);
        skillStart = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
            public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				}else {
					switch (Rnd.get(1, 6)) {
							case 1:
									useSkill(18710);
									sendMsg(1500134);
								break;
							case 2:
									useSkill(18711);
									sendMsg(1500135);
								break;
							case 3:
									useSkill(18712);
									sendMsg(1500136);
								break;
							case 4:
									useSkill(18713);
									sendMsg(1500137);
								break;
							case 5:
									useSkill(18986);
									sendMsg(1500135);
								break;
							case 6:
									useSkill(18987);
									sendMsg(1500137);
								break;
					} 
				}
			}
        }, 10000, 16000);
    }
	
	private void spawnHelp(int npcId) {
		useSkill(18709);
		sendMsg(1500133);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
				WorldPosition p = getPosition();
				attackPlayer((Npc) spawn(npcId, p.getX() + Rnd.get(-5, 5), p.getY() + Rnd.get(-5, 5), p.getZ(), (byte) 0));
			}
		}, 3000);
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
	
    private void spawnEfir() {
        skillEfir = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
            public void run() {
				if (isAlreadyDead()) {
					cancelEfir();
				}else {
					despawnNpcs(701353);
					despawnNpcs(701354);
					despawnNpcs(701355);
					despawnNpcs(701356);
					despawnNpcs(701357);
					despawnNpcs(701358);
					despawnNpcs(701359);
					despawnNpcs(701360);
					cancelTask();
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1401206);
					spawn(701353, 1832.12497f, 1961.17401f, 391.9f, (byte) 0);
					spawn(701354, 1840.12497f, 1972.17401f, 391.9f, (byte) 0);
					spawn(701355, 1835.12497f, 1989.17401f, 391.9f, (byte) 0);
					spawn(701356, 1823.12497f, 1995.17401f, 391.9f, (byte) 0);
					spawn(701357, 1809.12497f, 1989.17401f, 391.9f, (byte) 0);
					spawn(701358, 1803.12497f, 1975.17401f, 391.9f, (byte) 0);
					spawn(701359, 1809.12497f, 1963.17401f, 391.9f, (byte) 0);
					spawn(701360, 1820.12497f, 1958.17401f, 391.9f, (byte) 0);
					skillstartAoe();
				}
			}
        }, 0, 280000);
    }

    private void skillstartAoe() {
		think = false;
		EmoteManager.emoteStopAttacking(getOwner());
		startThinkTask();
		SkillEngine.getInstance().getSkill(getOwner(), 18704, 55, getOwner()).useNoAnimationSkill();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			
			@Override
            public void run() {
				startSkill();
				final WorldMapInstance instance = getPosition().getWorldMapInstance();
				final Npc efir = instance.getNpc(701353);
				final Npc efir2 = instance.getNpc(701354);
				final Npc efir3 = instance.getNpc(701355);
				final Npc efir4 = instance.getNpc(701356);
				final Npc efir5 = instance.getNpc(701357);
				final Npc efir6 = instance.getNpc(701358);
				final Npc efir7 = instance.getNpc(701359);
				final Npc efir8 = instance.getNpc(701360);
				if (efir != null || efir2 != null || efir3 != null || efir4 != null || efir5 != null || efir6 != null || efir7 != null || efir8 != null) {
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1401205);
					SkillEngine.getInstance().getSkill(getOwner(), 18705, 55, getOwner()).useNoAnimationSkill();
				}		
			}
        }, 33000);
    }
	
    private void startThinkTask() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    think = true;
                    Creature creature = getAggroList().getMostHated();
                    if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
                        setStateIfNot(AIState.FIGHT);
                        think();
                    } else {
						getMoveController().abortMove();
                        getOwner().getGameStats().renewLastAttackTime();
                        getOwner().getGameStats().renewLastAttackedTime();
                        getOwner().getGameStats().renewLastChangeTargetTime();
                        getOwner().getGameStats().renewLastSkillTime();
                        setStateIfNot(AIState.FIGHT);
						EmoteManager.emoteStartAttacking(getOwner());
						think();
                    }
                }
            }

        }, 34000);
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 55, getTarget()).useSkill();
	}
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
	
	private void despawnNpcs(int npcId) {
        List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(npcId);
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }

    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 75, 60, 50, 40, 20, 10, 5);
    }
	
	private void cancelTask() {
        if (skillStart != null && !skillStart.isCancelled()) {
            skillStart.cancel(true);
        }
    }
	
	private void cancelEfir() {
        if (skillEfir != null && !skillEfir.isCancelled()) {
            skillEfir.cancel(true);
        }
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }

    @Override
    public void handleDied() {
        super.handleDied();
		despawnNpcs(701353);
        despawnNpcs(701354);
		despawnNpcs(701355);
		despawnNpcs(701356);
		despawnNpcs(701357);
		despawnNpcs(701358);
		despawnNpcs(701359);
		despawnNpcs(701360);
		despawnNpcs(282890);
		despawnNpcs(282891);
		despawnNpcs(282892);
		despawnNpcs(282893);
        cancelTask();
        cancelEfir();
		sendMsg(1500140);
    }

    @Override
    public void handleBackHome() {
        super.handleBackHome();
		despawnNpcs(701353);
        despawnNpcs(701354);
		despawnNpcs(701355);
		despawnNpcs(701356);
		despawnNpcs(701357);
		despawnNpcs(701358);
		despawnNpcs(701359);
		despawnNpcs(701360);
		despawnNpcs(282890);
		despawnNpcs(282891);
		despawnNpcs(282892);
		despawnNpcs(282893);
        cancelTask();
        cancelEfir();
		start = 0;
		addPercent();
    }
	
    @Override
    public boolean canThink() {
        return think;
    }
}
