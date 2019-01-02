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
package com.jpexs.decompiler.flash.abc.types.traits;

import java.util.ArrayList;
import java.util.List;

import com.jpexs.decompiler.flash.abc.ABC;
import com.jpexs.decompiler.flash.abc.types.ConvertData;
import com.jpexs.decompiler.flash.abc.types.MethodBody;
import com.jpexs.decompiler.flash.exporters.modes.ScriptExportMode;
import com.jpexs.decompiler.flash.exporters.script.Dependency;
import com.jpexs.decompiler.flash.exporters.script.DependencyParser;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.flash.helpers.NulWriter;
import com.jpexs.decompiler.flash.helpers.hilight.HighlightSpecialType;
import com.jpexs.decompiler.flash.search.MethodId;
import com.jpexs.decompiler.graph.DottedChain;
import com.jpexs.decompiler.graph.ScopeStack;
import com.jpexs.helpers.Helper;

/**
 *
 * @author JPEXS
 */
public class TraitFunction extends Trait implements TraitWithSlot {

	public int slot_id;

	public int method_info;

	@Override
	public void delete(ABC abc, boolean d) {
		abc.constants.getMultiname(name_index).deleted = d;
		abc.method_info.get(method_info).delete(abc, d);
	}

	@Override
	public int getSlotIndex() {
		return slot_id;
	}

	@Override
	public String toString(ABC abc, List<DottedChain> fullyQualifiedNames) {
		String txt = "Function "
				+ abc.constants.getMultiname(name_index).toString(
						abc.constants, fullyQualifiedNames) + " slot="
				+ slot_id + " method_info=" + method_info + " metadata="
				+ Helper.intArrToString(metadata);
		// logger.info(txt);
		return txt;
	}

	@Override
	public GraphTextWriter toStringHeader(Trait parent,
			ConvertData convertData, String path, ABC abc, boolean isStatic,
			ScriptExportMode exportMode, int scriptIndex, int classIndex,
			GraphTextWriter writer, List<DottedChain> fullyQualifiedNames,
			boolean parallel) {
		GraphTextWriter nwriter = writer.cloneNew();

		MethodBody body = abc.findBody(method_info);
		if (body == null) {
			nwriter.appendNoHilight("native ");
		}
		getModifiers(abc, isStatic, nwriter);
		String txt = abc.constants.getMultiname(name_index).getName(
				abc.constants, fullyQualifiedNames, false, true);
		nwriter.hilightSpecial("function ", HighlightSpecialType.TRAIT_TYPE);
		nwriter.hilightSpecial(txt, HighlightSpecialType.TRAIT_NAME);
		nwriter.appendNoHilight("(");
		abc.method_info.get(method_info).getParamStr(nwriter, abc.constants,
				body, abc, fullyQualifiedNames);
		nwriter.appendNoHilight("):");
		abc.method_info.get(method_info).getReturnTypeStr(nwriter,
				abc.constants, fullyQualifiedNames);
		// logger.info(writer.toTmpString());
		writer.marge(nwriter);
		return writer;
	}

	@Override
	public void convertHeader(Trait parent, ConvertData convertData,
			String path, ABC abc, boolean isStatic,
			ScriptExportMode exportMode, int scriptIndex, int classIndex,
			NulWriter writer, List<DottedChain> fullyQualifiedNames,
			boolean parallel) {
	}

	@Override
	public GraphTextWriter toString(Trait parent, ConvertData convertData,
			String path, ABC abc, boolean isStatic,
			ScriptExportMode exportMode, int scriptIndex, int classIndex,
			GraphTextWriter writer, List<DottedChain> fullyQualifiedNames,
			boolean parallel) throws InterruptedException {
		GraphTextWriter nwriter = writer.cloneNew();

		writeImportsUsages(abc, nwriter, getPackage(abc), fullyQualifiedNames);
		getMetaData(parent, convertData, abc, nwriter);
		nwriter.startMethod(method_info);
		toStringHeader(parent, convertData, path, abc, isStatic, exportMode,
				scriptIndex, classIndex, nwriter, fullyQualifiedNames, parallel);

		nwriter.startBlock();
		int bodyIndex = abc.findBodyIndex(method_info);
		if (bodyIndex != -1) {
			abc.bodies.get(bodyIndex).toString(
					path
							+ "."
							+ abc.constants.getMultiname(name_index).getName(
									abc.constants, fullyQualifiedNames, false,
									true), exportMode, abc, this, nwriter,
					fullyQualifiedNames);
		}
		nwriter.endBlock();
		nwriter.newLine();
		nwriter.endMethod();
		writer.marge(nwriter);
		return writer;
	}

	@Override
	public void convert(Trait parent, ConvertData convertData, String path,
			ABC abc, boolean isStatic, ScriptExportMode exportMode,
			int scriptIndex, int classIndex, NulWriter writer,
			List<DottedChain> fullyQualifiedNames, boolean parallel)
			throws InterruptedException {
		fullyQualifiedNames = new ArrayList<>();
		writeImportsUsages(abc, writer, getPackage(abc), fullyQualifiedNames);
		writer.startMethod(method_info);
		convertHeader(parent, convertData, path, abc, isStatic, exportMode,
				scriptIndex, classIndex, writer, fullyQualifiedNames, parallel);
		int bodyIndex = abc.findBodyIndex(method_info);
		if (bodyIndex != -1) {
			abc.bodies.get(bodyIndex).convert(
					convertData,
					path
							+ "."
							+ abc.constants.getMultiname(name_index).getName(
									abc.constants, fullyQualifiedNames, false,
									true), exportMode, isStatic, method_info,
					scriptIndex, classIndex, abc, this, new ScopeStack(), 0,
					writer, fullyQualifiedNames, null, true);
		}
		writer.endMethod();
	}

	@Override
	public int removeTraps(int scriptIndex, int classIndex, boolean isStatic,
			ABC abc, String path) throws InterruptedException {
		int bodyIndex = abc.findBodyIndex(method_info);
		if (bodyIndex != -1) {
			return abc.bodies.get(bodyIndex).removeTraps(abc, this,
					scriptIndex, classIndex, isStatic, path);
		}
		return 0;
	}

	@Override
	public TraitFunction clone() {
		TraitFunction ret = (TraitFunction) super.clone();
		return ret;
	}

	@Override
	public void getDependencies(String customNs, ABC abc,
			List<Dependency> dependencies, List<String> uses,
			DottedChain ignorePackage, List<DottedChain> fullyQualifiedNames) {
		if (ignorePackage == null) {
			ignorePackage = getPackage(abc);
		}
		super.getDependencies(customNs, abc, dependencies, uses, ignorePackage,
				fullyQualifiedNames);
		// if (method_info != 0)
		{
			DependencyParser.parseDependenciesFromMethodInfo(customNs, abc,
					method_info, dependencies, uses, ignorePackage,
					fullyQualifiedNames, new ArrayList<>());
		}
	}

	@Override
	public GraphTextWriter convertTraitHeader(ABC abc, GraphTextWriter writer) {
		GraphTextWriter nwriter = writer.cloneNew();
		convertCommonHeaderFlags("function", abc, nwriter);
		nwriter.newLine();
		nwriter.appendNoHilight("slotid ");
		nwriter.hilightSpecial(Integer.toString(slot_id),
				HighlightSpecialType.SLOT_ID);
		writer.marge(nwriter);
		return writer;
	}

	@Override
	public void getMethodInfos(ABC abc, int traitId, int classIndex,
			List<MethodId> methodInfos) {
		methodInfos.add(new MethodId(traitId, classIndex, method_info));
	}
}
