/*
 *  Copyright (C) 2010-2018 JPEXS
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.decompiler.flash.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author JPEXS
 */
public class LogFormatter extends Formatter {

	private static final String lineSep = System.getProperty("line.separator");

	private DateFormat dateFormat;
	static boolean printClass = true;

	@Override
	public String format(LogRecord record) {
		StringBuilder buf = new StringBuilder(180);

		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		}

		buf.append(dateFormat.format(new Date(record.getMillis())));
		buf.append(" > ");

		if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
			buf.append(record.getLevel());
			buf.append(": ");
		}
		if (printClass) {
			StackTraceElement stes[] = Thread.currentThread().getStackTrace();
			StackTraceElement ste = null;
			for (StackTraceElement stackTraceElement : stes) {
				if (stackTraceElement.getClassName().equals(
						record.getSourceClassName())
						&& stackTraceElement.getMethodName().equals(
								record.getSourceMethodName())) {
					ste = stackTraceElement;
					break;
				}
			}
			if (ste == null) {
				ste = stes[7];
			}
			buf.append(record.getSourceClassName());
			buf.append(".");
			buf.append(record.getSourceMethodName());
			buf.append("(");
			buf.append(ste.getFileName());
			buf.append(":");
			buf.append(ste.getLineNumber());
			buf.append(") ");
		}

		// buf.append(ste.getMethodName());
		buf.append(formatMessage(record));
		buf.append(lineSep);
		Throwable throwable = record.getThrown();
		if (throwable != null) {
			StringWriter sink = new StringWriter();
			throwable.printStackTrace(new PrintWriter(sink, true));
			buf.append(record.getSourceClassName());
			buf.append(' ');
			buf.append(record.getSourceMethodName());
			buf.append(lineSep);
			buf.append(sink.toString());
		}

		return buf.toString();
	}
}
