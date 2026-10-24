import javax.swing.*;
import java.awt.*;

// the game window: map in the middle, HUD on the right side
public class MapScreen extends JFrame {

    private GameMap gameMap;
    private JLabel cheeseLabel;
    private JLabel fishLabel;
    private JLabel roundLabel;
    private JLabel catCostLabel;
    private JButton nextRoundButton;

    public MapScreen() {
        setTitle("Cats vs Rats - Map");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // HUD labels, each one gets its own colour
        cheeseLabel = makeHudLabel(new Color(255, 205, 60));
        fishLabel = makeHudLabel(new Color(120, 190, 255));
        roundLabel = makeHudLabel(Color.WHITE);
        catCostLabel = makeHudLabel(Color.LIGHT_GRAY);
        catCostLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        nextRoundButton = new JButton("Next Round");
        nextRoundButton.setFont(new Font("Arial", Font.PLAIN, 18));
        nextRoundButton.addActionListener(e -> gameMap.startNextRound());

        // side panel, everything stacked under each other
        JPanel hudPanel = new JPanel(new GridLayout(6, 1, 0, 10));
        hudPanel.setBackground(new Color(40, 40, 40));
        hudPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        hudPanel.setPreferredSize(new Dimension(190, 0));
        hudPanel.add(cheeseLabel);
        hudPanel.add(fishLabel);
        hudPanel.add(roundLabel);
        hudPanel.add(catCostLabel);
        hudPanel.add(new JLabel());   
        hudPanel.add(nextRoundButton);

        gameMap = new GameMap(this);

        add(gameMap, BorderLayout.CENTER);
        add(hudPanel, BorderLayout.EAST);

        updateLabels();
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JLabel makeHudLabel(Color color) {
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(color);
        return label;
    }

    // GameMap calls this whenever cheese, fish or the round changes
    public void updateLabels() {
        cheeseLabel.setText("Cheese: " + gameMap.getCheese());
        fishLabel.setText("Fish: " + gameMap.getFish());
        roundLabel.setText("Round: " + gameMap.getRound() + " / " + gameMap.getMaxRounds());
        catCostLabel.setText("1 cat = " + gameMap.getCatCost() + " fish");
        nextRoundButton.setEnabled(gameMap.canStartNextRound());  
    }
}
