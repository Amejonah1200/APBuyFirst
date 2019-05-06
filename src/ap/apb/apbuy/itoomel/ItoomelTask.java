package ap.apb.apbuy.itoomel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;

import ap.apb.APBuy;
import ap.apb.Utils;
import ap.apb.apbuy.markets.Market;
import ap.apb.apbuy.markets.MarketItem;

public class ItoomelTask implements Runnable {

	@Override
	public void run() {
		try {
			List<Market> l = loadAllMarkets(APBuy.getMarketHandler().getAllMarketsOnline());
			HashMap<Material, List<MarketItem>> allMM = new HashMap<>();
			List<MarketItem> miss = new ArrayList<>();
			for (Market m : l) {
				for (MarketItem mis : m.getMarketItems()) {
					if (mis.isBuyable()) {
						if (allMM.containsKey(mis.getIs().getType())) {
							miss = allMM.get(mis.getIs().getType());
							miss.add(mis);
							allMM.replace(mis.getIs().getType(), miss);
						} else {
							miss = new ArrayList<>();
							miss.add(mis);
							allMM.put(mis.getIs().getType(), miss);
						}
					}
				}
			}
			Itoomel.reopenItoomelToEveryone();
			Itoomel.itoomelStandard = allMM;
			APBuy.plugin.setGeneralStop(!APBuy.plugin.isRemoveGen());
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
}
