package me.yisang.limbus;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class LimbusEGOWeapons extends JavaPlugin implements Listener, TabCompleter {
    private String resourcePackHash = "";
    private final Map<String, EGOWeapon> weaponModules = new HashMap<>();
    private solemnlament solemn;
    private final String PACK_URL = "https://github.com/EvansGoethe/Limbus-E.G.O-weapon-plugin-ResourcePack/releases/download/Releases/Limbus_E.G.O_Weapons_plugin_ResourcePack.v.10.zip";
    private final String PACK_HASH = "3bcfbe628466736b7fa6e1649a2cf2bd847e5fce";

    @Override
    public void onEnable() {
        // 1. 初始化武器模組
        this.solemn = new solemnlament(this);
        mimicry m = new mimicry(this);
        dacapo d = new dacapo(this);
        ringbrush r = new ringbrush(this);

        weaponModules.put("mimicry", m);
        weaponModules.put("dacapo", d);
        weaponModules.put("brush", r);

        // 2. 註冊事件
        registerModule(m);
        registerModule(d);
        registerModule(r);

        // 3. 啟動輔助功能
        startShieldTick();
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            registerSoundInterceptor();
        }

        // --- 註冊與定時任務 ---
        getServer().getPluginManager().registerEvents(this, this);

        if (getCommand("getego") != null) {
            getCommand("getego").setExecutor(this);
            getCommand("getego").setTabCompleter(this);
        }
    }
    private void registerModule(org.bukkit.event.Listener module) {
        getServer().getPluginManager().registerEvents(module, this);
    }
    private void startShieldTick() {
        // 這裡放你原本計畫的護盾邏輯，或是先留空讓紅字消失
        org.bukkit.Bukkit.getScheduler().runTaskTimer(this, () -> {
            // 護盾檢查代碼...
        }, 0L, 1L);
    }

    // 3. 自動推送給玩家
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // 毫無懸念，直接推送寫死的 URL[cite: 12]
        event.getPlayer().setResourcePack(PACK_URL, PACK_HASH, true,
                net.kyori.adventure.text.Component.text("Receiving resource pack..."));
    }

    // 攔截拉弓/裝填音效的核心邏輯[cite: 7]
    private void registerSoundInterceptor() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL,
                PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item == null || !item.hasItemMeta() || item.getItemMeta().getItemModel() == null) return;

                String model = item.getItemMeta().getItemModel().toString();

                if (model.contains("solemn_lament")) {
                    Object soundObj = event.getPacket().getModifier().read(0);
                    String soundName = (soundObj != null) ? soundObj.toString().toLowerCase() : "";

                    if ((soundName.contains("bow") || soundName.contains("arrow") || soundName.contains("crossbow"))
                            && !soundName.contains("solemnlament")) {
                        event.setCancelled(true);
                    }
                }
            }
        });
    }

    public String translateHexColorCodes(String message) {
        java.util.regex.Pattern hexPattern = java.util.regex.Pattern.compile("&#([A-Fa-f0-9]{6})");
        java.util.regex.Matcher matcher = hexPattern.matcher(message);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) replacement.append('§').append(c);
            matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareLoad(EntityLoadCrossbowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack weapon = event.getCrossbow();
        if (solemn.isSolemnLament(weapon)) {
            // 全背包偵測蝴蝶箭[cite: 6, 7]
            if (!solemn.hasButterflyQuartz(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWeaponInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;

        // 檢查是否為莊嚴哀悼
        if (solemn.isSolemnLament(item)) {
            // 如果有蝴蝶子彈[cite: 8]
            if (hasButterflyQuartz(player)) {
                // 1. 播放自定義裝填音效（不再依賴原版攔截）[cite: 8]
                int quickLevel = item.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.QUICK_CHARGE);
                String sound = (quickLevel > 0) ? "solemnlament:solemn.quick_load." + Math.min(quickLevel, 3) : "solemnlament:solemn.load";
                player.getWorld().playSound(player.getLocation(), sound, 0.6f, 1.0f);

                // 2. 解決生存模式「沒箭矢不能拉弓」的問題[cite: 8]
                // 如果玩家背包真的沒箭矢，我們在拉弓瞬間塞給他一支隱形箭[cite: 8]
                if (!hasNormalArrows(player)) {
                    player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                    // 這裡可以加一個定時任務，1秒後把這支箭收回來，或是讓 onShoot 把它消耗掉[cite: 8]
                }
            }
        }
    }



    private boolean hasNormalArrows(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.ARROW && !solemn.isButterfly(item)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack bow = event.getBow();
        if (solemn.isSolemnLament(bow)) {
            // 1. 優先攔截：無論如何都不讓原版的箭射出去
            event.setCancelled(true);

            // 2. 尋找並消耗石英蝴蝶[cite: 16, 18]
            ItemStack quartz = findButterflyQuartz(player);
            if (quartz == null) {
                return;
            }

            // 3. 執行手動扣除
            quartz.setAmount(quartz.getAmount() - 1);

            // 4. 呼叫手動射擊方法，並傳入正確的 Model 字串來判斷黑白[cite: 16, 18]
            String model = bow.getItemMeta().getItemModel().toString();
            solemn.handleShootManual(player, bow, model);

            // 5. 強制重置弩的狀態 (防呆，避免玩家卡在裝填動作)
            if (bow.getType() == Material.CROSSBOW) {
                org.bukkit.inventory.meta.CrossbowMeta meta = (org.bukkit.inventory.meta.CrossbowMeta) bow.getItemMeta();
                meta.setChargedProjectiles(Collections.emptyList());
                bow.setItemMeta(meta);
            }
        }
    }
    private boolean hasButterflyQuartz(Player player) {
        return findButterflyQuartz(player) != null;
    }

    private ItemStack findButterflyQuartz(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            // 呼叫 solemnlament 裡已經改好的 isButterfly (現在認石英了)
            if (item != null && solemn.isButterfly(item)) {
                return item;
            }
        }
        return null;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player) || args.length == 0) return true;
        String target = args[0].toLowerCase();
        if (weaponModules.containsKey(target)) {
            weaponModules.get(target).give(player);
        } else if (List.of("black", "white", "butterflies", "shield").contains(target)) {
            solemn.give(player, target);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(List.of("brush", "black", "white", "butterflies", "shield", "mimicry", "dacapo"));
            return completions.stream().filter(s -> s.startsWith(args[0].toLowerCase())).toList();
        }
        return Collections.emptyList();
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow arrow && arrow.hasMetadata("solemn_arrow") && arrow.getShooter() instanceof Player shooter) {
            solemn.handleArrowHit(event, arrow, shooter);
        }
    }

    @EventHandler
    public void onProjectileHit(org.bukkit.event.entity.ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow && arrow.hasMetadata("solemn_arrow")) {
            if (event.getHitBlock() != null) {
                arrow.getWorld().spawnParticle(Particle.SQUID_INK, arrow.getLocation(), 8, 0.1, 0.1, 0.1, 0.05);
                arrow.remove();
            }
        }
    }

    // --- 修正後的近戰攻擊監聽器 ---
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata("lsmp_custom_damage")) return;
        if (!(event.getDamager() instanceof Player player)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta() || item.getItemMeta().getItemModel() == null) return;

        String model = item.getItemMeta().getItemModel().toString();

        // 修正：用 iterator 檢查所有模組，防止重複呼叫
        for (EGOWeapon ego : weaponModules.values()) {
            if (model.contains(ego.getId()) || (ego.getId().equals("brush") && model.contains("ring_brush"))) {
                ego.handleMelee(event, player);
                break;
            }
        }
    }

    // 1. 處理右鍵生物（環指筆刷連鎖傷害）
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (!(event.getRightClicked() instanceof LivingEntity target)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta() || item.getItemMeta().getItemModel() == null) return;

        String model = item.getItemMeta().getItemModel().toString();

        // 處理環指筆刷
        if (model.contains("ring_brush")) {
            EGOWeapon ego = weaponModules.get("brush");
            if (ego instanceof ringbrush brush) { // 先檢查它是不是筆刷[cite: 12]
                brush.handleInteractEntity(player, target); // 轉型後才能呼叫專屬方法[cite: 12]
            }
        }
    }

    // 2. 補回消失的輔助判斷方法 (放在類別內部即可)
    private boolean isNormalWeapon(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        // 判斷是否為普通弓或弩，且不是莊嚴哀悼
        return (type == Material.CROSSBOW || type == Material.BOW) && !solemn.isSolemnLament(item);
    }

    // 3. 處理右鍵空氣/方塊（莊嚴哀悼防呆）
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        // 防止莊嚴哀悼子彈誤用
        if (solemn.isButterfly(offHand) && isNormalWeapon(mainHand)) event.setCancelled(true);
        else if (solemn.isButterfly(mainHand) && isNormalWeapon(offHand)) event.setCancelled(true);
    }
}
