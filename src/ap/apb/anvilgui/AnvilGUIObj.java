package ap.apb.anvilgui;

import ap.apb.APBuy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class AnvilGUIObj implements AnvilGUI {
	
	protected Player player;

	protected AnvilClickEventHandler handler;

	protected HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();

	protected Inventory inv;

	protected Listener listener;

	protected boolean ignoreClose = false;

	public AnvilGUIObj(Player player, final AnvilClickEventHandler anvilClickEventHandler) {
		this.player = player;
		this.handler = anvilClickEventHandler;
		this.listener = new Listener() {
			@EventHandler
			public void onInventoryClick(InventoryClickEvent event) {
				if (event.getWhoClicked() instanceof Player) {
					Player clicker = (Player) event.getWhoClicked();

					if (event.getInventory().equals(inv)) {
						event.setCancelled(true);

						ItemStack item = event.getCurrentItem();
						int slot = event.getRawSlot();
						String name = "";

						if (item != null) {
							if (item.hasItemMeta()) {
								ItemMeta meta = item.getItemMeta();

								if (meta.hasDisplayName()) {
									name = meta.getDisplayName();
								}
							}
						}

						AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name, clicker);

						anvilClickEventHandler.onAnvilClick(clickEvent);

						if (clickEvent.getWillClose()) {
							event.getWhoClicked().closeInventory();
						}

						if (clickEvent.getWillDestroy()) {
							destroy();
						}
					}
				}
			}

			@EventHandler
			public void onInventoryClose(InventoryCloseEvent event) {
				if (event.getPlayer() instanceof Player) {
					Inventory inv = event.getInventory();
					if (inv.equals(AnvilGUIObj.this.inv)) {
						inv.clear();
						destroy();
					}
				}
			}

			@EventHandler
			public void onPlayerQuit(PlayerQuitEvent event) {
				if (event.getPlayer().equals(getPlayer())) {
					APBuy.getMarketHandler().removeFromAll(event.getPlayer());
					event.getPlayer().getOpenInventory().getTopInventory().clear();
					destroy();
				}
			}
		};

		Bukkit.getPluginManager().registerEvents(listener, APBuy.plugin);
	}

	public Player getPlayer() {
		return player;
	}

	public void setSlot(AnvilSlot slot, ItemStack item) {
		items.put(slot, item);
	}

	@Override
	public void open() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public AnvilClickEventHandler getHandler() {
		return null;
	}

	@Override
	public void setIgnoreClose(boolean ignore) {
	}

	@Override
	public boolean getIgnoreClose() {
		return false;
	}
	
    public enum AnvilSlot {
        INPUT_LEFT(0),
        INPUT_RIGHT(1),
        OUTPUT(2);
 
        private int slot;
 
        private AnvilSlot(int slot){
            this.slot = slot;
        }
 
        public int getSlot(){
            return slot;
        }
 
        public static AnvilSlot bySlot(int slot){
            for(AnvilSlot anvilSlot : values()){
                if(anvilSlot.getSlot() == slot){
                    return anvilSlot;
                }
            }
 
            return null;
        }
    }

}
