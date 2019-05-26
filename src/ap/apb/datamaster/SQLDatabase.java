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

import ap.apb.Utils;
import ap.apb.apbuy.markets.CategoryInfos;
import ap.apb.apbuy.markets.Market;
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
	// connection.prepareStatement("DELETE FROM APBuy_Categorys WHERE owner =
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
	// .prepareStatement("INSERT INTO APBuy_Categorys (owner, name, desc,
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

			connection.prepareStatement("DELETE FROM APBuy_Categorys WHERE owner = '"
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
			connection.prepareStatement(
					"REPLACE INTO APBuy_Markets (owner, open, name, devise, sales, solditems, material , subid ) VALUES "
							+ "('" + owner + "', '" + (open ? 1 : 0) + "', "
							+ (name == null ? "NULL" : "'" + name + "'") + ", "
							+ (devise == null ? "NULL" : "'" + devise + "'") + ", " + sales + ", " + solditems + ", '"
							+ Material.CHEST.toString() + "', " + 0 + ");")
					.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveCategoryInfos(String owner, String name, String desc, Material material, short subid)
			throws MarketException {
		try {
			connection.prepareStatement(
					"REPLACE INTO APBuy_Categorys (owner, name, description, material, subid) VALUES ('" + owner
							+ "', '" + name + "', " + (desc == null ? "NULL" : "'" + desc + "'") + ", '"
							+ material.toString() + "', " + subid + ");")
					.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void saveItemInfos(UUID id, String owner, String category, ItemStack itemstack, int price, long amount,
			int sellamount, long solditems) throws MarketException {
		try {
			connection.prepareStatement(
					"REPLACE INTO APBuy_MItems (id, owner, category, price, amount, sellamount, solditems, itemstack) VALUES ('"
							+ id.toString() + "', '" + owner + "', '" + category + "', " + price + ", " + amount + ", "
							+ sellamount + ", " + solditems + ", '" + Utils.serializeItemStack(itemstack) + "');")
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
							"DELETE FROM APBuy_Categorys WHERE owner = '" + owner + "' AND name = '" + name + "';")
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
					+ Utils.serializeItemStack(itemstack) + "';").execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<UUID> loadAllOnlineMarkets() throws MarketException {
		List<UUID> uuids = new ArrayList<>();
		try {
			ResultSet set = connection.prepareStatement("SELECT owner FROM APBuy_Markets WHERE open = '1'")
					.executeQuery();
			while (set.next()) {
				if (!set.getString("owner").equals("AdminShop")) {
					uuids.add(UUID.fromString(set.getString("owner")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uuids;
	}

	@Override
	public List<UUID> loadAllMarkets() throws MarketException {
		List<UUID> uuids = new ArrayList<>();
		try {
			ResultSet set = connection.prepareStatement("SELECT owner FROM APBuy_Markets").executeQuery();
			while (set.next()) {
				if (!set.getString("owner").equals("AdminShop")) {
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
			return connection
					.prepareStatement(
							"SELECT * FROM APBuy_Categorys WHERE owner = '" + owner + "' AND name = '" + catname + "';")
					.executeQuery().next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean hasMarketItem(String owner, ItemStack itemstack) {
		ItemStack clone = itemstack.clone();
		clone.setAmount(1);
		try {
			return connection.prepareStatement("SELECT * FROM APBuy_MItems WHERE owner = '" + owner
					+ "' AND itemstack = '" + Utils.serializeItemStack(clone) + "';").executeQuery().next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean hasPlayerMarketByUUID(String uuid) {
		try {
			return connection.prepareStatement("SELECT * FROM APBuy_Markets WHERE owner = '" + uuid + "';")
					.executeQuery().next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public MarketItem getMarketItemByIS(String owner, ItemStack is) {
		ItemStack clone = is.clone();
		clone.setAmount(1);
		try {
			ResultSet set = connection.prepareStatement("SELECT * FROM APBuy_MItems WHERE owner = '" + owner
					+ "' AND itemstack = '" + Utils.serializeItemStack(clone) + "';").executeQuery();
			if (!set.next()) {
				return null;
			}
			System.out.println(set.getLong("amount") + " " + clone.getAmount());
			return new MarketItem(UUID.fromString(set.getString("id")), clone, owner, set.getInt("price"),
					set.getLong("amount"), set.getInt("sellamount"), set.getLong("solditems"),
					set.getString("category"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public MarketInfos getMarketInfos(String owner) {
		try {
			ResultSet set = connection.prepareStatement("SELECT * FROM APBuy_Markets WHERE owner = '" + owner + "';")
					.executeQuery();
			if (!set.next()) {
				Market m = new Market(owner, false);
				m.saveMarketInfos();
				return m.marketInfos;
			} else {
				// owner, open, name, devise, sales, solditems, material , subid
				return new MarketInfos(owner, set.getString("name"), set.getString("devise"),
						set.getString("open").equalsIgnoreCase("1"), set.getLong("solditems"), set.getLong("sales"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CategoryInfos getCategoryInfos(String owner, String catname) {
		// owner, name, desc, material, subid
		try {
			ResultSet set = connection
					.prepareStatement(
							"SELECT * FROM APBuy_Categorys WHERE owner = '" + owner + "' AND name = '" + catname + "';")
					.executeQuery();
			if (!set.next()) {
				return null;
			}
			return new CategoryInfos(owner, catname, Material.valueOf(set.getString("material")), set.getShort("subid"),
					set.getString("description"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<MarketItem> getMarketItemsFromMarket(String owner) {
		List<MarketItem> miss = new ArrayList<>();
		try {
			ResultSet set = connection.prepareStatement("SELECT * FROM APBuy_MItems WHERE owner = '" + owner + "';")
					// WHERE owner = '" + owner + "';")
					.executeQuery();
			while (set.next()) {
				miss.add(new MarketItem(UUID.fromString(set.getString("id")),
						Utils.deserializeItemStack(set.getString("itemstack")), owner, set.getInt("price"),
						set.getLong("amount"), set.getInt("sellamount"), set.getLong("solditems"),
						set.getString("category")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return miss;
	}

	@Override
	public List<CategoryInfos> getAllCategoryInfosFromMarket(String owner) {
		List<CategoryInfos> catinfoss = new ArrayList<>();
		try {
			ResultSet set = connection.prepareStatement("SELECT * FROM APBuy_Categorys WHERE owner = '" + owner + "';")
					.executeQuery();
			while (set.next()) {
				catinfoss.add(
						new CategoryInfos(owner, set.getString("name"), Material.valueOf(set.getString("material")),
								set.getShort("subid"), set.getString("description")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return catinfoss;
	}

	@Override
	public HashMap<ItemStack, String[]> getItemsFromItemDepot(String owner) {
		HashMap<ItemStack, String[]> hmitms = new HashMap<>();
		try {
			ResultSet set = connection.prepareStatement("SELECT * FROM APBuy_ItemDepot WHERE owner = '" + owner + "';")
					.executeQuery();
			while (set.next()) {
				hmitms.put(Utils.deserializeItemStack(set.getString("itemstack")),
						new String[] { set.getString("uuid"), String.valueOf(set.getLong("amount")) });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hmitms;
	}

	@Override
	public void saveMisToItemDepot(String uuid, String owner, ItemStack itemStack, long amount) {
		if(amount == 0) {
			removeItemFromItemDepot(owner, itemStack);
			return;
		}
		try {
			connection
					.prepareStatement("REPLACE INTO APBuy_ItemDepot (uuid, owner, itemstack, amount) VALUES ('" + uuid
							+ "', '" + owner + "', '" + Utils.serializeItemStack(itemStack) + "', " + amount + ");")
					.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeItemFromItemDepot(String owner, ItemStack itemStack) {
		try {
			connection.prepareStatement("DELETE FROM APBuy_ItemDepot WHERE owner = '" + owner + "' AND itemstack = '"
					+ Utils.serializeItemStack(itemStack) + "';").execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String[] getItemDataFromDepot(String owner, ItemStack itemstack) {
		String[] data = null;
		try {
			ResultSet set = connection.prepareStatement("SELECT * FROM APBuy_ItemDepot WHERE owner = '" + owner
					+ "' AND itemstack = '" + Utils.serializeItemStack(itemstack) + "';").executeQuery();
			if (set.next()) {
				data = new String[] { set.getString("uuid"), String.valueOf(set.getLong("amount")) };
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}

}
