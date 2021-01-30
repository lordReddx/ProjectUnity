package unity.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;
import unity.util.*;

public class PointBlastLaserBulletType extends BulletType{
    public float damageRadius = 20f;
    public float auraDamage = 10f;
    public float length = 100f;
    public float width = 12f;
    public Color laserColor = Color.white;

    private static boolean available = false;

    public PointBlastLaserBulletType(float damage){
        speed = 0.01f;
        this.damage = damage;

        hitEffect = Fx.hitLancer;
        despawnEffect = Fx.none;
        shootEffect = Fx.none;
        smokeEffect = Fx.none;
        hitSize = 4f;
        lifetime = 16f;
        keepVelocity = false;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
    }

    @Override
    public float estimateDPS(){
        return (super.estimateDPS() * 3f) + auraDamage;
    }
    
    @Override
    public void init(){
        super.init();

        drawSize = Math.max(drawSize, length * 2f);
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        
        b.fdata = length;
        Tmp.v1.trns(b.rotation(), length);
        Tmp.v1.add(b);
        available = false;
        Utils.collideLineRaw(b.x, b.y, Tmp.v1.x, Tmp.v1.y, building -> building.team != b.team, unit -> unit.team != b.team, building -> {
            building.damage(damage);
            available = true;
            b.fdata = b.dst(building);
            Tmp.v2.trns(b.rotation(), b.dst(building));
            Tmp.v2.add(b.x, b.y);
            return building.block.absorbLasers;
        }, unit -> {
            available = true;
            Tmp.v2.trns(b.rotation(), b.dst(unit));
            Tmp.v2.add(b.x, b.y);
            unit.damage(damage);
            unit.apply(status, statusDuration);
        }, unit -> ((length * 1.5f) - (b.dst(unit) / 2f)) + unit.health, hitEffect);

        if(available){
            b.fdata = b.dst(Tmp.v2);
            Vars.indexer.eachBlock(null, Tmp.v2.x, Tmp.v2.y, damageRadius, building -> building.team != b.team, building -> building.damage(auraDamage));
            Units.nearby(Tmp.v2.x - damageRadius, Tmp.v2.y - damageRadius, damageRadius * 2f, damageRadius * 2f, unit -> {
                if(unit.team != b.team && unit.within(Tmp.v2.x, Tmp.v2.y, damageRadius)){
                    unit.damage(auraDamage);
                    unit.apply(status, statusDuration);
                }
            });
            UnityFx.pointBlastLaserEffect.at(Tmp.v2.x, Tmp.v2.y, damageRadius, laserColor);
        }
    }

    @Override
    public void draw(Bullet b){
        float realLength = b.fdata;
        float f = Mathf.curve(b.fin(), 0f, 0.2f);
        float baseLen = realLength * f;

        Draw.color(laserColor);
        Lines.stroke(width * b.fout());
        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen, false);
        Tmp.v1.trns(b.rotation(), baseLen).add(b);
        Drawf.tri(Tmp.v1.x, Tmp.v1.y, Lines.getStroke() * 1.22f, width * 2f, b.rotation());
        Fill.circle(b.x, b.y, (width / 2f) * b.fout());
        Draw.reset();
        Tmp.v1.trns(b.rotation(), baseLen + (width / 1.5f)).add(b);
        Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, width * 1.4f * b.fout(), laserColor, 0.5f);
    }

    @Override
    public void drawLight(Bullet b){

    }
}