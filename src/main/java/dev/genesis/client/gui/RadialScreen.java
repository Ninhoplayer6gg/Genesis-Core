package dev.genesis.client.gui;
import dev.genesis.client.*;
import dev.genesis.species.*;
import dev.genesis.network.CoreNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import dev.genesis.GenesisCore;
public final class RadialScreen extends Screen {
    private int selected;private boolean absolute;
    public RadialScreen(){super(Component.literal("Núcleo do Gênesis"));}
    @Override protected void init() {
        int y=height-30;
        addRenderableWidget(Button.builder(Component.literal("Humano"),b->{CoreNetwork.input(0,-1,false);onClose();}).bounds(width/2-158,y, 70,20).build());
        addRenderableWidget(Button.builder(Component.literal("Escanear"),b->{onClose();CoreNetwork.input(2,0,false);}).bounds(width/2-82,y,76,20).build());
        addRenderableWidget(Button.builder(Component.literal("Database"),b->minecraft.setScreen(new DatabaseScreen())).bounds(width/2,y,78,20).build());
        addRenderableWidget(Button.builder(Component.literal("Cores"),b->CoreNetwork.input(3,(ClientState.local().state.preset+1)%3,false)).bounds(width/2+84,y,70,20).build());
        addRenderableWidget(Button.builder(Component.literal("Forma Absoluta"),b->{if(ClientState.local().state.absoluteUnlocked)absolute=!absolute;}).bounds(width-126,34,120,18).build());
    }
    @Override public boolean isPauseScreen(){return false;}
    @Override public void render(GuiGraphics g,int mx,int my,float partial) {
        var s=ClientState.local().state;int accent=s.preset==1?0xff55bbff:s.preset==2?0xffbb66ef:0xffed3d42;
        g.fill(0,0,width,height,0xc5080b12);g.drawCenteredString(font,"GENESIS CORE • MATRIZES VIVAS",width/2,12,0xffd9b469);
        if(!s.installed){g.drawCenteredString(font,"Ative o item Núcleo do Gênesis para iniciar o vínculo.",width/2,height/2,0xffffffff);super.render(g,mx,my,partial);return;}
        int cx=width/2,cy=height/2-3,r=Math.min(95,Math.max(48,(height-110)/2));
        double spin=System.nanoTime()/1e10;
        for(int j=0;j<80;j++){double a=j*Math.PI/40+spin;int x=cx+(int)(Math.cos(a)*(r+16)),y=cy+(int)(Math.sin(a)*(r+16));g.fill(x,y,x+2,y+2,j%4==0?0xffd9b469:accent);}
        var forms=SpeciesRegistry.playable();
        for(int i=0;i<forms.size();i++) {
            double a=-Math.PI/2+i*Math.PI*2/forms.size();int x=cx+(int)(Math.cos(a)*r),y=cy+(int)(Math.sin(a)*r);
            if(Math.abs(mx-x)<40&&Math.abs(my-y)<24)selected=i;
            boolean unlocked=s.unlocked.contains(forms.get(i).id());int col=unlocked?accent:0xff424652;
            g.fill(x-40,y-24,x+40,y+24,i==selected?0xee27222b:0xd90b1018);g.renderOutline(x-40,y-24,80,48,col);
            g.blit(GenesisCore.id("textures/gui/"+forms.get(i).id()+".png"),x-10,y-21,0,0,20,20,20,20);
            g.drawCenteredString(font,forms.get(i).name(),x,y+2,unlocked?0xfff2e8d9:0xff9a9a9a);
            g.drawCenteredString(font,unlocked?"SELECIONAR":"NÍVEL "+forms.get(i).level(),x,y+13,col);
        }
        var chosen=forms.get(selected);g.drawCenteredString(font,chosen.name()+(absolute&&selected==0?" ∞":""),cx,cy-9,0xffefd297);
        g.drawCenteredString(font,(int)s.energy+" / "+(int)s.maxEnergy(),cx,cy+4,accent);
        g.drawCenteredString(font,"Lv "+s.level()+" • Tier "+s.tier(),cx,cy+17,0xffaeb5c1);
        String info=s.unlocked.contains(chosen.id())?chosen.speciesName():"Requer nível "+chosen.level()+" + scan de "+(selected==1?"metal":"slime");
        g.drawCenteredString(font,info,cx,height-61,0xffd9b469);
        g.drawCenteredString(font,s.transformationCooldown>0?"Estabilizando: "+(s.transformationCooldown+19)/20+"s":"Clique em uma matriz • Esc fecha",cx,height-48,0xffb6bfcd);
        if(width>=580){int tx=width-177,ty=height/2-35;g.fill(tx-6,ty-8,width-8,ty+74,0xee101622);for(int i=0;i<chosen.abilities().size();i++)g.drawString(font,CoreKeys.ABILITIES[i].getTranslatedKeyMessage().getString()+" "+chosen.abilities().get(i),tx,ty+i*13,0xffd8c4a8);}
        super.render(g,mx,my,partial);
    }
    @Override public boolean mouseClicked(double mx,double my,int button) {
        if(super.mouseClicked(mx,my,button))return true;
        if(button!=0)return false;
        int r=Math.min(95,Math.max(48,(height-110)/2)),cx=width/2,cy=height/2-3;
        for(int i=0;i<3;i++){double a=-Math.PI/2+i*Math.PI*2/3;double x=cx+Math.cos(a)*r,y=cy+Math.sin(a)*r;
            if(Math.abs(mx-x)<40&&Math.abs(my-y)<24&&ClientState.local().state.unlocked.contains(SpeciesRegistry.playable().get(i).id())){CoreNetwork.input(0,i,absolute&&i==0);onClose();return true;}}
        return false;
    }
}
