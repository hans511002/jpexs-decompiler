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
import com.jpexs.decompiler.flash.abc.avm2.parser.script.AbcIndexing;
import com.jpexs.decompiler.flash.abc.types.ClassInfo;
import com.jpexs.decompiler.flash.abc.types.ConvertData;
import com.jpexs.decompiler.flash.abc.types.InstanceInfo;
import com.jpexs.decompiler.flash.abc.types.MethodBody;
import com.jpexs.decompiler.flash.abc.types.Multiname;
import com.jpexs.decompiler.flash.abc.types.Namespace;
import com.jpexs.decompiler.flash.exporters.modes.ScriptExportMode;
import com.jpexs.decompiler.flash.exporters.script.Dependency;
import com.jpexs.decompiler.flash.exporters.script.DependencyParser;
import com.jpexs.decompiler.flash.exporters.script.DependencyType;
import com.jpexs.decompiler.flash.helpers.GraphTextWriter;
import com.jpexs.decompiler.flash.helpers.NulWriter;
import com.jpexs.decompiler.flash.helpers.hilight.HighlightSpecialType;
import com.jpexs.decompiler.flash.search.MethodId;
import com.jpexs.decompiler.graph.DottedChain;
import com.jpexs.decompiler.graph.ScopeStack;
import com.jpexs.decompiler.graph.TypeItem;
import com.jpexs.helpers.Helper;

/**
 *
 * @author JPEXS
 */
public class TraitClass extends Trait implements TraitWithSlot {

	public int slot_id;

	public int class_info;

	private boolean classInitializerIsEmpty;

	@Override
	public void delete(ABC abc, boolean d) {
		abc.deleteClass(class_info, d);
		abc.constants.getMultiname(name_index).deleted = d;
	}

	@Override
	public int getSlotIndex() {
		return slot_id;
	}

	@Override
	public String toString(ABC abc, List<DottedChain> fullyQualifiedNames) {
		return "Class " + abc.constants.getMultiname(name_index).toString(abc.constants, fullyQualifiedNames)
				+ " slot=" + slot_id + " class_info=" + class_info + " metadata=" + Helper.intArrToString(metadata);
	}

	@Override
	public void getDependencies(String customNs, ABC abc, List<Dependency> dependencies, List<String> uses,
			DottedChain ignorePackage, List<DottedChain> fullyQualifiedNames) {
		super.getDependencies(customNs, abc, dependencies, uses, ignorePackage == null ? getPackage(abc)
				: ignorePackage, fullyQualifiedNames);
		ClassInfo classInfo = abc.class_info.get(class_info);
		InstanceInfo instanceInfo = abc.instance_info.get(class_info);
		DottedChain packageName = instanceInfo.getName(abc.constants).getNamespace(abc.constants)
				.getName(abc.constants);
		// assume not null name
		// DependencyParser.parseDependenciesFromMultiname(customNs, abc,
		// dependencies, uses,
		// abc.constants.getMultiname(instanceInfo.name_index), packageName,
		// fullyQualifiedNames);
		if (instanceInfo.super_index > 0) {
			DependencyParser.parseDependenciesFromMultiname(customNs, abc, dependencies, uses,
					abc.constants.getMultiname(instanceInfo.super_index), packageName, fullyQualifiedNames,
					DependencyType.INHERITANCE);
		}
		for (int i : instanceInfo.interfaces) {
			DependencyParser.parseDependenciesFromMultiname(customNs, abc, dependencies, uses,
					abc.constants.getMultiname(i), packageName, fullyQualifiedNames, DependencyType.INHERITANCE);
		}

		// static
		classInfo.static_traits.getDependencies(customNs, abc, dependencies, uses, packageName, fullyQualifiedNames);

		// static initializer
		DependencyParser.parseDependenciesFromMethodInfo(customNs, abc, classInfo.cinit_index, dependencies, uses,
				packageName, fullyQualifiedNames, new ArrayList<>());

		// instance
		instanceInfo.instance_traits.getDependencies(customNs, abc, dependencies, uses, packageName,
				fullyQualifiedNames);

		// instance initializer
		DependencyParser.parseDependenciesFromMethodInfo(customNs, abc, instanceInfo.iinit_index, dependencies, uses,
				packageName, fullyQualifiedNames, new ArrayList<>());
	}

