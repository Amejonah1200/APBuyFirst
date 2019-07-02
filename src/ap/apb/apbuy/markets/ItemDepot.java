package ap.apb.apbuy.markets;

import ap.apb.*;
import ap.apb.datamaster.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemDepot implements Listener {

	private HashMap<Player, Integer> whoOpened = new HashMap<>();
	private Database database;

	public ItemDepot(Database db) {
		this.database = db;
	}

	public void openItemDepot(Player p, int page) {
		Inventory depotInv = Bukkit.createInventory(null, 54, "�0�lA�3�lP�r�8Buy - ItemDepot");
		for (int i = 0; i < 54; i++) {
			if ((10 <= i && i <= 16) || (19 <= i && i <= 25) || (28 <= i && i <= 34) || (37 <= i && i <= 43)
					|| (i == 49)) {
				continue;
			}
			depotInv.setItem(i, new AIS("�a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
		}
		for (int i1 = 0; i1 < 4; i1++) {
			for (int i2 = 0; i2 < 7; i2++) {
				depotInv.setItem(10 + i1 * 9 + i2, new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
			}
		}
		depotInv.setItem(31, new AIS(Translator.translate("menu.openerror1"), 1, Material.PAPER)
				.addLineToLore(Translator.translate("menu.openerror2")).toIS());
		whoOpened.remove(p);
		p.openInventory(depotInv);
		whoOpened.put(p, page);
		Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {

			@Override
			public void run() {
				try {
					HashMap<ItemStack, String[]> hmDepot = database
							.getItemsFromItemDepot(p.getUniqueId().toString());
					List<ItemStack> iss = new ArrayList<>();
					iss.addAll(hmDepot.keySet());
					// - Back Button 49
					depotInv.setItem(49, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());
					// - Getting all Markets to display
					int size = iss.size();
					int pages = ((size - (size % 28)) / 28);
					int pagea = page;
					int count = 28 * pagea;
					if (size != 0) {
						for (int i1 = 0; i1 < 4; i1++) {
							for (int i2 = 0; i2 < 7; i2++) {
								if (count >= size) {
									break;
								}
								depotInv.setItem(10 + i1 * 9 + i2, APBuy.tagger.setNBTTag("Depot", true,
										new AIS(iss.get(count).clone()).addLineToLore("")
												.addLineToLore("�7Stored Amount: " + hmDepot.get(iss.get(count))[1])
												.toIS()));
								count++;
							}
							if (count >= size) {
								break;
							}
						}
					}

					if ((pages > 0) && (pages != pagea) && (size - 28 * (pagea + 1) != 0)) {
						depotInv.setItem(53,
								APBuy.tagger.setNBTTag("ToPage", pagea + 1,
										new AIS("�7" + Translator.translate("menu.page.next") + " " + (pagea + 1), 1,
												Material.PAPER).toIS()));
					}
					if (pagea > 0) {
						depotInv.setItem(45,
								APBuy.tagger
										.setNBTTag("ToPage", pagea - 1,
												new AIS("�7" + (pagea - 1) + " "
														+ Translator.translate("menu.page.previous"), 1, Material.PAPER)
																.toIS()));
					}

					if (depotInv.getItem(31).getType() == Material.PAPER) {
						depotInv.setItem(31, new AIS("�a", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
					}
					whoOpened.remove(p);
					p.openInventory(depotInv);
					whoOpened.put(p, pagea);
				} catch (Exception e1) {
					e1.printStackTrace();
					whoOpened.remove(p);
					System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
					p.closeInventory();
					p.sendMessage(Translator.translate("dev.error"));
					p.sendMessage("�cError Code: " + Utils.addToFix(e1));
				}
			}
		});
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) throws APBuyException {
		if (whoOpened.containsKey(e.getWhoClicked())) {
			e.setCancelled(true);
			if (e.getClickedInventory() == e.getView().getTopInventory()) {
				if (e.getSlot() == 49) {
					APBuy.getMarketHandler().openInvToP("MyMarket:Main", (Player) e.getWhoClicked());
					return;
				}
				if (APBuy.tagger.hasTag("Depot", e.getCurrentItem())) {
					switch (e.getClick()) {
					case LEFT:
						ItemStack pureItemstack = APBuy.tagger.removeNBTTag("Depot",
								new AIS(e.getCurrentItem().clone()).removeLatestLore(2).toIS());
						String[] itemData = database
								.getItemDataFromDepot(e.getWhoClicked().getUniqueId().toString(), pureItemstack);
						int place = Utils.getPlaceForIS((Player) e.getWhoClicked(),
								Long.parseLong(itemData[1]) > 64 ? 64 : Long.parseLong(itemData[1]), pureItemstack);
						if (place != 0) {
							Utils.addItemToPlayer((Player) e.getWhoClicked(), pureItemstack, place);
							database.saveMisToItemDepot(itemData[0], e.getWhoClicked().getUniqueId().toString(),
									pureItemstack, Long.parseLong(itemData[1]) - place);
							openItemDepot((Player) e.getWhoClicked(), whoOpened.get(e.getWhoClicked()));
						} else {
							e.getWhoClicked().sendMessage("�cDu hast nicht gen�gend Platz in deinem Inventar!");
						}
						break;
					case MIDDLE:
						ItemStack pureItemstack1 = APBuy.tagger.removeNBTTag("Depot",
								new AIS(e.getCurrentItem().clone()).removeLatestLore(2).toIS());
						String[] itemData1 = database
								.getItemDataFromDepot(e.getWhoClicked().getUniqueId().toString(), pureItemstack1);
						int place1 = Utils.getPlaceForIS((Player) e.getWhoClicked(), Long.parseLong(itemData1[1]),
								pureItemstack1);
						if (place1 != 0) {
							Utils.addItemToPlayer((Player) e.getWhoClicked(), pureItemstack1, place1);
							database.saveMisToItemDepot(itemData1[0], e.getWhoClicked().getUniqueId().toString(),
									pureItemstack1, Long.parseLong(itemData1[1]) - place1);
							openItemDepot((Player) e.getWhoClicked(), whoOpened.get(e.getWhoClicked()));
						} else {
							e.getWhoClicked().sendMessage("�cDu hast nicht gen�gend Platz in deinem Inventar!");
						}
						break;
					default:
						break;
					}
				}
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		whoOpened.remove(e.getPlayer());
	}

	public boolean hasItemDepot(Player player) throws APBuyException {
		return database.getItemsFromItemDepot(player.getUniqueId().toString()).size() != 0;
	}

	public static ItemDepot getInstance() {
		return APBuy.itemDepot;
	}

	public void transferMarketToItemDepot(String uuid) throws APBuyException {
		Market m = new Market(uuid, true);
		HashMap<ItemStack, String[]> toSave = mergeList(uuid, m.getMarketItems());
		for (ItemStack is : toSave.keySet()) {
			database.saveMisToItemDepot(toSave.get(is)[0], uuid, is, Long.parseLong(toSave.get(is)[1]));
		}
	}

	public HashMap<ItemStack, String[]> mergeList(String uuid, List<MarketItem> misToTransfer) throws APBuyException {
		HashMap<ItemStack, String[]> hmDepot = database.getItemsFromItemDepot(uuid);
		String[] tempData;
		for (MarketItem mis : misToTransfer) {
			if (hmDepot.containsKey(mis.getIs())) {
				tempData = hmDepot.get(mis.getIs());
				tempData = new String[] { tempData[0], String.valueOf(Long.parseLong(tempData[1]) + mis.getAmmount()) };
				hmDepot.put(mis.getIs(), tempData);
			} else {
				hmDepot.put(mis.getIs(),
						new String[] { UUID.randomUUID().toString(), String.valueOf(mis.getAmmount()) });
			}
		}
		return hmDepot;
	}

}
