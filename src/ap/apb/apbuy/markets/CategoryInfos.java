package ap.apb.apbuy.markets;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import ap.apb.AIS;
import ap.apb.APBuy;
import ap.apb.Utils;

public class CategoryInfos {

	private String name;
	private Material mat;
	private short subid;
	private String desc;
	private String owner;

	public CategoryInfos(String owner, String name, Material mat, short subid, String desc) {
		this.name = name;
		this.mat = mat;
		this.subid = subid;
		this.desc = desc;
		this.owner = owner;
	}

	public CategoryInfos(String owner) {
		this.mat = Material.CHEST;
		this.owner = owner;
	}

	public String getName() {
		return this.name;
	}

	public String getOwner() {
		return this.owner;
	}

	public Material getMat() {
		return this.mat;
	}

	public short getSubid() {
		return this.subid;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setMat(Material mat) {
		this.mat = mat;
	}

	public void setSubid(short subid) {
		this.subid = subid;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void save() throws MarketException {
		APBuy.database.saveCategoryInfos(owner, name, desc, mat, subid);
	}

	public AIS getAIS() {
		AIS ais = new AIS(this.mat).setName(this.name == null ? "Preview" : this.name).setDamage(this.subid);
		if (this.desc != null) {
			ais.addLineToLore("").addToLore(
					Utils.createListFromStringToWidth(ChatColor.translateAlternateColorCodes('&', this.desc), 50));
		}
		return ais;
	}

}
