package me.yisang.limbusweapons.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import me.yisang.limbusweapons.item.ModItems;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

public class EgoCommand {

    private static final Map<String, ItemStack> ITEMS = Map.of(
            "black",       new ItemStack(ModItems.SOLEMN_LAMENT_BLACK),
            "white",       new ItemStack(ModItems.SOLEMN_LAMENT_WHITE),
            "butterflies", new ItemStack(ModItems.BUTTERFLY_QUARTZ, 16),
            "shield",      new ItemStack(ModItems.SOLEMN_SHIELD),
            "mimicry",     new ItemStack(ModItems.MIMICRY),
            "dacapo",      new ItemStack(ModItems.DACAPO),
            "brush",       new ItemStack(ModItems.RING_BRUSH)
    );

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(
                CommandManager.literal("getego")
                    .requires(src -> src.hasPermissionLevel(2))
                    .then(CommandManager.argument("item", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            ITEMS.keySet().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "item");
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

                            ItemStack stack = ITEMS.get(name.toLowerCase());
                            if (stack == null) {
                                player.sendMessage(Text.literal("§c未知武器：" + name), false);
                                return 0;
                            }
                            player.getInventory().insertStack(stack.copy());
                            player.sendMessage(Text.literal("§a已給予 " + name), false);
                            return 1;
                        }))
            )
        );
    }
}
