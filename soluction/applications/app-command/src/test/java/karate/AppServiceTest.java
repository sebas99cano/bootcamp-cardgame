package karate;

import com.intuit.karate.junit5.Karate;
import org.example.cardgame.application.command.AppCommand;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = AppCommand.class)
class AppServiceTest {
    @Karate.Test
    Karate commands() {
        return Karate.run("classpath:karate/command/juego.feature");
    }
}