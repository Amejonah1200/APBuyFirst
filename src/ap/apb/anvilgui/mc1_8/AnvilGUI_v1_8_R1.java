package ap.apb.anvilgui.mc1_8;

import ap.apb.anvilgui.AnvilGUIObj;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class AnvilGUI_v1_8_R1 extends AnvilGUIObj {
	public AnvilGUI_v1_8_R1(Player player, AnvilClickEventHandler anvilClickEventHandler) {
		super(player, anvilClickEventHandler);
	}

	private class AnvilContainer extends net.minecraft.server.v1_8_R3.ContainerAnvil {
		public AnvilContainer(net.minecraft.server.v1_8_R3.EntityHuman entity) {
			super(entity.inventory, entity.world, new net.minecraft.server.v1_8_R3.BlockPosition(0, 0, 0), entity);
		}

		@Override
		public boolean a(net.minecraft.server.v1_8_R3.EntityHuman EntityHuman) {
			return true;
		}
	}

	public void open() {
		net.minecraft.server.v1_8_R3.EntityPlayer p = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player)
				.getHandle();

		AnvilContainer container = new AnvilContainer(p);
		inv = container.getBukkitView().getTopInventory();

		for (AnvilSlot slot : items.keySet()) {
			inv.setItem(slot.getSlot(), items.get(slot));
		}
		int c = p.nextContainerCounter();
		p.playerConnection.sendPacket(new net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow(c, "minecraft:anvil",
				new net.minecraft.server.v1_8_R3.ChatMessage("Repairing", new Object[] {}), 0));
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