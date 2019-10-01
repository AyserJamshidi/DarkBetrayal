/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ne.gs.network.aion.iteminfo;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

import java.nio.ByteBuffer;

/**
 * This blob sends info about conditioning.
 *
 * @author -Nemesiss-
 */
public class ConditioningInfoBlobEntry extends ItemBlobEntry {

	ConditioningInfoBlobEntry() {
		super(ItemBlobType.CONDITIONING_INFO);
	}

	@Override
	public void writeThisBlob(ByteBuffer buf) {
		Item item = ownerItem;

		writeD(buf, item.getChargePoints());
	}

	@Override
	public int getSize() {
		return 4;
	}
}
