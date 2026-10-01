package dev.genesis.client.gui;
import dev.genesis.client.ClientState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.*;
public final class DatabaseScreen extends Screen {
    private int page;
    public DatabaseScreen(){super(Component.literal("Database Genesis"));}
    @Override protected void init(){addRenderableWidget(Button.builder(Component.literal("Voltar"),b->minecraft.setScreen(new RadialScreen())).bounds(width/2-45,height-28,90,20).build());}
    @Override public boolean isPauseScreen(){return false;}
    @Override public boolean mouseScrolled(double x,double y,double delta){page=Math.max(0,page-(int)delta);return true;}
    @Override public void render(GuiGraphics g,int mx,int my,float pt){
        g.fill(0,0,width,height,0xf0090d15);var d=ClientState.local();var s=d.state;
        g.drawCenteredString(font,"DATABASE • ARQUIVO DE EXPERIÊNCIAS",width/2,15,0xffd9b469);
        g.drawString(font,"Diagnóstico: "+(s.installed?"vínculo íntegro":"Core ausente")+" | Nível "+s.level()+" | XP "+s.xp,20,37,0xffe8e9ef);
        g.drawString(font,"Matrizes "+s.unlocked.size()+"/3 • Energia "+(int)s.energy+" • Absoluto "+(s.absoluteUnlocked?"liberado":"nível 6 + módulo"),20,51,0xffb6becd);
        var list=new ArrayList<>(s.discoveries);int count=Math.max(1,(height-108)/13);page=Math.min(page,Math.max(0,list.size()-count));
        for(int i=page;i<Math.min(list.size(),page+count);i++)g.drawString(font,"▸ "+translate(list.get(i)),22,76+(i-page)*13,0xffce8187);
        if(list.isEmpty())g.drawString(font,"Na forma humana, use a habilidade 1 para escanear.",22,77,0xffd0d0d0);
        super.render(g,mx,my,pt);
    }
    private String translate(String key){String[] parts=key.split(":",3);if(parts.length==3&&(parts[0].equals("block")||parts[0].equals("entity")||parts[0].equals("biome")))return Component.translatable(parts[0]+"."+parts[1]+"."+parts[2]).getString();return key;}
}
