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
import java.util.Set;

import com.jpexs.decompiler.flash.SourceGeneratorLocalData;
import com.jpexs.decompiler.flash.action.swf4.ActionStringLength;
import com.jpexs.decompiler.flash.ecma.EcmaScript;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.CompilationException;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.SourceGenerator;
import com.jpexs.decompiler.graph.model.LocalData;

/**
 *
 * @author JPEXS
 */
public class StringLengthActionItem extends ActionItem {

	public StringLengthActionItem(GraphSourceItem instruction,
			GraphSourceItem lineStartIns, GraphTargetItem value) {
		super(instruction, lineStartIns, PRECEDENCE_PRIMARY, value);
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData)
			throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();
		nwriter.append("length");
		nwriter.spaceBeforeCallParenthesies(1);
		nwriter.append("(");
		value.toString(nwriter, localData);
		nwriter.append(")");
		writer.marge(nwriter);
		return writer;
	}

	@Override
	public boolean isCompileTime(Set<GraphTargetItem> dependencies) {
		return false;
	}

	@Override
	public Object getResult() {
		return getResult(value.getResult());
	}

	public static Double getResult(Object obj) {
		return EcmaScript.toNumberAs2(EcmaScript.toString(obj).length());
	}

	@Override
	public List<GraphSourceItem> toSource(SourceGeneratorLocalData localData,
			SourceGenerator generator) throws CompilationException {
		return toSourceMerge(localData, generator, value,
				new ActionStringLength());
	}

	@Override
	public boolean hasReturnValue() {
		return true;
	}
}
