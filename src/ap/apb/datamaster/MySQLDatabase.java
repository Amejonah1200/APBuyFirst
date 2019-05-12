package ap.apb.datamaster;

import java.sql.SQLException;

public class MySQLDatabase extends SQLDatabase {

	public MySQLDatabase() {
		super("mysql");
	}

	public boolean connect(String... args) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// Nope kein password xD
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
