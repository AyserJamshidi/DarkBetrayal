/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.configs.administration;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ne.commons.scripting.scriptmanager.ScriptManager;
import com.ne.commons.utils.xml.XmlUtil;
import com.ne.gs.GameServer;
import com.ne.gs.utils.chathandlers.*;

/**
 * @author hex1r0
 *         // TODO implement script loader
 */
public final class ChatCommandsConfig {

    private static final Logger _log = LoggerFactory.getLogger(ChatCommandsConfig.class);

    public static void load() {
        try {
            GameServer.addStartupHook(ChatCommandsConfig::reload);
        } catch (Exception e) {
            _log.warn("Error while loading chat commands config", e);
        }
    }

    public static void reload() {
        Context context = new Context();

        loadXml(context);
        loadScripts(context);
    }

    private static void loadXml(Context context) {
        try {
            Document doc = XmlUtil.loadXmlSAX("./config/administration/chat_commands.xml");

            for (Node root : XmlUtil.nodesByName(doc, "chat_commands")) {
                for (Node registry : XmlUtil.nodesByName(root, "registry")) {
                    for (Node admin : XmlUtil.nodesByName(registry, "admin")) {
                        parseAliases(context.ra, admin);
                    }

                    for (Node user : XmlUtil.nodesByName(registry, "user")) {
                        parseAliases(context.ru, user);
                    }

                    for (Node wedding : XmlUtil.nodesByName(registry, "wedding")) {
                        parseAliases(context.rw, wedding);
                    }
                }

                for (Node security : XmlUtil.nodesByName(root, "security")) {
                    for (Node n : XmlUtil.nodesByName(security, "server")) {
                        parseRules(n, context.sa.getServerRules(), context.su.getServerRules(), context.sw.getServerRules());
                    }

                    for (Node n : XmlUtil.nodesByName(security, "group")) {
                        String name = XmlUtil.getAttribute(n, "name");
                        parseRules(n, context.sa.getGroupRules(name), context.su.getGroupRules(name), context.sw.getGroupRules(name));
                    }

                    for (Node n : XmlUtil.nodesByName(security, "player")) {
                        parseGroup(n, context.sa, context.su, context.sw);

                        String name = XmlUtil.getAttribute(n, "name");
                        parseRules(n, context.sa.getPlayerRules(name), context.su.getPlayerRules(name), context.sw.getPlayerRules(name));
                    }
                }
            }
        } catch (Exception e) {
            _log.warn("Error while loading chat commands config", e);
        }
    }

    private static ScriptManager sma;
    private static ScriptManager smu;
    private static ScriptManager smw;

    private static void loadScripts(Context context) {
        if (sma != null) {
            sma.shutdown();
        }

        if (smu != null) {
            smu.shutdown();
        }

        if (smw != null) {
            smw.shutdown();
        }

        sma = new ScriptManager();
        smu = new ScriptManager();
        smw = new ScriptManager();

        context.ra.setSm(sma);
        context.ru.setSm(smu);
        context.rw.setSm(smw);

        context.ra.load();
        context.ru.load();
        context.rw.load();

        ChatCommandHandler.clearHandlers();

        ChatCommandHandler.addHandler(context.ha);
        ChatCommandHandler.addHandler(context.hu);
        ChatCommandHandler.addHandler(context.hw);
    }

    private static void parseRules(Node n, ChatCommandRuleList a, ChatCommandRuleList u, ChatCommandRuleList w) {
        for (Node n1 : XmlUtil.nodesByName(n, "admin")) {
            a.addRules(parsePermit(n1));
            a.addRules(parseDeny(n1));
        }

        for (Node n1 : XmlUtil.nodesByName(n, "user")) {
            u.addRules(parsePermit(n1));
            u.addRules(parseDeny(n1));
        }

        for (Node n1 : XmlUtil.nodesByName(n, "wedding")) {
            w.addRules(parsePermit(n1));
            w.addRules(parseDeny(n1));
        }
    }

    private static void parseAliases(ChatCommandRegistry commands, Node node) {
        ChatCommandAliasRegistry aliases = new ChatCommandAliasRegistryImpl();
        for (Node clazz : XmlUtil.nodesByName(node, "class")) {
            for (Node alias : XmlUtil.nodesByName(clazz, "alias")) {
                aliases.addAlias(XmlUtil.getAttribute(clazz, "name"), XmlUtil.getAttribute(alias, "name"));
            }
        }

        commands.setAliasRegistry(aliases);
    }

    private static Iterable<ChatCommandRule> parsePermit(Node root) {
        List<ChatCommandRule> rules = new ArrayList<>(1);
        for (Node n : XmlUtil.nodesByName(root, "permit")) {
            rules.add(new ChatCommandRule(ChatCommandRule.Mode.PERMIT, XmlUtil.getAttribute(n, "class")));
        }

        return rules;
    }

    private static Iterable<ChatCommandRule> parseDeny(Node root) {
        List<ChatCommandRule> rules = new ArrayList<>(1);
        for (Node n : XmlUtil.nodesByName(root, "deny")) {
            rules.add(new ChatCommandRule(ChatCommandRule.Mode.DENY, XmlUtil.getAttribute(n, "class")));
        }

        return rules;
    }

    private static void parseGroup(Node root, ChatCommandSecurity... as) {
        for (Node n : XmlUtil.nodesByName(root, "group")) {
            for (ChatCommandSecurity s : as) {
                s.addGroupMember(XmlUtil.getAttribute(n, "name"), XmlUtil.getAttribute(root, "name"));
            }
        }
    }

    private static class Context {

        final ChatCommandRegistry ra = new AdminCommandRegistry();
        final ChatCommandRegistry ru = new UserCommandRegistry();
        final ChatCommandRegistry rw = new WeddingCommandRegistry();

        final ChatCommandSecurity sa = new AdminChatCommandSecurity();
        final ChatCommandSecurity su = new ChatCommandSecurityImpl();
        final ChatCommandSecurity sw = new ChatCommandSecurityImpl();

        final ChatCommandHandler ha = new AdminCommandHandler(ra, sa);
        final ChatCommandHandler hu = new UserCommandHandler(ru, su);
        final ChatCommandHandler hw = new WeddingCommandHandler(rw, sw);
    }
}
