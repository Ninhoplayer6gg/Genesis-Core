package dev.genesis.registry;
import dev.genesis.GenesisCore;
import dev.genesis.items.*;
import dev.genesis.world.ArchiveBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
public final class CoreRegistry {
    public static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,GenesisCore.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS=DeferredRegister.create(ForgeRegistries.BLOCKS,GenesisCore.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(Registries.CREATIVE_MODE_TAB,GenesisCore.MOD_ID);
    public static final RegistryObject<Item> CORE=ITEMS.register("genesis_core",()->new GenesisCoreItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> RESONANCE_SHARD=item("resonance_shard");
    public static final RegistryObject<Item> RESONANT_ALLOY=item("resonant_alloy");
    public static final RegistryObject<Item> MEMORY_FRAGMENT=item("memory_fragment");
    public static final RegistryObject<Item> MATRIX_VESSEL=item("matrix_vessel");
    public static final RegistryObject<Item> ABSOLUTE_MODULE=ITEMS.register("absolute_module",()->new AbsoluteModuleItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Block> ORE=BLOCKS.register("resonance_ore",()->new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_REDSTONE_ORE).lightLevel(s->3),UniformInt.of(2,5)));
    public static final RegistryObject<Block> ARCHIVE=BLOCKS.register("mnemonic_archive",()->new ArchiveBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN).strength(20,1200).lightLevel(s->6)));
    static {
        ITEMS.register("resonance_ore",()->new BlockItem(ORE.get(),new Item.Properties()));
        ITEMS.register("mnemonic_archive",()->new BlockItem(ARCHIVE.get(),new Item.Properties()));
        TABS.register("genesis",()->CreativeModeTab.builder().title(Component.literal("Genesis Core")).icon(()->new ItemStack(CORE.get())).displayItems((p,out)->ITEMS.getEntries().forEach(i->out.accept(i.get()))).build());
    }
    private static RegistryObject<Item> item(String name){return ITEMS.register(name,()->new Item(new Item.Properties()));}
    public static void register(IEventBus bus){ITEMS.register(bus);BLOCKS.register(bus);TABS.register(bus);}
}
