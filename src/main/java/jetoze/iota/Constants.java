package jetoze.iota;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class Constants {
	
	public static final int MAX_LINE_LENGTH = 4;
	
	public static final int MIN_FACE_VALUE = 1;
	
	public static final int MAX_FACE_VALUE = MAX_LINE_LENGTH;
	
	public static enum Color {
		
		RED, GREEN, BLUE, YELLOW, WHITE
		
	}


	public static enum Shape {
		
		CIRCLE, SQUARE, TRIANGLE, CROSS
	}

	
	public static Set<Object> collectAllCardProperties() {
		Set<Object> props = new HashSet<>();
		props.addAll(EnumSet.allOf(Color.class));
		props.addAll(EnumSet.allOf(Shape.class));
		for (int i = MIN_FACE_VALUE; i <= MAX_FACE_VALUE; ++i) {
			props.add(i);
		}
		return props;
	}

	private Constants() {/**/}

}
