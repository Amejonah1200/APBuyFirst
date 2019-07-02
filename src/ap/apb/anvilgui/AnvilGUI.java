package ap.apb.anvilgui;

import ap.apb.anvilgui.AnvilGUIObj.AnvilSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface AnvilGUI {
    public class AnvilClickEvent {
        private AnvilSlot slot;
 
        private String name;
 
        private boolean close = true;
        private boolean destroy = true;
        private boolean ignoreClose = false;
        private Player clicker;
 
        public AnvilClickEvent(AnvilSlot slot, String name, Player clicker){
            this.slot = slot;
            this.name = name;
        }
 
        public AnvilSlot getSlot(){
            return slot;
        }
 
        public String getName(){
            return name;
        }
 
        public boolean getWillClose(){
            return close;
        }
 
        public void setWillClose(boolean close){
            this.close = close;
        }
 
        public boolean getWillDestroy(){
            return destroy;
        }
 
        public void setWillDestroy(boolean destroy){
            this.destroy = destroy;
        }
        public Player getClicker() {
			return this.clicker;
		}

		public boolean isIgnoreClose() {
			return this.ignoreClose;
		}

		public void setIgnoreClose(boolean ignoreClose) {
			this.ignoreClose = ignoreClose;
		}
    }
    
    public interface AnvilClickEventHandler {

    	public void onAnvilClick(AnvilClickEvent event);
    	
    }
    
    public void open();

	public void setSlot(AnvilSlot slot, ItemStack is);
	
	public void destroy();
	
	public AnvilClickEventHandler getHandler();
	
	public Player getPlayer();
	
	public void setIgnoreClose(boolean ignore);
	public boolean getIgnoreClose();
}
