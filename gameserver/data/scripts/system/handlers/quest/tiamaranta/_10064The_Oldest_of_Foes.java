package quest.tiamaranta;

import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.Npc;
import java.util.ArrayList;
import java.util.List;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.SystemMessageId;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.services.instance.InstanceService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;

/**
 * @author Romanz
 *
 */
public class _10064The_Oldest_of_Foes extends QuestHandler {

    private final static int questId = 10064;
    private final static int[] mobs = {800036, 800037, 800038};
    private final static int[] drakans = {218773, 218775, 218774};

    public _10064The_Oldest_of_Foes() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerOnDie(questId);
        qe.registerOnEnterWorld(questId);
        for (int mob : mobs) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
        for (int mob : drakans) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
        qe.registerQuestNpc(218823).addOnKillEvent(questId);
        qe.registerOnMovieEndQuest(752, questId);
        qe.registerOnMovieEndQuest(753, questId);
        qe.registerQuestNpc(800018).addOnTalkEvent(questId);
        qe.registerQuestNpc(800021).addOnTalkEvent(questId);
        qe.registerQuestNpc(800022).addOnTalkEvent(questId);
        qe.registerQuestNpc(205886).addOnTalkEvent(questId);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
        QuestDialog dialog = env.getDialog();

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 800018) {
                switch (dialog) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
                    }
                    case STEP_TO_1: {
                        WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(300400000);
                        InstanceService.registerPlayerWithInstance(newInstance, player);
                        TeleportService.teleportTo(player, 300400000, newInstance.getInstanceId(), 433.27f, 685.31f, 183.4f, (byte) 10, TeleportAnimation.BEAM_ANIMATION);
                        return defaultCloseDialog(env, 0, 1);
                    }
                }
            } else if (targetId == 800022) {
                switch (dialog) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
                    }
                    case STEP_TO_2: {
                        playQuestMovie(env, 752);
                        return defaultCloseDialog(env, 1, 2);
                    }
                }
            } else if (targetId == 800021) {
                switch (dialog) {
                    case START_DIALOG: {
                        if (var == 3) {
                            return sendQuestDialog(env, 1693);
                        }
                    }
                    case STEP_TO_3:
                    case STEP_TO_4: {
                        spawnDrakans(player);
                        return defaultCloseDialog(env, 3, 4);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205886) {
                if (dialog == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 2) {
                QuestService.addNewSpawn(300400000, player.getInstanceId(), 800041, (float) 550.057, (float) 666.941, (float) 183.301, (byte) 40);
                QuestService.addNewSpawn(300400000, player.getInstanceId(), 800041, (float) 542.284, (float) 662.375, (float) 183.301, (byte) 40);
                QuestService.addNewSpawn(300400000, player.getInstanceId(), 800041, (float) 540.240, (float) 665.980, (float) 183.301, (byte) 40);
                QuestService.addNewSpawn(300400000, player.getInstanceId(), 800041, (float) 548.020, (float) 670.540, (float) 183.301, (byte) 40);
                QuestService.addNewSpawn(300400000, player.getInstanceId(), 800041, (float) 538.181, (float) 669.566, (float) 183.301, (byte) 40);
                QuestService.addNewSpawn(300400000, player.getInstanceId(), 800041, (float) 545.943, (float) 674.044, (float) 183.301, (byte) 40);
                QuestService.addNewSpawn(300400000, player.getInstanceId(), 800021, (float) 540.155, (float) 675.154, (float) 183.301, (byte) 40);
                // TODO MOVE
                return defaultOnKillEvent(env, mobs, 2, 3);
            } else if (var == 4) {
                QuestService.addNewSpawn(300400000, player.getInstanceId(), 218823, (float) 527.996, (float) 700.251, (float) 178.393, (byte) 120);
                return defaultOnKillEvent(env, drakans, 4, 5);
            } else if (var == 5) {
                playQuestMovie(env, 753);
                return defaultOnKillEvent(env, 218823, 5, true);
            }
        }
        return false;
    }

    @Override
    public boolean onMovieEndEvent(QuestEnv env, int movieId) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (movieId == 752 && var == 2) {
                spawnLegioners(player);
                return true;
            }
        } else if (movieId == 753 && qs != null && qs.getStatus() == QuestStatus.REWARD) {
            TeleportService.teleportTo(player, 600030000, 305.75726f, 1736.2083f, 295.90472f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
            return true;
        }
        return false;
    }

    @Override
    public boolean onEnterWorldEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (player.getWorldId() == 300400000) {
                if (var == 1) {
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800022, 514.004f, 718.839f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800033, 504.51f, 728.05f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800032, 500.42f, 735.26f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800033, 496.2f, 742.43f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800032, 500.91f, 727.45f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800033, 495.33f, 727.25f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800033, 491.35f, 734.78f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800034, 511.76f, 732.18f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800033, 507.68f, 739.07f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800032, 503.49f, 746.21f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800034, 514.64f, 735.77f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800034, 511.53f, 741.44f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800033, 516.96f, 739.95f, 178.393f, (byte) 0);
                    QuestService.addNewSpawn(300400000, player.getInstanceId(), 800034, 513.035f, 747.17f, 178.393f, (byte) 0);
                    return true;
                }
            } else {
                if (var >= 1) {
                    qs.setQuestVarById(0, 0);
                    updateQuestStatus(env);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onDieEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 1) {
                qs.setQuestVar(0);
                updateQuestStatus(env);
                player.sendPck(new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId)
                        .getName()));
                return true;
            }
        }
        return false;
    }

    private void spawnDrakans(Player player) {
        final List<Npc> drakans = new ArrayList<Npc>();
        drakans.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 218773, (float) 550.057, (float) 666.941, (float) 183.301, (byte) 40));
        drakans.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 218773, (float) 542.284, (float) 662.375, (float) 183.301, (byte) 40));
        drakans.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 218773, (float) 540.240, (float) 665.980, (float) 183.301, (byte) 40));
        drakans.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 218775, (float) 548.020, (float) 670.540, (float) 183.301, (byte) 40));
        drakans.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 218775, (float) 538.181, (float) 669.566, (float) 183.301, (byte) 40));
        drakans.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 218774, (float) 545.943, (float) 674.044, (float) 183.301, (byte) 40));
        drakans.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 218774, (float) 540.155, (float) 675.154, (float) 183.301, (byte) 40));
        for (Npc mob : drakans) {
            mob.setTarget(player);
            mob.getAggroList().addHate(player, 1);
        }
    }

    private void spawnLegioners(Player player) {
        final List<Npc> enemylegioners = new ArrayList<Npc>();
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 437.11f, 679.46f, 183.3f, (byte) 10));
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 434.35f, 676.1f, 183.3f, (byte) 10));
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 431.36f, 672.6f, 183.3f, (byte) 10));
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 434.98f, 683.7f, 183.3f, (byte) 10));
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 431.6f, 681f, 183.3f, (byte) 10));
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 428.7f, 677.26f, 183.3f, (byte) 10));
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 430.1f, 683.9f, 183.3f, (byte) 10));
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 432.53f, 687.9f, 183.3f, (byte) 10));
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 426.15f, 682.9f, 183.3f, (byte) 10));
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 429.917f, 692.7f, 183.3f, (byte) 10));
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 427.48f, 688.82f, 183.3f, (byte) 10));
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 423f, 688f, 183.3f, (byte) 10));
        enemylegioners.add((Npc) QuestService.addNewSpawn(300400000, player.getInstanceId(), 800037, 421.8f, 690.4f, 183.3f, (byte) 10));
        for (Npc mob : enemylegioners) {
            mob.getSpawn().setX(504.99f);
            mob.getSpawn().setY(737);
            mob.getSpawn().setZ(178);
            ((AbstractAI) mob.getAi2()).setStateIfNot(AIState.WALKING);
            mob.setState(1);
            mob.getMoveController().moveToPoint(504.99f, 737, 178);
            PacketSendUtility.broadcastPacket(mob, new SM_EMOTION(mob, EmotionType.START_EMOTE2, 0, mob.getObjectId()));
        }
    }
}