	@Override
	public GraphTextWriter toStringHeader(Trait parent, ConvertData convertData, String path, ABC abc,
			boolean isStatic, ScriptExportMode exportMode, int scriptIndex, int classIndex, GraphTextWriter writer,
			List<DottedChain> fullyQualifiedNames, boolean parallel) {
		abc.instance_info.get(class_info).getClassHeaderStr(writer, abc, fullyQualifiedNames, false, getNsName(abc));
		return writer;
	}

	@Override
	public void convertHeader(Trait parent, ConvertData convertData, String path, ABC abc, boolean isStatic,
			ScriptExportMode exportMode, int scriptIndex, int classIndex, NulWriter writer,
			List<DottedChain> fullyQualifiedNames, boolean parallel) {
	}

	String getNsName(ABC abc) {
		Namespace ns = abc.constants.getMultiname(name_index).getNamespace(abc.constants);
		if ((ns.kind == Namespace.KIND_PACKAGE) || (ns.kind == Namespace.KIND_PACKAGE_INTERNAL)) {
			String nsname = ns.getName(abc.constants).toPrintableString(true);
			return nsname;
		}
		return "";
	}

	@Override
	public GraphTextWriter toString(Trait parent, ConvertData convertData, String path, ABC abc, boolean isStatic,
			ScriptExportMode exportMode, int scriptIndex, int classIndex, GraphTextWriter nwriter,
			List<DottedChain> fullyQualifiedNames, boolean parallel) throws InterruptedException {
		// GraphTextWriter nwriter = writer.cloneNew();

		InstanceInfo instanceInfo = abc.instance_info.get(class_info);
		Multiname instanceInfoMultiname = instanceInfo.getName(abc.constants);
		DottedChain packageName = instanceInfoMultiname.getNamespace(abc.constants).getName(abc.constants);
		// assume not null name
		fullyQualifiedNames = new ArrayList<>();
		writeImportsUsages(abc, nwriter, packageName, fullyQualifiedNames);

		String instanceInfoName = instanceInfoMultiname.getName(abc.constants, fullyQualifiedNames, false, true);

		nwriter.startClass(class_info);

		getMetaData(parent, convertData, abc, nwriter);
		// class header
		instanceInfo.getClassHeaderStr(nwriter, abc, fullyQualifiedNames, false, getNsName(abc));
		nwriter.startBlock();

		// static variables & constants
		ClassInfo classInfo = abc.class_info.get(class_info);
		classInfo.static_traits.toString(new Class[] { TraitSlotConst.class }, this, convertData, path
				+ /* packageName + */"/" + instanceInfoName, abc, true, exportMode, false, scriptIndex, class_info,
				nwriter, fullyQualifiedNames, parallel);

		// static initializer
		int bodyIndex = abc.findBodyIndex(classInfo.cinit_index);
		if (bodyIndex != -1) {
			nwriter.startTrait(GraphTextWriter.TRAIT_CLASS_INITIALIZER);
			nwriter.startMethod(classInfo.cinit_index);
			if (exportMode != ScriptExportMode.AS_METHOD_STUBS) {
				if (!classInitializerIsEmpty) {
					nwriter.startBlock();
					abc.bodies.get(bodyIndex).toString(
							path + /* packageName + */"/" + instanceInfoName + ".staticinitializer", exportMode, abc,
							this, nwriter, fullyQualifiedNames);
					nwriter.endBlock();
				} else {
					// Note: There must be trait/method highlight even if the
					// initializer is empty to TraitList in GUI to work
					// correctly
					// TODO: handle this better in GUI(?)
					nwriter.append(" ").newLine();
				}
			}
			nwriter.endMethod();
			nwriter.endTrait();
			if (!classInitializerIsEmpty) {
				nwriter.newLine();
			}
		} else {
			// "/*classInitializer*/";
		}
		/*
		 * packageName +
		 */
		// instance variables
		instanceInfo.instance_traits.toString(new Class[] { TraitSlotConst.class }, this, convertData, path + "/"
				+ instanceInfoName, abc, false, exportMode, false, scriptIndex, class_info, nwriter,
				fullyQualifiedNames, parallel);
		nwriter.newLine();
		// instance initializer - constructor
		if (!instanceInfo.isInterface()) {
			String modifier = "";
			Multiname m = abc.constants.getMultiname(instanceInfo.name_index);
			Namespace ns = m.getNamespace(abc.constants);
			if (ns != null) {
				modifier = ns.getPrefix(abc) + " ";
				if (modifier.equals(" ")) {
					modifier = "";
				}
				if (modifier.startsWith("private")) {
					// cannot have private constuctor
					modifier = "";
				}
			}
			modifier = "public ";
			nwriter.newLine();
			nwriter.startTrait(GraphTextWriter.TRAIT_INSTANCE_INITIALIZER);
			nwriter.startMethod(instanceInfo.iinit_index);
			nwriter.appendNoHilight(modifier);
			// nwriter.appendNoHilight("function ");
			String funName = m.getName(abc.constants, null, false, true);

			if (funName.equals(instanceInfoName))
				funName = "constructor";
			nwriter.appendNoHilight(funName);

			nwriter.appendNoHilight("(");
			bodyIndex = abc.findBodyIndex(instanceInfo.iinit_index);
			MethodBody body = bodyIndex == -1 ? null : abc.bodies.get(bodyIndex);
			abc.method_info.get(instanceInfo.iinit_index).getParamStr(nwriter, abc.constants, body, abc,
					fullyQualifiedNames);
			nwriter.appendNoHilight(")").startBlock();
			if (exportMode != ScriptExportMode.AS_METHOD_STUBS) {
				if (body != null) {
					body.toString(path + "/" + instanceInfoName + ".initializer", exportMode, abc, this, nwriter,
							fullyQualifiedNames);
				} else {
					if (instanceInfo.super_index >= 0) {
						nwriter.appendNoHilight("super();").newLine();
					}
				}
			}
			nwriter.endBlock().newLine();
			nwriter.endMethod();
			nwriter.endTrait();
		}

		// static methods
		classInfo.static_traits.toString(new Class[] { TraitClass.class, TraitFunction.class,
				TraitMethodGetterSetter.class }, this, convertData, path + /* packageName + */"/" + instanceInfoName,
				abc, true, exportMode, false, scriptIndex, class_info, nwriter, fullyQualifiedNames, parallel);

		// instance methods
		instanceInfo.instance_traits.toString(new Class[] { TraitClass.class, TraitFunction.class,
				TraitMethodGetterSetter.class }, this, convertData, path + /* packageName + */"/" + instanceInfoName,
				abc, false, exportMode, false, scriptIndex, class_info, nwriter, fullyQualifiedNames, parallel);

		nwriter.endBlock(); // class
		nwriter.endClass();
		nwriter.newLine();
		// writer.marge(nwriter);
		return nwriter;
	}

