package ap.apb.apbuy.itoomel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ap.apb.AIS;
import ap.apb.APBuy;
import ap.apb.Translator;
import ap.apb.Utils;
import ap.apb.apbuy.BuyManager;
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
		switch (menu) {
		case MAIN:
			Inventory invMain = Bukkit.createInventory(null, 27, "Itoomel");
			for (int i = 0; i < 27; i++) {
				invMain.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			invMain.setItem(4, new AIS(Material.CHEST).setName("§7Durchsuchen").toIS());
			invMain.setItem(10, new AIS(Material.CHEST).setName("§7Simple Suche").toIS());
			this.opened = true;
			this.getPlayer().openInventory(invMain);
			this.opened = false;
			break;
		case ALL_ITEMS:
			Inventory allItemsInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");
			for (int i = 0; i < 54; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
						|| (i == 49)) {
					continue;
				}
				allItemsInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			for (int i1 = 0; i1 < 4; i1++) {
				for (int i2 = 0; i2 < 7; i2++) {
					allItemsInv.setItem(10 + i1 * 9 + i2,
							new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
			}
			allItemsInv.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
					.addLineToLore(Translator.translate("menu.openerror2")).toIS());
			opened = true;
			getPlayer().openInventory(allItemsInv);
			opened = false;
			Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

				@Override
				public void run() {
					try {
						List<MarketItem> miss = Itoomel.getInstance().getAllMisFromNSize(28 * page, 28);
						// - Back Button 49
						allItemsInv.setItem(49, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());
						allItemsInv.setItem(48, new AIS("§7Switch to: All_Mats", 1, Material.PAPER).toIS());
						// - Getting all Markets to display
						int size = miss.size();
						int pages = ((size - (size % 28)) / 28);
						int count = 28 * page;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									allItemsInv.setItem(10 + i1 * 9 + i2, APBuy.tagger.setNBTTag("MIS",
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

						if ((pages > 0) && (pages != page) && (size - 28 * (page + 1) != 0)) {
							allItemsInv.setItem(53,
									APBuy.tagger.setNBTTag("ToPage", page + 1,
											new AIS("§7" + Translator.translate("menu.page.next") + " " + (page + 1), 1,
													Material.PAPER).toIS()));
						}
						if (page > 0) {
							allItemsInv
									.setItem(45,
											APBuy.tagger
													.setNBTTag("ToPage",
															page - 1, new AIS(
																	"§7" + (page - 1) + " "
																			+ Translator
																					.translate("menu.page.previous"),
																	1, Material.PAPER).toIS()));
						}

						if (allItemsInv.getItem(31).getType() == Material.PAPER) {
							allItemsInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						opened = true;
						getPlayer().openInventory(allItemsInv);
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
		case ALL_MATS:
			Inventory allMatsInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");
			for (int i = 0; i < 54; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
						|| (i == 49)) {
					continue;
				}
				allMatsInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			for (int i1 = 0; i1 < 4; i1++) {
				for (int i2 = 0; i2 < 7; i2++) {
					allMatsInv.setItem(10 + i1 * 9 + i2,
							new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
			}
			allMatsInv.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
					.addLineToLore(Translator.translate("menu.openerror2")).toIS());
			opened = true;
			getPlayer().openInventory(allMatsInv);
			opened = false;
			Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

				@Override
				public void run() {
					try {
						HashMap<Material, Long> hmstats = Itoomel.getInstance().getMatsNAmount();
						List<Material> mats = new ArrayList<>();
						mats.addAll(hmstats.keySet());
						mats.sort(new Comparator<Material>() {

							@Override
							public int compare(Material mat1, Material mat2) {
								return mat1.toString().compareTo(mat2.toString());
							}
						});
						// - Back Button 49
						allMatsInv.setItem(49, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());
						allMatsInv.setItem(48, new AIS("§7Switch to: All_Items", 1, Material.PAPER).toIS());
						// - Getting all Markets to display
						int size = mats.size();
						int pages = ((size - (size % 28)) / 28);
						int count = 28 * page;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									allMatsInv.setItem(10 + i1 * 9 + i2,
											APBuy.tagger.setNBTTag("Mat", true, new AIS(mats.get(count))
													.setName("§7Suche nach: §6" + mats.get(count).toString())
													.addLineToLore("")
													.addLineToLore("§7Items: " + hmstats.get(mats.get(count))).toIS()));
									count++;
								}
								if (count >= size) {
									break;
								}
							}
						}

						if ((pages > 0) && (pages != page) && (size - 28 * (page + 1) != 0)) {
							allMatsInv.setItem(53,
									APBuy.tagger.setNBTTag("ToPage", page + 1,
											new AIS("§7" + Translator.translate("menu.page.next") + " " + (page + 1), 1,
													Material.PAPER).toIS()));
						}
						if (page > 0) {
							allMatsInv
									.setItem(45,
											APBuy.tagger
													.setNBTTag("ToPage",
															page - 1, new AIS(
																	"§7" + (page - 1) + " "
																			+ Translator
																					.translate("menu.page.previous"),
																	1, Material.PAPER).toIS()));
						}

						if (allMatsInv.getItem(31).getType() == Material.PAPER) {
							allMatsInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						opened = true;
						getPlayer().openInventory(allMatsInv);
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
		case SEARCH_MAT:
			Inventory searchMatInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");
			for (int i = 0; i < 54; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
						|| (i == 49)) {
					continue;
				}
				searchMatInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			for (int i1 = 0; i1 < 4; i1++) {
				for (int i2 = 0; i2 < 7; i2++) {
					searchMatInv.setItem(10 + i1 * 9 + i2,
							new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
			}
			searchMatInv.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
					.addLineToLore(Translator.translate("menu.openerror2")).toIS());
			opened = true;
			getPlayer().openInventory(searchMatInv);
			opened = false;
			Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

				@Override
				public void run() {
					try {
						List<MarketItem> miss = Itoomel.getInstance().getMissByMat(search.getItemStack().getType());
						// - Back Button 49
						searchMatInv.setItem(49,
								new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());
						// searchMatInv.setItem(48, new AIS("§7Switch to:
						// All_Items", 1, Material.PAPER).toIS());
						// - Getting all Markets to display
						int size = miss.size();
						int pages = ((size - (size % 28)) / 28);
						int count = 28 * page;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									searchMatInv.setItem(10 + i1 * 9 + i2, APBuy.tagger.setNBTTag("MIS",
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

						if ((pages > 0) && (pages != page) && (size - 28 * (page + 1) != 0)) {
							searchMatInv.setItem(53,
									APBuy.tagger.setNBTTag("ToPage", page + 1,
											new AIS("§7" + Translator.translate("menu.page.next") + " " + (page + 1), 1,
													Material.PAPER).toIS()));
						}
						if (page > 0) {
							searchMatInv
									.setItem(45,
											APBuy.tagger
													.setNBTTag("ToPage",
															page - 1, new AIS(
																	"§7" + (page - 1) + " "
																			+ Translator
																					.translate("menu.page.previous"),
																	1, Material.PAPER).toIS()));
						}

						if (searchMatInv.getItem(31).getType() == Material.PAPER) {
							searchMatInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						opened = true;
						getPlayer().openInventory(searchMatInv);
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
		case SEARCH_INIT_SIMPLE:
			Inventory sISInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");
			for (int i = 0; i < 54; i++) {
				sISInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			sISInv.setItem(22, new AIS("§7Bitte wähle ein Item in deinem Inventar aus.", Material.PAPER).toIS());
			sISInv.setItem(31, this.getSearch().getItemStack().clone());
			sISInv.setItem(48, new AIS("§cAbbrechen", 1, (short) 14, Material.WOOL).toIS());
			sISInv.setItem(50, new AIS("§aSuchen", 1, (short) 5, Material.WOOL).toIS());
			opened = true;
			getPlayer().openInventory(sISInv);
			opened = false;
			break;
		case SEARCH_MIS:
			Inventory searchMisInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");
			for (int i = 0; i < 54; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
						|| (i == 49)) {
					continue;
				}
				searchMisInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			for (int i1 = 0; i1 < 4; i1++) {
				for (int i2 = 0; i2 < 7; i2++) {
					searchMisInv.setItem(10 + i1 * 9 + i2,
							new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
			}
			searchMisInv.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
					.addLineToLore(Translator.translate("menu.openerror2")).toIS());
			opened = true;
			getPlayer().openInventory(searchMisInv);
			opened = false;
			Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

				@Override
				public void run() {
					try {
						List<MarketItem> miss = getSearch()
								.sortList(Itoomel.getInstance().getMissByMat(getSearch().getItemStack().getType()));

						// - Back Button 49
						searchMisInv.setItem(49,
								new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());
						// searchMatInv.setItem(48, new AIS("§7Switch to:
						// All_Items", 1, Material.PAPER).toIS());
						// - Getting all Markets to display
						int size = miss.size();
						int pages = ((size - (size % 28)) / 28);
						int count = 28 * page;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									searchMisInv.setItem(10 + i1 * 9 + i2, APBuy.tagger.setNBTTag("MIS",
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

						if ((pages > 0) && (pages != page) && (size - 28 * (page + 1) != 0)) {
							searchMisInv.setItem(53,
									APBuy.tagger.setNBTTag("ToPage", page + 1,
											new AIS("§7" + Translator.translate("menu.page.next") + " " + (page + 1), 1,
													Material.PAPER).toIS()));
						}
						if (page > 0) {
							searchMisInv
									.setItem(45,
											APBuy.tagger
													.setNBTTag("ToPage",
															page - 1, new AIS(
																	"§7" + (page - 1) + " "
																			+ Translator
																					.translate("menu.page.previous"),
																	1, Material.PAPER).toIS()));
						}

						if (searchMisInv.getItem(31).getType() == Material.PAPER) {
							searchMisInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						opened = true;
						getPlayer().openInventory(searchMisInv);
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
		default:
			this.setMenu(ItoomelMenu.MAIN);
			this.setPage(0);
			this.setSearch(null);
			this.setOptions(null);
			this.open();
			break;
		}
	}

	public void onTopInvClick(InventoryClickEvent e) {
		switch (menu) {
		case MAIN:
			e.setCancelled(true);
			switch (e.getSlot()) {
			case 4:
				this.setMenu(ItoomelMenu.ALL_ITEMS);
				this.open();
				return;
			case 10:
				this.setMenu(ItoomelMenu.SEARCH_INIT_SIMPLE);
				this.setPage(0);
				this.setSearch(new ItoomelSearch(new ItemStack(Material.CHEST), player));
				this.open();
				return;
			default:
				break;
			}
			break;
		case ALL_ITEMS:
			e.setCancelled(true);
			if (e.getSlot() == 49) {
				this.setMenu(ItoomelMenu.MAIN);
				this.open();
				return;
			} else if (e.getSlot() == 48) {
				this.setMenu(ItoomelMenu.ALL_MATS);
				this.open();
				return;
			}
			if (APBuy.tagger.hasTag("MIS", e.getCurrentItem())) {
				if (BuyManager
						.openBuyManager(
								Itoomel.getInstance().getMisByMarketNIS(
										new AIS(e.getCurrentItem().clone()).removeLatestLore(4).removeNBTTag("MIS")
												.toIS(),
										APBuy.tagger.getNBTTagString("MIS", e.getCurrentItem().clone())),
								1, this.getPlayer(), true, new String[] { "ALL_ITEMS" })) {
					Itoomel.getInstance().removeFromNav(this.getPlayer());
				}
			}
			if (APBuy.tagger.hasTag("ToPage", e.getCurrentItem())) {
				this.setPage(APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()));
				this.open();
			}
			break;
		case ALL_MATS:
			e.setCancelled(true);
			if (e.getSlot() == 49) {
				this.setMenu(ItoomelMenu.MAIN);
				this.setSearch(null);
				this.setPage(0);
				this.open();
				return;
			} else if (e.getSlot() == 48) {
				this.setMenu(ItoomelMenu.ALL_ITEMS);
				this.setSearch(null);
				this.setPage(0);
				this.open();
				return;
			}
			if (APBuy.tagger.hasTag("Mat", e.getCurrentItem())) {
				this.setMenu(ItoomelMenu.SEARCH_MAT);
				this.setPage(0);
				this.setSearch(new ItoomelSearch(new ItemStack(e.getCurrentItem().getType()), getPlayer()));
				this.open();
			}
			if (APBuy.tagger.hasTag("ToPage", e.getCurrentItem())) {
				this.setPage(APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()));
				this.open();
			}
			break;
		case SEARCH_MAT:
			e.setCancelled(true);
			if (e.getSlot() == 49) {
				this.setMenu(ItoomelMenu.ALL_MATS);
				this.setSearch(null);
				this.setPage(0);
				this.open();
				return;
			}
			if (APBuy.tagger.hasTag("MIS", e.getCurrentItem())) {
				if (BuyManager
						.openBuyManager(
								Itoomel.getInstance().getMisByMarketNIS(
										new AIS(e.getCurrentItem().clone()).removeLatestLore(4).removeNBTTag("MIS")
												.toIS(),
										APBuy.tagger.getNBTTagString("MIS", e.getCurrentItem().clone())),
								1, this.getPlayer(), true,
								new String[] { "SEARCH_MAT", e.getCurrentItem().getType().toString() })) {
					Itoomel.getInstance().removeFromNav(this.getPlayer());
				}
			}
			if (APBuy.tagger.hasTag("ToPage", e.getCurrentItem())) {
				this.setPage(APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()));
				this.open();
			}
			break;
		case SEARCH_INIT_SIMPLE:
			e.setCancelled(true);
			switch (e.getSlot()) {
			case 48:
				this.setMenu(ItoomelMenu.MAIN);
				this.setSearch(null);
				this.setPage(0);
				this.open();
				break;
			case 50:
				this.setMenu(ItoomelMenu.SEARCH_MIS);
				this.setPage(0);
				this.open();
				break;
			}
			return;
		case SEARCH_MIS:
			e.setCancelled(true);
			if (e.getSlot() == 49) {
				this.setMenu(ItoomelMenu.ALL_ITEMS);
				this.setSearch(null);
				this.setPage(0);
				this.open();
				return;
			}
			if (APBuy.tagger.hasTag("MIS", e.getCurrentItem())) {
				if (BuyManager
						.openBuyManager(
								Itoomel.getInstance().getMisByMarketNIS(
										new AIS(e.getCurrentItem().clone()).removeLatestLore(4).removeNBTTag("MIS")
												.toIS(),
										APBuy.tagger.getNBTTagString("MIS", e.getCurrentItem().clone())),
								1, this.getPlayer(), true,
								new String[] { "SEARCH_MAT", e.getCurrentItem().getType().toString() })) {
					Itoomel.getInstance().removeFromNav(this.getPlayer());
				}
			}
			if (APBuy.tagger.hasTag("ToPage", e.getCurrentItem())) {
				this.setPage(APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()));
				this.open();
			}
			break;
		default:
			break;
		}
	}

	public void onBottomInvClick(InventoryClickEvent e) {
		switch (this.getMenu()) {
		case SEARCH_INIT_SIMPLE:
			e.setCancelled(true);
			if (e.getCurrentItem() != null) {
				if (e.getCurrentItem().getType() != Material.AIR) {
					this.getSearch().setItemStack(e.getCurrentItem());
					this.open();
				}
			}
			break;
		default:
			e.setCancelled(true);
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
		MAIN, ALL_ITEMS, ALL_MATS, ALL_ICATS, SEARCH_MIS, SEARCH_MAT, ICAT, SEARCH_INIT_SIMPLE, SEARCH_INIT_ADVANCED
	}

}
