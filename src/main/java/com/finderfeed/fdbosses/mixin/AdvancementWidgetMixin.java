package com.finderfeed.fdbosses.mixin;


import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(AdvancementWidget.class)
public class AdvancementWidgetMixin {

    @Mutable
    @Shadow @Final private List<FormattedCharSequence> description;

    @Shadow @Nullable private AdvancementProgress progress;
    @Shadow @Final private AdvancementTab tab;
    private static List<FormattedCharSequence> sequences = new ArrayList<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(AdvancementTab p_97255_, Minecraft p_97256_, Advancement p_97257_, DisplayInfo p_97258_, CallbackInfo ci){

        if (this.tab.getTitle() == null) return;

        String title = this.tab.getTitle().getString();
        if (title.equals("Qliphoth Awakening")) {
            this.description = new ArrayList<>(this.description);
        }

    }

    @Inject(method = "drawHover", at = @At("HEAD"))
    public void drawHover(GuiGraphics p_283068_, int p_281304_, int p_281253_, float p_281848_, int p_282097_, int p_281537_, CallbackInfo ci){

        if (this.tab.getTitle() == null) return;

        String title = this.tab.getTitle().getString();

        if (title.equals("Qliphoth Awakening")) {
            float progress;
            if (this.progress == null) {
                progress = 0;
            } else {
                progress = this.progress.getPercent();
            }

            if (progress < 1) {
                sequences.addAll(description);
                description.clear();
                description.add(Language.getInstance().getVisualOrder(Component.literal("???")));
            }
        }

    }

    @Inject(method = "drawHover", at = @At("TAIL"))
    public void drawHoverPost(GuiGraphics p_283068_, int p_281304_, int p_281253_, float p_281848_, int p_282097_, int p_281537_, CallbackInfo ci){

        if (this.tab.getTitle() == null) return;

        String title = this.tab.getTitle().getString();

        if (title.equals("Qliphoth Awakening")) {

            float progress;
            if (this.progress == null) {
                progress = 0;
            } else {
                progress = this.progress.getPercent();
            }
            if (progress < 1) {
                this.description.clear();
                this.description.addAll(sequences);
                sequences.clear();
            }
        }
    }

}
