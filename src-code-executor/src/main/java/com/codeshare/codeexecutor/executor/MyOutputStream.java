/**
 * @ MyOutputStream.java
 *  	
 * <p>Copyright (c) 2014 Wal-Mart Stores, Inc. All rights reserved.
 * Wal-Mart PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.</p>
 */
package com.codeshare.codeexecutor.executor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.exec.ExecuteWatchdog;

/**
 * The <code>MyOutputStream</code> represents {description}
 * <p>
 * <li>{Enclosing Methods}</li> {short description}
 * 
 * Created at Aug 13, 2014 8:15:05 PM
 * 
 * @author vrasto1 (last updated by $Author$)
 * @version $Revision$ $Date$
 * @since GIF 1.0
 */
public class MyOutputStream extends ByteArrayOutputStream {

	private final int maxSize;

	private final AtomicInteger size = new AtomicInteger(0);

	private final ExecuteWatchdog watchdog;

	private final MyPumpStreamHandler myPumpStreamHandler;

	/**
	 * The constructor for <code>MyOutputStream</code> having following
	 * parameters
	 */
	public MyOutputStream(final int outputStreamMaxCharSize,
			final ExecuteWatchdog watchdog,
			final MyPumpStreamHandler myPumpStreamHandler) {
		super();
		maxSize = outputStreamMaxCharSize;
		this.watchdog = watchdog;
		this.myPumpStreamHandler = myPumpStreamHandler;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.io.ByteArrayOutputStream#write(int)
	 */
	@Override
	public synchronized void write(int b) {

		if (size.intValue() >= maxSize) {

			watchdog.destroyProcess();

			myPumpStreamHandler.outputStreamOverflowed();
		}

		super.write(b);
		size.incrementAndGet();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {

		if (size.intValue() >= maxSize) {

			watchdog.destroyProcess();

			myPumpStreamHandler.outputStreamOverflowed();
		}

		super.write(b);
		size.incrementAndGet();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.io.ByteArrayOutputStream#write(byte[], int, int)
	 */
	@Override
	public synchronized void write(byte[] b, int off, int len) {
		if (size.intValue() >= maxSize) {

			watchdog.destroyProcess();

			myPumpStreamHandler.outputStreamOverflowed();
		}
		super.write(b, off, len);
		size.set(size.get() + len);
	}

}
