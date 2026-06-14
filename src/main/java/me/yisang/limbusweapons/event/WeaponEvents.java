package me.yisang.limbusweapons.event;

import me.yisang.limbusweapons.ModSounds;
import me.yisang.limbusweapons.item.DaCapoItem;
import me.yisang.limbusweapons.item.MimicryItem;
import me.yisang.limbusweapons.item.ModItems;
import me.yisang.limbusweapons.item.SolemnLamentItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class WeaponEvents {

    // ── 內部資料結構 ──────────────────────────────────────────────────────────

    private record ProjectileData(ItemEntity entity, UUID ownerId, boolean isBlack, int[] ticksAlive) {}
    private static final List<ProjectileData> activeProjectiles = Collections.synchronizedList(new ArrayList<>());

    private static final Map<UUID, Long> solemnCooldowns = new HashMap<>();

    private record BrushHit(UUID targetId, long timeMs) {}
    private static final Map<UUID, BrushHit> brushLastHit = new HashMap<>();

    private record DaCapoHit(int executeTick, PlayerEntity attacker, LivingEntity target,
                              float damage, boolean special) {}
    private static final List<DaCapoHit> dacapoQueue = Collections.synchronizedList(new ArrayList<>());

    private static final Set<UUID> processingDaCapo = Collections.synchronizedSet(new HashSet<>());

    private static int shieldTick = 0;

    // ── 注冊入口 ──────────────────────────────────────────────────────────────

    public static void register() {
        AttackEntityCallback.EVENT.register(WeaponEvents::onAttack);
        ServerTickEvents.END_SERVER_TICK.register(WeaponEvents::onServerTick);
    }

    // ── 攻擊事件 ─────────────────────────────────────────────────────────────

    private static ActionResult onAttack(PlayerEntity player, World world, Hand hand,
                                         Entity entity, EntityHitResult hitResult) {
        if (world.isClient) return ActionResult.PASS;
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        if (!(entity instanceof LivingEntity target)) return ActionResult.PASS;

        ItemStack stack = player.getMainHandStack();
        ServerWorld sw = (ServerWorld) world;

        if (stack.getItem() instanceof MimicryItem) {
            handleMimicry(player, sw, target);
            return ActionResult.PASS;
        }

        if (stack.getItem() instanceof DaCapoItem) {
            if (processingDaCapo.contains(player.getUuid())) return ActionResult.PASS;
            handleDaCapo(player, sw, target);
            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }

    // ── 擬態邏輯 ──────────────────────────────────────────────────────────────

    private static void handleMimicry(PlayerEntity player, ServerWorld world, LivingEntity target) {
        if (world.random.nextFloat() < 0.10f) {
            float bonus = 40.0f + world.random.nextFloat() * 50.0f;
            target.damage(world, world.getDamageSources().playerAttack(player), bonus);
            world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER,
                    target.getX(), target.getY() + 1, target.getZ(), 1, 0, 0, 0, 0);
        }

        float healthBefore = target.getHealth();
        world.getServer().execute(() -> {
            float dealt = healthBefore - target.getHealth();
            if (dealt > 0) player.heal(dealt * 0.25f);
        });
    }

    // ── DaCapo 邏輯 ───────────────────────────────────────────────────────────

    private static void handleDaCapo(PlayerEntity player, ServerWorld world, LivingEntity target) {
        boolean special = world.random.nextFloat() < 0.40f;
        int hitCount    = special ? 3 : 5;
        float damage    = special ? 17.0f : 4.0f;
        int interval    = special ? 4 : 2;
        int currentTick = world.getServer().getTicks();

        for (int i = 0; i < hitCount; i++) {
            dacapoQueue.add(new DaCapoHit(currentTick + i * interval, player, target, damage, special));
        }
    }

    private static void processDaCapo(MinecraftServer server) {
        int tick = server.getTicks();
        dacapoQueue.removeIf(hit -> {
            if (hit.executeTick() > tick) return false;

            PlayerEntity p  = hit.attacker();
            LivingEntity tg = hit.target();

            if (tg == null || !tg.isAlive()) return true;
            if (!p.getMainHandStack().getItem().getClass().equals(DaCapoItem.class)) return true;

            ServerWorld sw = server.getWorld(p.getWorld().getRegistryKey());
            if (sw == null) return true;

            processingDaCapo.add(p.getUuid());
            try {
                tg.damage(sw, sw.getDamageSources().playerAttack(p), hit.damage());
                tg.hurtTime = 0;

                for (Entity nearby : tg.getWorld().getOtherEntities(p,
                        tg.getBoundingBox().expand(3.5))) {
                    if (!(nearby instanceof LivingEntity v)) continue;
                    if (nearby.equals(tg)) continue;
                    if (nearby instanceof PlayerEntity) continue;
                    if (nearby instanceof TameableEntity te && te.isTamed()) continue;
                    v.damage(sw, sw.getDamageSources().playerAttack(p), hit.damage() * 0.7f);
                    v.hurtTime = 0;
                }

                int color = hit.special() ? 0xFFFFFF : 0xB2B2B2;
                sw.spawnParticles(new DustParticleEffect(color, 1.2f),
                        tg.getX(), tg.getY() + 1, tg.getZ(), 15, 0.3, 0.3, 0.3, 0);

                String sound = hit.special() ? "block.anvil.place" : "block.note_block.harp";
                sw.playSound(null, tg.getBlockPos(),
                        net.minecraft.registry.Registries.SOUND_EVENT.get(
                                net.minecraft.util.Identifier.of(sound)),
                        SoundCategory.PLAYERS, 0.8f, 1.5f);
            } finally {
                processingDaCapo.remove(p.getUuid());
            }
            return true;
        });
    }

    // ── 環指筆刷邏輯 ─────────────────────────────────────────────────────────

    public static void handleRingBrush(PlayerEntity player, LivingEntity target) {
        long now = System.currentTimeMillis();
        UUID pid = player.getUuid();

        BrushHit last = brushLastHit.get(pid);
        boolean doubleHit = (last != null
                && now - last.timeMs() < 1500
                && last.targetId().equals(target.getUuid()));

        int hits = doubleHit ? 2 : 1;
        ServerWorld sw = (ServerWorld) player.getWorld();

        for (int i = 0; i < hits; i++) {
            applyBrushEffect(player, sw, target);
        }

        if (doubleHit) {
            brushLastHit.remove(pid);
        } else {
            player.setVelocity(player.getRotationVector().multiply(1.2).add(0, 0.2, 0));
            player.velocityModified = true;
            brushLastHit.put(pid, new BrushHit(target.getUuid(), now));
        }

        brushLastHit.entrySet().removeIf(e -> now - e.getValue().timeMs() > 1500);
    }

    private static final StatusEffectInstance[] BRUSH_EFFECTS = {
            new StatusEffectInstance(StatusEffects.BLINDNESS, 80, 0),
            new StatusEffectInstance(StatusEffects.SLOWNESS,  80, 1),
            new StatusEffectInstance(StatusEffects.POISON,    80, 0),
            new StatusEffectInstance(StatusEffects.WEAKNESS,  80, 1),
            new StatusEffectInstance(StatusEffects.WITHER,    80, 0),
    };

    private static void applyBrushEffect(PlayerEntity player, ServerWorld world, LivingEntity target) {
        target.damage(world, world.getDamageSources().playerAttack(player), 3.5f);
        StatusEffectInstance effect = BRUSH_EFFECTS[world.random.nextInt(BRUSH_EFFECTS.length)];
        target.addStatusEffect(new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier()));

        float r = world.random.nextFloat();
        float g = world.random.nextFloat() * 0.4f;
        float b = world.random.nextFloat() * 0.4f;
        int color = ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
        world.spawnParticles(new DustParticleEffect(color, 1.5f),
                target.getX(), target.getY() + 1, target.getZ(), 20, 0.3, 0.3, 0.3, 0);
    }

    // ── 莊嚴哀悼：射擊 ───────────────────────────────────────────────────────

    public static boolean handleSolemnLamentUse(PlayerEntity player, ServerWorld world, SolemnLamentItem weapon) {
        long now = System.currentTimeMillis();
        if (now - solemnCooldowns.getOrDefault(player.getUuid(), 0L) < 1200) return false;

        ItemStack ammo = findButterfly(player);
        if (ammo == null) return false;

        solemnCooldowns.put(player.getUuid(), now);

        world.playSound(null, player.getBlockPos(), ModSounds.SOLEMN_LOAD,
                SoundCategory.PLAYERS, 0.6f, 1.0f);

        int shootTick = world.getServer().getTicks() + 20;
        boolean isBlack = weapon.isBlack;

        scheduledShoots.add(new ScheduledShoot(shootTick, player, world.getRegistryKey(), isBlack));
        return true;
    }

    private record ScheduledShoot(int executeTick, PlayerEntity player,
                                   net.minecraft.registry.RegistryKey<World> worldKey, boolean isBlack) {}
    private static final List<ScheduledShoot> scheduledShoots = Collections.synchronizedList(new ArrayList<>());

    private static void processScheduledShoots(MinecraftServer server) {
        int tick = server.getTicks();
        scheduledShoots.removeIf(s -> {
            if (s.executeTick() > tick) return false;

            ServerWorld sw = server.getWorld(s.worldKey());
            if (sw == null) return true;
            PlayerEntity p = s.player();

            if (!(p.getMainHandStack().getItem() instanceof SolemnLamentItem)) return true;
            ItemStack ammo = findButterfly(p);
            if (ammo == null) return true;

            ammo.decrement(1);
            spawnSolemnProjectile(p, sw, s.isBlack());
            return true;
        });
    }

    private static void spawnSolemnProjectile(PlayerEntity player, ServerWorld world, boolean isBlack) {
        ItemStack visual = new ItemStack(ModItems.BUTTERFLY_QUARTZ);
        ItemEntity proj  = new ItemEntity(world,
                player.getX(), player.getEyeY(), player.getZ(), visual);
        proj.setPickupDelay(32767);

        Vec3d dir = player.getRotationVector().multiply(3.0);
        proj.setVelocity(dir);
        proj.setNeverDespawn();
        world.spawnEntity(proj);

        activeProjectiles.add(new ProjectileData(proj, player.getUuid(), isBlack, new int[]{0}));

        world.playSound(null, player.getBlockPos(), ModSounds.SOLEMN_SHOOT,
                SoundCategory.PLAYERS, 0.8f, 1.0f);
    }

    private static void tickProjectiles(MinecraftServer server) {
        activeProjectiles.removeIf(data -> {
            ItemEntity proj = data.entity();
            if (!proj.isAlive()) return true;

            data.ticksAlive()[0]++;
            if (data.ticksAlive()[0] > 100) { proj.discard(); return true; }

            ServerWorld sw = (ServerWorld) proj.getWorld();

            sw.spawnParticles(ParticleTypes.SQUID_INK,
                    proj.getX(), proj.getY(), proj.getZ(), 2, 0.02, 0.02, 0.02, 0.01);
            sw.spawnParticles(ParticleTypes.WHITE_ASH,
                    proj.getX(), proj.getY(), proj.getZ(), 4, 0.05, 0.05, 0.05, 0.01);

            if (proj.isOnGround()) {
                sw.spawnParticles(ParticleTypes.SQUID_INK,
                        proj.getX(), proj.getY(), proj.getZ(), 8, 0.1, 0.1, 0.1, 0.05);
                proj.discard();
                return true;
            }

            PlayerEntity owner = sw.getPlayerByUuid(data.ownerId());
            if (owner == null) { proj.discard(); return true; }

            for (Entity nearby : sw.getOtherEntities(proj, proj.getBoundingBox().expand(0.8))) {
                if (!(nearby instanceof LivingEntity target)) continue;
                if (nearby.equals(owner)) continue;

                sw.playSound(null, proj.getBlockPos(), ModSounds.SOLEMN_HIT,
                        SoundCategory.PLAYERS, 1.0f, 1.0f);
                sw.spawnParticles(ParticleTypes.SQUID_INK,
                        proj.getX(), proj.getY(), proj.getZ(), 15, 0.1, 0.1, 0.1, 0.05);

                if (data.isBlack()) {
                    target.damage(sw, sw.getDamageSources().playerAttack(owner), 8.0f);
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 80, 1));
                } else {
                    target.damage(sw, sw.getDamageSources().playerAttack(owner), 4.0f);
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));
                }

                proj.discard();
                return true;
            }

            return false;
        });
    }

    // ── 聖宣盾牌光環 ─────────────────────────────────────────────────────────

    private static void tickShieldAura(MinecraftServer server) {
        shieldTick++;
        if (shieldTick % 5 != 0) return;

        for (ServerWorld world : server.getWorlds()) {
            for (PlayerEntity player : world.getPlayers()) {
                boolean hasShield =
                        player.getMainHandStack().getItem() == ModItems.SOLEMN_SHIELD ||
                        player.getOffHandStack().getItem()  == ModItems.SOLEMN_SHIELD;
                if (!hasShield) continue;

                world.spawnParticles(ParticleTypes.WHITE_ASH,
                        player.getX(), player.getY() + 1, player.getZ(),
                        8, 0.4, 0.4, 0.4, 0.02);

                for (Entity e : world.getOtherEntities(player,
                        player.getBoundingBox().expand(5))) {
                    if (e instanceof LivingEntity target) {
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 1));
                    }
                }
            }
        }
    }

    // ── 主 Tick ───────────────────────────────────────────────────────────────

    private static void onServerTick(MinecraftServer server) {
        tickShieldAura(server);
        processDaCapo(server);
        processScheduledShoots(server);
        tickProjectiles(server);
    }

    // ── 輔助 ─────────────────────────────────────────────────────────────────

    private static ItemStack findButterfly(PlayerEntity player) {
        for (ItemStack s : player.getInventory().main) {
            if (s.getItem() == ModItems.BUTTERFLY_QUARTZ) return s;
        }
        return null;
    }
}
