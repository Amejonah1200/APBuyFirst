package ap.apb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Translator {
	private HashMap<String, String> translated;

	public Translator(HashMap<String, String> translated) {
		this.translated = translated;
	}

	public String trans(String toTranslate) {
		String s = (this == APBuy.defaulttranslator) || (!APBuy.isCustomtrans())
				? this.translated.containsKey(toTranslate) ? this.translated.get(toTranslate) : toTranslate
				: this.translated.containsKey(toTranslate) ? this.translated.get(toTranslate)
						: APBuy.defaulttranslator.trans(toTranslate);
		return s.startsWith("redirect:") ? this.trans(s.replaceFirst("redirect:", "")) : s;
	}

	public static String translate(String toTranslate) {
		return APBuy.translator.trans(toTranslate);
	}

	public static String translate(String toTranslate, Object[] objs) {
		String s = APBuy.translator.trans(toTranslate);
		String[] ss = new String[objs.length];
		for (int i = 0; i < objs.length; i++) {
			ss[i] = String.valueOf(objs[i]);
		}
		for (int i = 0; i < objs.length; i++) {
			s = s.replaceAll("%s" + i + "%", ss[i]);
		}
		return s;
	}

	public HashMap<String, String> getTranslated() {
		return this.translated;
	}

	public static Translator createTranslatorFromInputStream(InputStream input) {
		HashMap<String, String> toCreate = new HashMap<>();
		List<String> ss = new ArrayList<>();
		ss.clear();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String zeile = "";
			while ((zeile = br.readLine()) != null) {
				ss.add(zeile);
//				System.out.println(zeile);
			}
			br.close();
		} catch (Exception e1) {
			return null;
		}
		for (String s : ss) {
			if (s.contains("=")) {
				toCreate.put(s.split("=")[0], getTranslatedString(s));
			}
		}
//		List<String> ss1 = new ArrayList<>();
//		ss1.add("--Keys--");
//		ss1.addAll(toCreate.keySet());
//		ss1.add("--Keys--");
//		ss1.add("--Values--");
//		for (String s : toCreate.keySet()) {
//			ss1.add(toCreate.get(s));
//		}
//		ss1.add("--Values--");
//		FileWriter fw = null;
//		BufferedWriter bw;
//		try {
//			fw = new FileWriter(APBuy.plugin.getSTnErrors() + "/TranstextsTest.txt");
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		bw = new BufferedWriter(fw);
//		try {
//			for (String s : ss1) {
//				bw.write(s);
//				bw.newLine();
//			}
//			bw.close();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		return new Translator(toCreate);
	}

	private static String getTranslatedString(String s) {
		String[] ss = s.split("=");
		ss[0] = "";
		return String.join("=", ss).replaceFirst("=", "");
	}

	public static Translator createTranslatorDE() {
		return createTranslatorFromInputStream(APBuy.plugin.getResource("DE.lang"));
	}

	public static Translator createTranslatorEN() {
		return createTranslatorFromInputStream(APBuy.plugin.getResource("EN.lang"));
	}

}
