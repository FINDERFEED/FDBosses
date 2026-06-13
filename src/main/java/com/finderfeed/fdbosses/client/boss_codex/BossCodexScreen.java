package com.finderfeed.fdbosses.client.boss_codex;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerStartFight;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.systems.simple_screen.SimpleFDScreen;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.FDButton;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.util.FDButtonTextures;
import com.finderfeed.fdlib.systems.simple_screen.fdwidgets.util.WidgetTexture;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BossCodexScreen extends SimpleFDScreen {

    //484 * 683
    public static final ResourceLocation TREE = FDBosses.location("textures/gui/tree_of_life.png");
    public static final ResourceLocation NAMES = FDBosses.location("textures/gui/names.png");

    public float scaleProgress = 1;

    public float offsetX = 0;
    public float offsetY = 0;

    private List<LineBetweenStars> lines = new ArrayList<>();

    public BossCodexScreen(){

    }

    @Override
    protected void init() {
        super.init();

        var window = Minecraft.getInstance().getWindow();
        float aspectRatio = (float) window.getHeight() / window.getWidth();

        this.width = 400;
        this.height = (int) (aspectRatio * this.width);
        scaleProgress = 0.235f;
        this.offsetY = 40;

        Random random = new Random();


        float sideOffset = 100;

        StarButton starMalkuth = new StarButton(this, 0,200, 24,24, random.nextInt(6), 0);
        StarButton starYesod = new StarButton(this, 0,100, 24,24, random.nextInt(6), -30);
        StarButton starHod = new StarButton(this, -sideOffset,30, 24,24, random.nextInt(6), 23);
        StarButton starNetzach = new StarButton(this, sideOffset,30, 24,24, random.nextInt(6), -12);
        StarButton starTiphereth = new StarButton(this, 0,-29, 24,24, random.nextInt(6), 40);
        StarButton starGeburah = new StarButton(this, -sideOffset,-95, 24,24, random.nextInt(6), -10);
        StarButton starChesed = new StarButton(this, sideOffset,-95, 24,24, random.nextInt(6), 23);
        StarButton starBinah = new StarButton(this, -sideOffset,-220, 24,24, random.nextInt(6), -1);
        StarButton starHokma = new StarButton(this, sideOffset,-220, 24,24, random.nextInt(6),23);
        StarButton starKether = new StarButton(this, 0,-280, 24,24, random.nextInt(6), -23);

        int flashTime = 40;
        LineBetweenStars lineBetweenStars1 = new LineBetweenStars(starMalkuth, starYesod, flashTime, 0);
        LineBetweenStars lineBetweenStars14 = new LineBetweenStars(starMalkuth, starHod, flashTime, 31);
        LineBetweenStars lineBetweenStars15 = new LineBetweenStars(starMalkuth, starNetzach, flashTime, 17);

        LineBetweenStars lineBetweenStars2 = new LineBetweenStars(starYesod, starHod, flashTime, 20);
        LineBetweenStars lineBetweenStars3 = new LineBetweenStars(starYesod, starNetzach, flashTime, 10);
        LineBetweenStars lineBetweenStars12 = new LineBetweenStars(starNetzach, starChesed, flashTime, 29);
        LineBetweenStars lineBetweenStars13 = new LineBetweenStars(starHod, starGeburah, flashTime, 7);
        LineBetweenStars lineBetweenStars4 = new LineBetweenStars(starHod, starTiphereth, flashTime,4);
        LineBetweenStars lineBetweenStars5 = new LineBetweenStars(starNetzach, starTiphereth, flashTime, 30);
        LineBetweenStars lineBetweenStars6 = new LineBetweenStars(starTiphereth, starChesed, flashTime, 15);
        LineBetweenStars lineBetweenStars7 = new LineBetweenStars(starTiphereth, starGeburah, flashTime, 5);
        LineBetweenStars lineBetweenStars8 = new LineBetweenStars(starGeburah, starBinah, flashTime, 19);
        LineBetweenStars lineBetweenStars9 = new LineBetweenStars(starChesed, starHokma, flashTime, 25);
        LineBetweenStars lineBetweenStars10 = new LineBetweenStars(starBinah, starKether, flashTime, 34);
        LineBetweenStars lineBetweenStars11 = new LineBetweenStars(starHokma, starKether, flashTime, 23);

        this.lines.add(lineBetweenStars14);
        this.lines.add(lineBetweenStars15);
        this.lines.add(lineBetweenStars12);
        this.lines.add(lineBetweenStars13);
        this.lines.add(lineBetweenStars1);
        this.lines.add(lineBetweenStars2);
        this.lines.add(lineBetweenStars3);
        this.lines.add(lineBetweenStars4);
        this.lines.add(lineBetweenStars5);
        this.lines.add(lineBetweenStars6);
        this.lines.add(lineBetweenStars7);
        this.lines.add(lineBetweenStars8);
        this.lines.add(lineBetweenStars9);
        this.lines.add(lineBetweenStars10);
        this.lines.add(lineBetweenStars11);

        this.addRenderableWidget(starMalkuth);
        this.addRenderableWidget(starYesod);
        this.addRenderableWidget(starHod);
        this.addRenderableWidget(starNetzach);
        this.addRenderableWidget(starTiphereth);
        this.addRenderableWidget(starGeburah);
        this.addRenderableWidget(starChesed);
        this.addRenderableWidget(starBinah);
        this.addRenderableWidget(starHokma);
        this.addRenderableWidget(starKether);

    }

    @Override
    public void tick() {
        super.tick();
        for (var line : lines) {
            line.tick();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float pticks) {


        var previousProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        var previousSorting = RenderSystem.getVertexSorting();
        Matrix4f matrix4f = new Matrix4f()
                .setOrtho(
                        0.0F,
                        (float)((double)this.width),
                        (float)((double)this.height),
                        0.0F,
                        1000.0F,
                        net.neoforged.neoforge.client.ClientHooks.getGuiFarPlane()
                );
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);


        Window window = Minecraft.getInstance().getWindow();
        FDRenderUtil.fill(graphics.pose(), 0,0,window.getWidth(),window.getHeight(),0,0,0,0.9f);
        this.renderBackground(graphics, mx, my, pticks);

        this.renderTree(graphics, mx, my, pticks);



        RenderSystem.setProjectionMatrix(previousProjection, previousSorting);

    }

    public void renderTree(GuiGraphics graphics, int mx, int my, float pticks) {
        var matrices = graphics.pose();

        matrices.pushPose();

        float rs = this.getRealScale();

        matrices.translate(this.width / 2f, this.height / 2f, 0);
        matrices.scale(rs,rs,rs);
        matrices.translate(offsetX, offsetY,0);

        float treeWidth = 484;
        float treeHeight = 683;
        FDRenderUtil.bindTexture(TREE);
        FDRenderUtil.blitWithBlendCentered(matrices, 0, 0, treeWidth, treeHeight, 0,0,1,1,1,1,0,1f);

        FDRenderUtil.bindTexture(NAMES);
        FDRenderUtil.blitWithBlendCentered(matrices, 6,230, 66, 10,0,0,1,1,1,10,0,1);//MALKUTH
        FDRenderUtil.blitWithBlendCentered(matrices, 14,70, 66, 10,0,1,1,1,1,10,0,1);//YESOD
        FDRenderUtil.blitWithBlendCentered(matrices, -122,10, 66, 10,0,2,1,1,1,10,0,1);//HOD
        FDRenderUtil.blitWithBlendCentered(matrices, 0,-65, 66, 10,0,3,1,1,1,10,0,1);//TIPHERETH
        FDRenderUtil.blitWithBlendCentered(matrices, 155,-120, 66, 10,0,4,1,1,1,10,0,1);//CHESED
        FDRenderUtil.blitWithBlendCentered(matrices, -146,-120, 66, 10,0,5,1,1,1,10,0,1);//GEBURAH
        FDRenderUtil.blitWithBlendCentered(matrices, -136,-240, 66, 10,0,6,1,1,1,10,0,1);//BINAH
        FDRenderUtil.blitWithBlendCentered(matrices, 13,-310, 66, 10,0,7,1,1,1,10,0,1);//KETER
        FDRenderUtil.blitWithBlendCentered(matrices, 155,-240, 66, 10,0,8,1,1,1,10,0,1);//HOKMA
        FDRenderUtil.blitWithBlendCentered(matrices, 155,10, 66, 10,0,9,1,1,1,10,0,1);//NETZACH

        for (var line : lines) {
            line.render(graphics);
        }

        Vector2i mousePos = this.getMousePos();

        for (Renderable renderable : this.renderables) {
            renderable.render(graphics, mousePos.x, mousePos.y, pticks);
        }

        matrices.popPose();
    }

    @Override
    public boolean isMouseOver(double mx, double my) {
        var realPos = this.getMousePos();
        return super.isMouseOver(realPos.x, realPos.y);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double xOffs, double yOffs) {
        scaleProgress = (float) Mth.clamp((scaleProgress + yOffs * 0.035f) ,0.0f, 1f);
        var realPos = this.getMousePos();
        return super.mouseScrolled(realPos.x, realPos.y, xOffs, yOffs);
    }

    public float getRealScale(){
        return FDMathUtil.lerp(0.1f,5f, FDEasings.easeIn(scaleProgress));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        var realPos = this.getMousePos();
        return super.mouseClicked(realPos.x, realPos.y, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double xOffs, double yOffs) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.offsetX += (float) xOffs / this.getRealScale();
            this.offsetY += (float) yOffs / this.getRealScale();
        }
        var realPos = this.getMousePos();
        return super.mouseDragged(realPos.x, realPos.y, button, xOffs, yOffs);
    }

    private Vector2i getMousePos(){

        float px = (float) Minecraft.getInstance().mouseHandler.xpos() / Minecraft.getInstance().getWindow().getWidth();
        float py = (float) Minecraft.getInstance().mouseHandler.ypos() / Minecraft.getInstance().getWindow().getHeight();

        float windowX = px * this.width;
        float windowY = py * this.height;
        float rs = this.getRealScale();

        windowX -= this.width / 2f + offsetX * rs;
        windowY -= this.height / 2f + offsetY * rs;

        windowX /= rs;
        windowY /= rs;

        return new Vector2i((int) windowX, (int) windowY);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        var realPos = this.getMousePos();
        return super.mouseReleased(realPos.x,realPos.y, button);
    }

    @Override
    public void mouseMoved(double mx, double my) {
        var realPos = this.getMousePos();
        super.mouseMoved(realPos.x, realPos.y);
    }

    @Override
    public float getScreenWidth() {
        return 0;
    }

    @Override
    public float getScreenHeight() {
        return 0;
    }
}
