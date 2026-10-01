package dev.genesis.client;
import dev.genesis.GenesisCore;
import dev.genesis.species.SpeciesRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=GenesisCore.MOD_ID,value=Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
public final class CoreHud {
    @SubscribeEvent public static void register(RegisterGuiOverlaysEvent e){e.registerAboveAll("core",(gui,g,pt,w,h)->{
        var mc=Minecraft.getInstance();if(mc.player==null||mc.options.hideGui)return;
        var d=ClientState.local();var s=d.state;if(!s.installed)return;var species=SpeciesRegistry.get(s.form);
        int x=8,y=8;g.fill(x-3,y-3,x+188,y+45,0xa9090c13);
        g.drawString(mc.font,species==null?"NÚCLEO DO GÊNESIS":species.name()+(s.absolute?" • ABSOLUTO":""),x,y,0xffe5bb69);
        g.drawString(mc.font,"Tier "+s.tier()+" • Lv "+s.level()+" • XP "+s.xp,x,y+12,0xffb9c2cd);
        g.fill(x,y+26,x+174,y+31,0xff40242c);g.fill(x,y+26,x+(int)(174*s.energy/s.maxEnergy()),y+31,0xffe84751);
        g.drawString(mc.font,(int)s.energy+" / "+(int)s.maxEnergy()+" • "+(s.transformationCooldown>0?"CD "+(s.transformationCooldown+19)/20+"s":"PRONTO"),x,y+34,0xffe4e6ef);
        if(species!=null)for(int i=0;i<5;i++)g.drawString(mc.font,CoreKeys.ABILITIES[i].getTranslatedKeyMessage().getString()+"  "+species.abilities().get(i)+(s.cooldowns[i]>0?" ["+(s.cooldowns[i]+19)/20+"s]":""),x,y+51+i*12,s.cooldowns[i]>0?0xff858898:0xffd2d8e2);
        else g.drawString(mc.font,CoreKeys.MENU.getTranslatedKeyMessage().getString()+" Menu • "+CoreKeys.ABILITIES[0].getTranslatedKeyMessage().getString()+" Scanner",x,y+51,0xffd2d8e2);
        int line=0;for(var a:s.adaptations.entrySet())g.drawString(mc.font,a.getKey()+" • "+a.getValue().intValue()+"%",x,y+115+line++*12,0xff9fd1b8);
    });}
}
