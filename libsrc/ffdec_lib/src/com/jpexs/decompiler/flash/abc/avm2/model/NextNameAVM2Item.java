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
public class NextNameAVM2Item extends AVM2Item {

	GraphTargetItem index;

	GraphTargetItem obj;

	public NextNameAVM2Item(GraphSourceItem instruction,
			GraphSourceItem lineStartIns, GraphTargetItem index,
			GraphTargetItem obj) {
		super(instruction, lineStartIns, NOPRECEDENCE);
		this.index = index;
		this.obj = obj;
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData)
			throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();
		nwriter.append("���nextname");
		nwriter.spaceBeforeCallParenthesies(2);
		nwriter.append("(");
		index.toString(nwriter, localData);
		nwriter.append(",");
		obj.toString(nwriter, localData);
		nwriter.append(")");
		return writer.marge(nwriter);
	}

	@Override
	public List<GraphSourceItem> toSource(SourceGeneratorLocalData localData,
			SourceGenerator generator) throws CompilationException {
		return toSourceMerge(localData, generator, obj, index,
				ins(AVM2Instructions.NextName));
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
