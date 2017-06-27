package jetoze.iota;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

enum MatchType {

	SAME {

		@Override
		public Set<Card> collectCandidatesForNextCard(List<Card> line) {
			// The following could be rewritten using Stream.reduce, but the
			// resulting code is not any prettier.
			Set<Object> commonProperties = null;
			for (Card c : line) {
				if (c.isWildcard()) {
					continue;
				}
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
		public Set<Card> collectCandidatesForNextCard(List<Card> line) {
			Set<Object> props = Constants.collectAllCardProperties();
			line.stream().filter(c -> !c.isWildcard()).forEach(c -> {
				props.removeAll(c.getMatchProperties());
			});
			return Card.createPossibleCards(props);
		}
	},
	
	EITHER {

		@Override
		public Set<Card> collectCandidatesForNextCard(List<Card> line) {
			// A line with at most one concrete card. All card properties are possible.
			Set<Object> props = Constants.collectAllCardProperties();
			return Card.createPossibleCards(props);
		}

	};
	
	/**
	 * Given an existing line of cards, returns the possible Cards that can 
	 * be added as the next card to the line.
	 */
	public abstract Set<Card> collectCandidatesForNextCard(List<Card> line);
	
}