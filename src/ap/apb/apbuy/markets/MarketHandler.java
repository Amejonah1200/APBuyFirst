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
import ap.apb.APBuyException;
import ap.apb.Translator;
import ap.apb.Utils;
import ap.apb.anvilgui.AnvilGUI;
import ap.apb.anvilgui.AnvilGUI.AnvilClickEvent;
import ap.apb.anvilgui.AnvilGUI.AnvilClickEventHandler;
import ap.apb.anvilgui.AnvilGUIObj.AnvilSlot;
import ap.apb.anvilgui.mc1_8.AnvilGUI_v1_8_R3;
import ap.apb.apbuy.BuyManager;
import ap.apb.apbuy.itoomel.Itoomel;
import ap.apb.apbuy.itoomel.ItoomelPrime;
import ap.apb.datamaster.Database;

public class MarketHandler implements Listener {

	public HashMap<Player, String> PMLoc = new HashMap<>();
	public HashMap<Player, Integer> PMLocPage = new HashMap<>();
	private HashMap<Player, CategoryInfos> creatingCat = new HashMap<>();
	private HashMap<Player, MarketItem> creatingIS = new HashMap<>();
	public HashMap<Player, String[]> onMarketVisualiser = new HashMap<>();
	public HashMap<Player, List<ItemStack>> onItemInput = new HashMap<>();
	public static Market adminshop = null;
	public boolean settedAdminShop = false;
	private Database database;

	public MarketHandler(Database database) {
		this.database = database;
	}

	public void openMainMenu(Player p) {
		try {
			Inventory inv = Bukkit.createInventory(null, 27, Translator.translate("menu.title.mainmenu"));
			for (int i = 0; i < 27; i++) {
				inv.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}

			if (!p.hasPermission("apb.mymarket.edit")) {
				inv.setItem(4, new AIS("�3�lMy Market", 1, Material.BARRIER).addToLore(Utils
						.createListFromStringToWidth(Translator.translate("menu.inv.mainmenu.norightsmymarket"), 25))
						.toIS());
			} else {
				inv.setItem(4, new AIS("�3�lMy Market", 1, Material.CHEST)
						.addLineToLore(Translator.translate("menu.inv.mainmenu.hereismymarket")).toIS());
			}
			if (!p.hasPermission("apb.markets.search")) {
				inv.setItem(10, new AIS("�3Markets", 1, Material.BARRIER).addToLore(Utils
						.createListFromStringToWidth(Translator.translate("menu.inv.mainmenu.norightsmarkets"), 25))
						.toIS());
			} else {
				inv.setItem(10, new AIS("�3Markets", 1, Material.BOOK).addToLore(
						Utils.createListFromStringToWidth(Translator.translate("menu.inv.mainmenu.hereismarkets"), 30))
						.toIS());
			}
			if (p.hasPermission("apb.markets.search")) {
				// - Top Market of The week 16
				ItemStack is3 = new AIS("�3Top Market", Material.NETHER_STAR)
						.addLineToLore(Translator.translate("menu.inv.mainmenu.hereistopmarket")).toIS();
				inv.setItem(16,
						YamlConfiguration.loadConfiguration(APBuy.plugin.getPlayerMarketStats())
								.getString("TopMarket") == null
										? is3
										: APBuy.tagger.setNBTTag("Market",
												YamlConfiguration.loadConfiguration(APBuy.plugin.getPlayerMarketStats())
														.getString("TopMarket"),
												is3));
			} else {
				inv.setItem(16, new AIS("�3Top Market", 1, Material.BARRIER).addToLore(Utils
						.createListFromStringToWidth(Translator.translate("menu.inv.mainmenu.norightstopmarket"), 30))
						.toIS());
			}
			// // - Help 16
			// ItemStack is4 = new ItemStack(Material.PAPER);
			// ItemMeta m4 = is4.getItemMeta();
			// m4.setDisplayName("�3Help");
			// List<String> s4 = new ArrayList<>();
			// s4.add("�r�fHier kannst du Hilfe suchen.");
			// m4.setLore(s4);
			// is4.setItemMeta(m4);
			// inv.setItem(22, is4);

			inv.setItem(23,
					new AIS("�7Commands", 1, Material.PAPER).addLineToLore("").addToLore(Utils
							.createListFromStringToWidth(Translator.translate("menu.inv.mainmenu.hereiscommands"), 40))
							.toIS());
			inv.setItem(21, new AIS(
					adminshop != null
							? adminshop.isOpen()
									? "�6AdminShop (�a" + Translator.translate("menu.inv.mainmenu.open") + "�6)"
									: "�6AdminShop (�c" + Translator.translate("menu.inv.mainmenu.close") + "�6)"
							: "�6AdminShop (�c" + Translator.translate("menu.inv.mainmenu.close") + "�6)",
					1, Material.INK_SACK).setDamage((short) (adminshop != null ? adminshop.isOpen() ? 2 : 1 : 1))
							.addLineToLore("").addLineToLore(Translator.translate("menu.inv.mainmenu.hereisadminshop"))
							.toIS());
			// if(settedAdminShop) {
			// inv.setItem(22, new AIS("�", 1, ));
			// }

			p.openInventory(inv);
			PMLoc.put(p, "MainMenu");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
			p.closeInventory();
			this.removeFromAll(p);
			p.sendMessage(Translator.translate("dev.error"));
			p.sendMessage("�cError Code: " + Utils.addToFix(e));
		}
	}

