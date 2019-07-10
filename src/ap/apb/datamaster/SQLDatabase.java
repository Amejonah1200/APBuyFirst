package ap.apb.datamaster;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ap.apb.APBuy;
import ap.apb.APBuyException;
import ap.apb.Utils;
import ap.apb.APBuyException.ErrorCause;
import ap.apb.apbuy.markets.CategoryInfos;
import ap.apb.apbuy.markets.Market;
import ap.apb.apbuy.markets.MarketInfos;
import ap.apb.apbuy.markets.MarketItem;

public abstract class SQLDatabase implements Database {

	private String name;
	protected Connection connection;

	public SQLDatabase(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public abstract boolean connect(String... args);

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

	public Connection getConnection() {
		return this.connection;
	}

	// @Override
	// public void saveTotal(Market market) throws APBuyException {
	// try {
	// System.out.println("saving..");
	// connection.prepareStatement("DELETE FROM " +
	// Datamaster.getAPBuy_MarketsTableName(APBuy.getDatamaster().isSqlite()) +
	// " WHERE owner = '" +
	// market.getMarketOwner() + "';")
	// .execute();
	// connection.prepareStatement("DELETE FROM " +
	// Datamaster.getAPBuy_CategorysTableName(APBuy.getDatamaster().isSqlite())
	// + " WHERE owner =
	// '" + market.getMarketOwner() + "';")
	// .execute();
	// connection.prepareStatement("DELETE FROM " +
	// Datamaster.getAPBuy_MItemsTableName(APBuy.getDatamaster().isSqlite()) + "
	// WHERE owner = '" +
	// market.getMarketOwner() + "';")
	// .execute();
	// connection.prepareStatement(
	// "INSERT INTO " +
	// Datamaster.getAPBuy_MarketsTableName(APBuy.getDatamaster().isSqlite()) +
	// " (owner, open, name, devise, sales, solditems,
	// marketmat) VALUES ('"
	// + market.getMarketOwner() + "', " + (market.isOpen() ? 1 : 0) + ", '"
	// + (market.getName() == null ? "" : market.getName()) + "', '"
	// + (market.getDevise() == null ? "" : market.getDevise()) + "', " +
	// market.getSales() + ", "
	// + market.getSoldItems() + ", '" + (market.getMarketOwner() == null ?
	// "CHEST"
	// : market.getMarketItemStack().getType().toString())
	// + "');")
	// .execute();
	// for (Category c : market.getMarketCategories()) {
	// connection
	// .prepareStatement("INSERT INTO " +
	// Datamaster.getAPBuy_CategorysTableName(APBuy.getDatamaster().isSqlite())
	// + " (owner, name, desc,
	// material, subid) VALUES ('"
	// + (market.getMarketOwner() == null ? "AdminShop"
	// : market.getMarketOwner().getUniqueId().toString())
	// + "', '" + c.getName() + "', '" + c.getDesc() + "', '" +
	// c.getMat().toString() + "', "
	// + Integer.valueOf(c.getSubid()) + ");")
	// .execute();
	// for (MarketItem mis : c.getCatItems()) {
	// connection.prepareStatement(
	// "INSERT INTO " +
	// Datamaster.getAPBuy_MItemsTableName(APBuy.getDatamaster().isSqlite()) + "
	// (owner, category, price, amount, sellamount,
	// solditems, itemstack) VALUES ('"
	// + (market.getMarketOwner() == null ? "AdminShop"
	// : market.getMarketOwner().getUniqueId().toString())
	// + "', '" + c.getName() + "', " + mis.getPrice() + ", " + mis.getAmmount()
	// + ", "
	// + mis.getSellAmmount() + ", " + mis.getSoldItems() + ", '"
	// + new JSONObject(mis.getIs().serialize()).toJSONString() + "');")
	// .execute();
	// }
	// }
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }

