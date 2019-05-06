package ap.apb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.gson.JsonParser;

public class Utils {

	private static FileWriter fw;
	private static BufferedWriter bw;

	/**
	 * Breaks a string into a list of pieces that will fit a specified width.
	 * 
	 * @param string
	 *            Give here the String to be transformated.
	 * @param nbchars
	 *            Give here the maximal number of chars per Line.
	 */
	public static List<String> createListFromStringToWidthV2(String string, int nbchars) {
		return Arrays.asList(wrapFormattedStringToWidth(string, nbchars).split("\n"));
	}

	public static void stackAllInv(Player p) {
		ItemStack[] inv = p.getInventory().getContents().clone();
		HashMap<ItemStack, Integer> map = new HashMap<>();
		ItemStack temp = null;
		int tempint = 0;
		for (ItemStack is : inv) {
			if (is == null)
				continue;
			temp = is.clone();
			temp.setAmount(1);
			if (!map.containsKey(temp)) {
				map.put(temp, is.getAmount());
			} else {
				tempint = is.getAmount() + map.get(temp);
				map.replace(temp, tempint);
			}
		}
		p.getInventory().setContents(new ItemStack[36]);
		p.updateInventory();
		for (ItemStack is : map.keySet()) {
			addItemToPlayer(p, is, map.get(is));
		}
	}

	public static String getUuid(String name) {
		String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
		try {
			String UUIDJson = IOUtils.toString(new URL(url));
			if (UUIDJson.isEmpty())
				return "invalid name";
			JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
			return UUIDObject.get("id").toString().replaceFirst(
					"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
		} catch (Exception e) {
		}
		return "error";
	}

	public static String newHaste(String contents) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL("https://hastebin.com/documents").openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("user-agent", "Java/HastebinAPI");
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(contents);
		writer.flush();
		writer.close();
		JsonParser parser = new JsonParser();
		InputStreamReader reader = new InputStreamReader(connection.getInputStream());
		String key = parser.parse(reader).getAsJsonObject().get("key").getAsString();
		reader.close();
		return key;
	}

