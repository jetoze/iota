package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class Layouts {

	public static BorderLayoutBuilder border() {
		return border(new JPanel());
	}

	public static BorderLayoutBuilder border(int hGap, int vGap) {
		return border(new JPanel(), hGap, vGap);
	}

	public static BorderLayoutBuilder border(JComponent p) {
		return new BorderLayoutBuilder(p);
	}

	public static BorderLayoutBuilder border(JComponent p, int hGap, int vGap) {
		return new BorderLayoutBuilder(p, hGap, vGap);
	}
	
	public static GridLayoutBuilder grid(int rows, int cols) {
		return new GridLayoutBuilder(rows, cols);
	}
	
	public static GridLayoutBuilder grid(int rows, int cols, int hGap, int vGap) {
		return new GridLayoutBuilder(rows, cols, hGap, vGap);
	}
	
	public static GridLayoutBuilder grid(JComponent container, int rows, int cols) {
		return new GridLayoutBuilder(container, rows, cols);
	}
	
	public static GridLayoutBuilder grid(JComponent container, int rows, int cols, int hGap, int vGap) {
		return new GridLayoutBuilder(container, rows, cols, hGap, vGap);
	}
	
	
	public static interface LayoutBuilder {
		
		JComponent container();
		
	}

	
	public static final class BorderLayoutBuilder implements LayoutBuilder {
		
		private final JComponent container;
		
		public BorderLayoutBuilder(JComponent panel) {
			this(panel, 0, 0);
		}
		
		public BorderLayoutBuilder(JComponent panel, int hGap, int vGap) {
			this.container = checkNotNull(panel);
			this.container.setLayout(new BorderLayout(hGap, vGap));
		}

		@Override
		public JComponent container() {
			return container;
		}
		
		public BorderLayoutBuilder north(Component c) {
			container.add(c, BorderLayout.NORTH);
			return this;
		}
		
		public BorderLayoutBuilder north(LayoutBuilder lb) {
			return north(lb.container());
		}

		public BorderLayoutBuilder south(Component c) {
			container.add(c, BorderLayout.SOUTH);
			return this;
		}
		
		public BorderLayoutBuilder south(LayoutBuilder lb) {
			return south(lb.container());
		}

		public BorderLayoutBuilder east(Component c) {
			container.add(c, BorderLayout.EAST);
			return this;
		}
		
		public BorderLayoutBuilder east(LayoutBuilder lb) {
			return east(lb.container());
		}

		public BorderLayoutBuilder west(Component c) {
			container.add(c, BorderLayout.WEST);
			return this;
		}
		
		public BorderLayoutBuilder west(LayoutBuilder lb) {
			return west(lb.container());
		}

		public BorderLayoutBuilder center(Component c) {
			container.add(c, BorderLayout.CENTER);
			return this;
		}
		
		public BorderLayoutBuilder center(LayoutBuilder lb) {
			return center(lb.container());
		}
		
		public BorderLayoutBuilder withBorder(Border b) {
			container.setBorder(b);
			return this;
		}
	}
	
	
	public static final class GridLayoutBuilder implements LayoutBuilder {
		
		private final JComponent container;
		
		public GridLayoutBuilder(int rows, int cols) {
			this(new JPanel(), rows, cols);
		}
		
		public GridLayoutBuilder(int rows, int cols, int hGap, int vGap) {
			this(new JPanel(), rows, cols, hGap, vGap);
		}
		
		public GridLayoutBuilder(JComponent container, int rows, int cols) {
			this(container, rows, cols, 0, 0);
		}
		
		public GridLayoutBuilder(JComponent container, int rows, int cols, int hGap, int vGap) {
			GridLayout layout = new GridLayout(rows, cols, hGap, vGap);
			container.setLayout(layout);
			this.container = container;
		}
		
		@Override
		public JComponent container() {
			return container;
		}

		public GridLayoutBuilder of(Component... components) {
			return addAll(components);
		}
		
		public GridLayoutBuilder add(Component c) {
			return addAll(c);
		}
		
		public GridLayoutBuilder addAll(Component... components) {
			for (Component c : components) {
				checkNotNull(c);
				container.add(c);
			}
			return this;
		}
		
		public GridLayoutBuilder add(LayoutBuilder lb) {
			return add(lb.container());
		}
		
		public GridLayoutBuilder withHGap(int hGap) {
			((GridLayout) container.getLayout()).setHgap(hGap);
			return this;
		}
		
		public GridLayoutBuilder withVGap(int vGap) {
			((GridLayout) container.getLayout()).setVgap(vGap);
			return this;
		}
		
		public GridLayoutBuilder withBorder(Border b) {
			container.setBorder(b);
			return this;
		}
	}
	
	
	private Layouts() {/**/}

}
