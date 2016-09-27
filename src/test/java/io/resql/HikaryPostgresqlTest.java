package io.resql;

import com.zaxxer.hikari.HikariConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;


@RunWith( SpringRunner.class )
@ContextConfiguration( classes = HikaryPostgresqlTest.class )
@TestExecutionListeners( listeners = {DependencyInjectionTestExecutionListener.class} )
@lombok.extern.slf4j.Slf4j
public class HikaryPostgresqlTest {
	@Autowired
	private HikariConfig hikariConfig;

//	DbManager db = new DbManager( hikariConfig, log );

	@Bean private HikariConfig getHikariConfig() {
		return new HikariConfig();
	}

    @Test
    public void testAlive() {
        log.info( "Hello world: " + hikariConfig);
    }
}
