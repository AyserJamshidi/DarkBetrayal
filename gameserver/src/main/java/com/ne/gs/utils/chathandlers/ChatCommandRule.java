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
public class ChatCommandRule {

    public static enum Mode {
        DEFAULT,
        PERMIT,
        DENY
    }

    private ChatCommandRule.Mode _mode = ChatCommandRule.Mode.DEFAULT;
    private String _id;

    public ChatCommandRule(ChatCommandRule.Mode mode, String id) {
        setMode(mode);
        setId(id);
    }

    public ChatCommandRule.Mode getMode() {
        return _mode;
    }

    public void setMode(ChatCommandRule.Mode mode) {
        _mode = mode;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }
}
