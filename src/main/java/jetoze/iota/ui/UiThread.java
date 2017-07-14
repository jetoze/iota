package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.EventQueue;

public final class UiThread {
	
	public static boolean isUiThread() {
		return EventQueue.isDispatchThread();
	}

	public static void run(Runnable r) {
		if (isUiThread()) {
			r.run();
		} else {
			runLater(r);
		}
	}

	public static void runLater(Runnable r) {
		checkNotNull(r);
		EventQueue.invokeLater(r);
	}
	
	private UiThread() {/**/}

}
