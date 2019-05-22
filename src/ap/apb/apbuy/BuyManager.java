package ap.apb.apbuy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import ap.apb.AIS;
import ap.apb.APBuy;
import ap.apb.Translator;
import ap.apb.Utils;
import ap.apb.apbuy.itoomel.ItoomelPrime;
import ap.apb.apbuy.markets.Market;
import ap.apb.apbuy.markets.MarketItem;

public class BuyManager {

	public static List<BuyManager> onBuying = new ArrayList<>();

	private MarketItem marketItem;
	private String marketsOwner;
	private long wantToBuy;
	private Player buyer;

	private boolean thenInItoomel;
	private String[] s = null;

	public BuyManager(MarketItem marketItem, String market, long wantToBuy, Player buyer) {
		this.marketItem = marketItem;
		this.marketsOwner = market;
		this.wantToBuy = 1;
		this.buyer = buyer;
	}

	public BuyManager(MarketItem marketItem, String market, long wantToBuy, Player buyer, String[] mv) {
		this.marketItem = marketItem;
		this.marketsOwner = market;
		this.wantToBuy = 1;
		this.buyer = buyer;
		this.s = mv;
	}

	public BuyManager(MarketItem marketItem, String market, Player buyer) {
		this.marketItem = marketItem;
		this.marketsOwner = market;
		this.wantToBuy = 0;
		this.buyer = buyer;
	}

	public long getWantToBuy() {
		return this.wantToBuy;
	}

	public void setWantToBuy(long wantToBuy) {
		this.wantToBuy = wantToBuy;
	}

	public MarketItem getMarketItem() {
		return this.marketItem;
	}

	public String getMarket() {
		return this.marketsOwner;
	}

	public Player getBuyer() {
		return this.buyer;
	}

	public static boolean openBuyManager(MarketItem marketItem, String market, long wantToBuy, Player buyer,
			boolean thenInItoomel, String[] mv) {
		if (APBuy.database.hasPlayerMarketByUUID(market)) {
			if (!APBuy.database.getMarketInfos(market).isOpen()) {
				buyer.sendMessage(Translator.translate("buymanager.marketclose"));
				return false;
			}
		} else {
			buyer.sendMessage(Translator.translate("buymanager.nomarket"));
			return false;
		}
		if (buyer.getUniqueId().toString().equalsIgnoreCase(market)) {
			buyer.sendMessage(Translator.translate("buymanager.selfbuy"));
			return false;
		}
		if (marketItem == null) {
			buyer.sendMessage(Translator.translate("buymanager.noavailable"));
			return false;
		}
		if ((!marketItem.isBuyable()) && (!marketItem.getMarketuuid().equals("AdminShop"))) {
			buyer.sendMessage(Translator.translate("buymanager.sold"));
			return false;
		}
		BuyManager bm = new BuyManager(marketItem, market, wantToBuy, buyer, mv);
		bm.thenInItoomel = thenInItoomel;
		if (isBuying(buyer)) {
			BuyManager bm1 = getBMbyPlayer(buyer);
			onBuying.remove(bm1);
			onBuying.add(bm);
			bm.openBuyManager();
		} else {
			onBuying.add(bm);
			bm.openBuyManager();
		}
		return true;
	}

