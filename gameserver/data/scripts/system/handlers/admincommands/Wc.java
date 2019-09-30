/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author -Evilwizard-, Wakizashi World Channel, only for GM/Admins
 */
public class Wc extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        int i = 1;
        boolean check = true;
        Race adminRace = admin.getRace();

        if (params.length < 2) {
            admin.sendMsg("syntax : //wc <ELY | ASM | ALL | default> <message>");
            return;
        }

        StringBuilder sbMessage;
        if (params[0].equals("ELY")) {
            sbMessage = new StringBuilder("[World-Elyos]" + admin.getName() + ": ");
            adminRace = Race.ELYOS;
        } else if (params[0].equals("ASM")) {
            sbMessage = new StringBuilder("[World-Asmodian]" + admin.getName() + ": ");
            adminRace = Race.ASMODIANS;
        } else if (params[0].equals("ALL")) {
            sbMessage = new StringBuilder("[World-All]" + admin.getName() + ": ");
        } else {
            check = false;
            if (adminRace == Race.ELYOS) {
                sbMessage = new StringBuilder("[World-Elyos]" + admin.getName() + ": ");
            } else {
                sbMessage = new StringBuilder("[World-Asmodian]" + admin.getName() + ": ");
            }
        }

        for (String s : params) {
            if (i++ != 1 && check) {
                sbMessage.append(s).append(" ");
            }
        }

        String message = sbMessage.toString().trim();
        int messageLenght = message.length();

        message.substring(0, CustomConfig.MAX_CHAT_TEXT_LENGHT > messageLenght ? messageLenght : CustomConfig.MAX_CHAT_TEXT_LENGHT);
        params[0].equals("ALL");

        // FIXME
        // World.getInstance().doOnAllPlayers(new Visitor<Player>() {
        //
        // @Override
        // public void visit(final Player player)
        // {
        // if (toAll || player.getRace() == race || player.getAccessLevel() >= CommandsConfig.WC)
        // PacketSendUtility.sendMessage(player, sMessage);
        // }
        // });
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax : //wc <ELY | ASM | ALL | default> <message>");
    }
}
