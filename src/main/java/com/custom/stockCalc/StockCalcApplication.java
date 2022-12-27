package com.custom.stockCalc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;

import java.io.File;

@SpringBootApplication
public class StockCalcApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockCalcApplication.class, args);
	}

//	@Bean
	CommandLineRunner run() {
		return args -> {
			Log log = LogFactory.getLog(this.getClass());

			File electron_exe = ResourceUtils.getFile("electron/WinApp.exe");
			String exe_path = electron_exe.getAbsolutePath();
			log.info("exe_path = " + exe_path);

			log.info("=== start exe ===");
			Runtime.getRuntime().exec(exe_path);
		};
	}

}
