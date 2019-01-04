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
package com.jpexs.decompiler.flash.helpers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jpexs.decompiler.flash.helpers.hilight.HighlightData;
import com.jpexs.decompiler.flash.helpers.hilight.HighlightSpecialType;
import com.jpexs.helpers.utf8.Utf8OutputStreamWriter;

/**
 * Provides methods for highlighting positions of instructions in the text.
 *
 * @author JPEXS
 */
public class StreamTextWriter extends GraphTextWriter implements AutoCloseable {

	private final Writer writer;

	private boolean newLine = true;

	private int indent;

	private int writtenBytes;
	OutputStream os;

	public StreamTextWriter(CodeFormatting formatting, OutputStream os) {
		super(formatting);
		this.os = os;
		this.writer = new Utf8OutputStreamWriter(new BufferedOutputStream(os));
	}

	StreamTextWriter(CodeFormatting formatting) {
		super(formatting);
		// this.os = new java.io.ByteArrayOutputStream(64 * 1024);
		this.writer = null;// new Utf8OutputStreamWriter(os);
	}

	public GraphTextWriter cloneNew() {
		StreamTextWriter nw = new StreamTextWriter(formatting);
		nw.newLine = this.newLine;
		nw.indent = this.indent;
		this.newLine = false;
		return nw;
	};

	@Override
	public GraphTextWriter marge(GraphTextWriter w) {
		super.marge(w);
		StreamTextWriter o = (StreamTextWriter) w;
		if (o.writer == null) {
			String tmp = o.sb.toString();
			tmp = Convert2Ts.convertType(tmp);
			tmp = Convert2Ts.convertLine(tmp);
			this.append(tmp);
			this.indent = o.indent;
			this.newLine = o.newLine;
		}
		return this;
	};

	@Override
	public String toTmpString() {
		return sb.toString();
	}

	@Override
	public String toText() {
		return sb.toString();
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	@Override
	public GraphTextWriter hilightSpecial(String text, HighlightSpecialType type, String specialValue,
			HighlightData data) {
		writeToOutputStream(text);
		return this;
	}

	@Override
	public StreamTextWriter append(String str) {
		writeToOutputStream(str);
		return this;
	}

	@Override
	public GraphTextWriter appendWithData(String str, HighlightData data) {
		writeToOutputStream(str);
		return this;
	}

	@Override
	public StreamTextWriter append(String str, long offset, long fileOffset) {
		writeToOutputStream(str);
		return this;
	}

	@Override
	public StreamTextWriter appendNoHilight(int i) {
		writeToOutputStream(Integer.toString(i));
		return this;
	}

	@Override
	public StreamTextWriter appendNoHilight(String str) {
		writeToOutputStream(str);
		return this;
	}

	@Override
	public StreamTextWriter indent() {
		indent++;
		return this;
	}

	@Override
	public StreamTextWriter unindent() {
		indent--;
		return this;
	}

	@Override
	public StreamTextWriter newLine() {
		writeToOutputStream(formatting.newLineChars);
		newLine = true;
		return this;
	}

	@Override
	public int getLength() {
		return writtenBytes;
	}

	@Override
	public int getIndent() {
		return indent;
	}

	private void writeToOutputStream(String str) {
		if (newLine) {
			newLine = false;
			appendIndent();
		}
		try {
			if (writer != null) {
				writer.write(str);
			} else {
				sb.append(str);
			}
			writtenBytes += str.length();
		} catch (IOException ex) {
			Logger.getLogger(StreamTextWriter.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void appendIndent() {
		for (int i = 0; i < indent; i++) {
			writeToOutputStream(formatting.indentString);
		}
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}
