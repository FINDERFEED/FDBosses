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

public class BossCodexScreen extends SimpleFDScreen {

    //484 * 683
    public static final ResourceLocation TREE = FDBosses.location("textures/gui/tree_of_life.png");
    public static final ResourceLocation STAR = FDBosses.location("textures/gui/star/star.png");

    public float scaleProgress = 1;

    public float offsetX = 0;
    public float offsetY = 0;

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
        this.offsetY = 50;


        FDButton startFightButton = new FDButton(this,6,6,112,26)
                .setTexture(new FDButtonTextures(
                        new WidgetTexture(FDBosses.location("textures/gui/medium_button.png")),
                        new WidgetTexture(FDBosses.location("textures/gui/medium_button_selected.png"),0,0)
                ))
                .setSound(BossSounds.BUTTON_CLICK.get())
                .setText(Component.translatable("fdbosses.word.start_fight").withStyle(Style.EMPTY.withColor(0xffffff)),
                        110,1f,true,0,1)
                .setOnClickAction(((fdWidget1, v2, v11, i1) -> {
                    System.out.println("Pizdec");
                    return true;
                }));

        this.addRenderableWidget(startFightButton);

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
