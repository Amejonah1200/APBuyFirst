package ap.apb.nbttager.mc1_8;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ap.apb.nbttager.NBTTager;

public class NBTTager_v1_8_R2 implements NBTTager {

	@Override
	public ItemStack setNBTTag(String key, Object o, ItemStack is) {
		ItemStack is2 = is;
		ItemStack im = is;
		try {
			net.minecraft.server.v1_8_R2.ItemStack nms = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.asNMSCopy(im);
			net.minecraft.server.v1_8_R2.NBTTagCompound comp = nms.hasTag() ? nms.getTag() : new net.minecraft.server.v1_8_R2.NBTTagCompound();
			if (o instanceof net.minecraft.server.v1_8_R2.NBTBase) {
				comp.set(key, (net.minecraft.server.v1_8_R2.NBTBase) o);
			} else if (o instanceof String) {
				comp.setString(key, (String) o);
			} else if (o instanceof Boolean) {
				comp.setBoolean(key, (Boolean) o);
			} else if (o instanceof Integer) {
				comp.setInt(key, (int) o);
			} else if (o instanceof Float) {
				comp.setFloat(key, (float) o);
			} else if (o instanceof Long) {
				comp.setFloat(key, (long) o);
			}
			nms.setTag(comp);
			is = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.asBukkitCopy(nms);
			return is;
		} catch (Exception e) {
			e.printStackTrace();
			return is2;
		}
	}

	@Override
	public Object getNBTTag(String key, ItemStack is) {
		try {
			net.minecraft.server.v1_8_R2.ItemStack nmsItem = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.asNMSCopy(is);
			net.minecraft.server.v1_8_R2.NBTTagCompound comp = (nmsItem.hasTag()) ? nmsItem.getTag() : new net.minecraft.server.v1_8_R2.NBTTagCompound();
			return comp.get(key);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getNBTTagString(String key, ItemStack is) {
		try {
			net.minecraft.server.v1_8_R2.ItemStack nmsItem = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.asNMSCopy(is);
			net.minecraft.server.v1_8_R2.NBTTagCompound comp = (nmsItem.hasTag()) ? nmsItem.getTag() : new net.minecraft.server.v1_8_R2.NBTTagCompound();
			return comp.getString(key);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public int getNBTTagInt(String key, ItemStack is) {
		try {
			net.minecraft.server.v1_8_R2.ItemStack nmsItem = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.asNMSCopy(is);
			net.minecraft.server.v1_8_R2.NBTTagCompound comp = (nmsItem.hasTag()) ? nmsItem.getTag() : new net.minecraft.server.v1_8_R2.NBTTagCompound();
			return comp.getInt(key);
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public ItemStack removeNBTTag(String key, ItemStack is) {
		ItemStack is2 = is;
		try {
			net.minecraft.server.v1_8_R2.ItemStack nmsItem = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.asNMSCopy(is);
			net.minecraft.server.v1_8_R2.NBTTagCompound comp = (nmsItem.hasTag()) ? nmsItem.getTag() : new net.minecraft.server.v1_8_R2.NBTTagCompound();
			comp.remove(key);
			nmsItem.setTag(comp);
			ItemMeta meta = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.getItemMeta(nmsItem);
			is.setItemMeta(meta);
			return is;
		} catch (Exception e) {
			return is2;
		}
	}

	@Override
	public Object getNBTTagList(String key, ItemStack is, int type) {
		try {
			net.minecraft.server.v1_8_R2.ItemStack nmsItem = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.asNMSCopy(is);
			net.minecraft.server.v1_8_R2.NBTTagCompound comp = (nmsItem.hasTag()) ? nmsItem.getTag() : new net.minecraft.server.v1_8_R2.NBTTagCompound();
			return comp.getList(key, type);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean getNBTTagBoolean(String key, ItemStack is) {
		try {
			net.minecraft.server.v1_8_R2.ItemStack nmsItem = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.asNMSCopy(is);
			net.minecraft.server.v1_8_R2.NBTTagCompound comp = (nmsItem.hasTag()) ? nmsItem.getTag() : new net.minecraft.server.v1_8_R2.NBTTagCompound();
			return comp.getBoolean(key);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean hasTag(String key, ItemStack is) {
		try {
			net.minecraft.server.v1_8_R2.ItemStack nmsItem = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.asNMSCopy(is);
			net.minecraft.server.v1_8_R2.NBTTagCompound comp = (nmsItem.hasTag()) ? nmsItem.getTag() : new net.minecraft.server.v1_8_R2.NBTTagCompound();
			return comp.hasKey(key);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public float getNBTTagFloat(String key, ItemStack is) {
		try {
			net.minecraft.server.v1_8_R2.ItemStack nmsItem = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.asNMSCopy(is);
			net.minecraft.server.v1_8_R2.NBTTagCompound comp = (nmsItem.hasTag()) ? nmsItem.getTag() : new net.minecraft.server.v1_8_R2.NBTTagCompound();
			return comp.getFloat(key);
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public long getNBTTagLong(String key, ItemStack is) {
		try {
			net.minecraft.server.v1_8_R2.ItemStack nmsItem = org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack.asNMSCopy(is);
			net.minecraft.server.v1_8_R2.NBTTagCompound comp = (nmsItem.hasTag()) ? nmsItem.getTag() : new net.minecraft.server.v1_8_R2.NBTTagCompound();
			return comp.getLong(key);
		} catch (Exception e) {
			return 0;
		}
	}

}
