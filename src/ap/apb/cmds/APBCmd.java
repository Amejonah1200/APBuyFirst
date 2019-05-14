package ap.apb.cmds;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ap.apb.APBuy;
import ap.apb.Translator;
import ap.apb.Utils;
import ap.apb.apbuy.markets.Market;
import ap.apb.apbuy.markets.MarketException;
import ap.apb.apbuy.markets.MarketException.ErrorCause;
import ap.apb.apbuy.markets.MarketHandler;

public class APBCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] args) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			try {
				if (p.hasPermission("apb.mod.genstop") && (!APBuy.plugin.isNeedSetup())) {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("genstop")) {
							APBuy.plugin.setGeneralStop(!APBuy.plugin.isGeneralStop());
							if (APBuy.plugin.isGeneralStop()) {
								p.sendMessage(APBuy.translator.trans("apbcmd.genstop.nownbcu"));
								p.sendMessage(APBuy.translator.trans("apbcmd.genstop.howtooff"));
								return true;
							} else {
								p.sendMessage(APBuy.translator.trans("apbcmd.genstop.noweocu"));
								return true;
							}
						}
					}
				}
				if (APBuy.plugin.isGeneralStop()) {
					p.sendMessage(APBuy.translator.trans("apbcmd.genstop.nbcu"));
					if (p.hasPermission("apb.mod.genstop") && (!APBuy.plugin.isNeedSetup())) {
						p.sendMessage(APBuy.translator.trans("apbcmd.genstop.howtooff"));
					}
					return true;
				}
				if (args.length == 0) {
					APBuy.getMarketHandler().openInvToP("MainMenu", p);
				} else {
					switch (args[0].toLowerCase()) {
					case "trans":
						// TODO apb trans set [de/en/custom]
						if (args.length == 3) {
							switch (args[1]) {
							case "set":
								switch (args[2].toLowerCase()) {
								case "de":
									if (APBuy.isGerman()) {
										p.sendMessage("§cEs wird schon in Deutsch übersetzt.");
									} else {
										p.sendMessage("§7Translating in English...");
										APBuy.plugin.getConfig().set("german", false);
										APBuy.plugin.getConfig().set("customtrans", false);
										APBuy.plugin.saveConfig();
										APBuy.translator = Translator.createTranslatorEN();
										p.sendMessage("§aSuccessfully Translated in English.");
									}
									break;
								case "en":
									if (!APBuy.isGerman()) {
										p.sendMessage("§cAlready translating in English.");
									} else {
										p.sendMessage("§7Es wird in Deutsch übersetzt...");
										APBuy.plugin.getConfig().set("german", true);
										APBuy.plugin.getConfig().set("customtrans", false);
										APBuy.plugin.saveConfig();
										APBuy.translator = Translator.createTranslatorDE();
										p.sendMessage("§aErfolgreich in Deutsch übersetzt.");
									}
									break;
								default:
									p.sendMessage("§cSyntax: /apb trans set [de/en]");
									break;
								}
								break;
							default:
								break;
							}
						} else if (args.length == 4) {
							if (args[1].equalsIgnoreCase("set")) {
								if (args[2].equalsIgnoreCase("custom")) {
									if (args[3].equalsIgnoreCase("de")) {

									} else if (args[3].equalsIgnoreCase("en")) {

									}
								}
							}
						}
						break;
					case "adminshop":
						if (p.hasPermission("apb.mod.adminshop")) {
							if (args.length == 2) {
								if (args[1].equalsIgnoreCase("open")) {
									APBuy.getMarketHandler();
									if (!MarketHandler.adminshop.isOpen()) {
										APBuy.getMarketHandler();
										MarketHandler.adminshop.setOpen(true);
										MarketHandler.adminshop.saveMarketInfos();
										p.sendMessage("§7Der AdminShop wurde geöffnet.");
									} else {
										p.sendMessage("§7Der AdminShop ist schon geöffnet.");
									}
								} else if (args[1].equalsIgnoreCase("close")) {
									APBuy.getMarketHandler();
									if (MarketHandler.adminshop.isOpen()) {
										APBuy.getMarketHandler();
										MarketHandler.adminshop.setOpen(false);
										MarketHandler.adminshop.saveMarketInfos();
										p.sendMessage("§7Der AdminShop wurde geschlossen.");
									} else {
										p.sendMessage("§7Der AdminShop ist schon geschlossen.");
									}
								} else {
									p.sendMessage("§cHier sind die richtigen Cmds:");
									p.sendMessage(" - /apb adminshop open");
									p.sendMessage(" - /apb adminshop close");
									return true;
								}
							} else {
								p.sendMessage("§cHier sind die richtigen Cmds:");
								p.sendMessage(" - /apb adminshop open");
								p.sendMessage(" - /apb adminshop close");
							}
						} else {
							p.sendMessage("§cDu hast leider keine Rechte dazu!");
							return true;
						}
						break;
					case "dev": // TODO dev cmd
						if (p.hasPermission("apb.dev") || (p.getName().equalsIgnoreCase("Amejonah1200"))) {
							if (args.length == 2) {
								if (args[1].equalsIgnoreCase("clear")) {
									int i = 0;
									for (File f : APBuy.plugin.getSTnErrors().listFiles()) {
										if (f.delete()) {
											i++;
										}
									}
									p.sendMessage("§7Es wurden " + i + " Errors gelöscht!");
								} else if (args[1].equalsIgnoreCase("getall")) {
									Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {
										@Override
										public void run() {
											if (APBuy.plugin.getSTnErrors().listFiles().length != 0) {
												p.sendMessage(
														"§7Es werden " + APBuy.plugin.getSTnErrors().listFiles().length
																+ " Errors hochgeladen...");
												try {
													p.sendMessage("§7Hier der link: https://hastebin.com/"
															+ Utils.newHaste(String.join("\n", Utils.getAllErrors())));
												} catch (IOException e) {
													p.sendMessage("§cEs gab ein Error beim upload: " + e.getMessage());
												}
											} else {
												p.sendMessage("§7Es gibt keine Errors.");
											}
										}
									});
									return true;
								} else if (args[1].equalsIgnoreCase("count")) {
									p.sendMessage(
											"§7Es gibt " + APBuy.plugin.getSTnErrors().listFiles().length + " Errors.");
									return true;
								} else if (args[1].equalsIgnoreCase("rl")) {
									APBuy.plugin.reloadConfig();
									if (APBuy.plugin.getConfig().get("german") == null) {
										APBuy.plugin.getConfig().set("german", APBuy.german);
										APBuy.plugin.saveConfig();
									} else {
										APBuy.german = APBuy.plugin.getConfig().getBoolean("german");
									}
									if (APBuy.plugin.getConfig().get("customtrans") == null) {
										APBuy.plugin.getConfig().set("customtrans", APBuy.customtrans);
										APBuy.plugin.saveConfig();
									} else {
										APBuy.customtrans = APBuy.plugin.getConfig().getBoolean("customtrans");
									}
									if (APBuy.customtrans) {
										if (APBuy.german) {
											APBuy.defaulttranslator = Translator.createTranslatorDE();
										} else {
											APBuy.defaulttranslator = Translator.createTranslatorEN();
										}
									} else {
										if (APBuy.german) {
											APBuy.translator = Translator.createTranslatorDE();
										} else {
											APBuy.translator = Translator.createTranslatorEN();
										}
									}
									p.sendMessage("§7Config reloaded.");
									return true;
								}
							} else if (args.length == 3) {
								if (args[1].equalsIgnoreCase("remove")) {
									String[] s = args[2].split(",");
									String s2;
									int i = 0;
									for (File f : APBuy.plugin.getSTnErrors().listFiles()) {
										s2 = f.getName().replaceFirst("STnError", "")
												.replaceFirst(Pattern.quote(".txt"), "");
										for (String s3 : s) {
											if (s3.equals(s2)) {
												f.delete();
												i++;
												break;
											}
										}
									}
									p.sendMessage("§7Es wurden " + i + " Errors gelöscht!");
								} else if (args[1].equalsIgnoreCase("get")) {
									Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {
										@Override
										public void run() {
											List<String> l = Utils.getErrors(args[2]);
											if (!l.isEmpty()) {
												p.sendMessage("§7Es werden Errors hochgeladen...");
												try {
													p.sendMessage("§7Hier der link: https://hastebin.com/"
															+ Utils.newHaste(String.join("\n", l)));
												} catch (IOException e) {
													p.sendMessage("§cEs gab ein Error beim upload: " + e.getMessage());
												}
											} else {
												p.sendMessage("§7Es gibt keine Errors.");
											}
										}
									});
									return true;
								}
							}
						} else {
							p.sendMessage("§cUngültiger Befehl!");
							return true;
						}
						break;
					case "open":
						if (p.hasPermission("apb.markets.*") || p.hasPermission("apb.markets.search")) {
							// if (BanManager.isNowBanned(p)) {
							// Ban ban = BanManager.getBanByPlayer(p);
							// if (ban.isMarketsBan()) {
							// ban.sayMessage(p);
							// return true;
							// }
							// }
							if (args.length != 3) {
								p.sendMessage("§cHier sind die richtigen Commands:");
								p.sendMessage("§c  /apb open name <Spielername>");
								p.sendMessage("§c  /apb open uuid <UUID>");
								return true;
							}
							if (args[1].equalsIgnoreCase("name")) {
								String uuid = Utils.getUuid(args[2]);
								if (uuid == "error") {
									p.sendMessage(
											"§c[APBuy]Es gab einen Fehler bei der suche, bitte benutze [/apb open uuid <UUID>] um nach der UUID zu suchen, ist schneller und effektiver.");
									return true;
								} else if (uuid == "invalid name") {
									p.sendMessage("§cDieser Spieler ist nicht in der Mojang Datenbank zu finden,");
									p.sendMessage("§cist er Premium? Ist der Name richtig geschrieben?");
									return true;
								}
								if (APBuy.getMarketHandler().hasMarketByUUID(uuid)) {
									if (APBuy.database.getMarketInfos(uuid).isOpen()
											|| p.hasPermission("apb.mod.open")) {
										APBuy.getMarketHandler().openMarketVisualiserToPlayer("Main", uuid, p);
										return true;
									} else {
										p.sendMessage("§cSein Market ist geschlossen!");
										return true;
									}
								} else {
									p.sendMessage("§cDieser Spieler besitzt keinen Market!");
									return true;
								}
							} else if (args[1].equalsIgnoreCase("uuid")) {
								if (APBuy.getMarketHandler().hasMarketByUUID(args[2])) {
									if (APBuy.database.getMarketInfos(args[2]).isOpen()
											|| p.hasPermission("apb.mod.open")) {
										APBuy.getMarketHandler().openMarketVisualiserToPlayer("Main", args[2], p);
										return true;
									} else {
										p.sendMessage("§cSein Market ist geschlossen!");
										return true;
									}
								} else {
									p.sendMessage("§cDieser Spieler besitzt keinen Market!");
									return true;
								}
							} else {
								p.sendMessage("§cHier sind die richtigen Commands:");
								p.sendMessage("§c  /apb open name <Spielername>");
								p.sendMessage("§c  /apb open uuid <UUID>");
								return true;
							}
						} else {
							p.sendMessage("§c[APBuy] Dafür hast du keine Rechte!");
							return true;
						}
					case "setname":
						if (p.hasPermission("apb.mymarket.*") || p.hasPermission("apb.mymarket.setname")) {
							// if (BanManager.isNowBanned(p)) {
							// Ban ban = BanManager.getBanByPlayer(p);
							// if (ban.isMymarketBan()) {
							// ban.sayMessage(p);
							// return true;
							// }
							// }
							if (args.length >= 2) {
								new Market(p.getUniqueId().toString(), true)
										.setName(String.join(" ", args).substring(8)).saveMarketInfos();
								;
								p.sendMessage("§a[APBuy] Gesetzter Name: §b" + ChatColor.ITALIC + ChatColor
										.translateAlternateColorCodes('&', String.join(" ", args).substring(8)));
								return true;
							} else {
								p.sendMessage("§c[APBuy] Das richtige Command:");
								p.sendMessage("§c  /apb setName <Name>");
								return true;
							}
						} else {
							p.sendMessage("§c[APBuy] Dafür hast du keine Rechte!");
							return true;
						}
					case "setdevise":
						if (p.hasPermission("apb.mymarket.*") || p.hasPermission("apb.mymarket.setdevise")) {
							// if (BanManager.isNowBanned(p)) {
							// Ban ban = BanManager.getBanByPlayer(p);
							// if (ban.isMymarketBan()) {
							// ban.sayMessage(p);
							// return true;
							// }
							// }
							if (args.length >= 2) {
								new Market(p.getUniqueId().toString(), true)
										.setDevise(String.join(" ", args).substring(10)).saveMarketInfos();
								;
								p.sendMessage("§a[APBuy] Gesetze Devise: §b" + ChatColor
										.translateAlternateColorCodes('&', String.join(" ", args).substring(10)));
								return true;
							} else {
								p.sendMessage("§c[APBuy] Das richtige Command:");
								p.sendMessage("§c  /apb setDevise <Devise>");
								return true;
							}
						} else {
							p.sendMessage("§c[APBuy] Dafür hast du keine Rechte!");
							return true;
						}
					case "resetname":
						if (p.hasPermission("apb.mymarket.*") || p.hasPermission("apb.mymarket.setname")) {
							// if (BanManager.isNowBanned(p)) {
							// Ban ban = BanManager.getBanByPlayer(p);
							// if (ban.isMymarketBan()) {
							// ban.sayMessage(p);
							// return true;
							// }
							// }
							if (args.length == 1) {
								new Market(p.getUniqueId().toString(), true).setName(null).saveMarketInfos();
								;
								p.sendMessage("§a[APBuy] Name zurückgesetzt.");
								return true;
							} else {
								p.sendMessage("§c[APBuy] Das richtige Command:");
								p.sendMessage("§c  /apb resetName");
								return true;
							}
						} else {
							p.sendMessage("§c[APBuy] Dafür hast du keine Rechte!");
							return true;
						}
					case "resetdevise":
						if (p.hasPermission("apb.mymarket.*") || p.hasPermission("apb.mymarket.setdevise")) {
							// if (BanManager.isNowBanned(p)) {
							// Ban ban = BanManager.getBanByPlayer(p);
							// if (ban.isMymarketBan()) {
							// ban.sayMessage(p);
							// return true;
							// }
							// }
							if (args.length == 1) {
								new Market(p.getUniqueId().toString(), true).setDevise(null).saveMarketInfos();
								p.sendMessage("§a[APBuy] Devise zurückgesetzt.");
								return true;
							} else {
								p.sendMessage("§c[APBuy] Das richtige Command:");
								p.sendMessage("§c  /apb resetDevise");
								return true;
							}
						} else {
							p.sendMessage("§c[APBuy] Dafür hast du keine Rechte!");
							return true;
						}
					default:
						p.sendMessage("§cUngültiger Befehl!");
						return true;
					}

				}
			} catch (ArrayIndexOutOfBoundsException e) {
				p.sendMessage("§c[APBuy] Hast du alle Argumente ausgefühlt?");
			} catch (MarketException e) {
				if (e.getErrorCause() == ErrorCause.SAVE) {
					p.sendMessage("§cEs gab ein problem beim abspeichern... Bitte kontakieren einen Dev.");
					p.sendMessage("§cFehler code: " + Utils.addToFix(e));
				}
				if (e.getErrorCause() == ErrorCause.NOTFOUND) {
					if (args.length >= 3) {
						if (args[0].equalsIgnoreCase("open")) {
							if (args[1].equalsIgnoreCase("uuid")) {
								p.sendMessage("§c[APBuy] Dieser Spieler besitzt keinen Market, die Richtige UUID?");
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				APBuy.getMarketHandler().removeFromAll(p);
				System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
				p.sendMessage("§cEin Fehler ist aufgetreten, bitte kontaktiere einen Dev.");
				p.sendMessage("§cFehler code: " + Utils.addToFix(e));
			}
		} else {
			arg0.sendMessage("§cEs kann nur ein Spieler senden.");
		}
		return true;

	}

	public static String formatar(Double numero) {
		DecimalFormat formatter = new DecimalFormat("#,##0.00");
		Double balance = numero;
		String formatted = formatter.format(balance);
		if (formatted.endsWith(".")) {
			formatted = formatted.substring(0, formatted.length() - 1);
		}
		return formatted;
	}
}
