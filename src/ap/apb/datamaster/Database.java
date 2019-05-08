package ap.apb.datamaster;

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ap.apb.apbuy.markets.CategoryInfos;
import ap.apb.apbuy.markets.MarketException;
import ap.apb.apbuy.markets.MarketInfos;
import ap.apb.apbuy.markets.MarketItem;

public abstract interface Database {

	public String getName();

	public void saveMarketInfos(String owner, boolean open, String name, String devise, long sales, long solditems)
			throws MarketException;

	public void saveCategoryInfos(String owner, String name, String desc, Material material, short subid)
			throws MarketException;

	public void saveItemInfos(String owner, String category, ItemStack itemstack, int price, long amount,
			int sellamount, long solditems) throws MarketException;
	
	public MarketItem getMarketItemByIS(String owner, ItemStack is);

	public MarketInfos getMarketInfos(String owner);

	public CategoryInfos getCategoryInfos(String owner, String catname);

	public List<MarketItem> getMarketItemsFromMarket(String owner);

	public List<CategoryInfos> getAllCategoryInfosFromMarket(String owner);

	public void deleteMarket(UUID uuid) throws MarketException;

	public void removeCategory(String owner, String catname) throws MarketException;

	public void removeItem(String owner, ItemStack itemstack) throws MarketException;

	public List<UUID> loadAllOnlineMarkets() throws MarketException;

	public boolean hasPlayerMarketByUUID(String uuid);

	public void updateList();

	public boolean hasCategoryInfos(String owner, String catname);

	public boolean hasMarketItem(String owner, ItemStack is);

}
