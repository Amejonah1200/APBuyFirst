package ap.apb;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VersionUtils {

	@SuppressWarnings("deprecation")
	public static ItemStack getItemInMainHand(Player player) {
		return isVersion1_12() ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInHand();
	}

	public static boolean isVersion1_12() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].equals("v1_12_R1");
	}

}
