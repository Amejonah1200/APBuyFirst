package ap.apb.datamaster;

import java.io.File;
import java.sql.DriverManager;

import ap.apb.APBuy;

public class SQLiteDatabase extends SQLDatabase {

	public SQLiteDatabase() {
		super("sqlite");
	}

	public boolean connect(String... args) {
		try {
			APBuy.plugin.getDataFolder().mkdirs();
			if (!new File(APBuy.plugin.getDataFolder(), "database.db").exists()) {
				new File(APBuy.plugin.getDataFolder(), "database.db").createNewFile();
			}
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(
					"jdbc:sqlite:" + new File(APBuy.plugin.getDataFolder(), "database.db").getAbsolutePath());
			this.createTableIfNotExist(true);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return "sqlite";
	}

}
