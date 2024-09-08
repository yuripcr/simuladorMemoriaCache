package GUI;

import Init.Simulador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class App extends JFrame {
    private JPanel mainPanel;
    private JButton configButton;
    private JButton testeButton;
    private JButton iniciarButton;
    private JPanel subPanel;
    private JRadioButton randomRadioButton;
    private JRadioButton LFURadioButton;
    private JRadioButton LRURadioButton;
    private JRadioButton FIFORadioButton;
    private JPanel mapPanel;
    private JRadioButton DiretoRadioButton;
    private JRadioButton AssociativoRadioButton;
    private JRadioButton AssociativoConjuntoRadioButton;
    private JLabel configLabel;
    private JLabel testeLabel;
    private JTextField nConj;
    private JLabel textConj;

    private String tipoMap;
    private String tipoSubs;
    private int linhasConjunto;

    private String configPath;
    private String testPath;

    public App() {
        File configFile = new File("src/data/config.txt");
        configPath = configFile.getAbsolutePath();
        File testFile = new File("src/data/teste_1.txt");
        testPath = testFile.getAbsolutePath();
        configLabel.setText(configPath);
        testeLabel.setText(testPath);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
        randomRadioButton.setSelected(true);
        DiretoRadioButton.setSelected(true);
        textConj.setVisible(false);
        nConj.setVisible(false);
        tipoMap = "Direto";
        tipoSubs = "Random";
        subPanel.setVisible(false);
        JFileChooser fileChooser = new JFileChooser();

        DiretoRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tipoMap = "Direto";
                subPanel.setVisible(false);
            }
        });

        AssociativoRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tipoMap = "Associativo";
                subPanel.setVisible(true);
                textConj.setVisible(false);
                nConj.setVisible(false);
            }
        });

        AssociativoConjuntoRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tipoMap = "Conjunto";
                subPanel.setVisible(true);
                textConj.setVisible(true);
                nConj.setVisible(true);
            }
        });

        randomRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tipoSubs = "Random";
            }
        });

        FIFORadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tipoSubs = "FIFO";
            }
        });

        LRURadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tipoSubs = "LRU";
            }
        });

        LFURadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tipoSubs = "LFU";
            }
        });

        configButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = fileChooser.showOpenDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {
                    configPath = fileChooser.getSelectedFile().getAbsolutePath();
                    configLabel.setText(configPath);
                }
            }
        });

        testeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = fileChooser.showOpenDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {
                    testPath = fileChooser.getSelectedFile().getAbsolutePath();
                    testeLabel.setText(testPath);
                }
            }
        });

        iniciarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel frame = new JPanel();
                frame.setSize(400,300);
                TextArea textArea = new TextArea();
                try {
                    if(nConj.getText().isEmpty()){
                        int lConj = 0;
                        Simulador simulador  = new Simulador(tipoMap, tipoSubs, configPath, testPath, lConj);
                        simulador.run();
                    } else {
                        int lConj = Integer.parseInt(nConj.getText());
                        Simulador simulador  = new Simulador(tipoMap, tipoSubs, configPath, testPath, lConj);
                        simulador.run();
                    }
                } catch (RuntimeException exception) {
                    textArea.setText("Error na execução do simulador. Verifique os arquivos de teste e configuração!");
                    frame.add(textArea);
                    JOptionPane.showMessageDialog(iniciarButton, frame, "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    @Override
    public Container getContentPane() {
        return mainPanel;
    }
}