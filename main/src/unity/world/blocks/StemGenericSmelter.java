package unity.world.blocks;

import arc.func.Cons;
import mindustry.world.blocks.production.GenericSmelter;

public class StemGenericSmelter extends GenericSmelter{
    protected boolean preserveDraw = true, preserveUpdate = true;
    protected Cons<StemSmelterBuild> foreDrawer = e -> {}, afterDrawer = e -> {}, foreUpdate = e -> {}, afterUpdate = e -> {};

    public StemGenericSmelter(String name){
        super(name);
    }

    public class StemSmelterBuild extends SmelterBuild{
        public Object data;

        @Override
        public void draw(){
            foreDrawer.get(this);
            if(preserveDraw) super.draw();
            afterDrawer.get(this);
        }

        @Override
        public void updateTile(){
            foreUpdate.get(this);
            if(preserveUpdate) super.updateTile();
            afterUpdate.get(this);
        }
    }
}