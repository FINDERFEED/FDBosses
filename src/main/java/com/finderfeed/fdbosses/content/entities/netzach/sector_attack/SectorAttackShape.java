package com.finderfeed.fdbosses.content.entities.netzach.sector_attack;

import com.finderfeed.fdlib.systems.shapes.FD2DShape;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SectorAttackShape {

    private List<FD2DShape> shapes = new ArrayList<>();

    public SectorAttackShape(){
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
        return this;
    }


    public SectorAttackShape addSector(float radius, float spanAngle, float rotation){
        List<Vector3f> points = new ArrayList<>();
        points.add(new Vector3f(0,0,0));

        int sidecount = 4;
        for (int i = -sidecount; i <= sidecount; i++){
            float p = (float) i / sidecount;
            Vector3f vector3f = new Vector3f(radius,0,0).rotateY(rotation + spanAngle * p);
            points.add(vector3f);
        }

        this.shapes.add(new FD2DShape(points));
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
