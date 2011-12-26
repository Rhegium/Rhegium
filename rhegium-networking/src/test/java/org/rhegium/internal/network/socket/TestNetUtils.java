package org.rhegium.internal.network.socket;

import java.net.ServerSocket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TestNetUtils {

	private TestNetUtils() {
	}

	public static int findFreePort() {
		try {
			ServerSocket socket = new ServerSocket(0);
			int port = socket.getLocalPort();
			socket.close();

			return port;
		}
		catch (Exception e) {
			// ignore and try next one
		}

		return -1;
	}

	@SuppressWarnings("unchecked")
	public static ScheduledFuture<Runnable> startTestTimoutExceeded(ScheduledExecutorService executor,
			Runnable runnable, long timeout) {

		return (ScheduledFuture<Runnable>) executor.schedule(runnable, timeout, TimeUnit.MILLISECONDS);
	}

}
