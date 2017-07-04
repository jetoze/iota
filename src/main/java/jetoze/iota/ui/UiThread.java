package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.EventQueue;

public final class UiThread {

	public static void run(Runnable r) {
		if (isUiThread()) {
			r.run();
		} else {
			checkNotNull(r);
			EventQueue.invokeLater(r);
		}
	}
	
	public static boolean isUiThread() {
		return EventQueue.isDispatchThread();
	}
	
	private UiThread() {/**/}

}
