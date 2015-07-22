package com.brait;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MainApp {

	public static void main(String[] args) throws IOException {

		Map<String, String> transcriptMap = new HashMap<String, String>();
		List<String> transcriptOrigList = new ArrayList<String>();

		FileInputStream fis = new FileInputStream(new File("C://tempIria/refazendo-0x10.xlsx"));

		XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

		XSSFSheet mySheet = myWorkBook.getSheetAt(0);

		Iterator<Row> rowIterator = mySheet.iterator();
		rowIterator.next();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			transcriptOrigList.add(row.getCell(0).getStringCellValue());
			transcriptMap.put(row.getCell(2).getStringCellValue(),
					row.getCell(3).getStringCellValue());
		}

		myWorkBook.close();

		FileOutputStream fis2 = new FileOutputStream(new File(
				"C://tempIria/refazendo-0x10_ordenado.xlsx"));

		XSSFWorkbook myWorkBook2 = new XSSFWorkbook();

		XSSFSheet mySheet2 = myWorkBook2.createSheet("ORDENADO");

		for (int i = -1; i < transcriptOrigList.size(); i++) {
			Row row = mySheet2.createRow(i + 1);
			if (i == -1) {
				row.createCell(2).setCellValue("Ensembl Transcript ID");
				row.createCell(3).setCellValue("Ensembl Protein ID");
			} else {
				String curTrans = transcriptOrigList.get(i);
				row.createCell(0).setCellValue(curTrans);
				row.createCell(1);
				row.createCell(2).setCellValue(curTrans);
				row.createCell(3).setCellValue(transcriptMap.get(curTrans));
			}
		}
		
		myWorkBook2.write(fis2);
		
		myWorkBook2.close();

	}

}
