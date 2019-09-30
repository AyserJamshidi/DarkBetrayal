/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.utils.chathandlers;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

/**
 * @author hex1r0
 */
public class ChatCommandSecurityImpl implements ChatCommandSecurity {

    private final Map<String, ChatCommandRuleList> _groupRules = new THashMap<>(1);
    private final Map<String, ChatCommandRuleList> _playerRules = new THashMap<>(1);
    private final ChatCommandRuleList _serverRules = new ChatCommandRuleListImpl();

    private final Map<String, Set<String>> _groupMembership = new THashMap<>(1);

    @Override
    public ChatCommandRuleList getGroupRules(String name) {
        name = name.toLowerCase();

        ChatCommandRuleList list = _groupRules.get(name);
        if (list == null) {
            list = new ChatCommandRuleListImpl();
            _groupRules.put(name, list);
        }

        return list;
    }

    @Override
    public ChatCommandRuleList getPlayerRules(String name) {
        name = name.toLowerCase();

        ChatCommandRuleList list = _playerRules.get(name);
        if (list == null) {
            list = new ChatCommandRuleListImpl();
            _playerRules.put(name, list);
        }

        return list;

    }

    @Override
    public ChatCommandRuleList getServerRules() {
        return _serverRules;
    }

    @Override
    public void addGroupMember(String group, String member) {
        member = member.toLowerCase();

        Set<String> groups = _groupMembership.get(member);

        if (groups == null) {
            groups = new THashSet<>(1);
            _groupMembership.put(member, groups);
        }

        groups.add(group);
    }

    @Override
    public boolean check(String owner, String id, int accessLevel) {
        ChatCommandRule.Mode m;
        ChatCommandRule.Mode mode = getServerRules().getMode(id);

        for (String groupName : getGroupNames(owner)) {
            m = getGroupRules(groupName).getMode(id);

            if (m != ChatCommandRule.Mode.DEFAULT) {
                mode = m;
            }
        }

        m = getPlayerRules(owner).getMode(id);

        if (m != ChatCommandRule.Mode.DEFAULT) {
            mode = m;
        }

        return mode == ChatCommandRule.Mode.PERMIT;
    }

    private Iterable<String> getGroupNames(String member) {
        member = member.toLowerCase();

        Set<String> groups = _groupMembership.get(member);

        return groups == null ? Collections.<String>emptySet() : groups;
    }
}
