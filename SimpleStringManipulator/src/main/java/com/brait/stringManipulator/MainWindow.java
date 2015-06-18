package com.brait.stringManipulator;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.Toolkit;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.UIManager;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextPane;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import javax.swing.JSeparator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import javax.swing.KeyStroke;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

public class MainWindow {

	private JFrame frmSimpleStringManipulator;

	private static final String VERSION = "0.0.1";

	public static File OPEN_FILE = null;
	public static Charset OPEN_CHAR_SET = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					try {
						UIManager.setLookAndFeel(UIManager
								.getSystemLookAndFeelClassName());
					} catch (Exception e) {
						UIManager.setLookAndFeel(UIManager
								.getCrossPlatformLookAndFeelClassName());
					}
					MainWindow window = new MainWindow();
					window.frmSimpleStringManipulator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSimpleStringManipulator = new JFrame();
		frmSimpleStringManipulator
				.setIconImage(Toolkit
						.getDefaultToolkit()
						.getImage(
								MainWindow.class
										.getResource("/javax/swing/plaf/metal/icons/ocean/menu.gif")));
		frmSimpleStringManipulator.setTitle("Simple String Manipulator "
				+ VERSION);
		frmSimpleStringManipulator.setSize(800, 600);
		frmSimpleStringManipulator.setLocationRelativeTo(null);
		frmSimpleStringManipulator
				.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		GroupLayout groupLayout = new GroupLayout(
				frmSimpleStringManipulator.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(
				Alignment.TRAILING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE,
								794, Short.MAX_VALUE).addGap(0)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(
				Alignment.TRAILING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE,
								551, Short.MAX_VALUE).addGap(0)));

		JTextPane textPane = new JTextPane();
		textPane.setBorder(UIManager.getBorder("TextPane.border"));
		scrollPane.setViewportView(textPane);
		frmSimpleStringManipulator.getContentPane().setLayout(groupLayout);

		JMenuBar menuBar = new JMenuBar();
		frmSimpleStringManipulator.setJMenuBar(menuBar);

		JMenu mnArquivo = new JMenu("Arquivo");
		menuBar.add(mnArquivo);

		JMenuItem mntmAbrir = new JMenuItem("Abrir");
		mntmAbrir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		mnArquivo.add(mntmAbrir);
		mntmAbrir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser abrir = new JFileChooser();
				abrir.setSelectedFile(OPEN_FILE);
				abrir.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int retorno = abrir.showOpenDialog(frmSimpleStringManipulator);
				if (retorno == JFileChooser.APPROVE_OPTION) {
					try {
						byte[] encoded = Files.readAllBytes(abrir
								.getSelectedFile().toPath());
						textPane.setText(new String(encoded, detectCharSet(
								abrir.getSelectedFile(), true)));
						OPEN_FILE = abrir.getSelectedFile();
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(
								frmSimpleStringManipulator,
								"Não foi possível abrir o arquivo.", "Erro",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		JMenuItem mntmSalvar = new JMenuItem("Salvar");
		mntmSalvar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		mnArquivo.add(mntmSalvar);
		mntmSalvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (OPEN_FILE != null && OPEN_FILE.exists()) {
					saveTextFile(OPEN_FILE, textPane.getText());
				} else {
					saveTextFileSalvarComo(textPane);
				}
			}
		});

		JMenuItem mntmSalvarComo = new JMenuItem("Salvar como...");
		mntmSalvarComo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
				InputEvent.CTRL_MASK));
		mntmSalvarComo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveTextFileSalvarComo(textPane);
			}
		});
		mnArquivo.add(mntmSalvarComo);

		JSeparator separator = new JSeparator();
		mnArquivo.add(separator);

		JMenuItem mntmSair = new JMenuItem("Sair");
		mnArquivo.add(mntmSair);
		mntmSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmSimpleStringManipulator.dispose();
			}
		});

		JMenu mnAo = new JMenu("A\u00E7\u00E3o");
		menuBar.add(mnAo);

		JMenuItem mntmQuebrarEmLinhas = new JMenuItem("Quebrar em linhas");
		mntmQuebrarEmLinhas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = JOptionPane
						.showInputDialog(
								frmSimpleStringManipulator,
								"Escolha um ou mais caracteres para marcar a quebra das linhas.",
								"Quebrar em linhas", JOptionPane.PLAIN_MESSAGE);
				if (StringUtils.isNotEmpty(str)) {
					textPane.setText(StringUtils.replace(textPane.getText(),
							str, System.lineSeparator()));
				}
			}
		});
		mnAo.add(mntmQuebrarEmLinhas);

		JMenuItem mntmRemoverIntervalos = new JMenuItem("Remover intervalos");
		mntmRemoverIntervalos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = JOptionPane
						.showInputDialog(
								frmSimpleStringManipulator,
								"Digite os caracteres limitantes do texto a remover separados por espaço. (Ex.: digitar '[ ]' faz com que tudo entre colchetes seja removido do texto)",
								"Remover intervalos", JOptionPane.PLAIN_MESSAGE);
				if (StringUtils.isNotEmpty(str)) {
					String[] separadoresDisjuntos = StringUtils.split(str);
					if (separadoresDisjuntos.length == 2) {
						int inicio = 0;
						int fim = 0;
						String texto = textPane.getText();
						for (;;) {
							try {
								fim = texto.indexOf(separadoresDisjuntos[1]);
								inicio = texto.substring(0, fim).lastIndexOf(
										separadoresDisjuntos[0]);
								texto = texto.substring(0, inicio)
										+ texto.substring(fim + 1);
							} catch (IndexOutOfBoundsException ex) {
								if (fim == -1) {
									break;
								}
							}
						}
						textPane.setText(texto);
					}
				}
			}
		});
		mnAo.add(mntmRemoverIntervalos);

		JMenuItem mntmSubstituir = new JMenuItem("Substituir");
		mnAo.add(mntmSubstituir);
	}

	private void saveTextFileSalvarComo(JTextPane textPane) {
		JFileChooser salvarComo = new JFileChooser();
		salvarComo.setSelectedFile(OPEN_FILE != null ? OPEN_FILE : new File(
				"*.txt"));
		int retorno = salvarComo.showSaveDialog(frmSimpleStringManipulator);
		if (retorno == JFileChooser.APPROVE_OPTION) {
			File file = !salvarComo.getSelectedFile().exists()
					&& StringUtils.startsWithIgnoreCase(
							System.getProperty("os.name"), "Windows")
					&& !StringUtils.contains(salvarComo.getSelectedFile()
							.getPath(), ".") ? new File(
					salvarComo.getSelectedFile() + ".txt") : salvarComo
					.getSelectedFile();
			saveTextFile(file, textPane.getText());
		}
	}

	private void saveTextFile(File file, String text) {
		try (BufferedWriter bw = new BufferedWriter(new PrintWriter(file,
				detectCharSet(file, false).name()))) {
			bw.write(text);
			OPEN_FILE = file;
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(frmSimpleStringManipulator,
					"Não foi possível salvar o arquivo.", "Erro",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private static Charset detectCharSet(File file, boolean opening)
			throws IOException {
		if (OPEN_FILE != null && OPEN_CHAR_SET != null && !opening) {
			return OPEN_CHAR_SET;
		}
		if (file == null || !file.exists()) {
			return StandardCharsets.UTF_8;
		}
		UniversalDetector charSetDetector = new UniversalDetector(null);
		byte[] buf = new byte[4096];
		FileInputStream fis = new FileInputStream(file);
		int nread;
		while ((nread = fis.read(buf)) > 0 && !charSetDetector.isDone()) {
			charSetDetector.handleData(buf, 0, nread);
		}
		charSetDetector.dataEnd();
		String encoding = charSetDetector.getDetectedCharset();
		charSetDetector.reset();
		fis.close();
		OPEN_CHAR_SET = encoding != null && Charset.isSupported(encoding) ? Charset
				.forName(encoding) : StandardCharsets.UTF_8;
		return OPEN_CHAR_SET;
	}
}