	public void openInvToP(String menu, Player p) {
		try {
			if (menu == "MainMenu") {
				this.openMainMenu(p);
				return;
			}
			if (menu.equalsIgnoreCase("cmds")) {
				Inventory inv = Bukkit.createInventory(null, 27, "�0�lA�3�lP�r�8Buy - Cmds");
				for (int i = 0; i < 27; i++) {
					inv.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				inv.setItem(22, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());

				if (p.hasPermission("apb.*") || p.hasPermission("apb.mymarket.*")
						|| p.hasPermission("apb.mymarket.edit") || p.hasPermission("apb.mymarket.setdevise")
						|| p.hasPermission("apb.mymarket.setname")) {
					AIS ais = new AIS("�7MyMarket", Material.PAPER);
					ais.addLineToLore("");
					if (p.hasPermission("apb.*") || p.hasPermission("apb.mymarket.*")
							|| p.hasPermission("apb.mymarket.edit")) {
						ais.addToLore(Utils.createListFromStringToWidthPlusEffect(
								Translator.translate("menu.inv.cmds.mymarket.create"), 30));
					}
					if (p.hasPermission("apb.*") || p.hasPermission("apb.mymarket.*")
							|| p.hasPermission("apb.mymarket.setname")) {
						ais.addLineToLore("�7- [/apb setname <Name>]")
								.addToLore(Utils.createListFromStringToWidthPlusEffect(
										Translator.translate("menu.inv.cmds.mymarket.name"), 30));
					}
					if (p.hasPermission("apb.*") || p.hasPermission("apb.mymarket.*")
							|| p.hasPermission("apb.mymarket.setdevise")) {
						ais.addLineToLore("�7- [/apb setdevise <Devise>]")
								.addToLore(Utils.createListFromStringToWidthPlusEffect(
										Translator.translate("menu.inv.cmds.mymarket.devise"), 30));
					}
					inv.setItem(10, ais.toIS());
				} else {
					inv.setItem(10, new AIS(Translator.translate("menu.inv.cmds.mymarket.norights"), 1, (short) 7,
							Material.STAINED_GLASS_PANE).toIS());
				}
				if (p.hasPermission("apb.*") || p.hasPermission("apb.markets.*") || p.hasPermission("apb.markets.buy")
						|| p.hasPermission("apb.markets.search")) {
					AIS ais = new AIS("�7Markets", Material.PAPER);
					ais.addLineToLore("");
					if (p.hasPermission("apb.*") || p.hasPermission("apb.mymarket.*")
							|| p.hasPermission("apb.markets.search")) {
						ais.addLineToLore(Translator.translate("menu.inv.cmds.markets.search.topic"))
								.addLineToLore("�7- [/apb open name <Spielername>]")
								.addToLore(Utils.createListFromStringToWidthPlusEffect(
										"�8   " + Translator.translate("menu.inv.cmds.markets.search.name"), 30))
								.addLineToLore("�7- [/apb open uuid <UUID>]")
								.addToLore(Utils.createListFromStringToWidth(
										"�8   " + Translator.translate("menu.inv.cmds.markets.search.uuid"), 30));
					}
					if (p.hasPermission("apb.*") || p.hasPermission("apb.mymarket.*")
							|| p.hasPermission("apb.markets.buy")) {
						ais.addLineToLore(Translator.translate("menu.inv.cmds.markets.buy"));
					}
					inv.setItem(12, ais.toIS());
				} else {
					inv.setItem(12, new AIS(Translator.translate("menu.inv.cmds.markets.norights"), 1, (short) 7,
							Material.STAINED_GLASS_PANE).toIS());
				}

				if (p.hasPermission("apb.*") || p.hasPermission("apb.itoomel")) {
					AIS ais = new AIS("�7Itoomel", Material.PAPER);
					ais.addLineToLore("");
					if (p.hasPermission("apb.*") || p.hasPermission("apb.itoomel")) {
						ais.addLineToLore("�7Du kannst Itoomel benutzen.");
					}
					inv.setItem(14, ais.toIS());
				} else {
					inv.setItem(14,
							new AIS("�cDu hast keine Itoomel-Rechte.", 1, (short) 7, Material.STAINED_GLASS_PANE)
									.toIS());
				}

				if (p.hasPermission("apb.*") || p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.delete")
						|| p.hasPermission("apb.mod.genstop") || p.hasPermission("apb.mod.itoomel")
						|| p.hasPermission("apb.mod.reset") || p.hasPermission("apb.mod.status")
						|| p.hasPermission("apb.mod.invissee") || p.hasPermission("apb.mod.adminshop")
				// || p.hasPermission("apb.mod.itemdepot")
				) {
					AIS ais = new AIS("�7Mod", Material.PAPER);
					ais.addLineToLore("");
					if (p.hasPermission("apb.*") || p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.delete")) {
						ais.addLineToLore(Translator.translate("menu.inv.cmds.mod.delete"));
					}
					if (p.hasPermission("apb.*") || p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.reset")) {
						ais.addLineToLore(Translator.translate("menu.inv.cmds.mod.reset"));
					}
					if (p.hasPermission("apb.*") || p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.reset")) {
						ais.addLineToLore(Translator.translate("menu.inv.cmds.mod.status"));
					}
					if (p.hasPermission("apb.*") || p.hasPermission("apb.mod.*")
							|| p.hasPermission("apb.mod.invissee")) {
						ais.addLineToLore(Translator.translate("menu.inv.cmds.mod.invissee"));
					}
					if (p.hasPermission("apb.*") || p.hasPermission("apb.mod.*")
							|| p.hasPermission("apb.mod.genstop")) {
						ais.addToLore(Utils.createListFromStringToWidthPlusEffect(
								Translator.translate("menu.inv.cmds.mod.genstop"), 30));
					}
					if (p.hasPermission("apb.*") || p.hasPermission("apb.mod.*")
							|| p.hasPermission("apb.mod.itemdepot")) {
						ais.addLineToLore(Translator.translate("menu.inv.cmds.mod.itemdepot"));
					}
					if (p.hasPermission("apb.*") || p.hasPermission("apb.mod.*")
							|| p.hasPermission("apb.mod.adminshop")) {
						ais.addLineToLore(Translator.translate("menu.inv.cmds.mod.adminshop.edit"))
								.addLineToLore("�7- [/apb adminshop open]")
								.addLineToLore("�8    " + Translator.translate("menu.inv.cmds.mod.adminshop.open"))
								.addLineToLore("�7- [/apb adminshop close]")
								.addLineToLore("�8    " + Translator.translate("menu.inv.cmds.mod.adminshop.close"));
					}
					inv.setItem(16, ais.toIS());
				} else {
					inv.setItem(16, new AIS(Translator.translate("menu.inv.cmds.mod.norights"), 1, (short) 7,
							Material.STAINED_GLASS_PANE).toIS());
				}

				p.openInventory(inv);
				PMLoc.put(p, "cmds");
				return;
			}
			switch (menu.split(Pattern.quote(":"))[0]) {
			case "MyMarket":
				switch (menu.split(Pattern.quote(":"))[1]) {
				case "Main":
					Inventory inv = Bukkit.createInventory(null, 54, "�0�lA�3�lP�r�8Buy - My Market");
					for (int i = 0; i < 54; i++) {
						inv.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
					}
					MarketInfos m = database.getMarketInfos(p.getUniqueId().toString());
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
					nNdlist.add(Translator.translate("menu.inv.mymarket.main.nnd.name") + ": �6" + (m.getName() == null
							? Translator.translate("menu.inv.mymarket.main.nnd.notset") : m.getName()));
					nNdlist.add(
							Translator.translate("menu.inv.mymarket.main.nnd.devise") + ": �6" + (m.getDevise() == null
									? Translator.translate("menu.inv.mymarket.main.nnd.notset") : m.getDevise()));
					nNdlist.add("");
					nNdlist.add(Translator.translate("menu.inv.mymarket.main.nnd.howto") + ":");
					nNdlist.add("�8   /mr setName <MarketName>");
					nNdlist.add("�8   /mr setDevise <MarketDevise>");
					nNdlist.add("�8   /mr resetName");
					nNdlist.add("�8   /mr resetDevise");
					nNdmeta.setLore(nNdlist);
					nameNdevise.setItemMeta(nNdmeta);
					inv.setItem(16, nameNdevise);

					// - My Market Editor 12
					inv.setItem(12,
							new AIS(Material.CHEST).addLineToLore("")
									.addLineToLore(Translator.translate("menu.inv.mymarket.main.mymarket"))
									.setName("�7My Market").toIS());

					// - Back Button 49
					inv.setItem(49, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());

					// - Item Input 30
					inv.setItem(31,
							new AIS(Translator.translate("menu.inv.mymarket.main.iteminput.title"), 1, Material.HOPPER)
									.addLineToLore("").addToLore(Utils.createListFromStringToWidth(
											Translator.translate("menu.inv.mymarket.main.iteminput.desc"), 40))
									.toIS());

					if (ItemDepot.getInstance().hasItemDepot(p)) {
						inv.setItem(28,
								new AIS(Material.CHEST).setName("�7Item Depot").addLineToLore("")
										.addToLore(Utils.createListFromStringToWidth(
												Translator.translate("menu.inv.mymarket.main.itemdepot.desc"), 40))
										.toIS());
					}

					p.openInventory(inv);
					PMLoc.put(p, "MyMarket:Main");
					break;
				case "Editor":
					Inventory inv1 = Bukkit.createInventory(null, 54, "�0�lA�3�lP�r�8Buy - Editor");
					if (menu.split(Pattern.quote(":")).length == 3) {
						if (menu.split(Pattern.quote(":"))[2].contains("Add")) {
							for (int i = 0; i < 54; i++) {
								inv1.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
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
								inv1.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
							}
							for (int i1 = 0; i1 < 4; i1++) {
								for (int i2 = 0; i2 < 7; i2++) {
									inv1.setItem(10 + i1 * 9 + i2,
											new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
										List<CategoryInfos> catinfoss = database
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
															new AIS("�7" + Translator.translate("menu.page.next") + " "
																	+ (PMLocPage.get(p) + 1), 1, Material.PAPER)
																			.toIS()));
										}
										if (PMLocPage.get(p) > 0) {
											inv1.setItem(45,
													APBuy.tagger
															.setNBTTag("ToPage",
																	PMLocPage.get(p) - 1, new AIS(
																			"�7" + (PMLocPage.get(p) - 1) + " "
																					+ Translator.translate(
																							"menu.page.previous"),
																			1, Material.PAPER).toIS()));
										}

										if (inv1.getItem(31).getType() == Material.PAPER) {
											inv1.setItem(31,
													new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
										p.sendMessage("�cError Code: " + Utils.addToFix(e));
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
							inv1.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
						}
						for (int i1 = 0; i1 < 4; i1++) {
							for (int i2 = 0; i2 < 7; i2++) {
								inv1.setItem(10 + i1 * 9 + i2,
										new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
									List<CategoryInfos> catinfoss = database
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
																new AIS("�7" + Translator.translate("menu.page.next")
																		+ " " + (PMLocPage.get(p) + 1), 1,
																		Material.PAPER).toIS()));
									}
									if (PMLocPage.get(p) > 0) {
										inv1.setItem(45,
												APBuy.tagger
														.setNBTTag("ToPage",
																PMLocPage.get(p) - 1, new AIS(
																		"�7" + (PMLocPage.get(p) - 1) + " "
																				+ Translator.translate(
																						"menu.page.previous"),
																		1, Material.PAPER).toIS()));
									}
									if (inv1.getItem(31).getType() == Material.PAPER) {
										inv1.setItem(31,
												new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
									p.sendMessage("�cDas hier dem Dev sagen : " + Utils.addToFix(e));
								}
							}
						});
					}
					break;
				case "ItemInput":
					List<ItemStack> iss = onItemInput.get(p);
					onItemInput.remove(p);
					Inventory inv2 = Bukkit.createInventory(null, 54, "�0�lA�3�lP�r�8Buy - Editor");
					for (int i = 0; i < 54; i++) {
						if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
								|| (i == 49)) {
							continue;
						}
						inv2.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
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
				Inventory inv2 = Bukkit.createInventory(null, 54, "�0�lA�3�lP�r�8Buy - Top Markets");
				for (int i = 0; i < 54; i++) {
					if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
							|| (i == 49)) {
						continue;
					}
					inv2.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				for (int i1 = 0; i1 < 4; i1++) {
					for (int i2 = 0; i2 < 7; i2++) {
						inv2.setItem(10 + i1 * 9 + i2, new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
														database.getMarketInfos(s.replaceAll(Pattern.quote(".yml"), ""))
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
										"�7" + Translator.translate("menu.page.next") + " " + (PMLocPage.get(p) + 1), 1,
										Material.PAPER).toIS()));
							}
							if (PMLocPage.get(p) > 0) {
								inv2.setItem(45,
										APBuy.tagger
												.setNBTTag("ToPage",
														PMLocPage.get(p)
																- 1,
														new AIS("�7" + (PMLocPage.get(p) - 1) + " "
																+ Translator.translate("menu.page.previous"), 1,
																Material.PAPER).toIS()));
							}

							if (inv2.getItem(31).getType() == Material.PAPER) {
								inv2.setItem(31, new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
							p.sendMessage("�cError Code: " + Utils.addToFix(e));
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
			p.sendMessage("�cError Code: " + Utils.addToFix(e));
		}
	}

	public List<UUID> getTopMarkets() throws APBuyException {
		List<UUID> uuids = database.loadAllOnlineMarkets();
		uuids.sort(new Comparator<UUID>() {
			@Override
			public int compare(UUID o1, UUID o2) {
				try {
					return compareByUUID(o1.toString(), o2.toString());
				} catch (APBuyException e) {
					return 0;
				}
			}
		});
		return uuids;
	}

	public void removeFromAll(Player p) {
		if (PMLoc.containsKey(p) || creatingIS.containsKey(p) || creatingCat.containsKey(p)
				|| onMarketVisualiser.containsKey(p) || onItemInput.containsKey(p)
				|| ItoomelPrime.onItoomel.containsKey(p) || BuyManager.isBuying(p)
				|| Itoomel.getInstance().isInNav(p)) {
			PMLoc.remove(p);
			PMLocPage.remove(p);
			creatingCat.remove(p);
			creatingIS.remove(p);
			onMarketVisualiser.remove(p);
			onItemInput.remove(p);
			ItoomelPrime.onItoomel.remove(p);
			BuyManager.removeBuyer(p);
			p.closeInventory();
			Itoomel.getInstance().removeFromNav(p);
		}
	}

	public void openItemEditor(String Cat, String menu, Player p) {
		try {
			switch (menu) {
			case "Main":
				Inventory MainInv = Bukkit.createInventory(null, 54, "�0�lA�3�lP�r�8Buy - My Market");
				for (int i = 0; i < 54; i++) {
					if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
							|| (i == 49)) {
						continue;
					}
					MainInv.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				for (int i1 = 0; i1 < 4; i1++) {
					for (int i2 = 0; i2 < 7; i2++) {
						MainInv.setItem(10 + i1 * 9 + i2,
								new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
						List<MarketItem> miss;
						try {
							miss = database.getMarketItemsFromMarket(p.getUniqueId().toString());
						} catch (APBuyException e) {
							e.printStackTrace();
							miss = new ArrayList<>();
						}
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
									"�7" + Translator.translate("menu.page.next") + " " + (PMLocPage.get(p) + 1), 1,
									Material.PAPER).toIS()));
						}
						if (PMLocPage.get(p) > 0) {
							MainInv.setItem(45,
									APBuy.tagger
											.setNBTTag("ToPage",
													PMLocPage.get(p)
															- 1,
													new AIS("�7" + (PMLocPage.get(p) - 1) + " "
															+ Translator.translate("menu.page.previous"), 1,
															Material.PAPER).toIS()));
						}

						if (MainInv.getItem(31).getType() == Material.PAPER) {
							MainInv.setItem(31, new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
						}
						PMLoc.put(p, "MyMarket:ItemEditor:Main:" + Cat + ":Opened");
						p.openInventory(MainInv);
						PMLoc.put(p, "MyMarket:ItemEditor:Main:" + Cat);
					}
				});
				break;
			case "Add":
				Inventory AddInv = Bukkit.createInventory(null, 54, "�0�lA�3�lP�r�8Buy - My Market");
				for (int i = 0; i < 54; i++) {
					AddInv.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				AddInv.setItem(40,
						creatingIS.get(p).getIs() == null
								? new AIS(Translator.translate("menu.inv.items.noitem.title"), 1, Material.CHEST)
										.addLineToLore("")
										.addLineToLore(Translator.translate("menu.inv.items.noitem.desc")).toIS()
								: creatingIS.get(p).getIs());
				AddInv.setItem(10, new AIS("�c-1000$", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(11, new AIS("�c-100$", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(12, new AIS("�e-10$", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(13,
						new AIS(Translator.translate("menu.inv.items.price") + ": " + creatingIS.get(p).getPrice(), 1,
								(creatingIS.get(p).getIs() == null ? Material.CHEST
										: creatingIS.get(p).getIs().getType()))
												.setDamage(creatingIS.get(p).getIs() == null ? 0
														: creatingIS.get(p).getIs().getDurability())
												.toIS());
				AddInv.setItem(14, new AIS("�a+10$", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(15, new AIS("�2+100$", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(16, new AIS("�2+1000$", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(19, new AIS("�c-64", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(20, new AIS("�c-16", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(21, new AIS("�e-1", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(22, new AIS(
						Translator.translate("menu.inv.items.ppersell") + ": " + creatingIS.get(p).getSellAmmount(), 1,
						(creatingIS.get(p).getIs() == null ? Material.CHEST : creatingIS.get(p).getIs().getType()))
								.setDamage(creatingIS.get(p).getIs() == null ? 0
										: creatingIS.get(p).getIs().getDurability())
								.toIS());
				AddInv.setItem(23, new AIS("�a+1", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(24, new AIS("�2+16", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
				AddInv.setItem(25, new AIS("�2+64", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());
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
					Inventory AddInv1 = Bukkit.createInventory(null, 54, "�0�lA�3�lP�r�8Buy - My Market");
					for (int i = 0; i < 54; i++) {
						AddInv1.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
					}
					AddInv1.setItem(40, creatingIS.get(p).getIs());
					AddInv1.setItem(10, new AIS("�c-1000$", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(11, new AIS("�c-100$", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(12, new AIS("�e-10$", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(13,
							new AIS(Translator.translate("menu.inv.items.price") + ": " + creatingIS.get(p).getPrice(),
									1,
									creatingIS.get(p).getIs() == null ? Material.CHEST
											: creatingIS.get(p).getIs().getType())
													.setDamage(creatingIS.get(p).getIs() == null ? 0
															: creatingIS.get(p).getIs().getMaxStackSize() != 1
																	? creatingIS.get(p).getIs().getDurability() : 0)
													.toIS());
					AddInv1.setItem(14, new AIS("�a+10$", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(15, new AIS("�2+100$", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(16, new AIS("�2+1000$", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(19, new AIS("�c-64", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(20, new AIS("�c-16", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(21, new AIS("�e-1", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(22, new AIS(
							Translator.translate("menu.inv.items.ppersell") + ": " + creatingIS.get(p).getSellAmmount(),
							1, creatingIS.get(p).getIs() == null ? Material.CHEST : creatingIS.get(p).getIs().getType())
									.setDamage(creatingIS.get(p).getIs() == null ? 0
											: creatingIS.get(p).getIs().getMaxStackSize() != 1
													? creatingIS.get(p).getIs().getDurability() : 0)
									.toIS());
					AddInv1.setItem(23, new AIS("�a+1", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(24, new AIS("�2+16", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
					AddInv1.setItem(25, new AIS("�2+64", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());

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
			p.sendMessage("�cError Code: " + Utils.addToFix(e));
		}
	}

	// public Market getMarketByPlayer(OfflinePlayer offlinePlayer) throws
	// APBuyException {
	// try {
	// // return
	// // this.getMarketByFile(this.getPlayerMarketFile(offlinePlayer));
	// return database.loadByUUID(offlinePlayer.getUniqueId().toString());
	// } catch (APBuyException e) {
	// return this.createNewMarketForP(offlinePlayer);
	// }
	// }

	public Market createNewMarketForP(String uuid) throws APBuyException {
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
		if (PMLoc.containsKey(e.getPlayer())) {
			if (PMLoc.get(e.getPlayer()).endsWith("Opened")) {
				return;
			}
		}
		if (onMarketVisualiser.containsKey(e.getPlayer())) {
			onMarketVisualiser.remove(e.getPlayer());
			PMLocPage.remove(e.getPlayer());
		}
		if (BuyManager.isBuying((Player) e.getPlayer())) {
			e.getPlayer().sendMessage(Translator.translate("close.buycanceled"));
			BuyManager.removeBuyer((Player) e.getPlayer());
		}
		if (ItoomelPrime.onItoomel.containsKey(e.getPlayer())) {
			ItoomelPrime.onItoomel.remove(e.getPlayer());
			PMLocPage.remove(e.getPlayer());
			return;
		}
		if (creatingIS.containsKey(e.getPlayer())) {
			e.getPlayer().sendMessage(Translator.translate("close.itemcreate"));
			creatingIS.remove(e.getPlayer());
			PMLoc.remove(e.getPlayer());
			PMLocPage.remove(e.getPlayer());
			return;
		}
		if (creatingCat.containsKey(e.getPlayer())) {
			e.getPlayer().sendMessage(Translator.translate("close.catcreate"));
			creatingCat.remove(e.getPlayer());
			PMLoc.remove(e.getPlayer());
			PMLocPage.remove(e.getPlayer());
			return;
		}
		if (PMLoc.containsKey(e.getPlayer())) {
			PMLoc.remove(e.getPlayer());
			PMLocPage.remove(e.getPlayer());
			return;
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
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
						if (database.getMarketItemByIS(p.getUniqueId().toString(), e.getCurrentItem()) != null) {
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
							if (database.getMarketItemByIS(p.getUniqueId().toString(), e.getCurrentItem()) != null) {
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
				if (menu == "MainMenu") {
					switch (e.getSlot()) {
					case 4:
						if (e.getCurrentItem().getType() != Material.BARRIER) {
							this.openInvToP("MyMarket:Main", p);
						} else {
							p.sendMessage(Translator.translate("click.norights"));
						}
						break;
					case 10:
						if (e.getCurrentItem().getType() != Material.BARRIER) {
							this.openInvToP("Markets:Opened", p);
							PMLocPage.put(p, 0);
						} else {
							p.sendMessage(Translator.translate("click.norights"));
						}
						break;
					case 16:
						if (e.getCurrentItem().getType() != Material.BARRIER) {
							// if (APBuy.tagger.hasTag("Market",
							// e.getCurrentItem())) {
							// PMLoc.remove(p);
							// openMarketVisualiserToPlayer("Main",
							// database.getTopMarketsUUIDs(1).get(0).toString(),
							// p);
							// }
						} else {
							// if(BanManager.isNowBanned(p)) {
							// BanManager.getBanByPlayer(p).sayMessage(p);
							// } else {
							p.sendMessage(Translator.translate("click.norights"));
							// }
						}
						break;
					case 21:
						if (e.getCurrentItem().getDurability() == 1) {
							p.sendMessage(Translator.translate("click.adminsclose"));
						} else {
							PMLocPage.put(p, 0);
							openMarketVisualiserToPlayer("Cats", "AdminShop", p);
						}
						break;
					case 23:
						this.openInvToP("cmds", p);
						break;
					}
					return;
				} else if (menu.equalsIgnoreCase("cmds")) {
					if (e.getSlot() == 22) {
						this.openInvToP("MainMenu", p);
						return;
					}
				} else if (menu == "MyMarket:Main") {
					switch (e.getSlot()) {
					case 10:
						MarketInfos m = database.getMarketInfos(p.getUniqueId().toString());
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
					case 28:
						if (e.getCurrentItem().getType() == Material.CHEST) {
							ItemDepot.getInstance().openItemDepot(p, 0);
						}
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
									database.removeCategory(p.getUniqueId().toString(),
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
							if (!database.hasCategoryInfos(p.getUniqueId().toString(), c.getName())) {
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
									if (database.hasMarketItem(p.getUniqueId().toString(),
											e.getClickedInventory().getContents()[i])) {
										mis = database.getMarketItemByIS(p.getUniqueId().toString(),
												e.getClickedInventory().getContents()[i]);
										mis.setAmmount(mis.getAmmount()
												+ e.getClickedInventory().getContents()[i].getAmount());
										mis.save();
										// ItoomelPrime.replaceMISInItoomel(mis);
										Itoomel.getInstance().updateMis(mis);
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
											database.removeItem(p.getUniqueId().toString(), is.getIs());
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
									MarketItem mis = m.getMarketItemByIS(APBuy.tagger.removeNBTTag("Item",
											new AIS(e.getCurrentItem().clone()).removeLatestLore(4).toIS()));
									if (mis != null) {
										if (Utils.getPlaceForIS(p, mis.getAmmount(), mis.getIs()) != 0) {
											int count = Utils.getPlaceForIS(p, mis.getAmmount(), mis.getIs());
											Utils.addItemToPlayer(p, mis.getIs().clone(), count);
											m.removeItem(mis.getIs().clone(), count);
											ItoomelPrime.removeMISFromItoomel(mis.getIs(), mis.getMarketuuid());
											Itoomel.getInstance().updateMis(m.getMarketItemByIS(mis.getIs()));
											this.openItemEditor(
													menu.endsWith(":Opened")
															? menu.substring(0, menu.length() - 7)
																	.replaceFirst("MyMarket:ItemEditor:Main:", "")
															: menu.replaceFirst("MyMarket:ItemEditor:Main:", ""),
													"Main", p);
										} else {
											if (mis.getAmmount() == 0) {
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
												ItoomelPrime.removeMISFromItoomel(is.getIs(), is.getMarketuuid());
												Itoomel.getInstance().removeMisByMarketNIS(is.getIs(),
														is.getMarketuuid());
											} else {
												Itoomel.getInstance().updateMis(m.getMarketItemByIS(is.getIs()));
											}
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
									database.removeItem("AdminShop", APBuy.tagger.removeNBTTag("ToBuy",
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
								1, (Player) e.getWhoClicked(), false, onMarketVisualiser.get(p))) {
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
							case 29:
								if (p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.itemdepot")) {
									String uuid = onMarketVisualiser.get(p)[1];
									p.closeInventory();
									p.sendMessage(Translator.translate("itemdepot.trans.doing",
											new Object[] { Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() }));
									ItemDepot.getInstance().transferMarketToItemDepot(uuid);
									database.deleteMarket(UUID.fromString(uuid));
									Itoomel.getInstance().removeMarket(uuid);
									p.sendMessage(Translator.translate("itemdepot.trans.done",
											new Object[] { Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() }));
								}
								break;
							case 28:
								if (p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.status")) {
									database.getMarketInfos((String) onMarketVisualiser.get(p)[1]).setOpen(
											e.getCurrentItem().getItemMeta().getDisplayName().contains("geschlossen"))
											.save();
									openMarketVisualiserToPlayer("Main", ((String) onMarketVisualiser.get(p)[1]), p);
								}
								break;
							case 30:
								if (p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.reset")) {
									database.getMarketInfos((String) onMarketVisualiser.get(p)[1]).resetStats();
									openMarketVisualiserToPlayer("Main", ((String) onMarketVisualiser.get(p)[1]), p);
								}
								break;
							case 32:
								if (p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.delete")) {
									database.deleteMarket(UUID.fromString(((String) onMarketVisualiser.get(p)[1])));
									ItoomelPrime.removeMarketFromItoomel(
											UUID.fromString(((String) onMarketVisualiser.get(p)[1])));
									ItoomelPrime.reopenItoomelToEveryone();
									reopenMarketsToEveryone();
									p.closeInventory();
								}
								break;
							case 34:
								PMLocPage.put(p, 0);
								openMarketVisualiserToPlayer("InvisSee", ((String) onMarketVisualiser.get(p)[1]), p);
								break;
							}
						} catch (APBuyException e1) {
							switch (e1.getErrorCause()) {
							case NOTFOUND_MARKET:
								p.sendMessage(Translator.translate("click.notfoundmarket"));
								break;
							case NOTFOUND_CAT:
								p.sendMessage(Translator.translate("click.notfoundcat"));
								break;
							case SQL:
								p.sendMessage("�cEs gab ein Fehler beim abfragen bei der Datenbank (SQL Fehler).");
								break;
							default:
							case NPE:
								e1.printStackTrace();
								System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
								System.out.println("Slot: " + e.getSlot());
								break;
							}
							this.removeFromAll(p);
							p.sendMessage(Translator.translate("dev.error"));
							p.sendMessage("�cError Code: " + Utils.addToFix(e1));
						}
					}

				}
			}
		} catch (APBuyException e1) {
			switch (e1.getErrorCause()) {
			case NOTFOUND_MARKET:
				p.sendMessage(Translator.translate("click.notfoundmarket"));
				this.removeFromAll(p);
				p.closeInventory();
				return;
			case SQL:
				p.sendMessage("�cEs gab ein Fehler beim abfragen bei der Datenbank (SQL Fehler).");
				break;
			case NOTFOUND_CAT:
				p.sendMessage(Translator.translate("click.notfoundcat"));
				this.removeFromAll(p);
				p.closeInventory();
				this.openInvToP("Markets", p);
				return;
			case NPE:
				e1.printStackTrace();
				System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
				System.out.println("Slot: " + e.getSlot());
				p.closeInventory();
				this.removeFromAll(p);
				p.sendMessage(Translator.translate("dev.error"));
				p.sendMessage("�cError Code: " + Utils.addToFix(e1));
				break;
			default:
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
			p.sendMessage("�cError Code: " + Utils.addToFix(e1));
		}
	}

	private void openAdminShopInv(String menu, Player p) {
		if (menu.equalsIgnoreCase("AddCat")) {
			CategoryInfos cat = creatingCat.get(p);
			Inventory inv1 = Bukkit.createInventory(null, 54, "�0�lA�3�lP�r�8Buy - AdminShop");
			for (int i = 0; i < 54; i++) {
				inv1.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
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
			Inventory AddInv = Bukkit.createInventory(null, 54, "�0�lA�3�lP�r�8Buy - AdminShop");
			for (int i = 0; i < 54; i++) {
				AddInv.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			AddInv.setItem(40, mis.getIs() == null
					? new AIS(Translator.translate("menu.inv.items.noitem.title"), 1, Material.CHEST).addLineToLore("")
							.addLineToLore(Translator.translate("menu.inv.items.noitem.desc")).toIS()
					: mis.getIs());
			AddInv.setItem(10, new AIS("�c-1000$", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(11, new AIS("�c-100$", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(12, new AIS("�e-10$", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(13, new AIS(Translator.translate("menu.inv.items.price") + ": " + mis.getPrice(), 1,
					(mis.getIs() == null ? Material.CHEST : mis.getIs().getType())).toIS());
			AddInv.setItem(14, new AIS("�a+10$", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(15, new AIS("�2+100$", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(16, new AIS("�2+1000$", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(19, new AIS("�c-64", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(20, new AIS("�c-16", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(21, new AIS("�e-1", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(22, new AIS(Translator.translate("menu.inv.items.ppersell") + ": " + mis.getSellAmmount(), 1,
					(mis.getIs() == null ? Material.CHEST : mis.getIs().getType())).toIS());
			AddInv.setItem(23, new AIS("�a+1", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(24, new AIS("�2+16", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
			AddInv.setItem(25, new AIS("�2+64", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());
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
					Inventory MVMainInv = Bukkit.createInventory(null, 54, "�0�lA�3�lP�r�8Buy - Markets");
					for (int i = 0; i < 54; i++) {
						MVMainInv.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
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
					if (p.hasPermission("apb.mod.*") || p.hasPermission("apb.mod.itemdepot")) {
						MVMainInv.setItem(29, new AIS(Translator.translate("menu.inv.marketv.main.mod.itemdepot"),
								Material.STORAGE_MINECART).toIS());
					}
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
					Inventory MVMainInv = Bukkit.createInventory(null, 27, "�0�lA�3�lP�r�8Buy - Markets");
					for (int i = 0; i < 27; i++) {
						MVMainInv.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
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
						? "�0�lA�3�lP�r�8Buy - AdminShop" : "�0�lA�3�lP�r�8Buy - Markets");
				for (int i = 0; i < 54; i++) {
					if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
							|| (i == 49)) {
						continue;
					}
					catsInv.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				for (int i1 = 0; i1 < 4; i1++) {
					for (int i2 = 0; i2 < 7; i2++) {
						catsInv.setItem(10 + i1 * 9 + i2,
								new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
										"�7" + Translator.translate("menu.page.next") + " " + (PMLocPage.get(p) + 1), 1,
										Material.PAPER).toIS()));
							}
							if (PMLocPage.get(p) > 0) {
								catsInv.setItem(45,
										APBuy.tagger
												.setNBTTag("ToPage",
														PMLocPage.get(p)
																- 1,
														new AIS("�7" + (PMLocPage.get(p) - 1) + " "
																+ Translator.translate("menu.page.previous"), 1,
																Material.PAPER).toIS()));
							}

							if (catsInv.getItem(31).getType() == Material.PAPER) {
								catsInv.setItem(31, new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
							p.sendMessage("�cError Code: " + Utils.addToFix(e1));
						}
					}
				});
				break;
			case "Cat":
				int page1 = PMLocPage.get(p);
				CategoryInfos cat = m.getCatInfosByName(s.replaceFirst("Cat:", ""));
				Inventory catInv = Bukkit.createInventory(null, 54, m.getMarketOwner().equalsIgnoreCase("AdminShop")
						? "�0�lA�3�lP�r�8Buy - AdminShop" : "�0�lA�3�lP�r�8Buy - Markets");
				for (int i = 0; i < 54; i++) {
					if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
							|| (i == 49)) {
						continue;
					}
					catInv.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				for (int i1 = 0; i1 < 4; i1++) {
					for (int i2 = 0; i2 < 7; i2++) {
						catInv.setItem(10 + i1 * 9 + i2,
								new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
										"�7" + Translator.translate("menu.page.next") + " " + (PMLocPage.get(p) + 1), 1,
										Material.PAPER).toIS()));
							}
							if (PMLocPage.get(p) > 0) {
								catInv.setItem(45,
										APBuy.tagger
												.setNBTTag("ToPage",
														PMLocPage.get(p)
																- 1,
														new AIS("�7" + (PMLocPage.get(p) - 1) + " "
																+ Translator.translate("menu.page.previous"), 1,
																Material.PAPER).toIS()));
							}

							if (catInv.getItem(31).getType() == Material.PAPER) {
								catInv.setItem(31, new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
							p.sendMessage("�cError Code: " + Utils.addToFix(e1));
						}
					}
				});
				break;
			case "InvisSee":
				int page11 = PMLocPage.get(p);
				Inventory invInvisSee = Bukkit.createInventory(null, 54, "�0�lA�3�lP�r�8Buy - Markets");
				for (int i = 0; i < 54; i++) {
					if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
							|| (i == 49)) {
						continue;
					}
					invInvisSee.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
				}
				for (int i1 = 0; i1 < 4; i1++) {
					for (int i2 = 0; i2 < 7; i2++) {
						invInvisSee.setItem(10 + i1 * 9 + i2,
								new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
																new AIS("�7" + Translator.translate("menu.page.next")
																		+ " " + (PMLocPage.get(p) + 1), 1,
																		Material.PAPER).toIS()));
							}
							if (PMLocPage.get(p) > 0) {
								invInvisSee
										.setItem(45,
												APBuy.tagger
														.setNBTTag("ToPage",
																PMLocPage.get(p) - 1, new AIS(
																		"�7" + (PMLocPage.get(p) - 1) + " "
																				+ Translator.translate(
																						"menu.page.previous"),
																		1, Material.PAPER).toIS()));
							}

							if (invInvisSee.getItem(31).getType() == Material.PAPER) {
								invInvisSee.setItem(31,
										new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
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
							p.sendMessage("�cError Code: " + Utils.addToFix(e1));
						}
					}
				});
				break;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
			this.removeFromAll(p);
			p.sendMessage(Translator.translate("dev.error"));
			p.sendMessage("�cError Code: " + Utils.addToFix(e1));
		}
	}

	// public Market getMarketByPlayerName(String string) throws APBuyException
	// {
	// String uuid = Utils.getUuid(string);
	// if (uuid != "error") {
	// return database.loadByUUID(uuid);
	// }
	// return null;
	// }

	public boolean hasMarketByUUID(String uuid) throws APBuyException {
		return database.hasPlayerMarketByUUID(uuid);
	}

	public List<UUID> getAllMarketsOnline() throws APBuyException {
		return database.loadAllOnlineMarkets();
	}

	// public Market getMarketByUUID(String s) throws APBuyException {
	// if (s == "AdminShop") {
	// return adminshop;
	// }
	// if (hasMarketByUUID(s)) {
	// // return
	// //
	// this.getMarketByFile(this.getPlayerMarketFile(Bukkit.getOfflinePlayer(UUID.fromString(s))));
	// return database.loadByUUID(s);
	// }
	// throw new APBuyException(ErrorCause.NOTFOUND);
	// }

	public void reopenMarketToWhoSee(String uuid) throws APBuyException {
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

	public void reopenMarketsToEveryone() throws APBuyException {
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

	public int compareByUUID(String arg0, String arg1) throws APBuyException {
		Long[] m1 = getMarketByUUIDSalesnSoldItms(arg0);
		Long[] m2 = getMarketByUUIDSalesnSoldItms(arg1);
		return m1[0] == m2[0] ? Long.compare(m1[1], m2[1]) : Long.compare(m1[0], m2[0]);
	}

	public Long[] getMarketByUUIDSalesnSoldItms(String s) throws APBuyException {
		MarketInfos m = database.getMarketInfos(s);
		return new Long[] { m.getSales(), m.getSoldItems() };
	}

	public void createAdminShopWhenNotExist() throws APBuyException {
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
}