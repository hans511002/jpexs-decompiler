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
package com.jpexs.decompiler.flash.action.model.operations;

import java.util.List;

import com.jpexs.decompiler.flash.SourceGeneratorLocalData;
import com.jpexs.decompiler.flash.action.model.DirectValueActionItem;
import com.jpexs.decompiler.flash.action.swf4.ActionSubtract;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.CompilationException;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.SourceGenerator;
import com.jpexs.decompiler.graph.TypeItem;
import com.jpexs.decompiler.graph.model.BinaryOpItem;
import com.jpexs.decompiler.graph.model.LocalData;

/**
 *
 * @author JPEXS
 */
public class SubtractActionItem extends BinaryOpItem {

	public SubtractActionItem(GraphSourceItem instruction,
			GraphSourceItem lineStartIns, GraphTargetItem leftSide,
			GraphTargetItem rightSide) {
		super(instruction, lineStartIns, PRECEDENCE_ADDITIVE, leftSide,
				rightSide, "-", "Number", "Number");
	}

	@Override
	public Object getResult() {
		return getResult(rightSide.getResultAsNumber(),
				leftSide.getResultAsNumber());
	}

	public static Double getResult(Double rightResult, Double leftResult) {
		return leftResult - rightResult;
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData)
			throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();
		if ((leftSide instanceof DirectValueActionItem)
				&& (((((DirectValueActionItem) leftSide).value instanceof Float) && (((Float) ((DirectValueActionItem) leftSide).value) == 0f))
						|| ((((DirectValueActionItem) leftSide).value instanceof Double) && (((Double) ((DirectValueActionItem) leftSide).value) == 0.0)) || ((((DirectValueActionItem) leftSide).value instanceof Long) && (((Long) ((DirectValueActionItem) leftSide).value) == 0L)))) {
			nwriter.append(operator);
			nwriter.append(" ");
			rightSide.appendTry(nwriter, localData);

		} else if (rightSide.getPrecedence() >= precedence) { // >= add or
																// subtract too

			if (leftSide.getPrecedence() > precedence) {
				nwriter.append("(");
				leftSide.toString(nwriter, localData);
				nwriter.append(")");
			} else {
				leftSide.toString(nwriter, localData);
			}
			nwriter.append(" ");
			nwriter.append(operator);
			nwriter.append(" ");

			nwriter.append("(");
			rightSide.toString(nwriter, localData);
			nwriter.append(")");
		} else {
			super.appendTo(nwriter, localData);
		}
		writer.marge(nwriter);
		return writer;
	}

	@Override
	public List<GraphSourceItem> toSource(SourceGeneratorLocalData localData,
			SourceGenerator generator) throws CompilationException {
		return toSourceMerge(localData, generator, leftSide, rightSide,
				new ActionSubtract());
	}

	@Override
	public GraphTargetItem returnType() {
		return TypeItem.UNBOUNDED;
	}
}
