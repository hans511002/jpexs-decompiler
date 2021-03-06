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

import java.util.ArrayList;
import java.util.List;

import com.jpexs.decompiler.flash.SourceGeneratorLocalData;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.flash.helpers.LoopWithType;
import com.jpexs.decompiler.flash.helpers.NulWriter;
import com.jpexs.decompiler.graph.Block;
import com.jpexs.decompiler.graph.CompilationException;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.Loop;
import com.jpexs.decompiler.graph.SourceGenerator;
import com.jpexs.decompiler.graph.TypeItem;

/**
 *
 * @author JPEXS
 */
public class SwitchItem extends LoopItem implements Block {

	public GraphTargetItem switchedObject;

	public List<GraphTargetItem> caseValues;

	public List<List<GraphTargetItem>> caseCommands;

	public List<Integer> valuesMapping;

	private boolean labelUsed;

	@Override
	public List<List<GraphTargetItem>> getSubs() {
		List<List<GraphTargetItem>> ret = new ArrayList<>();
		ret.addAll(caseCommands);
		return ret;
	}

	public SwitchItem(GraphSourceItem instruction,
			GraphSourceItem lineStartIns, Loop loop,
			GraphTargetItem switchedObject, List<GraphTargetItem> caseValues,
			List<List<GraphTargetItem>> caseCommands,
			List<Integer> valuesMapping) {
		super(instruction, lineStartIns, loop);
		this.switchedObject = switchedObject;
		this.caseValues = caseValues;
		this.caseCommands = caseCommands;
		this.valuesMapping = valuesMapping;
	}

	@Override
	public boolean needsSemicolon() {
		return false;
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData)
			throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();
		if (nwriter instanceof NulWriter) {
			((NulWriter) nwriter).startLoop(loop.id,
					LoopWithType.LOOP_TYPE_SWITCH);
		}
		if (labelUsed) {
			nwriter.append("loop").append(loop.id).append(":").newLine();
		}
		nwriter.append("switch");
		if (nwriter.getFormatting().spaceBeforeParenthesesSwitchParentheses) {
			nwriter.append(" ");
		}
		nwriter.append("(");
		switchedObject.toString(nwriter, localData);
		nwriter.append(")").startBlock();
		for (int i = 0; i < caseCommands.size(); i++) {
			for (int k = 0; k < valuesMapping.size(); k++) {
				if (valuesMapping.get(k) == i) {
					if (!(caseValues.get(k) instanceof DefaultItem)) {
						nwriter.append("case ");
					}
					caseValues.get(k).toString(nwriter, localData);
					nwriter.append(":").newLine();
				}
			}
			nwriter.indent();
			for (int j = 0; j < caseCommands.get(i).size(); j++) {
				if (!caseCommands.get(i).get(j).isEmpty()) {
					caseCommands.get(i).get(j)
							.toStringSemicoloned(nwriter, localData).newLine();
				}
			}
			nwriter.unindent();
		}
		nwriter.endBlock();
		if (nwriter instanceof NulWriter) {
			LoopWithType loopOjb = ((NulWriter) nwriter).endLoop(loop.id);
			labelUsed = loopOjb.used;
		}
		writer.marge(nwriter);
		return writer;
	}

	@Override
	public List<ContinueItem> getContinues() {
		List<ContinueItem> ret = new ArrayList<>();

		for (List<GraphTargetItem> onecase : caseCommands) {
			for (GraphTargetItem ti : onecase) {
				if (ti instanceof ContinueItem) {
					ret.add((ContinueItem) ti);
				}
				if (ti instanceof Block) {
					ret.addAll(((Block) ti).getContinues());
				}
			}
		}
		return ret;
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
