/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.modules.dredgion

import com.ne.commons.util.Enum.EnumValue
import com.ne.gs.modules.instanceentry.InstanceEntryManager.DungeonType
import com.ne.gs.world.WorldMapType

/**
 * @author hex1r0
 */
object DredgionType extends com.ne.commons.util.Enum[DredgionType] {
  val BARANATH = ::(1, WorldMapType.BARANATH_DREDGION.getId)
  val CHANTRA = ::(2, WorldMapType.CHANTRA_DREDGION.getId)
  val TERATH = ::(3, WorldMapType.TERATH_DREDGION.getId)

  private def ::(id: Int, mapId: Int) = create(DredgionType(id, mapId))
}

case class DredgionType(id: Int, mapId: Int) extends EnumValue with DungeonType {}
