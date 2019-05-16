package ap.apb.menu.menus.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import ap.apb.AIS;
import ap.apb.Translator;
import ap.apb.Utils;
import ap.apb.menu.Menu;

public class CmdsMenu extends Menu {

	public CmdsMenu(Player player) {
		super("MainMenu:Cmds", player);
	}

	@Override
	public void openInv(Object... args) {
		Inventory inv = Bukkit.createInventory(null, 27, "§0§lA§3§lP§r§8Buy - Cmds");
		for (int i = 0; i < 27; i++) {
			inv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
		}
		inv.setItem(22, new AIS(Translator.translate("menu.back"), 1, Material.BARRIER).toIS());

		if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mymarket.*")
				|| getPlayer().hasPermission("apb.mymarket.edit") || getPlayer().hasPermission("apb.mymarket.setdevise")
				|| getPlayer().hasPermission("apb.mymarket.setname")) {
			AIS ais = new AIS("§7MyMarket", Material.PAPER);
			ais.addLineToLore("");
			if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mymarket.*")
					|| getPlayer().hasPermission("apb.mymarket.edit")) {
				ais.addToLore(Utils.createListFromStringToWidthPlusEffect(
						Translator.translate("menu.inv.cmds.mymarket.create"), 30));
			}
			if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mymarket.*")
					|| getPlayer().hasPermission("apb.mymarket.setname")) {
				ais.addLineToLore("§7- [/apb setname <Name>]").addToLore(Utils.createListFromStringToWidthPlusEffect(
						Translator.translate("menu.inv.cmds.mymarket.name"), 30));
			}
			if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mymarket.*")
					|| getPlayer().hasPermission("apb.mymarket.setdevise")) {
				ais.addLineToLore("§7- [/apb setdevise <Devise>]")
						.addToLore(Utils.createListFromStringToWidthPlusEffect(
								Translator.translate("menu.inv.cmds.mymarket.devise"), 30));
			}
			inv.setItem(10, ais.toIS());
		} else {
			inv.setItem(10, new AIS(Translator.translate("menu.inv.cmds.mymarket.norights"), 1, (short) 7,
					Material.STAINED_GLASS_PANE).toIS());
		}
		if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.markets.*")
				|| getPlayer().hasPermission("apb.markets.buy") || getPlayer().hasPermission("apb.markets.search")) {
			AIS ais = new AIS("§7Markets", Material.PAPER);
			ais.addLineToLore("");
			if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mymarket.*")
					|| getPlayer().hasPermission("apb.markets.search")) {
				ais.addLineToLore(Translator.translate("menu.inv.cmds.markets.search.topic"))
						.addLineToLore("§7- [/apb open name <Spielername>]")
						.addToLore(Utils.createListFromStringToWidthPlusEffect(
								"§8   " + Translator.translate("menu.inv.cmds.markets.search.name"), 30))
						.addLineToLore("§7- [/apb open uuid <UUID>]").addToLore(Utils.createListFromStringToWidth(
								"§8   " + Translator.translate("menu.inv.cmds.markets.search.uuid"), 30));
			}
			if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mymarket.*")
					|| getPlayer().hasPermission("apb.markets.buy")) {
				ais.addLineToLore(Translator.translate("menu.inv.cmds.markets.buy"));
			}
			inv.setItem(12, ais.toIS());
		} else {
			inv.setItem(12, new AIS(Translator.translate("menu.inv.cmds.markets.norights"), 1, (short) 7,
					Material.STAINED_GLASS_PANE).toIS());
		}

		if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.itoomel")) {
			AIS ais = new AIS("§7Itoomel", Material.PAPER);
			ais.addLineToLore("");
			if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.itoomel")) {
				ais.addLineToLore("§7Du kannst Itoomel benutzen.");
			}
			inv.setItem(14, ais.toIS());
		} else {
			inv.setItem(14,
					new AIS("§cDu hast keine Itoomel-Rechte.", 1, (short) 7, Material.STAINED_GLASS_PANE).toIS());
		}

		if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mod.*")
				|| getPlayer().hasPermission("apb.mod.delete") || getPlayer().hasPermission("apb.mod.genstop")
				|| getPlayer().hasPermission("apb.mod.itoomel") || getPlayer().hasPermission("apb.mod.reset")
				|| getPlayer().hasPermission("apb.mod.status") || getPlayer().hasPermission("apb.mod.invissee")
				|| getPlayer().hasPermission("apb.mod.adminshop")) {
			AIS ais = new AIS("§7Mod", Material.PAPER);
			ais.addLineToLore("");
			if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mod.*")
					|| getPlayer().hasPermission("apb.mod.delete")) {
				ais.addLineToLore(Translator.translate("menu.inv.cmds.mod.delete"));
			}
			if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mod.*")
					|| getPlayer().hasPermission("apb.mod.reset")) {
				ais.addLineToLore(Translator.translate("menu.inv.cmds.mod.reset"));
			}
			if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mod.*")
					|| getPlayer().hasPermission("apb.mod.reset")) {
				ais.addLineToLore(Translator.translate("menu.inv.cmds.mod.status"));
			}
			if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mod.*")
					|| getPlayer().hasPermission("apb.mod.invissee")) {
				ais.addLineToLore(Translator.translate("menu.inv.cmds.mod.invissee"));
			}
			if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mod.*")
					|| getPlayer().hasPermission("apb.mod.genstop")) {
				ais.addToLore(Utils
						.createListFromStringToWidthPlusEffect(Translator.translate("menu.inv.cmds.mod.genstop"), 30));
			}
			if (getPlayer().hasPermission("apb.*") || getPlayer().hasPermission("apb.mod.adminshop")) {
				ais.addLineToLore(Translator.translate("menu.inv.cmds.mod.adminshogetPlayer().edit"))
						.addLineToLore("§7- [/apb adminshop open]")
						.addLineToLore("§8    " + Translator.translate("menu.inv.cmds.mod.adminshogetPlayer().open"))
						.addLineToLore("§7- [/apb adminshop close]")
						.addLineToLore("§8    " + Translator.translate("menu.inv.cmds.mod.adminshogetPlayer().close"));
			}
			inv.setItem(16, ais.toIS());
		} else {
			inv.setItem(16, new AIS(Translator.translate("menu.inv.cmds.mod.norights"), 1, (short) 7,
					Material.STAINED_GLASS_PANE).toIS());
		}

		getPlayer().openInventory(inv);
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		return false;
	}

}
