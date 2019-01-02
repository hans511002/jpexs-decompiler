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

import com.jpexs.decompiler.flash.IdentifiersDeobfuscation;
import com.jpexs.decompiler.flash.SourceGeneratorLocalData;
import com.jpexs.decompiler.flash.action.parser.script.ActionSourceGenerator;
import com.jpexs.decompiler.flash.action.swf4.ActionPush;
import com.jpexs.decompiler.flash.action.swf4.RegisterNumber;
import com.jpexs.decompiler.flash.action.swf5.ActionSetMember;
import com.jpexs.decompiler.flash.action.swf5.ActionStoreRegister;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.graph.CompilationException;
import com.jpexs.decompiler.graph.GraphPart;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphSourceItemPos;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.SourceGenerator;
import com.jpexs.decompiler.graph.model.LocalData;

/**
 *
 * @author JPEXS
 */
public class SetMemberActionItem extends ActionItem implements
		SetTypeActionItem {

	public GraphTargetItem object;

	public GraphTargetItem objectName;

	private int tempRegister = -1;

	@Override
	public List<GraphTargetItem> getAllSubItems() {
		List<GraphTargetItem> ret = new ArrayList<>();
		ret.add(object);
		ret.add(value);
		return ret;
	}

	@Override
	public GraphPart getFirstPart() {
		return value.getFirstPart();
	}

	@Override
	public void setValue(GraphTargetItem value) {
		this.value = value;
	}

	@Override
	public int getTempRegister() {
		return tempRegister;
	}

	@Override
	public void setTempRegister(int tempRegister) {
		this.tempRegister = tempRegister;
	}

	@Override
	public GraphTargetItem getValue() {
		return value;
	}

	public SetMemberActionItem(GraphSourceItem instruction,
			GraphSourceItem lineStartIns, GraphTargetItem object,
			GraphTargetItem objectName, GraphTargetItem value) {
		super(instruction, lineStartIns, PRECEDENCE_ASSIGMENT, value);
		this.object = object;
		this.objectName = objectName;
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData)
			throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();
		object.toString(nwriter, localData);

		if ((!(objectName instanceof DirectValueActionItem))
				|| (!((DirectValueActionItem) objectName).isString())
				|| (!IdentifiersDeobfuscation.isValidName(false,
						((DirectValueActionItem) objectName)
								.toStringNoQuotes(localData)))) {
			nwriter.append("[");
			objectName.toString(nwriter, localData);
			nwriter.append("]");
		} else {
			nwriter.append(".");
			stripQuotes(objectName, localData, nwriter);
		}
		nwriter.append(" = ");
		value.toString(nwriter, localData);
		writer.marge(nwriter);
		return writer;
	}

	@Override
	public GraphTargetItem getObject() {
		return new GetMemberActionItem(getSrc(), getLineStartItem(), object,
				objectName);
	}

	@Override
	public List<GraphSourceItemPos> getNeededSources() {
		List<GraphSourceItemPos> ret = super.getNeededSources();
		ret.addAll(object.getNeededSources());
		ret.addAll(objectName.getNeededSources());
		ret.addAll(value.getNeededSources());
		return ret;
	}

	@Override
	public boolean hasSideEffect() {
		return true;
	}

	@Override
	public List<GraphSourceItem> toSource(SourceGeneratorLocalData localData,
			SourceGenerator generator) throws CompilationException {
		ActionSourceGenerator asGenerator = (ActionSourceGenerator) generator;
		int tmpReg = asGenerator.getTempRegister(localData);
		try {
			return toSourceMerge(localData, generator, object, objectName,
					value, new ActionStoreRegister(tmpReg),
					new ActionSetMember(), new ActionPush(new RegisterNumber(
							tmpReg)));
		} finally {
			asGenerator.releaseTempRegister(localData, tmpReg);
		}
	}

	@Override
	public List<GraphSourceItem> toSourceIgnoreReturnValue(
			SourceGeneratorLocalData localData, SourceGenerator generator)
			throws CompilationException {
		return toSourceMerge(localData, generator, object, objectName, value,
				new ActionSetMember());
	}

	@Override
	public boolean hasReturnValue() {
		return false;
	}
}
