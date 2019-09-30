/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.utils.chathandlers;

import com.ne.commons.utils.collections.AbstractRegistry;

/**
 * @author hex1r0
 */
public class ChatCommandRuleListImpl extends AbstractRegistry<String, ChatCommandRule> implements ChatCommandRuleList {

    @Override
    public ChatCommandRule getRule(String id) {
        return get(id);
    }

    @Override
    public ChatCommandRule.Mode getMode(String id) {
        ChatCommandRule rule = getRule(id);

        return rule == null ? ChatCommandRule.Mode.DEFAULT : rule.getMode();
    }

    @Override
    public void addRule(ChatCommandRule rule) {
        register(rule.getId(), rule);
    }

    @Override
    public void addRules(Iterable<ChatCommandRule> rules) {
        for (ChatCommandRule rule : rules) {
            addRule(rule);
        }
    }
}
