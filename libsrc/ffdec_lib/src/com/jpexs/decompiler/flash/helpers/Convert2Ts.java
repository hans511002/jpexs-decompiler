package com.jpexs.decompiler.flash.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Convert2Ts {
	static final Logger logger = Logger.getLogger(HighlightedTextWriter.class.getName());

	// ����ĸ��д
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
			val = "std.MCButton";
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
		} else if (val.equals("MouseEvent.MOUSE_DOWN")) {
			val = "egret.TouchEvent.TOUCH_BEGIN";
		} else if (val.equals("MouseEvent.MOUSE_UP")) {
			val = "egret.TouchEvent.TOUCH_END";
		} else if (val.equals("MouseEvent.MOUSE_MOVE")) {
			val = "egret.TouchEvent.TOUCH_MOVE";
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
		while (val.indexOf("public static public") > 0) {
			val = val.replace("public static public", "public static");
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
		// logger.info("tmpWriter=" + val);
		return val;
	}

	public static String convertCode(String tmpString) {
		String lines[] = tmpString.replaceAll("\\\r", "").split("\n");
		List<String> cnt = new ArrayList<String>();
		Map<String, String> members = new HashMap<String, String>();
		Map<String, String> methods = new HashMap<String, String>();
		methods.put("_sp", "any");
		convertCode(lines, members, methods, null);
		return convertCode(lines, members, methods, cnt);
	}

	static boolean special(List<String> cnt, String line) {
		if (line.indexOf(": TextField ") > 0) {
			line = line.replace(": TextField ", ": std.MCLabel");
			cnt.add(line);
			return true;
		} else if (line.indexOf("(stage)") > 0) {
			line = line.replace("(stage)", "(this.stage)");
			cnt.add(line);
			return true;
		} else if (line.indexOf("MouseEvent.MOUSE_DOWN") > 0) {
			line = line.replace("MouseEvent.MOUSE_DOWN", "egret.TouchEvent.TOUCH_BEGIN");
			cnt.add(line);
			return true;
		} else if (line.indexOf("MouseEvent.MOUSE_UP") > 0) {
			line = line.replace("MouseEvent.MOUSE_UP", "egret.TouchEvent.TOUCH_END");
			cnt.add(line);
			return true;
		} else if (line.indexOf("MouseEvent.MOUSE_MOVE") > 0) {
			line = line.replace("MouseEvent.MOUSE_MOVE", "egret.TouchEvent.TOUCH_MOVE");
			cnt.add(line);
			return true;
		} else if (line.indexOf(":* = ") > 0) {
			line = line.replace(":* = ", ":any = ");
			cnt.add(line);
			return true;
		} else if (line.indexOf("new Sound(") > 0) {
			line = line.replace("new Sound(", "new egret.Sound(");
			cnt.add(line);
			return true;
		} else if (line.indexOf(" extends Sprite") > 0) {
			line = line.replace(" extends Sprite", " extends egret.Sprite");
			cnt.add(line);
			return true;
		} else if (line.indexOf(" extends MovieClip") > 0) {
			line = line.replace(" extends MovieClip", " extends std.MovieClip");
			cnt.add(line);
			return true;
		} else if (line.indexOf(": Array = null") > 0) {
			line = line.replace(": Array = null", ": any = []");
			cnt.add(line);
			return true;
		} else if (line.indexOf("new Sprite(") > 0) {
			line = line.replace("new Sprite(", "new egret.Sprite(");
			cnt.add(line);
			return true;
		} else if (line.indexOf(": Sprite") > 0) {
			line = line.replace(": Sprite", ": egret.Sprite");
			cnt.add(line);
			return true;
		} else if (line.indexOf(": SoundTransform") > 0) {
			line = line.replace(": SoundTransform", ": number");
			if (line.indexOf(": number = null;") > 0) {
				line = line.replace(": number = null;", ": number = 0.0;");
			}
			cnt.add(line);
			return true;
		} else if (line.indexOf("new SoundTransform") > 0) {
			line = line.replaceFirst("new SoundTransform\\(([\\d\\.]+?)(,[\\d\\.]+)?\\);", "$1;");
			if (line.indexOf("new SoundTransform") > 0) {
				line = line.replace("new SoundTransform", "");
			}
			cnt.add(line);
			return true;
		} else if (line.indexOf(".removeEventListener(") > 0
				&& !line.matches(".*\\.removeEventListener\\((.*), ?this\\);")) {
			line = line.replaceFirst("\\.removeEventListener\\((.*)\\);", ".removeEventListener($1,this);");
			cnt.add(line);
			return true;
		} else if (line.indexOf(".addEventListener(") > 0 && !line.matches(".*\\.addEventListener\\((.*), ?this\\);")) {
			line = line.replaceFirst("\\.addEventListener\\((.*)\\);", ".addEventListener($1,this);");
			cnt.add(line);
			return true;
		}
		return false;
	}

	public static String convertCode(String lines[], Map<String, String> members, Map<String, String> methods,
			List<String> cnt) {
		boolean newLine = false;
		boolean inModule = false;
		boolean inClass = false;
		boolean inMethod = false;
		boolean inMem = false;
		int dkhNum = 0;
		String[] memSorts = new String[0];
		if (members.size() > 1)
			memSorts = new ArrayList<String>(members.keySet()).toArray(new String[0]);
		if (memSorts.length > 1)
			Arrays.sort(memSorts, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o2.compareTo(o1);
				}
			});

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			String lineCnt = line.trim();
			if (lineCnt.isEmpty()) {
				if (!newLine) {
					if (i < lines.length - 1) {
						if (lines[i + 1].trim().isEmpty()) {
							if (cnt != null)
								cnt.add("");
						}
					}
					continue;
				} else {
					if (cnt != null)
						cnt.add("");
					continue;
				}
			}
			if (lineCnt.startsWith("//")) {
				continue;
			}
			if (i < 10 && !inModule && lineCnt.startsWith("module ")) {
				inModule = true;
				if (cnt != null)
					cnt.add(line);
				continue;
			}
			if (i < 20 && !inClass && lineCnt.startsWith("export class ")) {
				inClass = true;
				inMem = true;
				if (cnt != null)
					cnt.add(line);
				continue;
			}
			if (i < 20 && !inClass && lineCnt.startsWith("class ")) {
				inClass = true;
				inMem = true;
				if (cnt != null)
					cnt.add(line);
				continue;
			}
			if (cnt != null) {
				if (special(cnt, line))
					continue;
			}
			if (inClass) {
				if (inMem) {//
					Pattern var = Pattern.compile("(\\w+): ([\\w\\.]*) = (.*);");
					Matcher m = var.matcher(lineCnt);
					if (m.find()) {
						String memName = m.group(1);
						String memType = m.group(2);
						String memVal = m.group(3);
						members.put(memName, memVal);
						if (cnt != null)
							cnt.add(line);
						continue;
					}
				}
				if (!inMethod && lineCnt.matches("public \\w+\\(.*?\\)(: \\w+)? ?\\{?")) {
					if (lines[i + 1].trim().equals("{") || lineCnt.endsWith("{")) {
						Pattern var = Pattern.compile("public (\\w+)\\((.*?)\\)(: \\w+)?");
						Matcher m = var.matcher(lineCnt);
						if (m.find()) {
							String methodName = m.group(1);
							if (!"constructor".equals(methodName)) {
								String r = m.group(3);
								if (r != null && !r.isEmpty()) {
									r = r.substring(1).trim();
								}
								methods.put(methodName, m.group(3));
							}
						}
						inMethod = true;
						inMem = false;
						dkhNum++;
						if (cnt != null)
							cnt.add("");
						if (cnt != null) {
							if (lines[i + 1].trim().equals("{")) {
								line += "{";
								i++;
							}
							cnt.add(line);
						}
						continue;
					}
					// } else if (inMethod && lineCnt.equals("{")) {
					// dkhNum++;
				} else if (inMethod && (lineCnt.equals("}") || lineCnt.startsWith("}"))) {
					dkhNum--;
				}
				if (inMethod && dkhNum == 0) {
					inMethod = false;
					newLine = true;
				}
				if (inMethod && cnt != null) {
					Pattern var = Pattern.compile(" (\\w+)\\((.*)\\)");
					Matcher m = var.matcher(lineCnt);
					if (m.find()) {
						String tmp = m.group(1);
						if (methods.containsKey(tmp)) {
							line = line.substring(0, line.indexOf(lineCnt));
							line += m.replaceAll(" this.$1($2)");
						}
					}
					var = Pattern.compile("^(\\w+)\\((.*)\\)");
					m = var.matcher(lineCnt);
					if (m.find()) {
						String tmp = m.group(1);
						if (methods.containsKey(tmp)) {
							line = line.substring(0, line.indexOf(lineCnt));
							line += m.replaceAll("this.$1($2)");
						}
					}

					var = Pattern.compile("^(\\w+)\\.");
					m = var.matcher(lineCnt);
					if (m.find()) {
						String tmp = m.group(1);
						if (members.containsKey(tmp)) {
							line = line.substring(0, line.indexOf(lineCnt));
							line += m.replaceAll("this.$0");
						}
					}
					var = Pattern.compile("this\\.(\\w+?)\\.");
					m = var.matcher(lineCnt);
					if (m.find()) {
						String tmp = m.group(1);
						if (!members.containsKey(tmp)) {
							for (String string : memSorts) {
								if (tmp.length() > string.length() && tmp.startsWith(string)) {// this.skinIcon_clIcon2Bg_cl.gotoAndStop(2);
									line = line.substring(0, line.indexOf(lineCnt));
									String l = tmp.substring(string.length());
									String lt = l.length() > 1 ? l.substring(1) : "";
									String slineline = "this." + string + "." + l.substring(0, 1).toLowerCase() + lt
											+ ".";
									line += m.replaceFirst(slineline);
									break;
								}
							}
						}
					}
				}
			}

			if (lines.length - 1 > i) {
				if (inMethod && (lines[i + 1].trim().equals("{") || lineCnt.endsWith("{"))) {
					if (cnt != null) {
						if (lines[i + 1].trim().equals("{")) {
							line += "{";
							i++;
						}
					}
					dkhNum++;
				}
			}
			if (cnt != null)
				cnt.add(line);
		}
		if (cnt == null)
			return "";
		StringBuffer sb = new StringBuffer();
		String lastLine = "";
		for (String str : cnt) {
			if (lastLine.isEmpty() && str.isEmpty()) {
				continue;
			}
			sb.append(str);
			sb.append("\n");
			lastLine = str;
		}
		return sb.toString();
	}
}
