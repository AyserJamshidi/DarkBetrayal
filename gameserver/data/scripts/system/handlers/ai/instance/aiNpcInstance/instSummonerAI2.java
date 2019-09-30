package ai.instance.aiNpcInstance;

import ai.AggressiveNpcAI2;

import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldPosition;

import java.util.concurrent.atomic.AtomicBoolean;

@AIName("inst_summoner")
public class instSummonerAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean isHome = new AtomicBoolean(true);

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
            if (!isAlreadyDead()) {
                sendMsg(npcMsg());
                WorldPosition p = getPosition();
                attackPlayer((Npc) spawn(getSumNpc(),  p.getX() + Rnd.get(-4, 4), p.getY() + Rnd.get(-4, 4), p.getZ(), (byte)0));
            }
        }
    }

    private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }

    private int npcMsg() {
        switch (getNpcId()) {
            case 213505:
            case 213506:
            case 213501:
            case 213446:
            case 213447:
                return 341261;
            default:
                return 0;
        }
    }

    private int getSumNpc() {
        switch (getNpcId()) {
            case 213505:
            case 213506:
                return 213455;
            case 213501:
                return 213436;
            case 213446:
                return 213452;
            case 213447:
                return 213451;
            default:
                return 0;
        }
    }

    private void attackPlayer(final Npc npc) {
        com.ne.gs.utils.ThreadPoolManager.getInstance().schedule(new Runnable() {

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

    private void despawn(int npcId) {
        for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
            npc.getController().onDelete();
        }
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        despawn(213451);
        despawn(213452);
        despawn(213436);
        despawn(213455);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        despawn(213451);
        despawn(213452);
        despawn(213436);
        despawn(213455);
    }
}
