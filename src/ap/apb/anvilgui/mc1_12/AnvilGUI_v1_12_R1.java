package ap.apb.anvilgui.mc1_12;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import ap.apb.anvilgui.AnvilGUIObj;
import net.minecraft.server.v1_12_R1.EntityHuman;

public class AnvilGUI_v1_12_R1 extends AnvilGUIObj {

	public AnvilGUI_v1_12_R1(Player player, AnvilClickEventHandler anvilClickEventHandler) {
		super(player, anvilClickEventHandler);
	}

	private class AnvilContainer extends net.minecraft.server.v1_12_R1.ContainerAnvil {
		public AnvilContainer(net.minecraft.server.v1_12_R1.EntityHuman entity) {
			super(entity.inventory, entity.world, new net.minecraft.server.v1_12_R1.BlockPosition(0, 0, 0), entity);
		}

		@Override
		public boolean a(EntityHuman entityhuman, int i) {
			return true;
		}
	}

	public void open() {
		net.minecraft.server.v1_12_R1.EntityPlayer p = ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player)
				.getHandle();

		AnvilContainer container = new AnvilContainer(p);
		inv = container.getBukkitView().getTopInventory();

		for (AnvilSlot slot : items.keySet()) {
			inv.setItem(slot.getSlot(), items.get(slot));
		}
		int c = p.nextContainerCounter();
		p.playerConnection.sendPacket(new net.minecraft.server.v1_12_R1.PacketPlayOutOpenWindow(c, "minecraft:anvil",
				new net.minecraft.server.v1_12_R1.ChatMessage("Repairing", new Object[] {}), 0));
		p.activeContainer = container;
		p.activeContainer.windowId = c;
		p.activeContainer.addSlotListener(p);
	}

	public void destroy() {
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
