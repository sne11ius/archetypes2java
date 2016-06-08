package es.archetyp.archetypes2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Archetypes2ApplicationTests {

	@Test
	public void contextLoads() {
		// Dieser Test stellt nur sicher, dass der Spring-Kontext geladen werden kann
	}

	@Configuration
	public static class Archetypes2TestConfiguration {

	}

}