	public void openBuyManager() {
		try {
			Inventory inv = Bukkit.createInventory(null, 54, Translator.translate("menu.title.buymanager"));
			for (int i = 0; i < 54; i++) {
				inv.setItem(i, new AIS("§a", 1, (short) 15, Material.STAINED_GLASS_PANE).toIS());
			}
			inv.setItem(4, this.getMarketItem().getAISToShow().removeLatestLore(1).toIS());
			inv.setItem(10, new AIS("§4-64", 1, (short) 14, Material.STAINED_GLASS_PANE).toIS());
			inv.setItem(11, new AIS("§4-16", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
			inv.setItem(12, new AIS("§4-1", 1, (short) 1, Material.STAINED_GLASS_PANE).toIS());
			inv.setItem(13, new AIS(Translator.translate("buymanager.buypackets", new Object[] { this.getWantToBuy() }),
					1, this.getMarketItem().getIs().getType()).toIS());
			inv.setItem(14, new AIS("§4+1", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
			inv.setItem(15, new AIS("§4+16", 1, (short) 5, Material.STAINED_GLASS_PANE).toIS());
			inv.setItem(16, new AIS("§4+64", 1, (short) 13, Material.STAINED_GLASS_PANE).toIS());

			inv.setItem(28,
					(this.getMarketItem().isBuyable()) && (this.getMarketItem().getMarketuuid() == "AdminShop")
							? this.getMarketItem().getAISToShow().removeLatestLore(1).toIS()
							: this.getMarketItem().getAISToShow().toIS());

			inv.setItem(34,
					new AIS("§7Dein Konto: §6" + APBuy.ecohandler.getPlayerBalance(this.getBuyer()), 1, Material.PAPER)
							.toIS());

			inv.setItem(40,
					new AIS(this.getMarketItem().getIs().clone()).addLineToLore("")
							.addLineToLore(Translator.translate("buymanager.summary")).addLineToLore("")
							.addLineToLore(Translator.translate("buymanager.tobuy") + ": §6"
									+ (this.getMarketItem().getSellAmmount() * this.getWantToBuy()))
							.addLineToLore(Translator.translate("buymanager.price") + ": §6" + this.deductPrice())
							.toIS().clone());

			inv.setItem(48, new AIS(Translator.translate("buymanager.cancel"), 1, (short) 14, Material.WOOL).toIS());
			inv.setItem(50, new AIS(Translator.translate("buymanager.buy"), 1, (short) 5, Material.WOOL).toIS());
			onBuying.remove(this);
			this.getBuyer().openInventory(inv);
			onBuying.add(this);
		} catch (Exception e1) {
			this.cancel();
			e1.printStackTrace();
			System.out.println(
					"Player: " + this.getBuyer().getName() + " (" + this.getBuyer().getUniqueId().toString() + ")");
			this.getBuyer().closeInventory();
			APBuy.getMarketHandler().removeFromAll(this.getBuyer());
			this.getBuyer().sendMessage(Translator.translate("dev.error"));
			this.getBuyer().sendMessage("§cError Code: " + Utils.addToFix(e1));
		}
	}

	public void onClick(int slot) {
		try {
			long ammount = this.wantToBuy;
			switch (slot) {
			case 50:
				if (!APBuy.database.hasPlayerMarketByUUID(this.getMarket())) {
					this.getBuyer().sendMessage(Translator.translate("buymanager.nomarket"));
					this.cancel();
					if (this.thenInItoomel) {
						ItoomelPrime.openItoomel("Main", this.getBuyer(), this.getMarketItem().getIs().getType(), 0);
					} else {
						APBuy.getMarketHandler().removeFromAll(this.getBuyer());
						APBuy.getMarketHandler().PMLocPage.put(this.getBuyer(), 0);
						APBuy.getMarketHandler().openMarketVisualiserToPlayer(this.s[0], this.s[1], this.getBuyer());
					}
					return;
				}
				Market m = new Market(this.getMarket(), true);
				if (APBuy.ecohandler.has(this.getBuyer(), this.deductPrice())) {
					if (!m.isOpen()) {
						this.getBuyer().sendMessage(Translator.translate("buymanager.marketclose"));
						this.cancel();
						if (this.thenInItoomel) {
							ItoomelPrime.openItoomel("Main", this.getBuyer(), this.getMarketItem().getIs().getType(), 0);
						} else {
							APBuy.getMarketHandler().removeFromAll(this.getBuyer());
							APBuy.getMarketHandler().PMLocPage.put(this.getBuyer(), 0);
							APBuy.getMarketHandler().openMarketVisualiserToPlayer(this.s[0], this.s[1],
									this.getBuyer());
						}
						return;
					}
					if (!m.isMisChanged(this.getMarketItem())) {
						if (m.has(this.getMarketItem(), this.getWantToBuy())
								|| this.getMarket().equalsIgnoreCase("AdminShop")) {
							this.prozessBuy();
						} else {
							this.getBuyer().sendMessage(Translator.translate("buymanager.tomany"));
						}
					} else {
						this.Update();
						this.getBuyer().sendMessage(Translator.translate("buymanager.nbchanged"));
						if (this.getMarketItem() == null) {
							this.getBuyer().sendMessage(Translator.translate("buymanager.nomore"));
							this.cancel();
							if (this.thenInItoomel) {
								ItoomelPrime.openItoomel("Main", this.getBuyer(), this.getMarketItem().getIs().getType(), 0);
							} else {
								APBuy.getMarketHandler().removeFromAll(this.getBuyer());
								APBuy.getMarketHandler().PMLocPage.put(this.getBuyer(), 0);
								APBuy.getMarketHandler().openMarketVisualiserToPlayer(this.s[0], this.s[1],
										this.getBuyer());
							}
							return;
						}
						if (this.getMarketItem().isBuyable()) {
							this.getBuyer().sendMessage("§aUuuff, ja. Kannst du!");
							if (m.has(this.getMarketItem(), this.getWantToBuy())
									|| this.getMarket().equalsIgnoreCase("AdminShop")) {
								this.prozessBuy();
							} else {
								this.getBuyer().sendMessage(Translator.translate("buymanager.tomany"));
							}
						} else {
							this.getBuyer().sendMessage(Translator.translate("buymanager.nomore"));
							this.cancel();
							if (this.thenInItoomel) {
								ItoomelPrime.openItoomel("Main", this.getBuyer(), this.getMarketItem().getIs().getType(), 0);
							} else {
								APBuy.getMarketHandler().removeFromAll(this.getBuyer());
								APBuy.getMarketHandler().PMLocPage.put(this.getBuyer(), 0);
								APBuy.getMarketHandler().openMarketVisualiserToPlayer(this.s[0], this.s[1],
										this.getBuyer());
							}
						}
					}
				} else {
					this.getBuyer().sendMessage(Translator.translate("buymanager.nomonney"));
				}
				break;
			case 48:
				this.cancel();
				if (this.thenInItoomel) {
					ItoomelPrime.openItoomel("Main", this.getBuyer(), this.getMarketItem().getIs().getType(), 0);
				} else {
					APBuy.getMarketHandler().removeFromAll(this.getBuyer());
					APBuy.getMarketHandler().PMLocPage.put(this.getBuyer(), 0);
					APBuy.getMarketHandler().openMarketVisualiserToPlayer(this.s[0], this.s[1], this.getBuyer());
				}
				break;
			case 10:
				ammount = ammount > 64 ? ammount - 64 : 1;
				this.setWantToBuy(ammount);
				onBuying.remove(this);
				this.openBuyManager();
				onBuying.add(this);
				break;
			case 11:
				ammount = ammount > 16 ? ammount - 16 : 1;
				this.setWantToBuy(ammount);
				onBuying.remove(this);
				this.openBuyManager();
				onBuying.add(this);
				break;
			case 12:
				ammount = ammount > 1 ? ammount - 1 : 1;
				this.setWantToBuy(ammount);
				onBuying.remove(this);
				this.openBuyManager();
				onBuying.add(this);
				break;
			case 14:
				ammount = ammount + 1;
				this.setWantToBuy(ammount);
				onBuying.remove(this);
				this.openBuyManager();
				onBuying.add(this);
				break;
			case 15:
				ammount = ammount + 16;
				this.setWantToBuy(ammount);
				onBuying.remove(this);
				this.openBuyManager();
				onBuying.add(this);
				break;
			case 16:
				ammount = ammount + 64;
				this.setWantToBuy(ammount);
				onBuying.remove(this);
				this.openBuyManager();
				onBuying.add(this);
				break;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println(
					"Player: " + this.getBuyer().getName() + " (" + this.getBuyer().getUniqueId().toString() + ")");
			this.getBuyer().closeInventory();
			APBuy.getMarketHandler().removeFromAll(this.getBuyer());
			this.getBuyer().sendMessage(Translator.translate("dev.error"));
			this.getBuyer().sendMessage("§cError Code: " + Utils.addToFix(e1));
		}
	}

	public void prozessBuy() {
		// TODO Analyze BuyManager When Item not Available or Bought
		try {
			if (Utils.getPlaceForIS(this.getBuyer(), this.getWantToBuy() * this.getMarketItem().getSellAmmount(),
					this.getMarketItem().getIs()) >= this.getWantToBuy() * this.getMarketItem().getSellAmmount()) {
				Market m = new Market(this.getMarket(), true);
				short sh = this.getMarket().equalsIgnoreCase("AdminShop")
						? APBuy.ecohandler.withdrawPlayer(this.getBuyer(), this.deductPrice())
						: APBuy.ecohandler.transPtoP(this.getBuyer(), this.deductPrice(),
								Bukkit.getOfflinePlayer(UUID.fromString(m.getMarketOwner())));
				if (sh == 0) {
					if ((!this.getMarket().equals("AdminShop")) && (this.getMarket() != null)) {
						this.getBuyer().sendMessage(Translator.translate("buymanager.thxmarket"));
						m.removeItem(this.getMarketItem().getIs(), this.getWantToBuy() * this.getMarketItem().getSellAmmount());
						if (this.getMarketItem().getAmmount()
								- (this.getWantToBuy() * this.getMarketItem().getSellAmmount()) == 0) {
							ItoomelPrime.removeMISFromItoomel(this.getMarketItem().getIs(), this.getMarket());
						} else {
							this.getMarketItem().setAmmount(this.getMarketItem().getAmmount()
									- (this.getWantToBuy() * this.getMarketItem().getSellAmmount()));
							ItoomelPrime.replaceMISInItoomel(this.getMarketItem());
						}
					} else {
						this.getBuyer().sendMessage(Translator.translate("buymanager.thxadminshop"));
					}
					Utils.addItemToPlayer(this.getBuyer(), this.getMarketItem().getIs(),
							(int) (this.getWantToBuy() * this.getMarketItem().getSellAmmount()));
					if (this.getMarket() != "AdminShop") {
						if ((Bukkit.getPlayer(UUID.fromString(this.getMarket())) != null)) {

							Bukkit.getPlayer(UUID.fromString(this.getMarket()))
									.sendMessage(Translator.translate("buymanager.notif",
											new Object[] { this.getBuyer().getName(),
													this.getMarketItem().getIs().getItemMeta().getDisplayName() == null
															? this.getMarketItem().getIs().getType().toString()
															: this.getMarketItem().getIs().getItemMeta()
																	.getDisplayName().toString(),
													this.getMarketItem().getSellAmmount(), this.getWantToBuy(),
													this.deductPrice() }));
						}
					}
					m.buyed(this.getMarketItem().getIs(), this.getWantToBuy() * this.getMarketItem().getSellAmmount());
					this.cancel();
					if (!this.getMarket().equalsIgnoreCase("AdminShop")) {
						APBuy.getMarketHandler().reopenMarketToWhoSee(this.getMarket());
					}
				} else {
					this.getBuyer().sendMessage(Translator.translate("buymanager.error1"));
					this.getBuyer().sendMessage(Translator.translate("buymanager.error2") + ":");
					this.getBuyer().sendMessage("§4   Error Code : " + sh);
					this.getBuyer().sendMessage("§4   " + Translator.translate("buymanager.buyer") + " : "
							+ this.getBuyer().getName().toString());
					this.getBuyer().sendMessage("§4              " + this.getBuyer().getUniqueId().toString());
					if ((this.getMarket() != "AdminShop") && (this.getMarket() != null)) {
						this.getBuyer().sendMessage(
								"§4   " + Translator.translate("buymanager.seller") + " : " + m.getMarketOwner());
						this.getBuyer().sendMessage("§4                  " + m.getMarketOwner());
					}
					this.cancel();
				}

			} else {
				this.getBuyer().sendMessage(Translator.translate("buymanager.noplace"));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println(
					"Player: " + this.getBuyer().getName() + " (" + this.getBuyer().getUniqueId().toString() + ")");
			this.getBuyer().closeInventory();
			APBuy.getMarketHandler().removeFromAll(this.getBuyer());
			this.getBuyer().sendMessage(Translator.translate("dev.error"));
			this.getBuyer().sendMessage("§cError Code: " + Utils.addToFix(e1));
			this.cancel();
		}
	}

	public void Update() {
		try {
			onBuying.remove(this);
			if (APBuy.getMarketHandler().hasMarketByUUID(this.getMarket())) {
				this.setMarketItem(
						new Market(this.getMarket(), false).getMarketItemByIS(this.marketItem.getIs().clone()));
			} else {
				this.setMarketItem(null);
			}
			this.openBuyManager();
			onBuying.add(this);
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println(
					"Player: " + this.getBuyer().getName() + " (" + this.getBuyer().getUniqueId().toString() + ")");
			this.getBuyer().closeInventory();
			APBuy.getMarketHandler().removeFromAll(this.getBuyer());
			this.getBuyer().sendMessage(Translator.translate("dev.error"));
			this.getBuyer().sendMessage("§cError Code: " + Utils.addToFix(e1));
			Utils.addToFix(e1);
			this.cancel();
		}
	}

	public void cancel() {
		removeBuyer(this.getBuyer());
		APBuy.getMarketHandler().removeFromAll(this.getBuyer());
		this.getBuyer().closeInventory();
	}

	public double deductPrice() {
		return this.getWantToBuy() * this.getMarketItem().getPrice();
	}

	public static boolean isBuying(Player p) {
		for (BuyManager bm : onBuying) {
			if (bm.getBuyer() == p) {
				return true;
			}
		}
		return false;
	}

	public static BuyManager getBMbyPlayer(Player p) {
		for (BuyManager bm : onBuying) {
			if (bm.getBuyer() == p) {
				return bm;
			}
		}
		return null;
	}

	public static void removeBuyer(Player p) {
		Iterator<BuyManager> iterator = onBuying.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getBuyer() == p) {
				iterator.remove();
			}
		}
	}

	public void setMarketItem(MarketItem marketItem) {
		this.marketItem = marketItem;
	}

	public void setMarket(String market) {
		this.marketsOwner = market;
	}
}
