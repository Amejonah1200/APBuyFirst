package ap.apb.apbuy.markets;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ap.apb.AIS;
import ap.apb.APBuy;

public class MarketItem {

	private ItemStack is;
	private int price;
	private long ammount;
	private int sellAmmount;
	private long soldItems;
	private String marketuuid;
	private String catName;

	public MarketItem(String uuid, String catname) {
		this.is = null;
		this.price = 0;
		this.ammount = 0;
		this.sellAmmount = 1;
		this.soldItems = 0;
		this.marketuuid = uuid;
		this.setCatName(catname);
	}

	public MarketItem(ItemStack is, String uuid, int price, long ammount, int sellAmmount, long selledItems,
			String catname) {
		this.is = is;
		this.price = price;
		this.ammount = ammount;
		this.sellAmmount = sellAmmount;
		this.soldItems = selledItems;
		this.marketuuid = uuid;
		this.setCatName(catname);
	}

	public String getCatName() {
		return this.catName;
	}

	public MarketItem setCatName(String catName) {
		this.catName = catName;
		return this;
	}

	public boolean isBuyable() {
		return (this.ammount >= this.sellAmmount) || (this.marketuuid == "AdminShop");
	}

	public ItemStack getIs() {
		return this.is;
	}

	public MarketItem setIs(ItemStack is) {
		this.is = is;
		return this;
	}

	public int getPrice() {
		return this.price;
	}

	public MarketItem setPrice(int price) {
		this.price = price;
		return this;
	}

	public MarketItem setAmmount(long ammount) {
		this.ammount = ammount;
		return this;
	}

	public long getAmmount() {
		return this.ammount;
	}

	public int getSellAmmount() {
		return this.sellAmmount;
	}

	public long getSoldItems() {
		return this.soldItems;
	}

	public MarketItem setSoldItems(long soldItems) {
		this.soldItems = soldItems;
		return this;
	}

	public MarketItem setSellAmmount(int sellAmmount) {
		this.sellAmmount = sellAmmount;
		return this;
	}

	public AIS getAISToShow() {
		if (this.getIs() == null) {
			return new AIS(Material.CHEST).addLineToLore("")
					.addLineToLore("�7Preis �8\\�7 " + this.getSellAmmount() + " �8:�6 " + this.getPrice())
					.addLineToLore("�7Items im Lager�8: "
							+ (this.getAmmount() == 0 ? "�cKeins mehr da!" : "�6" + this.getAmmount()));
		}
		return new AIS(this.getIs().clone()).addLineToLore("")
				.addLineToLore("�7Preis �8\\�7 " + this.getSellAmmount() + " �8:�6 " + this.getPrice())
				.addLineToLore("�7Items im Lager�8: "
						+ (this.getAmmount() == 0 ? "�cKeins mehr da!" : "�6" + this.getAmmount()));

	}

	public String getMarketuuid() {
		return this.marketuuid;
	}

	public void save() throws MarketException {
		APBuy.database.saveItemInfos(this.marketuuid, this.catName, this.is, this.price, this.ammount, this.sellAmmount,
				this.soldItems);
	}

	public MarketItem grantAmount(long amount) {
		this.ammount = this.ammount + amount;
		return this;
	}

}