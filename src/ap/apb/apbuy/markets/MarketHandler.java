package ap.apb.apbuy.markets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ap.apb.AIS;
import ap.apb.APBuy;
import ap.apb.Translator;
import ap.apb.Utils;
import ap.apb.anvilgui.AnvilGUI;
import ap.apb.anvilgui.AnvilGUI.AnvilClickEvent;
import ap.apb.anvilgui.AnvilGUI.AnvilClickEventHandler;
import ap.apb.anvilgui.AnvilGUI.AnvilSlot;
import ap.apb.anvilgui.mc1_8.AnvilGUI_v1_8_R3;
import ap.apb.apbuy.BuyManager;
import ap.apb.apbuy.itoomel.Itoomel;
import ap.apb.datamaster.YAMLDatabase;
import ap.apb.menu.Menu;
import ap.apb.menu.menus.MainMenu;
import ap.apb.menu.menus.main.CmdsMenu;
import ap.apb.menu.menus.main.mymarket.MyMarketMainMenu;

public class MarketHandler implements Listener {
	public List<Menu> menus = new ArrayList<>();
	private HashMap<Player, CategoryInfos> creatingCat = new HashMap<>();
	private HashMap<Player, MarketItem> creatingIS = new HashMap<>();
	public HashMap<Player, String[]> onMarketVisualiser = new HashMap<>();
	public HashMap<Player, List<ItemStack>> onItemInput = new HashMap<>();
	public static Market adminshop = null;
	public boolean settedAdminShop = false;

