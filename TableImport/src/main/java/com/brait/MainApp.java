package com.brait;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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

	public static String MONODELPHIS = "Monodelphis domestica";
	public static String MONODELPHIS_IMPORTED_ENRIQ = MONODELPHIS + " (imported Enriquecimentos)";
	public static String OTHER = "OTHER";

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
		int opt = 0;

		if (PersistenceConfiguration.getDB_PASSWORD() == null) {
			System.out.println("Escolha uma opção: ");
			System.out.println("\t1 - Apagar banco de dados e preencher com valores das tabelas iniciais");
			System.out.println("\t2 - Atualizar banco com os valores das tabelas");
			System.out.println("\t3 - Atualizar banco com novos dados da tabela fold change");
			System.out.println("\t4 - Consultar Uniprot");
			System.out.println("\t5 - Consultar Ensembl");
			System.out.println("\t6 - Gerar FASTA");
			System.out.println("\t7 - Completar tabelas filhas 6 e 7");
			opt = reader.nextInt();

			switch (opt) {
				case 1:
					runObj = () -> main.eraseAndFill();
					break;
				case 2:
					runObj = () -> main.updateTables();
					break;
				case 3:
					runObj = () -> main.updateFoldChange();
					break;
				case 4:
					runObj = () -> main.consultaUniprot();
					break;
				case 5:
					runObj = () -> main.consultaEnsembl();
					break;
				case 6:
					runObj = () -> main.gerarFasta();
					break;
				case 7:
					runObj = () -> main.completaTabelasFilhas();
					break;
				default:
					reader.close();
					return;
			}
		}

		if (opt != 6) {
			System.out.println("Entre com a senha do banco: ");
			reader.nextLine();
			PersistenceConfiguration.setDB_PASSWORD(reader.nextLine());
			ConfigurableApplicationContext applicationContext = SpringApplication.run(MainApp.class);
			applicationContext.getAutowireCapableBeanFactory().autowireBean(main);
		}

		reader.close();

		runObj.run();
	}

	private void consultaUniprot() {
		List<Transcrito> allTrans = transcritoRepository.findAll();
		for (Transcrito t : allTrans) {

		}
	}

	private void consultaEnsembl() {
		System.out.println("Consulta Ensembl");
	}

	private void gerarFasta() {
		FileInputStream fisResultados;
		BufferedWriter writer;
		try {
			System.out.println("Gerando fastas");
			fisResultados = new FileInputStream(new File("C://iria/genes para rede.xlsx"));
			XSSFWorkbook resultado = new XSSFWorkbook(fisResultados);
			for (int i = 0; i < resultado.getNumberOfSheets(); i++) {
				XSSFSheet curSheet = resultado.getSheetAt(i);
				String fileName = curSheet.getSheetName() + ".fasta";
				writer = new BufferedWriter(new FileWriter(new File("C://iria/" + fileName)));
				for (int j = 0; j <= curSheet.getLastRowNum(); j++) {
					Row curRow = curSheet.getRow(j);
					writer.write(">" + getNullSafeStringValue(curRow.getCell(0)));
					writer.newLine();
					if (StringUtils.isNotBlank(getNullSafeStringValue(curRow.getCell(1)))) {
						writer.write(getNullSafeStringValue(curRow.getCell(1)));
						writer.newLine();
					}
				}
				writer.close();
			}
			resultado.close();
			fisResultados.close();
			System.out.println("Gerar fastas terminado");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateTables() {
		try {
			System.out.println("Iniciando importação");
			System.out.println("Consultado resultados DE erica.xlsx");
			FileInputStream fisResultados = new FileInputStream(
					new File("C://iria/tabelas-mãe/resultados DE erika.xlsx"));
			XSSFWorkbook resultado = new XSSFWorkbook(fisResultados);
			for (int i = 1; i < 7; i++) {
				XSSFSheet curSheet = resultado.getSheetAt(i);
				for (int j = 3; j <= curSheet.getLastRowNum(); j++) {
					Row curRow = curSheet.getRow(j);
					String codigo = curRow.getCell(0).getStringCellValue();
					Long transcritoId;
					if (!transcritoRepository.existsByCodigo(codigo)) {
						Transcrito t = transcritoRepository.save(new Transcrito(codigo));
						transcritoId = t.getId();
						System.out.println("Inserindo transcrito " + t);
					} else {
						transcritoId = transcritoRepository.findIdByCodigo(codigo);
					}
					Long fase1 = (i == 3 || i == 6) ? 5L : 0L;
					Long fase2 = (i == 1 || i == 4) ? 5L : 10L;
					ResultadoPk id = new ResultadoPk(transcritoId, fase1, fase2);
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

			System.out.println("Consultado fold change.xlsx");
			FileInputStream fisFoldChange = new FileInputStream(new File("C://iria/tabelas-filhas/fold change.xlsx"));
			XSSFWorkbook foldChange = new XSSFWorkbook(fisFoldChange);
			for (int i = 0; i < foldChange.getNumberOfSheets(); i++) {
				XSSFSheet curSheet = foldChange.getSheetAt(i);
				Iterator<Row> rowIterator = curSheet.iterator();
				rowIterator.next();
				Map<String, Transcrito> transMap = new LinkedHashMap<>();
				Map<String, Proteina> protMap = new LinkedHashMap<>();
				while (rowIterator.hasNext()) {
					Row curRow = rowIterator.next();
					String codT = curRow.getCell(0).getStringCellValue();
					String codP = curRow.getCell(1).getStringCellValue();
					Transcrito t;
					if (!transMap.containsKey(codT)) {
						t = transcritoRepository.findByCodigoFetchProteina(codT);
						transMap.put(codT, t);
					} else {
						t = transMap.get(codT);
					}
					Proteina p;
					String sequence = getNullSafeStringValue(curRow.getCell(8));
					if (!protMap.containsKey(codP)) {
						if (!proteinaRepository.existsByCodigo(codP)) {
							p = new Proteina(codP);
							p.setSequence(sequence);
							System.out.println("Inserindo proteina " + p);
						} else {
							p = proteinaRepository.findByCodigo(codP);
							if (StringUtils.isBlank(p.getSequence())) {
								p.setSequence(sequence);
								System.out.println("Atualizando proteina " + p);
							}
						}
						protMap.put(codP, p);
					} else {
						p = protMap.get(codP);
					}
					String geneName = getNullSafeStringValue(curRow.getCell(4));
					if (StringUtils.isBlank(t.getGeneName()) && StringUtils.isNotBlank(geneName)) {
						CellStyle cs = curRow.getCell(4).getCellStyle();
						if (cs.getFillBackgroundColorColor() == null) {
							t.setOrganism(MONODELPHIS);
						} else {
							t.setOrganism(OTHER);
						}
						t.setGeneName(geneName);
						System.out.println("Atualizando transcrito " + t);
					}
					if (!containsByCodigo(t.getProteina(), p)) {
						t.getProteina().add(p);
						System.out.println("Adicionando " + p + " ao transcrito " + t);
					}
				}
				proteinaRepository.save(new ArrayList<>(protMap.values()));
				transcritoRepository.save(new ArrayList<>(transMap.values()));
			}
			foldChange.close();

			processEnriq("DE");

			processEnriq("EXCLUSIVOS");

			updateFoldChange();

			System.out.println("Import terminado");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean containsByCodigo(List<Proteina> list, Proteina p) {
		Iterator<Proteina> i = list.iterator();
		while (i.hasNext()) {
			if (i.next().getCodigo().equals(p.getCodigo())) {
				return true;
			}
		}
		return false;
	}

	public void processEnriq(String file) throws IOException {
		System.out.println("Consultado enriquecimentos GENES " + file + ".xlsx");
		FileInputStream fisDe = new FileInputStream(
				new File("C://iria/tabelas-mãe/enriquecimentos GENES " + file + ".xlsx"));
		XSSFWorkbook de = new XSSFWorkbook(fisDe);
		for (int i = 0; i < de.getNumberOfSheets(); i++) {
			XSSFSheet curSheet = de.getSheetAt(i);
			Iterator<Row> rowIterator = curSheet.iterator();
			rowIterator.next();
			while (rowIterator.hasNext()) {
				Row curRow = rowIterator.next();
				String codGo = curRow.getCell(0).getStringCellValue();
				Long goId;
				if (!goRepository.existsByCodigo(codGo)) {
					Go go = goRepository
							.save(new Go(curRow.getCell(0).getStringCellValue(), curRow.getCell(1).getStringCellValue(),
									curRow.getCell(2).getStringCellValue(), MONODELPHIS_IMPORTED_ENRIQ));
					goId = go.getId();
					System.out.println("Inserindo GO " + go);
				} else {
					goId = goRepository.findIdByCodigo(codGo);
				}
				List<Enriquecimento> toSave = new ArrayList<>();
				List<String> protCodigos = Arrays.asList(StringUtils
						.split(StringUtils.deleteWhitespace(getNullSafeStringValue(curRow.getCell(6))), ","));
				if (CollectionUtils.isNotEmpty(protCodigos)) {
					List<Long> protIds = proteinaRepository.findIdsByCodigos(protCodigos);
					for (Long protId : protIds) {
						EnriquecimentoPk eId = new EnriquecimentoPk(goId, protId);
						if (!enriquecimentoRepository.exists(eId)) {
							Enriquecimento e = new Enriquecimento(eId,
									getNullSafeBigDecimalValue(curRow.getCell(3), 30),
									getNullSafeBigDecimalValue(curRow.getCell(4), 30));
							toSave.add(e);
							System.out.println("Inserindo enriquecimento " + e);
						}
					}
				}
				enriquecimentoRepository.save(toSave);
			}
		}
		de.close();
	}

	public void updateFoldChange() {
		try {
			System.out.println("Consultado fold change.xlsx (Fase de novos GOs e resultados)");
			FileInputStream fisFoldChange;
			XSSFWorkbook foldChange;
			fisFoldChange = new FileInputStream(new File("C://iria/tabelas-filhas/fold change.xlsx"));
			foldChange = new XSSFWorkbook(fisFoldChange);
			for (int i = 0; i < foldChange.getNumberOfSheets(); i++) {
				XSSFSheet curSheet = foldChange.getSheetAt(i);
				Iterator<Row> rowIterator = curSheet.iterator();
				rowIterator.next();
				while (rowIterator.hasNext()) {
					Row curRow = rowIterator.next();
					String codT = curRow.getCell(0).getStringCellValue();
					String codP = curRow.getCell(1).getStringCellValue();
					Long protId = proteinaRepository.findIdByCodigo(codP);
					String geneName = getNullSafeStringValue(curRow.getCell(4));
					if (StringUtils.isBlank(transcritoRepository.findGenenameByCodigo(codT))
							&& StringUtils.isNotBlank(geneName)) {
						CellStyle cs = curRow.getCell(4).getCellStyle();
						Transcrito t = transcritoRepository.findByCodigo(codT);
						if (cs.getFillBackgroundColorColor() == null) {
							t.setOrganism(MONODELPHIS);
						} else {
							t.setOrganism(OTHER);
						}
						transcritoRepository.save(t);
						System.out.println("Atualizando transcrito " + t);
					}
					for (int j = 5; j < 8; j++) {
						String entradas = getNullSafeStringValue(curRow.getCell(j));
						if (StringUtils.isNotBlank(entradas)) {
							String organism;
							CellStyle cs = curRow.getCell(j).getCellStyle();
							if (cs.getFillBackgroundColorColor() == null) {
								organism = MONODELPHIS;
							} else {
								organism = OTHER;
							}
							if (StringUtils.isNotBlank(entradas)) {
								int matchesGo = StringUtils.countMatches(entradas, "[GO:");
								if (matchesGo > 1 && matchesGo > StringUtils.countMatches(entradas, ";") + 1) {
									entradas = StringUtils.replace(entradas, "]", "];");
								}
								String[] entradasNonTreated = StringUtils.split(entradas, ";");
								List<String> entradasTreated = new ArrayList<String>();
								for (int k = 0; k < entradasNonTreated.length; k++) {
									if (StringUtils.isNotBlank(entradasNonTreated[k])) {
										if (!StringUtils.contains(entradasNonTreated[k], "[GO:")) {
											entradasNonTreated[k] = entradasNonTreated[k] + entradasNonTreated[k + 1];
											entradasNonTreated[k + 1] = null;
										}
										entradasTreated.add(entradasNonTreated[k]);
									}
								}
								for (String entrada : entradasTreated) {
									String codGo = StringUtils.substringBetween(entrada, "[GO:", "]");
									codGo = "GO:" + codGo;
									Long goId;
									if (!goRepository.existsByCodigo(codGo)) {
										Go go = goRepository.save(new Go(codGo,
												StringUtils.trim(StringUtils.substringBefore(entrada, "[GO:")),
												j == 5 ? "P" : j == 6 ? "F" : "C", organism));
										goId = go.getId();
										System.out.println("Inserindo GO " + go);
									} else {
										goId = goRepository.findIdByCodigo(codGo);
									}
									EnriquecimentoPk eId = new EnriquecimentoPk(goId, protId);
									if (!enriquecimentoRepository.exists(eId)) {
										Enriquecimento e = new Enriquecimento(eId, BigDecimal.ZERO, BigDecimal.ZERO);
										enriquecimentoRepository.save(e);
										System.out.println("Inserindo enriquecimento " + e);
									}
								}
							}
						}
					}
				}
			}
			System.out.println("Atualização usando o fold change terminada");
			foldChange.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void eraseAndFill() {

		enriquecimentoRepository.deleteAllInBatch();
		resultadoRepository.deleteAllInBatch();
		goRepository.deleteAllInBatch();
		proteinaRepository.deleteAllInBatch();
		transcritoRepository.deleteAllInBatch();

		updateTables();
	}

	private void completaTabelasFilhas() {
		try {
			System.out.println("Iniciando preenchimento das tabelas filhas 6 e 7");
			FileInputStream fisTab6 = new FileInputStream(
					new File("C://iria/tabelas-filhas/table 6 - Enriched differentially expressed.xlsx"));
			XSSFWorkbook tab6 = new XSSFWorkbook(fisTab6);
			for (int i = 0; i < tab6.getNumberOfSheets(); i++) {
				XSSFSheet curSheet = tab6.getSheetAt(i);
				String nomePasta = curSheet.getSheetName();
				String[] partes = StringUtils.split(nomePasta);
				String mudanca = partes[0].toUpperCase();
				Long fase1 = Long.parseLong(StringUtils.substringBefore(partes[1], "x"));
				Long fase2 = Long.parseLong(StringUtils.substringAfter(partes[1], "x"));
				Iterator<Row> rowIterator = curSheet.iterator();
				rowIterator.next(); // pula cabeçalho
				while (rowIterator.hasNext()) {
					Row curRow = rowIterator.next();
					String term = StringUtils.upperCase(getNullSafeStringValue(curRow.getCell(1)));
					if (StringUtils.isNotBlank(term)) {
						StringBuilder sb = new StringBuilder();
						for (String prot : proteinaRepository.findCodigoByResultadoGoTerm(fase1, fase2, mudanca,
								term)) {
							sb.append(prot);
							sb.append(", ");
						}
						String toCell = sb.toString();
						toCell = StringUtils.removeEnd(toCell, ", ");
						curRow.getCell(2, Row.CREATE_NULL_AS_BLANK).setCellValue(toCell);
					}
				}
			}

			fisTab6.close();

			FileOutputStream fout = new FileOutputStream(
					new File("C://iria/tabelas-filhas/table 6 - Enriched differentially expressed.xlsx"));

			tab6.write(fout);
			tab6.close();
			fout.close();

			FileInputStream fisTab7 = new FileInputStream(
					new File("C://iria/tabelas-filhas/table 7 - Exclusive expressed genes.xlsx"));
			XSSFWorkbook tab7 = new XSSFWorkbook(fisTab7);
			for (int i = 0; i < tab7.getNumberOfSheets(); i++) {
				XSSFSheet curSheet = tab7.getSheetAt(i);
				Long fase1 = i <= 2 || i == 4 ? 0L : 5L;
				Long fase2 = i == 0 || i == 2 ? 5L : 10L;
				String mudanca = "EXCL " + (i <= 1 || i == 3 ? fase1 : fase2);
				Iterator<Row> rowIterator = curSheet.iterator();
				rowIterator.next(); // pula cabeçalho
				while (rowIterator.hasNext()) {
					Row curRow = rowIterator.next();
					String term = StringUtils.upperCase(getNullSafeStringValue(curRow.getCell(1)));
					if (StringUtils.isNotBlank(term)) {
						StringBuilder sb = new StringBuilder();
						for (String prot : proteinaRepository.findCodigoByResultadoGoTerm(fase1, fase2, mudanca,
								term)) {
							sb.append(prot);
							sb.append(", ");
						}
						String toCell = sb.toString();
						toCell = StringUtils.removeEnd(toCell, ", ");
						curRow.getCell(2, Row.CREATE_NULL_AS_BLANK).setCellValue(toCell);
					}
				}
			}

			fisTab7.close();

			FileOutputStream fout7 = new FileOutputStream(
					new File("C://iria/tabelas-filhas/table 7 - Exclusive expressed genes.xlsx"));

			tab7.write(fout7);
			tab7.close();
			fout7.close();

			System.out.println("Terminado!");

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
