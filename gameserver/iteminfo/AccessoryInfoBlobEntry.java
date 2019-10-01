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
import com.ne.gs.model.items.ItemSlot;
import com.ne.gs.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This blob is sent for accessory items (such as ring, earring, waist). It keeps info about slots that item can be equipped to.
 *
 * @author -Nemesiss-
 */
public class AccessoryInfoBlobEntry extends ItemBlobEntry {

    AccessoryInfoBlobEntry() {
        super(ItemBlobType.SLOTS_ACCESSORY);
    }

    @Override
    public void writeThisBlob(ByteBuffer buf) {
        // Updated for 4.0
        Item item = owner.item;

        ItemSlot[] slots = ItemSlot.getSlotsFor(item.getItemTemplate().getItemSlot());
        writeQ(buf, slots[0].id());
        writeQ(buf, slots.length > 1 ? slots[1].id() : 0);

        // 3.0
        //writeD(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).id());
        //writeD(buf, 0);// TODO! secondary slot?
    }
}