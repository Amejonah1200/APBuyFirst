package ap.apb.menu.menus.main.mymarket.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import ap.apb.menu.Menu;

public class MyMarketCatsMenu extends Menu {

	public MyMarketCatsMenu(Player player) {
		super("MyMarket:Cats", player);
	}

	@Override
	public Menu openInv(Object... args) {
		return null;
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		return false;
	}

}
