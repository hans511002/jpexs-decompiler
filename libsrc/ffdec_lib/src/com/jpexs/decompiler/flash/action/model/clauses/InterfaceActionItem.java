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
package com.jpexs.decompiler.flash.action.model.clauses;

import java.util.ArrayList;
import java.util.List;

import com.jpexs.decompiler.flash.SourceGeneratorLocalData;
import com.jpexs.decompiler.flash.action.Action;
import com.jpexs.decompiler.flash.action.model.ActionItem;
import com.jpexs.decompiler.flash.action.parser.script.ActionSourceGenerator;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.flash.helpers.collections.MyEntry;
import com.jpexs.decompiler.graph.CompilationException;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.SourceGenerator;
import com.jpexs.decompiler.graph.model.LocalData;

/**
 *
 * @author JPEXS
 */
public class InterfaceActionItem extends ActionItem {

	public GraphTargetItem name;

	public List<GraphTargetItem> superInterfaces;

	public InterfaceActionItem(GraphTargetItem name,
			List<GraphTargetItem> superInterfaces) {
		super(null, null, NOPRECEDENCE);
		this.name = name;
		this.superInterfaces = superInterfaces;
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData)
			throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();
		nwriter.startClass(name.toStringNoQuotes(localData));
		nwriter.append("interface ");
		name.toStringNoQuotes(nwriter, localData);
		boolean first = true;
		if (!superInterfaces.isEmpty()) {
			nwriter.append(" extends ");
		}
		for (GraphTargetItem ti : superInterfaces) {
			if (!first) {
				nwriter.append(", ");
			}
			first = false;
			Action.getWithoutGlobal(ti).toStringNoQuotes(nwriter, localData);
		}
		nwriter.startBlock().endBlock().endClass();
		writer.marge(nwriter);
		return writer;
	}

	@Override
	public boolean needsSemicolon() {
		return false;
	}

	@Override
	public List<GraphSourceItem> toSource(SourceGeneratorLocalData localData,
			SourceGenerator generator) throws CompilationException {
		List<GraphSourceItem> ret = new ArrayList<>();
		ActionSourceGenerator asGenerator = (ActionSourceGenerator) generator;
		ret.addAll(asGenerator.generateTraits(localData, true, name, null,
				superInterfaces,
				new ArrayList<MyEntry<GraphTargetItem, GraphTargetItem>>(),
				new ArrayList<>()));
		return ret;
	}

	@Override
	public boolean hasReturnValue() {
		return false;
	}
}
