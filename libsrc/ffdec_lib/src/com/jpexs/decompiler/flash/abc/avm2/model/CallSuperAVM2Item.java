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

import java.util.List;

import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.TypeItem;
import com.jpexs.decompiler.graph.model.LocalData;

/**
 *
 * @author JPEXS
 */
public class CallSuperAVM2Item extends AVM2Item {

	public GraphTargetItem receiver;

	public GraphTargetItem multiname;

	public List<GraphTargetItem> arguments;

	public boolean isVoid;

	public CallSuperAVM2Item(GraphSourceItem instruction,
			GraphSourceItem lineStartIns, boolean isVoid,
			GraphTargetItem receiver, GraphTargetItem multiname,
			List<GraphTargetItem> arguments) {
		super(instruction, lineStartIns, PRECEDENCE_PRIMARY);
		this.receiver = receiver;
		this.multiname = multiname;
		this.arguments = arguments;
		this.isVoid = isVoid;
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData)
			throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();
		if (!receiver.toString().equals("this")
				&& !(receiver instanceof FindPropertyAVM2Item)) {
			receiver.toString(nwriter, localData);
			nwriter.append(".");
		}
		nwriter.append("super.");
		multiname.toString(nwriter, localData);
		nwriter.append("(");
		String args = "";
		for (int a = 0; a < arguments.size(); a++) {
			if (a > 0) {
				nwriter.append(",");
			}
			arguments.get(a).toString(nwriter, localData);
		}
		nwriter.append(")");
		writer.marge(nwriter);
		return writer;
	}

	@Override
	public GraphTargetItem returnType() {
		return TypeItem.UNBOUNDED;
	}

	@Override
	public boolean hasReturnValue() {
		return false;
	}
}