	public static String splitInLines(String source, int lineLength) {
		String[] words = source.split("\\s+");
		StringBuilder builder = new StringBuilder();
		int currentLength = 0;
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			int wordSize = word.length();
			assert wordSize <= lineLength;
			if ((currentLength += wordSize) > lineLength) {
				builder.append("\n");
				currentLength = 0;
				i--;
			} else {
				builder.append(word);
			}
		}
		return builder.toString();
	}

	public static int getPlaceForIS(Player p, long ammount, ItemStack is) {
		int lengxD = 0;
		for (int i = 0; i < p.getInventory().getContents().length; i++) {
			if (p.getInventory().getContents()[i] == null) {
				lengxD = lengxD + 64;
			} else if (p.getInventory().getContents()[i].isSimilar(is)) {
				lengxD = lengxD + (64 - p.getInventory().getContents()[i].getAmount());
			}
		}
		return (int) (lengxD > ammount ? ammount : lengxD);
	}

	public static int addItemToPlayer(Player target, ItemStack is, int toGive) {
		ItemStack[] iss = target.getInventory().getContents();
		ItemStack temp = is.clone();
		int b = 0;
		for (int i = 0; i < target.getInventory().getContents().length; i++) {
			if (iss[i] == null) {
				if (toGive >= 64) {
					temp.setAmount(64);
					iss[i] = temp.clone();
					toGive = toGive - 64;
				} else {
					if (toGive == 0) {
						target.getInventory().setContents(iss);
						target.updateInventory();
						return 0;
					}
					temp.setAmount(toGive);
					iss[i] = temp.clone();
					target.getInventory().setContents(iss);
					target.updateInventory();
					return 0;
				}
			} else {
				if (iss[i].isSimilar(is)) {
					b = iss[i].getAmount();
					if (toGive >= 64) {
						temp.setAmount(64);
						iss[i] = temp.clone();
						toGive = toGive - (64 - b);
					} else {
						if (toGive == 0) {
							target.getInventory().setContents(iss);
							target.updateInventory();
							return 0;
						}
						if (b + toGive > 64) {
							temp.setAmount(64);
							iss[i] = temp.clone();
							toGive = toGive + b - 64;
						} else {
							temp.setAmount(b + toGive);
							iss[i] = temp.clone();
							target.getInventory().setContents(iss);
							target.updateInventory();
							return 0;
						}
					}
				}
			}
			if (toGive == 0) {
				target.getInventory().setContents(iss);
				target.updateInventory();
				return 0;
			}
		}
		target.getInventory().setContents(iss);
		target.updateInventory();
		return toGive;
	}

	/**
	 * Breaks a string into a list of pieces that will fit a specified width.
	 * 
	 * @param string
	 *            Give here the String to be transformated.
	 * @param nbchars
	 *            Give here the maximal number of chars per Line.
	 */

	public static List<String> createListFromStringToWidthPlusEffect(String string, int nbchars) {
		String[] ss = wrapFormattedStringToWidth(string, nbchars).split("\n");
		ss[0] = "�7" + ss[0];
		for (int i = 1; i < ss.length; i++) {
			ss[i] = "�8" + ss[i];
		}
		return Arrays.asList(ss);
	}

	public static List<String> createListFromStringToWidth(String string, int nbchars) {
		return Arrays.asList(wrapFormattedStringToWidth(string, nbchars).split("\n"));
	}

	private static String wrapFormattedStringToWidth(String str, int wrapWidth) {
		int var3 = sizeStringToWidth(str, wrapWidth);

		if (str.length() <= var3) {
			return str;
		} else {
			String var4 = str.substring(0, var3);
			char var5 = str.charAt(var3);
			boolean var6 = var5 == 32 || var5 == 10;
			String var7 = getFormatFromString(var4) + str.substring(var3 + (var6 ? 1 : 0));
			return var4 + "\n" + wrapFormattedStringToWidth("   " + var7, wrapWidth);
		}
	}

	private static String getFormatFromString(String p_78282_0_) {
		String var1 = "";
		int var2 = -1;
		int var3 = p_78282_0_.length();

		while ((var2 = p_78282_0_.indexOf(167, var2 + 1)) != -1) {
			if (var2 < var3 - 1) {
				char var4 = p_78282_0_.charAt(var2 + 1);

				if (isFormatColor(var4)) {
					var1 = "\u00a7" + var4;
				} else if (isFormatSpecial(var4)) {
					var1 = var1 + "\u00a7" + var4;
				}
			}
		}

		return var1;
	}

	private static boolean isFormatSpecial(char formatChar) {
		return formatChar >= 107 && formatChar <= 111 || formatChar >= 75 && formatChar <= 79 || formatChar == 114
				|| formatChar == 82;
	}

	private static int sizeStringToWidth(String str, int wrapWidth) {
		int var3 = str.length();
		float var4 = 0.0F;
		int var5 = 0;
		int var6 = -1;

		for (boolean var7 = false; var5 < var3; ++var5) {
			char var8 = str.charAt(var5);

			switch (var8) {
			case 10:
				--var5;
				break;

			case 167:
				if (var5 < var3 - 1) {
					++var5;
					char var9 = str.charAt(var5);

					if (var9 != 108 && var9 != 76) {
						if (var9 == 114 || var9 == 82 || isFormatColor(var9)) {
							var7 = false;
						}
					} else {
						var7 = true;
					}
				}

				break;

			case 32:
				var6 = var5;

			default:
				var4 += 1;

				if (var7) {
					++var4;
				}
			}

			if (var8 == 10) {
				++var5;
				var6 = var5;
				break;
			}

			if (var4 > (float) wrapWidth) {
				break;
			}
		}

		return var5 != var3 && var6 != -1 && var6 < var5 ? var6 : var5;
	}

	private static boolean isFormatColor(char colorChar) {
		return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102
				|| colorChar >= 65 && colorChar <= 70;
	}

	public static int addToFix(Exception e) {
		int nb;
		File file;
		int i = isAllreadyRegistered(e);
		if (i == -1) {
			APBuy.plugin.getSTnErrors().mkdirs();
			nb = 0;
			file = new File(APBuy.plugin.getSTnErrors() + "/STnError" + nb + ".txt");
			while (file.exists()) {
				nb++;
				file = new File(APBuy.plugin.getSTnErrors() + "/STnError" + nb + ".txt");
			}
			try {
				fw = new FileWriter(APBuy.plugin.getSTnErrors() + "/STnError" + nb + ".txt");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bw = new BufferedWriter(fw);
			try {
				bw.write(e.getClass().getName().toString());
				bw.newLine();
				bw.write(e.getMessage() == null ? "null" : e.getMessage().toString());
				bw.newLine();
				bw.write(e.getLocalizedMessage() == null ? "null" : e.getLocalizedMessage().toString());
				bw.newLine();

				for (StackTraceElement ste : e.getStackTrace()) {
					bw.write("   at " + ste.toString());
					bw.newLine();
				}
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return nb;
		}
		return i;
	}

	private static int isAllreadyRegistered(Exception e) {
		List<String> s = new ArrayList<>();
		List<String> st = new ArrayList<>();
		st.add(e.getClass().getName().toString());
		st.add(e.getMessage() == null ? "null" : e.getMessage().toString());
		st.add(e.getLocalizedMessage() == null ? "null" : e.getLocalizedMessage().toString());
		for (StackTraceElement ste : e.getStackTrace()) {
			st.add("   at " + ste.toString());
		}
		FileReader fr;
		for (File f : APBuy.plugin.getSTnErrors().listFiles()) {
			s.clear();
			try {
				fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String zeile = "";
				while ((zeile = br.readLine()) != null) {
					s.add(zeile);
				}
				br.close();
			} catch (Exception e1) {
			}
			if (s.equals(st))
				return Integer
						.parseInt(f.getName().replaceFirst("STnError", "").replaceFirst(Pattern.quote(".txt"), ""));
		}
		return -1;
	}

	public static List<String> getAllErrors() {
		List<String> l = new ArrayList<>();
		FileReader fr;
		for (File f : APBuy.plugin.getSTnErrors().listFiles()) {
			l.add("Filename: " + f.getName());
			l.add("");
			try {
				fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String zeile = "";
				while ((zeile = br.readLine()) != null) {
					l.add(zeile);
				}
				br.close();
			} catch (Exception e1) {
			}
			l.add("");
			l.add("--------------------------------");
			l.add("");
		}
		return l;
	}

	public static List<String> getErrors(String string) {
		List<String> l = new ArrayList<>();
		FileReader fr;
		String[] s = string.split(",");
		String s2;
		for (File f : APBuy.plugin.getSTnErrors().listFiles()) {
			s2 = f.getName().replaceFirst("STnError", "").replaceFirst(Pattern.quote(".txt"), "");
			for (String s3 : s) {
				if (s3.equals(s2)) {
					l.add("Filename: " + f.getName());
					l.add("");
					try {
						fr = new FileReader(f);
						BufferedReader br = new BufferedReader(fr);
						String zeile = "";
						while ((zeile = br.readLine()) != null) {
							l.add(zeile);
						}
						br.close();
					} catch (Exception e1) {
					}
					l.add("");
					l.add("--------------------------------");
					l.add("");
					break;
				}
			}

		}
		return l;
	}

	public static Map<String, Object> jsonToMap(JSONObject json) {
		Map<String, Object> retMap = new HashMap<String, Object>();

		if (json != null) {
			retMap = toMap(json);
		}
		return retMap;
	}

	public static Map<String, Object> toMap(JSONObject object) {
		Map<String, Object> map = new HashMap<String, Object>();

		@SuppressWarnings("unchecked")
		Iterator<String> keysItr = object.keySet().iterator();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	public static List<Object> toList(JSONArray array) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.size(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}

}