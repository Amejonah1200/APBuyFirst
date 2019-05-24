package ap.apb.apbuy.itoomel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import ap.apb.APBuy;
import ap.apb.Utils;
import ap.apb.apbuy.markets.Market;
import ap.apb.apbuy.markets.MarketItem;

public class Itoomel implements Listener {

	private List<MarketItem> itoomelitems;
	private List<ItoomelNavigation> itoomelNav;

	public Itoomel() {
		itoomelitems = new ArrayList<>();
		itoomelNav = new ArrayList<>();
		APBuy.setRemoveGen(!APBuy.isGeneralStop());
		APBuy.setGeneralStop(true);
		this.load();
	}

	public void load() {
		try {
			List<Market> l = loadAllMarkets(APBuy.getMarketHandler().getAllMarketsOnline());
			List<MarketItem> miss = new ArrayList<>();
			for (Market m : l) {
				for (MarketItem mis : m.getMarketItems()) {
					if (mis.isBuyable()) {
						miss.add(mis);
					}
				}
			}
			itoomelitems = miss;
			APBuy.setGeneralStop(!APBuy.plugin.isRemoveGen());
			System.out.println("[APB] Itoomel finished.");
		} catch (Exception e1) {
			e1.printStackTrace();
			Utils.addToFix(e1);
		}
	}

	private List<Market> loadAllMarkets(List<UUID> uuids) {
		List<Market> markets = new ArrayList<>();
		for (UUID uuid : uuids) {
			try {
				markets.add(new Market(uuid.toString(), true));
			} catch (Exception e) {
			}
		}
		return markets;
	}

	public List<MarketItem> getMissByMat(Material material) {
		List<MarketItem> miss = new ArrayList<>();
		for (MarketItem mis : itoomelitems) {
			if (mis.getIs().getType().equals(material)) {
				miss.add(mis);
			}
		}
		return miss;
	}

	public List<MarketItem> getMissByIS(ItemStack is) {
		List<MarketItem> miss = new ArrayList<>();
		for (MarketItem mis : itoomelitems) {
			if (mis.getIs().isSimilar(is)) {
				miss.add(mis);
			}
		}
		return miss;
	}

	public void removeMarket(String uuid) {
		Iterator<MarketItem> iterator = itoomelitems.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getMarketuuid().equals(uuid)) {
				iterator.remove();
			}
		}
	}

	public void removeMisByMarketNIS(ItemStack is, String uuid) {
		Iterator<MarketItem> iterator = itoomelitems.iterator();
		MarketItem mis;
		while (iterator.hasNext()) {
			mis = iterator.next();
			if (mis.getMarketuuid().equals(uuid) && mis.getIs().isSimilar(is)) {
				iterator.remove();
			}
		}
	}

	public MarketItem getMisByMarketNIS(ItemStack is, String uuid) {
		for (MarketItem mis : itoomelitems) {
			if (mis.getIs().isSimilar(is) && mis.getMarketuuid().equals(uuid)) {
				return mis;
			}
		}
		return null;
	}

	public MarketItem getMisBySimilarMis(MarketItem mis1) {
		for (MarketItem mis : itoomelitems) {
			if (mis.isSimilar(mis1)) {
				return mis;
			}
		}
		return null;
	}

	public void updateMis(MarketItem mis) {
		if (mis.getAmmount() == 0) {
			removeMisByMarketNIS(mis.getIs(), mis.getMarketuuid());
		} else {
			if (getMisBySimilarMis(mis) == null) {
				addMis(mis);
			} else
				for (MarketItem misa : itoomelitems) {
					if (misa.isSimilar(mis)) {
						misa.setAmmount(mis.getAmmount());
					}
				}
		}
	}

	public void addMis(MarketItem mis) {
		itoomelitems.add(mis);
	}

	public void addMarket(Market market) {
		for (MarketItem mis : market.getMarketItems()) {
			if (mis.isBuyable()) {
				itoomelitems.add(mis);
			}
		}
	}

	public boolean isMarketInItoomel(String uuid) {
		for (MarketItem mis : itoomelitems) {
			if (mis.getMarketuuid().equals(uuid)) {
				return true;
			}
		}
		return false;
	}

	public static Itoomel getInstance() {
		return APBuy.itoomel;
	}

	public void removeFromNav(Player player) {
		Iterator<ItoomelNavigation> iterator = itoomelNav.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getPlayer().equals(player)) {
				iterator.remove();
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		for (ItoomelNavigation nav : itoomelNav) {
			if (nav.getPlayer().equals(e.getWhoClicked())) {
				nav.onClick(e);
				break;
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		for (ItoomelNavigation nav : itoomelNav) {
			if (e.getPlayer().equals(nav.getPlayer())) {
				nav.onClose();
				break;
			}
		}
	}

	public void openNav(ItoomelNavigation nav) {
		removeFromNav(nav.getPlayer());
		nav.open();
		itoomelNav.add(nav);
	}

	public List<MarketItem> getAllMisFromNSize(int fromID, int size) {
		List<MarketItem> list = new ArrayList<>();
		if (fromID < itoomelitems.size()) {
			for (int i = fromID; i < itoomelitems.size(); i++) {
				if (i == fromID + size) {
					break;
				}
				list.add(itoomelitems.get(i));
			}
		}
		return list;
	}

	public int getPages() {
		return ((itoomelitems.size() - (itoomelitems.size() % 28)) / 28);
	}

}
