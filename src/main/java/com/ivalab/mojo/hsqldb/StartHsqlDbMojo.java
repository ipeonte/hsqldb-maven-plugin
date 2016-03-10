/*
 * Copyright 2016 Igor Peonte <igor.144@gmail.com>
 * 
 * Released under the Apache License v2.0
 * See http://www.apache.org/licenses/
 *
 */

package com.ivalab.mojo.hsqldb;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hsqldb.HsqlException;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;

/**
 * Start HSQLDB database.
 * 
 * @goal start
 * @threadSafe
 */
public class StartHsqlDbMojo extends AbstractMojo {
	/**
	 * Database path.
	 * 
	 * @since 1.0
	 * @required
	 * @parameter property="database"
	 */
	private String _db_path;

	/**
	 * Database alias.
	 * 
	 * @since 1.0
	 * @required
	 * @parameter property="alias"
	 */
	private String _db_name;

	/**
	 * When <code>true</code>, skip the execution.
	 * 
	 * @since 1.0
	 * @parameter property="skip" default-value="false"
	 */
	private boolean _skip;

	/**
	 * When <code>true</code>, start server as daemon.
	 * 
	 * @since 1.0
	 * @parameter property="daemon" default-value="false"
	 */
	private boolean _daemon;

	// Pointer on running server instance
	private Server _server;

	public boolean getSkip() {
		return _skip;
	}

	public void setSkip(boolean skip) {
		_skip = skip;
	}

	public String getDatabase() {
		return _db_path;
	}

	public void setDatabase(String database) {
		_db_path = database;
	}

	public String getAlias() {
		return _db_name;
	}

	public void setAlias(String alias) {
		_db_name = alias;
	}

	public boolean isDaemon() {
		return _daemon;
	}

	public void setDaemon(boolean daemon) {
		_daemon = daemon;
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (_skip) {
			getLog().info("Skip HSQLDB start");
			return;
		}

		getLog().info("Starting HSQLDB");

		HsqlProperties p = new HsqlProperties();
		p.setProperty("server.database.0", _db_path);
		p.setProperty("server.dbname.0", _db_name);

		_server = new Server();
		try {
			_server.setDaemon(true);
			_server.setProperties(p);
		} catch (Exception e) {
			throw new MojoExecutionException("HSQLDB configuration error.", e);
		}

		if (_daemon)
			startServerSingle();
		else
			startServerBlocked();
	}

	private void startServerSingle() {
		_server.start();
	}

	private void startServerBlocked() {
		// Start separate thread with server
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				_server.start();

				boolean running = true;

				while (running) {
					try {
						_server.checkRunning(running);
					} catch (HsqlException e) {
						running = false;
					}

					if (running) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// Nothing to do
						}
					}
				}

				System.out.println("Server thread completed.");
			}
		});

		t.start();

		try {
			t.join();
		} catch (InterruptedException e) {
			getLog().error(
					"Unable join running server thread: " + e.getMessage());
		}
	}
}
