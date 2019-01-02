/*
 *  Copyright (C) 2010-2018 JPEXS, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.jpexs.decompiler.flash.abc.avm2.model;

import java.util.Set;

import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.TypeItem;
import com.jpexs.decompiler.graph.model.LocalData;

/**
 *
 * @author JPEXS
 */
public class DecrementAVM2Item extends AVM2Item {

	public DecrementAVM2Item(GraphSourceItem instruction,
			GraphSourceItem lineStartIns, GraphTargetItem object) {
		super(instruction, lineStartIns, PRECEDENCE_ADDITIVE, object);
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData)
			throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();
		value.toString(nwriter, localData);
		nwriter.append(" - 1");
		writer.marge(nwriter);
		return writer;
	}

	@Override
	public boolean isCompileTime(Set<GraphTargetItem> dependencies) {
		if (dependencies.contains(value)) {
			return false;
		}
		dependencies.add(value);
		return value.isCompileTime(dependencies);
	}

	@Override
	public Object getResult() {
		return value.getResultAsNumber() - 1;
	}

	@Override
	public GraphTargetItem returnType() {
		return TypeItem.UNBOUNDED;
	}

	@Override
	public boolean hasReturnValue() {
		return true;
	}
}