	public void createTableIfNotExist(boolean sqlite) throws APBuyException {
		try {
			connection
					.prepareStatement("CREATE TABLE IF NOT EXISTS " + Datamaster.getAPBuy_MarketsTableName(sqlite)
							+ " (" + "owner VARCHAR(36) NOT NULL, " + "open CHAR(1), " + "name VARCHAR(32), "
							+ "devise varchar(64), " + "sales BIGINT, " + "solditems BIGINT, "
							+ "material varchar(25) NOT NULL, " + "subid TINYINT, " + "PRIMARY KEY(owner)) ;")
					.execute();
			connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + Datamaster.getAPBuy_CategorysTableName(sqlite)
					+ " (" + "owner VARCHAR(36) NOT NULL, " + "name VARCHAR(40) NOT NULL, "
					+ "description VARCHAR(40), " + "material varchar(25) NOT NULL, " + "subid TINYINT, "
					+ "PRIMARY KEY(owner, name));").execute();
			connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + Datamaster.getAPBuy_MItemsTableName(sqlite)
					+ " (id VARCHAR(36), " + "owner VARCHAR(36) NOT NULL, " + "category VARCHAR(40) NOT NULL, "
					+ "price INT, " + "amount INT, " + "sellamount INT, " + "solditems INT, "
					+ "itemstack LONGTEXT NOT NULL, " + "PRIMARY KEY(owner, id));").execute();
			connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + Datamaster.getAPBuy_ItemDepotTableName(sqlite)
					+ "` ( `uuid` CHAR(36) NOT NULL , " + "`owner` CHAR(36) NOT NULL , `itemstack` LONGTEXT NOT NULL ,"
					+ "`amount` BIGINT NOT NULL , PRIMARY KEY (`owner`, `uuid`));").execute();
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public void deleteMarket(UUID uuid) throws APBuyException {
		try {
			connection
					.prepareStatement(
							"DELETE FROM " + Datamaster.getAPBuy_MarketsTableName(APBuy.getDatamaster().isSqlite())
									+ " WHERE owner = '" + (uuid == null ? "AdminShop" : uuid.toString()) + "';")
					.execute();

			connection
					.prepareStatement(
							"DELETE FROM " + Datamaster.getAPBuy_CategorysTableName(APBuy.getDatamaster().isSqlite())
									+ " WHERE owner = '" + (uuid == null ? "AdminShop" : uuid.toString()) + "';")
					.execute();
			connection
					.prepareStatement(
							"DELETE FROM " + Datamaster.getAPBuy_MItemsTableName(APBuy.getDatamaster().isSqlite())
									+ " WHERE owner = '" + (uuid == null ? "AdminShop" : uuid.toString()) + "';")
					.execute();
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public void saveMarketInfos(String owner, boolean open, String name, String devise, long sales, long solditems)
			throws APBuyException {
		try {
			connection.prepareStatement(
					"REPLACE INTO " + Datamaster.getAPBuy_MarketsTableName(APBuy.getDatamaster().isSqlite())
							+ " (owner, open, name, devise, sales, solditems, material , subid ) VALUES " + "('" + owner
							+ "', '" + (open ? 1 : 0) + "', " + (name == null ? "NULL" : "'" + name + "'") + ", "
							+ (devise == null ? "NULL" : "'" + devise + "'") + ", " + sales + ", " + solditems + ", '"
							+ Material.CHEST.toString() + "', " + 0 + ");")
					.execute();
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public void saveCategoryInfos(String owner, String name, String desc, Material material, short subid)
			throws APBuyException {
		try {
			connection.prepareStatement("REPLACE INTO "
					+ Datamaster.getAPBuy_CategorysTableName(APBuy.getDatamaster().isSqlite())
					+ " (owner, name, description, material, subid) VALUES ('" + owner + "', '" + name + "', "
					+ (desc == null ? "NULL" : "'" + desc + "'") + ", '" + material.toString() + "', " + subid + ");")
					.execute();
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}

	}

	@Override
	public void saveItemInfos(UUID id, String owner, String category, ItemStack itemstack, int price, long amount,
			int sellamount, long solditems) throws APBuyException {
		try {
			connection.prepareStatement(
					"REPLACE INTO " + Datamaster.getAPBuy_MItemsTableName(APBuy.getDatamaster().isSqlite())
							+ " (id, owner, category, price, amount, sellamount, solditems, itemstack) VALUES ('"
							+ id.toString() + "', '" + owner + "', '" + category + "', " + price + ", " + amount + ", "
							+ sellamount + ", " + solditems + ", '" + Utils.serializeItemStack(itemstack) + "');")
					.execute();
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public void removeCategory(String owner, String name) throws APBuyException {
		try {
			connection.prepareStatement(
					"DELETE FROM " + Datamaster.getAPBuy_CategorysTableName(APBuy.getDatamaster().isSqlite())
							+ " WHERE owner = '" + owner + "' AND name = '" + name + "';")
					.execute();
			connection.prepareStatement(
					"DELETE FROM " + Datamaster.getAPBuy_MItemsTableName(APBuy.getDatamaster().isSqlite())
							+ " WHERE owner = '" + owner + "' AND category = '" + name + "';")
					.execute();
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public void removeItem(String owner, ItemStack itemstack) throws APBuyException {
		try {
			connection
					.prepareStatement("DELETE FROM "
							+ Datamaster.getAPBuy_MItemsTableName(APBuy.getDatamaster().isSqlite()) + " WHERE owner = '"
							+ owner + "' AND itemstack = '" + Utils.serializeItemStack(itemstack) + "';")
					.execute();
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public List<UUID> loadAllOnlineMarkets() throws APBuyException {
		List<UUID> uuids = new ArrayList<>();
		try {
			ResultSet set = connection.prepareStatement("SELECT owner FROM "
					+ Datamaster.getAPBuy_MarketsTableName(APBuy.getDatamaster().isSqlite()) + " WHERE open = '1'")
					.executeQuery();
			while (set.next()) {
				if (!set.getString("owner").equals("AdminShop")) {
					uuids.add(UUID.fromString(set.getString("owner")));
				}
			}
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
		return uuids;
	}

	@Override
	public List<UUID> loadAllMarkets() throws APBuyException {
		List<UUID> uuids = new ArrayList<>();
		try {
			ResultSet set = connection
					.prepareStatement("SELECT owner FROM "
							+ Datamaster.getAPBuy_MarketsTableName(APBuy.getDatamaster().isSqlite()) + "")
					.executeQuery();
			while (set.next()) {
				if (!set.getString("owner").equals("AdminShop")) {
					uuids.add(UUID.fromString(set.getString("owner")));
				}
			}
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
		return uuids;
	}

	@Override
	public boolean hasCategoryInfos(String owner, String catname) throws APBuyException {
		try {
			return connection
					.prepareStatement(
							"SELECT * FROM " + Datamaster.getAPBuy_CategorysTableName(APBuy.getDatamaster().isSqlite())
									+ " WHERE owner = '" + owner + "' AND name = '" + catname + "';")
					.executeQuery().next();
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public boolean hasMarketItem(String owner, ItemStack itemstack) throws APBuyException {
		ItemStack clone = itemstack.clone();
		clone.setAmount(1);
		try {
			return connection
					.prepareStatement("SELECT * FROM "
							+ Datamaster.getAPBuy_MItemsTableName(APBuy.getDatamaster().isSqlite()) + " WHERE owner = '"
							+ owner + "' AND itemstack = '" + Utils.serializeItemStack(clone) + "';")
					.executeQuery().next();
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public boolean hasPlayerMarketByUUID(String uuid) throws APBuyException {
		try {
			return connection.prepareStatement(
					"SELECT * FROM " + Datamaster.getAPBuy_MarketsTableName(APBuy.getDatamaster().isSqlite())
							+ " WHERE owner = '" + uuid + "';")
					.executeQuery().next();
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public MarketItem getMarketItemByIS(String owner, ItemStack is) throws APBuyException {
		ItemStack clone = is.clone();
		clone.setAmount(1);
		try {
			ResultSet set = connection
					.prepareStatement("SELECT * FROM "
							+ Datamaster.getAPBuy_MItemsTableName(APBuy.getDatamaster().isSqlite()) + " WHERE owner = '"
							+ owner + "' AND itemstack = '" + Utils.serializeItemStack(clone) + "';")
					.executeQuery();
			if (!set.next()) {
				return null;
			}
			return new MarketItem(UUID.fromString(set.getString("id")), clone, owner, set.getInt("price"),
					set.getLong("amount"), set.getInt("sellamount"), set.getLong("solditems"),
					set.getString("category"));
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public MarketInfos getMarketInfos(String owner) throws APBuyException {
		try {
			ResultSet set = connection.prepareStatement(
					"SELECT * FROM " + Datamaster.getAPBuy_MarketsTableName(APBuy.getDatamaster().isSqlite())
							+ " WHERE owner = '" + owner + "';")
					.executeQuery();
			if (!set.next()) {
				Market m = new Market(owner, false);
				try {
					m.saveMarketInfos();
				} catch (APBuyException e) {
					throw e;
				}
				return m.marketInfos;
			} else {
				// owner, open, name, devise, sales, solditems, material , subid
				return new MarketInfos(owner, set.getString("name"), set.getString("devise"),
						set.getString("open").equalsIgnoreCase("1"), set.getLong("solditems"), set.getLong("sales"));
			}
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public CategoryInfos getCategoryInfos(String owner, String catname) throws APBuyException {
		// owner, name, desc, material, subid
		try {
			ResultSet set = connection.prepareStatement(
					"SELECT * FROM " + Datamaster.getAPBuy_CategorysTableName(APBuy.getDatamaster().isSqlite())
							+ " WHERE owner = '" + owner + "' AND name = '" + catname + "';")
					.executeQuery();
			if (!set.next()) {
				return null;
			}
			return new CategoryInfos(owner, catname, Material.valueOf(set.getString("material")), set.getShort("subid"),
					set.getString("description"));
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public List<MarketItem> getMarketItemsFromMarket(String owner) throws APBuyException {
		List<MarketItem> miss = new ArrayList<>();
		try {
			ResultSet set = connection
					.prepareStatement(
							"SELECT * FROM " + Datamaster.getAPBuy_MItemsTableName(APBuy.getDatamaster().isSqlite())
									+ " WHERE owner = '" + owner + "';")
					// WHERE owner = '" + owner + "';")
					.executeQuery();
			while (set.next()) {
				miss.add(new MarketItem(UUID.fromString(set.getString("id")),
						Utils.deserializeItemStack(set.getString("itemstack")), owner, set.getInt("price"),
						set.getLong("amount"), set.getInt("sellamount"), set.getLong("solditems"),
						set.getString("category")));
			}
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
		return miss;
	}

	@Override
	public List<CategoryInfos> getAllCategoryInfosFromMarket(String owner) throws APBuyException {
		List<CategoryInfos> catinfoss = new ArrayList<>();
		try {
			ResultSet set = connection.prepareStatement(
					"SELECT * FROM " + Datamaster.getAPBuy_CategorysTableName(APBuy.getDatamaster().isSqlite())
							+ " WHERE owner = '" + owner + "';")
					.executeQuery();
			while (set.next()) {
				catinfoss.add(
						new CategoryInfos(owner, set.getString("name"), Material.valueOf(set.getString("material")),
								set.getShort("subid"), set.getString("description")));
			}
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
		return catinfoss;
	}

	@Override
	public HashMap<ItemStack, String[]> getItemsFromItemDepot(String owner) throws APBuyException {
		HashMap<ItemStack, String[]> hmitms = new HashMap<>();
		try {
			ResultSet set = connection.prepareStatement(
					"SELECT * FROM " + Datamaster.getAPBuy_ItemDepotTableName(APBuy.getDatamaster().isSqlite())
							+ " WHERE owner = '" + owner + "';")
					.executeQuery();
			while (set.next()) {
				hmitms.put(Utils.deserializeItemStack(set.getString("itemstack")),
						new String[] { set.getString("uuid"), String.valueOf(set.getLong("amount")) });
			}
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
		return hmitms;
	}

	@Override
	public void saveMisToItemDepot(String uuid, String owner, ItemStack itemStack, long amount) throws APBuyException {
		if (amount == 0) {
			removeItemFromItemDepot(owner, itemStack);
			return;
		}
		try {
			connection.prepareStatement(
					"REPLACE INTO " + Datamaster.getAPBuy_ItemDepotTableName(APBuy.getDatamaster().isSqlite())
							+ " (uuid, owner, itemstack, amount) VALUES ('" + uuid + "', '" + owner + "', '"
							+ Utils.serializeItemStack(itemStack) + "', " + amount + ");")
					.execute();
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public void removeItemFromItemDepot(String owner, ItemStack itemStack) throws APBuyException {
		try {
			connection.prepareStatement("DELETE FROM "
					+ Datamaster.getAPBuy_ItemDepotTableName(APBuy.getDatamaster().isSqlite()) + " WHERE owner = '"
					+ owner + "' AND itemstack = '" + Utils.serializeItemStack(itemStack) + "';").execute();
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
	}

	@Override
	public String[] getItemDataFromDepot(String owner, ItemStack itemstack) throws APBuyException {
		String[] data = null;
		try {
			ResultSet set = connection.prepareStatement("SELECT * FROM "
					+ Datamaster.getAPBuy_ItemDepotTableName(APBuy.getDatamaster().isSqlite()) + " WHERE owner = '"
					+ owner + "' AND itemstack = '" + Utils.serializeItemStack(itemstack) + "';").executeQuery();
			if (set.next()) {
				data = new String[] { set.getString("uuid"), String.valueOf(set.getLong("amount")) };
			}
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
		return data;
	}

	@Override
	public List<MarketInfos> getAllMarketInfos() throws APBuyException {
		List<MarketInfos> list = new ArrayList<>();
		try {
			ResultSet set = connection
					.prepareStatement("SELECT * FROM "
							+ Datamaster.getAPBuy_MarketsTableName(APBuy.getDatamaster().isSqlite()) + ";")
					.executeQuery();
			if (set.next()) {
				list.add(new MarketInfos(set.getString("owner"), set.getString("name"), set.getString("devise"),
						set.getString("open").equalsIgnoreCase("1"), set.getLong("solditems"), set.getLong("sales")));
			}
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
		return list;
	}

	@Override
	public List<CategoryInfos> getAllCategoryInfos() throws APBuyException {
		List<CategoryInfos> list = new ArrayList<>();
		try {
			ResultSet set = connection
					.prepareStatement("SELECT * FROM "
							+ Datamaster.getAPBuy_CategorysTableName(APBuy.getDatamaster().isSqlite()) + ";")
					.executeQuery();
			if (set.next()) {
				list.add(new CategoryInfos(set.getString("owner"), set.getString("name"),
						Material.valueOf(set.getString("material")), set.getShort("subid"),
						set.getString("description")));
			}
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
		return list;
	}

	@Override
	public List<MarketItem> getAllMarketItems() throws APBuyException {
		List<MarketItem> list = new ArrayList<>();
		try {
			ResultSet set = connection
					.prepareStatement("SELECT * FROM "
							+ Datamaster.getAPBuy_MItemsTableName(APBuy.getDatamaster().isSqlite()) + ";")
					.executeQuery();
			if (set.next()) {
				list.add(new MarketItem(UUID.fromString(set.getString("id")),
						Utils.deserializeItemStack(set.getString("itemstack")), set.getString("owner"),
						set.getInt("price"), set.getLong("amount"), set.getInt("sellamount"), set.getLong("solditems"),
						set.getString("category")));
			}
		} catch (SQLException e) {
			throw new APBuyException(ErrorCause.SQL, e, null, "SQLDatabase");
		}
		return list;
	}

}
