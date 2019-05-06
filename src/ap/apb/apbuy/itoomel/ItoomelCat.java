package ap.apb.apbuy.itoomel;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Material;

public abstract class ItoomelCat {
	private String name;
	private String desc;
	private Material catMat;
	private HashMap<Material, Short[]> catMats = new HashMap<>();
	private short id;

	public ItoomelCat(String name, Material catMat, String description) {
		this.name = name;
		this.desc = description;
		this.catMat = catMat;
		this.id = 0;
		this.catMats = new HashMap<>();
	}

	public ItoomelCat(String name, Material catMat, short id, String description) {
		this.name = name;
		this.desc = description;
		this.catMat = catMat;
		this.id = id;
		this.catMats = new HashMap<>();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Material getCatMat() {
		return this.catMat;
	}

	public void setCatMat(Material catItem) {
		this.catMat = catItem;
	}

	public HashMap<Material, Short[]> getCatMats() {
		return this.catMats;
	}

	public boolean hasMaterial(Material mat) {
		return this.catMats.containsKey(mat);
	}

	public void addMat(Material mat, Short[] ids) {
		this.catMats.put(mat, ids);
	}

	public void addIDsToMat(Material mat, short... ids) {
		Short[] s = new Short[ids.length + (this.catMats.containsKey(mat) ? this.catMats.get(mat).length : 0)];
		for (int i = 0; i < ids.length; i++) {
			s[i] = ids[i];
		}
		if(this.catMats.containsKey(mat)) {
			for(int i = ids.length; i < s.length;i++) {
				s[i] = this.catMats.get(mat)[i];
			}
		}
		this.catMats.put(mat, s);
	}

	public void addMat(Material mat) {
		this.catMats.put(mat,  new Short[] {-1});
	}

	public abstract ItoomelCat registerMats();

	public HashMap<Material, Short[]> getCatMatsRest() {
		HashMap<Material, Short[]> cM = new HashMap<>();
		cM.putAll(this.getCatMats());
		for (Material mat : this.getCatMats().keySet()) {
			if (!Itoomel.itoomelStandard.containsKey(mat)) {
				cM.remove(mat);
			}
		}
		return cM;
	}

	public boolean hasOneMat(Set<Material> keySet) {
		for (Material mat : keySet) {
			if (this.getCatMats().containsKey(mat)) {
				return true;
			}
		}
		return false;
	}

	public String getDescription() {
		return this.desc;
	}

	public short getId() {
		return this.id;
	}

	public void setId(short id) {
		this.id = id;
	}

}
