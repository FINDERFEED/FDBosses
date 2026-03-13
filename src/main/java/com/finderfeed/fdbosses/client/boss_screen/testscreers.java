package com.finderfeed.fdbosses.client.boss_screen;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdlib.systems.shapes.FD2DShape;
import com.finderfeed.fdlib.systems.simple_screen.SimpleFDScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

public class testscreers extends SimpleFDScreen {


    @Override
    public void render(GuiGraphics gr, int mx, int my, float pticks) {

        float r = 1;
        float g = 0;

        var anchor = this.getAnchor(0.5f, 0.5f);

//        Vector2f p1 = anchor.add(-30f,-30f, new Vector2f());
//        Vector2f p2 = anchor.add(60f,50f, new Vector2f());
//        Vector2f p3 = anchor.add(80f,-30f, new Vector2f());
//
//        if (BossUtil.isPointInTriangle(p1,p2,p3, new Vector2f(mx, my))){
//            g = 1;
//            r = 0;
//        }
//
//        this.renderLine(gr, p1.x, p1.y, p2.x, p2.y, r,g,0);
//        this.renderLine(gr, p2.x, p2.y, p3.x, p3.y, r,g,0);
//        this.renderLine(gr, p1.x, p1.y, p3.x, p3.y, r,g,0);

        FD2DShape testshape = new FD2DShape(List.of(
                new Vector3f(-20,0,-20),
                new Vector3f(-20,0,20),
                new Vector3f(20,0,20),
                new Vector3f(20,0,-20)
        ));

        var triangles = BossUtil.splitShapeToTriangles(testshape);

        for (var shape : triangles){



        }

    }

    public void renderTriangle(FD2DShape shape){

    }



    public void renderLine(GuiGraphics graphics, float x1, float y1, float x2, float y2, float r, float g, float b){
        Tesselator tesselator = Tesselator.getInstance();
        var vertex = tesselator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.lineWidth(10f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        Matrix4f mat = graphics.pose().last().pose();
        vertex.addVertex(mat, x1, y1, 0).setColor(r,g,b,1f);
        vertex.addVertex(mat, x2, y2, 0).setColor(r,g,b,1f);

        BufferUploader.drawWithShader(vertex.build());
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
