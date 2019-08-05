/**
 * @author Amejonah1200
 *
 *         APBuy: Better than a marketplace plugin.
 * 
 *         Copyright (C) 2019 Amejonah1200
 *         This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as
 *         published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 * 
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *         GNU Affero General Public License for more details.
 * 
 *         You should have received a copy of the GNU Affero General Public
 *         License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package ap.apb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import ap.apb.anvilgui.AnvilGUI;
import ap.apb.anvilgui.AnvilGUI.AnvilClickEventHandler;
import ap.apb.anvilgui.mc1_12.AnvilGUI_v1_12_R1;
import ap.apb.anvilgui.mc1_8.AnvilGUI_v1_8_R1;
import ap.apb.anvilgui.mc1_8.AnvilGUI_v1_8_R2;
import ap.apb.anvilgui.mc1_8.AnvilGUI_v1_8_R3;
import ap.apb.apbuy.itoomel.Itoomel;
import ap.apb.apbuy.itoomel.ItoomelCat;
import ap.apb.apbuy.markets.ItemDepot;
import ap.apb.apbuy.markets.MarketHandler;
import ap.apb.cmds.APBCmd;
import ap.apb.cmds.ItoomelCmd;
import ap.apb.datamaster.Datamaster;
import ap.apb.nbttager.NBTTager;
import ap.apb.nbttager.mc1_12.NBTTager_v1_12_R1;
import ap.apb.nbttager.mc1_8.NBTTager_v1_8_R1;
import ap.apb.nbttager.mc1_8.NBTTager_v1_8_R2;
import ap.apb.nbttager.mc1_8.NBTTager_v1_8_R3;

public class APBuy extends JavaPlugin {

	public static final String VERSION = "v0.6b";

	public static Translator translator = null;
	public static boolean german = true;
	public static NBTTager tagger;
	public static Itoomel itoomel;
	public static ItemDepot itemDepot;
	public static APBuy plugin;
	public static EcoHandler ecohandler;
	public static boolean genStopByPlugin = false;
	private static boolean removeGen = false;
	private static boolean generalStop = false;
	public static StopCause stopCause = StopCause.NONE;
	private static Datamaster datamaster;

	private File PlayerMarketFolder = new File("plugins/APBuy/Markets");
	private File PlayerMarketStats = new File("plugins/APBuy/MarketStats.yml");
	private File STnErrors = new File("plugins/APBuy/STnErrors");
	private BukkitTask Itoomeltask;
	private MarketHandler marketHandler;
	private boolean autoCreate = true;
	private List<ItoomelCat> icatslist = new ArrayList<>();
	private HashMap<String, File> playerToMarket = new HashMap<>();

	@Override
	public void onEnable() {
		plugin = this;
		if (!setupNBTTager()) {
			System.err.println("[APBuy] Wrong version!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		datamaster = new Datamaster(this);
		itemDepot = new ItemDepot(datamaster.getDatabase());
		if (this.getConfig().get("german") == null) {
			this.getConfig().set("german", german);
			this.saveConfig();
		} else {
			german = this.getConfig().getBoolean("german");
		}
		if (german) {
			translator = Translator.createTranslatorDE();
		} else {
			translator = Translator.createTranslatorEN();
		}
		this.setMarketHandler(new MarketHandler(datamaster.getDatabase()));
		if (this.getConfig().get("AutoCreateAccount") == null) {
			this.getConfig().set("AutoCreateAccount", autoCreate);
			this.saveConfig();
		} else {
			autoCreate = this.getConfig().getBoolean("AutoCreateAccount");
		}

		if (EcoHandler.isEcoInstalled()) {
			ecohandler = new EcoHandler();
			EcoHandler.setupEconomy();
		} else {
			System.err.println("[APBuy] Vault ist isn't installed or enabled!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		STnErrors.mkdirs();
		PlayerMarketFolder.mkdirs();
		try {
			if (PlayerMarketStats.exists()) {
				if ((!PlayerMarketStats.canRead()) && (!PlayerMarketStats.canWrite())) {
					PlayerMarketStats.createNewFile();
				}
			} else {
				PlayerMarketStats.createNewFile();
			}
		} catch (IOException e) {
		}
		this.saveConfig();
		APBCmd apbcmd = new APBCmd();
		getCommand("apb").setExecutor(apbcmd);
		getCommand("itoomel").setExecutor(new ItoomelCmd());
		Bukkit.getPluginManager().registerEvents(APBuy.getMarketHandler(), this);
		Bukkit.getPluginManager().registerEvents(itemDepot, this);
		Bukkit.getPluginManager().registerEvents(apbcmd, this);
		if (autoCreate && (!generalStop)) {
			Bukkit.getPluginManager().registerEvents(new Listener() {
				@EventHandler
				public void onJoin(PlayerJoinEvent e) {
					ecohandler.createAccount(e.getPlayer());
				}
			}, this);
		}

		// ICats icats = new ICats();
		// icatslist.clear();
		// icatslist.add(icats.new ICatFeaturedBB());
		// icatslist.add(icats.new ICatOres());
		// icatslist.add(icats.new ICatBattle());
		// icatslist.add(icats.new ICatSpawnEggs());
		// icatslist.add(icats.new ICatArmor());
		// icatslist.add(icats.new ICatDiaTools());
		// icatslist.add(icats.new ICatIronTools());
		// icatslist.add(icats.new ICatGoldTools());
		// icatslist.add(icats.new ICatStoneTools());
		// icatslist.add(icats.new ICatWoodenTools());
		// icatslist.add(icats.new ICatTools());
		// icatslist.add(icats.new ICatNether());
		// icatslist.add(icats.new ICatEnchantedBooks());
		// icatslist.add(icats.new ICatDye());
		// icatslist.add(icats.new ICatPotions());
		// icatslist.add(icats.new ICatFood());
		// icatslist.add(icats.new ICatBrewing());
		// icatslist.add(icats.new ICatRedstone());
		// ItoomelCat icat = new ItoomelCat("§7Other stuff", Material.CHEST,
		// "Hier wird alles sein was nicht einsortiert werden konnte.") {
		// @Override
		// public ItoomelCat registerMats() {
		// List<Material> mats = new ArrayList<>();
		// for (ItoomelCat icat2 : icatslist) {
		// mats.addAll(icat2.getCatMats().keySet());
		// }
		// for (Material mat : Material.values()) {
		// if (!mats.contains(mat)) {
		// this.addMat(mat);
		// }
		// }
		// return this;
		// }
		// };
		// icat.registerMats();
		// icatslist.add(icat);

		System.out.println("[APB] Starting Itoomel...");
		itoomel = new Itoomel();
		Bukkit.getPluginManager().registerEvents(itoomel, this);
		try {
			APBuy.getMarketHandler().createAdminShopWhenNotExist();
		} catch (Exception e) {
			e.printStackTrace();
			getMarketHandler().setAdminshop(null);
		}
	}

	@Override
	public void onDisable() {
		APBuy.datamaster.disconnect();
		for (Player p : Bukkit.getOnlinePlayers()) {
			APBuy.getMarketHandler().removeFromAll(p);
		}
	}

	public static Datamaster getDatamaster() {
		return datamaster;
	}

	public static boolean hasPlayerModPerms(Player p) {
		return ((p.hasPermission("apb.mod.*")) || (p.hasPermission("apb.mod.status"))
				|| (p.hasPermission("apb.mod.ban.mymarketban")) || (p.hasPermission("apb.mod.ban.marketsban"))
				|| (p.hasPermission("apb.mod.ban.itoomelban")) || (p.hasPermission("apb.mod.ban.*"))
				|| (p.hasPermission("apb.mod.reset")) || (p.hasPermission("apb.mod.delete"))
				|| (p.hasPermission("apb.mod.itoomel")));
	}

	private boolean setupNBTTager() {

		String version;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		} catch (Exception e) {
			return false;
		}
		switch (version) {
		case "v1_8_R1":
			tagger = new NBTTager_v1_8_R1();
			break;
		case "v1_8_R2":
			tagger = new NBTTager_v1_8_R2();
			break;
		case "v1_8_R3":
			tagger = new NBTTager_v1_8_R3();
			break;
		case "v1_12_R1":
			tagger = new NBTTager_v1_12_R1();
			break;
		}
		return tagger != null;
	}

	public static AnvilGUI anvilgui(Player p, AnvilClickEventHandler h) {
		String version;
		AnvilGUI anvilgui = null;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		} catch (Exception e) {
			return null;
		}
		switch (version) {
		case "v1_8_R1":
			anvilgui = new AnvilGUI_v1_8_R1(p, h);
			break;
		case "v1_8_R2":
			anvilgui = new AnvilGUI_v1_8_R2(p, h);
			break;
		case "v1_8_R3":
			anvilgui = new AnvilGUI_v1_8_R3(p, h);
			break;
		case "v1_12_R1":
			anvilgui = new AnvilGUI_v1_12_R1(p, h);
			break;
		}
		return anvilgui;
	}

	public static MarketHandler getMarketHandler() {
		return APBuy.plugin.marketHandler;
	}

	public void setMarketHandler(MarketHandler marketHandler) {
		APBuy.plugin.marketHandler = marketHandler;
	}

	public File getPlayerMarketFolder() {
		return this.PlayerMarketFolder;
	}

	public void setPlayerMarketFolder(File playerMarketFolder) {
		this.PlayerMarketFolder = playerMarketFolder;
	}

	public File getPlayerMarketStats() {
		return this.PlayerMarketStats;
	}

	public void setPlayerMarketStats(File playerMarketStats) {
		this.PlayerMarketStats = playerMarketStats;
	}

	public File getSTnErrors() {
		return this.STnErrors;
	}

	public void setSTnErrors(File sTnErrors) {
		STnErrors = sTnErrors;
	}

	public HashMap<String, File> getPlayerToMarket() {
		return this.playerToMarket;
	}

	public void setPlayerToMarket(HashMap<String, File> playerToMarket) {
		this.playerToMarket = playerToMarket;
	}

	public BukkitTask getItoomeltask() {
		return this.Itoomeltask;
	}

	public void setItoomeltask(BukkitTask itoomeltask) {
		Itoomeltask = itoomeltask;
	}

	public boolean isRemoveGen() {
		return removeGen;
	}

	public static void setRemoveGen(boolean removeGena) {
		removeGen = removeGena;
	}

	public static boolean isGeneralStop() {
		return generalStop;
	}

	public static void setGeneralStop(boolean generalStoa) {
		generalStop = generalStoa;
	}

	public boolean isAutoCreate() {
		return this.autoCreate;
	}

	public void setAutoCreate(boolean autoCreate) {
		this.autoCreate = autoCreate;
	}

	public List<ItoomelCat> getIcatslist() {
		return this.icatslist;
	}

	public void setIcatslist(List<ItoomelCat> icatslist) {
		this.icatslist = icatslist;
	}

	public static boolean isGerman() {
		return german;
	}

	public enum StopCause {
		NONE, DATABASE, TRANSFERINGDB
	}

}
