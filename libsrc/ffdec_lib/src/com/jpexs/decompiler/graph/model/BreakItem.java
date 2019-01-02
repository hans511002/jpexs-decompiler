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
package com.jpexs.decompiler.graph.model;

import java.util.List;

import com.jpexs.decompiler.flash.SourceGeneratorLocalData;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.flash.helpers.NulWriter;
import com.jpexs.decompiler.graph.CompilationException;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.SourceGenerator;
import com.jpexs.decompiler.graph.TypeItem;

/**
 *
 * @author JPEXS
 */
public class BreakItem extends GraphTargetItem {

	public long loopId;

	private boolean labelRequired;

	public BreakItem(GraphSourceItem src, GraphSourceItem lineStartIns,
			long loopId) {
		super(src, lineStartIns, NOPRECEDENCE);
		this.loopId = loopId;
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter nwriter, LocalData localData) {
		// GraphTextWriter nwriter = writer.cloneNew();
		nwriter.append("break");
		if (nwriter instanceof NulWriter) {
			NulWriter nulWriter = (NulWriter) nwriter;
			labelRequired = loopId != nulWriter.getLoop();
			if (labelRequired) {
				nulWriter.setLoopUsed(loopId);
			}
		}
		if (labelRequired) {
			nwriter.append(" loop").append(loopId);
		}
		// writer.marge(nwriter);
		return nwriter;
	}

	@Override
	public List<GraphSourceItem> toSource(SourceGeneratorLocalData localData,
			SourceGenerator generator) throws CompilationException {
		return generator.generate(localData, this);
	}

	@Override
	public boolean hasReturnValue() {
		return false;
	}

	@Override
	public GraphTargetItem returnType() {
		return TypeItem.UNBOUNDED;
	}
}
