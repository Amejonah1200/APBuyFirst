package ap.apb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import ap.apb.anvilgui.AnvilGUI;
import ap.apb.anvilgui.AnvilGUI.AnvilClickEventHandler;
import ap.apb.anvilgui.mc1_8.AnvilGUI_v1_8_R1;
import ap.apb.anvilgui.mc1_8.AnvilGUI_v1_8_R2;
import ap.apb.anvilgui.mc1_8.AnvilGUI_v1_8_R3;
import ap.apb.apbuy.itoomel.ICats;
import ap.apb.apbuy.itoomel.Itoomel;
import ap.apb.apbuy.itoomel.ItoomelCat;
import ap.apb.apbuy.itoomel.ItoomelTask;
import ap.apb.apbuy.markets.MarketHandler;
import ap.apb.cmds.APBCmd;
import ap.apb.cmds.ItoomelCmd;
import ap.apb.datamaster.Database;
import ap.apb.datamaster.SQLDatabase;
import ap.apb.datamaster.SQLiteDatabase;
import ap.apb.nbttager.NBTTager;
import ap.apb.nbttager.mc1_8.NBTTager_v1_8_R1;
import ap.apb.nbttager.mc1_8.NBTTager_v1_8_R2;
import ap.apb.nbttager.mc1_8.NBTTager_v1_8_R3;

public class APBuy extends JavaPlugin {

	private File PlayerMarketFolder = new File("plugins/APBuy/Markets");
	public static final String VERSION = "v0.6b";
	private File PlayerMarketStats = new File("plugins/APBuy/MarketStats.yml");
	private File STnErrors = new File("plugins/APBuy/STnErrors");
	private HashMap<String, File> playerToMarket = new HashMap<>();
	private BukkitTask Itoomeltask;
	public static APBuy plugin;
	public static NBTTager tagger;
	private boolean removeGen = false;
	public static EcoHandler ecohandler;
	private boolean generalStop = false;
	private boolean needSetup = false;
	private boolean autoCreate = true;
	private List<ItoomelCat> icatslist = new ArrayList<>();
	private MarketHandler marketHandler;
	public static Translator translator = null;
	public static Translator defaulttranslator = null;
	public static boolean german = true;
	public static boolean customtrans = false;
	public static Database database;

