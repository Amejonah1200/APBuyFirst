package ap.apb.datamaster;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import ap.apb.APBuy;
import ap.apb.VersionUtils;
import ap.apb.apbuy.markets.CategoryInfos;
import ap.apb.apbuy.markets.MarketInfos;
import ap.apb.apbuy.markets.MarketItem;

public class Datamaster {

	private boolean sqlite;
	private Database database;
	private static int number = 0;

	public Datamaster(JavaPlugin plugin) {
		String dbName = plugin.getConfig().getString("Database");
		if (dbName != null) {
			switch (dbName.toLowerCase()) {
			case "sqlite":
				this.setupSQLite();
				break;
			case "mysql":
				FileConfiguration fcg = plugin.getConfig();
				String[] ss = new String[] { fcg.getString("MySQLConfig.host"), fcg.getString("MySQLConfig.port"),
						fcg.getString("MySQLConfig.database"), fcg.getString("MySQLConfig.username"),
						fcg.getString("MySQLConfig.password") };
				boolean toConfig = false;
				for (int i = 0; i < 5; i++) {
					if (ss[i] == null) {
						toConfig = true;
						break;
					}
				}
				if (ss[0] != null) {
					if (ss[0].equals("blblabla.hoster.net")) {
						toConfig = true;
					}
				}
				if (toConfig) {
					plugin.getConfig().set("MySQLConfig.host", "blblabla.hoster.net");
					plugin.getConfig().set("MySQLConfig.port", "1200");
					plugin.getConfig().set("MySQLConfig.database", "IAm1Database");
					plugin.getConfig().set("MySQLConfig.username", "Amejonah1200");
					plugin.getConfig().set("MySQLConfig.password", "TopSecret!123");
					plugin.saveConfig();
					return;
				}
				if (!this.setupMySQL(ss[0], ss[1], ss[2], ss[3], ss[4])) {
					System.err.println("You musst give the right MySQL data in the Config.");
					System.err.println("Then reload the Plugin with [/apb rl].");
					APBuy.genStopByPlugin = true;
					return;
				} else {
					plugin.getConfig().set("Database", "mysql");
					plugin.saveConfig();
				}
				break;
			default:
				System.err.println("The Database \"" + dbName + "\" ist not valid... Switching to SQLite.");
				plugin.getConfig().set("Database", "sqlite");
				plugin.saveConfig();
				this.setupSQLite();
				break;
			}
		} else {
			plugin.getConfig().set("Database", "sqlite");
			plugin.saveConfig();
			this.setupSQLite();
		}
	}

	public boolean setDBToSQLite(boolean importing) {
		try {
			SQLDatabase db = new SQLiteDatabase();
			db.connect();
			if (importing) {
				db.getConnection().prepareStatement("DELETE FROM " + getAPBuy_MarketsTableName(true) + ";").execute();
				db.getConnection().prepareStatement("DELETE FROM " + getAPBuy_CategorysTableName(true) + ";").execute();
				db.getConnection().prepareStatement("DELETE FROM " + getAPBuy_MItemsTableName(true) + ";").execute();
				db.getConnection().prepareStatement("DELETE FROM " + getAPBuy_ItemDepotTableName(true) + ";").execute();
				for (MarketInfos mi : this.getDatabase().getAllMarketInfos()) {
					mi.save(db);
				}
				for (CategoryInfos catinfos : this.getDatabase().getAllCategoryInfos()) {
					catinfos.save(db);
				}
				for (MarketItem mis : this.getDatabase().getAllMarketItems()) {
					mis.save(db);
				}
			}
			if (this.database instanceof SQLDatabase) {
				((SQLDatabase) this.database).getConnection().close();
			}
			this.database = db;
			this.sqlite = true;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void setSqlite(boolean sqlite) {
		this.sqlite = sqlite;
	}

	public boolean setDBToMySQL(boolean importing, String host, String port, String databasename, String username,
			String password) {
		try {
			SQLDatabase db = new MySQLDatabase();
			db.connect(host, port, databasename, username, password);
			if (importing) {
				db.getConnection().prepareStatement("DELETE FROM " + getAPBuy_MarketsTableName(false) + ";").execute();
				db.getConnection().prepareStatement("DELETE FROM " + getAPBuy_CategorysTableName(false) + ";").execute();
				db.getConnection().prepareStatement("DELETE FROM " + getAPBuy_MItemsTableName(false) + ";").execute();
				db.getConnection().prepareStatement("DELETE FROM " + getAPBuy_ItemDepotTableName(false) + ";").execute();
				for (MarketInfos mi : this.getDatabase().getAllMarketInfos()) {
					mi.save(db);
				}
				for (CategoryInfos catinfos : this.getDatabase().getAllCategoryInfos()) {
					catinfos.save(db);
				}
				for (MarketItem mis : this.getDatabase().getAllMarketItems()) {
					mis.save(db);
				}
			}
			if (this.database instanceof SQLDatabase) {
				((SQLDatabase) this.database).getConnection().close();
			}
			this.database = db;
			this.sqlite = false;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void setupSQLite() {
		this.sqlite = true;
		database = new SQLiteDatabase();
		((SQLiteDatabase) database).connect();
	}

	public Database getDatabase() {
		return this.database;
	}

	public boolean setupMySQL(String host, String port, String databasename, String username, String password) {
		this.sqlite = false;
		database = new MySQLDatabase();
		return ((MySQLDatabase) database).connect(host, port, databasename, username, password);
	}

	public void disconnect() {
		if (database != null) {
			((SQLDatabase) database).disconnect();
		}
	}

	public boolean isSqlite() {
		return this.sqlite;
	}

	public static int getNumber() {
		return number;
	}

	public static String getAPBuy_MarketsTableName(boolean issqlite) {
		String versionStr = VersionUtils.isVersion1_12() ? "1_12_2" : "1_8_x";
		return "APBuy_Markets_" + versionStr + (issqlite ? "" : "_db" + getNumber());
	}

	public static String getAPBuy_CategorysTableName(boolean issqlite) {
		String versionStr = VersionUtils.isVersion1_12() ? "1_12_2" : "1_8_x";
		return "APBuy_Categorys_" + versionStr + (issqlite ? "" : "_db" + getNumber());
	}

	public static String getAPBuy_MItemsTableName(boolean issqlite) {
		String versionStr = VersionUtils.isVersion1_12() ? "1_12_2" : "1_8_x";
		return "APBuy_MItems_" + versionStr + (issqlite ? "" : "_db" + getNumber());
	}

	public static String getAPBuy_ItemDepotTableName(boolean issqlite) {
		String versionStr = VersionUtils.isVersion1_12() ? "1_12_2" : "1_8_x";
		return "APBuy_ItemDepot_" + versionStr + (issqlite ? "" : "_db" + getNumber());
	}

}