	public void openInvToP(String menu, Player p) {
		try {
			if (menu == "MainMenu") {
				Menu menu1 = new MainMenu(p);
				menu1.openInv();
				menus.add(menu1);
				return;
			}
			if (menu.equalsIgnoreCase("cmds")) {
				Menu menu1 = new CmdsMenu(p);
				menu1.openInv();
				menus.add(menu1);
				return;
			}
			switch (menu.split(Pattern.quote(":"))[0]) {
			case "MyMarket":
				switch (menu.split(Pattern.quote(":"))[1]) {
				case "Main":
					Inventory inv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - My Market");
					for (int i = 0; i < 54; i++) {
						inv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
					}
					MarketInfos m = APBuy.database.getMarketInfos(p.getUniqueId().toString());
					// - Status 10
					inv.setItem(10,
							m.isOpen()
									? new AIS(Translator.translate("menu.inv.mymarket.main.status.on"), 1, (short) 10,
											Material.INK_SACK).toIS()
									: new AIS(Translator.translate("menu.inv.mymarket.main.status.off"), 1, (short) 1,
											Material.INK_SACK).toIS());
					// - Soled Items 14

					inv.setItem(14,
							new AIS(Translator.translate("menu.inv.mymarket.main.stats.topic"), 1, Material.PAPER)
									.addLineToLore("")
									.addLineToLore(Translator.translate("menu.inv.mymarket.main.stats.solditems") + ": "
											+ m.getSoldItems())
									.addLineToLore(Translator.translate("menu.inv.mymarket.main.stats.solds") + ": "
											+ m.getSales())
									.toIS());

					// - Name/Devise 16
					ItemStack nameNdevise = new ItemStack(Material.PAPER, 1);
					ItemMeta nNdmeta = nameNdevise.getItemMeta();
					nNdmeta.setDisplayName(Translator.translate("menu.inv.mymarket.main.nnd.title"));
					List<String> nNdlist = new ArrayList<>();
					nNdlist.add(Translator.translate("menu.inv.mymarket.main.nnd.name") + ": §6" + (m.getName() == null
							? Translator.translate("menu.inv.mymarket.main.nnd.notset") : m.getName()));
					nNdlist.add(
							Translator.translate("menu.inv.mymarket.main.nnd.devise") + ": §6" + (m.getDevise() == null
									? Translator.translate("menu.inv.mymarket.main.nnd.notset") : m.getDevise()));
					nNdlist.add("");
					nNdlist.add(Translator.translate("menu.inv.mymarket.main.nnd.howto") + ":");
					nNdlist.add("§8   /mr setName <Neues Market Name>");
					nNdlist.add("§8   /mr setDevise <Neues Market Devise>");
					nNdlist.add("§8   /mr resetName");
					nNdlist.add("§8   /mr resetDevise");
					nNdmeta.setLore(nNdlist);
					nameNdevise.setItemMeta(nNdmeta);
					inv.setItem(16, nameNdevise);

					// - My Market Editor 12
					List<String> mmislist = new ArrayList<>();
					mmislist.add("");
					mmislist.add(Translator.translate("menu.inv.mymarket.main.mymarket"));
					inv.setItem(12, new AIS(Material.CHEST).addToLore(mmislist).toIS());

					// - Back Button 49
					inv.setItem(49, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());

					// - Item Input 30
					inv.setItem(31,
							new AIS(Translator.translate("menu.inv.mymarket.main.iteminput.title"), 1, Material.HOPPER)
									.addLineToLore("").addToLore(Utils.createListFromStringToWidth(
											Translator.translate("menu.inv.mymarket.main.iteminput.desc"), 40))
									.toIS());

					p.openInventory(inv);
					PMLoc.put(p, "MyMarket:Main");
					break;
				case "Editor":
					Inventory inv1 = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Editor");
					if (menu.split(Pattern.quote(":")).length == 3) {
						if (menu.split(Pattern.quote(":"))[2].contains("Add")) {
							for (int i = 0; i < 54; i++) {
								inv1.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
							}
							boolean hasItem = false;
							for (ItemStack stack : p.getInventory().getContents()) {
								if (stack != null) {
									hasItem = true;
									break;
								}
							}
							if (!hasItem) {
								p.sendMessage(Translator.translate("menu.inv.mymarket.editoradd.noitem"));
								this.openInvToP("MyMarket:Editor:0", p);
								return;
							}

							// - Back Button 49
							inv1.setItem(48, new AIS(Translator.translate("menu.inv.mymarket.editoradd.cancel"), 1,
									(short) 14, Material.WOOL).toIS());

							// - Finish Button 49
							inv1.setItem(50, new AIS(Translator.translate("menu.inv.mymarket.editoradd.finish"), 1,
									(short) 5, Material.WOOL).toIS());

							// - Cat Prewiev 31
							inv1.setItem(31, creatingCat.get(p).getAIS().toIS());

							// - Cat Rename 10
							inv1.setItem(10, new AIS(Translator.translate("menu.inv.mymarket.editoradd.change.name"), 1,
									Material.NAME_TAG).toIS());

							// - Cat Relore 12
							inv1.setItem(12, new AIS(Translator.translate("menu.inv.mymarket.editoradd.change.desc"), 1,
									Material.PAPER).toIS());

							// - Cat Set Item 14
							inv1.setItem(14, new AIS(creatingCat.get(p).getMat())
									.setDamage(creatingCat.get(p).getSubid()).setLore(null).addLineToLore("")
									.addLineToLore(Translator.translate("menu.inv.mymarket.editoradd.setitem.desc"))
									.setName(Translator.translate("menu.inv.mymarket.editoradd.setitem.title")).toIS());

							PMLoc.put(p, "MyMarket:Editor:Add:Opened");
							p.openInventory(inv1);
							PMLoc.put(p, "MyMarket:Editor:Add");
						} else {
							for (int i = 0; i < 54; i++) {
								if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34)
										|| (37 <= i && i <= 43) || (i == 49)) {
									continue;
								}
								inv1.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
							}
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									inv1.setItem(10 + i1 * 9 + i2,
											new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
								}
							}
							inv1.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
									.addLineToLore(Translator.translate("menu.openerror2")).toIS());
							PMLoc.put(p, "Markets:Opened");
							p.openInventory(inv1);
							Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

								@Override
								public void run() {
									try {
										// - Back Button 49
										inv1.setItem(49,
												new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());

										// - Add new Category 50
										inv1.setItem(50,
												new AIS(Translator.translate("menu.inv.mymarket.editor.newcat"), 1,
														Material.CHEST).toIS());

										// - Remove Category 48
										inv1.setItem(48,
												new AIS(Translator.translate("menu.inv.mymarket.editor.delete.title"),
														1, Material.CHEST)
																.addToLore(Utils.createListFromStringToWidth(
																		Translator.translate(
																				"menu.inv.mymarket.editor.delete.desc"),
																		40))
																.toIS());

										// - Getting all Markets to display
										List<CategoryInfos> catinfoss = APBuy.database
												.getAllCategoryInfosFromMarket(p.getUniqueId().toString());
										int size = catinfoss.size();
										int pages = ((size - (size % 28)) / 28);
										int count = 28 * PMLocPage.get(p);
										for (int i1 = 0; i1 < 4; i1++) {
											for (int i2 = 0; i2 < 7; i2++) {
												if (count >= size) {
													break;
												}

												inv1.setItem(10 + i1 * 9 + i2,
														APBuy.tagger.setNBTTag("Cat",
																String.valueOf(catinfoss.get(count).getName()),
																new AIS(catinfoss.get(count).getAIS().toIS())
																		.setDamage(catinfoss.get(count).getSubid())
																		.addLineToLore("")
																		.addLineToLore(Translator.translate(
																				"menu.inv.mymarket.editor.catdesc"))
																		.toIS()));
												count++;
											}
											if (count >= size) {
												break;
											}
										}

										if ((pages > 0) && (pages != PMLocPage.get(p))
												&& (size - 28 * (PMLocPage.get(p) + 1) != 0)) {
											inv1.setItem(53,
													APBuy.tagger.setNBTTag("ToPage", PMLocPage.get(p) + 1,
															new AIS("§7" + Translator.translate("menu.page.next") + " "
																	+ (PMLocPage.get(p) + 1), 1, Material.PAPER)
																			.toIS()));
										}
										if (PMLocPage.get(p) > 0) {
											inv1.setItem(45,
													APBuy.tagger
															.setNBTTag("ToPage",
																	PMLocPage.get(p) - 1, new AIS(
																			"§7" + (PMLocPage.get(p) - 1) + " "
																					+ Translator.translate(
																							"menu.page.previous"),
																			1, Material.PAPER).toIS()));
										}

										if (inv1.getItem(31).getType() == Material.PAPER) {
											inv1.setItem(31,
													new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
										}
										PMLoc.put(p, "MyMarket:Editor:Opened");
										p.openInventory(inv1);
										PMLoc.put(p, "MyMarket:Editor");
									} catch (Exception e) {
										e.printStackTrace();
										System.out.println("Menu: " + menu);
										System.out.println(
												"Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
										p.closeInventory();
										APBuy.getMarketHandler().removeFromAll(p);
										p.sendMessage(Translator.translate("dev.error"));
										p.sendMessage("§cError Code: " + Utils.addToFix(e));
									}
								}
							});
						}
					} else if (menu.split(Pattern.quote(":")).length == 2) {

						for (int i = 0; i < 54; i++) {
							if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34)
									|| (37 <= i && i <= 43) || (i == 49)) {
								continue;
							}
							inv1.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
						}
						for (int i1 = 0; i1 < 4; i1++) {
							for (int i2 = 0; i2 < 7; i2++) {
								inv1.setItem(10 + i1 * 9 + i2,
										new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
							}
						}
						inv1.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
								.addLineToLore(Translator.translate("menu.openerror2")).toIS());
						PMLoc.put(p, "Markets:Opened");
						p.openInventory(inv1);
						Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

							@Override
							public void run() {
								try {
									// - Back Button 49
									inv1.setItem(49,
											new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());

									// - Add new Category 50
									inv1.setItem(50, new AIS(Translator.translate("menu.inv.mymarket.editor.newcat"), 1,
											Material.CHEST).toIS());

									// - Remove Category 48
									inv1.setItem(48,
											new AIS(Translator.translate("menu.inv.mymarket.editor.delete.title"), 1,
													Material.CHEST)
															.addLineToLore("")
															.addToLore(Utils.createListFromStringToWidth(
																	Translator.translate(
																			"menu.inv.mymarket.editor.delete.desc"),
																	40))
															.toIS());
									List<CategoryInfos> catinfoss = APBuy.database
											.getAllCategoryInfosFromMarket(p.getUniqueId().toString());
									// - Getting all Markets to display
									int size = catinfoss.size();
									int pages = ((size - (size % 28)) / 28);
									int count = 28 * PMLocPage.get(p);
									for (int i1 = 0; i1 < 4; i1++) {
										for (int i2 = 0; i2 < 7; i2++) {
											if (count >= size) {
												break;
											}

											inv1.setItem(10 + i1 * 9 + i2,
													APBuy.tagger.setNBTTag("Cat",
															String.valueOf(catinfoss.get(count).getName()),
															new AIS(catinfoss.get(count).getMat())
																	.setDamage(catinfoss.get(count).getSubid())
																	.addLineToLore("")
																	.addLineToLore(Translator.translate(
																			"menu.inv.mymarket.editor.catdesc"))
																	.toIS()));
											count++;
										}
										if (count >= size) {
											break;
										}
									}

									if ((pages > 0) && (pages != PMLocPage.get(p))
											&& (size - 28 * (PMLocPage.get(p) + 1) != 0)) {
										inv1.setItem(53,
												APBuy.tagger
														.setNBTTag("ToPage", PMLocPage.get(p) + 1,
																new AIS("§7" + Translator.translate("menu.page.next")
																		+ " " + (PMLocPage.get(p) + 1), 1,
																		Material.PAPER).toIS()));
									}
									if (PMLocPage.get(p) > 0) {
										inv1.setItem(45,
												APBuy.tagger
														.setNBTTag("ToPage",
																PMLocPage.get(p) - 1, new AIS(
																		"§7" + (PMLocPage.get(p) - 1) + " "
																				+ Translator.translate(
																						"menu.page.previous"),
																		1, Material.PAPER).toIS()));
									}
									if (inv1.getItem(31).getType() == Material.PAPER) {
										inv1.setItem(31,
												new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
									}
									PMLoc.put(p, "MyMarket:Editor:Opened");
									p.openInventory(inv1);
									PMLoc.put(p, "MyMarket:Editor");
								} catch (Exception e) {
									e.printStackTrace();
									System.out.println("Menu: " + menu);
									System.out.println(
											"Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
									p.closeInventory();
									APBuy.getMarketHandler().removeFromAll(p);
									p.sendMessage(Translator.translate("dev.error"));
									p.sendMessage("§cDas hier dem Dev sagen : " + Utils.addToFix(e));
								}
							}
						});
					}
					break;
				case "ItemInput":
					List<ItemStack> iss = onItemInput.get(p);
					onItemInput.remove(p);
					Inventory inv2 = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Editor");
					for (int i = 0; i < 54; i++) {
						if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
								|| (i == 49)) {
							continue;
						}
						inv2.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
					}

					// - Back Button 49
					inv2.setItem(49, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());

					// - Info Button 48
					inv2.setItem(48,
							new AIS(Translator.translate("menu.inv.mymarket.iteminput.info"), 1, Material.PAPER)
									.addLineToLore("").addToLore(Utils.createListFromStringToWidth(
											Translator.translate("menu.inv.mymarket.iteminput.infodesc"), 40))
									.toIS());

					// - Sort Button 50
					inv2.setItem(50,
							new AIS(Translator.translate("menu.inv.mymarket.iteminput.sort"), 1, Material.HOPPER)
									.toIS());

					int size = iss.size();
					int count = 0;
					for (int i1 = 0; i1 < 4; i1++) {
						for (int i2 = 0; i2 < 7; i2++) {
							if (count >= size) {
								break;
							}

							inv2.setItem(10 + i1 * 9 + i2, iss.get(count));
							count++;
						}
						if (count >= size) {
							break;
						}
					}

					PMLoc.put(p, "MyMarket:ItemInput:Opened");
					p.openInventory(inv2);
					onItemInput.put(p, iss);
					PMLoc.put(p, "MyMarket:ItemInput");
					break;
				}
				break;
			case "Markets":
				Inventory inv2 = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Top Markets");
				for (int i = 0; i < 54; i++) {
					if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
							|| (i == 49)) {
						continue;
					}
					inv2.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				for (int i1 = 0; i1 < 4; i1++) {
					for (int i2 = 0; i2 < 7; i2++) {
						inv2.setItem(10 + i1 * 9 + i2, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
					}
				}
				inv2.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
						.addLineToLore(Translator.translate("menu.openerror2")).toIS());
				PMLoc.put(p, "Markets:Opened");
				p.openInventory(inv2);
				PMLoc.put(p, "Markets");
				Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

					@Override
					public void run() {
						try {
							// - Back Button 49
							inv2.setItem(49, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());
							if (APBuy.database instanceof YAMLDatabase) {
								if ((YamlConfiguration.loadConfiguration(APBuy.plugin.getPlayerMarketStats())
										.getStringList("TopMarkets") == null)
										|| (YamlConfiguration.loadConfiguration(APBuy.plugin.getPlayerMarketStats())
												.getBoolean("MusstBeUpdated"))) {
									updateLists();
								}
							}
							List<UUID> uuids = getTopMarkets();
							// - Getting all Markets to display
							int size = uuids.size();
							int pages = ((size - (size % 28)) / 28);
							int count = 28 * PMLocPage.get(p);
							if (size != 0) {
								String s;
								for (int i1 = 0; i1 < 4; i1++) {
									for (int i2 = 0; i2 < 7; i2++) {
										if (count >= size) {
											break;
										}
										s = uuids.get(count).toString() + ".yml";
										inv2.setItem(10 + i1 * 9 + i2,
												APBuy.tagger.setNBTTag("Market", s,
														APBuy.database
																.getMarketInfos(s.replaceAll(Pattern.quote(".yml"), ""))
																.getMarketAIS().toIS()));
										count++;
									}
									if (count >= size) {
										break;
									}
								}
							}

							if ((pages > 0) && (pages != PMLocPage.get(p))
									&& (size - 28 * (PMLocPage.get(p) + 1) != 0)) {
								inv2.setItem(53, APBuy.tagger.setNBTTag("ToPage", PMLocPage.get(p) + 1, new AIS(
										"§7" + Translator.translate("menu.page.next") + " " + (PMLocPage.get(p) + 1), 1,
										Material.PAPER).toIS()));
							}
							if (PMLocPage.get(p) > 0) {
								inv2.setItem(45,
										APBuy.tagger
												.setNBTTag("ToPage",
														PMLocPage.get(p)
																- 1,
														new AIS("§7" + (PMLocPage.get(p) - 1) + " "
																+ Translator.translate("menu.page.previous"), 1,
																Material.PAPER).toIS()));
							}

							if (inv2.getItem(31).getType() == Material.PAPER) {
								inv2.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
							}
							PMLoc.put(p, "Markets:Opened");
							p.openInventory(inv2);
							PMLoc.put(p, "Markets");
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("Menu: " + menu);
							System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
							p.closeInventory();
							APBuy.getMarketHandler().removeFromAll(p);
							p.sendMessage(Translator.translate("dev.error"));
							p.sendMessage("§cError Code: " + Utils.addToFix(e));
						}
					}

				});
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Menu: " + menu);
			System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
			p.closeInventory();
			this.removeFromAll(p);
			p.sendMessage(Translator.translate("dev.error"));
			p.sendMessage("§cError Code: " + Utils.addToFix(e));
		}
	}

	public List<UUID> getTopMarkets() throws MarketException {
		List<UUID> uuids = APBuy.database.loadAllOnlineMarkets();
		uuids.sort(new Comparator<UUID>() {
			@Override
			public int compare(UUID o1, UUID o2) {
				try {
					return compareByUUID(o1.toString(), o2.toString());
				} catch (MarketException e) {
					return 0;
				}
			}
		});
		return uuids;
	}

	public void removeFromAll(Player p) {
		if (PMLoc.containsKey(p) || creatingIS.containsKey(p) || creatingCat.containsKey(p)
				|| onMarketVisualiser.containsKey(p) || onItemInput.containsKey(p) || Itoomel.onItoomel.containsKey(p)
				|| BuyManager.isBuying(p)) {
			PMLoc.remove(p);
			PMLocPage.remove(p);
			creatingCat.remove(p);
			creatingIS.remove(p);
			onMarketVisualiser.remove(p);
			onItemInput.remove(p);
			Itoomel.onItoomel.remove(p);
			BuyManager.removeBuyer(p);
			p.closeInventory();
		}
	}

	public void openItemEditor(String Cat, String menu, Player p) {
		try {
			switch (menu) {
			case "Main":
				Inventory MainInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - My Market");
				for (int i = 0; i < 54; i++) {
					if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
							|| (i == 49)) {
						continue;
					}
					MainInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				for (int i1 = 0; i1 < 4; i1++) {
					for (int i2 = 0; i2 < 7; i2++) {
						MainInv.setItem(10 + i1 * 9 + i2,
								new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
					}
				}
				MainInv.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
						.addLineToLore(Translator.translate("menu.openerror2")).toIS());
				PMLoc.put(p, "MyMarket:ItemEditor:Main:" + Cat + ":Opened");
				p.openInventory(MainInv);
				PMLoc.put(p, "MyMarket:ItemEditor:Main:" + Cat);
				Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

					@Override
					public void run() {
						// - Back Button 49
						MainInv.setItem(49, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());

						// - new Item Button 50
						MainInv.setItem(50,
								new AIS(Translator.translate("menu.inv.iteminput.additem"), 1, Material.CHEST).toIS());

						// - Infos 48
						MainInv.setItem(48,
								new AIS(Translator.translate("menu.inv.iteminput.info.title"), 1, Material.PAPER)
										.addLineToLore("")
										.addLineToLore(Translator.translate("menu.inv.iteminput.info.middle"))
										.addLineToLore(Translator.translate("menu.inv.iteminput.info.right"))
										.addLineToLore(Translator.translate("menu.inv.iteminput.info.drop")).toIS());
						// - Getting all Markets to display
						List<MarketItem> miss = APBuy.database.getMarketItemsFromMarket(p.getUniqueId().toString());
						Iterator<MarketItem> iterator = miss.iterator();
						while (iterator.hasNext()) {
							String s = iterator.next().getCatName();
							if (!s.equals(Cat)) {
								iterator.remove();
							}
						}
						int size = miss.size();
						int pages = ((size - (size % 28)) / 28);
						int count = 28 * PMLocPage.get(p);
						long am = 0;
						if (size != 0) {
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									if (count >= size) {
										break;
									}
									am = miss.get(count).getAmmount();
									MainInv.setItem(10 + i1 * 9 + i2,
											APBuy.tagger.setNBTTag("Item", true, miss.get(count).getAISToShow()
													.addLineToLore(Translator.translate("menu.inv.iteminput.maxsell")
															+ ": "
															+ (am == 0
																	? Translator.translate("menu.inv.iteminput.nomore")
																	: ((am - (am % miss.get(count).getSellAmmount()))
																			/ miss.get(count).getSellAmmount())))
													.toIS()));
									count++;
								}
								if (count >= size) {
									break;
								}
							}
						}

						if ((pages > 0) && (pages != PMLocPage.get(p)) && (size - 28 * (PMLocPage.get(p) + 1) != 0)) {
							MainInv.setItem(53, APBuy.tagger.setNBTTag("ToPage", PMLocPage.get(p) + 1, new AIS(
									"§7" + Translator.translate("menu.page.next") + " " + (PMLocPage.get(p) + 1), 1,
									Material.PAPER).toIS()));
						}
						if (PMLocPage.get(p) > 0) {
							MainInv.setItem(45,
									APBuy.tagger
											.setNBTTag("ToPage",
													PMLocPage.get(p)
															- 1,
													new AIS("§7" + (PMLocPage.get(p) - 1) + " "
															+ Translator.translate("menu.page.previous"), 1,
															Material.PAPER).toIS()));
						}

						if (MainInv.getItem(31).getType() == Material.PAPER) {
							MainInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						PMLoc.put(p, "MyMarket:ItemEditor:Main:" + Cat + ":Opened");
						p.openInventory(MainInv);
						PMLoc.put(p, "MyMarket:ItemEditor:Main:" + Cat);
					}
				});
				break;
			case "Add":
				Inventory AddInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - My Market");
				for (int i = 0; i < 54; i++) {
					AddInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				AddInv.setItem(40,
						creatingIS.get(p).getIs() == null
								? new AIS(Translator.translate("menu.inv.items.noitem.title"), 1, Material.CHEST)
										.addLineToLore("")
										.addLineToLore(Translator.translate("menu.inv.items.noitem.desc")).toIS()
								: creatingIS.get(p).getIs());
				AddInv.setItem(10, new AIS("§c-1000$", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(11, new AIS("§c-100$", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(12, new AIS("§e-10$", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(13,
						new AIS(Translator.translate("menu.inv.items.price") + ": " + creatingIS.get(p).getPrice(), 1,
								(creatingIS.get(p).getIs() == null ? Material.CHEST
										: creatingIS.get(p).getIs().getType()))
												.setDamage(creatingIS.get(p).getIs() == null ? 0
														: creatingIS.get(p).getIs().getDurability())
												.toIS());
				AddInv.setItem(14, new AIS("§a+10$", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(15, new AIS("§2+100$", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(16, new AIS("§2+1000$", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(19, new AIS("§c-64", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(20, new AIS("§c-16", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(21, new AIS("§e-1", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(22, new AIS(
						Translator.translate("menu.inv.items.ppersell") + ": " + creatingIS.get(p).getSellAmmount(), 1,
						(creatingIS.get(p).getIs() == null ? Material.CHEST : creatingIS.get(p).getIs().getType()))
								.setDamage(creatingIS.get(p).getIs() == null ? 0
										: creatingIS.get(p).getIs().getDurability())
								.toIS());
				AddInv.setItem(23, new AIS("§a+1", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(24, new AIS("§2+16", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(25, new AIS("§2+64", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(29,
						new AIS(Translator.translate("menu.inv.items.info.title"), 1, (short) 2, Material.INK_SACK)
								.addLineToLore("").addToLore(Utils.createListFromStringToWidth(
										Translator.translate("menu.inv.items.info.desc"), 36))
								.toIS());
				AddInv.setItem(33,
						new AIS(Translator.translate("menu.inv.items.changesellitem.title"), 1,
								creatingIS.get(p).getIs() == null ? Material.CHEST
										: creatingIS.get(p).getIs().getType())
												.setDamage(creatingIS.get(p).getIs() == null ? 0
														: creatingIS.get(p).getIs().getMaxStackSize() != 1
																? creatingIS.get(p).getIs().getDurability() : 0)
												.addLineToLore("")
												.addLineToLore(
														Translator.translate("menu.inv.items.changesellitem.desc"))
												.toIS());
				AddInv.setItem(50,
						new AIS(Translator.translate("menu.inv.items.add"), 1, (short) 5, Material.WOOL).toIS());
				AddInv.setItem(48,
						new AIS(Translator.translate("menu.inv.items.cancel"), 1, (short) 14, Material.WOOL).toIS());
				PMLoc.put(p, "MyMarket:ItemEditor:Add:" + Cat + ":Opened");
				p.openInventory(AddInv);
				PMLoc.put(p, "MyMarket:ItemEditor:Add:" + Cat);
				break;
			default:
				if (menu.startsWith("MyMarket:ItemEditor:Add")) {
					Inventory AddInv1 = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - My Market");
					for (int i = 0; i < 54; i++) {
						AddInv1.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
					}
					AddInv1.setItem(40, creatingIS.get(p).getIs());
					AddInv1.setItem(10, new AIS("§c-1000$", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(11, new AIS("§c-100$", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(12, new AIS("§e-10$", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(13,
							new AIS(Translator.translate("menu.inv.items.price") + ": " + creatingIS.get(p).getPrice(),
									1,
									creatingIS.get(p).getIs() == null ? Material.CHEST
											: creatingIS.get(p).getIs().getType())
													.setDamage(creatingIS.get(p).getIs() == null ? 0
															: creatingIS.get(p).getIs().getMaxStackSize() != 1
																	? creatingIS.get(p).getIs().getDurability() : 0)
													.toIS());
					AddInv1.setItem(14, new AIS("§a+10$", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(15, new AIS("§2+100$", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(16, new AIS("§2+1000$", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(19, new AIS("§c-64", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(20, new AIS("§c-16", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(21, new AIS("§e-1", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(22, new AIS(
							Translator.translate("menu.inv.items.ppersell") + ": " + creatingIS.get(p).getSellAmmount(),
							1, creatingIS.get(p).getIs() == null ? Material.CHEST : creatingIS.get(p).getIs().getType())
									.setDamage(creatingIS.get(p).getIs() == null ? 0
											: creatingIS.get(p).getIs().getMaxStackSize() != 1
													? creatingIS.get(p).getIs().getDurability() : 0)
									.toIS());
					AddInv1.setItem(23, new AIS("§a+1", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(24, new AIS("§2+16", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(25, new AIS("§2+64", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());

					AddInv1.setItem(29,
							new AIS(Translator.translate("menu.inv.items.info.title"), 1, (short) 2, Material.INK_SACK)
									.addLineToLore("").addToLore(Utils.createListFromStringToWidth(
											Translator.translate("menu.inv.items.info.desc"), 36))
									.toIS());
					AddInv1.setItem(33,
							new AIS(Translator.translate("menu.inv.items.changesellitem.title"), 1,
									creatingIS.get(p).getIs() == null ? Material.CHEST
											: creatingIS.get(p).getIs().getType())
													.setDamage(creatingIS.get(p).getIs() == null ? 0
															: creatingIS.get(p).getIs().getMaxStackSize() != 1
																	? creatingIS.get(p).getIs().getDurability() : 0)
													.addLineToLore("")
													.addLineToLore(
															Translator.translate("menu.inv.items.changesellitem.desc"))
													.toIS());
					AddInv1.setItem(50,
							new AIS(Translator.translate("menu.inv.items.add"), 1, (short) 5, Material.WOOL).toIS());
					AddInv1.setItem(48,
							new AIS(Translator.translate("menu.inv.items.cancel"), 1, (short) 14, Material.WOOL)
									.toIS());
					PMLoc.put(p, menu.endsWith(":Opened") ? menu : menu + ":Opened");
					p.openInventory(AddInv1);
					PMLoc.put(p, menu.endsWith(":Opened") ? menu.substring(0, menu.length() - 7) : menu);
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
			System.out.println("Cat: " + Cat);
			System.out.println("Menu: " + menu);
			p.closeInventory();
			this.removeFromAll(p);
			p.sendMessage(Translator.translate("dev.error"));
			p.sendMessage("§cError Code: " + Utils.addToFix(e));
		}
	}

	// public Market getMarketByPlayer(OfflinePlayer offlinePlayer) throws
	// MarketException {
	// try {
	// // return
	// // this.getMarketByFile(this.getPlayerMarketFile(offlinePlayer));
	// return APBuy.database.loadByUUID(offlinePlayer.getUniqueId().toString());
	// } catch (MarketException e) {
	// return this.createNewMarketForP(offlinePlayer);
	// }
	// }

	public Market createNewMarketForP(String uuid) throws MarketException {
		Market market = new Market(uuid, false);
		market.saveMarketInfos();
		return market;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (onItemInput.containsKey(e.getPlayer())) {
			List<ItemStack> items = new ArrayList<>();
			for (int i = 0; i < e.getPlayer().getInventory().getContents().length; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)) {
					if (e.getPlayer().getInventory().getContents()[i] != null) {
						items.add(e.getPlayer().getInventory().getContents()[i]);
					}
				}
			}
			onItemInput.remove(e.getPlayer());
			for (ItemStack is : items) {
				e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation().add(0, 1, 0), is);
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (onItemInput.containsKey(e.getPlayer())) {
			List<ItemStack> items = new ArrayList<>();
			for (int i = 0; i < e.getInventory().getContents().length; i++) {
				if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)) {
					if (e.getInventory().getContents()[i] != null) {
						items.add(e.getInventory().getContents()[i]);
					}
				}
			}
			onItemInput.remove(e.getPlayer());
			for (ItemStack is : items) {
				e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation().add(0, 1, 0), is);
			}
		}
		if (onMarketVisualiser.containsKey(e.getPlayer())) {
			onMarketVisualiser.remove(e.getPlayer());
		}
		if (BuyManager.isBuying((Player) e.getPlayer())) {
			e.getPlayer().sendMessage(Translator.translate("close.buycanceled"));
			BuyManager.removeBuyer((Player) e.getPlayer());
		}
		if (Itoomel.onItoomel.containsKey(e.getPlayer())) {
			Itoomel.onItoomel.remove(e.getPlayer());
			return;
		}
		if (creatingIS.containsKey(e.getPlayer())) {
			e.getPlayer().sendMessage(Translator.translate("close.itemcreate"));
			creatingIS.remove(e.getPlayer());
			return;
		}
		if (creatingCat.containsKey(e.getPlayer())) {
			e.getPlayer().sendMessage(Translator.translate("close.catcreate"));
			creatingCat.remove(e.getPlayer());
			return;
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(performClick(e)) {
			return;
		}
		try {
			if (BuyManager.isBuying(p)) {
				e.setCancelled(true);
				BuyManager.getBMbyPlayer(p).onClick(e.getSlot());
				return;
			}
			if ((e.getClickedInventory() != e.getView().getTopInventory()) && (onMarketVisualiser.containsKey(p))) {
				e.setCancelled(true);
				if (e.getCurrentItem() == null) {
					return;
				}
				if (onMarketVisualiser.get(p)[0].equalsIgnoreCase("AddCat")) {
					CategoryInfos c = creatingCat.get(p);
					c.setMat(e.getCurrentItem().getType());
					if (e.getCurrentItem().getMaxStackSize() > 1) {
						c.setSubid(e.getCurrentItem().getDurability());
					}
					creatingCat.put(p, c);
					openAdminShopInv("AddCat", p);
				} else if (onMarketVisualiser.get(p)[0].contains("AddItem")) {
					MarketItem mis = creatingIS.get(p);
					if (adminshop.isItemStackRegistered(e.getCurrentItem())) {
						p.sendMessage(Translator.translate("click.aregistered"));
						return;
					} else {
						if (e.getCurrentItem() == null) {
							p.sendMessage(Translator.translate("click.selectitem"));
							return;
						} else if (e.getCurrentItem().getType() == Material.AIR) {
							p.sendMessage(Translator.translate("click.selectitem"));
							return;
						}
						mis.setIs(new AIS(e.getCurrentItem().clone()).setAmmount(1).toIS());
						creatingIS.put(p, mis);
					}
					creatingIS.put(p, mis);
					openAdminShopInv(onMarketVisualiser.get(p)[0], p);
				}
				return;
			}
			if (PMLoc.containsKey(p)) {
				e.setCancelled(true);
				String menu = PMLoc.get(p);
				if (e.getClickedInventory() != e.getView().getTopInventory()) {
					if (e.getCurrentItem() == null) {
						return;
					}
					if (menu.startsWith("MyMarket:Editor:Add")) {
						CategoryInfos c = creatingCat.get(p);
						c.setMat(e.getCurrentItem().getType());
						if (e.getCurrentItem().getMaxStackSize() > 1) {
							c.setSubid(e.getCurrentItem().getDurability());
						}
						creatingCat.put(p, c);
						PMLoc.put(p, "MyMarket:Editor:Add:Opened");
						this.openInvToP("MyMarket:Editor:Add", p);
					} else if (menu.startsWith("MyMarket:ItemEditor:Add")) {
						MarketItem mis = creatingIS.get(p);
						if (APBuy.database.getMarketItemByIS(p.getUniqueId().toString(), e.getCurrentItem()) != null) {
							p.sendMessage(Translator.translate("click.aregistered"));
							return;
						} else {
							if (e.getCurrentItem() == null) {
								p.sendMessage(Translator.translate("click.selectitem"));
								return;
							} else if (e.getCurrentItem().getType() == Material.AIR) {
								p.sendMessage(Translator.translate("click.selectitem"));
								return;
							}
							mis.setIs(new AIS(e.getCurrentItem().clone()).setAmmount(1).toIS());
							creatingIS.put(p, mis);
						}
						creatingIS.put(p, mis);
						this.openItemEditor(null, menu, p);
					} else if (menu.startsWith("MyMarket:ItemInput")) {
						e.setCancelled(false);
					}
					if (onMarketVisualiser.containsKey(p)) {
						if (onMarketVisualiser.get(p)[0].equalsIgnoreCase("AddCat")) {
							CategoryInfos c = creatingCat.get(p);
							c.setMat(e.getCurrentItem().getType());
							creatingCat.put(p, c);
							openAdminShopInv("AddCat", p);
						} else if (onMarketVisualiser.get(p)[0].contains("AddItem")) {
							MarketItem mis = creatingIS.get(p);
							if (APBuy.database.getMarketItemByIS(p.getUniqueId().toString(),
									e.getCurrentItem()) != null) {
								p.sendMessage(Translator.translate("click.aregistered"));
								return;
							} else {
								if (e.getCurrentItem() == null) {
									p.sendMessage(Translator.translate("click.selectitem"));
									return;
								} else if (e.getCurrentItem().getType() == Material.AIR) {
									p.sendMessage(Translator.translate("click.selectitem"));
									return;
								}
								mis.setIs(new AIS(e.getCurrentItem().clone()).setAmmount(1).toIS());
								creatingIS.put(p, mis);
							}
							creatingIS.put(p, mis);
							openAdminShopInv(onMarketVisualiser.get(p)[0], p);
						}
					}
					return;
				}
				if (menu.equalsIgnoreCase("cmds")) {
					if (e.getSlot() == 22) {
						this.openInvToP("MainMenu", p);
						return;
					}
				} else if (menu == "MyMarket:Main") {
					switch (e.getSlot()) {
					case 10:
						MarketInfos m = APBuy.database.getMarketInfos(p.getUniqueId().toString());
						m.setOpen(!m.isOpen());
						m.save();
						this.openInvToP("MyMarket:Main", p);
						reopenMarketToWhoSee(p.getUniqueId().toString());
						break;
					case 12:
						this.openInvToP("MyMarket:Editor:Opened", p);
						PMLocPage.put(p, 0);
						break;
					case 49:
						PMLocPage.put(p, 0);
						this.openInvToP("MainMenu", p);
						break;
					case 31:
						onItemInput.put(p, new ArrayList<>());
						this.openInvToP("MyMarket:ItemInput", p);
						onItemInput.put(p, new ArrayList<>());
						break;
					}
					return;
				} else if ((menu.startsWith("MyMarket:Editor") && (!menu.startsWith("MyMarket:Editor:Add")))) {
					switch (e.getSlot()) {
					case 49:
						PMLocPage.put(p, 0);
						this.openInvToP("MyMarket:Main", p);
						break;
					case 50:
						creatingCat.put(p, new CategoryInfos(p.getUniqueId().toString()));
						this.openInvToP("MyMarket:Editor:Add", p);
						break;
					}
					if ((10 <= e.getSlot() && e.getSlot() <= 16) || (19 <= e.getSlot() && e.getSlot() <= 25)
							|| (28 <= e.getSlot() && e.getSlot() <= 34) || (37 <= e.getSlot() && e.getSlot() <= 43)) {
						if (e.getClick() == ClickType.CONTROL_DROP) {
							if (APBuy.tagger.hasTag("Cat", e.getCurrentItem())) {
								if (new Market(p.getUniqueId().toString(), false)
										.getMarketItemsByCat(APBuy.tagger.getNBTTagString("Cat", e.getCurrentItem()))
										.isEmpty()) {
									APBuy.database.removeCategory(p.getUniqueId().toString(),
											APBuy.tagger.getNBTTagString("Cat", e.getCurrentItem()));
									this.openInvToP(PMLoc.get(p), p);
									reopenMarketToWhoSee(p.getUniqueId().toString());
								} else {
									p.sendMessage(Translator.translate("click.itemsininput"));
								}
							}
						} else {
							if (APBuy.tagger.hasTag("Cat", e.getCurrentItem())) {
								PMLocPage.put(p, 0);
								this.openItemEditor(APBuy.tagger.getNBTTagString("Cat", e.getCurrentItem()), "Main", p);
							}
						}
					} else if (APBuy.tagger.hasTag("ToPage", e.getCurrentItem())) {
						PMLocPage.put(p, APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()));
						this.openInvToP("MyMarket:Editor:Opened", p);
					}
				} else if (menu.startsWith("MyMarket:Editor:Add")) {
					switch (e.getSlot()) {
					case 48:
						creatingCat.remove(p);
						this.openInvToP("MyMarket:Main", p);
						break;
					case 50:
						CategoryInfos c = creatingCat.get(p);
						if (c.getName() != null) {
							if (!APBuy.database.hasCategoryInfos(p.getUniqueId().toString(), c.getName())) {
								new CategoryInfos(p.getUniqueId().toString(), c.getName(), c.getMat(), c.getSubid(),
										c.getDesc()).save();
								creatingCat.remove(p);
								this.openInvToP("MyMarket:Main", p);
							} else {
								p.sendMessage(Translator.translate("click.agivedname"));
							}
						} else {
							p.sendMessage(Translator.translate("click.forgetname"));
						}
						break;
					case 10:
						AnvilClickEventHandler h = new AnvilClickEventHandler() {

							@Override
							public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
								event.setWillClose(false);
								if ((!event.getName().equalsIgnoreCase("")) && (!event.getName()
										.equalsIgnoreCase(Translator.translate("click.herenewname")))) {
									CategoryInfos c = creatingCat.get(p);
									c.setName(ChatColor.translateAlternateColorCodes('&', event.getName()));
									creatingCat.put(p, c);
									PMLoc.put(p, "MyMarket:Editor:Add:Opened");
									APBuy.getMarketHandler().openInvToP("MyMarket:Editor:Add", p);
								} else {
									CategoryInfos c = creatingCat.get(p);
									c.setName(null);
									creatingCat.put(p, c);
									PMLoc.put(p, "MyMarket:Editor:Add:Opened");
									APBuy.getMarketHandler().openInvToP("MyMarket:Editor:Add", p);
								}
							}
						};
						AnvilGUI_v1_8_R3 g1 = new AnvilGUI_v1_8_R3(p, h);
						g1.setSlot(AnvilSlot.INPUT_LEFT,
								new AIS(Translator.translate("click.herenewname"), 1, Material.NAME_TAG).toIS());
						g1.open();
						break;
					case 12:
						AnvilClickEventHandler h1 = new AnvilClickEventHandler() {

							@Override
							public void onAnvilClick(AnvilClickEvent event) {
								event.setWillClose(false);
								event.setWillDestroy(false);
								if ((!event.getName().equalsIgnoreCase(Translator.translate("click.herenewdesc")))
										&& (!event.getName().equalsIgnoreCase(""))) {
									CategoryInfos c = creatingCat.get(p);
									c.setDesc(ChatColor.translateAlternateColorCodes('&', event.getName()));
									creatingCat.put(p, c);
									PMLoc.put(p, "MyMarket:Editor:Add:Opened");
									APBuy.getMarketHandler().openInvToP("MyMarket:Editor:Add", p);
								} else {
									CategoryInfos c = creatingCat.get(p);
									c.setDesc(null);
									creatingCat.put(p, c);
									PMLoc.put(p, "MyMarket:Editor:Add:Opened");
									APBuy.getMarketHandler().openInvToP("MyMarket:Editor:Add", p);
								}
							}
						};
						AnvilGUI g11 = APBuy.anvilgui(p, h1);
						g11.setSlot(AnvilSlot.INPUT_LEFT,
								new AIS(Translator.translate("click.herenewdesc"), 1, Material.PAPER).toIS());
						g11.open();
						break;
					}
				} else if (menu.startsWith("MyMarket:ItemInput")) {
					if ((10 <= e.getSlot() && e.getSlot() <= 16) || (19 <= e.getSlot() && e.getSlot() <= 25)
							|| (28 <= e.getSlot() && e.getSlot() <= 34) || (37 <= e.getSlot() && e.getSlot() <= 43)) {
						e.setCancelled(false);
						return;
					}
					switch (e.getSlot()) {
					case 49:
						boolean test = false;
						for (int i = 0; i < e.getClickedInventory().getContents().length; i++) {
							if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34)
									|| (37 <= i && i <= 43)) {
								if (e.getClickedInventory().getContents()[i] != null) {
									test = true;
									break;
								}
							}
						}
						if (test) {
							p.sendMessage(Translator.translate("click.forgetitems"));
						} else {
							PMLocPage.put(p, 0);
							this.openInvToP("MyMarket:Main", p);
						}
						break;
					case 50:
						List<ItemStack> notRegistered = new ArrayList<>();
						MarketItem mis;
						for (int i = 0; i < e.getClickedInventory().getContents().length; i++) {
							if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34)
									|| (37 <= i && i <= 43)) {
								if (e.getClickedInventory().getContents()[i] != null) {
									if (APBuy.database.hasMarketItem(p.getUniqueId().toString(),
											e.getClickedInventory().getContents()[i])) {
										mis = APBuy.database.getMarketItemByIS(p.getUniqueId().toString(),
												e.getClickedInventory().getContents()[i]);
										mis.setAmmount(mis.getAmmount()
												+ e.getClickedInventory().getContents()[i].getAmount());
										mis.save();
										Itoomel.replaceMISInItoomel(mis);
									} else {
										notRegistered.add(e.getClickedInventory().getContents()[i]);
									}
								}
							}
						}
						onItemInput.put(p, notRegistered);
						this.openInvToP("MyMarket:ItemInput", p);
						break;
					}
				} else if (menu.startsWith("Markets")) {
					if (e.getSlot() == 49) {
						PMLocPage.put(p, 0);
						this.openInvToP("MainMenu", p);
					}
					if ((10 <= e.getSlot() && e.getSlot() <= 16) || (19 <= e.getSlot() && e.getSlot() <= 25)
							|| (28 <= e.getSlot() && e.getSlot() <= 34) || (37 <= e.getSlot() && e.getSlot() <= 43)) {
						if (APBuy.tagger.hasTag("Market", e.getCurrentItem())) {
							PMLoc.remove(p);
							PMLocPage.remove(p);
							openMarketVisualiserToPlayer("Main",
									APBuy.tagger.getNBTTagString("Market", e.getCurrentItem())
											.replaceFirst(Pattern.quote(".yml"), ""),
									p);
							return;
						}
					}
					if (APBuy.tagger.hasTag("ToPage", e.getCurrentItem())) {
						PMLocPage.put(p, APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()));
						this.openInvToP("Markets:Opened", p);
						return;
					}
				} else if (menu.startsWith("MyMarket:ItemEditor:")) {
					if (menu.startsWith("MyMarket:ItemEditor:Main")) {
						if (e.getSlot() == 49) {
							PMLocPage.put(p, 0);
							this.openInvToP("MyMarket:Editor:Main", p);
						}
						if ((10 <= e.getSlot() && e.getSlot() <= 16) || (19 <= e.getSlot() && e.getSlot() <= 25)
								|| (28 <= e.getSlot() && e.getSlot() <= 34)
								|| (37 <= e.getSlot() && e.getSlot() <= 43)) {
							if (APBuy.tagger.hasTag("Item", e.getCurrentItem())) {
								if (e.getClick() == ClickType.CONTROL_DROP) {
									MarketItem is = new Market(p.getUniqueId().toString(), false)
											.getMarketItemByIS(APBuy.tagger.removeNBTTag("Item",
													new AIS(e.getCurrentItem().clone()).removeLatestLore(4).toIS()));
									if (is != null) {
										if (is.getAmmount() != 0) {
											p.sendMessage(Translator.translate("click.catforgetitems"));
											return;
										} else {
											APBuy.database.removeItem(p.getUniqueId().toString(), is.getIs());
											// this.getMarketByPlayer(p).removeItemInCat(
											// menu.endsWith(":Opened")
											// ? menu.substring(0, menu.length()
											// - 7)
											// .replaceFirst("MyMarket:ItemEditor:Main:",
											// "")
											// :
											// menu.replaceFirst("MyMarket:ItemEditor:Main:",
											// ""));
											this.openItemEditor(
													menu.endsWith(":Opened")
															? menu.substring(0, menu.length() - 7)
																	.replaceFirst("MyMarket:ItemEditor:Main:", "")
															: menu.replaceFirst("MyMarket:ItemEditor:Main:", ""),
													"Main", p);
										}
									}
								} else if (e.getClick() == ClickType.MIDDLE) {
									Market m = new Market(p.getUniqueId().toString(), true);
									MarketItem is = m.getMarketItemByIS(APBuy.tagger.removeNBTTag("Item",
											new AIS(e.getCurrentItem().clone()).removeLatestLore(4).toIS()));
									if (is != null) {
										if (Utils.getPlaceForIS(p, is.getAmmount(), is.getIs()) != 0) {
											int count = Utils.getPlaceForIS(p, is.getAmmount(), is.getIs());
											Utils.addItemToPlayer(p, is.getIs().clone(), count);
											m.removeItem(is.getIs().clone(), count);
											Itoomel.removeMISFromItoomel(is.getIs(), is.getMarketuuid());
											this.openItemEditor(
													menu.endsWith(":Opened")
															? menu.substring(0, menu.length() - 7)
																	.replaceFirst("MyMarket:ItemEditor:Main:", "")
															: menu.replaceFirst("MyMarket:ItemEditor:Main:", ""),
													"Main", p);
										} else {
											if (is.getAmmount() == 0) {
												p.sendMessage(Translator.translate("click.noitems"));
											} else {
												p.sendMessage(Translator.translate("click.noinvplace"));
											}
										}
									}
								} else if (e.getClick() == ClickType.RIGHT) {
									Market m = new Market(p.getUniqueId().toString(), true);
									MarketItem is = m.getMarketItemByIS(APBuy.tagger.removeNBTTag("Item",
											new AIS(e.getCurrentItem().clone()).removeLatestLore(4).toIS()));
									if (is != null) {
										int test = (int) (is.getAmmount() - 32 < 0 ? is.getAmmount() : 32);
										if (Utils.getPlaceForIS(p, test, is.getIs()) != 0) {
											int count = Utils.getPlaceForIS(p, test, is.getIs());
											Utils.addItemToPlayer(p, is.getIs().clone(), count);
											if (m.removeItem(is.getIs().clone(), count) == 0) {
												Itoomel.removeMISFromItoomel(is.getIs(), is.getMarketuuid());
											}
											// else {
											// is.setAmmount(is.getAmmount() -
											// 32);
											// Itoomel.replaceMISInItoomel(is);
											// }
											this.openItemEditor(
													menu.endsWith(":Opened")
															? menu.substring(0, menu.length() - 7)
																	.replaceFirst("MyMarket:ItemEditor:Main:", "")
															: menu.replaceFirst("MyMarket:ItemEditor:Main:", ""),
													"Main", p);

										} else {
											if (is.getAmmount() == 0) {
												p.sendMessage(Translator.translate("click.noitems"));
											} else {
												p.sendMessage(Translator.translate("click.noinvplace"));
											}
										}
									}
								}
							}
						} else if (e.getSlot() == 50) {
							if (e.getWhoClicked().getInventory().getContents().length == 0) {
								e.getWhoClicked().sendMessage(Translator.translate("click.noiteminvregister"));
								return;
							}
							creatingIS.put(p, new MarketItem(e.getWhoClicked().getUniqueId().toString(),
									menu.replaceFirst("MyMarket:ItemEditor:Main:", "").replace(":Opened", "")));
							this.openItemEditor(
									menu.replaceFirst("MyMarket:ItemEditor:Main:", "").replace(":Opened", ""), "Add",
									p);
						} else if (APBuy.tagger.hasTag("ToPage", e.getCurrentItem())) {
							PMLocPage.put(p, APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()));
							this.openItemEditor(menu.endsWith(":Opened")
									? menu.substring(0, menu.length() - 7).replaceFirst("MyMarket:ItemEditor:Main:", "")
									: menu.replaceFirst("MyMarket:ItemEditor:Main:", ""), "Main", p);
						}
					} else if (menu.startsWith("MyMarket:ItemEditor:Add")) {
						MarketItem mis;
						int price;
						int sm;
						if ((e.getSlot() != 48) && (creatingIS.get(p).getIs() == null)) {
							p.sendMessage(Translator.translate("click.noitemselected"));
							return;
						}
						switch (e.getSlot()) {
						case 11:
							mis = creatingIS.get(p);
							price = mis.getPrice();
							price = price > 100 ? price - 100 : 0;
							mis.setPrice(price);
							creatingIS.put(p, mis);
							this.openItemEditor(null, menu, p);
							break;
						case 12:
							mis = creatingIS.get(p);
							price = mis.getPrice();
							price = price > 1 ? price - 10 : 0;
							mis.setPrice(price);
							creatingIS.put(p, mis);
							this.openItemEditor(null, menu, p);
							break;
						case 14:
							mis = creatingIS.get(p);
							price = mis.getPrice();
							price = price + 10;
							mis.setPrice(price);
							creatingIS.put(p, mis);
							this.openItemEditor(null, menu, p);
							break;
						case 15:
							mis = creatingIS.get(p);
							price = mis.getPrice();
							price = price + 100;
							mis.setPrice(price);
							creatingIS.put(p, mis);
							this.openItemEditor(null, menu, p);
							break;
						case 19:
							mis = creatingIS.get(p);
							sm = mis.getSellAmmount();
							sm = sm > 64 ? sm - 64 : 1;
							mis.setSellAmmount(sm);
							creatingIS.put(p, mis);
							this.openItemEditor(null, menu, p);
							break;
						case 20:
							mis = creatingIS.get(p);
							sm = mis.getSellAmmount();
							sm = sm > 16 ? sm - 16 : 1;
							mis.setSellAmmount(sm);
							creatingIS.put(p, mis);
							this.openItemEditor(null, menu, p);
							break;
						case 21:
							mis = creatingIS.get(p);
							sm = mis.getSellAmmount();
							sm = sm > 1 ? sm - 1 : 1;
							mis.setSellAmmount(sm);
							creatingIS.put(p, mis);
							this.openItemEditor(null, menu, p);
							break;
						case 23:
							mis = creatingIS.get(p);
							sm = mis.getSellAmmount();
							sm++;
							mis.setSellAmmount(sm);
							creatingIS.put(p, mis);
							this.openItemEditor(null, menu, p);
							break;
						case 24:
							mis = creatingIS.get(p);
							sm = mis.getSellAmmount();
							sm += 16;
							mis.setSellAmmount(sm);
							creatingIS.put(p, mis);
							this.openItemEditor(null, menu, p);
							break;
						case 25:
							mis = creatingIS.get(p);
							sm = mis.getSellAmmount();
							sm += 64;
							mis.setSellAmmount(sm);
							creatingIS.put(p, mis);
							this.openItemEditor(null, menu, p);
							break;
						case 50:
							creatingIS.get(p).save();
							// ,menu.endsWith(":Opened")
							// ? menu.substring(0, menu.length() - 7)
							// .replaceFirst("MyMarket:ItemEditor:Add:", "")
							// : menu.replaceFirst("MyMarket:ItemEditor:Add:",
							// ""));
							creatingIS.remove(p);
							this.openItemEditor(menu.endsWith(":Opened")
									? menu.substring(0, menu.length() - 7).replaceFirst("MyMarket:ItemEditor:Add:", "")
									: menu.replaceFirst("MyMarket:ItemEditor:Add:", ""), "Main", p);
							break;
						case 48:
							creatingIS.remove(p);
							this.openItemEditor(menu.endsWith(":Opened")
									? menu.substring(0, menu.length() - 7).replaceFirst("MyMarket:ItemEditor:Add:", "")
									: menu.replaceFirst("MyMarket:ItemEditor:Add:", ""), "Main", p);
							break;
						}
					}
				}
			}
			if (onMarketVisualiser.containsKey(p)) {
				e.setCancelled(true);
				if (e.getSlot() == 49) {
					switch (((String) onMarketVisualiser.get(p)[0]).split(":")[0]) {
					case "Main":
						PMLocPage.put(p, 0);
						this.openInvToP("Markets:Opened", p);
						break;
					case "Cats":
						if (onMarketVisualiser.get(p)[1] == "AdminShop") {
							PMLocPage.remove(p);
							this.openMainMenu(p);
						} else {
							PMLocPage.put(p, 0);
							openMarketVisualiserToPlayer("Main", ((String) onMarketVisualiser.get(p)[1]), p);
						}
						break;
					case "Cat":
						PMLocPage.put(p, 0);
						openMarketVisualiserToPlayer("Cats", ((String) onMarketVisualiser.get(p)[1]), p);
						break;
					case "InvisSee":
						PMLocPage.put(p, 0);
						openMarketVisualiserToPlayer("Main", ((String) onMarketVisualiser.get(p)[1]), p);
						break;
					}
				} else {
					if (onMarketVisualiser.get(p)[0] == "AddCat") {
						switch (e.getSlot()) {
						case 48:
							creatingCat.remove(p);
							openMarketVisualiserToPlayer("Cats", "AdminShop", p);
							break;
						case 50:
							CategoryInfos c = creatingCat.get(p);
							if (c.getName() != null) {
								Market m = adminshop;
								if (m.getCatInfosByName(c.getName()) == null) {
									c.save();
									creatingCat.remove(p);
									openMarketVisualiserToPlayer("Cats", "AdminShop", p);
								} else {
									p.sendMessage(Translator.translate("click.agivedname"));
								}
							} else {
								p.sendMessage(Translator.translate("click.forgetname"));
							}
							break;
						case 10:
							AnvilClickEventHandler h = new AnvilClickEventHandler() {

								@Override
								public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
									event.setWillClose(false);
									if ((!event.getName().equalsIgnoreCase("")) && (!event.getName()
											.equalsIgnoreCase(Translator.translate("click.herenewname")))) {
										CategoryInfos c = creatingCat.get(p);
										c.setName(ChatColor.translateAlternateColorCodes('&', event.getName()));
										creatingCat.put(p, c);
										openAdminShopInv("AddCat", p);
									} else {
										CategoryInfos c = creatingCat.get(p);
										c.setName(null);
										creatingCat.put(p, c);
										openAdminShopInv("AddCat", p);
									}
								}
							};

							AnvilGUI_v1_8_R3 g1 = new AnvilGUI_v1_8_R3(p, h);
							g1.setSlot(AnvilSlot.INPUT_LEFT,
									new AIS(Translator.translate("click.herenewname"), 1, Material.NAME_TAG).toIS());
							g1.open();
							break;
						case 12:
							AnvilClickEventHandler h1 = new AnvilClickEventHandler() {

								@Override
								public void onAnvilClick(AnvilClickEvent event) {
									event.setWillClose(false);
									event.setWillDestroy(false);
									if ((!event.getName().equalsIgnoreCase(Translator.translate("click.herenewdesc")))
											&& (!event.getName().equalsIgnoreCase(""))) {
										CategoryInfos c = creatingCat.get(p);
										c.setDesc(ChatColor.translateAlternateColorCodes('&', event.getName()));
										creatingCat.put(p, c);
										openAdminShopInv("AddCat", p);
									} else {
										CategoryInfos c = creatingCat.get(p);
										c.setDesc(null);
										creatingCat.put(p, c);
										openAdminShopInv("AddCat", p);
									}
								}
							};
							AnvilGUI g11 = APBuy.anvilgui(p, h1);
							g11.setSlot(AnvilSlot.INPUT_LEFT,
									new AIS(Translator.translate("click.herenewdesc"), 1, Material.PAPER).toIS());
							g11.open();
							break;
						}
						return;
					} else if (onMarketVisualiser.get(p)[0].contains("AddItem")) {
						MarketItem mis;
						int price;
						int sm;
						String menu = onMarketVisualiser.get(p)[0];
						if ((e.getSlot() != 48) && (creatingIS.get(p).getIs() == null)) {
							p.sendMessage(Translator.translate("click.noitemselected"));
							return;
						}
						switch (e.getSlot()) {
						case 11:
							mis = creatingIS.get(p);
							creatingIS.remove(p);
							price = mis.getPrice();
							price = price > 100 ? price - 100 : 0;
							mis.setPrice(price);
							creatingIS.put(p, mis);
							openAdminShopInv(menu, p);
							break;
						case 12:
							mis = creatingIS.get(p);
							creatingIS.remove(p);
							price = mis.getPrice();
							price = price > 1 ? price - 10 : 0;
							mis.setPrice(price);
							creatingIS.put(p, mis);
							openAdminShopInv(menu, p);
							break;
						case 14:
							mis = creatingIS.get(p);
							creatingIS.remove(p);
							price = mis.getPrice();
							price = price + 10;
							mis.setPrice(price);
							creatingIS.put(p, mis);
							openAdminShopInv(menu, p);
							break;
						case 15:
							mis = creatingIS.get(p);
							creatingIS.remove(p);
							price = mis.getPrice();
							price = price + 100;
							mis.setPrice(price);
							creatingIS.put(p, mis);
							openAdminShopInv(menu, p);
							break;
						case 19:
							mis = creatingIS.get(p);
							creatingIS.remove(p);
							sm = mis.getSellAmmount();
							sm = sm > 64 ? sm - 64 : 1;
							mis.setSellAmmount(sm);
							creatingIS.put(p, mis);
							openAdminShopInv(menu, p);
							break;
						case 20:
							mis = creatingIS.get(p);
							creatingIS.remove(p);
							sm = mis.getSellAmmount();
							sm = sm > 16 ? sm - 16 : 1;
							mis.setSellAmmount(sm);
							creatingIS.put(p, mis);
							openAdminShopInv(menu, p);
							break;
						case 21:
							mis = creatingIS.get(p);
							creatingIS.remove(p);
							sm = mis.getSellAmmount();
							sm = sm > 1 ? sm - 1 : 1;
							mis.setSellAmmount(sm);
							creatingIS.put(p, mis);
							openAdminShopInv(menu, p);
							break;
						case 23:
							mis = creatingIS.get(p);
							creatingIS.remove(p);
							sm = mis.getSellAmmount();
							sm++;
							mis.setSellAmmount(sm);
							creatingIS.put(p, mis);
							openAdminShopInv(menu, p);
							break;
						case 24:
							mis = creatingIS.get(p);
							creatingIS.remove(p);
							sm = mis.getSellAmmount();
							sm += 16;
							mis.setSellAmmount(sm);
							creatingIS.put(p, mis);
							openAdminShopInv(menu, p);
							break;
						case 25:
							mis = creatingIS.get(p);
							creatingIS.remove(p);
							sm = mis.getSellAmmount();
							sm += 64;
							mis.setSellAmmount(sm);
							creatingIS.put(p, mis);
							openAdminShopInv(menu, p);
							break;
						case 50:
							// adminshop.addItemToCategory(
							creatingIS.get(p).save();
							// , menu.replaceAll(":AddItem",
							// "").replaceFirst("Cat:", ""));
							creatingIS.remove(p);
							openMarketVisualiserToPlayer(menu.replaceAll(":AddItem", ""), "AdminShop", p);
							break;
						case 48:
							creatingIS.remove(p);
							openMarketVisualiserToPlayer(menu.replaceAll(":AddItem", ""), "AdminShop", p);
							break;
						}
						return;
					}
					if (APBuy.tagger.hasTag("ToPage", e.getCurrentItem())) {
						PMLocPage.put(p, APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()));
						if (onMarketVisualiser.get(p)[0] == "Cats") {
							openMarketVisualiserToPlayer("Cats", ((String) onMarketVisualiser.get(p)[1]), p);
						} else if (((String) onMarketVisualiser.get(p)[0]).startsWith("Cat:")) {
							openMarketVisualiserToPlayer((String) onMarketVisualiser.get(p)[0],
									((String) onMarketVisualiser.get(p)[1]), p);
						} else if (onMarketVisualiser.get(p)[0].equalsIgnoreCase("InvisSee")) {
							PMLocPage.put(p, APBuy.tagger.getNBTTagInt("ToPage", e.getCurrentItem()));
							openMarketVisualiserToPlayer("InvisSee", ((String) onMarketVisualiser.get(p)[1]), p);
						}
						return;
					} else if (APBuy.tagger.hasTag("Cat", e.getCurrentItem())) {
						if (onMarketVisualiser.get(p)[1].equals("AdminShop")) {
							if (p.hasPermission("apb.mod.adminshop")) {
								if (e.getClick() == ClickType.CONTROL_DROP) {
									adminshop.removeCategory(APBuy.tagger.getNBTTagString("Cat", e.getCurrentItem()));
									openMarketVisualiserToPlayer("Cats", "AdminShop", p);
									return;
								}
							}
						}
						PMLocPage.put(p, 0);
						openMarketVisualiserToPlayer("Cat:" + APBuy.tagger.getNBTTagString("Cat", e.getCurrentItem()),
								onMarketVisualiser.get(p)[1], p);
						return;
					} else if (APBuy.tagger.hasTag("ToBuy", e.getCurrentItem())) {
						if (onMarketVisualiser.get(p)[1] == "AdminShop") {
							if (p.hasPermission("apb.mod.adminshop")) {
								if (e.getClick() == ClickType.CONTROL_DROP) {
									APBuy.database.removeItem("AdminShop", APBuy.tagger.removeNBTTag("ToBuy",
											new AIS(e.getCurrentItem()).removeLatestLore(2).toIS()));
									openMarketVisualiserToPlayer(onMarketVisualiser.get(p)[0], "AdminShop", p);
									return;
								}
							}
						}
						if (BuyManager.openBuyManager(
								new Market(onMarketVisualiser.get(p)[1], false)
										.getMarketItemByIS(new AIS(e.getCurrentItem().clone())
												.removeLatestLore(
														onMarketVisualiser.get(p)[1].equals("AdminShop") ? 2 : 3)
												.removeNBTTag("ToBuy").toIS()),
								onMarketVisualiser.get(p)[1], 1, (Player) e.getWhoClicked(), false,
								onMarketVisualiser.get(p))) {
							onMarketVisualiser.remove(p);
						}
						return;
					} else if (APBuy.tagger.hasTag("AddCat", e.getCurrentItem())) {
						creatingCat.put(p, new CategoryInfos("AdminShop"));
						openAdminShopInv("AddCat", p);
						return;
					} else if (APBuy.tagger.hasTag("AddItem", e.getCurrentItem())) {
						creatingIS.put(p,
								new MarketItem("AdminShop", onMarketVisualiser.get(p)[0].replaceFirst("Cat:", "")));
						openAdminShopInv(onMarketVisualiser.get(p)[0] + ":AddItem", p);
						return;
					}
					if (onMarketVisualiser.get(p)[0] == "Main") {
						try {
							switch (e.getSlot()) {
							case 10:
								PMLocPage.put(p, 0);
								openMarketVisualiserToPlayer("Cats", ((String) onMarketVisualiser.get(p)[1]), p);
								break;
							case 22:
								PMLocPage.put(p, 0);
								this.openInvToP("Markets:Opened", p);
								break;
							case 28:
								if (p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.status")) {
									APBuy.database.getMarketInfos((String) onMarketVisualiser.get(p)[1]).setOpen(
											e.getCurrentItem().getItemMeta().getDisplayName().contains("geschlossen"))
											.save();
									openMarketVisualiserToPlayer("Main", ((String) onMarketVisualiser.get(p)[1]), p);
								}
								break;
							case 30:
								if (p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.reset")) {
									APBuy.database.getMarketInfos((String) onMarketVisualiser.get(p)[1]).resetStats();
									openMarketVisualiserToPlayer("Main", ((String) onMarketVisualiser.get(p)[1]), p);
									updateLists();
								}
								break;
							case 32:
								if (p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.delete")) {
									APBuy.database
											.deleteMarket(UUID.fromString(((String) onMarketVisualiser.get(p)[1])));
									updateLists();
									Itoomel.removeMarketFromItoomel(
											UUID.fromString(((String) onMarketVisualiser.get(p)[1])));
									Itoomel.reopenItoomelToEveryone();
									reopenMarketsToEveryone();
									p.closeInventory();
								}
								break;
							case 34:
								PMLocPage.put(p, 0);
								openMarketVisualiserToPlayer("InvisSee", ((String) onMarketVisualiser.get(p)[1]), p);
								break;
							}
						} catch (MarketException e1) {
							switch (e1.getErrorCause()) {
							case NOTFOUND:
								p.sendMessage(Translator.translate("click.notfoundmarket"));

								updateLists();
								break;
							case CATNOTFOUND:
								p.sendMessage(Translator.translate("click.notfoundcat"));
								break;
							default:
								break;
							}
							this.removeFromAll(p);
							p.closeInventory();
							p.sendMessage("§cError Code: " + Utils.addToFix(e1));
						}
					}

				}
			}
		} catch (

		MarketException e1) {
			switch (e1.getErrorCause()) {
			case CATNOTFOUND:
				p.sendMessage(Translator.translate("click.notfoundmarket"));
				this.removeFromAll(p);
				p.closeInventory();
				return;
			case LOAD:
				break;
			case MIS:
				break;
			case NOTFOUND:
				p.sendMessage(Translator.translate("click.notfoundcat"));
				updateLists();
				this.removeFromAll(p);
				p.closeInventory();
				this.openInvToP("Markets", p);
				return;
			case NULL:
				break;
			case SAVE:
				break;
			}
			this.removeFromAll(p);
			p.closeInventory();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
			System.out.println("Slot: " + e.getSlot());
			p.closeInventory();
			this.removeFromAll(p);
			p.sendMessage(Translator.translate("dev.error"));
			p.sendMessage("§cError Code: " + Utils.addToFix(e1));
		}
	}

	private void openAdminShopInv(String menu, Player p) {
		if (menu.equalsIgnoreCase("AddCat")) {
			CategoryInfos cat = creatingCat.get(p);
			Inventory inv1 = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - AdminShop");
			for (int i = 0; i < 54; i++) {
				inv1.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			boolean hasItem = false;
			for (ItemStack stack : p.getInventory().getContents()) {
				if (stack != null) {
					hasItem = true;
					break;
				}
			}
			if (!hasItem) {
				p.sendMessage(Translator.translate("click.noiteminvregister"));
				openMarketVisualiserToPlayer("Cats", "AdminShop", p);
				return;
			}

			// - Back Button 49
			inv1.setItem(48,
					new AIS(Translator.translate("menu.inv.adminshop.cancel"), 1, (short) 14, Material.WOOL).toIS());

			// - Finish Button 49
			inv1.setItem(50,
					new AIS(Translator.translate("menu.inv.adminshop.finish"), 1, (short) 5, Material.WOOL).toIS());

			// - Cat Prewiev 31
			inv1.setItem(31, cat.getAIS().toIS());

			// - Cat Rename 10
			inv1.setItem(10,
					new AIS(Translator.translate("menu.inv.adminshop.change.name"), 1, Material.NAME_TAG).toIS());

			// - Cat Relore 12
			inv1.setItem(12, new AIS(Translator.translate("menu.inv.adminshop.change.desc"), 1, Material.PAPER).toIS());

			// - Cat Set Item 14
			inv1.setItem(14,
					new AIS(cat.getMat()).setDamage(cat.getSubid()).addLineToLore("")
							.addLineToLore(Translator.translate("menu.inv.adminshop.setitem.desc"))
							.setName(Translator.translate("menu.inv.adminshop.setitem.title")).toIS());
			String[] ss = onMarketVisualiser.get(p);
			ss[0] = "AddCat";
			creatingCat.remove(p);
			onMarketVisualiser.remove(p);
			p.openInventory(inv1);
			creatingCat.put(p, cat);
			onMarketVisualiser.put(p, ss);
		} else if (menu.contains("AddItem")) {
			boolean hasItem = false;
			for (ItemStack stack : p.getInventory().getContents()) {
				if (stack != null) {
					hasItem = true;
					break;
				}
			}
			if (!hasItem) {
				p.sendMessage(Translator.translate("click.noiteminvregister"));
				openMarketVisualiserToPlayer(menu.substring(0, menu.length() - 8), "AdminShop", p);
				return;
			}
			MarketItem mis = creatingIS.get(p);
			creatingIS.remove(p);
			Inventory AddInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - AdminShop");
			for (int i = 0; i < 54; i++) {
				AddInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			AddInv.setItem(40, mis.getIs() == null
					? new AIS(Translator.translate("menu.inv.items.noitem.title"), 1, Material.CHEST).addLineToLore("")
							.addLineToLore(Translator.translate("menu.inv.items.noitem.desc")).toIS()
					: mis.getIs());
			AddInv.setItem(10, new AIS("§c-1000$", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(11, new AIS("§c-100$", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(12, new AIS("§e-10$", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(13, new AIS(Translator.translate("menu.inv.items.price") + ": " + mis.getPrice(), 1,
					(mis.getIs() == null ? Material.CHEST : mis.getIs().getType())).toIS());
			AddInv.setItem(14, new AIS("§a+10$", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(15, new AIS("§2+100$", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(16, new AIS("§2+1000$", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(19, new AIS("§c-64", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(20, new AIS("§c-16", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(21, new AIS("§e-1", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(22, new AIS(Translator.translate("menu.inv.items.ppersell") + ": " + mis.getSellAmmount(), 1,
					(mis.getIs() == null ? Material.CHEST : mis.getIs().getType())).toIS());
			AddInv.setItem(23, new AIS("§a+1", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(24, new AIS("§2+16", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(25, new AIS("§2+64", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(29,
					new AIS(Translator.translate("menu.inv.items.info.title"), 1, (short) 2, Material.INK_SACK)
							.addToLore(Utils
									.createListFromStringToWidth(Translator.translate("menu.inv.items.info.desc"), 36))
							.toIS());
			AddInv.setItem(33,
					new AIS(Translator.translate("menu.inv.items.changesellitem.title"), 1,
							mis.getIs() == null ? Material.CHEST : mis.getIs().getType()).addLineToLore("")
									.addLineToLore(Translator.translate("menu.inv.items.changesellitem.desc")).toIS());
			AddInv.setItem(50, new AIS(Translator.translate("menu.inv.items.add"), 1, (short) 5, Material.WOOL).toIS());
			AddInv.setItem(48,
					new AIS(Translator.translate("menu.inv.items.cancel"), 1, (short) 14, Material.WOOL).toIS());
			String[] ss = onMarketVisualiser.get(p);
			ss[0] = menu;
			onMarketVisualiser.remove(p);
			p.openInventory(AddInv);
			creatingIS.put(p, mis);
			onMarketVisualiser.put(p, ss);
		}
	}

	public void openMarketVisualiserToPlayer(String s, String uuid, Player p) {
		try {
			Market m = new Market(uuid, true);
			switch (s.split(":")[0]) {
			case "Main":
				if (APBuy.hasPlayerModPerms(p)) {
					Inventory MVMainInv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Markets");
					for (int i = 0; i < 54; i++) {
						MVMainInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
					}
					MVMainInv.setItem(10,
							new AIS(Translator.translate("menu.inv.marketv.main.marketsee"), 1, Material.CHEST).toIS());
					MVMainInv.setItem(16,
							new AIS(Translator.translate("menu.inv.marketv.main.stats.title"), 1, Material.PAPER)
									.addLineToLore("")
									.addLineToLore(Translator.translate("menu.inv.marketv.main.stats.solds") + ": "
											+ m.getSales())
									.addLineToLore(Translator.translate("menu.inv.marketv.main.stats.solditems") + ": "
											+ m.getSoldItems())
									.toIS());
					MVMainInv.setItem(49, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());
					MVMainInv.setItem(4, m.getMarkeAIS().toIS());
					if (p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.status")) {
						if (m.isOpen()) {
							MVMainInv
									.setItem(28,
											new AIS(Translator.translate("menu.inv.marketv.main.mod.status.open"), 1,
													(short) 2, Material.INK_SACK)
															.addLineToLore("")
															.addToLore(Utils.createListFromStringToWidth(
																	Translator.translate(
																			"menu.inv.marketv.main.mod.status.whatdoes"),
																	25))
															.toIS());
						} else {
							MVMainInv
									.setItem(28,
											new AIS(Translator.translate("menu.inv.marketv.main.mod.status.close"), 1,
													(short) 1, Material.INK_SACK)
															.addLineToLore("")
															.addToLore(Utils.createListFromStringToWidth(
																	Translator.translate(
																			"menu.inv.marketv.main.mod.status.whatdoes"),
																	25))
															.toIS());
						}
					} else {
						if (m.isOpen()) {
							MVMainInv
									.setItem(28,
											new AIS(Translator.translate("menu.inv.marketv.main.mod.status.open"), 1,
													(short) 2, Material.BARRIER)
															.addLineToLore("")
															.addToLore(Utils.createListFromStringToWidth(
																	Translator.translate(
																			"menu.inv.marketv.main.mod.status.norights"),
																	25))
															.toIS());
						} else {
							MVMainInv
									.setItem(28,
											new AIS(Translator.translate("menu.inv.marketv.main.mod.status.close"), 1,
													(short) 1, Material.BARRIER)
															.addLineToLore("")
															.addToLore(Utils.createListFromStringToWidth(
																	Translator.translate(
																			"menu.inv.marketv.main.mod.status.norights"),
																	25))
															.toIS());
						}
					}
					if (p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.reset")) {
						MVMainInv.setItem(30,
								new AIS(Translator.translate("menu.inv.marketv.main.mod.reset.title"), 1,
										Material.BOOK_AND_QUILL)
												.addLineToLore("")
												.addToLore(Utils.createListFromStringToWidth(
														Translator.translate("menu.inv.marketv.main.mod.reset.title"),
														25))
												.toIS());
					} else {
						MVMainInv.setItem(30,
								new AIS(Translator.translate("menu.inv.marketv.main.mod.reset.title"), 1,
										Material.BARRIER)
												.addLineToLore("")
												.addToLore(Utils.createListFromStringToWidth(
														Translator.translate("menu.inv.marketv.main.mod.reset.title"),
														25))
												.toIS());
					}
					if (p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.delete")) {
						MVMainInv.setItem(32,
								new AIS(Translator.translate("menu.inv.marketv.main.mod.delete.title"), 1,
										Material.ANVIL)
												.addLineToLore("")
												.addToLore(Utils.createListFromStringToWidth(Translator
														.translate("menu.inv.marketv.main.mod.delete.whatdoes"), 25))
												.toIS());
					} else {
						MVMainInv.setItem(32,
								new AIS(Translator.translate("menu.inv.marketv.main.mod.delete.title"), 1,
										Material.BARRIER)
												.addLineToLore("")
												.addToLore(Utils.createListFromStringToWidth(Translator
														.translate("menu.inv.marketv.main.mod.delete.norights"), 25))
												.toIS());
					}
					if (p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.invissee")) {
						MVMainInv.setItem(34,
								new AIS(Translator.translate("menu.inv.marketv.main.mod.invissee.title"), 1,
										Material.CHEST)
												.addLineToLore("")
												.addToLore(Utils.createListFromStringToWidth(Translator
														.translate("menu.inv.marketv.main.mod.invissee.whatdoes"), 25))
												.toIS());
					} else {
						MVMainInv.setItem(34,
								new AIS(Translator.translate("menu.inv.marketv.main.mod.invissee.title"), 1,
										Material.BARRIER)
												.addLineToLore("")
												.addToLore(Utils.createListFromStringToWidth(Translator
														.translate("menu.inv.marketv.main.mod.invissee.norights"), 25))
												.toIS());
					}
					onMarketVisualiser.remove(p);
					p.openInventory(MVMainInv);

					onMarketVisualiser.put(p, new String[] { "Main", m.getMarketOwner() });
				} else {
					Inventory MVMainInv = Bukkit.createInventory(null, 27, "§0§lA§3§lP§r§8Buy - Markets");
					for (int i = 0; i < 27; i++) {
						MVMainInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
					}
					MVMainInv.setItem(10,
							new AIS(Translator.translate("menu.inv.marketv.main.marketsee"), 1, Material.CHEST).toIS());
					MVMainInv.setItem(16,
							new AIS(Translator.translate("menu.inv.marketv.main.stats.title"), 1, Material.PAPER)
									.addLineToLore("")
									.addLineToLore(Translator.translate("menu.inv.marketv.main.stats.solds") + ": "
											+ m.getSales())
									.addLineToLore(Translator.translate("menu.inv.marketv.main.stats.solditems") + ": "
											+ m.getSoldItems())
									.toIS());
					MVMainInv.setItem(22, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());
					MVMainInv.setItem(4, m.getMarkeAIS().toIS());
					onMarketVisualiser.remove(p);
					p.openInventory(MVMainInv);
					onMarketVisualiser.put(p, new String[] { "Main", m.getMarketOwner() });
				}
				break;
			case "Cats":
				int page = PMLocPage.get(p);
				Inventory catsInv = Bukkit.createInventory(null, 54, m.getMarketOwner().equals("AdminShop")
						? "§0§lA§3§lP§r§8Buy - AdminShop" : "§0§lA§3§lP§r§8Buy - Markets");
				for (int i = 0; i < 54; i++) {
					if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
							|| (i == 49)) {
						continue;
					}
					catsInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				for (int i1 = 0; i1 < 4; i1++) {
					for (int i2 = 0; i2 < 7; i2++) {
						catsInv.setItem(10 + i1 * 9 + i2,
								new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
					}
				}
				if (p.hasPermission("apb.mod.adminshop") && (m.getMarketOwner().equals("AdminShop"))) {
					catsInv.setItem(50,
							APBuy.tagger.setNBTTag("AddCat", true,
									new AIS(Translator.translate("menu.inv.mymarket.editor.newcat"), 1, Material.CHEST)
											.toIS()));
					catsInv.setItem(48,
							new AIS(Translator.translate("menu.inv.mymarket.editor.delete.title"), 1, Material.CHEST)
									.addLineToLore("").addToLore(Utils.createListFromStringToWidth(
											Translator.translate("menu.inv.mymarket.editor.delete.desc"), 40))
									.toIS());
				}
				catsInv.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
						.addLineToLore(Translator.translate("menu.openerror2")).toIS());
				PMLocPage.remove(p);
				onMarketVisualiser.remove(p);
				p.openInventory(catsInv);
				PMLocPage.put(p, page);
				Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

					@Override
					public void run() {
						try {
							List<CategoryInfos> catinfoss = m.getCatsInfos();
							// - Back Button 49
							catsInv.setItem(49, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());
							// - Getting all Markets to display
							int size = catinfoss.size();
							int pages = ((size - (size % 28)) / 28);
							int count = 28 * PMLocPage.get(p);
							if (size != 0) {
								for (int i1 = 0; i1 < 4; i1++) {
									for (int i2 = 0; i2 < 7; i2++) {
										if (count >= size) {
											break;
										}
										catsInv.setItem(10 + i1 * 9 + i2, APBuy.tagger.setNBTTag("Cat",
												catinfoss.get(count).getName(), catinfoss.get(count).getAIS().toIS()));
										count++;
									}
									if (count >= size) {
										break;
									}
								}
							}

							if ((pages > 0) && (pages != PMLocPage.get(p))
									&& (size - 28 * (PMLocPage.get(p) + 1) != 0)) {
								catsInv.setItem(53, APBuy.tagger.setNBTTag("ToPage", PMLocPage.get(p) + 1, new AIS(
										"§7" + Translator.translate("menu.page.next") + " " + (PMLocPage.get(p) + 1), 1,
										Material.PAPER).toIS()));
							}
							if (PMLocPage.get(p) > 0) {
								catsInv.setItem(45,
										APBuy.tagger
												.setNBTTag("ToPage",
														PMLocPage.get(p)
																- 1,
														new AIS("§7" + (PMLocPage.get(p) - 1) + " "
																+ Translator.translate("menu.page.previous"), 1,
																Material.PAPER).toIS()));
							}

							if (catsInv.getItem(31).getType() == Material.PAPER) {
								catsInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
							}
							onMarketVisualiser.remove(p);
							PMLocPage.remove(p);
							p.openInventory(catsInv);
							onMarketVisualiser.put(p, new String[] { "Cats",
									m.getMarketOwner() == null ? "AdminShop" : m.getMarketOwner() });
							PMLocPage.put(p, page);
						} catch (Exception e1) {
							e1.printStackTrace();
							System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
							p.closeInventory();
							APBuy.getMarketHandler().removeFromAll(p);
							p.sendMessage(Translator.translate("dev.error"));
							p.sendMessage("§cError Code: " + Utils.addToFix(e1));
						}
					}
				});
				break;
			case "Cat":
				int page1 = PMLocPage.get(p);
				CategoryInfos cat = m.getCatInfosByName(s.replaceFirst("Cat:", ""));
				Inventory catInv = Bukkit.createInventory(null, 54, m.getMarketOwner().equalsIgnoreCase("AdminShop")
						? "§0§lA§3§lP§r§8Buy - AdminShop" : "§0§lA§3§lP§r§8Buy - Markets");
				for (int i = 0; i < 54; i++) {
					if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
							|| (i == 49)) {
						continue;
					}
					catInv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				for (int i1 = 0; i1 < 4; i1++) {
					for (int i2 = 0; i2 < 7; i2++) {
						catInv.setItem(10 + i1 * 9 + i2,
								new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
					}
				}
				if (p.hasPermission("apb.mod.adminshop") && m.getMarketOwner().equals("AdminShop")) {
					catInv.setItem(50, APBuy.tagger.setNBTTag("AddItem", true,
							new AIS(Translator.translate("menu.inv.iteminput.additem"), 1, Material.CHEST).toIS()));
				}
				catInv.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
						.addLineToLore(Translator.translate("menu.openerror2")).toIS());
				p.openInventory(catInv);
				PMLocPage.put(p, page1);
				Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

					@Override
					public void run() {
						try {
							List<MarketItem> miss = m.getMarketItemsByCat(cat.getName());
							Iterator<MarketItem> iterator = miss.iterator();
							while (iterator.hasNext()) {
								if (!iterator.next().isBuyable()) {
									iterator.remove();
								}
							}
							// - Back Button 49
							catInv.setItem(49, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());

							// - Getting all Markets to display
							int size = miss.size();
							int pages = ((size - (size % 28)) / 28);
							int count = 28 * PMLocPage.get(p);
							if (size != 0) {
								for (int i1 = 0; i1 < 4; i1++) {
									for (int i2 = 0; i2 < 7; i2++) {
										if (count >= size) {
											break;
										}
										List<String> lore = new ArrayList<>();
										lore.add("");
										lore.add(Translator.translate("menu.inv.items.price") + ": "
												+ miss.get(count).getPrice() + "$");
										if (!uuid.equalsIgnoreCase("AdminShop")) {
											lore.add(Translator.translate("menu.inv.marketv.items.available") + ": "
													+ miss.get(count).getAmmount());
										}
										catInv.setItem(10 + i1 * 9 + i2, APBuy.tagger.setNBTTag("ToBuy", count,
												new AIS(miss.get(count).getIs().clone()).addToLore(lore).toIS()));
										count++;
									}
									if (count >= size) {
										break;
									}
								}
							}

							if ((pages > 0) && (pages != PMLocPage.get(p))
									&& (size - 28 * (PMLocPage.get(p) + 1) != 0)) {
								catInv.setItem(53, APBuy.tagger.setNBTTag("ToPage", PMLocPage.get(p) + 1, new AIS(
										"§7" + Translator.translate("menu.page.next") + " " + (PMLocPage.get(p) + 1), 1,
										Material.PAPER).toIS()));
							}
							if (PMLocPage.get(p) > 0) {
								catInv.setItem(45,
										APBuy.tagger
												.setNBTTag("ToPage",
														PMLocPage.get(p)
																- 1,
														new AIS("§7" + (PMLocPage.get(p) - 1) + " "
																+ Translator.translate("menu.page.previous"), 1,
																Material.PAPER).toIS()));
							}

							if (catInv.getItem(31).getType() == Material.PAPER) {
								catInv.setItem(31, new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
							}
							p.openInventory(catInv);
							onMarketVisualiser.put(p, new String[] { "Cat:" + cat.getName(), m.getMarketOwner() });
							PMLocPage.put(p, page1);
						} catch (Exception e1) {
							e1.printStackTrace();
							System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
							p.closeInventory();
							APBuy.getMarketHandler().removeFromAll(p);
							p.sendMessage(Translator.translate("dev.error"));
							p.sendMessage("§cError Code: " + Utils.addToFix(e1));
						}
					}
				});
				break;
			case "InvisSee":
				int page11 = PMLocPage.get(p);
				Inventory invInvisSee = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Markets");
				for (int i = 0; i < 54; i++) {
					if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
							|| (i == 49)) {
						continue;
					}
					invInvisSee.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				for (int i1 = 0; i1 < 4; i1++) {
					for (int i2 = 0; i2 < 7; i2++) {
						invInvisSee.setItem(10 + i1 * 9 + i2,
								new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
					}
				}
				invInvisSee.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
						.addLineToLore(Translator.translate("menu.openerror2")).toIS());
				p.openInventory(invInvisSee);
				PMLocPage.put(p, page11);
				Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

					@Override
					public void run() {
						try {
							List<MarketItem> miss = m.getMarketItems();
							Iterator<MarketItem> iterator = miss.iterator();
							while (iterator.hasNext()) {
								if (iterator.next().isBuyable()) {
									iterator.remove();
								}
							}
							// - Back Button 49
							invInvisSee.setItem(49,
									new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());

							// - Getting all Markets to display
							int size = miss.size();
							int pages = ((size - (size % 28)) / 28);
							int count = 28 * PMLocPage.get(p);
							if (size != 0) {
								for (int i1 = 0; i1 < 4; i1++) {
									for (int i2 = 0; i2 < 7; i2++) {
										if (count >= size) {
											break;
										}
										invInvisSee.setItem(10 + i1 * 9 + i2, miss.get(count).getAISToShow().toIS());
										count++;
									}
									if (count >= size) {
										break;
									}
								}
							}

							if ((pages > 0) && (pages != PMLocPage.get(p))
									&& (size - 28 * (PMLocPage.get(p) + 1) != 0)) {
								invInvisSee
										.setItem(53,
												APBuy.tagger
														.setNBTTag("ToPage", PMLocPage.get(p) + 1,
																new AIS("§7" + Translator.translate("menu.page.next")
																		+ " " + (PMLocPage.get(p) + 1), 1,
																		Material.PAPER).toIS()));
							}
							if (PMLocPage.get(p) > 0) {
								invInvisSee
										.setItem(45,
												APBuy.tagger
														.setNBTTag("ToPage",
																PMLocPage.get(p) - 1, new AIS(
																		"§7" + (PMLocPage.get(p) - 1) + " "
																				+ Translator.translate(
																						"menu.page.previous"),
																		1, Material.PAPER).toIS()));
							}

							if (invInvisSee.getItem(31).getType() == Material.PAPER) {
								invInvisSee.setItem(31,
										new AIS("§a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
							}
							p.openInventory(invInvisSee);
							onMarketVisualiser.put(p, new String[] { "InvisSee",
									m.getMarketOwner() == null ? "AdminShop" : m.getMarketOwner() });
							PMLocPage.put(p, page11);
						} catch (Exception e1) {
							e1.printStackTrace();
							System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
							p.closeInventory();
							APBuy.getMarketHandler().removeFromAll(p);
							p.sendMessage(Translator.translate("dev.error"));
							p.sendMessage("§cError Code: " + Utils.addToFix(e1));
						}
					}
				});
				break;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
			p.closeInventory();
			this.removeFromAll(p);
			p.sendMessage(Translator.translate("dev.error"));
			p.sendMessage("§cError Code: " + Utils.addToFix(e1));
		}
	}

	public void updateLists() {
		APBuy.database.updateList();
	}

	// public Market getMarketByPlayerName(String string) throws MarketException
	// {
	// String uuid = Utils.getUuid(string);
	// if (uuid != "error") {
	// return APBuy.database.loadByUUID(uuid);
	// }
	// return null;
	// }

	public boolean hasMarketByUUID(String uuid) {
		return APBuy.database.hasPlayerMarketByUUID(uuid);
	}

	public List<UUID> getAllMarketsOnline() throws MarketException {
		return APBuy.database.loadAllOnlineMarkets();
	}

	// public Market getMarketByUUID(String s) throws MarketException {
	// if (s == "AdminShop") {
	// return adminshop;
	// }
	// if (hasMarketByUUID(s)) {
	// // return
	// //
	// this.getMarketByFile(this.getPlayerMarketFile(Bukkit.getOfflinePlayer(UUID.fromString(s))));
	// return APBuy.database.loadByUUID(s);
	// }
	// throw new MarketException(ErrorCause.NOTFOUND);
	// }

	public void reopenMarketToWhoSee(String uuid) {
		if (hasMarketByUUID(uuid)) {
			for (Player p : onMarketVisualiser.keySet()) {
				if (onMarketVisualiser.get(p)[1].equalsIgnoreCase(uuid)) {
					if (!BuyManager.isBuying(p)) {
						openMarketVisualiserToPlayer(onMarketVisualiser.get(p)[0], uuid, p);
					}
				}
			}
		} else {
			for (Player p : onMarketVisualiser.keySet()) {
				if (onMarketVisualiser.get(p)[1].equalsIgnoreCase(uuid)) {
					onMarketVisualiser.remove(p);
					p.closeInventory();
					p.sendMessage(Translator.translate("misc.reload"));
				}
			}
		}
	}

	public void reopenMarketsToEveryone() {
		for (Player p : onMarketVisualiser.keySet()) {
			if (!BuyManager.isBuying(p)) {
				if (hasMarketByUUID(onMarketVisualiser.get(p)[1])) {
					openMarketVisualiserToPlayer(onMarketVisualiser.get(p)[0], onMarketVisualiser.get(p)[1], p);
				} else {
					onMarketVisualiser.remove(p);
					p.closeInventory();
					p.sendMessage(Translator.translate("misc.reload"));
				}
			}
		}
	}

	public int compareByUUID(String arg0, String arg1) throws MarketException {
		Long[] m1 = getMarketByUUIDSalesnSoldItms(arg0);
		Long[] m2 = getMarketByUUIDSalesnSoldItms(arg1);
		return m1[0] == m2[0] ? Long.compare(m1[1], m2[1]) : Long.compare(m1[0], m2[0]);
	}

	public Long[] getMarketByUUIDSalesnSoldItms(String s) throws MarketException {
		MarketInfos m = APBuy.database.getMarketInfos(s);
		return new Long[] { m.getSales(), m.getSoldItems() };
	}

	public void createAdminShopWhenNotExist() throws MarketException {
		if (hasMarketByUUID("AdminShop")) {
			// adminshop = APBuy.getMarketHandler().getMarketByFile(new
			// File("plugins/APBuy/Markets/Adminshop.yml"));
			adminshop = new Market("AdminShop", true);
		} else {
			adminshop = new Market("AdminShop", false);
			adminshop.saveMarketInfos();
		}
	}

	public void setAdminshop(Market market) {
		MarketHandler.adminshop = market;
	}

	public Market getAdminshop() {
		return MarketHandler.adminshop;
	}

	public Menu getMenuByPlayer(Player player) {
		for (Menu menu : menus) {
			if (menu.getPlayer().equals(player)) {
				return menu;
			}
		}
		return null;
	}

	public boolean performClick(InventoryClickEvent event) {
		Menu menu = getMenuByPlayer((Player) event.getWhoClicked());
		if(menu != null) {
			if(menu.onClick(event)) {
				event.setCancelled(true);
			}
			return true;
		}
		return false;
	}
}