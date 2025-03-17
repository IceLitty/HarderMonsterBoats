package com.mactso.hardermonsterboats.events;

import com.mactso.hardermonsterboats.Main;
import com.mactso.hardermonsterboats.config.MyConfig;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Main.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void onTarget(LivingDamageEvent.Pre event) {
        LivingEntity e = event.getEntity();
        if (event.getEntity() instanceof Monster) {
            Entity vehicle = e.getVehicle();
            boolean isVehicle = (MyConfig.boatType.compareTo(MyConfig.BoatType.BOAT) == 0 && vehicle instanceof Boat)
                    || (MyConfig.boatType.compareTo(MyConfig.BoatType.MINECART) == 0 && vehicle instanceof Minecart)
                    || (MyConfig.boatType.compareTo(MyConfig.BoatType.BOAT_AND_MINECART) == 0 && (vehicle instanceof Boat || vehicle instanceof Minecart))
                    || (MyConfig.boatType.compareTo(MyConfig.BoatType.EVERY_VEHICLE) == 0 && vehicle instanceof VehicleEntity);
            if (isVehicle) {
                String meRN = EntityType.getKey(e.getType()).toString();
                if (!MyConfig.isWillMonsterNotLeaveBoat(meRN)) {
                    e.stopRiding();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onMountEvent(EntityMountEvent event) {
        if (event.getEntity() instanceof Monster me) {
            Entity vehicle = event.getEntityBeingMounted();
            boolean isVehicle = (MyConfig.boatType.compareTo(MyConfig.BoatType.BOAT) == 0 && vehicle instanceof Boat)
                    || (MyConfig.boatType.compareTo(MyConfig.BoatType.MINECART) == 0 && vehicle instanceof Minecart)
                    || (MyConfig.boatType.compareTo(MyConfig.BoatType.BOAT_AND_MINECART) == 0 && (vehicle instanceof Boat || vehicle instanceof Minecart))
                    || (MyConfig.boatType.compareTo(MyConfig.BoatType.EVERY_VEHICLE) == 0 && vehicle instanceof VehicleEntity);
            if (isVehicle) {
                String meRN = EntityType.getKey(me.getType()).toString();
                boolean willMonsterNotHitBoat = MyConfig.isWillMonsterNotHitBoat(meRN);
                if (MyConfig.willMonsterNotHitBoatReverse) {
                    willMonsterNotHitBoat = !willMonsterNotHitBoat;
                }
                if (!willMonsterNotHitBoat) {
                    vehicle.hurt(me.damageSources().generic(), MyConfig.hitBoatDamage);
                }
                if (MyConfig.isWillMonsterMountBoat(meRN)) {
                    return;
                } else {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
