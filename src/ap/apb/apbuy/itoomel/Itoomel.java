package ap.apb.apbuy.itoomel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ap.apb.AIS;
import ap.apb.APBuy;
import ap.apb.Utils;
import ap.apb.apbuy.BuyManager;
import ap.apb.apbuy.markets.Market;
import ap.apb.apbuy.markets.MarketException;
import ap.apb.apbuy.markets.MarketItem;

public class Itoomel implements Listener {

	public static HashMap<Player, Object[]> onItoomel = new HashMap<>();
	public static HashMap<Material, List<MarketItem>> itoomelStandard = new HashMap<>();

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getClickedInventory() == e.getView().getBottomInventory())
			return;
		try {
			if (Itoomel.onItoomel.containsKey(p)) {
				e.setCancelled(true);
				String menu = String.valueOf(Itoomel.onItoomel.get(p)[0]);
				Material type = (Material) Itoomel.onItoomel.get(p)[1];
				if (menu.equalsIgnoreCase("ICats")) {
					if ((10 <= e.getSlot() && e.getSlot() <= 16) || (19 <= e.getSlot() && e.getSlot() <= 25)
							|| (28 <= e.getSlot() && e.getSlot() <= 34) || (37 <= e.getSlot() && e.getSlot() <= 43)) {
						if (APBuy.tagger.hasTag("ICat", e.getCurrentItem())) {
							APBuy.getMarketHandler().PMLocPage.put(p, 0);
							ItoomelCat icat = ICats.getICatByType(e.getCurrentItem().getType());
							int i = icat.getCatMatsRest().size();
							if (i == 1) {
								if (icat.getCatMatsRest().values().toArray(new Short[0][0])[0].length == 1) {
									APBuy.getMarketHandler().PMLocPage.put(p, 0);
									Itoomel.openItoomel("Main", p,
											(Material) ICats.getICatByType(e.getCurrentItem().getType())
													.getCatMatsRest().keySet().toArray()[0],
											0);
								} else {
									APBuy.getMarketHandler().PMLocPage.put(p, 0);
									Itoomel.openItoomel("CatSelect", p, e.getCurrentItem().getType(), 0);
								}
							} else if (i == 0) {
								p.sendMessage("§cHmm keine Items drin gefunden, vllt wurde alles gekauft...");
							} else {
								APBuy.getMarketHandler().PMLocPage.put(p, 0);
								Itoomel.openItoomel("CatSelect", p, e.getCurrentItem().getType(), 0);
							}
						} else if (APBuy.tagger.hasTag("Market", e.getCurrentItem())) {
							APBuy.getMarketHandler().PMLocPage.put(p, 0);
							Itoomel.openItoomel("Market:" + APBuy.tagger.getNBTTagString("Market", e.getCurrentItem()),
									p, type, 0);
						}
					} else if (e.getSlot() == 49) {
						if (e.getCurrentItem().getType() == Material.BARRIER) {
							APBuy.getMarketHandler().PMLocPage.put(p, 0);
							Itoomel.openItoomel("Main", p, Material.AIR, 0);
						}
					} else if (e.getSlot() == 46) {
						if (e.getCurrentItem().getType() == Material.PAPER) {
							if (e.getCurrentItem().getItemMeta().getLore().get(0).contains("^")) {
								APBuy.getMarketHandler().PMLocPage.put(p, 0);
								Itoomel.openItoomel("Main", p, Material.AIR, 0, 2);
							} else if (e.getCurrentItem().getItemMeta().getLore().get(0).contains("v")) {
								APBuy.getMarketHandler().PMLocPage.put(p, 0);
								Itoomel.openItoomel("Main", p, Material.AIR, 0, 1);
							}
						}
					} else if (e.getSlot() == 47) {
						if (e.getCurrentItem().getType() == Material.PAPER) {
							if (e.getCurrentItem().getItemMeta().getLore().get(0).contains("^")) {
								APBuy.getMarketHandler().PMLocPage.put(p, 0);
								Itoomel.openItoomel("Main", p, Material.AIR, 0, 4);
							} else if (e.getCurrentItem().getItemMeta().getLore().get(0).contains("v")) {
								APBuy.getMarketHandler().PMLocPage.put(p, 0);
								Itoomel.openItoomel("Main", p, Material.AIR, 0, 3);
							}
						}
					} else if (e.getSlot() == 48) {
						if (e.getCurrentItem().getType() == Material.PAPER) {
							if (e.getCurrentItem().getItemMeta().getLore().get(0).contains("^")) {
								APBuy.getMarketHandler().PMLocPage.put(p, 0);
								Itoomel.openItoomel("Main", p, Material.AIR, 0, 6);
							} else if (e.getCurrentItem().getItemMeta().getLore().get(0).contains("v")) {
								APBuy.getMarketHandler().PMLocPage.put(p, 0);
								Itoomel.openItoomel("Main", p, Material.AIR, 0, 5);
							}
						}
					} else if (APBuy.tagger.hasTag("ToPage", e.getCurrentItem())) {
						APBuy.getMarketHandler().PMLocPage.put(p,
								APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()));
						if (type == Material.AIR) {
							int i = (int) Itoomel.onItoomel.get(p)[2];
							Itoomel.openItoomel("Main", p, type,
									APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()), i);
						} else {
							Itoomel.openItoomel("Main", p, type,
									APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()), 0);
						}

					}
				} else if ((menu.startsWith("Market:")) || (menu.startsWith("SubMarket:"))) {
					if (e.getSlot() == 49) {
						if (e.getCurrentItem().getType() == Material.BARRIER) {
							APBuy.getMarketHandler().PMLocPage.put(p, 0);
							Itoomel.openItoomel("Main", p, type, 0);
						}
					} else if (APBuy.tagger.hasTag("ToPage", e.getCurrentItem())) {
						APBuy.getMarketHandler().PMLocPage.put(p,
								APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()));
						if (type == Material.AIR) {
							int i = (int) Itoomel.onItoomel.get(p)[2];
							Itoomel.openItoomel(menu, p, type, APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()),
									i);
						} else {
							Itoomel.openItoomel(menu, p, type, APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()),
									menu.startsWith("Market:") ? 0 : (Integer) Itoomel.onItoomel.get(p)[2]);
						}

					} else if (APBuy.tagger.hasTag("ToBuy", e.getCurrentItem())) {
						if (BuyManager
								.openBuyManager(
										new Market(
												menu.startsWith("Market:") ? menu.replaceFirst("Market:", "")
														: menu.replaceFirst("SubMarket:", ""),
												false).getMarketItemByIS(
														new AIS(e.getCurrentItem().clone()).removeLatestLore(4).toIS()),
										menu.startsWith("Market:") ? menu.replaceFirst("Market:", "")
												: menu.replaceFirst("SubMarket:", ""),
										1, (Player) e.getWhoClicked(), true, null)) {
							APBuy.getMarketHandler().PMLocPage.remove(p);
							Itoomel.onItoomel.remove(p);
							return;
						}
					}

				} else if (menu.equalsIgnoreCase("CatSelect")) {
					if ((10 <= e.getSlot() && e.getSlot() <= 16) || (19 <= e.getSlot() && e.getSlot() <= 25)
							|| (28 <= e.getSlot() && e.getSlot() <= 34) || (37 <= e.getSlot() && e.getSlot() <= 43)) {
						if (APBuy.tagger.hasTag("ToSelect", e.getCurrentItem())) {
							APBuy.getMarketHandler().PMLocPage.put(p, 0);
							if (APBuy.tagger.hasTag("NoSubID", e.getCurrentItem())) {
								Itoomel.openItoomel("Main", p, e.getCurrentItem().getType(), 0);
							} else {
								Itoomel.openItoomel("searchSubID:" + e.getCurrentItem().getDurability(), p,
										e.getCurrentItem().getType(), 0);
							}
						}
					} else if (e.getSlot() == 49) {
						APBuy.getMarketHandler().PMLocPage.put(p, 0);
						Itoomel.openItoomel("Main", p, null, 0);
					} else if (APBuy.tagger.hasTag("ToPage", e.getCurrentItem())) {
						APBuy.getMarketHandler().PMLocPage.put(p,
								APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()));
						if (type == Material.AIR) {
							int i = (int) Itoomel.onItoomel.get(p)[2];
							Itoomel.openItoomel(menu, p, type, APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()),
									i);
						} else {
							Itoomel.openItoomel(menu, p, type, APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()),
									0);
						}
					}
				} else if (menu.equalsIgnoreCase("search")) {
					if (e.getSlot() == 49) {
						if (e.getCurrentItem().getType() == Material.BARRIER) {
							APBuy.getMarketHandler().PMLocPage.put(p, 0);
							openItoomel("Main", p, type, 0);
						}
					} else if (APBuy.tagger.hasTag("Market", e.getCurrentItem())) {
						APBuy.getMarketHandler().PMLocPage.put(p, 0);
						Itoomel.openItoomel("Market:" + APBuy.tagger.getNBTTagString("Market", e.getCurrentItem()), p,
								type, 0);
					}
				} else if (menu.contains("searchSubID:")) {
					if (e.getSlot() == 49) {
						if (e.getCurrentItem().getType() == Material.BARRIER) {
							APBuy.getMarketHandler().PMLocPage.put(p, 0);
							openItoomel("Main", p, Material.AIR, 0);
						}
					} else if (APBuy.tagger.hasTag("Market", e.getCurrentItem())) {
						APBuy.getMarketHandler().PMLocPage.put(p, 0);
						Itoomel.openItoomel("SubMarket:" + APBuy.tagger.getNBTTagString("Market", e.getCurrentItem()),
								p, type, 0, Integer.valueOf(menu.replaceFirst("searchSubID:", "")));
					}
				}
			}
		} catch (MarketException e1) {
			switch (e1.getErrorCause()) {
			case CATNOTFOUND:
				p.sendMessage("§cDieser Kategorie wurde nicht gefunden! (Error)");
				APBuy.getMarketHandler().removeFromAll(p);
				p.closeInventory();
				return;
			case LOAD:
				break;
			case MIS:
				break;
			case NOTFOUND:
				p.sendMessage("§cDieser Market wurde nicht gefunden! (Error)");
				APBuy.getMarketHandler().removeFromAll(p);
				p.closeInventory();
				APBuy.getMarketHandler().openInvToP("Markets", p);
				return;
			case NULL:
				break;
			case SAVE:
				break;
			}
			APBuy.getMarketHandler().removeFromAll(p);
			p.closeInventory();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
			System.out.println("Slot: " + e.getSlot());
			p.closeInventory();
			APBuy.getMarketHandler().removeFromAll(p);
			p.sendMessage("§cEin Fehler ist aufgetreten, bitte kontaktiere einen Dev.");
			p.sendMessage("§c Das hier dem Dev sagen : " + Utils.addToFix(e1));
		}
	}

	public static void openItoomel(String menu, Player p, Material type, int page) {
		try {
			openItoomel(menu, p, type, page, 1);
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
			System.out.println("Menu: " + menu);
			System.out.println("Page: " + page);
			p.closeInventory();
			APBuy.getMarketHandler().removeFromAll(p);
			p.sendMessage("§cEin Fehler ist aufgetreten, bitte kontaktiere einen Dev.");
			p.sendMessage("§c Das hier dem Dev sagen : " + Utils.addToFix(e1));
		}
	}

	public static void openItoomel(String menu, Player p, Material type, int page, int mode) throws MarketException {
		// System.out.println("MenuItoomel: " + menu);
		// System.out.println("Type: " + type.toString());
		if ((menu == "Main") && ((type == Material.AIR) || type == null)) {
			Itoomel.onItoomel.remove(p);
			Inventory MainInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");
			for (int i = 0; i < 54; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)) {
					continue;
				}
				MainInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			for (int i1 = 0; i1 < 4; i1++) {
				for (int i2 = 0; i2 < 7; i2++) {
					MainInv.setItem(10 + i1 * 9 + i2, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
			}
			MainInv.setItem(31, new AIS("§4Loading.. dont touch something.", 1, Material.PAPER).toIS());
			p.openInventory(MainInv);
			Itoomel.onItoomel.put(p, new Object[] { "Main", Material.AIR, mode });
			Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

				@Override
				public void run() {
					try {
						// - Getting all Markets to display
						List<ItoomelCat> allMats = new ArrayList<>();
						allMats.addAll(APBuy.plugin.getIcatslist());
						Iterator<ItoomelCat> iterator = allMats.iterator();
						while (iterator.hasNext()) {
							if (!iterator.next().hasOneMat(Itoomel.itoomelStandard.keySet())) {
								iterator.remove();
							}
						}
						int size = allMats.size();
						int pages = ((size - (size % 28)) / 28);
						int count = 28 * page;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									MainInv.setItem(10 + i1 * 9 + i2,
											APBuy.tagger.setNBTTag("ICat", allMats.get(count).getCatMat().toString(),
													new AIS(new ItemStack(allMats.get(count).getCatMat()).clone())
															.addLineToLore("")
															.addToLore(Utils.createListFromStringToWidth(
																	"§7" + allMats.get(count).getDescription(), 40))
															.addLineToLore("")
															.addLineToLore("§7Total Items: "
																	+ Itoomel.getTotalItems(allMats.get(count)))
															.addLineToLore("§7Total Markets: "
																	+ Itoomel.getTotalMarkets(allMats.get(count)))
															.addLineToLore("§7Sold Items: "
																	+ Itoomel.getTotalSoldItems(allMats.get(count)))
															.setName(allMats.get(count).getName()).toIS()));
									count++;
								}
								if (count >= size) {
									break;
								}
							}
						}

						if ((pages > 0) && (pages != page) && (size - 28 * (page + 1) != 0)) {
							MainInv.setItem(53, APBuy.tagger.setNBTTag("ToPage", page + 1,
									new AIS("§7Next page --> " + (page + 1), 1, Material.PAPER).toIS()));
						}
						if (page > 0) {
							MainInv.setItem(45, APBuy.tagger.setNBTTag("ToPage", page - 1,
									new AIS("§7" + (page - 1) + " <-- Previous page", 1, Material.PAPER).toIS()));
						}

						if (MainInv.getItem(31).getType() == Material.PAPER) {
							MainInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						p.openInventory(MainInv);
						Itoomel.onItoomel.remove(p);
						Itoomel.onItoomel.put(p, new Object[] { "Main", Material.AIR, mode });
						APBuy.getMarketHandler().PMLocPage.put(p, page);
					} catch (Exception e1) {
						e1.printStackTrace();
						System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
						System.out.println("Menu: " + menu);
						p.closeInventory();
						APBuy.getMarketHandler().removeFromAll(p);
						p.sendMessage("§cEin Fehler ist aufgetreten, bitte kontaktiere einen Dev.");
						p.sendMessage("§c Das hier dem Dev sagen : " + Utils.addToFix(e1));
					}
				}
			});
		} else if ((menu == "Main") && (type != Material.AIR) && (type != null)) {

			Itoomel.onItoomel.remove(p);
			Itoomel.onItoomel.put(p, new Object[] { "Main", type });
			Inventory MainInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");
			for (int i = 0; i < 54; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)) {
					continue;
				}
				MainInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			for (int i1 = 0; i1 < 4; i1++) {
				for (int i2 = 0; i2 < 7; i2++) {
					MainInv.setItem(10 + i1 * 9 + i2, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
			}

			// - Back Button 49
			MainInv.setItem(49, new AIS("§7<-- Back", 1, Material.BARRIER).toIS());

			MainInv.setItem(31, new AIS("§4Loading.. dont touch something.", 1, Material.PAPER).toIS());
			p.openInventory(MainInv);
			Itoomel.onItoomel.remove(p);
			Itoomel.onItoomel.put(p, new Object[] { "Main", type });
			Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

				@Override
				public void run() {
					try {
						// - Back Button 49
						MainInv.setItem(49, new AIS("§7<-- Back", 1, Material.BARRIER).toIS());

						MainInv.setItem(4, new ItemStack(type).clone());
						// - Getting all Markets to display
						if (Itoomel.itoomelStandard.get(type) == null) {
							p.closeInventory();
							openItoomel("Main", p, null, page);
							p.sendMessage("§cDieses Item ist nicht mehr auf dem Markt! :/");
							return;
						}
						List<String> allMarket = getMarketsUUIDsInItoomelByType(type);
						allMarket.sort(new Comparator<String>() {

							@Override
							public int compare(String arg0, String arg1) {
								try {
									return APBuy.getMarketHandler().compareByUUID(arg0, arg1);
								} catch (MarketException e) {
								}
								return 0;
							}
						});
						int size = allMarket.size();
						int pages = ((size - (size % 28)) / 28);
						int count = 28 * page;
						Market m;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									m = new Market(allMarket.get(count), true);
									MainInv.setItem(10 + i1 * 9 + i2,
											APBuy.tagger.setNBTTag("Market", allMarket.get(count),
													m.getMarkeAIS().addLineToLore("")
															.addLineToLore("§7Items: " + m.getAmmountOfMaterial(type))
															.toIS()));
									count++;
								}
								if (count >= size) {
									break;
								}
							}
						}

						if ((pages > 0) && (pages != page) && (size - 28 * (page + 1) != 0)) {
							MainInv.setItem(53, APBuy.tagger.setNBTTag("ToPage", page + 1,
									new AIS("§7Next page --> " + (page + 1), 1, Material.PAPER).toIS()));
						}
						if (page > 0) {
							MainInv.setItem(45, APBuy.tagger.setNBTTag("ToPage", page - 1,
									new AIS("§7" + (page - 1) + " <-- Previous page", 1, Material.PAPER).toIS()));
						}

						if (MainInv.getItem(31).getType() == Material.PAPER) {
							MainInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						p.openInventory(MainInv);
						Itoomel.onItoomel.remove(p);
						Itoomel.onItoomel.put(p, new Object[] { "Main", type });
						APBuy.getMarketHandler().PMLocPage.put(p, page);
					} catch (Exception e1) {
						e1.printStackTrace();
						System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
						System.out.println("Menu: " + menu);
						p.closeInventory();
						APBuy.getMarketHandler().removeFromAll(p);
						p.sendMessage("§cEin Fehler ist aufgetreten, bitte kontaktiere einen Dev.");
						p.sendMessage("§c Das hier dem Dev sagen : " + Utils.addToFix(e1));
					}
				}
			});
		} else if (menu.startsWith("Market:")) {
			Itoomel.onItoomel.remove(p);
			Itoomel.onItoomel.put(p, new Object[] { menu, type });
			Inventory MainInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");

			for (int i = 0; i < 54; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)) {
					continue;
				}
				MainInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			for (int i1 = 0; i1 < 4; i1++) {
				for (int i2 = 0; i2 < 7; i2++) {
					MainInv.setItem(10 + i1 * 9 + i2, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
			}
			MainInv.setItem(31, new AIS("§4Loading.. dont touch something.", 1, Material.PAPER).toIS());
			p.openInventory(MainInv);
			Itoomel.onItoomel.remove(p);
			Itoomel.onItoomel.put(p, new Object[] { menu, type });
			Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

				@Override
				public void run() {
					try {
						// - Back Button 49
						MainInv.setItem(49, new AIS("§7<-- Back", 1, Material.BARRIER).toIS());

						// - Getting all Markets to display
						MainInv.setItem(4, new ItemStack(type).clone());
						List<MarketItem> allMarket = new ArrayList<>();
						allMarket = new Market(menu.replaceFirst("Market:", ""), false).getMISsByTypeRest(type);
						if (allMarket.size() == 0) {
							p.closeInventory();
							openItoomel("search", p, type, page);
							p.sendMessage(
									"§cDieses Market hat keine zukaufende Items mehr... Suche dir einen anderen aus :/");
							return;
						}
						int size = allMarket.size();
						int pages = ((size - (size % 28)) / 28);
						int count = 28 * page;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									List<String> lore = new ArrayList<>();
									lore.add("");
									lore.add("§7Preis: " + allMarket.get(count).getPrice() + "$");
									lore.add("§7Auf Lager: " + allMarket.get(count).getAmmount());
									lore.add("§7Verkauft: " + allMarket.get(count).getSoldItems());
									MainInv.setItem(10 + i1 * 9 + i2, APBuy.tagger.setNBTTag("ToBuy", count,
											new AIS(allMarket.get(count).getIs().clone()).addToLore(lore).toIS()));
									count++;
								}
								if (count >= size) {
									break;
								}
							}
						}

						if ((pages > 0) && (pages != page) && (size - 28 * (page + 1) != 0)) {
							MainInv.setItem(53, APBuy.tagger.setNBTTag("ToPage", page + 1,
									new AIS("§7Next page --> " + (page + 1), 1, Material.PAPER).toIS()));
						}
						if (page > 0) {
							MainInv.setItem(45, APBuy.tagger.setNBTTag("ToPage", page - 1,
									new AIS("§7" + (page - 1) + " <-- Previous page", 1, Material.PAPER).toIS()));
						}

						if (MainInv.getItem(31).getType() == Material.PAPER) {
							MainInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						p.openInventory(MainInv);
						Itoomel.onItoomel.remove(p);
						Itoomel.onItoomel.put(p, new Object[] { menu, type });
						APBuy.getMarketHandler().PMLocPage.put(p, page);
					} catch (Exception e1) {
						e1.printStackTrace();
						System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
						System.out.println("Menu: " + menu);
						p.closeInventory();
						APBuy.getMarketHandler().removeFromAll(p);
						p.sendMessage("§cEin Fehler ist aufgetreten, bitte kontaktiere einen Dev.");
						p.sendMessage("§c Das hier dem Dev sagen : " + Utils.addToFix(e1));
					}
				}

			});
		} else if (menu.startsWith("SubMarket:")) {
			Itoomel.onItoomel.remove(p);
			Itoomel.onItoomel.put(p, new Object[] { menu, type });
			Inventory MainInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");

			for (int i = 0; i < 54; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)) {
					continue;
				}
				MainInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			for (int i1 = 0; i1 < 4; i1++) {
				for (int i2 = 0; i2 < 7; i2++) {
					MainInv.setItem(10 + i1 * 9 + i2, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
			}
			MainInv.setItem(31, new AIS("§4Loading.. dont touch something.", 1, Material.PAPER).toIS());
			p.openInventory(MainInv);
			Itoomel.onItoomel.remove(p);
			Itoomel.onItoomel.put(p, new Object[] { menu, type });
			Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

				@Override
				public void run() {
					try {
						// - Back Button 49
						MainInv.setItem(49, new AIS("§7<-- Back", 1, Material.BARRIER).toIS());

						// - Getting all Markets to display
						MainInv.setItem(4, new AIS(new ItemStack(type).clone()).setDamage((short) mode).toIS());
						List<MarketItem> allMarket = new ArrayList<>();
						allMarket = new Market(menu.replaceFirst("SubMarket:", ""), false).getMISsByTypeRestSubID(type,
								mode);
						if (allMarket.size() == 0) {
							p.closeInventory();
							openItoomel("search", p, type, page);
							p.sendMessage(
									"§cDieses Market hat keine zukaufende Items mehr... Suche dir einen anderen aus :/");
							return;
						}
						int size = allMarket.size();
						int pages = ((size - (size % 28)) / 28);
						int count = 28 * page;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									List<String> lore = new ArrayList<>();
									lore.add("");
									lore.add("§7Preis: " + allMarket.get(count).getPrice() + "$");
									lore.add("§7Auf Lager: " + allMarket.get(count).getAmmount());
									lore.add("§7Verkauft: " + allMarket.get(count).getSoldItems());
									MainInv.setItem(10 + i1 * 9 + i2, APBuy.tagger.setNBTTag("ToBuy", count,
											new AIS(allMarket.get(count).getIs().clone()).addToLore(lore).toIS()));
									count++;
								}
								if (count >= size) {
									break;
								}
							}
						}

						if ((pages > 0) && (pages != page) && (size - 28 * (page + 1) != 0)) {
							MainInv.setItem(53, APBuy.tagger.setNBTTag("ToPage", page + 1,
									new AIS("§7Next page --> " + (page + 1), 1, Material.PAPER).toIS()));
						}
						if (page > 0) {
							MainInv.setItem(45, APBuy.tagger.setNBTTag("ToPage", page - 1,
									new AIS("§7" + (page - 1) + " <-- Previous page", 1, Material.PAPER).toIS()));
						}

						if (MainInv.getItem(31).getType() == Material.PAPER) {
							MainInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						p.openInventory(MainInv);
						Itoomel.onItoomel.remove(p);
						Itoomel.onItoomel.put(p, new Object[] { menu, type });
						APBuy.getMarketHandler().PMLocPage.put(p, page);
					} catch (Exception e1) {
						e1.printStackTrace();
						System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
						System.out.println("Menu: " + menu);
						p.closeInventory();
						APBuy.getMarketHandler().removeFromAll(p);
						p.sendMessage("§cEin Fehler ist aufgetreten, bitte kontaktiere einen Dev.");
						p.sendMessage("§c Das hier dem Dev sagen : " + Utils.addToFix(e1));
					}
				}

			});
		} else if (menu.equalsIgnoreCase("search")) {
			if (!Itoomel.itoomelStandard.containsKey(type)) {
				p.sendMessage(
						"§cItoomel hat keinen Item gefunden das deinen Type entspricht. (" + type.toString() + ")");
				Itoomel.onItoomel.remove(p);
				APBuy.getMarketHandler().PMLocPage.remove(p);
				return;
			}
			Itoomel.onItoomel.remove(p);
			Itoomel.onItoomel.put(p, new Object[] { menu, type });
			Inventory MainInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");

			for (int i = 0; i < 54; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)) {
					continue;
				}
				MainInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			for (int i1 = 0; i1 < 4; i1++) {
				for (int i2 = 0; i2 < 7; i2++) {
					MainInv.setItem(10 + i1 * 9 + i2, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
			}
			MainInv.setItem(31, new AIS("§4Loading.. dont touch something.", 1, Material.PAPER).toIS());
			p.openInventory(MainInv);
			Itoomel.onItoomel.remove(p);
			Itoomel.onItoomel.put(p, new Object[] { menu, type });
			Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

				@Override
				public void run() {
					try {
						// - Back Button 49
						MainInv.setItem(49, new AIS("§7<-- Back", 1, Material.BARRIER).toIS());

						// - Getting all Markets to display
						MainInv.setItem(4, new ItemStack(type).clone());
						List<String> allMarket = getMarketsUUIDsInItoomelByType(type);
						allMarket.sort(new Comparator<String>() {

							@Override
							public int compare(String arg0, String arg1) {
								try {
									return APBuy.getMarketHandler().compareByUUID(arg0, arg1);
								} catch (MarketException e) {
								}
								return 0;
							}
						});
						int size = allMarket.size();
						int pages = ((size - (size % 28)) / 28);
						int count = 28 * page;
						Market m;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									m = new Market(allMarket.get(count), true);
									MainInv.setItem(10 + i1 * 9 + i2,
											APBuy.tagger.setNBTTag("Market", allMarket.get(count),
													m.getMarkeAIS().addLineToLore("")
															.addLineToLore("§7Items: " + m.getAmmountOfMaterial(type))
															.toIS()));
									count++;
								}
								if (count >= size) {
									break;
								}
							}
						}

						if ((pages > 0) && (pages != page) && (size - 28 * (page + 1) != 0)) {
							MainInv.setItem(53, APBuy.tagger.setNBTTag("ToPage", page + 1,
									new AIS("§7Next page --> " + (page + 1), 1, Material.PAPER).toIS()));
						}
						if (page > 0) {
							MainInv.setItem(45, APBuy.tagger.setNBTTag("ToPage", page - 1,
									new AIS("§7" + (page - 1) + " <-- Previous page", 1, Material.PAPER).toIS()));
						}

						if (MainInv.getItem(31).getType() == Material.PAPER) {
							if (MainInv.getItem(31).getItemMeta()
									.getDisplayName() == "§4Loading.. dont touch something.") {
								MainInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
							}
						}
						Itoomel.onItoomel.remove(p);
						p.openInventory(MainInv);
						Itoomel.onItoomel.put(p, new Object[] { menu, type });
						APBuy.getMarketHandler().PMLocPage.put(p, page);
					} catch (Exception e1) {
						e1.printStackTrace();
						System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
						System.out.println("Menu: " + menu);
						p.closeInventory();
						APBuy.getMarketHandler().removeFromAll(p);
						p.sendMessage("§cEin Fehler ist aufgetreten, bitte kontaktiere einen Dev.");
						p.sendMessage("§c Das hier dem Dev sagen : " + Utils.addToFix(e1));
					}
				}
			});
		} else if (menu.equalsIgnoreCase("CatSelect") && (type != null) && (type != Material.AIR)) {
			Itoomel.onItoomel.put(p, new Object[] { "CatSelect", type });
			Inventory MainInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");

			for (int i = 0; i < 54; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)) {
					continue;
				}
				MainInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			for (int i1 = 0; i1 < 4; i1++) {
				for (int i2 = 0; i2 < 7; i2++) {
					MainInv.setItem(10 + i1 * 9 + i2, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
			}
			MainInv.setItem(31, new AIS("§4Loading.. dont touch something.", 1, Material.PAPER).toIS());
			p.openInventory(MainInv);
			Itoomel.onItoomel.put(p, new Object[] { "CatSelect", type });
			Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {
				@Override
				public void run() {
					try {
						// - Back Button 49
						MainInv.setItem(49, new AIS("§7<-- Back", 1, Material.BARRIER).toIS());

						// - Getting all Markets to display
						MainInv.setItem(4, new ItemStack(type).clone());
						ItoomelCat icat = ICats.getICatByType(type);
						List<Material> mats = new ArrayList<>();
						mats.addAll(icat.getCatMats().keySet());
						Iterator<Material> iterator = mats.iterator();
						while (iterator.hasNext()) {
							if (!Itoomel.itoomelStandard.containsKey(iterator.next())) {
								iterator.remove();
							}
						}
						List<Short> ss = getListOfSubIDs(mats, icat);
						System.out.println(mats.size() + " " + ss.size());
						mats = setMatsMenu(mats, icat, ss);
						System.out.println(mats.size());
						int size = mats.size();
						int pages = ((size - (size % 28)) / 28);
						int count = 28 * page;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									if (ss.get(count) == -1) {
										MainInv.setItem(10 + i1 * 9 + i2, APBuy.tagger.setNBTTag("NoSubID", true,
												APBuy.tagger.setNBTTag("ToSelect", count,
														new AIS(new ItemStack(mats.get(count)).clone())
																.addLineToLore("")
																.addLineToLore(
																		"§7Items: " + getTotalItems(mats.get(count)))
																.addLineToLore("§7Verkaufte Items: "
																		+ getTotalSoldItems(mats.get(count)))
																.addLineToLore("§7Markets: "
																		+ getTotalMarkets(mats.get(count)))
																.setDamage(ss.get(count) == -1 ? 0 : ss.get(count))
																.toIS())));
									} else {
										MainInv.setItem(10 + i1 * 9 + i2, APBuy.tagger.setNBTTag("ToSelect", count,
												new AIS(new ItemStack(mats.get(count)).clone()).addLineToLore("")
														.addLineToLore("§7Items: " + getTotalItemsSubID(icat,
																mats.get(count), ss.get(count)))
														.addLineToLore("§7Verkaufte Items: " + getTotalSoldItemsSubID(
																icat, mats.get(count), ss.get(count)))
														.addLineToLore("§7Markets: " + getTotalMarketsSubID(icat,
																mats.get(count), ss.get(count)))
														.setDamage(ss.get(count)).toIS()));
									}
									count++;
								}
								if (count >= size) {
									break;
								}
							}
						}

						if ((pages > 0) && (pages != page) && (size - 28 * (page + 1) != 0)) {
							MainInv.setItem(53, APBuy.tagger.setNBTTag("ToPage", page + 1,
									new AIS("§7Next page --> " + (page + 1), 1, Material.PAPER).toIS()));
						}

						if (page > 0) {
							MainInv.setItem(45, APBuy.tagger.setNBTTag("ToPage", page - 1,
									new AIS("§7" + (page - 1) + " <-- Previous page", 1, Material.PAPER).toIS()));
						}

						if (MainInv.getItem(31).getType() == Material.PAPER) {
							MainInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						p.openInventory(MainInv);
						Itoomel.onItoomel.remove(p);
						Itoomel.onItoomel.put(p, new Object[] { "CatSelect", type });
						APBuy.getMarketHandler().PMLocPage.put(p, page);
					} catch (Exception e1) {
						e1.printStackTrace();
						System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
						System.out.println("Menu: " + menu);
						p.closeInventory();
						APBuy.getMarketHandler().removeFromAll(p);
						p.sendMessage("§cEin Fehler ist aufgetreten, bitte kontaktiere einen Dev.");
						p.sendMessage("§c Das hier dem Dev sagen : " + Utils.addToFix(e1));
					}
				}

				private List<Material> setMatsMenu(List<Material> mats, ItoomelCat icat, List<Short> ss2) {
					List<Material> ss = new ArrayList<>();
					for (int i2 = 0; i2 < mats.size(); i2++) {
						for (int i = 0; i < getSubIDsForMat(mats.get(i2), icat).size(); i++) {
							ss.add(mats.get(i2));
						}
					}
					return ss;
				}

				private List<Short> getListOfSubIDs(List<Material> mats, ItoomelCat icat) {
					List<Short> ss = new ArrayList<>();
					for (int i2 = 0; i2 < mats.size(); i2++) {
						for (Short s : icat.getCatMats().get(mats.get(i2))) {
							if (s != -1) {
								if (isSubIDInItoomel(mats.get(i2), s)) {
									ss.add(s);
								}
							} else {
								ss.add(s);
							}
						}
					}
					return ss;
				}
			});
		} else if (menu.contains("searchSubID:")) {
			if (!Itoomel.itoomelStandard.containsKey(type)) {
				p.sendMessage(
						"§cItoomel hat keinen Item gefunden das deinen Type entspricht. (" + type.toString() + ")");
				Itoomel.openItoomel("Main", p, type, 0);
				return;
			}
			Short s = Short.valueOf(menu.replaceFirst("searchSubID:", ""));
			if (!isSubIDInItoomel(type, s)) {
				p.sendMessage("§cItoomel hat keinen Item gefunden das deinem SubID entspricht (" + s + ")");
				Itoomel.openItoomel("CatSelect", p, type, 0);
				return;
			}
			Itoomel.onItoomel.put(p, new Object[] { menu, type });
			Inventory MainInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Itoomel");

			for (int i = 0; i < 54; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)) {
					continue;
				}
				MainInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			for (int i1 = 0; i1 < 4; i1++) {
				for (int i2 = 0; i2 < 7; i2++) {
					MainInv.setItem(10 + i1 * 9 + i2, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
				}
			}
			MainInv.setItem(31, new AIS("§4Loading.. dont touch something.", 1, Material.PAPER).toIS());
			p.openInventory(MainInv);
			Itoomel.onItoomel.put(p, new Object[] { menu, type });
			Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

				@Override
				public void run() {
					try {
						// - Back Button 49
						MainInv.setItem(49, new AIS("§7<-- Back", 1, Material.BARRIER).toIS());

						// - Getting all Markets to display
						MainInv.setItem(4, new AIS(new ItemStack(type).clone()).setDamage(s).toIS());
						List<String> allMarket = getMarketsUUIDsInItoomelByTypeSubID(type, s);
						allMarket.sort(new Comparator<String>() {

							@Override
							public int compare(String arg0, String arg1) {
								try {
									return APBuy.getMarketHandler().compareByUUID(arg0, arg1);
								} catch (MarketException e) {
								}
								return 0;
							}
						});

						int size = allMarket.size();
						int pages = ((size - (size % 28)) / 28);
						int count = 28 * page;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									MainInv.setItem(10 + i1 * 9 + i2, APBuy.tagger.setNBTTag("Market",
											allMarket.get(count),
											new AIS(new ItemStack(APBuy.database.getMarketInfos(allMarket.get(count))
													.getMarketAIS().toIS()).clone())
															.addLineToLore("")
															.addLineToLore("§7Items: "
																	+ (new Market(allMarket.get(count), false)
																			.getAmmountOfMaterialSubID(type, s)))
															.toIS()));
									count++;
								}
								if (count >= size) {
									break;
								}
							}
						}

						if ((pages > 0) && (pages != page) && (size - 28 * (page + 1) != 0)) {
							MainInv.setItem(53, APBuy.tagger.setNBTTag("ToPage", page + 1,
									new AIS("§7Next page --> " + (page + 1), 1, Material.PAPER).toIS()));
						}
						if (page > 0) {
							MainInv.setItem(45, APBuy.tagger.setNBTTag("ToPage", page - 1,
									new AIS("§7" + (page - 1) + " <-- Previous page", 1, Material.PAPER).toIS()));
						}

						if (MainInv.getItem(31).getType() == Material.PAPER) {
							MainInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						p.openInventory(MainInv);
						Itoomel.onItoomel.put(p, new Object[] { menu, type });
						APBuy.getMarketHandler().PMLocPage.put(p, page);
					} catch (Exception e1) {
						e1.printStackTrace();
						System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
						System.out.println("Menu: " + menu);
						p.closeInventory();
						APBuy.getMarketHandler().removeFromAll(p);
						p.sendMessage("§cEin Fehler ist aufgetreten, bitte kontaktiere einen Dev.");
						p.sendMessage("§c Das hier dem Dev sagen : " + Utils.addToFix(e1));
					}
				}
			});
		}
	}

	public static List<Short> getSubIDsForMat(Material material, ItoomelCat icat) {
		List<Short> ss = new ArrayList<>();
		for (Short s : icat.getCatMats().get(material)) {
			if (s != -1) {
				if (isSubIDInItoomel(material, s)) {
					ss.add(s);
				}
			} else {
				ss.add(s);
			}
		}
		return ss;
	}

	public static boolean isSubIDInItoomel(Material material, Short s) {
		for (MarketItem mis : itoomelStandard.get(material)) {
			if (mis.getIs().getDurability() == s) {
				return true;
			}
		}
		return false;
	}

	public static long getTotalSoldItems(ItoomelCat itoomelCat) {
		return getTotalSoldItemsList(Arrays.asList(itoomelCat.getCatMatsRest().keySet().toArray(new Material[0])));
	}

	public static long getTotalMarkets(ItoomelCat itoomelCat) {
		return getTotalMarketsList(Arrays.asList(itoomelCat.getCatMatsRest().keySet().toArray(new Material[0])));
	}

	public static long getTotalItems(ItoomelCat itoomelCat) {
		return getTotalItemsList(Arrays.asList(itoomelCat.getCatMatsRest().keySet().toArray(new Material[0])));
	}

	public static long getTotalSoldItemsSubID(ItoomelCat itoomelCat, Material mat, short s) {
		long leng = 0;
		if (itoomelStandard.containsKey(mat)) {
			for (MarketItem mis : itoomelStandard.get(mat)) {
				if (mis.getIs().getDurability() == s) {
					leng += mis.getSoldItems();
				}
			}
		}
		return leng;
	}

	public static long getTotalMarketsSubID(ItoomelCat itoomelCat, Material mat, short s) {
		List<String> l = new ArrayList<>();
		for (String ss : getMarketsUUIDsInItoomelByTypeSubID(mat, s)) {
			if (!l.contains(ss)) {
				l.add(ss);
			}
		}
		return l.size();
	}

	public static long getTotalItemsSubID(ItoomelCat itoomelCat, Material mat, short s) {
		long leng = 0;
		if (itoomelStandard.containsKey(mat)) {
			for (MarketItem mis : itoomelStandard.get(mat)) {
				if (mis.getIs().getDurability() == s) {
					leng += mis.getAmmount();
				}
			}
		}
		return leng;
	}

	public static void reopenItoomelToEveryone() {
		for (Player p : Itoomel.onItoomel.keySet()) {
			if (BuyManager.isBuying(p)) {
				continue;
			}
			int i = APBuy.getMarketHandler().PMLocPage.get(p);
			APBuy.getMarketHandler().PMLocPage.remove(p);
			if (String.valueOf(Itoomel.onItoomel.get(p)[0]).contains("Market:")) {
				if (APBuy.getMarketHandler()
						.hasMarketByUUID(String.valueOf(Itoomel.onItoomel.get(p)[0]).replaceFirst("Market:", ""))) {
					APBuy.getMarketHandler().PMLocPage.put(p, 0);
					Itoomel.openItoomel(String.valueOf(Itoomel.onItoomel.get(p)[0]), p,
							(Material) Itoomel.onItoomel.get(p)[1], 0);
				} else {
					p.sendMessage("§cSorry, es gab einfach ein reload... :/");
					Itoomel.openItoomel("Main", p, Material.AIR, 0);
				}
			} else {
				if (itoomelStandard.containsKey(Itoomel.onItoomel.get(p)[1])) {
					Itoomel.openItoomel(String.valueOf(Itoomel.onItoomel.get(p)[0]), p,
							(Material) Itoomel.onItoomel.get(p)[1], i);
				} else {
					APBuy.getMarketHandler().PMLocPage.put(p, 0);
					Itoomel.openItoomel("Main", p, Material.AIR, 0);
					p.sendMessage("§cDieses Material ist nicht mehr verfügbar.");
				}
			}
		}
	}

	public static void addMarketToItoomel(Market m) {
		if (!isMarketInItoomel(m.getMarketOwner())) {
			for (MarketItem mis : m.getMarketItems()) {
				if (mis.isBuyable()) {
					addMISToItoomel(mis);
				}
			}

		}
	}

	public static void removeMarketFromItoomel(UUID uuid) {
		if (isMarketInItoomel(uuid.toString())) {
			List<MarketItem> l = new ArrayList<>();
			List<Material> toRemove = new ArrayList<>();
			for (Material mat : itoomelStandard.keySet()) {
				l.addAll(itoomelStandard.get(mat));
				Iterator<MarketItem> iterator = l.iterator();
				while (iterator.hasNext()) {
					if (iterator.next().getMarketuuid().equals(uuid.toString())) {
						iterator.remove();
					}
				}
				if (l.isEmpty()) {
					toRemove.add(mat);
				} else {
					itoomelStandard.replace(mat, l);
				}
				l.clear();
			}
			for (Material mat : toRemove) {
				itoomelStandard.remove(mat);
			}
		}
	}

	public static void addMISToItoomel(MarketItem mis) {
		List<MarketItem> l = new ArrayList<>();
		if (itoomelStandard.containsKey(mis.getIs().getType())) {
			l.addAll(itoomelStandard.get(mis.getIs().getType()));
		}
		l.add(mis);
		itoomelStandard.remove(mis.getIs().getType());
		itoomelStandard.put(mis.getIs().getType(), l);
	}

	public static void addMISsToItoomel(List<MarketItem> miss) {
		List<Material> mats = getMatsFromMISs(miss);
		List<MarketItem> miss2 = new ArrayList<>();
		List<MarketItem> miss3 = new ArrayList<>();
		for (Material mat : mats) {
			miss2 = getMISsWhoHaveMat(miss, mat);
			if (itoomelStandard.containsKey(mat)) {
				miss3 = itoomelStandard.get(mat);
				miss3.addAll(miss2);
				itoomelStandard.replace(mat, miss3);
			} else {
				itoomelStandard.put(mat, miss2);
			}
		}
	}

	public static List<MarketItem> getMISsWhoHaveMat(List<MarketItem> miss, Material mat) {
		List<MarketItem> l = new ArrayList<>();
		l.addAll(miss);
		Iterator<MarketItem> iterator = l.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getIs().getType() != mat) {
				iterator.remove();
			}
		}
		return l;
	}

	public static List<Material> getMatsFromMISs(List<MarketItem> miss) {
		List<Material> l = new ArrayList<>();
		for (MarketItem mis : miss) {
			if (!l.contains(mis.getIs().getType())) {
				l.add(mis.getIs().getType());
			}
		}
		return l;
	}

	public static void removeMISFromItoomel(ItemStack is, String uuid) {
		List<MarketItem> l = new ArrayList<>();
		if (itoomelStandard.containsKey(is.getType())) {
			l.addAll(itoomelStandard.get(is.getType()));
			Iterator<MarketItem> iterator = l.iterator();
			MarketItem mis = null;
			while (iterator.hasNext()) {
				mis = iterator.next();
				if (mis.getIs().equals(is)) {
					if (mis.getMarketuuid().equals(uuid)) {
						iterator.remove();
					}
				}
			}
			if (l.isEmpty()) {
				itoomelStandard.remove(is.getType());
			} else {
				itoomelStandard.replace(is.getType(), l);
			}

		}
	}

	public static void replaceMISInItoomel(MarketItem mis) {
		List<MarketItem> l = new ArrayList<>();
		if (itoomelStandard.containsKey(mis.getIs().getType())) {
			l.addAll(itoomelStandard.get(mis.getIs().getType()));
			for (int i = 0; i < l.size(); i++) {
				if (l.get(i).getIs().isSimilar(mis.getIs())) {
					if (l.get(i).getMarketuuid().equals(mis.getMarketuuid())) {
						l.set(i, mis);
					}
				}
			}
			itoomelStandard.replace(mis.getIs().getType(), l);
		} else {
			addMISToItoomel(mis);
		}
	}

	public static boolean isMarketInItoomel(String uuid) {
		for (Material mat : itoomelStandard.keySet()) {
			if (getMarketsUUIDsInItoomelByType(mat).contains(uuid)) {
				return true;
			}
		}
		return false;
	}

	public static List<String> getMarketsUUIDsInItoomelByType(Material mat) {
		List<String> l = new ArrayList<>();
		if (itoomelStandard.containsKey(mat)) {
			for (MarketItem mis : itoomelStandard.get(mat)) {
				if (!l.contains(mis.getMarketuuid())) {
					l.add(mis.getMarketuuid());
				}
			}
		}
		return l;
	}

	public static List<String> getMarketsUUIDsInItoomelByTypeSubID(Material mat, short s) {
		List<String> l = new ArrayList<>();
		if (itoomelStandard.containsKey(mat)) {
			for (MarketItem mis : itoomelStandard.get(mat)) {
				System.out.println(mis.getIs().getDurability() + " " + s + " " + mat.toString());
				if ((mis.getIs().getDurability() == s) || (s == -1)) {
					if (!l.contains(mis.getMarketuuid())) {
						l.add(mis.getMarketuuid());
					}
				}
			}
		}
		return l;
	}

	public static int getTotalMarkets(Material material) {
		return getMarketsUUIDsInItoomelByType(material).size();
	}

	public static long getTotalSoldItems(Material mat) {
		long leng = 0;
		if (itoomelStandard.containsKey(mat)) {
			for (MarketItem mis : itoomelStandard.get(mat)) {
				leng += mis.getSoldItems();
			}
		}
		return leng;
	}

	public static long getTotalItems(Material mat) {
		if (itoomelStandard.containsKey(mat)) {
			long leng = 0;
			for (MarketItem mis : itoomelStandard.get(mat)) {
				leng += mis.getAmmount();
			}
			return leng;
		} else {
			return 0;
		}
	}

	public static int getTotalMarketsList(List<Material> mats) {
		List<String> l = new ArrayList<>();
		for (Material mat : mats) {
			for (String s : getMarketsUUIDsInItoomelByType(mat)) {
				if (!l.contains(s)) {
					l.add(s);
				}
			}
		}
		return l.size();
	}

	public static long getTotalSoldItemsList(List<Material> mats) {
		long leng = 0;
		for (Material mat : mats) {
			if (itoomelStandard.containsKey(mat)) {
				for (MarketItem mis : itoomelStandard.get(mat)) {
					leng += mis.getSoldItems();
				}
			}
		}
		return leng;
	}

	public static long getTotalItemsList(List<Material> mats) {
		long leng = 0;
		for (Material mat : mats) {
			if (itoomelStandard.containsKey(mat)) {
				for (MarketItem mis : itoomelStandard.get(mat)) {
					leng += mis.getAmmount();
				}
			}
		}
		return leng;
	}
}
