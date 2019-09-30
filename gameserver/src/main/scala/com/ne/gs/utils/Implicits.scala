/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */

package com.ne.gs.utils

import com.ne.gs.world.knownlist.Visitor
import com.ne.gs.model.gameobjects.AionObject

/**
 * @author hex1r0
 */
object Implicits {
  implicit def func2visitor[T](f: (T) => Unit) = new Visitor[T]() {def visit(o: T) { f(o) }}
  implicit def func2runnable(f: () => Unit) = new Runnable() {def run() { f() }}

   def uid(p1: AionObject) = p1.getObjectId.toInt
}
