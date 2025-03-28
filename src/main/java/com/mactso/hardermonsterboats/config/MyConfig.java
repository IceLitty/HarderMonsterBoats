package com.mactso.hardermonsterboats.config;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.hardermonsterboats.Main;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MyConfig {

    static {

        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;


    public static String[] willMonsterMountBoat;
    public static String[] willMonsterNotLeaveBoat;
    public static String[] willMonsterNotHitBoat;
    public static boolean willMonsterNotHitBoatReverse;
    public static float hitBoatDamage;
    public static String[] boatType;
    public static boolean matchRegex;

    public static boolean isWillMonsterMountBoat(String classname) {
        for (String mod : willMonsterMountBoat) {
            if (matchRegex ? classname.matches(mod) : classname.contains(mod)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWillMonsterNotHitBoat(String classname) {
        for (String mod : willMonsterNotHitBoat) {
            if (matchRegex ? classname.matches(mod) : classname.contains(mod)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWillMonsterNotLeaveBoat(String classname) {
        for (String mod : willMonsterNotLeaveBoat) {
            if (matchRegex ? classname.matches(mod) : classname.contains(mod)) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == MyConfig.COMMON_SPEC) {
            bakeConfig();
        }
    }

    public static void bakeConfig() {
        matchRegex = COMMON.matchRegex.get();
        if (matchRegex) {
            try {
                for (String regex : COMMON.willMonsterMountBoat.get()) {
                    Pattern.compile(regex);
                }
                for (String regex : COMMON.willMonsterNotLeaveBoat.get()) {
                    Pattern.compile(regex);
                }
                for (String regex : COMMON.willMonsterNotHitBoat.get()) {
                    Pattern.compile(regex);
                }
            } catch (Exception e) {
                throw new RuntimeException("Wrong pattern of config: ", e);
            }
        }
        willMonsterMountBoat = extract(COMMON.willMonsterMountBoat.get());
        willMonsterNotLeaveBoat = extract(COMMON.willMonsterNotLeaveBoat.get());
        willMonsterNotHitBoatReverse = COMMON.willMonsterNotHitBoatReverse.get();
        willMonsterNotHitBoat = extract(COMMON.willMonsterNotHitBoat.get());
        hitBoatDamage = COMMON.hitBoatDamage.get();
        boatType = extract(COMMON.boatType.get());
    }

    private static String[] extract(List<String> value) {
        return value.toArray(new String[0]);
    }

    public static class Common {
        List<String> willMonsterMountBoatList = Arrays.asList(".*");
        List<String> willMonsterNotLeaveBoatList = Arrays.asList("minecraft:zombie_villager");
        List<String> willMonsterNotHitBoatList = Arrays.asList("minecraft:wither", "minecraft:ender_dragon");
        List<String> boatTypeList = Arrays.asList("boat", "minecart", "snowyspirit:sled", "car:car", "hpm:*");

        public final ModConfigSpec.ConfigValue<List<String>> willMonsterMountBoat;
        public final ModConfigSpec.ConfigValue<List<String>> willMonsterNotLeaveBoat;
        public final ModConfigSpec.ConfigValue<Boolean> willMonsterNotHitBoatReverse;
        public final ModConfigSpec.ConfigValue<List<String>> willMonsterNotHitBoat;
        public final ModConfigSpec.ConfigValue<Float> hitBoatDamage;
        public final ModConfigSpec.ConfigValue<List<String>> boatType;
        public final ModConfigSpec.ConfigValue<Boolean> matchRegex;

        public Common(ModConfigSpec.Builder builder) {
            willMonsterMountBoat = builder
                    .comment("Checked Monsters who will get into boats, registry name list")
                    .translation(Main.MODID + ".config." + "willMonsterMountBoat")
                    .define("willMonsterMountBoat", willMonsterMountBoatList);

            willMonsterNotLeaveBoat = builder
                    .comment("Checked Monsters who stay in boat when it take damage, registry name list")
                    .translation(Main.MODID + ".config." + "willMonsterNotLeaveBoat")
                    .define("willMonsterNotLeaveBoat", willMonsterNotLeaveBoatList);

            willMonsterNotHitBoatReverse = builder
                    .comment("Turn config willMonsterNotHitBoat to reverse. Set false to predicate mob if they are in list then break the boat, set true to predicate mob if they not are.")
                    .translation(Main.MODID + ".config.willMonsterNotHitBoatReverse")
                    .define("willMonsterNotHitBoatReverse", true);
            willMonsterNotHitBoat = builder
                    .comment("Checked Monsters who will don't break boat when ride, registry name list")
                    .translation(Main.MODID + ".config." + "willMonsterNotHitBoat")
                    .define("willMonsterNotHitBoat", willMonsterNotHitBoatList);
            hitBoatDamage = builder
                    .comment("Damage boat when willMonsterNotHitBoat triggered.")
                    .translation(Main.MODID + ".config.hitBoatDamage")
                    .define("hitBoatDamage", 6.0f);

            boatType = builder
                    .comment("Which type of boat will trigger predicate. Can be BOAT, MINECART, VEHICLE, ENTITY, *, or register id like snowyspirit:sled or hpm:*.")
                    .translation(Main.MODID + ".config.boatType")
                    .define("boatType", boatTypeList);

            matchRegex = builder
                    .comment("Use regex instead of contains Monsters registry name predicate.")
                    .translation(Main.MODID + ".config.matchRegex")
                    .define("matchRegex", true);
        }

        public static boolean isString(Object o) {
            return (o instanceof String);
        }
    }

}
