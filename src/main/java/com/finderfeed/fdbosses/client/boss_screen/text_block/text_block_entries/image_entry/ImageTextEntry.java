package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries.image_entry;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlock;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockCursor;
import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.InteractionBox;
import com.finderfeed.fdbosses.client.boss_screen.text_block.interactions.TextBlockEntryInteraction;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ImageTextEntry implements TextBlockEntry {

    public ImageInText image;
    public float textScale;
    public TextBlockEntryInteraction interaction;

    public ImageTextEntry(ImageInText imageInText, float textScale, TextBlockEntryInteraction interaction){
        this.image = imageInText;
        this.textScale = textScale;
        this.interaction = interaction;
    }

    public ImageTextEntry(ResourceLocation location, float textScale, TextBlockEntryInteraction interaction){
        this.image = new ImageInText(location,0,0,1,1,1,1);
        this.textScale = textScale;
        this.interaction = interaction;
    }

    @Override
    public void render(GuiGraphics graphics, TextBlock textBlock, TextBlockCursor cursor, float mouseX, float mouseY, float pticks) {

        float widthheight = Minecraft.getInstance().font.lineHeight;

        if (cursor.shouldGoToNextLine(widthheight,textBlock.getBorderX())){
            cursor.nextLine(widthheight);
        }

        float x = cursor.x;
        float y = cursor.y;

        FDRenderUtil.bindTexture(image.location);


        FDRenderUtil.blitWithBlend(graphics.pose(),x,y,widthheight,widthheight,image.u0,image.v0,image.u1 - image.u0,image.v1 - image.v0,1,1,0f,1f);

        float addX = widthheight + textScale;

        textBlock.addInteractionBox(new InteractionBox(x,y,addX,widthheight,interaction));

        cursor.addX(addX);
    }
}
