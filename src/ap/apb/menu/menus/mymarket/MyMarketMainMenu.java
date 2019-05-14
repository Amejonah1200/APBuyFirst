package ap.apb.menu.menus.mymarket;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import ap.apb.menu.Menu;

public class MyMarketMainMenu extends Menu {

	public MyMarketMainMenu(Player player) {
		super("MyMarket:Main", player);
	}

	@Override
	public void openInv(Object... args) {
		
	}

	@Override
	public boolean onClick(InventoryClickEvent event) {
		return false;
	}

	

}
