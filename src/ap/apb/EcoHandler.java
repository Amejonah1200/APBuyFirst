package ap.apb;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class EcoHandler {

	public static Economy ecoVault = null;

	public static boolean isEcoInstalled() {
		return Bukkit.getServer().getPluginManager().getPlugin("Vault") != null;

	}

	public static boolean setupEconomy() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		ecoVault = rsp.getProvider();
		return ecoVault != null;
	}

	public boolean hasPlayerAccount(OfflinePlayer p) {
		return ecoVault.hasAccount(p);
	}

	public void createAccount(OfflinePlayer p) {
		ecoVault.createPlayerAccount(p);
	}

	public short transPtoP(OfflinePlayer p1, double money, OfflinePlayer p2) {
		if (APBuy.plugin.isAutoCreate()) {
			this.createAccount(p1);
			this.createAccount(p2);
		}
		boolean argone = ecoVault.withdrawPlayer(p1, money).transactionSuccess();
		boolean argtwo = ecoVault.depositPlayer(p2, money).transactionSuccess();
		if ((argone) && (argtwo)) {
			return 0;
		} else if ((!argone) && (argtwo)) {
			ecoVault.withdrawPlayer(p2, money);
			return -1;
		} else if ((argone) && (!argtwo)) {
			ecoVault.depositPlayer(p1, money);
			return 1;
		} else {
			return -2;
		}
	}

	public double getPlayerBalance(Player p) {
		if (this.hasPlayerAccount(p)) {
			return ecoVault.getBalance(p);
		} else {
			return 0D;
		}
	}

	public boolean has(OfflinePlayer p, double balance) {
		if (this.hasPlayerAccount(p)) {
			return ecoVault.has(p, balance);
		}
		return false;
	}

	public short withdrawPlayer(OfflinePlayer p, double money) {
		return (short) (ecoVault.withdrawPlayer(p, money).transactionSuccess() ? 0 : -1);
	}

	public short depositPlayer(OfflinePlayer p, double money) {
		return (short) (ecoVault.depositPlayer(p, money).transactionSuccess() ? 0 : -1);
	}

}
