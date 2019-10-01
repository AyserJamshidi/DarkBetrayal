package com.ne.gs.network.aion.iteminfo;

import com.ne.gs.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

import java.nio.ByteBuffer;

/**
 * @author Rolandas
 */
public class StigmaShardInfoBlobEntry extends ItemBlobEntry {

    public StigmaShardInfoBlobEntry() {
        super(ItemBlobType.STIGMA_SHARD);
    }

    @Override
    public void writeThisBlob(ByteBuffer buf) {
        writeD(buf, 0);
    }

    @Override
    public int getSize() {
        return 4;
    }

}