	@Override
	public void convert(Trait parent, ConvertData convertData, String path, ABC abc, boolean isStatic,
			ScriptExportMode exportMode, int scriptIndex, int classIndex, NulWriter writer,
			List<DottedChain> fullyQualifiedNames, boolean parallel) throws InterruptedException {

		fullyQualifiedNames = new ArrayList<>();

		InstanceInfo instanceInfo = abc.instance_info.get(class_info);
		String instanceInfoName = instanceInfo.getName(abc.constants).getName(abc.constants, fullyQualifiedNames,
				false, true);
		ClassInfo classInfo = abc.class_info.get(class_info);

		AbcIndexing index = new AbcIndexing(abc.getSwf());
		// for simplification of String(this)
		convertData.thisHasDefaultToPrimitive = null == index.findProperty(
				new AbcIndexing.PropertyDef("toString", new TypeItem(instanceInfo.getName(abc.constants)
						.getNameWithNamespace(abc.constants, true)), abc, abc.constants.getNamespaceId(
						Namespace.KIND_PACKAGE, DottedChain.TOPLEVEL, abc.constants.getStringId("", true), true)),
				false, true);

		// class initializer
		int bodyIndex = abc.findBodyIndex(classInfo.cinit_index);
		if (bodyIndex != -1) {
			writer.mark();
			List<Traits> ts = new ArrayList<>();
			ts.add(classInfo.static_traits);
			abc.bodies.get(bodyIndex).convert(convertData,
					path + /* packageName + */"/" + instanceInfoName + ".staticinitializer", exportMode, true,
					classInfo.cinit_index, scriptIndex, class_info, abc, this, new ScopeStack(),
					GraphTextWriter.TRAIT_CLASS_INITIALIZER, writer, fullyQualifiedNames, ts, true);
			classInitializerIsEmpty = !writer.getMark();
		}

		// constructor - instance initializer
		if (!instanceInfo.isInterface()) {
			bodyIndex = abc.findBodyIndex(instanceInfo.iinit_index);
			if (bodyIndex != -1) {
				List<Traits> ts = new ArrayList<>();
				ts.add(instanceInfo.instance_traits);
				abc.bodies.get(bodyIndex).convert(convertData,
						path + /* packageName + */"/" + instanceInfoName + ".initializer", exportMode, false,
						instanceInfo.iinit_index, scriptIndex, class_info, abc, this, new ScopeStack(),
						GraphTextWriter.TRAIT_INSTANCE_INITIALIZER, writer, fullyQualifiedNames, ts, true);
			}
		}

		// static variables,constants & methods
		classInfo.static_traits.convert(this, convertData, path + /* packageName + */"/" + instanceInfoName, abc, true,
				exportMode, false, scriptIndex, class_info, writer, fullyQualifiedNames, parallel);

		instanceInfo.instance_traits.convert(this, convertData, path + /*
																		 * packageName +
																		 */"/" + instanceInfoName, abc, false,
				exportMode, false, scriptIndex, class_info, writer, fullyQualifiedNames, parallel);
	}

