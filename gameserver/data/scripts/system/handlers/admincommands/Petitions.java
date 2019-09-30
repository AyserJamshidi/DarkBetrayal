/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.Collection;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.database.GDB;
import com.ne.gs.database.dao.PetitionDAO;
import com.ne.gs.database.dao.PlayerDAO;
import com.ne.gs.model.Petition;
import com.ne.gs.model.PetitionType;
import com.ne.gs.model.gameobjects.LetterType;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.PetitionService;
import com.ne.gs.services.mail.MailService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author zdead
 */
public class Petitions extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        // Send ticket general info
        if (params.length == 0) {
            Collection<Petition> petitions = PetitionService.getInstance().getRegisteredPetitions();
            Petition[] petitionsArray = petitions.toArray(new Petition[petitions.size()]);
            admin.sendMsg(petitionsArray.length + " unprocessed petitions.");
            if (petitionsArray.length < 5) {
                admin.sendMsg("== " + petitionsArray.length + " first petitions to reply ==");
                for (Petition element : petitionsArray) {
                    admin.sendMsg(element.getPetitionId() + " | " + element.getTitle());
                }
            } else {
                admin.sendMsg("== 5 first petitions to reply ==");
                for (int i = 0; i < 5; i++) {
                    admin.sendMsg(petitionsArray[i].getPetitionId() + " | " + petitionsArray[i].getTitle());
                }
            }
            return;
        }

        int petitionId;

        try {
            petitionId = Integer.parseInt(params[0]);
        } catch (NumberFormatException nfe) {
            admin.sendMsg("Invalid petition id.");
            return;
        }

        Petition petition = GDB.get(PetitionDAO.class).getPetitionById(petitionId);

        if (petition == null) {
            admin.sendMsg("There is no petition with id #" + petitionId);
            return;
        }

        String petitionPlayer;
        boolean isOnline;

        if (World.getInstance().findPlayer(petition.getPlayerObjId()) != null) {
            petitionPlayer = World.getInstance().findPlayer(petition.getPlayerObjId()).getName();
            isOnline = true;
        } else {
            petitionPlayer = GDB.get(PlayerDAO.class).getPlayerNameByObjId(petition.getPlayerObjId());
            isOnline = false;
        }

        // Read petition
        if (params.length == 1) {
            StringBuilder message = new StringBuilder();
            message.append("== Petition #").append(petitionId).append(" ==\n");
            message.append("Player: ").append(petitionPlayer).append(" (");
            if (isOnline) {
                message.append("Online");
            } else {
                message.append("Offline");
            }
            message.append(")\n");
            message.append("Type: ").append(getHumanizedValue(petition.getPetitionType())).append("\n");
            message.append("Title: ").append(petition.getTitle()).append("\n");
            message.append("Text: ").append(petition.getContentText()).append("\n");
            message.append("= Additional Data =\n");
            message.append(getFormattedAdditionalData(petition.getPetitionType(), petition.getAdditionalData()));
            admin.sendMsg(message.toString());
        }
        // Delete
        else if (params.length == 2 && params[1].equals("delete")) {
            PetitionService.getInstance().deletePetition(petition.getPlayerObjId());
            admin.sendMsg("Petition #" + petitionId + " deleted.");
        }
        // Reply
        else if (params.length >= 3 && params[1].equals("reply")) {
            String replyMessage = "";
            for (int i = 2; i < params.length - 1; i++) {
                replyMessage += params[i] + " ";
            }
            replyMessage += params[params.length - 1];
            if (replyMessage.equals("")) {
                admin.sendMsg("You must specify a reply to that petition");
                return;
            }

            MailService.getInstance().sendMail(admin, petitionPlayer, "GM-Re:" + petition.getTitle(), replyMessage, 0, 0, 0, LetterType.NORMAL);
            PetitionService.getInstance().setPetitionReplied(petitionId);

            admin.sendMsg("Your reply has been sent to " + petitionPlayer + ". Petition is now closed.");
        }
    }

    private String getHumanizedValue(PetitionType type) {
        switch (type) {
            case CHARACTER_STUCK:
                return "Character Stuck";
            case CHARACTER_RESTORATION:
                return "Character Restoration";
            case BUG:
                return "Bug";
            case QUEST:
                return "Quest";
            case UNACCEPTABLE_BEHAVIOR:
                return "Unacceptable Behavior";
            case SUGGESTION:
                return "Suggestion";
            case INQUIRY:
                return "Inquiry about the game";
            default:
                return "Unknown";
        }
    }

    private String getFormattedAdditionalData(PetitionType type, String additionalData) {
        String result;
        switch (type) {
            case CHARACTER_STUCK:
                result = "Character Location: " + additionalData;
                break;
            case CHARACTER_RESTORATION:
                result = "Category: " + additionalData;
                break;
            case BUG:
                String[] bugData = additionalData.split("/");
                result = "Time Occured: " + bugData[0] + "\n";
                result += "Zone and Coords: " + bugData[1];
                if (bugData.length > 2) {
                    result += "\nHow to Replicate: " + bugData[2];
                }
                break;
            case QUEST:
                result = "Quest Title: " + additionalData;
                break;
            case UNACCEPTABLE_BEHAVIOR:
                String[] bData = additionalData.split("/");
                result = "Time Occured: " + bData[0] + "\n";
                result += "Character Name: " + bData[1] + "\n";
                result += "Category: " + bData[2];
                break;
            case SUGGESTION:
                //
                result = "Category: " + additionalData;
                break;
            case INQUIRY:
                //
                result = "Petition Category: " + additionalData;
                break;
            default:
                result = additionalData;
        }
        return result;
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //petition");
        player.sendMsg("Syntax: //petition <id>");
        player.sendMsg("Syntax: //petition <id> <reply | delete>");
    }
}
