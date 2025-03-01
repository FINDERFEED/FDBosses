package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries.image_entry;

import net.minecraft.resources.ResourceLocation;

public class ImageInText {

    public ResourceLocation location;
    public float u0;
    public float v0;
    public float u1;
    public float v1;
    public float width;
    public float height;

    public ImageInText(ResourceLocation location, float u0, float v0, float u1, float v1,float width,float height) {
        this.location = location;
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
        this.width = width;
        this.height = height;
    }
}
