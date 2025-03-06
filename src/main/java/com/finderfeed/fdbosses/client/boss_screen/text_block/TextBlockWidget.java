package com.finderfeed.fdbosses.client.boss_screen.text_block;

import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.InteractionBox;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser.TextBlockParser;
import com.finderfeed.fdlib.systems.simple_screen.FDScrollableWidget;
import com.finderfeed.fdlib.systems.simple_screen.FDWidget;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class TextBlockWidget extends FDScrollableWidget {

    private List<TextBlockEntry> textBlockEntries = new ArrayList<>();

    private List<InteractionBox> interactionBoxes = new ArrayList<>();

    private float maxScroll;

    private int textColor = 0xffffff;

    public TextBlockWidget(Screen owner, float x, float y, float width, float height){
        super(owner,x,y,width,height);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, float mx, float my, float pticks) {
        TextBlockCursor cursor = new TextBlockCursor(this.getX(),this.getY() - this.getCurrentScroll());

//        FDRenderUtil.fill(graphics.pose(),this.getX(),this.getY(),this.getWidth(),this.getHeight(),1f,1f,1f,0.25f);

        this.clearInteractions();

        FDRenderUtil.Scissor.pushScissors(this.getX(),this.getY(),this.getWidth(),this.getHeight());
        for (int i = 0; i < textBlockEntries.size();i++){
            var entry = this.textBlockEntries.get(i);
            boolean last = i == textBlockEntries.size() - 1;
            entry.render(graphics,this,cursor,mx,my,pticks,last);
        }
        FDRenderUtil.Scissor.popScissors();

        maxScroll = Math.max(0,Math.max(maxScroll,cursor.y - this.getY() - this.getHeight()));


        if (maxScroll != 0){

            float r = ((this.textColor & 0xff0000) >> 16) / 255f;
            float g = ((this.textColor & 0x00ff00) >> 8) / 255f;
            float b = ((this.textColor & 0x0000ff)) / 255f;

            FDRenderUtil.renderScrollBar(graphics.pose(),this.getX() + this.getWidth() + 1, this.getY(),
                    2,this.getHeight(), this.getCurrentScroll(), maxScroll,
                    Math.max(r - 0.2f,0), Math.max(g - 0.2f,0), Math.max(b - 0.2f,0), 1f,
                    Math.min(r + 0.1f,1), Math.min(g + 0.1f,1), Math.min(b + 0.1f,1), 1f
            );
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
        if (!FDRenderUtil.isMouseInBounds(mx,my,this.getX(),this.getY(),this.getWidth(),this.getHeight())) return null;
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
        this.setCurrentScroll(0);
        this.maxScroll = 0;
    }

    public TextBlockWidget addTextBlockEntry(TextBlockEntry entry){
        this.textBlockEntries.add(entry);
        this.setCurrentScroll(0);
        this.maxScroll = 0;
        return this;
    }

    public TextBlockWidget addTextBlockEntries(Collection<TextBlockEntry> entry){
        this.textBlockEntries.addAll(entry);
        this.setCurrentScroll(0);
        this.maxScroll = 0;
        return this;
    }

    public void setText(Component component, float textScale, int color, boolean renderShadow){
        this.clearInteractions();
        this.textBlockEntries = TextBlockParser.parseComponent(component,textScale,renderShadow,color);
        this.setCurrentScroll(0);
        this.maxScroll = 0;
        this.textColor = color;
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
        }else{
            return super.onMouseScroll(mx,my,scrollX,scrollY);
        }
        return true;
    }

    @Override
    public float getMaxScroll() {
        return maxScroll;
    }

    @Override
    public void onScroll(float v) {

    }

    @Override
    public float scrollAmount() {
        return 4;
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
