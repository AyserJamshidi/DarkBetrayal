/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.ArrayList;
import java.util.List;

import com.ne.gs.database.GDB;
import org.apache.commons.lang3.StringUtils;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.database.dao.PlayerDAO;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.LetterType;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.PlayerCommonData;
import com.ne.gs.model.templates.item.ItemTemplate;
import com.ne.gs.services.mail.MailFormatter;
import com.ne.gs.services.mail.SystemMailService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author xTz
 */
public class SysMail extends ChatCommand {

    enum RecipientType {

        ELYOS,
        ASMO,
        ALL,
        PLAYER;

        public boolean isAllowed(Race race) {
            switch (this) {
                case ELYOS:
                    return race == Race.ELYOS;
                case ASMO:
                    return race == Race.ASMODIANS;
                case ALL:
                    return race == Race.ELYOS || race == Race.ASMODIANS;
                default:
                    return false;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects.player.Player, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) throws Exception {
        if (params.length < 5) {
            throw new Exception();
        }

        String[] paramValues = new String[params.length];
        System.arraycopy(params, 0, paramValues, 0, params.length);

        RecipientType recipientType;
        String sender;
        String recipient = null;

        if (paramValues[0].startsWith("$$") || paramValues[0].startsWith("%")) {
            if (params.length < 6) {
                throw new Exception();
            }
            sender = paramValues[0];
            paramValues = new String[params.length - 1];
            System.arraycopy(params, 1, paramValues, 0, params.length - 1);
        } else {
            sender = "Admin";
        }

        if (paramValues[0].startsWith("@")) {
            if ("@all".startsWith(paramValues[0])) {
                recipientType = RecipientType.ALL;
            } else if ("@elyos".startsWith(paramValues[0])) {
                recipientType = RecipientType.ELYOS;
            } else if ("@asmodians".startsWith(paramValues[0])) {
                recipientType = RecipientType.ASMO;
            } else {
                admin.sendMsg("Recipient must be Player name, @all, @elyos or @asmodians.");
                return;
            }
        } else {
            recipientType = RecipientType.PLAYER;
            recipient = paramValues[0];
        }

        int item, count, kinah;
        LetterType letterType;

        try {
            item = Integer.parseInt(paramValues[2]);
            count = Integer.parseInt(paramValues[3]);
            kinah = Integer.parseInt(paramValues[4]);
            letterType = LetterType.getLetterTypeById(Integer.parseInt(paramValues[1]));
        } catch (NumberFormatException e) {
            admin.sendMsg("<Regular|Blackcloud|Express> <Item|Count|Kinah> value must be an integer.");
            return;
        }

        if (letterType == LetterType.BLACKCLOUD) {
            sender = "$$CASH_ITEM_MAIL";
        }

        Boolean express = checkExpress(admin, item, count, kinah, recipient, recipientType, letterType);
        if (express == null) {
            return;
        }

        if (item <= 0) {
            item = 0;
        }

        if (count <= 0) {
            count = -1;
        }

        String title = "System Mail";
        String message = " ";

        if (paramValues.length > 5) {
            String[] words = new String[paramValues.length - 5];
            System.arraycopy(paramValues, 5, words, 0, words.length);
            String[] outText = new String[1];
            int wordCount = extractText(words, outText);
            if (wordCount > 0) {
                title = outText[0];
                String[] msgWords = new String[words.length - wordCount];
                System.arraycopy(words, wordCount, msgWords, 0, msgWords.length);
                wordCount = extractText(msgWords, outText);
                if (wordCount > 0) {
                    message = outText[0];
                }
            }
        }

        if (recipientType == RecipientType.PLAYER) {
            if (letterType == LetterType.BLACKCLOUD) {
                MailFormatter.sendBlackCloudMail(recipient, item, count);
            } else {
                SystemMailService.getInstance().sendMail(sender, recipient, title, message, item, count, kinah, letterType);
            }
        } else {
            for (Player player : World.getInstance().getAllPlayers()) {
                if (recipientType.isAllowed(player.getRace())) {
                    if (letterType == LetterType.BLACKCLOUD) {
                        MailFormatter.sendBlackCloudMail(player.getName(), item, count);
                    } else {
                        SystemMailService.getInstance().sendMail(sender, player.getName(), title, message, item, count, kinah, letterType);
                    }
                }
            }
        }

        if (item != 0) {
            String msg = "You send to " + recipientType + (recipientType == RecipientType.PLAYER ? " " + recipient : "") + "\n"
                + "[item:" + item + "] Count:" + count + " Kinah:" + kinah + "\n" + "Letter send successfully.";
            admin.sendMsg(msg);
        } else if (kinah > 0) {
            String msg = "You send to " + recipientType + (recipientType == RecipientType.PLAYER ? " " + recipient : "") + "\n"
                + " Kinah:" + kinah + "\n" + "Letter send successfully.";
            admin.sendMsg(msg);
        }
    }

    private int extractText(String[] words, String[] outText) {
        if (words.length == 0 || outText.length == 0) {
            return 0;
        }

        if (!words[0].startsWith("|")) {
            return 0;
        }

        int wordCount = 1;

        String enclosedText = words[0].substring(1);
        if (enclosedText.endsWith("|")) {
            outText[0] = enclosedText.substring(0, enclosedText.length() - 1);
        } else {
            List<String> titleWords = new ArrayList<>();
            titleWords.add(enclosedText);
            for (; wordCount < words.length; wordCount++) {
                String word = words[wordCount];
                if (word.endsWith("|")) {
                    word = word.substring(0, word.length() - 1);
                    titleWords.add(word);
                    wordCount++;
                    break;
                } else {
                    titleWords.add(word);
                }
            }

            outText[0] = StringUtils.join(titleWords.toArray(new String[titleWords.size()]), ' ');
        }

        return wordCount;
    }

    private static Boolean checkExpress(Player admin, int item, int count, int kinah, String recipient,
                                        RecipientType recipientType, LetterType letterType) {
        Boolean shouldExpress;

        if (recipientType == null) {
            admin.sendMsg("Please insert Recipient Type.\n" + "Recipient = player, @all, @elyos or @asmodians");
            return null;
        } else if (recipientType == RecipientType.PLAYER) {
            PlayerCommonData recipientCommonData = GDB.get(PlayerDAO.class).loadPlayerCommonDataByName(recipient);
            if (recipientCommonData != null && recipientCommonData.getMailboxLetters() >= 100) {
                admin.sendMsg(recipient + "Players mail box is full");
                return null;
            }

            if (letterType == LetterType.NORMAL) {
                if (!GDB.get(PlayerDAO.class).isNameUsed(recipient)) {
                    admin.sendMsg("Could not find a Recipient by that name.");
                    return null;
                }
                shouldExpress = false;
            } else if (letterType == LetterType.EXPRESS) {
                if (World.getInstance().findPlayer(recipient) == null) {
                    admin.sendMsg("This Recipient is offline.");
                    return null;
                }
                shouldExpress = true;
            } else { // Black cloud
                shouldExpress = World.getInstance().findPlayer(recipient) != null;
            }
        } else {
            shouldExpress = letterType != LetterType.NORMAL;
        }

        if (item == 0 && count != 0) {
            admin.sendMsg("Please insert Item Id..");
            return null;
        }

        if (count == 0 && item != 0) {
            admin.sendMsg("Please insert Item Count.");
            return null;
        }

        if (count <= 0 && item <= 0 && kinah <= 0) {
            admin.sendMsg("Parameters <Item> <Count> <Kinah> are icorrect.");
            return null;
        }

        ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(item);
        if (item != 0) {
            if (itemTemplate == null) {
                admin.sendMsg("Item id is incorrect: " + item);
                return null;
            }
            long maxStackCount = itemTemplate.getMaxStackCount();
            if (count > maxStackCount && maxStackCount != 0) {
                admin.sendMsg("Please insert correct Item Count.");
                return null;
            }
        }

        if (kinah < 0) {
            admin.sendMsg("Kinah value must be >= 0.");
            return null;
        } else if (kinah > 0 && letterType == LetterType.BLACKCLOUD) {
            admin.sendMsg("Kinah attachment are not for black cloud letters!");
            return null;
        }
        return shouldExpress;
    }

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#onError(com.ne.gs.model.gameobjects.player.Player, java.lang.Exception)
     */
    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("No parameters detected.\n"
            + "Please use //sysmail [%|$$<Sender>] <Recipient> <Regular|Blackcloud|Express> <Item> <Count> <Kinah> [|Title|] [|Message|]\n"
            + "Sender name must start with % or $$. Can be ommitted.\n" + "Regular mail type is 0, Express mail type is 1, Blackcloud type is 2.\n"
            + "If parameters (Item, Count) = 0 than the item will not be send\n" + "If parameters (Kinah) = 0 not send Kinah\n"
            + "Recipient = Player name, @all, @elyos or @asmodians\n" + "Optional Title and Message must be enclosed within pipe chars");
    }

}
