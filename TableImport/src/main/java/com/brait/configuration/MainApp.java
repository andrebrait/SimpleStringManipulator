package com.brait.configuration;

import java.util.Scanner;

import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class MainApp {

	public static void main(String[] args) {
		
		if(PersistenceConfiguration.getDB_PASSWORD() == null){
			Scanner reader = new Scanner(System.in);
			System.out.println("Entre com a senha do banco: ");
			PersistenceConfiguration.setDB_PASSWORD(reader.nextLine());
			reader.close();
		}
		
		

	}

}
