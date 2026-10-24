import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// start screen with a start and a quit button
public class MainMenu extends JFrame implements ActionListener {

    private JButton startButton;
    private JButton quitButton;

    public MainMenu() {
        setTitle("Cats vs Rats");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        JLabel titleLabel = new JLabel("Cats vs Rats");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.PLAIN, 20));
        startButton.addActionListener(this);
        gbc.gridy = 1;
        panel.add(startButton, gbc);

        quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Arial", Font.PLAIN, 20));
        quitButton.addActionListener(this);
        gbc.gridy = 2;
        panel.add(quitButton, gbc);

        add(panel);
    }

    // start closes the menu and opens the game, quit closes everything
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            dispose();
            MapScreen mapScreen = new MapScreen();
            mapScreen.setVisible(true);
        } else if (e.getSource() == quitButton) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}
