package ap.apb.apbuy.itoomel;

import ap.apb.APBuy;
import ap.apb.APBuyException;
import ap.apb.Utils;
import ap.apb.apbuy.itoomel.ItoomelNavigation.ItoomelMenu;
import ap.apb.apbuy.markets.Market;
import ap.apb.apbuy.markets.MarketItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Itoomel implements Listener {

	private List<MarketItem> itoomelitems;
	private List<ItoomelNavigation> itoomelNav;

	public Itoomel() {
		itoomelitems = new ArrayList<>();
		itoomelNav = new ArrayList<>();
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
			System.out.println(mis.getMarketuuid() + " " + mis.getIs().toString());
			if (mis.getMarketuuid().equals(uuid) && mis.getIs().isSimilar(is)) {
				System.out.println("remove");
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
			} else {
				for (MarketItem misa : itoomelitems) {
					if (misa.isSimilar(mis)) {
						misa.setAmmount(mis.getAmmount());
					}
				}
			}
		}
	}

	public void addMis(MarketItem mis) {
		itoomelitems.add(mis);
	}

	public void addMarket(Market market) throws APBuyException {
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
		if (e.getClickedInventory() == e.getView().getTopInventory()) {
			for (ItoomelNavigation nav : itoomelNav) {
				if (nav.getPlayer().equals(e.getWhoClicked())) {
					nav.onTopInvClick(e);
					break;
				}
			}
		} else {
			for (ItoomelNavigation nav : itoomelNav) {
				if (nav.getPlayer().equals(e.getWhoClicked())) {
					nav.onBottomInvClick(e);
					break;
				}
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
		System.out.println("Perform open: " + nav.getMenu().toString());
		removeFromNav(nav.getPlayer());
		nav.open();
		itoomelNav.add(nav);
	}

	public List<MarketItem> getAllMisFromNSize(int fromID, int size) {
		return itoomelitems;
	}

	public int getPages() {
		return ((itoomelitems.size() - (itoomelitems.size() % 28)) / 28);
	}

	public HashMap<Material, Long> getMatsNAmount() {
		HashMap<Material, Long> hmstats = new HashMap<>();
		long leng = 0;
		for (MarketItem mis : itoomelitems) {
			if (hmstats.containsKey(mis.getIs().getType())) {
				leng = hmstats.get(mis.getIs().getType());
				leng += mis.getAmmount();
				hmstats.put(mis.getIs().getType(), leng);
			} else {
				hmstats.put(mis.getIs().getType(), mis.getAmmount());
			}
		}
		return hmstats;
	}

	public void openNav(String[] args, Player player) {
		System.out.println("Args-Openening: " + args[0].toLowerCase());
		switch (args[0].toLowerCase()) {
		case "search_mat":
			System.out.println("Openning Search_Mat");
			ItoomelSearch isearch = new ItoomelSearch(new ItemStack(Material.valueOf(args[1])), player);
			this.openNav(new ItoomelNavigation(ItoomelMenu.SEARCH_MAT, player, isearch));
			break;
		default:
			System.out.println("opening_other");
			this.openNav(new ItoomelNavigation(ItoomelMenu.valueOf(args[0]), player));
			break;
		}
	}

	public boolean isInNav(Player p) {
		for (ItoomelNavigation inav : itoomelNav) {
			if (inav.getPlayer().equals(p)) {
				return true;
			}
		}
		return false;
	}

}
