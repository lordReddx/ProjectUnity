package unity.world.blocks.exp;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

import static mindustry.Vars.*;

public class ExpHub extends ExpTank{
    public float ratio = 0.3f;
    public float reloadTime = 30f;
    public float range = 40f;

    public int maxLinks = 4;
    private final Seq<Building> tmpe = new Seq<>();

    public ExpHub(String name){
        super(name);
        configurable = true;

        config(Integer.class, (ExpHubBuild entity, Integer value) -> {
            Building other = world.build(value);
            boolean contains = entity.links.contains(value);
            //if(entity.occupied == null) entity.occupied = new boolean[maxLinks];

            if(contains){
                //unlink
                int i = entity.links.indexOf(value);
                entity.links.removeIndex(i);;
            }else if(linkValid(entity, other) && entity.links.size < maxLinks){
                if(!entity.links.contains(other.pos())){
                    entity.links.add(other.pos());
                }
            }
            else{
                return;
            }
            entity.sanitize();
        });
        configClear((ExpHubBuild entity) -> {
            entity.links.clear();
        });
    }

    @Override
    public void init(){
        super.init();
        clipSize = Math.max(clipSize, range * 2f + 8f);
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.add("links", (ExpHubBuild entity) -> new Bar(() -> Core.bundle.format("bar.iconlinks", entity.links.size, maxLinks, Iconc.turret), () -> Pal.accent, () -> entity.links.size / (float)maxLinks));
    }

    public boolean linkValid(Building tile, Building link){
        if(tile == link || link == null || tile.team != link.team || link.dead) return false;

        return tile.dst2(link) <= range * range &&
                (link instanceof ExpHolder e && e.hubbable() && e.canHub(tile));
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }

    public class ExpHubBuild extends ExpTankBuild {
        public IntSeq links = new IntSeq();

        public int takeAmount(int e){
            if(e <= 0) return 0;
            int prefa = Mathf.ceilPositive(ratio * e);
            return handleExp(prefa);
        }

        public void sanitize(){
            for(int i = 0; i < links.size; i++){
                Building b = world.build(links.get(i));

                if(!linkValid(this, b) || links.get(i) != b.pos()){
                    links.removeIndex(i);
                    i--;
                }
                else if(b instanceof ExpHolder e && e.hubbable() && e.canHub(this)) e.setHub(this);
            }
        }


        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            sanitize();
        }
    }
}
