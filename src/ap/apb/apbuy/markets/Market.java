package ap.apb.apbuy.markets;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ap.apb.AIS;
import ap.apb.APBuy;

public class Market {

	public MarketInfos marketInfos;

	public Market(String uuid, boolean load) {
		if (load) {
			marketInfos = APBuy.database.getMarketInfos(uuid);
		} else {
			marketInfos = new MarketInfos(uuid, null, null, false, 0, 0);
		}
	}

	public void saveMarketInfos() throws MarketException {
		this.marketInfos.save();
	}

	public List<CategoryInfos> getCatsInfos() {
		return APBuy.database.getAllCategoryInfosFromMarket(this.marketInfos.getMarketOwner());
	}

	public List<MarketItem> getMarketItems() {
		return APBuy.database.getMarketItemsFromMarket(this.marketInfos.getMarketOwner());
	}

	public void buyed(ItemStack is, long amount) throws MarketException {
		this.marketInfos.grantSalesOne().grandSoldItems(amount).save();
		APBuy.database.getMarketItemByIS(this.marketInfos.getMarketOwner(), is).grantAmount(amount).save();
	}

	public CategoryInfos getCatInfosByName(String catname) {
		for (CategoryInfos ci : this.getCatsInfos()) {
			if (ci.getName() == catname) {
				return ci;
			}
		}
		return null;
	}

	public MarketItem getMarketItemByIS(ItemStack is) {
		for (MarketItem mis : this.getMarketItems()) {
			if (mis.getIs().isSimilar(is)) {
				return mis;
			}
		}
		return null;
	}

	public MarketItem getMarketItemByISByCat(ItemStack is, String catname) {
		for (MarketItem mis : this.getMarketItems()) {
			if (mis.getIs().isSimilar(is) && mis.getCatName().equals(catname)) {
				return mis;
			}
		}
		return null;
	}

	public List<MarketItem> getMarketItemsByMat(Material mat) {
		List<MarketItem> miss = new ArrayList<>();
		for (MarketItem mis : this.getMarketItems()) {
			if (mis.getIs().getType() == mat) {
				miss.add(mis);
			}
		}
		return miss;
	}

	public List<MarketItem> getMarketItemsByMat(Material mat, String catname) {
		List<MarketItem> miss = new ArrayList<>();
		for (MarketItem mis : this.getMarketItems()) {
			if ((mis.getIs().getType() == mat) && (mis.getCatName().equals(catname))) {
				miss.add(mis);
			}
		}
		return miss;
	}

	public String getMarketOwner() {
		return this.marketInfos.getMarketOwner();
	}

	public String getName() {
		return this.marketInfos.getName();
	}

	public String getDevise() {
		return this.marketInfos.getDevise();
	}

	public boolean isOpen() {
		return this.marketInfos.isOpen();
	}

	public long getSoldItems() {
		return this.marketInfos.getSoldItems();
	}

	public long getSales() {
		return this.marketInfos.getSales();
	}

	public Market setName(String name) {
		this.marketInfos.setName(name);
		return this;
	}

	public Market setDevise(String devise) {
		this.marketInfos.setDevise(devise);
		return this;
	}

	public Market setOpen(boolean open) {
		this.marketInfos.setOpen(open);
		return this;
	}

	public Market setSoldItems(long soldItems) {
		this.marketInfos.setSoldItems(soldItems);
		return this;
	}

	public Market setSales(long sales) {
		this.marketInfos.setSales(sales);
		return this;
	}

	public Market grantSalesOne() {
		this.marketInfos.setSales(this.marketInfos.getSales() + 1);
		return this;
	}

	public AIS getMarkeAIS() {
		AIS ais = new AIS(Material.CHEST);
		String devise = this.getDevise();
		String name = this.getName();
		String shopname = (this.getMarketOwner().equalsIgnoreCase("AdminShop") ? "AdminShop"
				: Bukkit.getOfflinePlayer(UUID.fromString(this.getMarketOwner())).getName());
		if (devise == null) {
			if (name != null) {
				ais.setName(ChatColor.translateAlternateColorCodes('&', name));
				ais.addLineToLore("").addLineToLore("§r§6" + shopname + "'s Market.");
			} else {
				ais.setName("§r§6" + shopname + "'s Market.");
			}
		} else {
			devise = ChatColor.translateAlternateColorCodes('&', devise);
			if (name != null) {
				ais.setName(ChatColor.translateAlternateColorCodes('&', name));
				ais.addLineToLore("§r§6" + shopname + "'s Market.").addLineToLore("").addLineToLore("§b" + devise);
			} else {
				ais.setName("§r§6" + shopname + "'s Market.");
				ais.addLineToLore("").addLineToLore("§b" + devise);
			}
		}
		return ais;
	}
 
}
