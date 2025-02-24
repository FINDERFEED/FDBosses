package com.finderfeed.fdbosses.client.boss_screen.text_block;

import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.InteractionBox;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TextBlock implements GuiEventListener, Renderable, NarratableEntry {

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

        InteractionBox box = this.getHoverOverInteractionBoxUnderMouse(mx,my);
        if (box != null){
            box.interaction.getOnHover().onHoverOver(this,graphics,mx,my);
        }

    }

    public InteractionBox getHoverOverInteractionBoxUnderMouse(float mx,float my){
        return this.getInteractionBoxUnderMouse(mx,my,(box)->box.interaction.isHoverOver());
    }

    public InteractionBox getClickInteractionBoxUnderMouse(float mx,float my){
        return this.getInteractionBoxUnderMouse(mx,my,(box)->box.interaction.isClick());
    }

    public InteractionBox getScrollInteractionBoxUnderMouse(float mx,float my){
        return this.getInteractionBoxUnderMouse(mx,my,(box)->box.interaction.isScroll());
    }

    public InteractionBox getInteractionBoxUnderMouse(float mx, float my, Predicate<InteractionBox> boxPredicate){
        for (InteractionBox box : interactionBoxes){
            if (box.isMouseInside(mx,my) && boxPredicate.test(box)){
                return box;
            }
        }
        return null;
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
    public boolean mouseClicked(double mx, double my, int button) {

        InteractionBox box = this.getClickInteractionBoxUnderMouse((float)mx,(float)my);
        if (box != null){
            box.interaction.getOnClick().click(this,(float)mx,(float)my,button);
        }

        return GuiEventListener.super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {

        InteractionBox box = this.getScrollInteractionBoxUnderMouse((float)mx,(float)my);
        if (box != null){
            box.interaction.getOnScroll().scroll(this,(float)mx,(float)my,(float)scrollX,(float)scrollY);
        }

        return GuiEventListener.super.mouseScrolled(mx, my, scrollX, scrollY);
    }

    @Override
    public void setFocused(boolean state) {
        isFocused = state;
    }

    @Override
    public boolean isFocused() {
        return isFocused;
    }


    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {

    }
}
