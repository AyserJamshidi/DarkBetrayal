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
 * This blob is sent for weapons. It keeps info about slots that weapon can be equipped to.
 *
 * @author -Nemesiss-
 */
public class WeaponInfoBlobEntry extends ItemBlobEntry {

    WeaponInfoBlobEntry() {
        super(ItemBlobType.SLOTS_WEAPON);
    }

    @Override
    public void writeThisBlob(ByteBuffer buf) {
        Item item = ownerItem;

        ItemSlot[] slots = ItemSlot.getSlotsFor(item.getItemTemplate().getItemSlot());
        if (slots.length == 1) {
            writeQ(buf, slots[0].id());
            writeQ(buf, item.hasFusionedItem() ? 0x00 : 0x02);
            return;
        }
        if (item.getItemTemplate().isTwoHandWeapon()) {
            // must occupy two slots
            writeQ(buf, slots[0].id() | slots[1].id());
            writeQ(buf, 0);
        } else {
            // primary and secondary slots
            writeQ(buf, slots[0].id());
            writeQ(buf, slots[1].id());
        }

        // 3.0
        /*writeD(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).id());
        writeD(buf, item.hasFusionedItem() ? 0x00 : 0x02);// TODO! isn't it secondary slot?*/
    }

    @Override
    public int getSize() {
        return 16;
    }
}