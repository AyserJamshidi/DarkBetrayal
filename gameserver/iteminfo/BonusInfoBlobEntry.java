package com.ne.gs.network.aion.iteminfo;

import java.nio.ByteBuffer;

public class BonusInfoBlobEntry extends ItemBlobEntry {

    BonusInfoBlobEntry(ItemInfoBlob.ItemBlobType type) {
        super(ItemInfoBlob.ItemBlobType.STAT_BONUSES);
    }

    @Override
    public void writeThisBlob(ByteBuffer buf) {
        /*if (DeveloperConfig.ITEM_STAT_ID > 0) {
            writeH(buf, DeveloperConfig.ITEM_STAT_ID);
            writeD(buf, 10);
            writeC(buf, 0);
        } else {*/
            //writeH(buf, modifier.getName().getItemStoneMask());
            //writeD(buf, modifier.getValue() * modifier.getName().getSign());
            //writeC(buf, modifier instanceof StatRateFunction ? 1 : 0);
        //}
    }
}
