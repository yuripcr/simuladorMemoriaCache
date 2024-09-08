import GUI.App;
import javax.swing.*;

public class SimuladorMemoriaCache {
    public static void main(String[] args) {
        // TODO code application logic here
        JFrame frame = new App();
        frame.setContentPane(frame.getContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Simulador de Memória Cache");
        frame.setSize(800,400);
        frame.setVisible(true);
    }
}