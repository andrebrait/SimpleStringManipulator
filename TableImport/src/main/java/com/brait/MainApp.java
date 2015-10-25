package com.brait;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import com.brait.model.Enriquecimento;
import com.brait.model.Enriquecimento.EnriquecimentoPk;
import com.brait.model.Go;
import com.brait.model.Proteina;
import com.brait.model.Resultado;
import com.brait.model.Resultado.ResultadoPk;
import com.brait.model.Transcrito;
import com.brait.repository.EnriquecimentoRepository;
import com.brait.repository.GoRepository;
import com.brait.repository.ProteinaRepository;
import com.brait.repository.ResultadoRepository;
import com.brait.repository.TranscritoRepository;

@SpringBootApplication
public class MainApp {

	@Autowired
	private EnriquecimentoRepository enriquecimentoRepository;

	@Autowired
	private ProteinaRepository proteinaRepository;

	@Autowired
	private ResultadoRepository resultadoRepository;

	@Autowired
	private TranscritoRepository transcritoRepository;

	@Autowired
	private GoRepository goRepository;

	public static void main(String[] args) {

		Scanner reader = new Scanner(System.in);

		Runnable runObj = null;
		final MainApp main = new MainApp();

		if (PersistenceConfiguration.getDB_PASSWORD() == null) {
			System.out.println("Entre com a senha do banco: ");
			PersistenceConfiguration.setDB_PASSWORD(reader.nextLine());
			System.out.println("Escolha uma opção: ");
			System.out.println("\t1 - Apagar banco de dados e preencher com valores das tabelas iniciais");
			System.out.println("\t2 - Consultar Uniprot");
			System.out.println("\t3 - Consultar Ensembl");
			int opt = reader.nextInt();

			switch (opt) {
				case 1:
					runObj = new Runnable() {

						@Override
						public void run() {
							main.eraseAndFill();
						}
					};

					break;
				case 2:
					runObj = new Runnable() {

						@Override
						public void run() {
							main.consultaUniprot();
						}
					};
					break;
				case 3:
					runObj = new Runnable() {

						@Override
						public void run() {
							main.consultaEnsembl();
						}
					};
					break;
				default:
			}

			reader.close();
		}

		ConfigurableApplicationContext applicationContext = SpringApplication.run(MainApp.class);
		applicationContext.getAutowireCapableBeanFactory().autowireBean(main);

		runObj.run();
	}

	private void consultaUniprot() {
		System.out.println("Consulta Uniprot");
	}

	private void consultaEnsembl() {
		System.out.println("Consulta Ensembl");
	}

	private void eraseAndFill() {

		// enriquecimentoRepository.deleteAllInBatch();
		// resultadoRepository.deleteAllInBatch();
		// goRepository.deleteAllInBatch();
		// proteinaRepository.deleteAll();
		// transcritoRepository.deleteAll();

		try {

			FileInputStream fisResultados = new FileInputStream(
					new File("C://iria/tabelas-mãe/resultados DE erika.xlsx"));
			XSSFWorkbook resultado = new XSSFWorkbook(fisResultados);
			for (int i = 1; i < 7; i++) {
				XSSFSheet curSheet = resultado.getSheetAt(i);
				for (int j = 3; j <= curSheet.getLastRowNum(); j++) {
					Row curRow = curSheet.getRow(j);
					String codigo = curRow.getCell(0).getStringCellValue();
					Transcrito t = transcritoRepository.findByCodigo(codigo);
					if (t == null) {
						t = transcritoRepository.save(new Transcrito(codigo));
						System.out.println("Inserindo transcrito " + t);
					}
					Long fase1 = (i == 3 || i == 6) ? 5L : 0L;
					Long fase2 = (i == 1 || i == 4) ? 5L : 10L;
					ResultadoPk id = new ResultadoPk(t.getId(), fase1, fase2);
					if (!resultadoRepository.exists(id)) {
						Resultado r = new Resultado(id, getNullSafeBigDecimalValue(curRow.getCell(15), 2),
								getNullSafeBigDecimalValue(curRow.getCell(i == 4 ? 32 : 29), 2),
								i < 4 ? Resultado.TABELA_COMPLETA : Resultado.TABELA_DE,
								getNullSafeBigDecimalValue(curRow.getCell(1), 40));
						r = resultadoRepository.save(r);
						System.out.println("Inserindo resultado " + r);
					}
				}
			}
			resultado.close();

			FileInputStream fisFoldChange = new FileInputStream(new File("C://iria/tabelas-filhas/fold change.xlsx"));
			XSSFWorkbook foldChange = new XSSFWorkbook(fisFoldChange);
			for (int i = 0; i < foldChange.getNumberOfSheets(); i++) {
				XSSFSheet curSheet = foldChange.getSheetAt(i);
				Iterator<Row> rowIterator = curSheet.iterator();
				rowIterator.next();
				while (rowIterator.hasNext()) {
					Row curRow = rowIterator.next();
					String codT = curRow.getCell(0).getStringCellValue();
					String codP = curRow.getCell(1).getStringCellValue();
					Transcrito t = transcritoRepository.findByCodigo(codT);
					Proteina p = proteinaRepository.findByCodigo(codP);
					if (p == null) {
						p = new Proteina(codP);
						p.setSequence(getNullSafeStringValue(curRow.getCell(8)));
						p = proteinaRepository.save(p);
						System.out.println("Inserindo proteina " + p);
					}
					t.setGeneName(getNullSafeStringValue(curRow.getCell(4)));
					if (!t.getProteina().contains(p)) {
						t.getProteina().add(p);
					}
					t = transcritoRepository.save(t);
				}
			}
			foldChange.close();

			FileInputStream fisDe = new FileInputStream(new File("C://iria/tabelas-mãe/enriquecimentos GENES DE.xlsx"));
			XSSFWorkbook de = new XSSFWorkbook(fisDe);
			for (int i = 0; i < de.getNumberOfSheets(); i++) {
				XSSFSheet curSheet = de.getSheetAt(i);
				Iterator<Row> rowIterator = curSheet.iterator();
				rowIterator.next();
				while (rowIterator.hasNext()) {
					Row curRow = rowIterator.next();
					String codGo = curRow.getCell(0).getStringCellValue();
					Go go = goRepository.findByCodigo(codGo);
					if (go == null) {
						go = goRepository.save(new Go(curRow.getCell(0).getStringCellValue(),
								curRow.getCell(1).getStringCellValue(), curRow.getCell(2).getStringCellValue()));
						System.out.println("Inserindo GO " + go);
					}
					List<Enriquecimento> toSave = new ArrayList<>();
					for (String testSeq : StringUtils
							.split(StringUtils.deleteWhitespace(curRow.getCell(6).getStringCellValue()), ",")) {
						Proteina prot = proteinaRepository.findByCodigo(testSeq);
						EnriquecimentoPk eId = new EnriquecimentoPk(go.getId(), prot.getId());
						if (!enriquecimentoRepository.exists(eId)) {
							Enriquecimento e = new Enriquecimento(eId,
									getNullSafeBigDecimalValue(curRow.getCell(3), 30),
									getNullSafeBigDecimalValue(curRow.getCell(4), 30));
							toSave.add(e);
							System.out.println("Inserindo enriquecimento " + e);
						}
					}
					enriquecimentoRepository.save(toSave);
				}
			}
			de.close();

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
