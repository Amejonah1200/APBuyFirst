package ap.apb.apbuy.markets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	}

	public CategoryInfos getCatInfosByName(String catname) {
		for (CategoryInfos ci : this.getCatsInfos()) {
			if (ci.getName().equals(catname)) {
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
		return this.marketInfos.getMarketAIS();
	}

	public boolean isItemStackRegistered(ItemStack is) {
		for (MarketItem mis : this.getMarketItems()) {
			if (mis.getIs().isSimilar(is)) {
				return true;
			}
		}
		return false;
	}

	public List<MarketItem> getMarketItemsByCat(String catname) {
		List<MarketItem> miss = this.getMarketItems();
		Iterator<MarketItem> iterator = miss.iterator();
		while (iterator.hasNext()) {
			if (!iterator.next().getCatName().equals(catname)) {
				iterator.remove();
			}
		}
		return miss;
	}

	public void removeCategory(String catname) throws MarketException {
		APBuy.database.removeCategory(this.getMarketOwner(), catname);
	}

	public boolean has(MarketItem mis2, long leng) {
		MarketItem mis = this.getMarketItemByIS(mis2.getIs().clone());
		if (mis.getIs().isSimilar(mis2.getIs().clone())) {
			if (mis.isBuyable()) {
				if (mis.getAmmount() - (leng * mis.getSellAmmount()) >= 0) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isMisChanged(MarketItem marketItem) {
		MarketItem mis = this.getMarketItemByIS(marketItem.getIs().clone());
		if (mis.getIs().isSimilar(marketItem.getIs().clone())) {
			if (mis.isBuyable() == marketItem.isBuyable()) {
				if (mis.getAmmount() == marketItem.getAmmount()) {
					return false;
				}
			}
		}
		return true;
	}

	public long removeItem(ItemStack is, long l) throws MarketException {
		APBuy.database.getMarketItemByIS(this.getMarketOwner(), is).grantAmount(-l).save();
		return APBuy.database.getMarketItemByIS(this.getMarketOwner(), is).getAmmount();
	}

	public long getAmmountOfMaterialSubID(Material type, Short s) {
		List<MarketItem> miss = new ArrayList<>();
		miss.addAll(this.getMarketItemsByMat(type));
		long i = 0;
		for (MarketItem mis : miss) {
			if (mis.getIs().getDurability() == s) {
				i = i + mis.getAmmount();
			}
		}
		return i;
	}

	public long getAmmountOfMaterial(Material type) {
		List<MarketItem> miss = new ArrayList<>();
		miss.addAll(this.getMarketItemsByMat(type));
		long i = 0;
		for (MarketItem mis : miss) {
			i = i + mis.getAmmount();
		}
		return i;
	}

	public List<MarketItem> getMISsByTypeRest(Material type) {
		List<MarketItem> miss = new ArrayList<>();
		miss.addAll(this.getMarketItemsByMat(type));
		Iterator<MarketItem> iterator = miss.iterator();
		while (iterator.hasNext()) {
			if (!iterator.next().isBuyable()) {
				iterator.remove();
			}
		}
		return miss;
	}

	public List<MarketItem> getMISsByTypeRestSubID(Material type, int subid) {
		List<MarketItem> miss = new ArrayList<>();
		miss.addAll(this.getMarketItemsByMat(type));
		Iterator<MarketItem> iterator = miss.iterator();
		MarketItem mis = null;
		while (iterator.hasNext()) {
			mis = iterator.next();
			if ((!mis.isBuyable()) || (mis.getIs().getDurability() != subid)) {
				iterator.remove();
			}
		}
		return miss;
	}

}
