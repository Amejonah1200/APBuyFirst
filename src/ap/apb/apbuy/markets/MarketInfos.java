package ap.apb.apbuy.markets;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import ap.apb.AIS;
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
		System.out.println("b.b");
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
		System.out.println("a.a: " + open);
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

	public AIS getMarketAIS() {
		AIS ais = new AIS(Material.CHEST);
		String devise = this.getDevise();
		String name = this.getName();
		String shopname = (this.getMarketOwner().equalsIgnoreCase("AdminShop") ? "AdminShop"
				: Bukkit.getOfflinePlayer(UUID.fromString(this.getMarketOwner())).getName());
		if (devise == null) {
			if (name != null) {
				ais.setName(ChatColor.translateAlternateColorCodes('&', name));
				ais.addLineToLore("").addLineToLore("§r§6" + shopname + "'s Market.");
			} else {
				ais.setName("§r§6" + shopname + "'s Market.");
			}
		} else {
			devise = ChatColor.translateAlternateColorCodes('&', devise);
			if (name != null) {
				ais.setName(ChatColor.translateAlternateColorCodes('&', name));
				ais.addLineToLore("§r§6" + shopname + "'s Market.").addLineToLore("").addLineToLore("§b" + devise);
			} else {
				ais.setName("§r§6" + shopname + "'s Market.");
				ais.addLineToLore("").addLineToLore("§b" + devise);
			}
		}
		return ais;
	}

	public void resetStats() throws MarketException {
		this.sales = 0;
		this.soldItems = 0;
		this.save();
	}
}
