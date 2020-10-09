package unity.units;

import arc.math.Mathf;
import mindustry.gen.*;
import mindustry.type.UnitType;
import unity.content.UnityUnitTypes;

public class CopterUnit extends UnitEntity{
	private int classID;
	public CopterUnitType copterType;

	@Override
	public void update(){
		super.update();
		if (dead) rotation += copterType.fallRotateSpeed * Mathf.signs[id % 2];
		customUpdate();
	}

	public void customUpdate(){}

	@Override
	public void type(UnitType type){
		super.type(type);
		if (type instanceof CopterUnitType) copterType = (CopterUnitType) type;
		else throw new ClassCastException("you set this unit's type in sneaky way");
	}

	@Override
	public void setStats(UnitType type){
		super.setStats(type);
		if (type instanceof CopterUnitType) copterType = (CopterUnitType) type;
		else throw new ClassCastException("you set this unit's type in sneaky way");
	}

	@Override
	public int classId(){ return UnityUnitTypes.getClassId(0); }

}