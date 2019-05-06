package ap.apb.datamaster;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import ap.apb.apbuy.markets.CategoryInfos;
import ap.apb.apbuy.markets.MarketException;
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

	public abstract void disconnect();

	// @Override
	// public void saveTotal(Market market) throws MarketException {
	// try {
	// System.out.println("saving..");
	// connection.prepareStatement("DELETE FROM APBuy_Markets WHERE owner = '" +
	// market.getMarketOwner() + "';")
	// .execute();
	// connection.prepareStatement("DELETE FROM APBuy_Categories WHERE owner =
	// '" + market.getMarketOwner() + "';")
	// .execute();
	// connection.prepareStatement("DELETE FROM APBuy_MItems WHERE owner = '" +
	// market.getMarketOwner() + "';")
	// .execute();
	// connection.prepareStatement(
	// "INSERT INTO APBuy_Markets (owner, open, name, devise, sales, solditems,
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
	// .prepareStatement("INSERT INTO APBuy_Categories (owner, name, desc,
	// material, subid) VALUES ('"
	// + (market.getMarketOwner() == null ? "AdminShop"
	// : market.getMarketOwner().getUniqueId().toString())
	// + "', '" + c.getName() + "', '" + c.getDesc() + "', '" +
	// c.getMat().toString() + "', "
	// + Integer.valueOf(c.getSubid()) + ");")
	// .execute();
	// for (MarketItem mis : c.getCatItems()) {
	// connection.prepareStatement(
	// "INSERT INTO APBuy_MItems (owner, category, price, amount, sellamount,
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

	@Override
	public void deleteMarket(UUID uuid) throws MarketException {
		try {
			connection.prepareStatement(
					"DELETE FROM APBuy_Markets WHERE owner = '" + (uuid == null ? "AdminShop" : uuid.toString()) + "';")
					.execute();

			connection.prepareStatement("DELETE FROM APBuy_Categories WHERE owner = '"
					+ (uuid == null ? "AdminShop" : uuid.toString()) + "';").execute();
			connection.prepareStatement(
					"DELETE FROM APBuy_MItems WHERE owner = '" + (uuid == null ? "AdminShop" : uuid.toString()) + "';")
					.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveMarketInfos(String owner, boolean open, String name, String devise, long sales, long solditems)
			throws MarketException {
		try {
			connection
					.prepareStatement("REPLACE INTO APBuy_Markets (owner, open, name, devise, sales, solditems) VALUES "
							+ "('" + owner + "', " + (open ? 1 : 0) + ", '" + name + "', '" + devise + "', " + sales
							+ ", " + solditems + ");")
					.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveCategoryInfos(String owner, String name, String desc, Material material, short subid)
			throws MarketException {
		try {
			connection.prepareStatement("REPLACE INTO APBuy_Categories (owner, name, desc, material, subid) VALUES ('"
					+ owner + "', '" + name + "', '" + desc + "', '" + material.toString() + "', " + subid + ");")
					.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void saveItemInfos(String owner, String category, ItemStack itemstack, int price, long amount,
			int sellamount, long solditems) throws MarketException {
		try {
			connection.prepareStatement(
					"REPLACE INTO APBuy_MItems (owner, category, price, amount, sellamount, solditems, itemstack) VALUES ('"
							+ owner + "', '" + category + "', " + price + ", " + amount + ", " + sellamount + ", "
							+ solditems + ", '" + new JSONObject(itemstack.serialize()).toJSONString() + "');")
					.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeCategory(String owner, String name) throws MarketException {
		try {
			connection
					.prepareStatement(
							"DELETE FROM APBuy_Categories WHERE owner = '" + owner + "' AND name = '" + name + "';")
					.execute();
			connection
					.prepareStatement(
							"DELETE FROM APBuy_MItems WHERE owner = '" + owner + "' AND category = '" + name + "';")
					.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeItem(String owner, ItemStack itemstack) throws MarketException {
		try {
			connection.prepareStatement("DELETE FROM APBuy_MItems WHERE owner = '" + owner + "' AND itemstack = '"
					+ new JSONObject(itemstack.serialize()).toJSONString() + "';").execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<UUID> loadAllOnlineMarkets() throws MarketException {
		List<UUID> uuids = new ArrayList<>();
		try {
			ResultSet set = connection.prepareStatement("").executeQuery();
			while (set.next()) {
				if (set.getString("owner") != "AdminShop") {
					uuids.add(UUID.fromString(set.getString("owner")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uuids;
	}

	@Override
	public boolean hasCategoryInfos(String owner, String catname) {
		try {
			return connection.prepareStatement(
					"SELECT * FROM APBuy_Categories WHERE owner = '" + owner + "' AND name = '" + catname + "';")
					.executeQuery().last();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean hasMarketItem(String owner, ItemStack itemstack) {
		try {
			return connection.prepareStatement("SELECT * FROM APBuy_MItems WHERE owner = '" + owner
					+ "' AND itemstack = '" + new JSONObject(itemstack.serialize()).toJSONString() + "';")
					.executeQuery().last();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void updateList() {
	}

	@Override
	public boolean hasPlayerMarketByUUID(String uuid) {
		try {
			return connection.prepareStatement("SELECT * FROM APBuy_Markets WHERE owner = '" + uuid + "';")
					.executeQuery().last();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	


	@Override
	public MarketItem getMarketItemByIS(String owner, ItemStack is) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MarketInfos getMarketInfos(String owner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CategoryInfos getCategoryInfos(String owner, String catname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MarketItem> getMarketItemsFromMarket(String owner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CategoryInfos> getAllCategoryInfosFromMarket(String owner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UUID> getTopMarketsUUIDs(int top) {
		// TODO Auto-generated method stub
		return null;
	}

}