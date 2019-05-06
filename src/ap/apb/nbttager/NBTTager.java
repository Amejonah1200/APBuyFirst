package ap.apb.nbttager;

import org.bukkit.inventory.ItemStack;

public interface NBTTager {

	public ItemStack setNBTTag(String key, Object o,ItemStack is);
	
	public Object getNBTTag(String key, ItemStack is);
	
	public String getNBTTagString(String key, ItemStack is);
	
	public int getNBTTagInt(String key, ItemStack is);
	
	public long getNBTTagLong(String key, ItemStack is);
	
	public ItemStack removeNBTTag(String key, ItemStack is);
	
	public boolean getNBTTagBoolean(String key, ItemStack is);

	public Object getNBTTagList(String key, ItemStack is, int type);

	public boolean hasTag(String key, ItemStack is);

	public float getNBTTagFloat(String key, ItemStack is);
	

}
