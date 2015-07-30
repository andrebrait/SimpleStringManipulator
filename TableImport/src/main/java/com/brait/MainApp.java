package com.brait;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.brait.configuration.PersistenceConfiguration;
import com.brait.model.De;
import com.brait.model.De.DePk;
import com.brait.model.Ensembl;
import com.brait.model.Exclusivo;
import com.brait.model.Exclusivo.ExPk;
import com.brait.model.FoldChange;
import com.brait.model.FoldChange.FoldPk;
import com.brait.model.Go;
import com.brait.repository.DeRepository;
import com.brait.repository.EnsemblRepository;
import com.brait.repository.ExclusivoRepository;
import com.brait.repository.FoldChangeRepository;
import com.brait.repository.GoRepository;

@SpringBootApplication
public class MainApp {

	@Autowired
	private DeRepository deRepository;

	@Autowired
	private EnsemblRepository ensemblRepository;

	@Autowired
	private ExclusivoRepository exclusivoRepository;

	@Autowired
	private FoldChangeRepository foldChangeRepository;

	@Autowired
	private GoRepository goRepository;

	public static void main(String[] args) {

		if (PersistenceConfiguration.getDB_PASSWORD() == null) {
			Scanner reader = new Scanner(System.in);
			System.out.println("Entre com a senha do banco: ");
			PersistenceConfiguration.setDB_PASSWORD(reader.nextLine());
			reader.close();
		}

		ConfigurableApplicationContext applicationContext = SpringApplication.run(MainApp.class);
		MainApp main = new MainApp();

		applicationContext.getAutowireCapableBeanFactory().autowireBean(main);

		main.readAll();

	}

	private void readAll() {

		deRepository.deleteAllInBatch();
		exclusivoRepository.deleteAllInBatch();
		goRepository.deleteAllInBatch();
		foldChangeRepository.deleteAllInBatch();
		ensemblRepository.deleteAllInBatch();

		try {
			FileInputStream fisFoldChange = new FileInputStream(new File("C://iria/tabelas-filhas/fold change.xlsx"));
			XSSFWorkbook foldChange = new XSSFWorkbook(fisFoldChange);
			for (int i = 0; i < foldChange.getNumberOfSheets(); i++) {
				XSSFSheet curSheet = foldChange.getSheetAt(i);
				String[] fields = StringUtils.split(curSheet.getSheetName(), "x");
				Iterator<Row> rowIterator = curSheet.iterator();
				rowIterator.next();
				while (rowIterator.hasNext()) {
					Row curRow = rowIterator.next();
					Ensembl e = null;
					e = new Ensembl(curRow.getCell(1).getStringCellValue(), curRow.getCell(0).getStringCellValue());
					if (!ensemblRepository.exists(e.getEnsembl_p())) {
						e = ensemblRepository.save(e);
					}
					FoldPk id = new FoldPk(e.getEnsembl_p(), "DOWN", fields[0], fields[1]);
					if (!foldChangeRepository.exists(id)) {
						FoldChange f = new FoldChange(id, getNullSafeBigDecimalValue(curRow.getCell(2), 8), getNullSafeBigDecimalValue(curRow.getCell(3), 8),
								getNullSafeStringValue(curRow.getCell(4)), getNullSafeStringValue(curRow.getCell(8)));
						f = foldChangeRepository.save(f);
					} else {
						System.out.println("Registro duplicado: " + id.toString());
					}
				}
			}
			foldChange.close();

			FileInputStream fisDe = new FileInputStream(new File("C://iria/tabelas-mãe/enriquecimentos GENES DE.xlsx"));
			XSSFWorkbook de = new XSSFWorkbook(fisDe);
			for (int i = 0; i < de.getNumberOfSheets(); i++) {
				XSSFSheet curSheet = de.getSheetAt(i);
				String[] fields = StringUtils.split(curSheet.getSheetName(), null);
				String[] fases = StringUtils.split(StringUtils.remove(fields[2], "T"), "X");
				Iterator<Row> rowIterator = curSheet.iterator();
				rowIterator.next();
				while (rowIterator.hasNext()) {
					Row curRow = rowIterator.next();
					Go go = new Go(curRow.getCell(0).getStringCellValue(), curRow.getCell(1).getStringCellValue(), curRow.getCell(2).getStringCellValue());
					if (!goRepository.exists(go.getGoid())) {
						go = goRepository.save(go);
					}
					for (String testSeq : StringUtils.split(StringUtils.deleteWhitespace(curRow.getCell(6).getStringCellValue()), ",")) {
						DePk id = new DePk(go.getGoid(), testSeq, fields[1], fases[0], fases[1]);
						if (!deRepository.exists(id)) {
							deRepository.save(new De(id, getNullSafeBigDecimalValue(curRow.getCell(3), 30), getNullSafeBigDecimalValue(curRow.getCell(4), 30), curRow.getCell(5).getStringCellValue()));
						}
					}
				}
			}
			de.close();

			FileInputStream fisEx = new FileInputStream(new File("C://iria/tabelas-mãe/enriquecimentos GENES EXCLUSIVOS.xlsx"));
			XSSFWorkbook ex = new XSSFWorkbook(fisEx);
			for (int i = 0; i < ex.getNumberOfSheets(); i++) {
				XSSFSheet curSheet = ex.getSheetAt(i);
				String[] fases = StringUtils.split(StringUtils.remove(StringUtils.remove(StringUtils.split(curSheet.getSheetName(), null)[3], ")"), "T"), "(");
				Iterator<Row> rowIterator = curSheet.iterator();
				rowIterator.next();
				while (rowIterator.hasNext()) {
					Row curRow = rowIterator.next();
					Go go = new Go(curRow.getCell(0).getStringCellValue(), curRow.getCell(1).getStringCellValue(), curRow.getCell(2).getStringCellValue());
					if (!goRepository.exists(go.getGoid())) {
						go = goRepository.save(go);
					}
					for (String testSeq : StringUtils.split(StringUtils.deleteWhitespace(curRow.getCell(6).getStringCellValue()), ",")) {
						ExPk id = new ExPk(go.getGoid(), testSeq, fases[1], fases[0]);
						if (!exclusivoRepository.exists(id)) {
							exclusivoRepository.save(new Exclusivo(id, getNullSafeBigDecimalValue(curRow.getCell(3), 30), getNullSafeBigDecimalValue(curRow.getCell(4), 30), curRow.getCell(5)
									.getStringCellValue()));
						}
					}
				}
			}
			ex.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getNullSafeStringValue(Cell cell) {
		try {
			return cell.getStringCellValue();
		} catch (NullPointerException e) {
			return null;
		}
	}

	private BigDecimal getNullSafeBigDecimalValue(Cell cell, Integer scale) {
		try {
			return new BigDecimal(Double.toString(cell.getNumericCellValue())).setScale(scale, RoundingMode.HALF_EVEN);
		} catch (NumberFormatException | IllegalStateException ex) {
			return BigDecimal.ZERO;
		}
	}
}
