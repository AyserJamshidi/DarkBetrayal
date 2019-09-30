package ai.specialNpc;

import ai.ActionItemNpcAI2;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.RequestResponseHandler;
import com.ne.gs.model.team.legion.Legion;
import com.ne.gs.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.ne.gs.services.LegionService;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.utils.PacketSendUtility;
import java.util.ArrayList;
import java.util.List;

/**
 * Romanz
 */
@AIName("legion_invite_asmo")
public class LegionInviteAsmoAI2 extends ActionItemNpcAI2 {

    private LegionService service;

    private Player getTargetPlayer() {
        List<Player> players = new ArrayList<Player>();
        for (Player player : getKnownList().getKnownPlayers().values()) {
            if (!CreatureActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 5)) {
                players.add(player);
            }
        }
        return !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
    }

    @Override
    public void handleUseItemFinish(Player player) {
        RequestResponseHandler responseHandler = new RequestResponseHandler(player) {
            @Override
            public void acceptRequest(Creature requester, Player responder) {

                service = LegionService.getInstance();
                final Player target = getTargetPlayer();
                if (target == null) {
                    PacketSendUtility.sendBrightYellowMessageOnCenter(target, "Игрок недоступен!");
                    return;
                }
                Legion legion = service.getLegion("AsmoBeginner");//legion asmo name

                if (legion == null) {
                    PacketSendUtility.sendBrightYellowMessageOnCenter(target, "Ошибка. Нет такого легиона. Сообщите администрации!");
                    return;
                }
                if (target.isLegionMember()) {
                    PacketSendUtility.sendBrightYellowMessageOnCenter(target, "Вы уже состоите в легионе " + target.getLegion().getLegionName() + ". Для открытия меню легиона нажмите клавишу G. Для написания сообщения в легион нажмите клавишу Enter и в появившемся окне, нажав на иконку слева, выберите /Легион");
                    return;
                }
                LegionService.getInstance().directAddPlayer(legion, target);
            }

            @Override
            public void denyRequest(Creature p2, Player p) {
            }
        };
        boolean requested = player.getResponseRequester().putRequest(902247, responseHandler);
        if (requested) {
            PacketSendUtility.sendPck(player, new SM_QUESTION_WINDOW(902247, 0, 0, "Желаете вступить в легион для новичков " + "AsmoBeginner" + "?"));//legion asmo name
        }
    }
}