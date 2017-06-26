package jetoze.iota;

import java.util.List;
import java.util.Set;

enum MatchType {

	SAME {

		@Override
		public Set<Card> collectPossibleWildcardRepresentations(List<Card> nonWcCards) {
			// TODO: Implement me.
			return null;
		}
	},
	
	DIFFERENT {

		@Override
		public Set<Card> collectPossibleWildcardRepresentations(List<Card> nonWcCards) {
			Set<Object> props = Constants.collectAllCardProperties();
			for (Card c : nonWcCards) {
				props.removeAll(c.getMatchProperties());
			}
			return Card.createPossibleCards(props);
		}
	},
	
	EITHER {

		@Override
		public Set<Card> collectPossibleWildcardRepresentations(List<Card> nonWcCards) {
			// A line with at most one concrete card. All card properties are possible.
			Set<Object> props = Constants.collectAllCardProperties();
			return Card.createPossibleCards(props);
		}

	};
	
	public abstract Set<Card> collectPossibleWildcardRepresentations(List<Card> nonWcCards);
	
}