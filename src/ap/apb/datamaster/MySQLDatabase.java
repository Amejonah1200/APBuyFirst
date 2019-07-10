package ap.apb.datamaster;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabase extends SQLDatabase {

	public MySQLDatabase() {
		super("mysql");
	}

	public boolean connect(String... args) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + args[0] + ":" + args[1] + "/" + args[2] + "?autoReconnect=true";
			connection = DriverManager.getConnection(url, args[3], args[4]);
			this.createTableIfNotExist(false);
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
