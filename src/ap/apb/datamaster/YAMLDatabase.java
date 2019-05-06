package ap.apb.datamaster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import ap.apb.APBuy;
import ap.apb.Utils;
import ap.apb.apbuy.markets.CategoryInfos;
import ap.apb.apbuy.markets.Market;
import ap.apb.apbuy.markets.MarketException;
import ap.apb.apbuy.markets.MarketException.ErrorCause;
import ap.apb.apbuy.markets.MarketInfos;
import ap.apb.apbuy.markets.MarketItem;

public class YAMLDatabase implements Database {

	@Override
	public String getName() {
		return "yaml";
	}

//	@Override
//	public void saveTotal(Market market) throws MarketException {
//		FileConfiguration fcg = YamlConfiguration.loadConfiguration(getPlayerMarketFile(market.getMarketOwner()));
//		if (market.getMarketOwner() != null) {
//			fcg.set("Owner", market.getMarketOwner());
//		}
//		fcg.set("SoldItems", market.getSoldItems());
//		fcg.set("Sales", market.getSales());
//		fcg.set("Devise",
//				market.getDevise() == null ? null : ChatColor.translateAlternateColorCodes('&', market.getDevise()));
//		fcg.set("Name",
//				market.getName() == null ? null : ChatColor.translateAlternateColorCodes('&', market.getName()));
//		fcg.set("IsMarketOpen", market.isMarketOpen());
//		List<String> l = new ArrayList<>();
//		for (Category c : market.getMarketCategories()) {
//			l.add(c.getName().replaceAll("§", "&"));
//			fcg.set("Category." + c.getName().replaceAll("§", "&") + ".desc", c.getDesc());
//			fcg.set("Category." + c.getName().replaceAll("§", "&") + ".material", c.getMat().toString());
//			fcg.set("Category." + c.getName().replaceAll("§", "&") + ".subid", c.getSubid());
//			fcg.set("Category." + c.getName().replaceAll("§", "&") + ".Items", c.getCatItems().size());
//			List<MarketItem> iss = new ArrayList<>();
//			iss.addAll(c.getCatItems());
//			for (int i = 0; i < iss.size(); i++) {
//				fcg.set("Category." + c.getName().replaceAll("§", "&") + ".Item." + i + ".is", iss.get(i).getIs());
//				fcg.set("Category." + c.getName().replaceAll("§", "&") + ".Item." + i + ".price",
//						iss.get(i).getPrice());
//				fcg.set("Category." + c.getName().replaceAll("§", "&") + ".Item." + i + ".amount",
//						iss.get(i).getAmmount());
//				fcg.set("Category." + c.getName().replaceAll("§", "&") + ".Item." + i + ".sellAmount",
//						iss.get(i).getSellAmmount());
//				fcg.set("Category." + c.getName().replaceAll("§", "&") + ".Item." + i + ".soldItems",
//						iss.get(i).getSoldItems());
//			}
//		}
//		fcg.set("Categorys", l);
//		try {
//			fcg.save(getPlayerMarketFile(market.getMarketOwner()));
//		} catch (IOException e) {
//		}
//		APBuy.getMarketHandler().updateLists();
//	}

	@Override
	public List<UUID> loadAllOnlineMarkets() {
		List<UUID> l = new ArrayList<>();
		for (String s : YamlConfiguration.loadConfiguration(APBuy.plugin.getPlayerMarketStats())
				.getStringList("TopMarkets")) {
			l.add(UUID.fromString(s.replaceAll(Pattern.quote(".yml"), "")));
		}
		return l;
	}

	private File getPlayerMarketFile(String uuid) throws MarketException {
		for (File f : APBuy.plugin.getPlayerMarketFolder().listFiles()) {
			if (f.getName().startsWith(uuid)) {
				return f;
			}
		}
		throw new MarketException(ErrorCause.NOTFOUND);
	}

	@Override
	public void deleteMarket(UUID uuid) throws MarketException {
		getPlayerMarketFile(uuid.toString()).delete();
	}

	public static File getAdminShopFile() {
		return new File("plugins/APBuy/Markets/AdminShop.yml");
	}

	@Override
	public void updateList() {
		try {
			FileConfiguration fcg = YamlConfiguration.loadConfiguration(APBuy.plugin.getPlayerMarketStats());
			if (APBuy.plugin.getPlayerMarketFolder().listFiles().length >= 1) {
				List<String> filesnames = new ArrayList<>();
				for (File f : APBuy.plugin.getPlayerMarketFolder().listFiles()) {
					if (!f.getName().toLowerCase().contains("adminshop")) {
						filesnames.add(f.getName());
					}
				}
				Iterator<String> iterator = filesnames.iterator();
				while (iterator.hasNext()) {
					if (!YamlConfiguration.loadConfiguration(new File("plugins/APBuy/Markets/" + iterator.next()))
							.getBoolean("IsMarketOpen")) {
						iterator.remove();
					}
				}
				if (filesnames.size() >= 1) {
					filesnames.sort(new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							Market m1 = getCompareMarketByFile(new File("plugins/APBuy/Markets/" + o2));
							Market m2 = getCompareMarketByFile(new File("plugins/APBuy/Markets/" + o1));
							if (m1.getSales() > m2.getSales()) {
								return 1;
							} else if (m1.getSales() < m2.getSales()) {
								return -1;
							} else {
								if (m1.getSoldItems() > m2.getSoldItems()) {
									return 1;
								} else if (m1.getSoldItems() < m2.getSoldItems()) {
									return -1;
								}
							}
							return 0;
						}

						private Market getCompareMarketByFile(File file) {
							FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
							Market market = new Market(null, false);
							market.setSoldItems(cfg.getLong("SelledItems"));
							market.setSales(cfg.getLong("Sales"));
							return market;
						}
					});
				}
				fcg.set("TopMarkets", filesnames);
				if (filesnames.size() == 0) {
					fcg.set("TopMarket", null);
				} else {
					fcg.set("TopMarket", filesnames.get(0));
				}
				fcg.set("MusstBeUpdated", false);
				try {
					fcg.save(APBuy.plugin.getPlayerMarketStats());
				} catch (IOException e) {
				}
			} else {
				fcg.set("TopMarkets", new ArrayList<>());
				fcg.set("MusstBeUpdated", false);
				fcg.set("TopMarket", null);
				try {
					fcg.save(APBuy.plugin.getPlayerMarketStats());
				} catch (IOException e) {
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("[APB] Error in File nb: " + Utils.addToFix(e1));
		}
	}

