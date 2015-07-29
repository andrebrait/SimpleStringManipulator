package com.brait;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.brait.configuration.PersistenceConfiguration;

@SpringBootApplication
public class MainApp {

	public static void main(String[] args) {

		if (PersistenceConfiguration.getDB_PASSWORD() == null) {
			Scanner reader = new Scanner(System.in);
			System.out.println("Entre com a senha do banco: ");
			PersistenceConfiguration.setDB_PASSWORD(reader.nextLine());
			reader.close();
		}

		ConfigurableApplicationContext applicationContext = SpringApplication.run(MainApp.class);

		try {
			FileInputStream fis = new FileInputStream(new File("C://tempIria/refazendo-0x10.xlsx"));

			XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

			XSSFSheet mySheet = myWorkBook.getSheetAt(0);

			Iterator<Row> rowIterator = mySheet.iterator();
			rowIterator.next();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