	@Override
	public int removeTraps(int scriptIndex, int classIndex, boolean isStatic, ABC abc, String path)
			throws InterruptedException {
		ClassInfo classInfo = abc.class_info.get(class_info);
		InstanceInfo instanceInfo = abc.instance_info.get(class_info);
		int iInitializer = abc.findBodyIndex(instanceInfo.iinit_index);
		int ret = 0;
		if (iInitializer != -1) {
			ret += abc.bodies.get(iInitializer).removeTraps(abc, this, scriptIndex, class_info, false, path);
		}
		int sInitializer = abc.findBodyIndex(classInfo.cinit_index);
		if (sInitializer != -1) {
			ret += abc.bodies.get(sInitializer).removeTraps(abc, this, scriptIndex, class_info, true, path);
		}
		ret += instanceInfo.instance_traits.removeTraps(scriptIndex, class_info, false, abc, path);
		ret += classInfo.static_traits.removeTraps(scriptIndex, class_info, true, abc, path);
		return ret;
	}

	@Override
	public TraitClass clone() {
		TraitClass ret = (TraitClass) super.clone();
		return ret;
	}

	@Override
	public GraphTextWriter convertTraitHeader(ABC abc, GraphTextWriter writer) {
		GraphTextWriter nwriter = writer.cloneNew();
		convertCommonHeaderFlags("class", abc, nwriter);
		nwriter.appendNoHilight(" slotid ");
		nwriter.hilightSpecial(Integer.toString(slot_id), HighlightSpecialType.SLOT_ID);
		nwriter.newLine();
		writer.marge(nwriter);
		return writer;
	}

	@Override
	public void getMethodInfos(ABC abc, int traitId, int classIndex, List<MethodId> methodInfos) {
		InstanceInfo instanceInfo = abc.instance_info.get(class_info);
		ClassInfo classInfo = abc.class_info.get(class_info);

		// class initializer
		methodInfos.add(new MethodId(GraphTextWriter.TRAIT_CLASS_INITIALIZER, class_info, classInfo.cinit_index));

		// constructor - instance initializer
		methodInfos.add(new MethodId(GraphTextWriter.TRAIT_INSTANCE_INITIALIZER, class_info, instanceInfo.iinit_index));

		// static variables,constants & methods
		classInfo.static_traits.getMethodInfos(abc, true, class_info, methodInfos);

		instanceInfo.instance_traits.getMethodInfos(abc, false, class_info, methodInfos);
	}
}
