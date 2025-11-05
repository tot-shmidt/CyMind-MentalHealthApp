package cymind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Yaroslav Ziabkin
 * @author Sean Krueger
 */

@SpringBootApplication
@ComponentScan(basePackages = "cymind")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}