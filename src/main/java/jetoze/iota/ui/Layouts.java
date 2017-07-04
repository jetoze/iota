package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

public class Layouts {

	public static BorderLayoutBuilder border(JPanel p) {
		return new BorderLayoutBuilder(p);
	}

	public static BorderLayoutBuilder border(JPanel p, int hGap, int vGap) {
		return new BorderLayoutBuilder(p, hGap, vGap);
	}
	
	
	public static final class BorderLayoutBuilder {
		
		private final JPanel panel;
		
		public BorderLayoutBuilder(JPanel panel) {
			this(panel, 0, 0);
		}
		
		public BorderLayoutBuilder(JPanel panel, int hGap, int vGap) {
			this.panel = checkNotNull(panel);
			this.panel.setLayout(new BorderLayout(hGap, vGap));
		}

		public BorderLayoutBuilder north(Component c) {
			panel.add(c, BorderLayout.NORTH);
			return this;
		}

		public BorderLayoutBuilder south(Component c) {
			panel.add(c, BorderLayout.SOUTH);
			return this;
		}

		public BorderLayoutBuilder east(Component c) {
			panel.add(c, BorderLayout.EAST);
			return this;
		}

		public BorderLayoutBuilder west(Component c) {
			panel.add(c, BorderLayout.WEST);
			return this;
		}

		public BorderLayoutBuilder center(Component c) {
			panel.add(c, BorderLayout.CENTER);
			return this;
		}
	}
	
	
	private Layouts() {/**/}

}
