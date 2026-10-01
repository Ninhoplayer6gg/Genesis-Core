package dev.genesis;
import com.mojang.logging.LogUtils;
import dev.genesis.config.CoreConfig;
import dev.genesis.core.GenesisCoreData;
import dev.genesis.network.CoreNetwork;
import dev.genesis.registry.CoreRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
@Mod(GenesisCore.MOD_ID)
public final class GenesisCore {
    public static final String MOD_ID="genesis";
    public static final Logger LOG=LogUtils.getLogger();
    public static ResourceLocation id(String path){return new ResourceLocation(MOD_ID,path);}
    public GenesisCore() {
        var bus=FMLJavaModLoadingContext.get().getModEventBus();
        CoreRegistry.register(bus);bus.addListener((RegisterCapabilitiesEvent e)->e.register(GenesisCoreData.class));
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,CoreConfig.SPEC);
        CoreNetwork.register();
    }
}
