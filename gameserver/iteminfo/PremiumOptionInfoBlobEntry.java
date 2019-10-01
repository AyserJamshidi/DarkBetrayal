package com.ne.gs.network.aion.iteminfo;

import java.nio.ByteBuffer;

public class PremiumOptionInfoBlobEntry extends ItemBlobEntry {

    public PremiumOptionInfoBlobEntry() {
        super(ItemInfoBlob.ItemBlobType.PREMIUM_OPTION);
    }

    @Override
    public void writeThisBlob(ByteBuffer buf) {
        writeH(buf, ownerItem.isConfigured() ? ownerItem.getBonusNumber() : 0);
        writeC(buf, 0);
    }

    @Override
    public int getSize() {
        return 1 + 2;
    }

}