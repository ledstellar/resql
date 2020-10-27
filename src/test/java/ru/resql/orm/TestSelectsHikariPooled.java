package ru.resql.orm;

import com.zaxxer.hikari.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j @TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSelectsHikariPooled extends TestSelectsBase<HikariDataSource> {
	private String poolName;
	private final HikariPoolMXBean poolProxy;
	private int connectionsBefore;

	TestSelectsHikariPooled() throws IOException, SQLException, MalformedObjectNameException {
		super("pgsql_hikari.properties", "pgsql hikari pool", log);

		poolProxy = JMX.newMXBeanProxy(
			ManagementFactory.getPlatformMBeanServer(),
			new ObjectName("com.zaxxer.hikari:type=Pool (" + poolName + ")"),
			HikariPoolMXBean.class
		);
	}

	@Override
	HikariDataSource getDataSource(Properties properties) {
		poolName = properties.getProperty("poolName");
		return new HikariDataSource(new HikariConfig(properties));
	}

	@BeforeEach
	private void storeActiveConnections() {
		// save active connection count before each call to avoid misdetection of connection leakage
		// in each method after buggy one
		connectionsBefore = poolProxy.getActiveConnections();
	}

	@AfterEach
	private void checkNoActiveConnections() {
		// compare active connection count not with zero but with connection count before test method call
		assertEquals(connectionsBefore, poolProxy.getActiveConnections(), "Connection leak detected!");
	}
}
