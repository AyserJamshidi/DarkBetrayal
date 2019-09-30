/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.database.GDB;
import com.ne.gs.database.dao.PlayerDAO;
import com.ne.gs.model.account.PlayerAccountData;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.PlayerCommonData;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;
import com.ne.gs.world.WorldPosition;

/**
 * @author Kolobrodik
 * @date 20:38/28.12.12
 */
public class cmd_repair extends ChatCommand {

    private static final WorldPosition ELYOS_TP = World.getInstance().createPosition(210010000, 806.6567f, 1242.3309f, 118.98627f, (byte) 109, 0);
    private static final WorldPosition ASMOD_TP = World.getInstance().createPosition(220010000, 529.2442f, 2448.9736f, 281.59262f, (byte) 117, 0);

    @Override
    protected void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params == null || params.length != 1) {
            onError(player, null);
            return;
        }

        String targetName = params[0];

        if (player.getName().equalsIgnoreCase(targetName)) {
            player.sendMsg("Вы не можете переместить самого себя.");
            return;
        }

        if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
            player.sendMsg("Вы не можете использовать эту команду в Тюрьме.");
            return;
        }

        PlayerCommonData targetCommonData = null;
        for (PlayerAccountData playerAccountData : player.getPlayerAccount()) {
            if (targetName.equalsIgnoreCase(playerAccountData.getPlayerCommonData().getName())) {
                targetCommonData = playerAccountData.getPlayerCommonData();
                break;
            }
        }

        if (targetCommonData == null) {
            player.sendMsg("Вы можете переместить только персонажа на вашем аккаунте.");
            return;
        }

        if (targetCommonData.isOnline()) {
            player.sendMsg("Игрок которого вы хотите переместить находится в сети.");
            return;
        }

        if (targetCommonData.getPosition().getMapId() == 510010000 || targetCommonData.getPosition().getMapId() == 520010000) {
            player.sendMsg("Вы не можете переместить персонажа, так как он в тюрьме.");
            return;
        }

        PlayerDAO dao = GDB.get(PlayerDAO.class);
        switch (targetCommonData.getRace()) {
            case ELYOS:
                targetCommonData.getPosition().setXYZH(ELYOS_TP.getX(), ELYOS_TP.getY(), ELYOS_TP.getZ(), ELYOS_TP.getH());
                targetCommonData.getPosition().setMapId(ELYOS_TP.getMapId());
                dao.setPlayerPosition(targetCommonData.getPlayerObjId(), ELYOS_TP);
                break;
            case ASMODIANS:
                targetCommonData.getPosition().setXYZH(ASMOD_TP.getX(), ASMOD_TP.getY(), ASMOD_TP.getZ(), ASMOD_TP.getH());
                targetCommonData.getPosition().setMapId(ASMOD_TP.getMapId());
                dao.setPlayerPosition(targetCommonData.getPlayerObjId(), ASMOD_TP);
                break;
        }
        player.sendMsg("Персонаж успешно перемещен.");
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("синтаксис .repair НикПерсонажа");
    }
}
