package com.jpexs.decompiler.flash.helpers;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Convert2Ts {
	static final Logger logger = Logger.getLogger(HighlightedTextWriter.class.getName());

	// Ê××ÖÄ¸´óÐ´
	// (^|\W*)(\w*)
	//
	//
	// container->(\w*)->(^|\W*)(\w*)->
	// container->$1$2\\u $3->

	public static void main(String[] argv) {
		String name = "dgdsg.dgdg.dg.fe.gre.ht.rh.tfdf";
		java.util.regex.Pattern number = Pattern.compile("(\\w+)\\.(\\w+)\\.(\\w+)\\.(\\w+)");
		Matcher m = number.matcher(name);
		while (m.find()) {
			name = m.replaceFirst("$1") + "." + m.group(2) + toUp(m.group(3) + "." + m.group(2));
			m = number.matcher(name);
		}
		System.err.println(name);
	}

	public static String toUp(String val) {
		return val.substring(0, 1).toUpperCase() + val.substring(1);
	}

	public static String convertType(String val) {
		if (val.equals("MovieClip")) {
			val = "std.MovieClip";
		} else if (val.equals("SimpleButton")) {
			val = "std.MCSimpleButton";
		} else if (val.equals("Sound")) {
			val = "egret.Sound";
		} else if (val.equals("SoundChannel")) {
			val = "egret.SoundChannel";
		} else if (val.equals("int") || val.equals("Number") || val.equals("uint")) {
			val = "number";
		} else if (val.equals("Boolean")) {
			val = "boolean";
		} else if (val.equals("Event")) {
			val = "egret.Event";
		} else if (val.startsWith("Event.")) {
			val = "egret." + val;
		} else if (val.equals("Event.ENTER_FRAME")) {
			val = "egret.Event.ENTER_FRAME";
		} else if (val.equals("Event.ADDED_TO_STAGE")) {
			val = "egret.Event.ADDED_TO_STAGE";
		}
		return val;

	}

	static java.util.regex.Pattern number = Pattern.compile("number\\((.*)\\)");
	static java.util.regex.Pattern sety = Pattern.compile("\\.y = (.*);");
	static java.util.regex.Pattern setx = Pattern.compile("\\.x = (.*);");
	static java.util.regex.Pattern visible = Pattern.compile("\\.visible = (.*) ?;");
	// addChild(_loc2_);
	static java.util.regex.Pattern addFrameScript = Pattern.compile("addFrameScript\\((.*)\\) ?;");
	static java.util.regex.Pattern thisaddFrameScript = Pattern.compile("\\.addFrameScript\\((.*)\\) ?;");

	static java.util.regex.Pattern addChild = Pattern.compile("addChild\\((.*)\\) ?;");
	static java.util.regex.Pattern thisAddChild = Pattern.compile("\\.addChild\\((.*)\\) ?;");

	static java.util.regex.Pattern removeChild = Pattern.compile("removeChild\\((.*)\\) ?;");
	static java.util.regex.Pattern thisRemoveChild = Pattern.compile("\\.removeChild\\((.*)\\) ?;");

	static java.util.regex.Pattern stop = Pattern.compile("stop\\((.*)\\) ?;");
	static java.util.regex.Pattern thisstop = Pattern.compile("\\.stop\\((.*)\\) ?;");

	static java.util.regex.Pattern play = Pattern.compile("play\\((.*)\\) ?;");
	static java.util.regex.Pattern thisplay = Pattern.compile("\\.play\\((.*)\\) ?;");

	static java.util.regex.Pattern addEventListener = Pattern.compile("addEventListener\\((.*)\\) ?;");
	static java.util.regex.Pattern thisaddEventListener = Pattern.compile("\\.addEventListener\\((.*)\\) ?;");
	static java.util.regex.Pattern removeEventListener = Pattern.compile("removeEventListener\\((.*)\\) ?;");
	static java.util.regex.Pattern thisRemoveEventListener = Pattern.compile("\\.removeEventListener\\((.*)\\) ?;");

	static java.util.regex.Pattern mcCtl = Pattern
			.compile("this\\.([\\w.]+)\\.((gotoAndPlay\\()|(gotoAndStop\\()|(play\\()|(stop\\())");
	static java.util.regex.Pattern thisMemMc = Pattern
			.compile("(\\w+)\\.(\\w+)\\.(\\w+)\\.((gotoAndPlay\\()|(gotoAndStop\\()|(play\\()|(stop\\())");
	static java.util.regex.Pattern objPlay = Pattern
			.compile("(\\w+)\\.(\\w+)\\.(\\w+)\\.((gotoAndPlay\\()|(gotoAndStop\\()|(play\\()|(stop\\())");

	static java.util.regex.Pattern _rnd = Pattern.compile("([ \\(])_rnd\\((\\d*)\\)");
	static java.util.regex.Pattern _int = Pattern.compile("([ \\(])int\\((.*)\\)");

	public static String convertLine(String val) {
		if (val.trim().isEmpty())
			return val;
		String lines[] = val.split("\\n");
		if (lines.length > 1) {
			return val;
		}
		while (val.indexOf("public public") > 0) {
			val = val.replace("public public", "public");
		}
		while (val.indexOf("this.this.") > 0) {
			val = val.replace("this.this.", "this.");
		}
		if (val.length() < 10)
			return val;

		// Event.ADDED_TO_STAGE
		// this.removeEventListener(Event.ADDED_TO_STAGE,this.init);
		// this.sfx_bt.addEventListener(MouseEvent.CLICK,this.click_sfx_f);
		// this.music_bt.addEventListener(MouseEvent.CLICK,this.click_music_f);
		if (val.indexOf("(Event.") >= 0) {
			val = val.replaceAll("\\(Event\\.", "(egret.Event.");
		}
		if (val.indexOf("(MouseEvent.CLICK") >= 0) {
			val = val.replaceAll("\\(MouseEvent\\.CLICK", "(egret.TouchEvent.TOUCH_TAP");
		}
		if (val.indexOf(": MouseEvent") >= 0) {
			val = val.replaceAll(": MouseEvent", ": egret.TouchEvent");
		}

		val = val.replaceAll("([ \\(])stage\\.", "$1this.stage\\.");
		Matcher m = number.matcher(val);
		if (m.find()) {
			val = m.replaceAll("$1");
		}
		m = _rnd.matcher(val);
		if (m.find()) {
			val = m.replaceAll("$1std._rnd($2)");
		}
		m = _int.matcher(val);
		if (m.find()) {
			val = m.replaceAll("$1Math.floor($2)");
		}
		m = mcCtl.matcher(val);
		if (m.find()) {
			m = thisMemMc.matcher(val);
			while (m.find()) {
				String g1 = m.group(1);
				if (g1.equals("this")) {

					val = m.replaceFirst("$1" + "." + m.group(2) + toUp(m.group(3)) + "." + m.group(4));
					m = thisMemMc.matcher(val);
				} else {
					val = m.replaceFirst("$1" + toUp(m.group(2)) + toUp(m.group(3)) + "." + m.group(4));
					m = thisMemMc.matcher(val);
				}
			}
		} else {
			m = objPlay.matcher(val);
			while (m.find()) {
				val = m.replaceFirst("$1" + "." + m.group(2) + toUp(m.group(3)) + "." + m.group(4));
				m = objPlay.matcher(val);
			}
		}

		m = setx.matcher(val);
		if (m.find()) {
			val = m.replaceAll(".\\$setX($1);");
		}
		m = sety.matcher(val);
		if (m.find()) {
			val = m.replaceAll(".\\$setY($1);");
		}
		m = visible.matcher(val);
		if (m.find()) {
			val = m.replaceAll(".\\$setVisible($1);");
		}
		m = addFrameScript.matcher(val);
		if (m.find()) {
			if (!thisaddFrameScript.matcher(val).find())
				val = m.replaceFirst("this.addFrameScript($1);");
		}
		m = addChild.matcher(val);
		if (m.find()) {
			if (!thisAddChild.matcher(val).find())
				val = m.replaceFirst("this.addChild($1);");
		}
		m = removeChild.matcher(val);
		if (m.find()) {
			if (!thisRemoveChild.matcher(val).find())
				val = m.replaceFirst("this.removeChild($1);");
		}
		m = play.matcher(val);
		if (m.find()) {
			if (!thisplay.matcher(val).find())
				val = m.replaceFirst("this.play($1);");
		}
		m = stop.matcher(val);
		if (m.find()) {
			if (!thisstop.matcher(val).find())
				val = m.replaceFirst("this.stop($1);");
		}
		m = addEventListener.matcher(val);
		if (m.find()) {
			if (!thisaddEventListener.matcher(val).find())
				val = m.replaceFirst("this.addEventListener($1);");
		}
		m = removeEventListener.matcher(val);
		if (m.find()) {
			if (!thisRemoveEventListener.matcher(val).find())
				val = m.replaceFirst("this.removeEventListener($1);");
		}
		logger.info("tmpWriter=" + val);
		return val;
	}
}