	@Override
	public void onEnable() {
		plugin = this;
		if (!setupNBTTager()) {
			System.err.println("[APBuy] Wrong version!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		database = new SQLiteDatabase();
		if (database instanceof SQLDatabase) {
			((SQLDatabase) database).connect();
		}
		if (this.getConfig().get("german") == null) {
			this.getConfig().set("german", german);
			this.saveConfig();
		} else {
			german = this.getConfig().getBoolean("german");
		}
		if (this.getConfig().get("customtrans") == null) {
			this.getConfig().set("customtrans", customtrans);
			this.saveConfig();
		} else {
			customtrans = this.getConfig().getBoolean("customtrans");
		}
		if (customtrans) {
			if (german) {
				defaulttranslator = Translator.createTranslatorDE();
			} else {
				defaulttranslator = Translator.createTranslatorEN();
			}
		} else {
			if (german) {
				translator = Translator.createTranslatorDE();
			} else {
				translator = Translator.createTranslatorEN();
			}
		}
		this.setMarketHandler(new MarketHandler());
		if (this.getConfig().get("AutoCreateAccount") == null) {
			this.getConfig().set("AutoCreateAccount", autoCreate);
			this.saveConfig();
		} else {
			autoCreate = this.getConfig().getBoolean("AutoCreateAccount");
		}

		if (EcoHandler.isEcoInstalled()) {
			ecohandler = new EcoHandler();
			EcoHandler.setupEconomy();
		}
		// Future vvvvvvvvvvvvvv
		// if (isLatest()) {
		// System.out.println("[APBuy] APBuy is on the latest version.");
		// } else {
		// System.out.println("[APBuy] You musst send \"/apb update\" to
		// configure.");
		// }
		// BanManager.onEnable();
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
		getCommand("apb").setExecutor(new APBCmd());
		getCommand("itoomel").setExecutor(new ItoomelCmd());
		Bukkit.getPluginManager().registerEvents(APBuy.getMarketHandler(), this);
		Bukkit.getPluginManager().registerEvents(new Itoomel(), this);
		if (autoCreate && (!generalStop)) {
			Bukkit.getPluginManager().registerEvents(new Listener() {
				@EventHandler
				public void onJoin(PlayerJoinEvent e) {
					ecohandler.createAccount(e.getPlayer());
				}
			}, this);
		}
		ICats icats = new ICats();
		icatslist.clear();
		icatslist.add(icats.new ICatFeaturedBB());
		icatslist.add(icats.new ICatOres());
		icatslist.add(icats.new ICatBattle());
		icatslist.add(icats.new ICatSpawnEggs());
		icatslist.add(icats.new ICatArmor());
		icatslist.add(icats.new ICatDiaTools());
		icatslist.add(icats.new ICatIronTools());
		icatslist.add(icats.new ICatGoldTools());
		icatslist.add(icats.new ICatStoneTools());
		icatslist.add(icats.new ICatWoodenTools());
		icatslist.add(icats.new ICatTools());
		icatslist.add(icats.new ICatNether());
		icatslist.add(icats.new ICatEnchantedBooks());
		icatslist.add(icats.new ICatDye());
		icatslist.add(icats.new ICatPotions());
		icatslist.add(icats.new ICatFood());
		icatslist.add(icats.new ICatBrewing());
		icatslist.add(icats.new ICatRedstone());

		ItoomelCat icat = new ItoomelCat("§7Other stuff", Material.CHEST,
				"Hier wird alles sein was nicht einsortiert werden konnte.") {
			@Override
			public ItoomelCat registerMats() {
				List<Material> mats = new ArrayList<>();
				for (ItoomelCat icat2 : icatslist) {
					mats.addAll(icat2.getCatMats().keySet());
				}
				for (Material mat : Material.values()) {
					if (!mats.contains(mat)) {
						this.addMat(mat);
					}
				}
				return this;
			}
		};
		icat.registerMats();
		icatslist.add(icat);
		System.out.println("[APB] Starting Itoomel...");
		removeGen = !generalStop;
		generalStop = true;
		Bukkit.getScheduler().runTask(this, new ItoomelTask());
		try {
			APBuy.getMarketHandler().createAdminShopWhenNotExist();
		} catch (Exception e) {
			e.printStackTrace();
			getMarketHandler().setAdminshop(null);
		}
	}

	@Override
	public void onDisable() {
		if (database instanceof SQLDatabase) {
			((SQLDatabase) database).disconnect();
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			APBuy.getMarketHandler().removeFromAll(p);
		}
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
		if (version.equals("v1_8_R1")) {
			tagger = new NBTTager_v1_8_R1();
		} else if (version.equals("v1_8_R2")) {
			tagger = new NBTTager_v1_8_R2();
		} else if (version.equals("v1_8_R3")) {
			tagger = new NBTTager_v1_8_R3();
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
		if (version.equals("v1_8_R1")) {
			anvilgui = new AnvilGUI_v1_8_R1(p, h);
		} else if (version.equals("v1_8_R2")) {
			anvilgui = new AnvilGUI_v1_8_R2(p, h);
		} else if (version.equals("v1_8_R3")) {
			anvilgui = new AnvilGUI_v1_8_R3(p, h);
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
		return this.removeGen;
	}

	public void setRemoveGen(boolean removeGen) {
		this.removeGen = removeGen;
	}

	public boolean isGeneralStop() {
		return this.generalStop;
	}

	public void setGeneralStop(boolean generalStop) {
		this.generalStop = generalStop;
	}

	public boolean isNeedSetup() {
		return this.needSetup;
	}

	public void setNeedSetup(boolean needSetup) {
		this.needSetup = needSetup;
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

	public static boolean isCustomtrans() {
		return customtrans;
	}

}
