package com.ne.gs.network.aion.iteminfo;

import com.ne.gs.model.gameobjects.Item;

import java.nio.ByteBuffer;

/**
 * @author Rolandas
 */
public class ArrowInfoBlobEntry extends ItemBlobEntry {

    public ArrowInfoBlobEntry() {
        super(ItemInfoBlob.ItemBlobType.SLOTS_ARROW);
    }

    @Override
    public void writeThisBlob(ByteBuffer buf) {
        /*Item item = owner;
        writeQ(buf, ItemSlot.getSlotFor(item.getItemTemplate().getItemSlot()).getSlotIdMask());
        writeQ(buf, 0);*/
    }

}