	@Override
	public List<UUID> getTopMarketsUUIDs(int top) {
		List<UUID> uuids = new ArrayList<>();
		int i = 0;
		for (String s : YamlConfiguration.loadConfiguration(APBuy.plugin.getPlayerMarketStats())
				.getStringList("TopMarkets")) {
			if ((i < top) || (top == -1)) {
				uuids.add(UUID.fromString(s.replaceFirst(Pattern.quote(".yml"), "")));
				i++;
			}
		}
		return uuids;
	}

	// @Override
	// public boolean adminShopExist() {
	// return getAdminShopFile().exists();
	// }

	@Override
	public void saveMarketInfos(String owner, boolean open, String name, String devise, long sales, long solditems)
			throws MarketException {
		FileConfiguration fcg = YamlConfiguration
				.loadConfiguration(new File("plugins/APBuy/Markets/" + owner + ".yml"));
		fcg.set("Owner", owner);
		fcg.set("SoldItems", solditems);
		fcg.set("Sales", sales);
		fcg.set("Devise", devise == null ? null : ChatColor.translateAlternateColorCodes('&', devise));
		fcg.set("Name", name == null ? null : ChatColor.translateAlternateColorCodes('&', name));
		fcg.set("IsMarketOpen", open);
		try {
			fcg.save(new File("plugins/APBuy/Markets/" + owner + ".yml"));
		} catch (IOException e) {
			throw new MarketException(ErrorCause.SAVE);
		}

	}

	@Override
	public void saveCategoryInfos(String owner, String name, String desc, Material material, short subid)
			throws MarketException {
		FileConfiguration fcg = YamlConfiguration
				.loadConfiguration(new File("plugins/APBuy/Markets/" + owner + ".yml"));
		fcg.set("Category." + name.replaceAll("§", "&") + ".desc", desc);
		fcg.set("Category." + name.replaceAll("§", "&") + ".material", material.toString());
		fcg.set("Category." + name.replaceAll("§", "&") + ".subid", subid);
		try {
			fcg.save(new File("plugins/APBuy/Markets/" + owner + ".yml"));
		} catch (IOException e) {
			throw new MarketException(ErrorCause.SAVE);
		}
	}

	@Override
	public void saveItemInfos(String owner, String category, ItemStack itemstack, int price, long amount,
			int sellamount, long solditems) throws MarketException {
		FileConfiguration fcg = YamlConfiguration
				.loadConfiguration(new File("plugins/APBuy/Markets/" + owner + ".yml"));
		int i = -1;
		for (int i2 = 0; i2 < fcg.getInt("Category." + category.replaceAll("§", "&") + ".Items"); i2++) {
			if (fcg.getItemStack("Category." + category.replaceAll("§", "&") + ".Item." + i + ".is")
					.isSimilar(itemstack)) {
				i = i2;
				break;
			}
		}
		if (i == -1)
			return;

		fcg.set("Category." + category.replaceAll("§", "&") + ".Item." + i + ".price", price);
		fcg.set("Category." + category.replaceAll("§", "&") + ".Item." + i + ".amount", amount);
		fcg.set("Category." + category.replaceAll("§", "&") + ".Item." + i + ".sellAmount", sellamount);
		fcg.set("Category." + category.replaceAll("§", "&") + ".Item." + i + ".soldItems", solditems);
		try {
			fcg.save(new File("plugins/APBuy/Markets/" + owner + ".yml"));
		} catch (IOException e) {
			throw new MarketException(ErrorCause.SAVE);
		}
	}

	@Override
	public void removeCategory(String owner, String name) throws MarketException {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeItem(String owner, ItemStack itemstack) throws MarketException {
		// TODO Auto-generated method stub
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
	public CategoryInfos getCategoryInfos(String owner, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CategoryInfos> getAllCategoryInfosFromMarket(String owner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCategoryInfos(String owner, String catname) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasMarketItem(String owner, ItemStack is) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<MarketItem> getMarketItemsFromMarket(String owner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPlayerMarketByUUID(String uuid) {
		for (File f : APBuy.plugin.getPlayerMarketFolder().listFiles()) {
			if (f.getName().startsWith(uuid)) {
				return true;
			}
		}
		return false;
	}

}
