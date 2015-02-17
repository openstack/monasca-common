/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package monasca.common.middleware;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.ClientConnectionManager;

/**
 * A runner to clean the connection pool! There should only be one!
 * 
 * @author liemmn
 * 
 */
public class HttpPoolCleaner implements Runnable {
	private final ClientConnectionManager connMgr;
	private long timeBetweenEvictionRunsMillis, minEvictableIdleTimeMillis;
	private volatile boolean shutdown;

	public HttpPoolCleaner(ClientConnectionManager connMgr,
			long timeBetweenEvictionRunsMillis, long minEvictableIdleTimeMillis) {
		this.connMgr = connMgr;
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	/**
	 * Start the cleaner.
	 */
	@Override
	public void run() {
		try {
			while (!shutdown) {
				synchronized (this) {
					wait(timeBetweenEvictionRunsMillis);
					// Close expired connections
					connMgr.closeExpiredConnections();
					// Close connections that have been idle longer than x sec
					connMgr.closeIdleConnections(minEvictableIdleTimeMillis,
							TimeUnit.MILLISECONDS);
				}
			}
		} catch (InterruptedException ex) {
			// terminate
		}
	}

	/**
	 * Shutdown the cleaner.
	 */
	public void shutdown() {
		shutdown = true;
		synchronized (this) {
			notifyAll();
		}
	}

}
