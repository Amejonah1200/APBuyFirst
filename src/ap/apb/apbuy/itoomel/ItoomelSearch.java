package ap.apb.apbuy.itoomel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ap.apb.apbuy.markets.MarketItem;

public class ItoomelSearch {

	private ItemStack itemStack;
	private Player player;
	private int page;
	private boolean comparingEnchantments;
	private boolean comparingEnchantsLvLs;
	private boolean comparingLore;
	private boolean comparingName;

	public ItoomelSearch(ItemStack itemStack, Player player) {
		this.itemStack = itemStack;
		this.player = player;
		this.comparingName = true;
		this.comparingLore = true;
		this.comparingEnchantments = true;
		this.comparingEnchantsLvLs = true;
	}

	public int getSimilarPts(ItemStack is) {
		int pts = 0;
		if (getItemStack().isSimilar(is)) {
			return Integer.MAX_VALUE;
		}
		String nameS = getItemStack().getItemMeta().getDisplayName();
		String nameIS = is.getItemMeta().getDisplayName();
		if (isComparingName()) {
			if (nameS == null && nameIS == null ? true
					: nameS == null && nameIS != null ? true
							: nameS != null && nameIS == null ? false : nameS.equals(nameIS)) {
				pts++;
			}
		}
		List<String> loreS = getItemStack().getItemMeta().getLore();
		List<String> loreIS = is.getItemMeta().getLore();
		if (isComparingLore()) {
			if (loreS == null && loreIS == null ? true
					: loreS == null && loreIS != null ? true
							: loreS != null && loreIS == null ? false : loreS.equals(loreIS)) {
				pts++;
			}
		}
//		if (isComparingEnchantments()) {
//			Set<Enchantment> setS = is.getEnchantments().keySet();
//			Set<Enchantment> setIS = getItemStack().getEnchantments().keySet();
//			if (setS.isEmpty() && setIS.isEmpty() ? true
//					: setS.isEmpty() && (!setIS.isEmpty()) ? true
//							: (!setS.isEmpty()) && setIS.isEmpty() ? false : setS.containsAll(setIS)) {
//				pts++;
//				// if (isComparingEnchantsLvLs()) {
//				// if
//				// (getItemStack().getEnchantments().values().containsAll(is.getEnchantments().values()))
//				// {
//				// System.out.println("Same enchants and level");
//				// pts++;
//				// }
//				// }
//			}
//		}
		return pts;
	}

	public List<MarketItem> sortList(List<MarketItem> missa) {
		List<MarketItem> miss = new ArrayList<>();
		miss.addAll(missa);
		System.out.println(getItemStack().toString());
		miss.sort(new Comparator<MarketItem>() {
			@Override
			public int compare(MarketItem mis1, MarketItem mis2) {
				return 0 - Integer.compare(getSimilarPts(mis1.getIs()), getSimilarPts(mis2.getIs()));
			}
		});
		return miss;
	}

	public ItemStack getItemStack() {
		return this.itemStack;
	}

	public Player getPlayer() {
		return this.player;
	}

	public int getPage() {
		return this.page;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setEnchantments(boolean enchantments) {
		this.comparingEnchantments = enchantments;
	}

	public void setEnchantsLvLs(boolean enchantsLvLs) {
		this.comparingEnchantsLvLs = enchantsLvLs;
	}

	public void setLore(boolean lore) {
		this.comparingLore = lore;
	}

	public void setName(boolean name) {
		this.comparingName = name;
	}

	public boolean isComparingEnchantments() {
		return this.comparingEnchantments;
	}

	public boolean isComparingEnchantsLvLs() {
		return this.comparingEnchantsLvLs;
	}

	public boolean isComparingLore() {
		return this.comparingLore;
	}

	public boolean isComparingName() {
		return this.comparingName;
	}

}
