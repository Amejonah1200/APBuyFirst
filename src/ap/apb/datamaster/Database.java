package ap.apb.datamaster;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ap.apb.APBuyException;
import ap.apb.apbuy.markets.CategoryInfos;
import ap.apb.apbuy.markets.MarketInfos;
import ap.apb.apbuy.markets.MarketItem;

public interface Database {

	public String getName();

	public void saveMarketInfos(String owner, boolean open, String name, String devise, long sales, long solditems)
			throws APBuyException;

	public void saveCategoryInfos(String owner, String name, String desc, Material material, short subid)
			throws APBuyException;

	public void saveItemInfos(UUID id, String owner, String category, ItemStack itemstack, int price, long amount,
			int sellamount, long solditems) throws APBuyException;

	public MarketItem getMarketItemByIS(String owner, ItemStack is) throws APBuyException;

	public MarketInfos getMarketInfos(String owner) throws APBuyException;

	public CategoryInfos getCategoryInfos(String owner, String catname) throws APBuyException;

	public List<MarketItem> getMarketItemsFromMarket(String owner) throws APBuyException;

	public List<CategoryInfos> getAllCategoryInfosFromMarket(String owner) throws APBuyException;

	public void deleteMarket(UUID uuid) throws APBuyException;

	public void removeCategory(String owner, String catname) throws APBuyException;

	public void removeItem(String owner, ItemStack itemstack) throws APBuyException;

	public List<UUID> loadAllOnlineMarkets() throws APBuyException;

	public List<MarketInfos> getAllMarketInfos() throws APBuyException;

	public List<CategoryInfos> getAllCategoryInfos() throws APBuyException;

	public List<MarketItem> getAllMarketItems() throws APBuyException;

	public boolean hasPlayerMarketByUUID(String uuid) throws APBuyException;

	public boolean hasCategoryInfos(String owner, String catname) throws APBuyException;

	public boolean hasMarketItem(String owner, ItemStack is) throws APBuyException;

	public List<UUID> loadAllMarkets() throws APBuyException;

	public HashMap<ItemStack, String[]> getItemsFromItemDepot(String owner) throws APBuyException;

	public void saveMisToItemDepot(String uuid, String owner, ItemStack itemStack, long amount) throws APBuyException;

	public void removeItemFromItemDepot(String owner, ItemStack itemStack) throws APBuyException;;

	public String[] getItemDataFromDepot(String uuid, ItemStack itemstack) throws APBuyException;

}
