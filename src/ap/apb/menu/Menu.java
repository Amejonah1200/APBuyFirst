package ap.apb.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class Menu {

	private String name;
	private Player player;

	public Menu(String name, Player player) {
		this.name = name;
		this.setPlayer(player);
	}

	public abstract void openInv(Object... args);

	public abstract boolean onClick(InventoryClickEvent event);

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Player getPlayer() {
		return this.player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
