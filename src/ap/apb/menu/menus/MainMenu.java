package ap.apb.menu.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ap.apb.AIS;
import ap.apb.APBuy;
import ap.apb.Translator;
import ap.apb.Utils;
import ap.apb.apbuy.markets.MarketHandler;
import ap.apb.menu.Menu;

public class MainMenu extends Menu {

	public MainMenu(Player p) {
		super("MainMenu", p);
	}

	@Override
	public void openInv(Object... args) {
		try {
			Inventory inv = Bukkit.createInventory(null, 27, Translator.translate("menu.title.mainmenu"));
			for (int i = 0; i < 27; i++) {
				ItemStack nothing = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
				ItemMeta m = nothing.getItemMeta();
				m.setDisplayName("§a");
				nothing.setItemMeta(m);
				inv.setItem(i, nothing);
			}

			if (!getPlayer().hasPermission("apb.mymarket.edit")) {
				inv.setItem(4, new AIS("§3§lMy Market", 1, Material.BARRIER).addToLore(Utils
						.createListFromStringToWidth(Translator.translate("menu.inv.mainmenu.norightsmymarket"), 25))
						.toIS());
			} else {
				inv.setItem(4, new AIS("§3§lMy Market", 1, Material.CHEST)
						.addLineToLore(Translator.translate("menu.inv.mainmenu.hereismymarket")).toIS());
			}
			if (!getPlayer().hasPermission("apb.markets.search")) {
				inv.setItem(10, new AIS("§3Markets", 1, Material.BARRIER).addToLore(Utils
						.createListFromStringToWidth(Translator.translate("menu.inv.mainmenu.norightsmarkets"), 25))
						.toIS());
			} else {
				inv.setItem(10, new AIS("§3Markets", 1, Material.BOOK).addToLore(
						Utils.createListFromStringToWidth(Translator.translate("menu.inv.mainmenu.hereismarkets"), 30))
						.toIS());
			}
			if (getPlayer().hasPermission("apb.markets.search")) {
				// - Top Market of The week 16
				ItemStack is3 = new AIS("§3Top Market", Material.NETHER_STAR)
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
				inv.setItem(16, new AIS("§3Top Market", 1, Material.BARRIER).addToLore(Utils
						.createListFromStringToWidth(Translator.translate("menu.inv.mainmenu.norightstopmarket"), 30))
						.toIS());
			}
			// // - Help 16
			// ItemStack is4 = new ItemStack(Material.PAPER);
			// ItemMeta m4 = is4.getItemMeta();
			// m4.setDisplayName("§3Help");
			// List<String> s4 = new ArrayList<>();
			// s4.add("§r§fHier kannst du Hilfe suchen.");
			// m4.setLore(s4);
			// is4.setItemMeta(m4);
			// inv.setItem(22, is4);

			inv.setItem(23,
					new AIS("§7Commands", 1, Material.PAPER).addLineToLore("").addToLore(Utils
							.createListFromStringToWidth(Translator.translate("menu.inv.mainmenu.hereiscommands"), 40))
							.toIS());
			APBuy.getMarketHandler();
			APBuy.getMarketHandler();
			APBuy.getMarketHandler();
			APBuy.getMarketHandler();
			inv.setItem(21, new AIS(
					MarketHandler.adminshop != null
							? MarketHandler.adminshop.isOpen()
									? "§6AdminShop (§a" + Translator.translate("menu.inv.mainmenu.open") + "§6)"
									: "§6AdminShop (§c" + Translator.translate("menu.inv.mainmenu.close") + "§6)"
							: "§6AdminShop (§c" + Translator.translate("menu.inv.mainmenu.close") + "§6)",
					1, Material.INK_SACK).setDamage(
							(short) (MarketHandler.adminshop != null ? MarketHandler.adminshop.isOpen() ? 2 : 1 : 1))
							.addLineToLore("").addLineToLore(Translator.translate("menu.inv.mainmenu.hereisadminshop"))
							.toIS());
			// if(settedAdminShop) {
			// inv.setItem(22, new AIS("§", 1, ));
			// }

			getPlayer().openInventory(inv);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Player: " + getPlayer().getName() + " (" + getPlayer().getUniqueId().toString() + ")");
			getPlayer().closeInventory();
			APBuy.getMarketHandler().removeFromAll(getPlayer());
			getPlayer().sendMessage(Translator.translate("dev.error"));
			getPlayer().sendMessage("§cError Code: " + Utils.addToFix(e));
		}
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		switch (event.getSlot()) {
		case 4:
			if (event.getCurrentItem().getType() != Material.BARRIER) {
				APBuy.getMarketHandler().openInvToP("MyMarket:Main", getPlayer());
			} else {
				getPlayer().sendMessage(Translator.translate("click.norights"));
			}
			break;
		case 10:
			if (event.getCurrentItem().getType() != Material.BARRIER) {
				APBuy.getMarketHandler().openInvToP("Markets:Opened", getPlayer());
			} else {
				getPlayer().sendMessage(Translator.translate("click.norights"));
			}
			break;
		case 16:
			if (event.getCurrentItem().getType() != Material.BARRIER) {
				// if (APBuy.tagger.hasTag("Market",
				// e.getCurrentItem())) {
				// PMLoc.remove(p);
				// openMarketVisualiserToPlayer("Main",
				// APBuy.database.getTopMarketsUUIDs(1).get(0).toString(),
				// p);
				// }
			} else {
				// if(BanManager.isNowBanned(p)) {
				// BanManager.getBanByPlayer(p).sayMessage(p);
				// } else {
				getPlayer().sendMessage(Translator.translate("click.norights"));
				// }
			}
			break;
		case 21:
			if (event.getCurrentItem().getDurability() == 1) {
				getPlayer().sendMessage(Translator.translate("click.adminsclose"));
			} else {
				APBuy.getMarketHandler().openMarketVisualiserToPlayer("Cats", "AdminShop", getPlayer());
			}
			break;
		case 23:
			APBuy.getMarketHandler().openInvToP("cmds", getPlayer());
			break;
		}
		return true;
	}

}
