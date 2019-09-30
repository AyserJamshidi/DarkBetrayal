package ai.instance.darkPoeta;

import ai.AggressiveNpcAI2;

import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.PacketSendUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.EmotionType;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;

@AIName("tahabatapyrelord")
public class TahabataPyrelordAI2 extends AggressiveNpcAI2
{
	protected List<Integer> percents = new ArrayList<Integer>();
	private AtomicBoolean isStart = new AtomicBoolean(false);
	private boolean lock1 = false;
	private boolean lock2 = false;
	private Future<?> useSkillTask;
	private Future<?> firstTask;
	private Future<?> secondTask;
	private Future<?> thirdTask;
	private Future<?> lastTask;

	@Override
	protected void handleSpawned() {
		addPercent();
		lock2 = false;
		super.handleSpawned();
	}

	@Override
	protected void handleAttack(Creature creature){
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
		if (isStart.compareAndSet(false, true)) {
			checkTimer();
		}

	}
	
	private void checkTimer() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1400257);
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
								NpcShoutsService.getInstance().sendMsg(getOwner(), 1400258);
							}
						}
					}, 2000);
				}
			}
		}, 600000);
	}

	private synchronized void checkPercentage(int hpPercentage)
	{
		for (Integer percent : percents)
		{
			if (hpPercentage <= percent)
			{
				switch(percent)
				{
					case 100:
						useFirstSkillTree();
						break;
					case 80:
					case 60:
						cancelTask();
						firstSkill();
						break;
					case 30:
						cancelTask();
						lock1 = false;
						useLastSkillTree();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void useFirstSkillTree()
	{
		useSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() 
		{

			@Override
			public void run()
			{
				firstSkill();
			}
		}, 20000);
	}

	private void firstSkill()
	{
		if(!lock2){
			int hpPercent = getLifeStats().getHpPercentage();
			if(hpPercent > 80)
			{
				EmoteManager.emoteStopAttacking(getOwner());
				useSkill(18217);//Strike Down with Anger
			}
			else if(80 >= hpPercent && hpPercent > 60)
			{
				EmoteManager.emoteStopAttacking(getOwner());
				useSkill(18232);//Explosion of Wrath
			}
			else if(60 >= hpPercent && hpPercent > 30)
			{
				EmoteManager.emoteStopAttacking(getOwner());
				useSkill(18236);//Eruption of Power
				sp(281258);//Faithful Subordinate(stone gargoyle)
			}
			skillTwo();
		}
	}

	private void skillTwo()
	{
		if(!lock2)
			firstTask = ThreadPoolManager.getInstance().schedule(new Runnable()
			{

				@Override
				public void run() 
				{
					int hpPercent = getLifeStats().getHpPercentage();
					if(hpPercent > 80)
					{
						EmoteManager.emoteStopAttacking(getOwner());
						useSkill(18229);//Dragon's Fireball
					}
					else if(80 >= hpPercent && hpPercent > 60)
					{
						EmoteManager.emoteStopAttacking(getOwner());
						useSkill(18225);//Dragon Flame
					}
					else if(60 >= hpPercent && hpPercent > 30)
					{
						EmoteManager.emoteStopAttacking(getOwner());
						useSkill(18232);//Explosion of Wrath
					}
					skillThree();
				}
			}, 5000);
	}

	private void skillThree()
	{
		if(!lock2)
			secondTask = ThreadPoolManager.getInstance().schedule(new Runnable() 
			{

				@Override
				public void run() 
				{
					int hpPercent = getLifeStats().getHpPercentage();
					if(hpPercent > 80 || (60 >= hpPercent && hpPercent > 30))
					{
						EmoteManager.emoteStopAttacking(getOwner());
						useSkill(18225);//Dragon Flame
					}
					else if(80 >= hpPercent && hpPercent > 60)
					{
						EmoteManager.emoteStopAttacking(getOwner());
						useSkill(18231);//Mighty Thrust
					}
					thirdTask = ThreadPoolManager.getInstance().schedule(new Runnable() 
					{

						@Override
						public void run() 
						{
							firstSkill();
						}
					}, 31000);
				}
			}, 4000);
	}

	private void useLastSkillTree()
	{
		if(!lock2){
			EmoteManager.emoteStopAttacking(getOwner());
			getOwner().clearAttackedCount();
			useSkill(18239);//Soul Petrify
			firstTask = ThreadPoolManager.getInstance().schedule(new Runnable() 
			{

				@Override
				public void run() 
				{
					EmoteManager.emoteStopAttacking(getOwner());
					useSkill(18243);//Destroy Frozen Soul
					lastTask = ThreadPoolManager.getInstance().schedule(new Runnable() 
					{

						@Override
						public void run() 
						{
							useSkill(18243);//Destroy Frozen Soul
							lastSkillTree2();
						}
					}, 3000);
				}
			}, 4000);
		}
	}

	private void lastSkillTree2()
	{
		if(!lock2){
			secondTask = ThreadPoolManager.getInstance().schedule(new Runnable() 
			{

				@Override
				public void run() 
				{
					useSkill(18232);//Explosion of Wrath
					if(!lock1)
					{
						EmoteManager.emoteStopAttacking(getOwner());
						lock1 = true;
						lastSkillTree2();
					}
				}
			}, 10000);
			if(lock1)
			{
				thirdTask = ThreadPoolManager.getInstance().schedule(new Runnable() 
				{

					@Override
					public void run() 
					{
						EmoteManager.emoteStopAttacking(getOwner());
						useSkill(18241);//Powerful Flame
						sp(281259);//Faithful Subordinate(Dragon)
						lastTask = ThreadPoolManager.getInstance().schedule(new Runnable() 
						{

							@Override
							public void run() 
							{
								lock1 = false;
								useLastSkillTree();//new skilltree repeat
							}
						}, 27000);
					}
				}, 13000);
			}
		}
	}

	private void cancelTask()
	{
		if(useSkillTask != null && !useSkillTask.isDone())
			useSkillTask.cancel(true);
		else if(firstTask != null && !firstTask.isDone())
			firstTask.cancel(true);
		else if(secondTask != null && !secondTask.isDone())
			secondTask.cancel(true);
		else if(thirdTask != null && !thirdTask.isDone())
			thirdTask.cancel(true);
		else if(lastTask != null && !lastTask.isDone())
			lastTask.cancel(true);
	}

	private void sp(int npcId) {
		if(!lock2){
			if(npcId == 281258)
			{
				attackPlayer((Npc) spawn(npcId,  1191.2714f,  1220.5795f,  144.2901f,  (byte)36));
				attackPlayer((Npc) spawn(npcId,  1188.3695f,  1257.1322f,  139.66028f,  (byte)80));
				attackPlayer((Npc) spawn(npcId,  1177.1423f,  1253.9136f,  140.58705f,  (byte)97));
				attackPlayer((Npc) spawn(npcId,  1163.5889f,  1231.9149f,  145.40042f,  (byte)118));
			}
			else
			{
				attackPlayer((Npc) spawn(npcId,  1182.0021f,  1244.0125f,  142.67587f, (byte)88));
				attackPlayer((Npc) spawn(npcId,  1192.3885f,  1236.5231f,  142.50638f, (byte)68));
				attackPlayer((Npc) spawn(npcId,  1185.647f,  1227.2747f,  144.2261f, (byte)32));
				attackPlayer((Npc) spawn(npcId,  1172.3302f,  1232.5709f,  144.70761f, (byte)12));
			}
		}
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

	private void useSkill(int skillId)
	{
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 50, getTarget()).useSkill();
	}

	private void addPercent() 
	{
		percents.clear();
		Collections.addAll(percents, new Integer[]{100, 80, 60, 30});
	}
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		 npc.getController().onDelete();
	  }
	}

	@Override
	protected void handleBackHome() {
		addPercent();
		cancelTask();
		lock2 = false;
		despawn(281258);
		despawn(281259);
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned() {
		percents.clear();
		cancelTask();
		despawn(281258);
		despawn(281259);
		lock2 = true;
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		percents.clear();
		cancelTask();
		despawn(281258);
		despawn(281259);
		lock2 = true;
		super.handleDied();
	}
}