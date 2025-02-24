package com.finderfeed.fdbosses.client.boss_screen.text_block.interactions;

public class Interaction {

    private OnClick onClick;
    private OnScroll onScroll;
    private OnHover onHover;

    public Interaction(OnClick onClick, OnScroll onScroll, OnHover onHover) {
        this.onClick = onClick;
        this.onScroll = onScroll;
        this.onHover = onHover;
    }

    public static Interaction click(OnClick onClick){
        return new Interaction(onClick, null, null);
    }

    public static Interaction hoverOver(OnHover onHover){
        return new Interaction(null,null, onHover);
    }

    public static Interaction scroll(OnScroll scroll){
        return new Interaction(null,scroll,null);
    }

    public static Interaction empty(){
        return new Interaction(null,null,null);
    }

    public OnClick getOnClick() {
        return onClick;
    }

    public OnHover getOnHover() {
        return onHover;
    }

    public OnScroll getOnScroll() {
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
