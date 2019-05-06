package ap.apb.apbuy.markets;

public class MarketTemplate {

//	public OfflinePlayer MarketOwner;
//	public String name;
//	public List<Category> marketCategories = new ArrayList<>();
//	public String devise;
//	public boolean open = false;
//	public long soldItems;
//	public long sales;
//
//	public MarketTemplate(Player marketOwner, String name) {
//		this.MarketOwner = marketOwner;
//		this.name = name;
//		this.devise = null;
//		this.soldItems = 0;
//		this.sales = 0;
//	}
//
//	public MarketTemplate(OfflinePlayer offlinePlayer) {
//		this.MarketOwner = offlinePlayer;
//		this.name = null;
//		this.devise = null;
//		this.soldItems = 0;
//		this.sales = 0;
//		this.open = false;
//		this.marketCategories = new ArrayList<>();
//	}
//
//	public void addCategory(Category cat) throws MarketException {
//		this.marketCategories.add(cat);
//		this.saveCategoryInfos(cat);
//	}
//
//	public boolean addCategory(String name, ItemStack is) throws MarketException {
//		for (Category c : this.marketCategories) {
//			if (c.getName().equalsIgnoreCase(name)) {
//				return false;
//			}
//		}
//		this.addCategory(new Category(name, is));
//		return true;
//	}
//
//	public void removeCategory(String name) throws MarketException {
//		Iterator<Category> cats = this.marketCategories.iterator();
//		while (cats.hasNext()) {
//			if (cats.next().getName() == name) {
//				cats.remove();
//			}
//		}
//		APBuy.database.removeCategory(
//				this.MarketOwner != null ? this.MarketOwner.getUniqueId().toString() : "AdminShop", name);
//	}
//
//	public void setDevise(String devise) throws MarketException {
//		this.devise = devise;
//		this.saveMarketInfos();
//	}
//
//	public void saveTotal() throws MarketException {
//		try {
////			APBuy.database.saveTotal(this);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			System.out.println(
//					"Market: " + this.getMarketOwner().getName() + " (" + this.getMarketOwner().getUniqueId() + ")");
//			Utils.addToFix(e1);
//			throw new MarketException(ErrorCause.SAVE);
//		}
//	}
//
//	public void saveMarketInfos() throws MarketException {
//		try {
//			APBuy.database.saveMarketInfos(
//					this.MarketOwner != null ? this.MarketOwner.getUniqueId().toString() : "AdminShop", this.open,
//					this.name, this.devise, this.sales, this.soldItems);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			System.out.println(
//					"Market: " + this.getMarketOwner().getName() + " (" + this.getMarketOwner().getUniqueId() + ")");
//			Utils.addToFix(e1);
//			throw new MarketException(ErrorCause.SAVE);
//		}
//	}
//
//	public void saveCategoryInfos(Category cat) throws MarketException {
//		try {
//			APBuy.database.saveCategoryInfos(
//					this.MarketOwner != null ? this.MarketOwner.getUniqueId().toString() : "AdminShop", cat.getName(),
//					cat.getDesc(), cat.getMat(), cat.getSubid());
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			System.out.println(
//					"Market: " + this.getMarketOwner().getName() + " (" + this.getMarketOwner().getUniqueId() + ")");
//			Utils.addToFix(e1);
//			throw new MarketException(ErrorCause.SAVE);
//		}
//	}
//
//	public void saveMarketItem(String catname, MarketItem mis) throws MarketException {
//		try {
//			APBuy.database.saveItemInfos(
//					this.MarketOwner != null ? this.MarketOwner.getUniqueId().toString() : "AdminShop", catname,
//					mis.getIs(), mis.getPrice(), mis.getAmmount(), mis.getSellAmmount(), mis.getSoldItems());
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			System.out.println(
//					"Market: " + this.getMarketOwner().getName() + " (" + this.getMarketOwner().getUniqueId() + ")");
//			Utils.addToFix(e1);
//			throw new MarketException(ErrorCause.SAVE);
//		}
//	}
//
//	// public void updateMarketItem(MarketItem mis, String catname) throws
//	// MarketException {
//	// try {
//	// APBuy.database.updateItemInfos(
//	// this.MarketOwner != null ? this.MarketOwner.getUniqueId().toString() :
//	// "AdminShop", catname,
//	// mis.getIs(), mis.getAmmount(), mis.getSoldItems());
//	// } catch (Exception e1) {
//	// e1.printStackTrace();
//	// System.out.println(
//	// "Market: " + this.getMarketOwner().getName() + " (" +
//	// this.getMarketOwner().getUniqueId() + ")");
//	// Utils.addToFix(e1);
//	// throw new MarketException(ErrorCause.SAVE);
//	// }
//	// }
//	//
//	// public void updateMarketInfos() throws MarketException {
//	// try {
//	// APBuy.database.updateMarketInfos(
//	// this.MarketOwner != null ? this.MarketOwner.getUniqueId().toString() :
//	// "AdminShop", this.open,
//	// this.name, this.devise, this.sales, this.getSoldItems());
//	// } catch (Exception e1) {
//	// e1.printStackTrace();
//	// System.out.println(
//	// "Market: " + this.getMarketOwner().getName() + " (" +
//	// this.getMarketOwner().getUniqueId() + ")");
//	// Utils.addToFix(e1);
//	// throw new MarketException(ErrorCause.SAVE);
//	// }
//	// }
//
//	public Category getCategorieByName(String name) {
//		for (Category c : this.marketCategories) {
//			if (c.getName().equalsIgnoreCase(name)) {
//				return c;
//			}
//		}
//		return null;
//	}
//
//	public static ItemStack justGetMarketItem(FileConfiguration cfg) {
//		ItemStack MIS = new ItemStack(Material.CHEST);
//		String name = cfg.getString("Name");
//		String devise = cfg.getString("Devise");
//
//		String oname = Bukkit.getOfflinePlayer(UUID.fromString(cfg.getString("Owner"))).getName();
//		ItemMeta m = MIS.getItemMeta();
//		if (devise == null) {
//			if (name != null) {
//				m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
//				List<String> s = new ArrayList<>();
//				s.add("§r§6" + oname + "'s Market.");
//				m.setLore(s);
//			} else {
//				m.setDisplayName("§r§6" + oname + "'s Market.");
//			}
//		} else {
//			devise = ChatColor.translateAlternateColorCodes('&', devise);
//			if (name != null) {
//				m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
//				List<String> s = new ArrayList<>();
//				s.add("§r§6" + oname + "'s Market.");
//				s.add("");
//				s.add("§b" + devise);
//				m.setLore(s);
//			} else {
//				m.setDisplayName("§r§6" + oname + "'s Market.");
//				List<String> s = new ArrayList<>();
//				s.add("");
//				s.add("§b" + devise);
//				m.setLore(s);
//			}
//		}
//		MIS.setItemMeta(m);
//		return MIS;
//	}
//
//	public void removeItemInCat(String catname, ItemStack is) throws MarketException {
//		this.getCategorieByName(catname).removeItem(is);
//		APBuy.database.removeItem(this.MarketOwner == null ? "AdminShop" : this.MarketOwner.getUniqueId().toString(), is);
//	}
//
//	public void addItemToCategory(MarketItem marketItem, String catname) throws MarketException {
//		this.getCategorieByName(catname).addItem(marketItem);
//		this.saveMarketItem(catname, marketItem);
//	}
//
//	// public void setItemAmmountInCat(String catname, MarketItem is, long
//	// ammount) throws MarketException {
//	// this.getCategorieByName(catname).setItemAmmount(is, ammount);
//	// this.updateMarketItem(is, catname);
//	// }
//	//
//	// public void setItemSellAmmountInCat(String catname, MarketItem is, int
//	// sellAmmount) throws MarketException {
//	// this.getCategorieByName(catname).setItemSellAmmount(is, sellAmmount);
//	// this.save();
//	// }
//
//	public void setMarketOwner(Player marketOwner) {
//		this.MarketOwner = marketOwner;
//	}
//
//	public OfflinePlayer getMarketOwner() {
//		return this.MarketOwner;
//	}
//
//	public String getName() {
//		return this.name;
//	}
//
//	public void setName(String name) throws MarketException {
//		this.name = name;
//		this.saveMarketInfos();
//	}
//
//	public ItemStack getMarketItemStack() {
//		AIS ais = new AIS(Material.CHEST);
//		String devise = this.devise;
//		String name = this.name;
//		if (devise == null) {
//			if (name != null) {
//				ais.setName(ChatColor.translateAlternateColorCodes('&', name));
//				ais.addLineToLore("").addLineToLore("§r§6" + this.MarketOwner.getName() + "'s Market.");
//			} else {
//				ais.setName("§r§6" + this.MarketOwner.getName() + "'s Market.");
//			}
//		} else {
//			devise = ChatColor.translateAlternateColorCodes('&', devise);
//			if (name != null) {
//				ais.setName(ChatColor.translateAlternateColorCodes('&', name));
//				ais.addLineToLore("§r§6" + this.MarketOwner.getName() + "'s Market.").addLineToLore("")
//						.addLineToLore("§b" + devise);
//			} else {
//				ais.setName("§r§6" + this.MarketOwner.getName() + "'s Market.");
//				ais.addLineToLore("").addLineToLore("§b" + devise);
//			}
//		}
//		return ais.toIS();
//	}
//
//	public List<Category> getMarketCategories() {
//		return this.marketCategories;
//	}
//
//	public boolean isMarketOpen() {
//		return this.open;
//	}
//
//	public void setMarketOpen(boolean isMarketOpen) throws MarketException {
//		this.open = isMarketOpen;
//		this.saveMarketInfos();
//		if (this.getMarketOwner() == null)
//			return;
//		if (this.open) {
////			Itoomel.addMarketToItoomel(this);
//		} else {
//			if (this.getMarketOwner() != null) {
//				Itoomel.removeMarketFromItoomel(this.getMarketOwner().getUniqueId());
//			}
//		}
//	}
//
//	public String getDevise() {
//		return this.devise;
//	}
//
//	public long getSoldItems() {
//		return this.soldItems;
//	}
//
//	public void setSoldItems(long selledItems) {
//		this.soldItems = selledItems;
//	}
//
//	public long getSales() {
//		return this.sales;
//	}
//
//	public void setSales(long sales) {
//		this.sales = sales;
//	}
//
//	public boolean registeredIS(ItemStack currentItem) {
//		for (Category cat : this.marketCategories) {
//			if (cat.containsIS(currentItem)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public void addItem(ItemStack is) throws MarketException {
//		for (Category cat : this.marketCategories) {
//			if (cat.containsIS(is)) {
//				cat.addToItems(is);
//				return;
//			}
//		}
//		this.saveMarketItem(this.getCategoryByIS(is).getName(), this.getMISByIS(is));
//	}
//
//	public long removeItem(ItemStack is, long l) throws MarketException {
//		long leng = 0;
//		for (Category cat : this.marketCategories) {
//			if (cat.containsIS(is)) {
//				leng = cat.removeItems(is, l);
//				this.saveMarketItem(cat.getName(), cat.getMISbyIS(is));
//				return leng;
//			}
//		}
//		throw new MarketException(ErrorCause.MIS);
//	}
//
//	public Category getCategoryByIS(ItemStack is) throws MarketException {
//		for (Category cat : this.marketCategories) {
//			if (cat.containsIS(is)) {
//				return cat;
//			}
//		}
//		throw new MarketException(ErrorCause.CATNOTFOUND);
//	}
//
//	public long getAmmountOfMaterial(Material type) {
//		List<MarketItem> miss = new ArrayList<>();
//		for (Category cat : this.marketCategories) {
//			if (cat.containsType(type)) {
//				miss.addAll(cat.getMISsByType(type));
//			}
//		}
//		long i = 0;
//		for (MarketItem mis : miss) {
//			i = i + mis.getAmmount();
//		}
//		return i;
//	}
//
//	public List<MarketItem> getMISsByType(Material type) {
//		List<MarketItem> miss = new ArrayList<>();
//		for (Category cat : this.marketCategories) {
//			if (cat.containsType(type)) {
//				miss.addAll(cat.getMISsByType(type));
//			}
//		}
//		return miss;
//	}
//
//	public boolean isMisBuyableNow(MarketItem marketItem) {
//		for (MarketItem mis : getMISsByType(marketItem.getIs().getType())) {
//			if (mis.getIs().isSimilar(marketItem.getIs().clone())) {
//				if (mis.isBuyable()) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	public boolean has(MarketItem mis2, long leng) {
//		for (MarketItem mis : getMISsByType(mis2.getIs().getType())) {
//			if (mis.getIs().isSimilar(mis2.getIs().clone())) {
//				if (mis.isBuyable()) {
//					if (mis.getAmmount() - (leng * mis.getSellAmmount()) >= 0) {
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}
//
//	public MarketItem getMISByIS(ItemStack clone) throws MarketException {
//		for (MarketItem mis : getMISsByType(clone.getType())) {
//			if (clone.isSimilar(mis.getIs())) {
//				return mis;
//			}
//		}
//		throw new MarketException(ErrorCause.MIS);
//	}
//
//	public boolean isMisChanged(MarketItem marketItem) {
//		for (MarketItem mis : getMISsByType(marketItem.getIs().getType())) {
//			if (mis.getIs().isSimilar(marketItem.getIs().clone())) {
//				if (mis.isBuyable() == marketItem.isBuyable()) {
//					if (mis.getAmmount() == marketItem.getAmmount()) {
//						return false;
//					}
//				}
//			}
//		}
//		return true;
//	}
//
//	public MarketItem getMISByNB(int index, String cat) throws MarketException {
//		List<MarketItem> miss = getCategorieByName(cat).getCatItems();
//		Iterator<MarketItem> iterator = miss.iterator();
//		while (iterator.hasNext()) {
//			if (!iterator.next().isBuyable()) {
//				iterator.remove();
//			}
//		}
//		return miss.get(index);
//	}
//
//	public void resetStats() throws MarketException {
//		this.sales = 0;
//		this.soldItems = 0;
//		this.saveMarketInfos();
//
//	}
//
//	public List<MarketItem> getAllMIS() {
//		List<MarketItem> l = new ArrayList<>();
//		for (Category cat : this.getMarketCategories()) {
//			l.addAll(cat.getCatItems());
//		}
//		return l;
//	}
//
//	public List<ItemStack> getAllIS() {
//		List<ItemStack> l = new ArrayList<>();
//		for (Category cat : this.getMarketCategories()) {
//			l.addAll(cat.getAllIS());
//		}
//		return l;
//	}
//
//	public void buyed(long l, MarketItem mis) throws MarketException {
//		this.sales++;
//		this.soldItems += l;
//		if (mis.getMarketuuid() != "AdminShop") {
//			this.removeItem(mis.getIs(), l);
//		}
//	}
//
//	public static boolean compre(ItemStack is1, ItemStack is2) {
//		String is1String = is1.toString(), is2String = is2.toString();
//		if (is1String.contains(", internal=") || is2String.contains(", internal=")) {
//			if (is1String.contains(", internal=")) {
//				String[] split = is1String.split(", internal=");
//				is1String = split[0] + "}}";
//			}
//			if (is2String.contains(", internal=")) {
//				String[] split = is2String.split(", internal=");
//				is2String = split[0] + "}}";
//			}
//			if (is1.getDurability() != is2.getDurability()) {
//				return false;
//			}
//			return is1String.equals(is2String);
//		} else {
//			return is1.isSimilar(is2);
//		}
//
//	}
//
//	public List<MarketItem> getMISsByTypeRest(Material type) {
//		List<MarketItem> miss = new ArrayList<>();
//		for (Category cat : this.marketCategories) {
//			if (cat.containsType(type)) {
//				miss.addAll(cat.getMISsByType(type));
//			}
//		}
//		Iterator<MarketItem> iterator = miss.iterator();
//		while (iterator.hasNext()) {
//			if (!iterator.next().isBuyable()) {
//				iterator.remove();
//			}
//		}
//		return miss;
//	}
//
//	public List<MarketItem> getMISsByTypeBuyable(Material type) {
//		List<MarketItem> miss = new ArrayList<>();
//		for (Category cat : this.marketCategories) {
//			if (cat.containsType(type)) {
//				miss.addAll(cat.getMISsByTypeBuyable(type));
//			}
//		}
//		return miss;
//	}
//
//	public HashMap<Material, List<MarketItem>> getMaterialMISsBuyable() {
//		HashMap<Material, List<MarketItem>> matsMISs = new HashMap<>();
//		for (Material mat : this.getAllMaterialsBuyable()) {
//			matsMISs.put(mat, this.getMISsByTypeBuyable(mat));
//		}
//		return matsMISs;
//	}
//
//	public List<Material> getAllMaterialsBuyable() {
//		List<Material> l = new ArrayList<>();
//		for (Category cat : this.getMarketCategories()) {
//			for (MarketItem mis : cat.getCatItems()) {
//				if (mis.isBuyable()) {
//					if (!l.contains(mis.getIs().getType())) {
//						l.add(mis.getIs().getType());
//					}
//				}
//			}
//		}
//		return l;
//	}
//
//	public List<MarketItem> getMISsByTypeRestSubID(Material type, int mode) {
//		List<MarketItem> miss = new ArrayList<>();
//		for (Category cat : this.marketCategories) {
//			if (cat.containsType(type)) {
//				miss.addAll(cat.getMISsByType(type));
//			}
//		}
//		Iterator<MarketItem> iterator = miss.iterator();
//		MarketItem mis = null;
//		while (iterator.hasNext()) {
//			mis = iterator.next();
//			System.out.println(
//					mis.isBuyable() + " " + mis.getIs().getDurability() + " =?= " + mode + " " + type.toString());
//			if ((!mis.isBuyable()) || (mis.getIs().getDurability() != mode)) {
//				iterator.remove();
//			}
//		}
//		return miss;
//	}
//
//	public long getAmmountOfMaterialSubID(Material type, Short s) {
//		List<MarketItem> miss = new ArrayList<>();
//		for (Category cat : this.marketCategories) {
//			if (cat.containsType(type)) {
//				miss.addAll(cat.getMISsByType(type));
//			}
//		}
//		long i = 0;
//		for (MarketItem mis : miss) {
//			if (mis.getIs().getDurability() == s) {
//				i = i + mis.getAmmount();
//			}
//		}
//		return i;
//	}
}