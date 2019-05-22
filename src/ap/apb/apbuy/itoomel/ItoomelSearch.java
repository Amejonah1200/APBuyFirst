package ap.apb.apbuy.itoomel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ap.apb.AIS;
import ap.apb.APBuy;
import ap.apb.apbuy.markets.MarketItem;

public class ItoomelSearch {

	private ItemStack itemStack;
	private Player player;
	private int page;
	private boolean comparingEnchantments;
	private boolean comparingEnchantsLvLs;
	private boolean comparingLore;
	private boolean comparingName;

	public ItoomelSearch(ItemStack itemStack, Player player, int page) {
		this.itemStack = itemStack;
		this.player = player;
		this.page = page;
		this.comparingName = true;
		this.comparingLore = true;
		this.comparingEnchantments = true;
		this.comparingEnchantsLvLs = true;
	}

	public void openItoomelSearch() {
		Inventory inv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");
		for (int i = 0; i < 54; i++) {
			if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)) {
				continue;
			}
			inv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
		}
		for (int i1 = 0; i1 < 4; i1++) {
			for (int i2 = 0; i2 < 7; i2++) {
				inv.setItem(10 + i1 * 9 + i2, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
			}
		}
		inv.setItem(31, new AIS("§4Loading.. dont touch something.", 1, Material.PAPER).toIS());
		player.openInventory(inv);
		Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

			@Override
			public void run() {
				// - Getting all Markets to display
				List<MarketItem> miss = new ArrayList<>();
				miss.addAll(ItoomelPrime.itoomelStandard.get(getItemStack().getType()));
				miss.sort(new Comparator<MarketItem>() {

					@Override
					public int compare(MarketItem o1, MarketItem o2) {
						return Integer.compare(getSimilarPts(o1.getIs()), getSimilarPts(o2.getIs()));
					}
				});
				int size = miss.size();
				int pages = ((size - (size % 28)) / 28);
				int count = 28 * page;
				if (size != 0) {
					for (int i1 = 0; i1 < 4; i1++) {
						for (int i2 = 0; i2 < 7; i2++) {
							if (count >= size) {
								break;
							}
							inv.setItem(
									10 + i1 * 9
											+ i2,
									APBuy.tagger.setNBTTag("MIS", miss.get(count).getMarketuuid(),
											new AIS(miss.get(count).getAISToShow().toIS().clone())
													.addLineToLore("§6Market: " + Bukkit
															.getOfflinePlayer(
																	UUID.fromString(miss.get(count).getMarketuuid()))
															.getName().toString())
													.toIS()));
							count++;
						}
						if (count >= size) {
							break;
						}
					}
				}

				if ((pages > 0) && (pages != page) && (size - 28 * (page + 1) != 0)) {
					inv.setItem(53, APBuy.tagger.setNBTTag("ToPage", page + 1,
							new AIS("§7Next page --> " + (page + 1), 1, Material.PAPER).toIS()));
				}
				if (page > 0) {
					inv.setItem(45, APBuy.tagger.setNBTTag("ToPage", page - 1,
							new AIS("§7" + (page - 1) + " <-- Previous page", 1, Material.PAPER).toIS()));
				}

				if (inv.getItem(31).getType() == Material.PAPER) {
					inv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
				player.openInventory(inv);
			}
		});
	}

	public int getSimilarPts(ItemStack is) {
		int pts = 0;
		if (getItemStack().isSimilar(is)) {
			return Integer.MAX_VALUE;
		}
		if (isComparingName()) {
			if (is.getItemMeta().getDisplayName().equals(getItemStack().getItemMeta().getDisplayName())) {
				pts++;
			}
		}
		if (isComparingLore()) {
			if (getItemStack().getItemMeta().getLore().equals(is.getItemMeta().getLore())) {
				pts++;
			}
		}
		if (isComparingEnchantments()) {
			if (getItemStack().getEnchantments().keySet().containsAll(is.getEnchantments().keySet())) {
				pts++;
				if (isComparingEnchantsLvLs()) {
					if (getItemStack().getEnchantments().values().containsAll(is.getEnchantments().values())) {
						pts++;
					}
				}
			}
		}
		return pts;
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
