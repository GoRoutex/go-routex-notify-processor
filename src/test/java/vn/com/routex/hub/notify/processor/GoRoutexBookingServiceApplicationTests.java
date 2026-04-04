package vn.com.routex.hub.notify.processor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=" +
				"org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration," +
				"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
		"spring.kafka.bootstrap-servers=localhost:9092",
		"spring.kafka.consumer.group-id=test",
		"spring.kafka.listener.auto-startup=false"
})
class GoRoutexBookingServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
