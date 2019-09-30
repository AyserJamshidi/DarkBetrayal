/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.concurrent.ScheduledFuture;
import javolution.util.FastMap;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.chathandlers.ChatCommand;

public class Dialog extends ChatCommand {

    private int dialog;
    private final FastMap<Integer, ScheduledFuture<?>> tasks = new FastMap<>();

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {

        if (admin.getTarget() == null) {
            admin.sendMsg("You need selected target");
            return;
        }
        if (params.length == 0) {
            admin.sendMsg("Syntax : //dialog <DialogId> <start> or //dialog quest <QuestId> <DialogId>");
            return;
        }
        int target = admin.getTarget().getObjectId();
        if (params[0].equals("quest")) {
            if (params.length < 2) {
                admin.sendMsg("Syntax : //dialog quest <QuestId> <DialogId>");
                return;
            }
            try {
                dialog = Integer.parseInt(params[2]);
                int quest = Integer.parseInt(params[1]);
                sendDialog(target, dialog, quest, admin);
            } catch (NumberFormatException e) {
                admin.sendMsg("Syntax : //dialog quest <QuestId> <DialogId>");
            }
        } else if (params.length == 1) {
            if (params[0].equals("stop")) {
                tasks.get(admin.getObjectId()).cancel(true);
                tasks.remove(admin.getObjectId());
                return;
            }
            dialog = Integer.parseInt(params[0]);
            sendDialog(target, dialog, admin);
        } else {
            dialog = Integer.parseInt(params[0]);
            startAutoSend(target, admin);
        }
    }

    private void startAutoSend(final int target, final Player admin) {
        ScheduledFuture<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                admin.sendPck(new SM_DIALOG_WINDOW(target, 0, 0));
                sendDialog(target, dialog, admin);
                dialog++;
            }
        }, 500, 500);
        tasks.put(admin.getObjectId(), task);
    }

    private void sendDialog(int target, int dialog, Player admin) {
        try {
            admin.sendMsg("Target objId:" + target + " DialogId: " + dialog);
            admin.sendPck(new SM_DIALOG_WINDOW(target, dialog, 0));
        } catch (NumberFormatException e) {
            admin.sendMsg("Syntax : //dialog <DialogId>");
        }
    }

    private void sendDialog(int target, int dialog, int quest, Player admin) {
        try {
            admin.sendMsg("Target objId:" + target + " DialogId: " + dialog + " QuestId: " + quest);
            admin.sendPck(new SM_DIALOG_WINDOW(target, dialog, quest));
        } catch (NumberFormatException e) {
            admin.sendMsg("Syntax : //dialog quest <QuestId> <DialogId>");
        }
    }
}
