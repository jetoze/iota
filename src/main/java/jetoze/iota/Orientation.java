package jetoze.iota;import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public enum Orientation {

	HORIZONTAL,
	
	VERTICAL;
	
	
	public static void validatePoints(LineItem... items) throws IllegalArgumentException {
		validatePoints(Arrays.asList(items));
	}
	
	public static void validatePoints(List<LineItem> items) throws IllegalArgumentException {
		switch (items.size()) {
		case 0:
			throw new IllegalArgumentException("Must provide at least one item");
		case 1:
			break;
		default:
			if (items.size() > Constants.MAX_LINE_LENGTH) {
				throw new IllegalArgumentException("Line too long");
			}
			Orientation.of(items.stream().map(LineItem::getPosition).collect(toList()));
		}
	}

	public static Orientation of(List<Position> positions) {
		checkArgument(positions.size() >= 2);
		TreeSet<Integer> rows = new TreeSet<>();
		TreeSet<Integer> cols = new TreeSet<>();
		for (Position p : positions) {
			rows.add(p.row);
			cols.add(p.col);
		}
		if (rows.size() > 1 && cols.size() > 1) {
			throw new IllegalArgumentException("Not a line");
		}
		if (rows.size() == 1) {
			checkArgument(cols.last() - cols.first() == cols.size() - 1, "Gaps are not allowed");
			return HORIZONTAL;
		} else {
			assert cols.size() == 1;
			checkArgument(rows.last() - rows.first() == rows.size() - 1, "Gaps are not allowed");
			return VERTICAL;
		}
	}
	
}
