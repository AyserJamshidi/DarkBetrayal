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
import java.util.HashMap;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.dataholders.WalkerData;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.state.CreatureState;
import com.ne.gs.model.templates.walker.RouteStep;
import com.ne.gs.model.templates.walker.WalkerTemplate;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Rolandas
 */
public class FixPath extends ChatCommand {

    static volatile boolean canceled = false;
    static volatile boolean isRunning = false;
    static Player runner = null;

    @Override
    public void runImpl(@NotNull final Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            admin.sendMsg("Syntax : //fixpath <route id> <jump height> | <cancel>");
            return;
        }

        String routeId = "";
        final float z = admin.getZ();
        float jumpHeight = 0;

        try {
            if (isRunning && runner != null && !admin.equals(runner)) {
                admin.sendMsg("Someone is already running this command!");
                return;
            }
            if ("cancel".equals(params[0])) {
                if (isRunning) {
                    admin.sendMsg("Canceled.");
                    canceled = true;
                }
                return;
            } else if (params.length < 2) {
                admin.sendMsg("Syntax : //fixpath <route id> <jump height> | <cancel>");
                return;
            } else {
                routeId = params[0];
                jumpHeight = Float.parseFloat(params[1]);
            }
        } catch (NumberFormatException e) {
            admin.sendMsg("Only numbers please!!!");
        }

        final WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(routeId);
        if (template == null) {
            admin.sendMsg("Invalid route id");
            return;
        }

        admin.sendMsg("Make sure you are at NPC spawn position. If not use cancel!");

        isRunning = true;
        runner = admin;
        final float height = jumpHeight;

        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                boolean wasInvul = admin.isInvul();
                admin.setInvul(true);

                float zDelta = 0;
                HashMap<Integer, Float> corrections = new HashMap<>();

                try {
                    int i = 1;
                    for (RouteStep step : template.getRouteSteps()) {
                        if (canceled || admin.isInState(CreatureState.DEAD)) {
                            corrections.clear();
                            return;
                        }
                        if (step.getX() == 0 || step.getY() == 0) {
                            corrections.put(i++, admin.getZ());
                            admin.sendMsg("Skipping zero coordinate...");
                            continue;
                        }
                        if (zDelta == 0) {
                            zDelta = z - step.getZ() + height;
                        }
                        admin.sendMsg("Teleporting to step " + i + "...");
                        TeleportService.teleportTo(admin, admin.getWorldId(), step.getX(), step.getY(), step.getZ() + zDelta);
                        admin.getController().stopProtectionActiveTask();
                        admin.sendMsg("Waiting to get Z...");
                        Thread.sleep(5000);
                        step.setZ(admin.getZ());
                        corrections.put(i++, admin.getZ());
                    }

                    admin.sendMsg("Saving corrections...");

                    WalkerData data = new WalkerData();
                    WalkerTemplate newTemplate = new WalkerTemplate(template.getRouteId());

                    i = 1;
                    ArrayList<RouteStep> newSteps = new ArrayList<>();

                    int lastStep = template.isReversed() ? (template.getRouteSteps().size() + 2) / 2 : template.getRouteSteps().size();
                    for (int s = 0; s < lastStep; s++) {
                        RouteStep step = template.getRouteSteps().get(s);
                        RouteStep fixedStep = new RouteStep(step.getX(), step.getY(), corrections.get(i), 0);
                        fixedStep.setRouteStep(i++);
                        newSteps.add(fixedStep);
                    }

                    newTemplate.setRouteSteps(newSteps);
                    if (template.isReversed()) {
                        newTemplate.setIsReversed(true);
                    }
                    newTemplate.setPool(template.getPool());
                    data.AddTemplate(newTemplate);
                    data.saveData(template.getRouteId());

                    admin.sendMsg("Done.");
                } catch (Exception e) {
                } finally {
                    runner = null;
                    isRunning = false;
                    canceled = false;
                    if (!wasInvul) {
                        admin.setInvul(false);
                    }
                }
            }
        }, 5000);
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax : //fixpath <route id> <jump height> | <cancel>");
    }

}
