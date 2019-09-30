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
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.PlayerAppearance;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Divinity
 */
public class Appearance extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            onError(admin, null);
            return;
        }

        VisibleObject target = admin.getTarget();
        Player player;

        if (target == null) {
            player = admin;
        } else {
            player = (Player) target;
        }

        if (params[0].equals("reset")) {
            PlayerAppearance savedPlayerAppearance = player.getSavedPlayerAppearance();

            if (savedPlayerAppearance == null) {
                admin.sendMsg("The target has already the normal appearance.");
                return;
            }

            // Edit the current player's appearance with the saved player's appearance
            player.setPlayerAppearance(savedPlayerAppearance);

            // See line 44
            player.setSavedPlayerAppearance(null);

            // Warn the player
            player.sendMsg("An admin has resetted your appearance.");

            // Send update packets
            TeleportService.teleportTo(player, player.getWorldId(), player.getX(), player.getY(), player.getZ(), player.getHeading(),
                TeleportAnimation.BEAM_ANIMATION);

            return;
        }

        if (params.length < 2) {
            onError(player, null);
            return;
        }

        // Get the current player's appearance
        PlayerAppearance playerAppearance = player.getPlayerAppearance();

        // Save a clean player's appearance
        if (player.getSavedPlayerAppearance() == null) {
            player.setSavedPlayerAppearance((PlayerAppearance) playerAppearance.clone());
        }

        if (params[0].equals("size")) // Edit player's size. Min: 0, Max: 50 (prevent bug)
        {
            float height;

            try {
                height = Float.parseFloat(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("The value must be a number !");
                onError(player, e);
                return;
            }

            if (height < 0 || height > 50) {
                admin.sendMsg("Size: Min value : 0 - Max value : 50");
                return;
            }

            // Edit the height
            playerAppearance.setHeight(height);
        } else if (params[0].equals("voice")) // Min: 0, Max: 3
        {
            int voice;

            try {
                voice = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("The value must be a number !");
                onError(player, e);
                return;
            }

            if (voice < 0 || voice > 3) {
                admin.sendMsg("Voice: Min value : 0 - Max value : 3");
                return;
            }

            // Edit the voice
            playerAppearance.setVoice(voice);
        } else if (params[0].equals("hair")) // Min: 1, Max: 43
        {
            int hair;

            try {
                hair = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("The value must be a number !");
                onError(player, e);
                return;
            }

            if (hair < 1 || hair > 43) {
                admin.sendMsg("Hair: Min value : 1 - Max value : 43");
                return;
            }

            // Edit the hair
            playerAppearance.setHair(hair);
        } else if (params[0].equals("face")) // Min: 1, Max: 24
        {
            int face;

            try {
                face = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("The value must be a number !");
                onError(player, e);
                return;
            }

            if (face < 1 || face > 24) {
                admin.sendMsg("Face: Min value : 1 - Max value : 24");
                return;
            }

            // Edit the face
            playerAppearance.setFace(face);
        } else if (params[0].equals("deco")) // Min: 1, Max: 18
        {
            int deco;

            try {
                deco = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("The value must be a number !");
                onError(player, e);
                return;
            }

            if (deco < 1 || deco > 18) {
                admin.sendMsg("Deco: Min value : 1 - Max value : 18");
                return;
            }

            // Edit the deco
            playerAppearance.setDeco(deco);
        } else if (params[0].equals("head_size")) // Min: 0, Max: 100
        {
            int head;

            try {
                head = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("The value must be a number !");
                onError(player, e);
                return;
            }

            if (head < 0 || head > 100) {
                admin.sendMsg("Head Size: Min value : 0 - Max value : 100");
                return;
            }

            // Edit the head
            playerAppearance.setHeadSize(head + 200);
        } else if (params[0].equals("tattoo")) // Min: 1, Max: 13
        {
            int tattoo;

            try {
                tattoo = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                admin.sendMsg("The value must be a number !");
                onError(player, e);
                return;
            }

            if (tattoo < 1 || tattoo > 13) {
                admin.sendMsg("Tattoo: Min value : 1 - Max value : 13");
                return;
            }

            // Edit the tattoo
            playerAppearance.setTattoo(tattoo);
        } else {
            onError(player, null);
            return;
        }

        // Edit the current player's appearance with our modifications
        player.setPlayerAppearance(playerAppearance);

        // Warn the player
        player.sendMsg("An admin has changed your appearance.");

        // Send update packets
        TeleportService.teleportTo(player, player.getWorldId(), player.getX(), player.getY(), player.getZ(), player.getHeading(),
            TeleportAnimation.BEAM_ANIMATION);
    }

    @Override
    public void onError(Player player, Exception e) {
        String syntax = "Syntax: //appearance <size | voice | hair | face | deco | head_size | tattoo | reset (to reset the appearance)> <value>";
        player.sendMsg(syntax);
    }
}
