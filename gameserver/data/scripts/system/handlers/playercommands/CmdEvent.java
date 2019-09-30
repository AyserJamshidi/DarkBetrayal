package playercommands;

import com.ne.commons.annotations.NotNull;
import com.ne.commons.func.tuple.Tuple;
import com.ne.commons.func.tuple.Tuple3;
import com.ne.commons.utils.Chainer;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.modules.pvpevent.EventCallback;
import com.ne.gs.modules.pvpevent.Messages;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author hex1r0
 */
public class CmdEvent extends ChatCommand {

    @Override
    protected void runImpl(@NotNull Player player,
            @NotNull String alias,
            @NotNull String... params) throws Exception {
        if (params.length <= 1) {
            return;
        }

        String locId = params[1];
        if (params[0].equals("apply")) {
            if (!Chainer.GLOBAL.handle(EventCallback.Apply.class, Tuple.of(player, locId))) {
                player.sendMsg(Messages.REGISTER_NO_EVENTS);
            }
        } else if (params[0].equals("cancel")) {
            Chainer.GLOBAL.handle(EventCallback.Cancel.class, Tuple.of(player, locId));
        }
    }
}
