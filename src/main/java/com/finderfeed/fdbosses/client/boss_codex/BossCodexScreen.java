package com.finderfeed.fdbosses.client.boss_codex;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.init.BossCoreShaders;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDScreenParticle;
import com.finderfeed.fdlib.systems.screen.screen_particles.ScreenParticleEngine;
import com.finderfeed.fdlib.systems.simple_screen.SimpleFDScreen;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class BossCodexScreen extends SimpleFDScreen {

    public static RenderTarget RENDER_TARGET;

    //484 * 683
    public static final ResourceLocation TREE = FDBosses.location("textures/gui/tree_of_life.png");
    public static final ResourceLocation NAMES = FDBosses.location("textures/gui/names.png");

    // Smooth Scaling
    public float scaleProgressO = 1;
    public float fromScaleProgress = 1;
    public float scaleProgress = 1;
    public float targetScaleProgress = 1;
    public int scaleTime = 0;
    public int currentScaleTime = 0;


    // Smooth position change

    public float offsetXTarget = 0;
    public float offsetYTarget = 0;

    public float offsetXPrev = 0;
    public float offsetYPrev = 0;

    public float offsetX = 0;
    public float offsetY = 0;

    public float offsetXO = 0;
    public float offsetYO = 0;

    public int offsetTime = 0;
    public int currentOffsetTime = 0;
    private boolean blockingOffset;

    public Runnable offsetEndAction;

    private List<LineBetweenStars> lines = new ArrayList<>();

    public ScreenParticleEngine screenParticleEngine;

    public StarButton starMalkuth;
    public StarButton starYesod;
    public StarButton starHod;
    public StarButton starNetzach;
    public StarButton starTiphereth;
    public StarButton starGeburah;
    public StarButton starChesed;
    public StarButton starBinah;
    public StarButton starHokma;
    public StarButton starKether;

    private int time = 0;

    private boolean playOpenAnimation;

    public BossCodexScreen(boolean playOpenAnimation){
        this.playOpenAnimation = playOpenAnimation;
    }

    @Override
    protected void init() {
        super.init();

        var window = Minecraft.getInstance().getWindow();
        float aspectRatio = (float) window.getHeight() / window.getWidth();

        this.width = 400;
        this.height = (int) (aspectRatio * this.width);
        scaleProgress = 0.635f;
        fromScaleProgress = scaleProgress;
        scaleProgressO = scaleProgress;
        targetScaleProgress = scaleProgress;
        this.offsetY = -250;
        this.offsetYPrev = this.offsetY;
        this.offsetYO = this.offsetY;
        this.offsetYTarget = this.offsetY;
        this.offsetTime = 0;
        this.currentOffsetTime = 0;

        this.offsetX = 0;
        this.offsetXO = this.offsetX;
        this.offsetXPrev = this.offsetX;
        this.offsetXTarget = this.offsetX;

        if (playOpenAnimation) {
            this.scaleTo(0.235f, 42);
            this.moveTo(0, 40, 42);
        }else{
            scaleProgress = 0.235f;
            fromScaleProgress = scaleProgress;
            scaleProgressO = scaleProgress;
            targetScaleProgress = scaleProgress;

            this.offsetY = 40;
            this.offsetYPrev = this.offsetY;
            this.offsetYO = this.offsetY;
            this.offsetYTarget = this.offsetY;
        }

        if (RENDER_TARGET == null) {
            RENDER_TARGET = new TextureTarget(
                    window.getWidth(),
                    window.getHeight(),
                    true,
                    Minecraft.ON_OSX
            );
        }else{
            RENDER_TARGET.resize(window.getWidth(), window.getHeight(), Minecraft.ON_OSX);
        }

        this.screenParticleEngine = new ScreenParticleEngine();

        this.lines.clear();


        Random random = new Random();


        float sideOffset = 100;
        float wOffset = -12;
        float hOffset = -12;
        int lineTravelTime = 6;

        starMalkuth = new StarButton(BossEntities.MALKUTH.get(), this, wOffset,200 + hOffset, 24,24, random.nextInt(6), 0,0, 1.3f);
        starYesod = new StarButton(null, this, wOffset,100 + hOffset, 24,24, random.nextInt(6), -30,lineTravelTime, 1.35f);
        starHod = new StarButton(null, this, -sideOffset + wOffset,30 + hOffset, 24,24, random.nextInt(6), 23,lineTravelTime * 2, -1.4f);
        starNetzach = new StarButton(null, this, sideOffset + wOffset,30 + hOffset, 24,24, random.nextInt(6), -12, lineTravelTime * 2, 1.4f);
        starTiphereth = new StarButton(null, this, wOffset,-29 + hOffset, 24,24, random.nextInt(6), 40, lineTravelTime * 3, 1.45f);
        starGeburah = new StarButton(BossEntities.GEBURAH.get(), this, -sideOffset + wOffset,-95 + hOffset, 24,24, random.nextInt(6), -10, lineTravelTime * 4, 1.5f);
        starChesed = new StarButton(BossEntities.CHESED.get(), this, sideOffset + wOffset,-95 + hOffset, 24,24, random.nextInt(6), 23, lineTravelTime * 4, -1.5f);
        starBinah = new StarButton(null, this, -sideOffset + wOffset,-220 + hOffset, 24,24, random.nextInt(6), -1, lineTravelTime * 5, -1.55f);
        starHokma = new StarButton(null, this, sideOffset + wOffset,-220 + hOffset, 24,24, random.nextInt(6),23, lineTravelTime * 5, 1.55f);
        starKether = new StarButton(null, this, wOffset,-280 + hOffset, 24,24, random.nextInt(6), -23, lineTravelTime * 6, 1.6f);

        if (!playOpenAnimation){
            starMalkuth.setActivated();
            starYesod.setActivated();
            starHod.setActivated();
            starNetzach.setActivated();
            starTiphereth.setActivated();
            starGeburah.setActivated();
            starChesed.setActivated();
            starBinah.setActivated();
            starHokma.setActivated();
            starKether.setActivated();
        }

        int flashTime = 40;


        LineBetweenStars lineBetweenStars1 = new LineBetweenStars(starMalkuth, starYesod, flashTime, 0,0,lineTravelTime);
        LineBetweenStars lineBetweenStars14 = new LineBetweenStars(starMalkuth, starHod, flashTime, 31,0,lineTravelTime * 2);
        LineBetweenStars lineBetweenStars15 = new LineBetweenStars(starMalkuth, starNetzach, flashTime, 17,0,lineTravelTime * 2);

        LineBetweenStars lineBetweenStars2 = new LineBetweenStars(starYesod, starHod, flashTime, 20,lineTravelTime,lineTravelTime);
        LineBetweenStars lineBetweenStars3 = new LineBetweenStars(starYesod, starNetzach, flashTime, 10,lineTravelTime,lineTravelTime);

        LineBetweenStars lineBetweenStars12 = new LineBetweenStars(starNetzach, starChesed, flashTime, 29,lineTravelTime * 2,lineTravelTime * 2);
        LineBetweenStars lineBetweenStars13 = new LineBetweenStars(starHod, starGeburah, flashTime, 7, lineTravelTime * 2,lineTravelTime * 2);

        LineBetweenStars lineBetweenStars4 = new LineBetweenStars(starHod, starTiphereth, flashTime,4, lineTravelTime * 2,lineTravelTime);
        LineBetweenStars lineBetweenStars5 = new LineBetweenStars(starNetzach, starTiphereth, flashTime, 30, lineTravelTime * 2,lineTravelTime);

        LineBetweenStars lineBetweenStars6 = new LineBetweenStars(starTiphereth, starChesed, flashTime, 15,lineTravelTime * 3,lineTravelTime);
        LineBetweenStars lineBetweenStars7 = new LineBetweenStars(starTiphereth, starGeburah, flashTime, 5,lineTravelTime * 3,lineTravelTime);

        LineBetweenStars lineBetweenStars8 = new LineBetweenStars(starGeburah, starBinah, flashTime, 19, lineTravelTime * 4,lineTravelTime);
        LineBetweenStars lineBetweenStars9 = new LineBetweenStars(starChesed, starHokma, flashTime, 25, lineTravelTime * 4,lineTravelTime);
        LineBetweenStars lineBetweenStars10 = new LineBetweenStars(starBinah, starKether, flashTime, 34, lineTravelTime * 5,lineTravelTime);
        LineBetweenStars lineBetweenStars11 = new LineBetweenStars(starHokma, starKether, flashTime, 23, lineTravelTime * 5,lineTravelTime);

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

        if (!playOpenAnimation) {
            for (var line : lines) {
                line.setActivated();
            }
        }

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

    private LinkedList<FDScreenParticle<?>> particles = new LinkedList<>();

    @Override
    public void tick() {
        super.tick();

        scaleProgressO = scaleProgress;
        if (scaleTime != 0){
            currentScaleTime = Mth.clamp(currentScaleTime + 1, 0, scaleTime);
            float p = FDEasings.easeOut((float) currentScaleTime / scaleTime);

            this.scaleProgress = FDMathUtil.lerp(fromScaleProgress, targetScaleProgress, p);
        }else{
            scaleProgress = targetScaleProgress;
        }

        this.offsetXO = this.offsetX;
        this.offsetYO = this.offsetY;
        if (offsetTime != 0){
            currentOffsetTime = Mth.clamp(currentOffsetTime + 1, 0, offsetTime);
            float p = FDEasings.easeOut((float) currentOffsetTime / offsetTime);

            if (currentOffsetTime >= offsetTime) {
                blockingOffset = false;
                if (offsetEndAction != null){
                    offsetEndAction.run();
                    offsetEndAction = null;
                }
            }

            this.offsetX = FDMathUtil.lerp(offsetXPrev, offsetXTarget, p);
            this.offsetY = FDMathUtil.lerp(offsetYPrev, offsetYTarget, p);
        }else{
            if (offsetEndAction != null){
                offsetEndAction.run();
                offsetEndAction = null;
            }
            blockingOffset = false;
            offsetX = offsetXTarget;
            offsetY = offsetYTarget;
        }

        particles.removeIf(FDScreenParticle::isRemoved);

        if (time % 4 == 0) {
            var mousePos = this.getMousePos(0);

            var random = Minecraft.getInstance().level.random;

            float speed = random.nextFloat() * 0.5f + 0.5f;
            Vec3 rnd = new Vec3(speed, 0, 0).zRot(random.nextFloat() * FDMathUtil.FPI * 2);

            FlashyColoredQuadParticle flashyColoredQuadParticle = new FlashyColoredQuadParticle()
                    .setPos(mousePos.x, mousePos.y, true)
                    .setColor(1f, 1f, 0.25f + random.nextFloat() * 0.25f, 1f)
                    .setQuadSize(0.5f + random.nextFloat() * 0.25f)
                    .setFlashFrequency(0.75f)
                    .setSpeed(rnd.x, rnd.y)
                    .setFriction(0.95f)
                    .setLifetime(40);

            particles.add(flashyColoredQuadParticle);

            screenParticleEngine.addParticle(flashyColoredQuadParticle);
        }

        time++;
        screenParticleEngine.tick();
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
        FDRenderUtil.fill(graphics.pose(), 0,0,window.getWidth(),window.getHeight(),0,0,0,0.75f);
        this.renderBackground(graphics, mx, my, pticks);


        this.renderTree(graphics, mx, my, pticks);

        RenderSystem.setProjectionMatrix(previousProjection, previousSorting);


    }

    public void renderFramebufferedTree(GuiGraphics graphics, int mx, int my, float pticks) {

        pticks = FDRenderUtil.tryGetPartialTickIgnorePause();

        var matrices = graphics.pose();

        matrices.pushPose();
        RenderSystem.disableCull();

        matrices.scale(1,-1,1);
        matrices.translate(0,-this.height,0);

        RenderSystem.setShader(()-> BossCoreShaders.CODEX_UI);

        float ntime =  0.005f * (time + pticks);
        BossCoreShaders.CODEX_UI.safeGetUniform("time").set(ntime);
        BossCoreShaders.CODEX_UI.safeGetUniform("offsetX").set(-this.getOffsetX(pticks));
        BossCoreShaders.CODEX_UI.safeGetUniform("offsetY").set(this.getOffsetY(pticks));
        BossCoreShaders.CODEX_UI.safeGetUniform("scale").set(this.getRealScale(pticks));
        BossCoreShaders.CODEX_UI.safeGetUniform("screenSize").set((float) this.width, this.height);

        int count = 64;
        float[] positions = new float[count * 2];
        Arrays.fill(positions, 1000000);
        float[] radiuses = new float[count];

        int index = 0;

        for (var renderable : this.renderables){
            if (renderable instanceof StarButton starButton){
                if (starButton.isActivated()){

                    float starTime = starButton.getTick() + FDRenderUtil.tryGetPartialTickIgnorePause();

                    float t = starTime + starButton.getCurrentFrame() * 0.37f;

                    float flicker = 1f - 0.15f * (float)Math.pow(
                            Math.max(0, Math.sin(t * 9f)),
                            8
                    );

                    float radius = 60f * flicker;

                    if (starTime > starButton.getActivationTime()){
                        float p = 1 - Mth.clamp((starTime - starButton.getActivationTime()) / 10, 0, 1);
                        radius = Math.max(radius, 100 * FDEasings.easeIn(p));
                    }


                    positions[index * 2] = starButton.getX();
                    positions[index * 2 + 1] = starButton.getY();
                    radiuses[index] = radius;

                    index++;
                }
            }
        }

        var mousePos = this.getMousePos(pticks);

        positions[index * 2] = mousePos.x;
        positions[index * 2 + 1] = mousePos.y;
        radiuses[index] = 15;
        index++;


        for (int i = particles.size() - 1; i >= 0 && index < count; i--){
            var particle = particles.get(i);
            float p = 1 - (particle.getAge() + FDRenderUtil.tryGetPartialTickIgnorePause()) / particle.getLifetime();
            p = FDEasings.easeOutBack(p);

            positions[index * 2] = (float) particle.getX(FDRenderUtil.tryGetPartialTickIgnorePause());
            positions[index * 2 + 1] = (float) particle.getY(FDRenderUtil.tryGetPartialTickIgnorePause());
            radiuses[index] = p * 3;

            index++;
        }


        BossCoreShaders.CODEX_UI.safeGetUniform("radiuses").set(radiuses);

        BossCoreShaders.CODEX_UI.safeGetUniform("positions").set(positions);

        RenderSystem.setShaderTexture(0, RENDER_TARGET.getColorTextureId());
        RenderSystem.enableBlend();


        var vertex = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        vertex.addVertex(matrices.last().pose(),0,0,0).setUv(0,0).setColor(1,1,1,1f);
        vertex.addVertex(matrices.last().pose(), 0,this.height,0).setUv(0,1).setColor(1,1,1,1f);
        vertex.addVertex(matrices.last().pose(), this.width,this.height,0).setUv(1,1).setColor(1,1,1,1f);
        vertex.addVertex(matrices.last().pose(),this.width, 0,0).setUv(1,0).setColor(1,1,1,1f);

        BufferUploader.drawWithShader(vertex.build());

        RenderSystem.disableBlend();
        matrices.popPose();

    }
    public void renderTree(GuiGraphics graphics, int mx, int my, float pticks) {
        var matrices = graphics.pose();


        matrices.pushPose();

        float rs = this.getRealScale(FDRenderUtil.tryGetPartialTickIgnorePause());


        float offsetX = this.getOffsetX(FDRenderUtil.tryGetPartialTickIgnorePause());
        float offsetY = this.getOffsetY(FDRenderUtil.tryGetPartialTickIgnorePause());

        matrices.translate(this.width / 2f, this.height / 2f, 0);
        matrices.scale(rs,rs,rs);
        matrices.translate(offsetX, offsetY,0);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var main = Minecraft.getInstance().getMainRenderTarget();

        float treeWidth = 484;
        float treeHeight = 683;
        RENDER_TARGET.clear(Minecraft.ON_OSX);
        RENDER_TARGET.bindWrite(true);
        FDRenderUtil.bindTexture(TREE);
        FDRenderUtil.blitWithBlendCentered(matrices, -4.5f, -0.5f, treeWidth, treeHeight, 0,0,1,1,1,1,0,1f);
        main.bindWrite(true);

        matrices.popPose();


        this.renderFramebufferedTree(graphics, mx, my, pticks);


        matrices.pushPose();

        matrices.translate(this.width / 2f, this.height / 2f, 0);
        matrices.scale(rs,rs,rs);
        matrices.translate(offsetX, offsetY,0);

        FDRenderUtil.bindTexture(NAMES);
        if (starMalkuth.isActivated())    this.renderName(matrices, 0,6,230, 0, true);// MALKUTH
        if (starYesod.isActivated())      this.renderName(matrices, FDMathUtil.FPI * 0.574f,14,70, 1, false);// YESOD
        if (starHod.isActivated())      this.renderName(matrices, FDMathUtil.FPI * 0.1574f,-122,10, 2, false);// HOD
        if (starTiphereth.isActivated())      this.renderName(matrices,FDMathUtil.FPI * 0.7574f, 0,-65, 3, false);// TIPHERETH
        if (starChesed.isActivated())      this.renderName(matrices,FDMathUtil.FPI * 0.874f, 155,-120, 4, true);// CHESED
        if (starGeburah.isActivated())      this.renderName(matrices,FDMathUtil.FPI * 0.374f, -146,-120, 5, true);// GEBURAH
        if (starBinah.isActivated())      this.renderName(matrices,FDMathUtil.FPI * 0.274f, -136,-240, 6, false);// BINAH
        if (starKether.isActivated())      this.renderName(matrices,FDMathUtil.FPI * 0.974f, 13,-310, 7, false);// KETER
        if (starHokma.isActivated())      this.renderName(matrices,FDMathUtil.FPI * 0.374f, 155,-240, 8, false);// HOKMA
        if (starNetzach.isActivated())      this.renderName(matrices,FDMathUtil.FPI * 1.174f, 155,10, 9, false);// NETZACH

        for (var line : lines) {
            line.render(graphics);
        }


        Vector2f mousePos = this.getMousePos(FDRenderUtil.tryGetPartialTickIgnorePause());

        screenParticleEngine.render(graphics,FDRenderUtil.tryGetPartialTickIgnorePause());


        matrices.pushPose();
        matrices.translate(mousePos.x,mousePos.y,0);
        matrices.mulPose(Axis.ZP.rotationDegrees(-2 * (time + FDRenderUtil.tryGetPartialTickIgnorePause()) ));
        FDRenderUtil.bindTexture(StarButton.STAR);
        FDRenderUtil.blitWithBlendCentered(matrices,
                0,0,
                16, 16,
                0, time / 2 % 10,
                1, 1,
                1, 11,
                0, 1);

        matrices.popPose();

        for (Renderable renderable : this.renderables) {
            renderable.render(graphics, (int) mousePos.x, (int) mousePos.y, pticks);
        }

        matrices.popPose();
        RenderSystem.disableBlend();

    }

    private void renderName(PoseStack matrices, float offset, float x, float y, float texPosY, boolean opened){

        float t = (time + FDRenderUtil.tryGetPartialTickIgnorePause()) * 0.05f;

        float ampl = 1.5f;
        float xOffs = (float) Math.sin(t + offset) * ampl;
        float yOffs = (float) Math.sin(t * 1.25f + FDMathUtil.FPI * 0.4235f + offset) * ampl;

        FDRenderUtil.blitWithBlendRgb(matrices,x + xOffs - 33, y + yOffs - 5, 66, 10, 0, texPosY, 1,1,1,10,0,1,
                1,
                opened ?  1 : 0.15f,
                opened ?  1 : 0.15f);

//        FDRenderUtil.blitWithBlendCentered(matrices, x + xOffs,y + yOffs,66,10,0,texPosY,1,1,1,10,0,1);

    }

    public float getOffsetX(float partialTicks){
        return FDMathUtil.lerp(offsetXO, offsetX, partialTicks);
    }

    public float getOffsetY(float partialTicks){
        return FDMathUtil.lerp(offsetYO, offsetY, partialTicks);
    }

    public void moveTo(float offsetX, float offsetY, int time){
        this.moveTo(offsetX, offsetY, time, false, null);
    }

    public void moveTo(float offsetX, float offsetY, int time, boolean blocking, Runnable offsetEndAction){
        this.offsetXPrev = this.offsetX;
        this.offsetYPrev = this.offsetY;
        this.offsetXTarget = offsetX;
        this.offsetYTarget = offsetY;
        this.currentOffsetTime = 0;
        this.offsetTime = time;
        this.blockingOffset = blocking;
        if (this.offsetEndAction != null){
            this.offsetEndAction.run();
        }
        this.offsetEndAction = offsetEndAction;
    }

    @Override
    public boolean isMouseOver(double mx, double my) {
        var realPos = this.getMousePos(0);
        return super.isMouseOver(realPos.x, realPos.y);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double xOffs, double yOffs) {

        this.scaleTo((float) (this.getTargetScaleProgress() + yOffs * 0.035f), 2);

        var realPos = this.getMousePos(0);
        return super.mouseScrolled(realPos.x, realPos.y, xOffs, yOffs);
    }

    public void scaleTo(float target, int time){
        target = Mth.clamp(target, 0, 1);
        this.currentScaleTime = 0;
        this.fromScaleProgress = scaleProgress;
        this.scaleTime = time;
        this.targetScaleProgress = target;
    }

    public float getTargetScaleProgress() {
        return targetScaleProgress;
    }

    public float getRealScale(float pticks){
        return FDMathUtil.lerp(0.1f,5f, FDEasings.easeIn(FDMathUtil.lerp(scaleProgressO, scaleProgress, pticks)));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        var realPos = this.getMousePos(0);
        return super.mouseClicked(realPos.x, realPos.y, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double xOffs, double yOffs) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {

            if (!blockingOffset){
                float realScale = this.getRealScale(0);

                this.moveTo(
                        (float) (offsetXTarget + xOffs / realScale),
                        (float) (offsetYTarget + yOffs / realScale),
                        0
                );
            }


        }
        var realPos = this.getMousePos(0);
        return super.mouseDragged(realPos.x, realPos.y, button, xOffs, yOffs);
    }

    private Vector2f getMousePos(float pticks){

        float px = (float) Minecraft.getInstance().mouseHandler.xpos() / Minecraft.getInstance().getWindow().getWidth();
        float py = (float) Minecraft.getInstance().mouseHandler.ypos() / Minecraft.getInstance().getWindow().getHeight();

        float windowX = px * this.width;
        float windowY = py * this.height;
        float rs = this.getRealScale(pticks);

        windowX -= this.width / 2f + this.getOffsetX(pticks) * rs;
        windowY -= this.height / 2f + this.getOffsetY(pticks) * rs;

        windowX /= rs;
        windowY /= rs;

        return new Vector2f(windowX, windowY);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        var realPos = this.getMousePos(0);
        return super.mouseReleased(realPos.x,realPos.y, button);
    }

    @Override
    public void mouseMoved(double mx, double my) {
        var realPos = this.getMousePos(0);
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
