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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import ap.apb.APBuy;
import ap.apb.APBuy.StopCause;
import ap.apb.APBuyException;
import ap.apb.APBuyException.ErrorCause;
import ap.apb.Translator;
import ap.apb.Utils;
import ap.apb.apbuy.markets.Market;
import ap.apb.apbuy.markets.MarketHandler;

public class APBCmd implements CommandExecutor, Listener {

	private Player dbConfigurator;
	private String[] dbArgs = new String[5];
	private byte progress = -1;

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] args) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			try {
				if (APBuy.genStopByPlugin && (!((p.hasPermission("apb.dev.rl") || p.hasPermission("apb.dev.db"))
						&& ((args.length == 4) || (args.length == 1))))) {
					p.sendMessage("§cKeiner kann aktuell APBuy oder Itoomel verwenden!");
					if ((p.hasPermission("apb.dev.rl") || p.hasPermission("apb.dev.db"))
							&& (APBuy.stopCause == StopCause.DATABASE)) {
						p.sendMessage("§cDu kannst nur diese Commands als Dev benutzen:");
						if (p.hasPermission("apb.dev.rl")) {
							p.sendMessage("§c/apb rl");
						}
						if (p.hasPermission("apb.dev.db")) {
							p.sendMessage("§c/apb dev setdb mysql [true/false]");
							p.sendMessage("§c/apb dev setdb sqlite [true/false]");
						}
					}
					return true;
				}
				if (APBuy.genStopByPlugin
						&& ((APBuy.stopCause == StopCause.DATABASE) || (APBuy.stopCause == StopCause.TRANSFERINGDB))
						&& ((p.hasPermission("apb.dev.rl") || p.hasPermission("apb.dev.db"))
								&& ((args.length == 4) || (args.length == 1)))) {
					if (args[0].equalsIgnoreCase("dev")) {
						if (args[1].equalsIgnoreCase("setdb")) {
							if (p.hasPermission("apb.dev.db")) {
								switch (args[2].toLowerCase()) {
								case "sqlite":
									if (APBuy.stopCause == StopCause.TRANSFERINGDB) {
										p.sendMessage("§7Es wird gerade schon transferiert...");
										return true;
									}
									if (APBuy.getDatamaster().isSqlite()) {
										p.sendMessage("§cEs wird schon SQLite verwendet.");
										return true;
									}
									if (args[3].equalsIgnoreCase("true")) {
										new BukkitRunnable() {
											@Override
											public void run() {
												p.sendMessage(
														"§7Es wird die Datenbank auf SQLite gesetzt und dabei wird alles vom alte MySQL Datenbank importiert.");
												APBuy.stopCause = StopCause.TRANSFERINGDB;

												if (APBuy.getDatamaster().setDBToSQLite(true)) {
													p.sendMessage("§aErfolgreich geändert.");
													APBuy.stopCause = StopCause.NONE;
													APBuy.genStopByPlugin = false;
												} else {
													p.sendMessage("§cEs gab einen Fehler...");
												}
											}
										}.runTask(APBuy.plugin);
									} else if (args[3].equalsIgnoreCase("false")) {
										p.sendMessage("§7Es wird die Datenbank auf SQLite gesetzt");
										if (APBuy.getDatamaster().setDBToSQLite(false)) {
											p.sendMessage("§aErfolgreich geändert.");
											APBuy.stopCause = StopCause.NONE;
											APBuy.genStopByPlugin = false;
										} else {
											p.sendMessage("§cEs gab einen Fehler...");
										}
									} else {
										p.sendMessage("§cSyntax: /apb dev setdb sqlite [true/false]");
									}
									break;
								case "mysql":
									if (APBuy.stopCause == StopCause.TRANSFERINGDB) {
										p.sendMessage("§7Es wird gerade schon transferiert...");
										return true;
									}
									if (args[3].equalsIgnoreCase("true")) {
										if (dbConfigurator != null) {
											p.sendMessage("§cDer Spieler §6" + dbConfigurator.getName()
													+ " §cist schon gerade am ändern... Bitte warten.");
											p.sendMessage("§7Wenn er aufhören soll, muss er oder disconnecten,"
													+ " oder \"@cancel\" in den CHat schreiben.");
											return true;
										}
										dbArgs[0] = "true";
										progress = 1;
										dbConfigurator = p;
										p.sendMessage("§7Bitte sende den Hoster in den Chat.");
									} else if (args[3].equalsIgnoreCase("false")) {
										if (dbConfigurator != null) {
											p.sendMessage("§cDer Spieler §6" + dbConfigurator.getName()
													+ " §cist schon gerade am ändern... Bitte warten.");
											p.sendMessage("§7Wenn er aufhören soll, muss er oder disconnecten,"
													+ " oder \"@cancel\" in den CHat schreiben.");
											return true;
										}
										dbArgs[0] = "false";
										progress = 1;
										dbConfigurator = p;
										p.sendMessage("§7Bitte sende den Hoster in den Chat.");
									} else {
										p.sendMessage("§cSyntax: /apb dev setdb sqlite [true/false]");
									}
									break;
								default:
									p.sendMessage("§cEs gibt nur 2 Datenbanken: \"sqlite\" und \"mysql\".");
									break;
								}
							} else {
								p.sendMessage("§cDu kannst nur [/apb rl] ausführen.");
							}
							return true;
						} else {
							if (p.hasPermission("apb.dev.db")) {
								p.sendMessage("§cDu kannst nur diese Commands als Dev benutzen:");
								p.sendMessage("§c/apb dev setdb mysql [true/false]");
								p.sendMessage("§c/apb dev setdb sqlite [true/false]");
							} else {
								p.sendMessage("§cDu kannst nur [/apb rl] ausführen.");
								return true;
							}
						}
					} else if (args[0].equalsIgnoreCase("rl")) {
						if (p.hasPermission("apb.dev.rl")) {
							if (APBuy.stopCause == StopCause.TRANSFERINGDB) {
								p.sendMessage("§7Es wird gerade transferiert, du kannst jetzt keinen Reload machen...");
								return true;
							} else {
								// TODO Reload Cmd
							}
						} else {
							p.sendMessage("§cDu kannst nur diese Commands als Dev benutzen:");
							p.sendMessage("§c/apb dev setdb mysql [true/false]");
							p.sendMessage("§c/apb dev setdb sqlite [true/false]");
						}
					} else {
						p.sendMessage("§cDu kannst nur diese Commands als Dev benutzen:");
						if (p.hasPermission("apb.dev.rl")) {
							p.sendMessage("§c/apb rl");
						}
						if (p.hasPermission("apb.dev.db")) {
							p.sendMessage("§c/apb dev setdb mysql [true/false]");
							p.sendMessage("§c/apb dev setdb sqlite [true/false]");
						}
					}
					return true;
				} else {
					if (APBuy.genStopByPlugin) {
						if ((p.hasPermission("apb.dev.rl") || p.hasPermission("apb.dev.db"))) {
							p.sendMessage("§cDu kannst nur diese Commands als Dev benutzen:");
							if (p.hasPermission("apb.dev.rl")) {
								p.sendMessage("§c/apb rl");
							}
							if (p.hasPermission("apb.dev.db")) {
								p.sendMessage("§c/apb dev setdb mysql [true/false]");
								p.sendMessage("§c/apb dev setdb sqlite [true/false]");
							}
							return true;
						}
					}
				}
				if (APBuy.isGeneralStop()) {
					p.sendMessage(APBuy.translator.trans("apbcmd.genstop.nbcu"));
					if (p.hasPermission("apb.mod.genstop")) {
						p.sendMessage(APBuy.translator.trans("apbcmd.genstop.howtooff"));
					}
					return true;
				}
				if (args.length == 0) {
					APBuy.getMarketHandler().openMainMenu(p);
				} else {
					switch (args[0].toLowerCase()) {
					case "trans":
						if (args.length == 3) {
							switch (args[1]) {
							case "set":
								switch (args[2].toLowerCase()) {
								case "de":
									if (APBuy.isGerman()) {
										p.sendMessage("§c[APBuy] Es wird bereits in Deutsch übersetzt.");
									} else {
										p.sendMessage("§7[APBuy] Übersetze in Deutsch...");
										APBuy.plugin.getConfig().set("german", true);
										APBuy.plugin.saveConfig();
										APBuy.translator = Translator.createTranslatorDE();
										p.sendMessage("§a[APBuy] Erfolgreich in Deutsch übersetzt.");
										APBuy.german = true;
									}
									break;
								case "en":
									if (!APBuy.isGerman()) {
										p.sendMessage("§c[APBuy] Already translating in English.");
									} else {
										p.sendMessage("§7[APBuy] Translating in English...");
										APBuy.plugin.getConfig().set("german", false);
										APBuy.plugin.saveConfig();
										APBuy.translator = Translator.createTranslatorEN();
										p.sendMessage("§a[APBuy] Successfully translated in English.");
										APBuy.german = false;
									}
									break;
								default:
									p.sendMessage("§7[APBuy] Syntax: /apb trans set [de/en]");
									break;
								}
								break;
							default:
								break;
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
										p.sendMessage("§7[APBuy] Der AdminShop wurde geöffnet.");
									} else {
										p.sendMessage("§7[APBuy] Der AdminShop ist schon geöffnet.");
									}
								} else if (args[1].equalsIgnoreCase("close")) {
									APBuy.getMarketHandler();
									if (MarketHandler.adminshop.isOpen()) {
										APBuy.getMarketHandler();
										MarketHandler.adminshop.setOpen(false);
										MarketHandler.adminshop.saveMarketInfos();
										p.sendMessage("§7[APBuy] Der AdminShop wurde geschlossen.");
									} else {
										p.sendMessage("§7[APBuy] Der AdminShop ist schon geschlossen.");
									}
								} else {
									p.sendMessage("§c[APBuy] Hier sind die richtigen Cmds:");
									p.sendMessage(" - /apb adminshop open");
									p.sendMessage(" - /apb adminshop close");
									return true;
								}
							} else {
								p.sendMessage("§c[APBuy] Hier sind die richtigen Cmds:");
								p.sendMessage(" - /apb adminshop open");
								p.sendMessage(" - /apb adminshop close");
							}
						} else {
							p.sendMessage("§c[APBuy] Du hast leider keine Rechte dazu!");
							return true;
						}
						break;
					case "dev":
						// TODO Change Database
						// TODO Backup
						// /apb dev db set sqlite
						// /apb dev db set mysql -> Ask for infos
						if (p.hasPermission("apb.dev.*") || p.hasPermission("apb.dev.st")
								|| p.hasPermission("apb.dev.db") || p.hasPermission("apb.dev.rl")
								|| p.hasPermission("apb.*") || p.hasPermission("*")
								|| p.getName().equals("Amejonah1200")) {
							if (args.length == 2) {
								if (args[1].equalsIgnoreCase("clearst")) {
									if (p.hasPermission("apb.dev.*") || p.hasPermission("apb.dev.st")
											|| p.hasPermission("apb.*") || p.hasPermission("*")
											|| p.getName().equals("Amejonah1200")) {
										int i = 0;
										for (File f : APBuy.plugin.getSTnErrors().listFiles()) {
											if (f.delete()) {
												i++;
											}
										}
										p.sendMessage("§7[APBuy] Es wurden " + i + " Errors gelöscht!");
									} else {
										p.sendMessage("§c[APBuy] Ungültiger Befehl!");
										return true;
									}
								} else if (args[1].equalsIgnoreCase("getallst")) {
									if (p.hasPermission("apb.dev.*") || p.hasPermission("apb.dev.st")
											|| p.hasPermission("apb.*") || p.hasPermission("*")
											|| p.getName().equals("Amejonah1200")) {
										Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {
											@Override
											public void run() {
												if (APBuy.plugin.getSTnErrors().listFiles().length != 0) {
													p.sendMessage("§7[APBuy] Es werden "
															+ APBuy.plugin.getSTnErrors().listFiles().length
															+ " Errors hochgeladen...");
													try {
														p.sendMessage("§7[APBuy] Hier der link: https://hastebin.com/"
																+ Utils.newHaste(
																		String.join("\n", Utils.getAllErrors())));
													} catch (IOException e) {
														p.sendMessage("§c[APBuy] Es gab ein Error beim upload: "
																+ e.getMessage());
													}
												} else {
													p.sendMessage("§7[APBuy] Es gibt keine Errors.");
												}
											}
										});
										return true;
									} else {
										p.sendMessage("§c[APBuy] Ungültiger Befehl!");
										return true;
									}
								} else if (args[1].equalsIgnoreCase("countst")) {
									if (p.hasPermission("apb.dev.*") || p.hasPermission("apb.dev.st")
											|| p.hasPermission("apb.*") || p.hasPermission("*")
											|| p.getName().equals("Amejonah1200")) {
										p.sendMessage("§7Es gibt " + APBuy.plugin.getSTnErrors().listFiles().length
												+ " Errors.");
										return true;
									} else {
										p.sendMessage("§c[APBuy] Ungültiger Befehl!");
										return true;
									}
								} else if (args[1].equalsIgnoreCase("rl")) {
									if (p.hasPermission("apb.dev.*") || p.hasPermission("apb.dev.rl")
											|| p.hasPermission("apb.*") || p.hasPermission("*")) {
										APBuy.plugin.reloadConfig();
										if (APBuy.plugin.getConfig().get("german") == null) {
											APBuy.plugin.getConfig().set("german", APBuy.german);
											APBuy.plugin.saveConfig();
										} else {
											APBuy.german = APBuy.plugin.getConfig().getBoolean("german");
										}
										if (APBuy.german) {
											APBuy.translator = Translator.createTranslatorDE();
										} else {
											APBuy.translator = Translator.createTranslatorEN();
										}
										p.sendMessage("§7[APBuy] Config reloaded.");
										return true;
									} else {
										p.sendMessage("§c[APBuy] Ungültiger Befehl!");
										return true;
									}
								} else {
									p.sendMessage("§c[APBuy] Ungültiger Befehl!");
									return true;
								}
							} else if (args.length == 3) {
								if (p.hasPermission("apb.dev.*") || p.hasPermission("apb.dev.st")
										|| p.hasPermission("apb.*") || p.hasPermission("*")
										|| p.getName().equals("Amejonah1200")) {
									if (args[1].equalsIgnoreCase("removest")) {
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
										p.sendMessage("§7[APBuy] Es wurden " + i + " Errors gelöscht!");
									} else if (args[1].equalsIgnoreCase("getst")) {
										Bukkit.getScheduler().runTask(APBuy.plugin, new Runnable() {
											@Override
											public void run() {
												List<String> l = Utils.getErrors(args[2]);
												if (!l.isEmpty()) {
													p.sendMessage("§7[APBuy] Es werden Errors hochgeladen...");
													try {
														p.sendMessage("§7[APBuy] Hier der link: https://hastebin.com/"
																+ Utils.newHaste(String.join("\n", l)));
													} catch (IOException e) {
														p.sendMessage("§c[APBuy] Es gab ein Error beim upload: "
																+ e.getMessage());
													}
												} else {
													p.sendMessage("§7[APBuy] Es gibt keine Errors.");
												}
											}
										});
										return true;
									}
								} else {
									p.sendMessage("§c[APBuy] Ungültiger Befehl!");
									return true;
								}
							} else if (args.length == 4) {
								if (p.hasPermission("apb.dev.db")) {
									switch (args[2].toLowerCase()) {
									case "sqlite":
										if (APBuy.stopCause == StopCause.TRANSFERINGDB) {
											p.sendMessage("§7Es wird gerade schon transferiert...");
											return true;
										}
										if (APBuy.getDatamaster().isSqlite()) {
											p.sendMessage("§cEs wird schon SQLite verwendet.");
											return true;

										}
										if (args[3].equalsIgnoreCase("true")) {
											new BukkitRunnable() {
												@Override
												public void run() {
													p.sendMessage(
															"§7Es wird die Datenbank auf SQLite gesetzt und dabei wird alles vom alte MySQL Datenbank importiert.");

													APBuy.stopCause = StopCause.TRANSFERINGDB;
													if (APBuy.getDatamaster().setDBToSQLite(true)) {
														p.sendMessage("§aErfolgreich geändert.");
													} else {

													}
													APBuy.stopCause = StopCause.NONE;
													APBuy.genStopByPlugin = false;
												}
											}.runTask(APBuy.plugin);
										} else if (args[3].equalsIgnoreCase("false")) {
											p.sendMessage("§7Es wird die Datenbank auf SQLite gesetzt");
											APBuy.getDatamaster().setDBToSQLite(false);
											p.sendMessage("§aErfolgreich geändert.");
										} else {
											p.sendMessage("§cSyntax: /apb dev setdb sqlite [true/false]");
										}
										break;
									case "mysql":
										if (APBuy.stopCause == StopCause.TRANSFERINGDB) {
											p.sendMessage("§7Es wird gerade schon transferiert...");
											return true;
										}
										if (args[3].equalsIgnoreCase("true")) {
											if (dbConfigurator != null) {
												p.sendMessage("§cDer Spieler §6" + dbConfigurator.getName()
														+ " §cist schon gerade am ändern... Bitte warten.");
												p.sendMessage("§7Wenn er aufhören soll, muss er oder disconnecten,"
														+ " oder \"@cancel\" in den CHat schreiben.");
												return true;
											}
											dbArgs[0] = "true";
											progress = 1;
											dbConfigurator = p;
											p.sendMessage("§7Bitte sende den Hoster in den Chat.");
										} else if (args[3].equalsIgnoreCase("false")) {
											if (dbConfigurator != null) {
												p.sendMessage("§cDer Spieler §6" + dbConfigurator.getName()
														+ " §cist schon gerade am ändern... Bitte warten.");
												p.sendMessage("§7Wenn er aufhören soll, muss er oder disconnecten,"
														+ " oder \"@cancel\" in den CHat schreiben.");
												return true;
											}
											dbArgs[0] = "false";
											progress = 1;
											dbConfigurator = p;
											p.sendMessage("§7Bitte sende den Hoster in den Chat.");
										} else {
											p.sendMessage("§cSyntax: /apb dev setdb sqlite [true/false]");
										}
										break;
									default:
										p.sendMessage("§cEs gibt nur 2 Datenbanken: \"sqlite\" und \"mysql\".");
										break;
									}
								} else {
									p.sendMessage("§c[APBuy] Ungültiger Befehl!");
								}
								return true;
							}
						} else {
							p.sendMessage("§c[APBuy] Ungültiger Befehl!");
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
								p.sendMessage("§c[APBuy] Hier sind die richtigen Commands:");
								p.sendMessage("§c  /apb open name <Spielername>");
								p.sendMessage("§c  /apb open uuid <UUID>");
								return true;
							}
							if (args[1].equalsIgnoreCase("name")) {
								String uuid = Utils.getUuid(args[2]);
								if (uuid == "error") {
									p.sendMessage(
											"§c[APBuy] Es gab einen Fehler bei der suche, bitte benutze [/apb open uuid <UUID>] um nach der UUID zu suchen, ist schneller und effektiver.");
									return true;
								} else if (uuid == "invalid name") {
									p.sendMessage(
											"§c[APBuy] Dieser Spieler ist nicht in der Mojang Datenbank zu finden,");
									p.sendMessage("§cist er Premium? Ist der Name richtig geschrieben?");
									return true;
								}
								if (APBuy.getMarketHandler().hasMarketByUUID(uuid)) {
									if (APBuy.getDatamaster().getDatabase().getMarketInfos(uuid).isOpen()
											|| p.hasPermission("apb.mod.open")) {
										APBuy.getMarketHandler().openMarketVisualiserToPlayer("Main", uuid, p);
										return true;
									} else {
										p.sendMessage("§c[APBuy] Sein Market ist geschlossen!");
										return true;
									}
								} else {
									p.sendMessage("§c[APBuy] Dieser Spieler besitzt keinen Market!");
									return true;
								}
							} else if (args[1].equalsIgnoreCase("uuid")) {
								if (APBuy.getMarketHandler().hasMarketByUUID(args[2])) {
									if (APBuy.getDatamaster().getDatabase().getMarketInfos(args[2]).isOpen()
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
								p.sendMessage("§c[APBuy] Hier sind die richtigen Commands:");
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
						p.sendMessage("§c[APBuy] Ungültiger Befehl!");
						return true;
					}

				}
			} catch (ArrayIndexOutOfBoundsException e) {
				p.sendMessage(Translator.translate("apbcmd.allargs"));
			} catch (APBuyException e) {
				if (e.getErrorCause() == ErrorCause.NOTFOUND_MARKET) {
					if (args.length >= 3) {
						if (args[0].equalsIgnoreCase("open")) {
							if (args[1].equalsIgnoreCase("uuid")) {
								p.sendMessage("§c[APBuy] Dieser Spieler besitzt keinen Market, die Richtige UUID?");
							}
						}
					}
				} else {
					e.printStackTrace();
					APBuy.getMarketHandler().removeFromAll(p);
					System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
					p.sendMessage(Translator.translate("dev.error"));
					p.sendMessage("§cFehler code: " + Utils.addToFix(e));
				}
			} catch (Exception e) {
				e.printStackTrace();
				APBuy.getMarketHandler().removeFromAll(p);
				System.out.println("Player: " + p.getName() + " (" + p.getUniqueId().toString() + ")");
				p.sendMessage(Translator.translate("dev.error"));
				p.sendMessage("§cFehler code: " + Utils.addToFix(e));
			}
		} else {
			arg0.sendMessage("§c[APBuy] Es kann nur ein Spieler senden.");
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

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (dbConfigurator == null) {
			return;
		}
		if (e.getPlayer().equals(dbConfigurator)) {
			e.setCancelled(true);
			switch (progress) {
			case 1:
				dbArgs[1] = e.getMessage();
				progress++;
				e.getPlayer().sendMessage("§7Bitte sende jetzt den Port.");
				break;
			case 2:
				dbArgs[2] = e.getMessage();
				progress++;
				e.getPlayer().sendMessage("§7Bitte sende jetzt den Datenbank Namen.");
				break;
			case 3:
				dbArgs[3] = e.getMessage();
				progress++;
				e.getPlayer().sendMessage("§7Bitte sende jetzt den Username.");
				break;
			case 4:
				dbArgs[4] = e.getMessage();
				progress++;
				e.getPlayer().sendMessage("§7Bitte sende jetzt den Passwort Namen.");
				break;
			case 5:
				if (dbArgs[0].equalsIgnoreCase("true")) {
					new BukkitRunnable() {

						@Override
						public void run() {
							e.getPlayer().sendMessage("§7Die Datenbank auf MySQL gesetzt und dabei "
									+ "wird alles vom alten Datenbank importiert und ersetzt.");
							APBuy.stopCause = StopCause.TRANSFERINGDB;
							if (APBuy.getDatamaster().setDBToMySQL(true, dbArgs[1], dbArgs[2], dbArgs[3], dbArgs[4],
									e.getMessage())) {
								e.getPlayer().sendMessage("§aErfolgreich geändert.");
								APBuy.plugin.getConfig().set("MySQLConfig.host", dbArgs[1]);
								APBuy.plugin.getConfig().set("MySQLConfig.port", dbArgs[2]);
								APBuy.plugin.getConfig().set("MySQLConfig.database", dbArgs[3]);
								APBuy.plugin.getConfig().set("MySQLConfig.username", dbArgs[4]);
								APBuy.plugin.getConfig().set("MySQLConfig.password", e.getMessage());
								APBuy.plugin.getConfig().set("Database", "mysql");
								APBuy.plugin.saveConfig();
								APBuy.stopCause = StopCause.NONE;
								APBuy.genStopByPlugin = false;
							} else {
								e.getPlayer().sendMessage("§cEs gab einen Fehler, sind die Daten"
										+ " alle korrekt? Bitte versuche es noch einmal.");
								APBuy.stopCause = StopCause.DATABASE;
							}
							APBuy.getDatamaster().setSqlite(false);
							dbConfigurator = null;
							progress = -1;
						}
					}.runTask(APBuy.plugin);
				} else {
					e.getPlayer().sendMessage("§7Die Datenbank auf SQLite gesetzt...");
					if (APBuy.getDatamaster().setDBToMySQL(false, dbArgs[1], dbArgs[2], dbArgs[3], dbArgs[4],
							e.getMessage())) {
						e.getPlayer().sendMessage("§aErfolgreich geändert.");
						APBuy.plugin.getConfig().set("MySQLConfig.host", dbArgs[1]);
						APBuy.plugin.getConfig().set("MySQLConfig.port", dbArgs[2]);
						APBuy.plugin.getConfig().set("MySQLConfig.database", dbArgs[3]);
						APBuy.plugin.getConfig().set("MySQLConfig.username", dbArgs[4]);
						APBuy.plugin.getConfig().set("MySQLConfig.password", e.getMessage());
						APBuy.plugin.getConfig().set("Database", "mysql");
						APBuy.plugin.saveConfig();
						APBuy.stopCause = StopCause.NONE;
						APBuy.genStopByPlugin = false;
					} else {
						e.getPlayer().sendMessage("§cEs gab einen Fehler, sind die Daten "
								+ "alle korrekt? Bitte versuche es noch einmal.");
						APBuy.stopCause = StopCause.DATABASE;
					}
					dbConfigurator = null;
					APBuy.getDatamaster().setSqlite(false);
					// TODO see log ERRORS
					progress = -1;
				}
				break;
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (dbConfigurator == null) {
			return;
		}
		if (e.getPlayer().equals(dbConfigurator)) {
			dbConfigurator = null;
			progress = -1;
		}
	}
}
