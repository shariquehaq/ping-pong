package com.techday.reservation.pingpong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "me.ramswaroop.jbot", "com.techday.reservation.pingpong"
})
public class PingpongApplication {

    public static void main(String[] args) {
        SpringApplication.run(PingpongApplication.class, args);
    }

}

