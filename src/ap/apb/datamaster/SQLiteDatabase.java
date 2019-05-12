package ap.apb.datamaster;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import ap.apb.APBuy;

public class SQLiteDatabase extends SQLDatabase {

	public SQLiteDatabase() {
		super("sqlite");
	}

	public boolean connect(String... args) {
		try {
			 if (!new File(APBuy.plugin.getDataFolder(),"database.db").exists()) {
				 new File(APBuy.plugin.getDataFolder(), "database.db").createNewFile();
			 }
			 Class.forName("org.sqlite.JDBC");
			 connection = DriverManager.getConnection("jdbc:sqlite:" + new File(APBuy.plugin.getDataFolder(),"database.db").getAbsolutePath());
			connection.prepareStatement(
					"CREATE TABLE IF NOT EXISTS APBuy_Markets (" + "owner VARCHAR(36) NOT NULL, " + "open CHAR(1), "
							+ "name VARCHAR(32), " + "devise varchar(64), " + "sales BIGINT, " + "solditems BIGINT, "
							+ "material varchar(25) NOT NULL, " + "subid TINYINT, " + "PRIMARY KEY(owner)) ;")
					.execute();
			connection.prepareStatement("CREATE TABLE IF NOT EXISTS APBuy_Categorys (" + "owner VARCHAR(36) NOT NULL, "
					+ "name VARCHAR(40) NOT NULL, " + "description VARCHAR(40), " + "material varchar(25) NOT NULL, "
					+ "subid TINYINT, " + "PRIMARY KEY(owner, name));").execute();
			connection.prepareStatement("CREATE TABLE IF NOT EXISTS APBuy_MItems (id VARCHAR(36), " + "owner VARCHAR(36) NOT NULL, "
					+ "category VARCHAR(40) NOT NULL, " + "price INT, " + "amount INT, " + "sellamount INT, "
					+ "solditems INT, " + "itemstack LONGTEXT NOT NULL, " + "PRIMARY KEY(owner, id));").execute();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void disconnect() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getName() {
		return "sqlite";
	}

}
