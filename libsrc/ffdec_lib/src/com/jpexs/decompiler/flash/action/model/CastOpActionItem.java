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

import java.util.ArrayList;
import java.util.List;

import com.jpexs.decompiler.flash.SourceGeneratorLocalData;
import com.jpexs.decompiler.flash.action.swf7.ActionCastOp;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.CompilationException;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphSourceItemPos;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.SourceGenerator;
import com.jpexs.decompiler.graph.model.LocalData;

/**
 *
 * @author JPEXS
 */
public class CastOpActionItem extends ActionItem {

	public GraphTargetItem constructor;

	public GraphTargetItem object;

	@Override
	public List<GraphTargetItem> getAllSubItems() {
		List<GraphTargetItem> ret = new ArrayList<>();
		ret.add(constructor);
		ret.add(object);
		return ret;
	}

	public CastOpActionItem(GraphSourceItem instruction,
			GraphSourceItem lineStartIns, GraphTargetItem constructor,
			GraphTargetItem object) {
		super(instruction, lineStartIns, PRECEDENCE_PRIMARY);
		this.constructor = constructor;
		this.object = object;
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData)
			throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();
		nwriter.append("(");
		stripQuotes(constructor, localData, nwriter);
		nwriter.append(")");
		object.toString(nwriter, localData);
		writer.marge(nwriter);
		return writer;
	}

	@Override
	public List<GraphSourceItemPos> getNeededSources() {
		List<GraphSourceItemPos> ret = super.getNeededSources();
		ret.addAll(constructor.getNeededSources());
		ret.addAll(object.getNeededSources());
		return ret;
	}

	@Override
	public List<GraphSourceItem> toSource(SourceGeneratorLocalData localData,
			SourceGenerator generator) throws CompilationException {
		return toSourceMerge(localData, generator, constructor, object,
				new ActionCastOp());
	}

	@Override
	public boolean hasReturnValue() {
		return true;
	}
}
