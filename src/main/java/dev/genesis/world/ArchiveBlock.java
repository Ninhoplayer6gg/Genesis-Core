package dev.genesis.world;
import dev.genesis.core.GenesisCoreData;
import dev.genesis.network.CoreNetwork;
import dev.genesis.registry.CoreRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
public final class ArchiveBlock extends Block {
    public ArchiveBlock(Properties p){super(p);}
    @Override public InteractionResult use(BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit) {
        if(player instanceof ServerPlayer p) {
            var d=GenesisCoreData.get(p);boolean fresh=d.state.discover("structure:oriel");
            p.sendSystemMessage(Component.literal("[Arquivo Oriel] Somos os Aevorim. A paz exige experimentar a existência alheia. Registro 7: quarenta e duas matrizes respondem de mundos que nunca existiram. Não abram a Câmara de Retorno."));
            if(fresh){d.state.addXp(50);p.getInventory().placeItemBackInInventory(new ItemStack(CoreRegistry.MEMORY_FRAGMENT.get()));CoreNetwork.syncOwner(p);}
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
