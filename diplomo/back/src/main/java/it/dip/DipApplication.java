package it.dip;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStream;
import java.io.StringWriter;

@SpringBootApplication
public class DipApplication  {

    public static void main(String[] args) {
        SpringApplication.run(DipApplication.class, args);
    }

}
