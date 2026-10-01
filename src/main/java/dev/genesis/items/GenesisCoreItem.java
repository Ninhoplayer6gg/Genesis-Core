package dev.genesis.items;
import dev.genesis.core.GenesisCoreData;
import dev.genesis.network.CoreNetwork;
import dev.genesis.transformation.TransformationManager;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
public final class GenesisCoreItem extends Item {
    public GenesisCoreItem(Properties p){super(p);}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand) {
        ItemStack stack=player.getItemInHand(hand);
        if(player instanceof ServerPlayer p) {
            var d=GenesisCoreData.get(p);
            if(d.state.installed){TransformationManager.message(p,"O Core já está vinculado. Abra o menu pela tecla configurada.");return InteractionResultHolder.fail(stack);}
            d.state.install();if(!p.isCreative())stack.shrink(1);
            TransformationManager.pulse(p);CoreNetwork.sync(p);
            TransformationManager.message(p,"Vínculo permanente iniciado. Adaptaris disponível.");
        }
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
}
