package unity.blocks.experience;

import java.lang.reflect.*;;

public class ExpField{
	public final String name;
	public final Field field;
	public final int start, intensity;

	public ExpField(String name, Field field, int start, int intensity){
		this.name = name;
		this.field = field;
		this.start = start;
		this.intensity = intensity;
	}
}
