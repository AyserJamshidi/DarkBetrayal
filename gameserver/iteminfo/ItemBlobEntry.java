/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.iteminfo;

import java.nio.ByteBuffer;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.stats.calc.functions.IStatFunction;
import com.ne.gs.network.PacketWriteHelper;
import com.ne.gs.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * ItemInfo blob entry (contains detailed item info).
 */
public abstract class ItemBlobEntry extends PacketWriteHelper {

    private final ItemBlobType type;
    Player owner;
    Item ownerItem;
    IStatFunction modifier;

    ItemBlobEntry(ItemBlobType type) {
        this.type = type;
    }

    void setOwner(Player owner, Item item, IStatFunction modifier) {
        this.owner = owner;
        this.ownerItem = item;
        this.modifier = modifier;
    }

    @Override
    protected void writeMe(ByteBuffer buf) {
        writeC(buf, type.getEntryId());
        writeThisBlob(buf);
    }

    public abstract void writeThisBlob(ByteBuffer buf);

    public abstract int getSize();

}