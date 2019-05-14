package ap.apb.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class MenuButton {

	private int slot;
	private Object[] args;

	public MenuButton(int slot, Object... args) {
		this.setSlot(slot);
		this.setArgs(args);
	}

	public abstract void onClick(InventoryClickEvent event);

	public abstract ItemStack getShowItem();

	public int getSlot() {
		return this.slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public Object[] getArgs() {
		return this.args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

}
