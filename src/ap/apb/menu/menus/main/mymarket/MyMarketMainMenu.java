package ap.apb.menu.menus.main.mymarket;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import ap.apb.apbuy.markets.MarketInfos;
import ap.apb.menu.Menu;

public class MyMarketMainMenu extends Menu {

	public MyMarketMainMenu(Player player) {
		super("MyMarket:Main", player);
	}

	@Override
	public Menu openInv(Object... args) {
		Inventory inv = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - My Market");
		for (int i = 0; i < 54; i++) {
			inv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
		}
		MarketInfos m = APBuy.database.getMarketInfos(getPlayer().getUniqueId().toString());
		// - Status 10
		inv.setItem(10, m.isOpen()
				? new AIS(Translator.translate("menu.inv.mymarket.main.status.on"), 1, (short) 10, Material.INK_SACK)
						.toIS()
				: new AIS(Translator.translate("menu.inv.mymarket.main.status.off"), 1, (short) 1, Material.INK_SACK)
						.toIS());
		// - Soled Items 14

		inv.setItem(14, new AIS(Translator.translate("menu.inv.mymarket.main.stats.topic"), 1, Material.PAPER)
				.addLineToLore("")
				.addLineToLore(Translator.translate("menu.inv.mymarket.main.stats.solditems") + ": " + m.getSoldItems())
				.addLineToLore(Translator.translate("menu.inv.mymarket.main.stats.solds") + ": " + m.getSales())
				.toIS());

		// - Name/Devise 16
		ItemStack nameNdevise = new ItemStack(Material.PAPER, 1);
		ItemMeta nNdmeta = nameNdevise.getItemMeta();
		nNdmeta.setDisplayName(Translator.translate("menu.inv.mymarket.main.nnd.title"));
		List<String> nNdlist = new ArrayList<>();
		nNdlist.add(Translator.translate("menu.inv.mymarket.main.nnd.name") + ": §6"
				+ (m.getName() == null ? Translator.translate("menu.inv.mymarket.main.nnd.notset") : m.getName()));
		nNdlist.add(Translator.translate("menu.inv.mymarket.main.nnd.devise") + ": §6"
				+ (m.getDevise() == null ? Translator.translate("menu.inv.mymarket.main.nnd.notset") : m.getDevise()));
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
		inv.setItem(31, new AIS(Translator.translate("menu.inv.mymarket.main.iteminput.title"), 1, Material.HOPPER)
				.addLineToLore("")
				.addToLore(Utils
						.createListFromStringToWidth(Translator.translate("menu.inv.mymarket.main.iteminput.desc"), 40))
				.toIS());

		getPlayer().openInventory(inv);
		return this;
	}

	@Override
	public boolean onClick(InventoryClickEvent event) throws Exception {
		MarketHandler mh = APBuy.getMarketHandler();
		switch (event.getSlot()) {
		case 10:
			MarketInfos m = APBuy.database.getMarketInfos(getPlayer().getUniqueId().toString());
			m.setOpen(!m.isOpen());
			m.save();
			mh.openInvToP("MyMarket:Main", getPlayer());
			mh.reopenMarketToWhoSee(getPlayer().getUniqueId().toString());
			break;
		case 12:
			mh.openInvToP("MyMarket:Editor:Opened", getPlayer());
			break;
		case 49:
			mh.openInvToP("MainMenu", getPlayer());
			break;
		case 31:
			mh.onItemInput.put(getPlayer(), new ArrayList<>());
			mh.openInvToP("MyMarket:ItemInput", getPlayer());
			mh.onItemInput.put(getPlayer(), new ArrayList<>());
			break;
		}
		return true;
	}

}
