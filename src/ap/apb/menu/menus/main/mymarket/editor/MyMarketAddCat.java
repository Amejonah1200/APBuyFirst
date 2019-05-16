package ap.apb.menu.menus.main.mymarket.editor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ap.apb.AIS;
import ap.apb.APBuy;
import ap.apb.Translator;
import ap.apb.menu.Menu;

public class MyMarketAddCat extends Menu {

	public MyMarketAddCat(Player player) {
		super("MyMarket:Editor:Add", player);
	}

	@Override
	public Menu openInv(Object... args) {
		Inventory inv1 = Bukkit.createInventory(null, 54, "§0§lA§3§lP§r§8Buy - Editor");
		for (int i = 0; i < 54; i++) {
			inv1.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
		}
		boolean hasItem = false;
		for (ItemStack stack : getPlayer().getInventory().getContents()) {
			if (stack != null) {
				hasItem = true;
				break;
			}
		}
		if (!hasItem) {
			getPlayer().sendMessage(Translator.translate("menu.inv.mymarket.editoradd.noitem"));
			APBuy.getMarketHandler().openInvToP("MyMarket:Editor:0", getPlayer());
			return this;
		}

		// - Back Button 49
		inv1.setItem(48,
				new AIS(Translator.translate("menu.inv.mymarket.editoradd.cancel"), 1, (short) 14, Material.WOOL)
						.toIS());

		// - Finish Button 49
		inv1.setItem(50,
				new AIS(Translator.translate("menu.inv.mymarket.editoradd.finish"), 1, (short) 5, Material.WOOL)
						.toIS());

		// - Cat Prewiev 31
		inv1.setItem(31, APBuy.getMarketHandler().creatingCat.get(getPlayer()).getAIS().toIS());

		// - Cat Rename 10
		inv1.setItem(10,
				new AIS(Translator.translate("menu.inv.mymarket.editoradd.change.name"), 1, Material.NAME_TAG).toIS());

		// - Cat Relore 12
		inv1.setItem(12,
				new AIS(Translator.translate("menu.inv.mymarket.editoradd.change.desc"), 1, Material.PAPER).toIS());

		// - Cat Set Item 14
		inv1.setItem(14, new AIS(APBuy.getMarketHandler().creatingCat.get(getPlayer()).getMat())
				.setDamage(APBuy.getMarketHandler().creatingCat.get(getPlayer()).getSubid()).setLore(null)
				.addLineToLore("").addLineToLore(Translator.translate("menu.inv.mymarket.editoradd.setitem.desc"))
				.setName(Translator.translate("menu.inv.mymarket.editoradd.setitem.title")).toIS());
		getPlayer().openInventory(inv1);
		return this;
	}

	@Override
	public boolean onClick(InventoryClickEvent event) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
