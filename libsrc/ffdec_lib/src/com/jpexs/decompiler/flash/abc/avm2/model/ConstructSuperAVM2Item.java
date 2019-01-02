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

import com.jpexs.decompiler.flash.SourceGeneratorLocalData;
import com.jpexs.decompiler.flash.abc.avm2.instructions.AVM2Instruction;
import com.jpexs.decompiler.flash.abc.avm2.instructions.AVM2Instructions;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.CompilationException;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.SourceGenerator;
import com.jpexs.decompiler.graph.TypeItem;
import com.jpexs.decompiler.graph.model.LocalData;

/**
 *
 * @author JPEXS
 */
public class ConstructSuperAVM2Item extends AVM2Item {

	public GraphTargetItem object;

	public List<GraphTargetItem> args;

	public ConstructSuperAVM2Item(GraphSourceItem instruction,
			GraphSourceItem lineStartIns, GraphTargetItem object,
			List<GraphTargetItem> args) {
		super(instruction, lineStartIns, PRECEDENCE_PRIMARY);
		this.object = object;
		this.args = args;
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData)
			throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();
		if (!object.toString().equals("this")) {
			object.toString(nwriter, localData);
			nwriter.append(".");
		}
		nwriter.spaceBeforeCallParenthesies(args.size());
		nwriter.append("super(");
		for (int a = 0; a < args.size(); a++) {
			if (a > 0) {
				nwriter.append(",");
			}
			args.get(a).toString(nwriter, localData);
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

	@Override
	public List<GraphSourceItem> toSource(SourceGeneratorLocalData localData,
			SourceGenerator generator) throws CompilationException {
		return toSourceMerge(localData, generator, object, args,
				new AVM2Instruction(0, AVM2Instructions.ConstructSuper,
						new int[] { args.size() }));
	}
}
