package com.finderfeed.fdbosses.content.entities.netzach.sector_attack;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.systems.shapes.FD2DShape;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SectorAttackShape {

    private List<FD2DShape> shapes = new ArrayList<>();
    private Pair<Vector2f, Vector2f> bounds = new Pair<>(new Vector2f(), new Vector2f());

    public SectorAttackShape(){

    }

    public void setMaxXBound(float xBound){
        bounds.second.x = xBound;
    }

    public void setMaxZBound(float yBound){
        bounds.second.y = yBound;
    }

    public void setMinXBound(float xBound){
        bounds.first.x = xBound;
    }

    public void setMinZBound(float yBound){
        bounds.first.y = yBound;
    }

    public float getMinBoundX(){
        return bounds.first.x;
    }

    public float getMinBoundZ(){
        return bounds.first.y;
    }

    public float getMaxBoundX(){
        return bounds.second.x;
    }

    public float getMaxBoundZ(){
        return bounds.second.y;
    }

    private void trySetBoundsUsingShape(FD2DShape shape){
        for (var point : shape.getPoints()){

            var x = point.x;
            var z = point.z;

            if (x > this.getMaxBoundX()){
                this.setMaxXBound(x);
            }else if (x < this.getMinBoundX()){
                this.setMinXBound(x);
            }

            if (z > this.getMaxBoundZ()){
                this.setMaxZBound(z);
            }else if (z < this.getMinBoundZ()){
                this.setMinZBound(z);
            }

        }
    }

    public SectorAttackShape addSquare(float xOffset, float zOffset, float radius, float rotation){
        Vector3f offset = new Vector3f(xOffset,0,zOffset).rotateY(rotation);
        FD2DShape square = new FD2DShape(List.of(
                new Vector3f(- radius,0,- radius).rotateY(rotation).add(offset),
                new Vector3f(- radius,0,+ radius).rotateY(rotation).add(offset),
                new Vector3f(+ radius,0,+ radius).rotateY(rotation).add(offset),
                new Vector3f(+ radius,0,- radius).rotateY(rotation).add(offset)
        ));
        this.shapes.add(square);
        this.trySetBoundsUsingShape(square);
        return this;
    }

    public SectorAttackShape addTriangle(float xOffset, float zOffset, float width, float height, float rotation){
        Vector3f offset = new Vector3f(xOffset,0,zOffset).rotateY(rotation);
        FD2DShape triangle = new FD2DShape(List.of(
                new Vector3f(-width,0,0).rotateY(rotation).add(offset),
                new Vector3f(-width/2,0,height).rotateY(rotation).add(offset),
                new Vector3f(offset)
        ));
        this.shapes.add(triangle);
        this.trySetBoundsUsingShape(triangle);
        return this;
    }


    public SectorAttackShape addSector(float startRadius, float endRadius, float spanAngle, float rotation){
        List<Vector3f> points = new ArrayList<>();
        if (startRadius == 0) {
            points.add(new Vector3f(0, 0, 0));
        }else{
            int stepCount = BossUtil.calculateNeededStepCountOnCircle(startRadius, spanAngle, 1);
            for (int i = stepCount; i >= -stepCount; i--){
                float p = (float) i / stepCount;
                Vector3f vector3f = new Vector3f(startRadius,0,0).rotateY(rotation + spanAngle * p);
                points.add(vector3f);
            }
        }

        int stepCount = BossUtil.calculateNeededStepCountOnCircle(endRadius, spanAngle, 2);
        for (int i = -stepCount; i <= stepCount; i++){
            float p = (float) i / stepCount;
            Vector3f vector3f = new Vector3f(endRadius,0,0).rotateY(rotation + spanAngle * p);
            points.add(vector3f);
        }

        var shape = new FD2DShape(points);
        this.shapes.add(shape);
        this.trySetBoundsUsingShape(shape);
        return this;
    }

    public SectorAttackShape addSimpleSquareCheckerboard(float quadRadius, int quadAmountAround, float rotation){

        this.addSquare(0,0,quadRadius, rotation);

        for (int xi = -quadAmountAround; xi <= quadAmountAround; xi++){
            float xOffset = quadRadius * xi * 2;

            for (int zi = -quadAmountAround; zi <= quadAmountAround; zi++){
                if (xi == 0 && zi == 0) continue;

                if ((xi + zi) % 2 != 0) continue;

                float zOffset = quadRadius * zi * 2;

                this.addSquare(xOffset, zOffset, quadRadius, rotation);
            }
        }


        return this;
    }

    public List<FD2DShape> getShapes() {
        return shapes;
    }

}
