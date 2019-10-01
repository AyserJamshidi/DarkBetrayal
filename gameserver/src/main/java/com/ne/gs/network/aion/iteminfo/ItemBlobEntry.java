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
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.stats.calc.functions.IStatFunction;
import com.ne.gs.network.PacketWriteHelper;
import com.ne.gs.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

import java.nio.ByteBuffer;

/**
 * ItemInfo blob entry (contains detailed item info). Client does have blob tree as implemented, it contains sequence of
 * blobs. Just blame Nemesiss for deep recursion to get the right size [RR] :P
 *
 * @author -Nemesiss-
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