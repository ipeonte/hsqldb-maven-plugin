/*
 * Copyright 2016 Igor Peonte <igor.144@gmail.com>
 * 
 * Released under the Apache License v2.0
 * See http://www.apache.org/licenses/
 *
 */
package com.ivalab.mojo.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Stop HSQLDB database.
 * @goal stop
 * @threadSafe
 */
public class StopHsqlDbMojo extends AbstractMojo {

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
	
	public String getAlias() {
		return _db_name;
	}

	public void setAlias(String alias) {
		_db_name = alias;
	}
	
	public boolean getSkip() {
		return _skip;
	}

	public void setSkip(boolean skip) {
		_skip = skip;
	}
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (_skip)
			return;
		
		// Stopping
		getLog().info("Stopping HSQLDB");
		
		// Declare the JDBC objects.
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (ClassNotFoundException e) {
			throw new MojoExecutionException("HSQLDB Driver not found.");
		}
		
		try {
			// Connect to localhost and execute SHUTDOWN command
			// To simplify test the user always SA and password same as alias
			conn = DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/" + _db_name, "SA", _db_name);
			stmt = conn.createStatement();
			stmt.execute("SHUTDOWN");
		} catch (SQLException e) {
			getLog().error("Unable shutdown database '" + _db_name + "'. " + e.getMessage());
		}
	}
}
