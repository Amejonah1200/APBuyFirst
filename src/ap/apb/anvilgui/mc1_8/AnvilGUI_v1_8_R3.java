package ap.apb.anvilgui.mc1_8;
 
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ap.apb.APBuy;
import ap.apb.anvilgui.AnvilGUI;

public class AnvilGUI_v1_8_R3 implements AnvilGUI {
    private class AnvilContainer extends net.minecraft.server.v1_8_R3.ContainerAnvil {
        public AnvilContainer(net.minecraft.server.v1_8_R3.EntityHuman entity){
            super(entity.inventory, entity.world,new net.minecraft.server.v1_8_R3.BlockPosition(0, 0, 0), entity);
        }
 
        @Override
        public boolean a(net.minecraft.server.v1_8_R3.EntityHuman EntityHuman){
            return true;
        }
    }
 
    private Player player;
 
    private AnvilClickEventHandler handler;
 
    private HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();
 
    private Inventory inv;
 
    private Listener listener;
    
    private boolean ignoreClose = false;
 
    public AnvilGUI_v1_8_R3(Player player, final AnvilClickEventHandler anvilClickEventHandler){
        this.player = player;
        this.handler = anvilClickEventHandler;
 
        this.listener = new Listener(){
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event){
                if(event.getWhoClicked() instanceof Player){
                    Player clicker = (Player) event.getWhoClicked();
 
                    if(event.getInventory().equals(inv)){
                        event.setCancelled(true);
 
                        ItemStack item = event.getCurrentItem();
                        int slot = event.getRawSlot();
                        String name = "";
 
                        if(item != null){
                            if(item.hasItemMeta()){
                                ItemMeta meta = item.getItemMeta();
 
                                if(meta.hasDisplayName()){
                                    name = meta.getDisplayName();
                                }
                            }
                        }
 
                        AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name,clicker);
 
                        anvilClickEventHandler.onAnvilClick(clickEvent);
                        AnvilGUI_v1_8_R3.this.setIgnoreClose(clickEvent.isIgnoreClose());
                        if(clickEvent.getWillClose()){
                            event.getWhoClicked().closeInventory();
                        }
 
                        if(clickEvent.getWillDestroy()){
                            destroy();
                        }
                    }
                }
            }
            
            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event){
                if(event.getPlayer() instanceof Player){
                    Inventory inv = event.getInventory();
                    if(inv.equals(AnvilGUI_v1_8_R3.this.inv)){
                        inv.clear();
                        destroy();
                    }
                }
            }
 
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event){
                if(event.getPlayer().equals(getPlayer())){
                	APBuy.getMarketHandler().removeFromAll(event.getPlayer());
                	event.getPlayer().getOpenInventory().getTopInventory().clear();
                    destroy();
                }
            }
        };
 
        Bukkit.getPluginManager().registerEvents(listener, APBuy.plugin);
    }
 
    public Player getPlayer(){
        return player;
    }
 
    public void setSlot(AnvilSlot slot, ItemStack item){
        items.put(slot, item);
    }
 
    public void open(){
        net.minecraft.server.v1_8_R3.EntityPlayer p = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle();
 
        AnvilContainer container = new AnvilContainer(p);
        inv = container.getBukkitView().getTopInventory();
 
        for(AnvilSlot slot : items.keySet()){
            inv.setItem(slot.getSlot(), items.get(slot));
        }
        int c = p.nextContainerCounter();
        p.playerConnection.sendPacket(new net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow(c, "minecraft:anvil", new net.minecraft.server.v1_8_R3.ChatMessage("Repairing", new Object[]{}), 0));
        p.activeContainer = container;
        p.activeContainer.windowId = c;
        p.activeContainer.addSlotListener(p);
    }
 
    public void destroy(){
        player = null;
        handler = null;
        items = null;
 
        HandlerList.unregisterAll(listener);
 
        listener = null;
    }
    
    public AnvilClickEventHandler getHandler() {
		return this.handler;
	}

	@Override
	public void setIgnoreClose(boolean ignore) {
		this.ignoreClose = ignore;
	}

	@Override
	public boolean getIgnoreClose() {
		return this.ignoreClose;
	}
}