import java.awt.*;

// the yarn ball a cat throws at a rat
public class YarnBall {

    private static final int SIZE = 10;

    private double x;
    private double y;
    private double speed = 5;
    private Rat target;
    private int damage;
    private boolean finished = false;

    public YarnBall(double x, double y, Rat target, int damage) {
        this.x = x;
        this.y = y;
        this.target = target;
        this.damage = damage;
    }

    // flies towards its rat (follows it if it moves) and does damage when it gets there
    public void move() {
        // rat is already gone, so this ball just disappears
        if (target.isDead() || target.hasReachedEnd()) {
            finished = true;
            return;
        }

        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= speed) {
            target.takeDamage(damage);
            finished = true;
        } else {
            x += dx / distance * speed;
            y += dy / distance * speed;
        }
    }

    public boolean isFinished() {
        return finished;
    }

    // the yarn ball
    public void draw(Graphics2D g2) {
        int left = (int) x - SIZE / 2;
        int top = (int) y - SIZE / 2;
        g2.setColor(new Color(220, 70, 120));
        g2.fillOval(left, top, SIZE, SIZE);
        g2.setColor(new Color(150, 30, 80));
        g2.drawOval(left, top, SIZE, SIZE);
        g2.drawLine(left + 2, top + 3, left + SIZE - 2, top + SIZE - 3);
    }
}
