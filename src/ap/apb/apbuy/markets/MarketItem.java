package ap.apb.apbuy.markets;

import ap.apb.AIS;
import ap.apb.APBuy;
import ap.apb.APBuyException;
import ap.apb.Utils;
import ap.apb.datamaster.Datamaster;
import ap.apb.datamaster.SQLDatabase;
import ap.apb.datamaster.SQLiteDatabase;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.UUID;

public class MarketItem {

	private ItemStack is;
	private int price;
	private long amount;
	private int sellAmount;
	private long soldItems;
	private String marketuuid;
	private String catName;
	private UUID uuid;

	public MarketItem(String uuid, String catname) {
		this.is = null;
		this.price = 0;
		this.amount = 0;
		this.sellAmount = 1;
		this.soldItems = 0;
		this.marketuuid = uuid;
		this.setCatName(catname);
		this.setUuid(UUID.randomUUID());

	}

	public MarketItem(UUID uuid, ItemStack is, String owner, int price, long ammount, int sellAmmount, long selledItems,
			String catname) {
		this.is = is;
		this.price = price;
		this.amount = ammount;
		this.sellAmount = sellAmmount;
		this.soldItems = selledItems;
		this.marketuuid = owner;
		this.setCatName(catname);
		this.setUuid(uuid);
	}

	public String getCatName() {
		return this.catName;
	}

	public MarketItem setCatName(String catName) {
		this.catName = catName;
		return this;
	}

	public boolean isBuyable() {
		return (this.amount >= this.sellAmount) || (this.marketuuid == "AdminShop");
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
		this.amount = ammount;
		return this;
	}

	public long getAmmount() {
		return this.amount;
	}

	public int getSellAmmount() {
		return this.sellAmount;
	}

	public long getSoldItems() {
		return this.soldItems;
	}

	public MarketItem setSoldItems(long soldItems) {
		this.soldItems = soldItems;
		return this;
	}

	public MarketItem setSellAmmount(int sellAmmount) {
		this.sellAmount = sellAmmount;
		return this;
	}

	public AIS getAISToShow() {
		return new AIS(this.getIs() == null ? new ItemStack(Material.CHEST) : this.getIs().clone()).addLineToLore("")
				.addLineToLore("�7Preis �8\\�7 " + this.getSellAmmount() + " �8:�6 " + this.getPrice())
				.addLineToLore("�7Items im Lager�8: "
						+ (this.getAmmount() == 0 ? "�cKeins mehr da!" : "�6" + this.getAmmount()));

	}

	public String getMarketuuid() {
		return this.marketuuid;
	}

	public void save() throws APBuyException {
		APBuy.getDatamaster().getDatabase().saveItemInfos(this.uuid, this.marketuuid, this.catName, this.is, this.price,
				this.amount, this.sellAmount, this.soldItems);
	}

	public MarketItem grantAmount(long amount) {
		this.amount = this.amount + amount;
		return this;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public boolean isSimilar(MarketItem mis) {
		return mis.getIs().isSimilar(this.getIs()) && mis.getMarketuuid().equals(this.getMarketuuid());
	}

	public void save(SQLDatabase db) {
		try {
			db.getConnection()
					.prepareStatement(
							"REPLACE INTO " + Datamaster.getAPBuy_MItemsTableName(db instanceof SQLiteDatabase)
									+ " (id, owner, category, price, amount, sellamount, solditems, itemstack) VALUES ('"
									+ this.getUuid().toString() + "', '" + this.getMarketuuid() + "', '"
									+ this.getCatName() + "', " + price + ", " + amount + ", " + sellAmount + ", "
									+ soldItems + ", '" + Utils.serializeItemStack(is) + "');")
					.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
