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
public class CallStaticAVM2Item extends AVM2Item {

	public GraphTargetItem receiver;

	public String methodName;

	public List<GraphTargetItem> arguments;

	public CallStaticAVM2Item(GraphSourceItem instruction,
			GraphSourceItem lineStartIns, GraphTargetItem receiver,
			String methodName, List<GraphTargetItem> arguments) {
		super(instruction, lineStartIns, PRECEDENCE_PRIMARY);
		this.receiver = receiver;
		this.methodName = methodName;
		this.arguments = arguments;
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData)
			throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();
		receiver.toString(nwriter, localData);
		nwriter.append(".");
		nwriter.append(methodName);
		nwriter.spaceBeforeCallParenthesies(arguments.size());
		nwriter.append("(");
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
		return true;
	}
}
