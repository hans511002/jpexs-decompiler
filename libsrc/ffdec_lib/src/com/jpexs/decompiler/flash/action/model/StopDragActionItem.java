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
package com.jpexs.decompiler.flash.action.model;

import java.util.List;

import com.jpexs.decompiler.flash.SourceGeneratorLocalData;
import com.jpexs.decompiler.flash.action.swf4.ActionEndDrag;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.CompilationException;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.SourceGenerator;
import com.jpexs.decompiler.graph.model.LocalData;

/**
 *
 * @author JPEXS
 */
public class StopDragActionItem extends ActionItem {

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData) {
		GraphTextWriter nwriter = writer.cloneNew();
		nwriter.append("stopDrag");
		nwriter.spaceBeforeCallParenthesies(0);
		nwriter.append("()");
		writer.marge(nwriter);
		return writer;
	}

	public StopDragActionItem(GraphSourceItem instruction,
			GraphSourceItem lineStartIns) {
		super(instruction, lineStartIns, PRECEDENCE_PRIMARY);
	}

	@Override
	public List<GraphSourceItem> toSource(SourceGeneratorLocalData localData,
			SourceGenerator generator) throws CompilationException {
		return toSourceMerge(localData, generator, new ActionEndDrag());
	}

	@Override
	public boolean hasReturnValue() {
		return false;
	}
}
