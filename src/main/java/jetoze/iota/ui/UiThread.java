package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.EventQueue;
import java.util.function.Consumer;

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
	
	public static <T> void supply(T value, Consumer<? super T> consumer) {
		if (isUiThread()) {
			consumer.accept(value);
		} else {
			runLater(() -> consumer.accept(value));
		}
	}
	
	private UiThread() {/**/}

}
