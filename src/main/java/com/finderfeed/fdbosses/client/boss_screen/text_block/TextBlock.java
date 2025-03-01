package com.finderfeed.fdbosses.client.boss_screen.text_block;

import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.InteractionBox;
import com.finderfeed.fdlib.systems.simple_screen.FDWidget;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TextBlock extends FDWidget {

    private List<TextBlockEntry> textBlockEntries = new ArrayList<>();

    private List<InteractionBox> interactionBoxes = new ArrayList<>();

    public TextBlock(Screen owner,float x, float y, float width, float height){
        super(owner,x,y,width,height);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, float mx, float my, float pticks) {
        TextBlockCursor cursor = new TextBlockCursor(this.getX(),this.getY());

        FDRenderUtil.fill(graphics.pose(),this.getX(),this.getY(),this.getWidth(),this.getHeight(),1f,1f,1f,0.25f);

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

    public void removeTextEntries(){
        this.textBlockEntries.clear();
        this.clearInteractions();
    }

    public TextBlock addTextBlockEntry(TextBlockEntry entry){
        this.textBlockEntries.add(entry);
        return this;
    }

    public TextBlock addTextBlockEntries(Collection<TextBlockEntry> entry){
        this.textBlockEntries.addAll(entry);
        return this;
    }

    public List<TextBlockEntry> getTextBlockEntries() {
        return textBlockEntries;
    }

    public float getBorderX(){
        return this.getX() + this.getWidth();
    }


    @Override
    public boolean onMouseClick(float mx, float my, int key) {
        InteractionBox box = this.getClickInteractionBoxUnderMouse((float)mx,(float)my);
        if (box != null){
            box.interaction.getOnClick().click(this,(float)mx,(float)my,key);
        }
        return true;
    }

    @Override
    public boolean onMouseRelease(float v, float v1, int i) {
        return false;
    }

    @Override
    public boolean onMouseScroll(float mx, float my, float scrollX, float scrollY) {
        InteractionBox box = this.getScrollInteractionBoxUnderMouse((float)mx,(float)my);
        if (box != null){
            box.interaction.getOnScroll().scroll(this,(float)mx,(float)my,(float)scrollX,(float)scrollY);
        }
        return true;
    }

    @Override
    public boolean onCharTyped(char c, int i) {
        return false;
    }

    @Override
    public boolean onKeyPress(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean onKeyRelease(int i, int i1, int i2) {
        return false;
    }

    @Override
    public ScreenRectangle getRectangle() {
        return ScreenRectangle.empty();
    }


}
