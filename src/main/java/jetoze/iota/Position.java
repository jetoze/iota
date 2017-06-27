package jetoze.iota;

import java.util.Objects;

public final class Position {
	
	public final int row;
	
	public final int col;
	
	public Position(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public Position leftOf() {
		return new Position(row, col - 1);
	}
	
	public Position rightOf() {
		return new Position(row, col + 1);
	}
	
	public Position above() {
		return new Position(row - 1, col);
	}
	
	public Position below() {
		return new Position(row + 1, col);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true; 
		}
		if (o instanceof Position) {
			Position that = (Position) o;
			return this.row == that.row && this.col == that.col;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(row, col);
	}
	
	@Override
	public String toString() {
		return String.format("[%d, %d]", row, col);
	}
}