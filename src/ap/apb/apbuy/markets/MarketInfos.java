package ap.apb.apbuy.markets;

import ap.apb.APBuy;

public class MarketInfos {

	private String marketOwner;
	private String name;
	private String devise;
	private boolean open;
	private long soldItems;
	private long sales;

	public MarketInfos(String marketOwner, String name, String devise, boolean open, long soldItems, long sales) {
		this.marketOwner = marketOwner;
		this.name = name;
		this.devise = devise;
		this.open = open;
		this.soldItems = soldItems;
		this.sales = sales;
	}

	public String getMarketOwner() {
		return this.marketOwner;
	}

	public String getName() {
		return this.name;
	}

	public String getDevise() {
		return this.devise;
	}

	public boolean isOpen() {
		return this.open;
	}

	public long getSoldItems() {
		return this.soldItems;
	}

	public long getSales() {
		return this.sales;
	}

	public void save() throws MarketException {
		APBuy.database.saveMarketInfos(marketOwner, open, name, devise, sales, soldItems);
	}

	public MarketInfos setName(String name) {
		this.name = name;
		return this;
	}

	public MarketInfos setDevise(String devise) {
		this.devise = devise;
		return this;
	}

	public MarketInfos setOpen(boolean open) {
		this.open = open;
		return this;
	}

	public MarketInfos setSoldItems(long soldItems) {
		this.soldItems = soldItems;
		return this;
	}

	public MarketInfos setSales(long sales) {
		this.sales = sales;
		return this;
	}

	public MarketInfos grantSalesOne() {
		this.sales = this.sales + 1;
		return this;
	}

	public MarketInfos grandSoldItems(long amount) {
		this.soldItems = this.soldItems + amount;
		return this;
	}
}
