package ap.apb.cmds;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ap.apb.APBuy;
import ap.apb.Utils;
import ap.apb.apbuy.itoomel.Itoomel;
import ap.apb.apbuy.itoomel.ItoomelNavigation;
import ap.apb.apbuy.itoomel.ItoomelNavigation.ItoomelMenu;

public class ItoomelCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		try {
			if (arg0 instanceof Player) {
				Player p = (Player) arg0;
				if (APBuy.isGeneralStop()) {
					p.sendMessage(
							"§cAktuel kann niemand Itoomel öffnen. Bitte kontakiere die Mods, wenn Fragen offen sind!");
					return true;
				}
				// if (BanManager.isNowBanned((Player) p)) {
				// Ban ban = BanManager.getBanByPlayer((Player) p);
				// if (ban.isItoomelBan()) {
				// ban.sayMessage((Player) p);
				// return true;
				// }
				// }
				if (arg3.length == 0) {
					if (p.hasPermission("apb.itoomel")) {
						// ItoomelPrime.openItoomel("Main", p, Material.AIR, 0);
						Itoomel.getInstance().openNav(new ItoomelNavigation(ItoomelMenu.ALL_ITEMS, p));
					} else {
						p.sendMessage("§c[Itoomel] Dafür hast du keine Rechte!");
					}
				} else if (arg3.length == 1) {
					if (p.hasPermission("apb.itoomel")) {
						if (arg3[0].equalsIgnoreCase("search")) {
							if (((Player) p).getInventory().getItemInHand().getType() != Material.AIR) {
								// ItoomelPrime.openItoomel("Main", p,
								// p.getInventory().getItemInHand().getType(),
								// 0);
								Itoomel.getInstance().openNav(
										new String[] { "SEARCH_MAT", p.getItemInHand().getType().toString() }, p);
							} else {
								p.sendMessage("§cBitte halte einen Item in der Hand.");
							}

						} else {
							p.sendMessage("§cEs gibt nur 2 Itoomel-Commands:");
							p.sendMessage("§c   /itoomel");
							p.sendMessage("§c   /itoomel search");
						}

					} else {
						p.sendMessage("§c[Itoomel] Dafür hast du keine Rechte!");
					}
				} else {
					if (p.hasPermission("apb.itoomel")) {
						p.sendMessage("§cEs gibt nur 2 Itoomel-Commands:");
						p.sendMessage("§c   /itoomel");
						p.sendMessage("§c   /itoomel search");
					} else {
						p.sendMessage("§c[Itoomel] Dafür hast du keine Rechte!");
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			if (arg0 instanceof Player) {
				APBuy.getMarketHandler().removeFromAll((Player) arg0);
				System.out.println("Player: " + arg0.getName() + " (" + ((Player) arg0).getUniqueId().toString() + ")");
			}
			arg0.sendMessage("§cEin Fehler ist aufgetreten, bitte kontaktiere einen Dev.");
			arg0.sendMessage("§cFehler code: " + Utils.addToFix(e));
		}
		return true;
	}

}
