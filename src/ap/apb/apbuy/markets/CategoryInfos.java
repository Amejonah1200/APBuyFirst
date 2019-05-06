package ap.apb.apbuy.markets;

import org.bukkit.Material;

import ap.apb.APBuy;

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
	
	public void save() throws MarketException {
		APBuy.database.saveCategoryInfos(owner, name, desc, mat, subid);
	}
	
}
