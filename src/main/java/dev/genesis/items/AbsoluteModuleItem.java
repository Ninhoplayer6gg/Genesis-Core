package dev.genesis.items;
import dev.genesis.core.GenesisCoreData;
import dev.genesis.network.CoreNetwork;
import dev.genesis.transformation.TransformationManager;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
public final class AbsoluteModuleItem extends Item {
    public AbsoluteModuleItem(Properties p){super(p);}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand) {
        var stack=player.getItemInHand(hand);
        if(player instanceof ServerPlayer p) {
            var s=GenesisCoreData.get(p).state;
            if(!s.installed||s.level()<6||s.absoluteUnlocked){TransformationManager.message(p,"Requer Core instalado, nível 6 e módulo ainda não aplicado.");return InteractionResultHolder.fail(stack);}
            s.absoluteUnlocked=true;s.upgrades.add("absolute_lattice");if(!p.isCreative())stack.shrink(1);
            TransformationManager.message(p,"Adaptaris Absoluto: Matriz de Contrarresposta desbloqueada.");CoreNetwork.sync(p);
        }
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
}
