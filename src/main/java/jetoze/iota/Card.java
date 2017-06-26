package jetoze.iota;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import jetoze.iota.Constants.Color;
import jetoze.iota.Constants.Shape;

public abstract class Card {

	public abstract int getFaceValue();

	public abstract boolean isWildcard();
	
	public abstract Set<Object> getMatchProperties();

	public abstract Set<Object> match(Set<Object> properties);

	public static Card wildcard() {
		return new Wildcard();
	}
	
	public static Card newCard(Color color, Shape shape, int faceValue) {
		return new ConcreteCard(color, shape, faceValue);
	}
	
	public static Set<Card> createPossibleCards(Set<Object> properties) {
		Set<Color> colors = new HashSet<>();
		Set<Shape> shapes = new HashSet<>();
		Set<Integer> faceValues = new HashSet<>();
		for (Object o : properties) {
			if (o instanceof Color) {
				colors.add((Color) o);
			} else if (o instanceof Shape) {
				shapes.add((Shape) o);
			} else if (o instanceof Integer) {
				faceValues.add((Integer) o);
			}
		}
		Set<Card> cards = new HashSet<>();
		for (Color c : colors) {
			for (Shape s : shapes) {
				for (Integer fv : faceValues) {
					cards.add(newCard(c, s, fv));
				}
			}
		}
		return cards;
	}
	
	
	private static final class ConcreteCard extends Card {

		private final Color color;

		private final Shape shape;

		private final int faceValue;

		public ConcreteCard(Color color, Shape shape, int faceValue) {
			this.color = checkNotNull(color);
			this.shape = checkNotNull(shape);
			checkArgument(faceValue >= Constants.MIN_FACE_VALUE && faceValue <= Constants.MAX_FACE_VALUE);
			this.faceValue = faceValue;
		}

		@Override
		public int getFaceValue() {
			return faceValue;
		}

		@Override
		public boolean isWildcard() {
			return false;
		}

		@Override
		public Set<Object> getMatchProperties() {
			return Sets.newHashSet(color, shape, faceValue);
		}

		@Override
		public Set<Object> match(Set<Object> properties) {
			Set<Object> newSet = new HashSet<>(properties);
			newSet.retainAll(this.getMatchProperties());
			return newSet;
		}

		@Override
		public boolean equals(@Nullable Object o) {
			if (o == this) {
				return true;
			}
			if (o instanceof ConcreteCard) {
				ConcreteCard that = (ConcreteCard) o;
				return this.color == that.color && this.shape == that.shape && this.faceValue == that.faceValue;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(color, shape, faceValue);
		}

		@Override
		public String toString() {
			return String.format("(%s %s %d)", color, shape, faceValue);
		}
	}

	
	private static class Wildcard extends Card {

		@Override
		public int getFaceValue() {
			return 0;
		}

		@Override
		public boolean isWildcard() {
			return true;
		}

		@Override
		public Set<Object> getMatchProperties() {
			return Collections.emptySet();
		}

		@Override
		public Set<Object> match(Set<Object> properties) {
			return new HashSet<>(properties);
		}
	}
	
}
