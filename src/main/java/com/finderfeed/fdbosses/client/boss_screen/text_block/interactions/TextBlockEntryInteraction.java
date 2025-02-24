package com.finderfeed.fdbosses.client.boss_screen.text_block.interactions;

public class TextBlockEntryInteraction {

    private TextBlockClick onClick;
    private TextBlockScroll onScroll;
    private TextBlockHover onHover;

    public TextBlockEntryInteraction(TextBlockClick onClick, TextBlockScroll onScroll, TextBlockHover onHover) {
        this.onClick = onClick;
        this.onScroll = onScroll;
        this.onHover = onHover;
    }

    public static TextBlockEntryInteraction click(TextBlockClick onClick){
        return new TextBlockEntryInteraction(onClick, null, null);
    }

    public static TextBlockEntryInteraction hoverOver(TextBlockHover onHover){
        return new TextBlockEntryInteraction(null,null, onHover);
    }

    public static TextBlockEntryInteraction scroll(TextBlockScroll scroll){
        return new TextBlockEntryInteraction(null,scroll,null);
    }

    public static TextBlockEntryInteraction empty(){
        return new TextBlockEntryInteraction(null,null,null);
    }

    public TextBlockClick getOnClick() {
        return onClick;
    }

    public TextBlockHover getOnHover() {
        return onHover;
    }

    public TextBlockScroll getOnScroll() {
        return onScroll;
    }

    public boolean isClick(){
        return onClick != null;
    }

    public boolean isHoverOver(){
        return onHover != null;
    }

    public boolean isScroll(){
        return onScroll != null;
    }
}
