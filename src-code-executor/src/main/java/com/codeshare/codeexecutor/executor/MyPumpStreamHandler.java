/**
 * @ MyPumpStreamHandler.java
 *  	
 * <p>Copyright (c) 2014 Wal-Mart Stores, Inc. All rights reserved.
 * Wal-Mart PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.</p>
 */
package com.codeshare.codeexecutor.executor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * The <code>MyPumpStreamHandler</code> represents {description}
 * <p>
 * <li>{Enclosing Methods}</li> {short description}
 * 
 * Created at Aug 13, 2014 7:12:46 PM
 * 
 * @author vrasto1 (last updated by $Author$)
 * @version $Revision$ $Date$
 * @since GIF 1.0
 */
public class MyPumpStreamHandler {

	private ByteArrayOutputStream out;;

	private PumpStreamHandler pumpStreamHandler;

	private volatile boolean isOutputStreamOverflowed = false;

	/**
	 * The constructor for <code>MyPumpStreamHandler</code> having following
	 * parameters
	 */
	public MyPumpStreamHandler(final String stdin,
			final ExecuteWatchdog watchdog, final int outputStreamMaxCharSize) {

		out = new MyOutputStream(outputStreamMaxCharSize, watchdog, this);

		final byte[] stdinBytes;

		if (stdin != null) {
			stdinBytes = stdin.getBytes();
		} else {
			stdinBytes = "".getBytes();
		}
		pumpStreamHandler = new PumpStreamHandler(out, out,
				new ByteArrayInputStream(stdinBytes));
	}

	/**
	 * @return the pumpStreamHandler
	 */
	public PumpStreamHandler getPumpStreamHandler() {
		return pumpStreamHandler;
	}

	/**
	 * The method <code>getOutput</code> {description}
	 * 
	 * @see
	 */
	public String getStdout() {

		return new String(out.toByteArray());
	}

	/**
	 * The method <code>isOutputStreamOverflow</code> {description}
	 * 
	 * @see
	 */
	public boolean isOutputStreamOverflowed() {

		return isOutputStreamOverflowed;
	}

	/**
	 * The method <code>isOutputStreamOverflow</code> {description}
	 * 
	 * @see
	 */
	public void outputStreamOverflowed() {

		isOutputStreamOverflowed = true;
	}
}
