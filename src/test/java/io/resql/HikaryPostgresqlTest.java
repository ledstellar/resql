package io.resql;

import com.zaxxer.hikari.HikariConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.HashMap;


@RunWith( SpringRunner.class )
@ContextConfiguration( classes = HikaryPostgresqlTest.class )
@TestExecutionListeners( listeners = { DependencyInjectionTestExecutionListener.class } )
public class HikaryPostgresqlTest {
	@Autowired private HikariConfig hikariConfig;

	private static final Logger log = LoggerFactory.getLogger( HikaryPostgresqlTest.class );

//	DbManager db = new DbManager( hikariConfig, log );

	@Bean private HikariConfig getHikariConfig() {
		final HikariConfig hc = new HikariConfig();
		return hc;
	}

    @Test
    public void testAlive() {
	    HashMap h = new HashMap< Integer, Integer >( 33 );
	    log.info( "JDBC url: " + hikariConfig.getJdbcUrl() );
    }
}
