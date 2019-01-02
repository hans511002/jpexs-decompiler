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

import com.jpexs.decompiler.flash.abc.ABC;
import com.jpexs.decompiler.flash.abc.types.ConvertData;
import com.jpexs.decompiler.flash.abc.types.MethodBody;
import com.jpexs.decompiler.flash.configuration.Configuration;
import com.jpexs.decompiler.flash.exporters.modes.ScriptExportMode;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.flash.helpers.NulWriter;
import com.jpexs.decompiler.graph.DottedChain;
import com.jpexs.decompiler.graph.GraphSourceItem;
import com.jpexs.decompiler.graph.GraphTargetItem;
import com.jpexs.decompiler.graph.ScopeStack;
import com.jpexs.decompiler.graph.TypeItem;
import com.jpexs.decompiler.graph.model.LocalData;

/**
 *
 * @author JPEXS
 */
public class NewFunctionAVM2Item extends AVM2Item {

	public String functionName;

	public String path;

	public boolean isStatic;

	public int scriptIndex;

	public int classIndex;

	public ABC abc;

	public List<DottedChain> fullyQualifiedNames;

	public int methodIndex;

	public NewFunctionAVM2Item(GraphSourceItem instruction,
			GraphSourceItem lineStartIns, String functionName, String path,
			boolean isStatic, int scriptIndex, int classIndex, ABC abc,
			List<DottedChain> fullyQualifiedNames, int methodIndex) {
		super(instruction, lineStartIns, PRECEDENCE_PRIMARY);
		this.functionName = functionName;
		this.path = path;
		this.isStatic = isStatic;
		this.scriptIndex = scriptIndex;
		this.classIndex = classIndex;
		this.abc = abc;
		this.fullyQualifiedNames = fullyQualifiedNames;
		this.methodIndex = methodIndex;
	}

	@Override
	public GraphTextWriter appendTo(GraphTextWriter writer, LocalData localData)
			throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();
		MethodBody body = abc.findBody(methodIndex);
		nwriter.append("function");
		nwriter.startMethod(methodIndex);
		nwriter.append((!functionName.isEmpty() ? " " + functionName : ""));
		nwriter.appendNoHilight("(");
		abc.method_info.get(methodIndex).getParamStr(nwriter, abc.constants,
				body, abc, fullyQualifiedNames);
		nwriter.appendNoHilight("):");
		if (Configuration.showMethodBodyId.get()) {
			nwriter.appendNoHilight("// method body index: ");
			nwriter.appendNoHilight(abc.findBodyIndex(methodIndex));
			nwriter.appendNoHilight(" method index: ");
			nwriter.appendNoHilight(methodIndex);
			nwriter.newLine();
		}
		abc.method_info.get(methodIndex).getReturnTypeStr(nwriter,
				abc.constants, fullyQualifiedNames);
		nwriter.startBlock();
		if (body != null) {
			body.convert(new ConvertData(), path + "/inner",
					ScriptExportMode.AS, isStatic, methodIndex, scriptIndex,
					classIndex, abc, null, new ScopeStack(), 0,
					new NulWriter(), fullyQualifiedNames, null, false);
			body.toString(path + "/inner", ScriptExportMode.AS, abc, null,
					nwriter, fullyQualifiedNames);
		}
		nwriter.endBlock();
		nwriter.endMethod();
		return writer.marge(nwriter);
	}

	@Override
	public GraphTargetItem returnType() {
		return new TypeItem(DottedChain.FUNCTION);
	}

	@Override
	public boolean hasReturnValue() {
		return true;
	}
}
