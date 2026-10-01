package dev.genesis.core;

import dev.genesis.GenesisCore;
import dev.genesis.network.CoreNetwork;
import dev.genesis.transformation.TransformationManager;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Self-service diagnostics; the test kit requires operator permission. */
@Mod.EventBusSubscriber(modid = GenesisCore.MOD_ID)
public final class CoreCommands {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("genesis")
            .then(Commands.literal("status").executes(context -> {
                var player = context.getSource().getPlayerOrException();
                var s = GenesisCoreData.get(player).state;
                context.getSource().sendSuccess(() -> Component.literal(s.installed
                    ? "Genesis • Tier " + s.tier() + " • Nível " + s.level() + " • XP " + s.xp
                        + " • Energia " + (int) s.energy + "/" + (int) s.maxEnergy()
                        + " • " + (s.transformed() ? s.form : "Humano")
                        + " • Matrizes: " + String.join(", ", s.unlocked)
                    : "Core não instalado. Ative o item genesis:genesis_core."), false);
                return 1;
            }))
            .then(Commands.literal("testkit").requires(source -> source.hasPermission(2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    TransformationManager.revert(player);
                    var data = GenesisCoreData.get(player);
                    var s = data.state;
                    if (!s.installed) s.install();
                    s.xp = Math.max(s.xp, 900);
                    s.discover("metal");
                    s.discover("colony");
                    s.unlockAvailable();
                    s.absoluteUnlocked = true;
                    s.upgrades.add("absolute_reticulum");
                    s.energy = s.maxEnergy();
                    s.transformationCooldown = s.abilityLock = 0;
                    java.util.Arrays.fill(s.cooldowns, 0);
                    player.getInventory().add(new ItemStack(Items.IRON_NUGGET, 64));
                    CoreNetwork.sync(player);
                    context.getSource().sendSuccess(() -> Component.literal(
                        "Kit de teste: Core instalado, três matrizes, Adaptaris Absoluto e pepitas. Abra o radial pela tecla configurada (R por padrão)."), false);
                    return 1;
                })));
    }
}
