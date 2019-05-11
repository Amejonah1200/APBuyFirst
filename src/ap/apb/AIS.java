package ap.apb;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AIS {

	private ItemStack is;
	private ItemMeta meta;

	public AIS(String name, int ammount, short damage, Material material) {
		this.is = new ItemStack(material, ammount, damage);
		this.meta = this.is.getItemMeta();
		this.meta.setDisplayName(name);
	}

	public AIS(String name, int ammount, Material material) {
		this.is = new ItemStack(material, ammount);
		this.meta = this.is.getItemMeta();
		this.meta.setDisplayName(name);
	}

	public AIS(String name, int ammount, short damage, Material material, List<String> lore) {
		this.is = new ItemStack(material, ammount, damage);
		this.meta = this.is.getItemMeta();
		this.meta.setDisplayName(name);
		this.meta.setLore(lore);
	}

	public AIS(String name, int ammount, Material material, List<String> lore) {
		this.is = new ItemStack(material, ammount);
		this.meta = this.is.getItemMeta();
		this.meta.setDisplayName(name);
		this.meta.setLore(lore);
	}

	public AIS(ItemStack is, List<String> lore) {
		this.is = new ItemStack(is);
		this.meta = this.is.getItemMeta();
		this.meta.setLore(lore);
	}

	public AIS(ItemStack is) {
		this.is = is;
		this.meta = this.is.getItemMeta();
	}

	public AIS(String name, Material mat) {
		this.is = new ItemStack(mat, 1);
		this.meta = this.is.getItemMeta();
		this.meta.setDisplayName(name);
	}

	public AIS(Material type) {
		this.is = new ItemStack(type);
		this.meta = this.is.getItemMeta();
	}

	public AIS setName(String name) {
		this.meta.setDisplayName(name);
		return this;
	}

	public AIS setAmmount(int ammount) {
		this.is.setAmount(ammount);
		return this;
	}

	public AIS setDamage(short damage) {
		this.is.setDurability(damage);
		return this;
	}

	public AIS setMaterial(Material material) {
		this.is.setType(material);
		return this;
	}

	public AIS addToLore(List<String> l) {
		if (this.meta.getLore() == null) {
			this.meta.setLore(l);
			return this;
		} else {
			List<String> l2 = this.meta.getLore();
			l2.addAll(l);
			this.meta.setLore(l2);
			return this;
		}
	}

	public AIS addLineToLore(String l) {
		if (this.meta.getLore() == null) {
			List<String> s = new ArrayList<>();
			s.add(l);
			this.meta.setLore(s);
			return this;
		} else {
			List<String> l2 = this.meta.getLore();
			l2.add(l);
			this.meta.setLore(l2);
			return this;
		}
	}

	public AIS setNBTTag(String key, Object obj) {
		this.is.setItemMeta(this.meta);
		this.is = APBuy.tagger.setNBTTag(key, obj, this.is);
		this.meta = this.is.getItemMeta();
		return this;
	}

	public AIS removeNBTTag(String key) {
		this.is.setItemMeta(this.meta);
		this.is = APBuy.tagger.removeNBTTag(key, this.is);
		this.meta = this.is.getItemMeta();
		return this;
	}

	public AIS setLore(List<String> l) {
		this.meta.setLore(l);
		if (this.meta.toString().split(Pattern.quote(",")).length == 2) {
			this.meta = null;
			this.meta = this.toIS().getItemMeta();
		}
		return this;
	}

	public ItemStack toIS() {
		this.is.setItemMeta(this.meta);
		return this.is;
	}

	public AIS removeLatestLore(int i) {
		List<String> s = this.meta.getLore() == null ? new ArrayList<>() : this.meta.getLore();
		if (i >= s.size()) {
			return this.setLore(null);
		}
		int b = s.size() - 1;
		for (int a = 0; a < i; a++) {
			if (!s.isEmpty()) {
				s.remove(b);
				b--;
			}
		}
		return this.setLore(s);
	}

}
