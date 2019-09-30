/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.utils.chathandlers;

/**
 * @author hex1r0
 */
public interface ChatCommandSecurity {

    ChatCommandRuleList getGroupRules(String name);

    ChatCommandRuleList getPlayerRules(String name);

    ChatCommandRuleList getServerRules();

    void addGroupMember(String group, String member);

    // FIXME temp solution (make it portable)
    boolean check(String owner, String id, int accessLevel);
}
