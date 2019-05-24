package ap.apb.apbuy.itoomel;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import ap.apb.AIS;
import ap.apb.APBuy;
import ap.apb.Translator;
import ap.apb.Utils;
import ap.apb.apbuy.markets.MarketItem;

public class ItoomelNavigation {

	private ItoomelMenu menu;
	private Player player;
	private ItoomelSearch search;
	private int page;
	private boolean[] options;
	private boolean opened;

	public ItoomelNavigation(ItoomelMenu menu, Player player, ItoomelSearch search) {
		this.menu = menu;
		this.player = player;
		this.search = search;
	}

	public ItoomelNavigation(ItoomelMenu menu, Player player) {
		this.menu = menu;
		this.player = player;
		this.search = null;
	}

	public void open() {
		// TODO Buy in Itoomel
		// TODO New Menus
		switch (menu) {
		case MAIN:
			Inventory invMain = Bukkit.createInventory(null, 27, "Itoomel");
			for (int i = 0; i < 27; i++) {
				invMain.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			invMain.setItem(4, new AIS(Material.CHEST).setName("§7Durchsuchen").toIS());
			this.opened = true;
			this.getPlayer().openInventory(invMain);
			this.opened = false;
			break;
		case ALL:
			Inventory allInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");
			for (int i = 0; i < 54; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
						|| (i == 49)) {
					continue;
				}
				allInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			for (int i1 = 0; i1 < 4; i1++) {
				for (int i2 = 0; i2 < 7; i2++) {
					allInv.setItem(10 + i1 * 9 + i2, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
			}
			allInv.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
					.addLineToLore(Translator.translate("menu.openerror2")).toIS());
			opened = true;
			getPlayer().openInventory(allInv);
			opened = false;
			Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

				@Override
				public void run() {
					try {
						List<MarketItem> miss = Itoomel.getInstance().getAllMisFromNSize(28 * page, 28);
						// - Back Button 49
						allInv.setItem(49, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());
						// - Getting all Markets to display
						int size = miss.size();
						int pages = Itoomel.getInstance().getPages();
						int count = 28 * page;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									allInv.setItem(10 + i1 * 9 + i2, APBuy.tagger.setNBTTag("MIS",
											miss.get(count).getMarketuuid(),
											miss.get(count).getAISToShow().addLineToLore("§7Market: " + Bukkit
													.getOfflinePlayer(UUID.fromString(miss.get(count).getMarketuuid()))
													.getName().toString()).toIS()));
									count++;
								}
								if (count >= size) {
									break;
								}
							}
						}

						if ((pages > 0) && (pages != page) && (size - 28 * (pages + 1) != 0)) {
							allInv.setItem(53,
									APBuy.tagger.setNBTTag("ToPage", page + 1,
											new AIS("§7" + Translator.translate("menu.page.next") + " " + (page + 1), 1,
													Material.PAPER).toIS()));
						}
						if (page > 0) {
							allInv.setItem(45,
									APBuy.tagger
											.setNBTTag(
													"ToPage", page
															- 1,
													new AIS("§7" + (page - 1) + " "
															+ Translator.translate("menu.page.previous"), 1,
															Material.PAPER).toIS()));
						}

						if (allInv.getItem(31).getType() == Material.PAPER) {
							allInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						opened = true;
						getPlayer().openInventory(allInv);
						opened = false;
					} catch (Exception e1) {
						e1.printStackTrace();
						System.out.println(
								"Player: " + getPlayer().getName() + " (" + getPlayer().getUniqueId().toString() + ")");
						getPlayer().closeInventory();
						Itoomel.getInstance().removeFromNav(getPlayer());
						getPlayer().sendMessage(Translator.translate("dev.error"));
						getPlayer().sendMessage("§cError Code: " + Utils.addToFix(e1));
					}
				}
			});
			break;
		}
	}

	public void onClick(InventoryClickEvent e) {
		switch (menu) {
		case MAIN:
			e.setCancelled(true);
			switch (e.getSlot()) {
			case 4:
				this.setMenu(ItoomelMenu.ALL);
				this.open();
				break;
			}
			break;
		case ALL:
			e.setCancelled(true);
			switch (e.getSlot()) {
			case 49:
				this.setMenu(ItoomelMenu.MAIN);
				this.open();
				break;
			}
			break;
		}
	}

	public void onClose() {
		if (!this.opened) {
			Itoomel.getInstance().removeFromNav(this.getPlayer());
		}
	}

	public ItoomelMenu getMenu() {
		return this.menu;
	}

	public Player getPlayer() {
		return this.player;
	}

	public ItoomelSearch getSearch() {
		return this.search;
	}

	public int getPage() {
		return this.page;
	}

	public boolean[] getOptions() {
		return this.options;
	}

	public void setMenu(ItoomelMenu menu) {
		this.menu = menu;
	}

	public void setSearch(ItoomelSearch search) {
		this.search = search;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setOptions(boolean[] options) {
		this.options = options;
	}

	public enum ItoomelMenu {
		MAIN, ALL
	}

}
