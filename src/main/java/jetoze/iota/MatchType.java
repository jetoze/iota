package jetoze.iota;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

enum MatchType {

	SAME {

		@Override
		public Set<Card> collectPossibleWildcardRepresentations(List<Card> nonWildcards) {
			Set<Object> commonProperties = null;
			for (Card c : nonWildcards) {
				if (commonProperties == null) {
					commonProperties = c.getMatchProperties();
				} else {
					commonProperties = c.match(commonProperties);
				}
			}
			Set<Card> candidates = new HashSet<>();
			for (Object p : commonProperties) {
				Set<Object> allProps = Constants.collectAllCardProperties();
				allProps.removeIf(o -> o != p && p.getClass().isInstance(o));
				candidates.addAll(Card.createPossibleCards(allProps));
			}
			return candidates;
		}
	},
	
	DIFFERENT {

		@Override
		public Set<Card> collectPossibleWildcardRepresentations(List<Card> nonWildcards) {
			Set<Object> props = Constants.collectAllCardProperties();
			for (Card c : nonWildcards) {
				props.removeAll(c.getMatchProperties());
			}
			return Card.createPossibleCards(props);
		}
	},
	
	EITHER {

		@Override
		public Set<Card> collectPossibleWildcardRepresentations(List<Card> nonWildcards) {
			// A line with at most one concrete card. All card properties are possible.
			Set<Object> props = Constants.collectAllCardProperties();
			return Card.createPossibleCards(props);
		}

	};
	
	public abstract Set<Card> collectPossibleWildcardRepresentations(List<Card> nonWildcards);
	
}