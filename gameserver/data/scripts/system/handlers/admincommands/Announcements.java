/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.Set;
import org.quartz.CronExpression;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.Announcement;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.AnnouncementService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Divinity
 */
public class Announcements extends ChatCommand {

    private final String separator = "_";
    private final String space = " ";

    private final AnnouncementService announceService = AnnouncementService.getInstance();

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params[0].equals("list")) {
            Set<Announcement> announces = announceService.getAnnouncements();
            player.sendMsg("ID  |  FACTION  |  CHAT TYPE  |  DELAY  |  MESSAGE");
            player.sendMsg("-------------------------------------------------------------------");

            for (Announcement announce : announces) {
                player.sendMsg(announce.getId() + "  |  " + announce.getFaction() + "  |  " + announce.getType() + "  |  " + announce.getDelay() + "  |  "
                    + announce.getAnnounce());
            }
        } else if (params[0].equals("add")) {
            if (params.length < 5) {
                onError(player, null);
                return;
            }

            //Jenelli 02.03.2013: Теперь в базе лежит строка CronExpression, а не интервал.
            //			int delay;
            //
            //			try {
            //				delay = Integer.parseInt(params[3]);
            //			} catch (final NumberFormatException e) {
            //				// 15 minutes, default
            //				delay = 900;
            //			}

            String delay;
            delay = params[3].replaceAll(separator, space);
            if (!CronExpression.isValidExpression(delay)) {
                delay = "0 0 * ? * *";
            }

            String message = "";

            // Add with space
            for (int i = 4; i < params.length - 1; i++) {
                message += params[i] + " ";
            }

            // Add the last without the end space
            message += params[params.length - 1];

            // Create the announce
            Announcement announce = new Announcement(message, params[1], params[2], delay);

            // Add the announce in the database
            announceService.addAnnouncement(announce);

            // Reload all announcements
            announceService.reload();

            player.sendMsg("The announcement has been created with successful !");
        } else if (params[0].equals("delete")) {
            if (params.length < 2) {
                onError(player, null);
                return;
            }

            int id;

            try {
                id = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                player.sendMsg("The announcement's ID is wrong !");
                onError(player, e);
                return;
            }

            // Delete the announcement from the database
            announceService.delAnnouncement(id);

            // Reload all announcements
            announceService.reload();

            player.sendMsg("The announcement has been deleted with successful !");
        } else {
            onError(player, null);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        String syntaxCommand = "Syntax: //announcements list - Obtain all announcements in the database.\n";
        syntaxCommand += "Syntax: //announcements add <faction: ELYOS | ASMODIANS | ALL> <type: SYSTEM | WHITE | ORANGE | SHOUT | YELLOW> <delay> <message> - Add an announcements in the database.\n";
        syntaxCommand += "Syntax delay: Seconds_Minutes_Hours_Day-of-month_Month_Day-of-Week_Year - Full specification look in CronExpression";
        syntaxCommand += "Syntax: //announcements delete <id (see //announcements list to find all id> - Delete an announcements from the database.";
        player.sendMsg(syntaxCommand);
    }
}
