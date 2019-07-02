package ap.apb.apbuy.itoomel;

import ap.apb.APBuy;
import ap.apb.Translator;
import org.bukkit.Material;

public class ICats {
	public class ICatFeaturedBB extends ItoomelCat {
		public ICatFeaturedBB() {
			super(Translator.translate("icats.building.title"), Material.STONE,
					Translator.translate("icats.building.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.STONE, new Short[] { 0, 1, 2, 3, 4, 5, 6 });
			this.addMat(Material.DIRT, new Short[] { 0, 1, 2 });
			this.addMat(Material.COBBLESTONE);
			this.addMat(Material.OBSIDIAN);
			this.addMat(Material.LOG);
			this.addMat(Material.LOG_2);
			this.addMat(Material.WOOD);
			this.addMat(Material.GRAVEL);
			this.addMat(Material.GRASS);
			this.addMat(Material.GLASS);
			this.addMat(Material.STAINED_GLASS);
			this.addMat(Material.THIN_GLASS);
			this.addMat(Material.STAINED_GLASS_PANE);
			this.addMat(Material.SPONGE);
			this.addMat(Material.WOOL, new Short[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 });
			this.addMat(Material.SANDSTONE);
			this.addMat(Material.BRICK);
			this.addMat(Material.ICE);
			this.addMat(Material.PACKED_ICE);
			this.addMat(Material.MOSSY_COBBLESTONE);
			this.addMat(Material.RED_SANDSTONE);
			this.addMat(Material.BOOKSHELF);
			this.addMat(Material.JACK_O_LANTERN);
			this.addMat(Material.MYCEL);
			this.addMat(Material.SNOW_BLOCK);
			this.addMat(Material.HARD_CLAY);
			this.addMat(Material.STAINED_CLAY);
			this.addMat(Material.SLIME_BLOCK);
			return this;
		}
	}

	public class ICatNether extends ItoomelCat {
		public ICatNether() {
			super(Translator.translate("icats.nether.title"), Material.NETHERRACK,
					Translator.translate("icats.nether.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.NETHERRACK);
			this.addMat(Material.SOUL_SAND);
			this.addMat(Material.NETHER_BRICK);
			this.addMat(Material.NETHER_BRICK_ITEM);
			this.addMat(Material.NETHER_BRICK_STAIRS);
			this.addMat(Material.QUARTZ);
			this.addMat(Material.QUARTZ_BLOCK);
			this.addMat(Material.QUARTZ_STAIRS);
			this.addMat(Material.QUARTZ_ORE);
			this.addMat(Material.GLOWSTONE_DUST);
			this.addMat(Material.NETHER_WARTS);
			this.addMat(Material.BLAZE_ROD);
			this.addMat(Material.BLAZE_POWDER);
			return this;
		}
	}

	public class ICatOres extends ItoomelCat {
		public ICatOres() {
			super(Translator.translate("icats.ores.title"), Material.DIAMOND_ORE,
					Translator.translate("icats.ores.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.DIAMOND_ORE);
			this.addMat(Material.DIAMOND_BLOCK);
			this.addMat(Material.DIAMOND);
			this.addMat(Material.EMERALD_ORE);
			this.addMat(Material.EMERALD_BLOCK);
			this.addMat(Material.EMERALD);
			this.addMat(Material.LAPIS_ORE);
			this.addMat(Material.LAPIS_BLOCK);
			this.addMat(Material.INK_SACK, new Short[] { 4 });
			this.addMat(Material.REDSTONE_ORE);
			this.addMat(Material.REDSTONE_BLOCK);
			this.addMat(Material.REDSTONE);
			this.addMat(Material.IRON_ORE);
			this.addMat(Material.IRON_BLOCK);
			this.addMat(Material.IRON_INGOT);
			this.addMat(Material.GOLD_ORE);
			this.addMat(Material.GOLD_BLOCK);
			this.addMat(Material.GOLD_INGOT);
			this.addMat(Material.GOLD_NUGGET);
			this.addMat(Material.COAL_ORE);
			this.addMat(Material.COAL_BLOCK);
			this.addMat(Material.COAL);
			this.addMat(Material.QUARTZ_ORE);
			this.addMat(Material.QUARTZ_BLOCK);
			this.addMat(Material.QUARTZ);
			return this;
		}
	}

	public class ICatDye extends ItoomelCat {
		public ICatDye() {
			super(Translator.translate("icats.dyes.title"), Material.INK_SACK, Translator.translate("icats.dyes.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.INK_SACK, new Short[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 });
			return this;
		}
	}

	public class ICatDiaTools extends ItoomelCat {
		public ICatDiaTools() {
			super(Translator.translate("icats.tools.dia.title"), Material.DIAMOND_PICKAXE,
					Translator.translate("icats.tools.dia.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.DIAMOND_AXE);
			this.addMat(Material.DIAMOND_PICKAXE);
			this.addMat(Material.DIAMOND_SPADE);
			this.addMat(Material.DIAMOND_HOE);
			return this;
		}
	}

	public class ICatStoneTools extends ItoomelCat {
		public ICatStoneTools() {
			super(Translator.translate("icats.tools.stone.title"), Material.STONE_PICKAXE,
					Translator.translate("icats.tools.stone.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.STONE_AXE);
			this.addMat(Material.STONE_PICKAXE);
			this.addMat(Material.STONE_SPADE);
			this.addMat(Material.STONE_HOE);
			return this;
		}
	}

	public class ICatIronTools extends ItoomelCat {
		public ICatIronTools() {
			super(Translator.translate("icats.tools.iron.title"), Material.IRON_PICKAXE,
					Translator.translate("icats.tools.iron.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.IRON_AXE);
			this.addMat(Material.IRON_PICKAXE);
			this.addMat(Material.IRON_SPADE);
			this.addMat(Material.IRON_HOE);
			return this;
		}
	}

	public class ICatWoodenTools extends ItoomelCat {
		public ICatWoodenTools() {
			super(Translator.translate("icats.tools.wood.title"), Material.WOOD_PICKAXE,
					Translator.translate("icats.tools.wood.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.WOOD_AXE);
			this.addMat(Material.WOOD_PICKAXE);
			this.addMat(Material.WOOD_SPADE);
			this.addMat(Material.WOOD_HOE);
			return this;
		}
	}

	public class ICatGoldTools extends ItoomelCat {
		public ICatGoldTools() {
			super(Translator.translate("icats.tools.gold.title"), Material.GOLD_PICKAXE,
					Translator.translate("icats.tools.gold.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.GOLD_AXE);
			this.addMat(Material.GOLD_PICKAXE);
			this.addMat(Material.GOLD_SPADE);
			this.addMat(Material.GOLD_HOE);
			return this;
		}
	}

	public class ICatTools extends ItoomelCat {
		public ICatTools() {
			super(Translator.translate("icats.tools.misc.title"), Material.SHEARS,
					Translator.translate("icats.tools.misc.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.SHEARS);
			this.addMat(Material.FISHING_ROD);
			this.addMat(Material.WATCH);
			this.addMat(Material.LEASH);
			this.addMat(Material.COMPASS);
			this.addMat(Material.FLINT_AND_STEEL);
			return this;
		}
	}

	public class ICatBattle extends ItoomelCat {
		public ICatBattle() {
			super(Translator.translate("icats.battle.title"), Material.DIAMOND_SWORD,
					Translator.translate("icats.battle.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.DIAMOND_SWORD);
			this.addMat(Material.GOLD_SWORD);
			this.addMat(Material.IRON_SWORD);
			this.addMat(Material.STONE_SWORD);
			this.addMat(Material.WOOD_SWORD);
			this.addMat(Material.BOW);
			this.addMat(Material.ARROW);
			return this;
		}
	}

	public class ICatSpawnEggs extends ItoomelCat {
		public ICatSpawnEggs() {
			super("�6Spawn Eggs", Material.MONSTER_EGG, "Hier kannst du dir Spawneggs besorgen.");
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.MONSTER_EGG, new Short[] { -1, 50, 68, 52, 95, 67, 59, 94, 51, 58, 61, 57, 56, 62, 93,
					96, 66, 60, 65, 91, 98, 54, 55, 120, 92, 101, 100, 99, 97 });
			return this;
		}
	}

	public class ICatEnchantedBooks extends ItoomelCat {
		public ICatEnchantedBooks() {
			super("�6Verzauberte B�cher", Material.ENCHANTED_BOOK, "Hier kannst du dir verzauberte B�cher besorgen.");
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.ENCHANTED_BOOK);
			return this;
		}
	}

	public class ICatPotions extends ItoomelCat {
		public ICatPotions() {
			super("�6Tr�nke", Material.POTION, "Hier kannst du dir Tr�nke besorgen.");
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.POTION);
			return this;
		}
	}

	public class ICatArmor extends ItoomelCat {
		public ICatArmor() {
			super(Translator.translate("icats.armor.title"), Material.DIAMOND_CHESTPLATE,
					Translator.translate("icats.armor.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.DIAMOND_HELMET);
			this.addMat(Material.DIAMOND_CHESTPLATE);
			this.addMat(Material.DIAMOND_LEGGINGS);
			this.addMat(Material.DIAMOND_BOOTS);
			this.addMat(Material.IRON_HELMET);
			this.addMat(Material.IRON_CHESTPLATE);
			this.addMat(Material.IRON_LEGGINGS);
			this.addMat(Material.IRON_BOOTS);
			this.addMat(Material.GOLD_HELMET);
			this.addMat(Material.GOLD_CHESTPLATE);
			this.addMat(Material.GOLD_LEGGINGS);
			this.addMat(Material.GOLD_BOOTS);
			this.addMat(Material.CHAINMAIL_HELMET);
			this.addMat(Material.CHAINMAIL_CHESTPLATE);
			this.addMat(Material.CHAINMAIL_LEGGINGS);
			this.addMat(Material.CHAINMAIL_BOOTS);
			this.addMat(Material.LEATHER_HELMET);
			this.addMat(Material.LEATHER_CHESTPLATE);
			this.addMat(Material.LEATHER_LEGGINGS);
			this.addMat(Material.LEATHER_BOOTS);
			return this;
		}
	}

	public class ICatFood extends ItoomelCat {
		public ICatFood() {
			super(Translator.translate("icats.food.title"), Material.APPLE, Translator.translate("icats.food.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.GOLDEN_APPLE);
			this.addMat(Material.COOKED_BEEF);
			this.addMat(Material.COOKED_CHICKEN);
			this.addMat(Material.COOKED_FISH);
			this.addMat(Material.COOKED_MUTTON);
			this.addMat(Material.COOKED_RABBIT);
			this.addMat(Material.BAKED_POTATO);
			this.addMat(Material.POTATO_ITEM);
			this.addMat(Material.CARROT_ITEM);
			this.addMat(Material.APPLE);
			this.addMat(Material.MUSHROOM_SOUP);
			this.addMat(Material.COOKIE);
			this.addMat(Material.MELON);
			this.addMat(Material.GOLDEN_APPLE);
			this.addMat(Material.GOLDEN_CARROT);
			this.addMat(Material.RABBIT_STEW);
			this.addMat(Material.BREAD);
			this.addMat(Material.CAKE);
			this.addMat(Material.PUMPKIN_PIE);
			this.addMat(Material.RAW_BEEF);
			this.addMat(Material.RAW_CHICKEN);
			this.addMat(Material.RAW_FISH);
			this.addMat(Material.MUTTON);
			this.addMat(Material.RABBIT);
			return this;
		}
	}

	public class ICatBrewing extends ItoomelCat {
		public ICatBrewing() {
			super(Translator.translate("icats.brewing.title"), Material.BREWING_STAND_ITEM,
					Translator.translate("icats.brewing.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.BREWING_STAND_ITEM);
			this.addMat(Material.GLASS_BOTTLE);
			this.addMat(Material.SPIDER_EYE);
			this.addMat(Material.FERMENTED_SPIDER_EYE);
			this.addMat(Material.GLOWSTONE_DUST);
			this.addMat(Material.REDSTONE);
			this.addMat(Material.NETHER_STALK);
			this.addMat(Material.SPECKLED_MELON);
			this.addMat(Material.CAULDRON_ITEM);
			this.addMat(Material.MAGMA_CREAM);
			this.addMat(Material.RABBIT_FOOT);
			this.addMat(Material.GOLDEN_CARROT);
			this.addMat(Material.BLAZE_POWDER);
			this.addMat(Material.BLAZE_ROD);
			this.addMat(Material.SULPHUR);
			return this;
		}
	}

	public class ICatRedstone extends ItoomelCat {
		public ICatRedstone() {
			super(Translator.translate("icats.redstone.title"), Material.REDSTONE,
					Translator.translate("icats.redstone.desc"));
			this.registerMats();
		}

		@Override
		public ItoomelCat registerMats() {
			this.addMat(Material.REDSTONE);
			this.addMat(Material.REDSTONE_BLOCK);
			this.addMat(Material.REDSTONE_COMPARATOR);
			this.addMat(Material.REDSTONE_ORE);
			this.addMat(Material.REDSTONE_LAMP_OFF);
			this.addMat(Material.REDSTONE_TORCH_ON);
			this.addMat(Material.HOPPER);
			this.addMat(Material.DROPPER);
			this.addMat(Material.DISPENSER);
			this.addMat(Material.PISTON_BASE);
			this.addMat(Material.PISTON_STICKY_BASE);
			this.addMat(Material.DIODE);
			this.addMat(Material.DAYLIGHT_DETECTOR);
			this.addMat(Material.GOLD_PLATE);
			this.addMat(Material.IRON_PLATE);
			this.addMat(Material.STONE_PLATE);
			this.addMat(Material.WOOD_PLATE);
			this.addMat(Material.STONE_BUTTON);
			this.addMat(Material.WOOD_BUTTON);
			this.addMat(Material.TRIPWIRE_HOOK);
			this.addMat(Material.TRAP_DOOR);
			this.addMat(Material.IRON_TRAPDOOR);
			this.addMat(Material.IRON_DOOR);
			this.addMat(Material.TNT);
			this.addMat(Material.LEVER);
			this.addMat(Material.NOTE_BLOCK);

			return this;
		}
	}

	// TODO Add more Categories
	// TODO do Custom ICats, Sql

	public static ItoomelCat getICatByType(Material type) {
		for (ItoomelCat ic : APBuy.plugin.getIcatslist()) {
			if (ic.getCatMat() == type) {
				return ic;
			}
		}
		return null;
	}
}
