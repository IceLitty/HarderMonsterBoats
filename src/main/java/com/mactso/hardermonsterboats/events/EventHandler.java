package com.mactso.hardermonsterboats.events;

import com.mactso.hardermonsterboats.Main;
import com.mactso.hardermonsterboats.config.MyConfig;

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

    private static boolean isVehicle(Entity entity) {
        if (MyConfig.boatType == null) {
            return false;
        }
        for (String id : MyConfig.boatType) {
            if ("boat".equalsIgnoreCase(id)) {
                if (entity instanceof Boat) {
                    return true;
                }
            } else if ("minecart".equalsIgnoreCase(id)) {
                if (entity instanceof Minecart) {
                    return true;
                }
            } else if ("vehicle".equalsIgnoreCase(id)) {
                if (entity instanceof VehicleEntity) {
                    return true;
                }
            } else if ("entity".equalsIgnoreCase(id)) {
                if (entity instanceof LivingEntity) {
                    return true;
                }
            } else if ("*".equalsIgnoreCase(id)) {
                return true;
            } else if (id.contains(":")) {
                String encodeId = entity.getEncodeId();
                if (encodeId != null) {
                    String namespaceKey = id.substring(0, id.indexOf(":"));
                    String namespaceVal = id.substring(id.indexOf(":") + 1);
                    String entityKey;
                    String entityVal;
                    if (encodeId.contains(":")) {
                        entityKey = encodeId.substring(0, encodeId.indexOf(":"));
                        entityVal = encodeId.substring(encodeId.indexOf(":") + 1);
                    } else {
                        entityKey = null;
                        entityVal = encodeId;
                    }
                    if ("*".equalsIgnoreCase(namespaceKey)) {
                        if (namespaceVal.equalsIgnoreCase(entityVal)) {
                            return true;
                        }
                    } else if ("*".equalsIgnoreCase(namespaceVal)) {
                        if (namespaceKey.equalsIgnoreCase(entityKey)) {
                            return true;
                        }
                    } else if (namespaceKey.equalsIgnoreCase(entityKey) && namespaceVal.equalsIgnoreCase(entityVal)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onTarget(LivingDamageEvent.Pre event) {
        LivingEntity e = event.getEntity();
        if (event.getEntity() instanceof Monster) {
            Entity vehicle = e.getVehicle();
            boolean isVehicle = isVehicle(vehicle);
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
            boolean isVehicle = isVehicle(vehicle);
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
