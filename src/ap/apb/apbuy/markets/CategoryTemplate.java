package ap.apb.apbuy.markets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ap.apb.AIS;
import ap.apb.Utils;
import ap.apb.apbuy.itoomel.ItoomelPrime;

public class CategoryTemplate {

	private String name;
	private Material mat;
	private short subid;
	private List<MarketItem> catItems = new ArrayList<>();
	private String desc;

	public CategoryTemplate(String name, String desc, Material mat, short subid) {
		this.name = name;
		this.desc = desc;
		this.mat = mat;
		this.subid = subid;
	}

	public CategoryTemplate() {
		this.name = null;
		this.desc = null;
		this.mat = Material.CHEST;
		this.subid = 0;
	}

	public CategoryTemplate(String name, ItemStack itemStack) {
		this.name = name;
		this.desc = getDescFromIS(itemStack);
	}

	private String getDescFromIS(ItemStack itemStack) {
		String s = null;
		if(itemStack.getItemMeta() != null) {
			if(itemStack.getItemMeta().getLore() != null) {
				if(!itemStack.getItemMeta().getLore().isEmpty()) {
					s = String.join(" ", itemStack.getItemMeta().getLore());
				}
			}
		}
		return s;
	}

	public ItemStack buildToItem() {
		return new AIS(this.mat).setName(this.name == null ? "Preview" : this.name).addLineToLore("")
				.addToLore(
						Utils.createListFromStringToWidth(ChatColor.translateAlternateColorCodes('&', this.desc), 50))
				.setDamage(this.subid).toIS();
	}

	public Material getMat() {
		return this.mat;
	}

	public short getSubid() {
		return this.subid;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setMat(Material mat) {
		this.mat = mat;
	}

	public void setSubid(short subid) {
		this.subid = subid;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public List<MarketItem> getCatItems() {
		return this.catItems;
	}

	public void setCatItems(List<MarketItem> catItems) {
		this.catItems = catItems;
	}

	public void removeItem(ItemStack is) {
		Iterator<MarketItem> iterator = catItems.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getIs().isSimilar(is)) {
				iterator.remove();
			}
		}
	}

	public void addItem(MarketItem marketItem) {
		this.catItems.add(marketItem);
	}

	public boolean containsIS(ItemStack currentItem) {
		Iterator<MarketItem> iterator = this.getCatItems().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getIs().isSimilar(currentItem)) {
				return true;
			}
		}
		return false;
	}

	public void setItemAmmount(MarketItem is, long ammount) {
		for (MarketItem m : this.getCatItems()) {
			if (m.getIs().clone().equals(is.getIs().clone())) {
				m.setAmmount(ammount);
				return;
			}
		}
	}

	public void setItemSellAmmount(MarketItem is, int sellAmmount) {
		for (MarketItem m : this.getCatItems()) {
			if (m.getIs().clone().equals(is.getIs().clone())) {
				m.setSellAmmount(sellAmmount);
				return;
			}
		}
	}

	public void addToItems(ItemStack is) {
		MarketItem mis = this.getMISbyIS(is);
		this.removeItem(is);
		long iss = mis.getAmmount();
		if ((iss == 0) && (is.getAmount() > 0)) {
			iss = iss + is.getAmount();
			mis.setAmmount(iss);
			this.addItem(mis);
			ItoomelPrime.addMISToItoomel(mis);
		} else {
			iss = iss + is.getAmount();
			mis.setAmmount(iss);
			this.addItem(mis);
		}
	}

	public MarketItem getMISbyIS(ItemStack is) {
		for (MarketItem m : this.getCatItems()) {
			if (m.getIs().isSimilar(is)) {
				return m;
			}
		}
		return null;
	}

	public long removeItems(ItemStack is, long l) {
		MarketItem mis = this.getMISbyIS(is);
		this.removeItem(is);
		long iss = mis.getAmmount();
		iss = iss - l;
		mis.setAmmount(iss);
		this.addItem(mis);
		return mis.getAmmount();
	}

	public boolean containsType(Material type) {
		Iterator<MarketItem> iterator = this.getCatItems().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getIs().getType() == type) {
				return true;
			}
		}
		return false;
	}

	public List<MarketItem> getMISsByType(Material type) {
		List<MarketItem> miss = new ArrayList<>();
		for (MarketItem mis : this.getCatItems()) {
			if (mis.getIs().getType() == type) {
				miss.add(mis);
			}
		}
		return miss;
	}

	public List<ItemStack> getAllIS() {
		List<ItemStack> iss = new ArrayList<>();
		for (MarketItem mis : this.getCatItems()) {
			iss.add(mis.getIs());
		}
		return iss;
	}

	public boolean hasItems() {
		for (MarketItem mis : this.getCatItems()) {
			if (mis.getAmmount() > 0) {
				return true;
			}
		}
		return false;
	}

	public List<MarketItem> getMISsByTypeBuyable(Material type) {
		List<MarketItem> miss = this.getCatItems();
		Iterator<MarketItem> iterator = miss.iterator();
		while (iterator.hasNext()) {
			if (!iterator.next().isBuyable()) {
				iterator.remove();
			}
		}
		return miss;
	}
}
