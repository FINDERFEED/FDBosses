package com.finderfeed.fdbosses.client.boss_screen.text_block;

import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.Interaction;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.InteractionBox;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.OnHover;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.ArrayList;
import java.util.List;

public class TextBlock implements GuiEventListener, Renderable {

    public float x;
    public float y;
    public float width;
    public float height;

    public boolean isFocused;

    private List<TextBlockEntry> textBlockEntries = new ArrayList<>();

    private List<InteractionBox> interactionBoxes = new ArrayList<>();

    public TextBlock(float x,float y,float width,float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float pticks) {
        TextBlockCursor cursor = new TextBlockCursor(x,y);

        FDRenderUtil.fill(graphics.pose(),x,y,width,height,1f,1f,1f,0.25f);

        this.clearInteractions();

        for (TextBlockEntry entry : textBlockEntries){
            entry.render(graphics,this,cursor,mx,my,pticks);
        }

        for (InteractionBox box : interactionBoxes){
            if (box.isMouseInside(mx,my) && box.interaction.isHoverOver()){
                OnHover onHover = box.interaction.getOnHover();
                onHover.onHoverOver(graphics,mx,my);
                break;
            }
        }

    }

    private void clearInteractions(){
        this.interactionBoxes.clear();
    }

    public void addInteractionBox(InteractionBox box){
        this.interactionBoxes.add(box);
    }

    public TextBlock addTextBlockEntry(TextBlockEntry entry){
        this.textBlockEntries.add(entry);
        return this;
    }

    public List<TextBlockEntry> getTextBlockEntries() {
        return textBlockEntries;
    }

    public float getBorderX(){
        return x + width;
    }

    @Override
    public void setFocused(boolean state) {
        isFocused = state;
    }

    @Override
    public boolean isFocused() {
        return isFocused;
    }


}
