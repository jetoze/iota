package jetoze.iota.ui;

import static com.google.common.base.Preconditions.*;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JComponent;

import com.google.common.collect.ImmutableList;

import jetoze.iota.Player;

public final class GameBoard {

	private final GridUi grid;
	
	private final ControlPanel controlPanel;
	
	private final LinkedHashMap<Player, PlayerArea> playerAreas = new LinkedHashMap<>();
	
	public GameBoard(GridUi grid, ControlPanel controlPanel, List<PlayerArea> playerAreas) {
		this.grid = checkNotNull(grid);
		this.controlPanel = checkNotNull(controlPanel);
		playerAreas.forEach(pa -> GameBoard.this.playerAreas.put(pa.getPlayer(), pa));
	}
	
	public ImmutableList<PlayerArea> getPlayerAreas() {
		return ImmutableList.copyOf(playerAreas.values());
	}
	
	public JComponent layout() {
		checkState(playerAreas.size() == 2, "Only two players supported at the moment.");
		Iterator<PlayerArea> pas = playerAreas.values().iterator();
		return Layouts.border(0, 10)
				.center(grid.inScroll())
				.south(Layouts.border(10, 0)
						.west(pas.next().getUi())
						.center(controlPanel.layout())
						.east(pas.next().getUi()))
				.container();
	}
	
}
