# hsqldb-maven-plugin

Simple maven plugin to start/stop HSQLDB database as standalone server
Used for maven integration test phase

## Configuration Parameters
There is no password in configuration
Database username must be *SA*
Password is the same as database alias

- **database** - absolute path to database, for ex. ${basedir}/target/db/test
- **alias** - alias used to connect over the tcp/ip. For ex. if alias=test than JDBC connection string
will be *jdbc:hsqldb:hsql://localhost/test*
- **skip** - Flag to skip (True) server start during integration phase or not (False). Default: false
- **daemon** - Start server as daemon. Default: false

## Plugin usage example

```xml
<properties>
	<test-db>${basedir}/target/db/test</test-db>
</properties>

....

<plugin>
	<groupId>com.ivalab</groupId>
	<artifactId>hsqldb-maven-plugin</artifactId>
	<version>1.0.0</version>
	<configuration>
		<database>${test-db}</database>
		<alias>test</alias>
		<skip>${skipTests}</skip>
	</configuration>
	<executions>
		<execution>
			<id>start-hsqldb</id>
			<phase>pre-integration-test</phase>
			<configuration>
				<daemon>true</daemon>
			</configuration>
			<goals>
				<goal>start</goal>
			</goals>
		</execution>
		<execution>
			<id>stop-hsqldb</id>
			<phase>post-integration-test</phase>
			<goals>
				<goal>stop</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```

## Command line usage
mvn hsqldb:start  
mvn hsqldb:stop

