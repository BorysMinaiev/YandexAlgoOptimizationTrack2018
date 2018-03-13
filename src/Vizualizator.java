import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Vizualizator extends JPanel {
    final static int WIDTH = 1000;
    final static int HEIGHT = 1000;
    final static int SHIFT = 100;

    final Color PEOPLE_COLOR = Color.BLUE;
    final int PEOPLE_SIZE = 3;
    final Color ZONES_COLOR = Color.GREEN;
    final int ZONES_SIZE = 5;
    final Color TAXI_COLOR = Color.RED;
    final int TAXI_SIZE = 5;

    final Color TAXI_WITH_PEOPLE = Color.ORANGE;

    int minX, maxX, minY, maxY;
    double zoom = -1;// 0.15;

    int moveIt = 0;

    int getScreenCoord(int minC, int maxC, int coord, int screenSize) {
        return (int) (screenSize / 2 + (coord - (0) / 2.0) * zoom);
    }

    void nextStep() {
        if (moveIt != moves.length) {
            Move move = moves[moveIt++];
            for (int id : move.ids) {
                taxi[id] = taxi[id].move(move.dx, move.dy);
                for (int i = 0; i < peoples.length; i++) {
                    if (peoples[i].x == taxi[id].x && peoples[i].y == taxi[id].y) {
                        if (deadTime[i] >= moveIt) {
                            deadTime[i] = moveIt;
                            taxi[id].color = TAXI_WITH_PEOPLE;
                        }
                    }
                }
                for (Point p : zones) {
                    if (p.x == taxi[id].x && p.y == taxi[id].y) {
                        taxi[id].color = TAXI_COLOR;
                    }
                }
            }
        }
        repaint();
    }

    void prevStep() {
        if (moveIt != 0) {
            Move move = moves[--moveIt];
            for (int id : move.ids) {
                taxi[id] = taxi[id].move(-move.dx, -move.dy);
            }
        }
        repaint();
    }

    void recalcCoords() {
        minX = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        minY = Integer.MAX_VALUE;
        maxY = Integer.MIN_VALUE;
        recalcCoords(peoples);
        recalcCoords(zones);
        recalcCoords(taxi);
        if (zoom == -1)
            zoom = Math.min((WIDTH - SHIFT) / (maxX - minX + SHIFT + 0.), (HEIGHT - SHIFT) / (maxY - minY + SHIFT + 0.));
//        System.err.println(zoom);
    }

    void recalcCoords(Point[] pts) {
        for (Point p : pts) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }
    }

    @Override
    public void paint(Graphics g) {
//        System.err.println("paint!");
        Graphics2D gr = (Graphics2D) g;
        gr.setColor(Color.WHITE);
        gr.fillRect(0, 0, WIDTH, HEIGHT);
        recalcCoords();
        for (int i = 0; i < peoples.length; i++) {
            if (deadTime[i] <= moveIt) {
                peoples[i].color = null;
            } else {
                peoples[i].color = PEOPLE_COLOR;
            }
        }
        drawPoints(peoples, gr, PEOPLE_SIZE);
        drawPoints(taxi, gr, TAXI_SIZE);
        gr.setColor(Color.BLACK);
        gr.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        for (int i = 0; i < taxi.length; i++) {
            gr.drawString(Integer.toString(i + 1), getScreenX(taxi[i].x) + 5, getScreenY(taxi[i].y) - 5);
        }
        drawPoints(zones, gr, ZONES_SIZE);
        if (moveIt >= 0 && moveIt < moves.length) {
            drawMove(moves[moveIt], gr);
        }
    }

    int getScreenX(int x) {
        return getScreenCoord(minX, maxX, x, WIDTH);
    }

    int getScreenY(int y) {
        return getScreenCoord(minY, maxY, y, HEIGHT);
    }

    private void drawMove(Move move, Graphics2D g) {
        g.setColor(Color.BLACK);
        for (int id : move.ids) {
            int x1 = getScreenX(taxi[id].x);
            int y1 = getScreenY(taxi[id].y);
            int x2 = getScreenX(taxi[id].x + move.dx);
            int y2 = getScreenY(taxi[id].y + move.dy);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawPoints(Point[] pts, Graphics2D g, int size) {
        for (Point p : pts) {
            if (p.color == null) {
                continue;
            }
            g.setColor(p.color);
            int nx = getScreenX(p.x);
            int ny = getScreenY(p.y);
            g.fillOval(nx - size, ny - size, size * 2, size * 2);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Let's rock!");
        frame.setSize(WIDTH, HEIGHT);
        Vizualizator game = new Vizualizator(args);
        frame.add(game);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 39) {
//                    System.err.println("next step");
                    game.nextStep();
                } else if (keyEvent.getKeyCode() == 37) {
//                    System.err.println("prev step");
                    game.prevStep();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });
    }

    Vizualizator(String[] args) {
        try {
            Scanner scanner = new Scanner(new File(args.length >= 1 ? args[0] : "test.in"));
            taxi = new Point[scanner.nextInt()];
            for (int i = 0; i < taxi.length; i++) {
                taxi[i] = new Point(scanner.nextInt(), scanner.nextInt(), TAXI_COLOR);
            }

            peoples = new Point[scanner.nextInt()];
            for (int i = 0; i < peoples.length; i++) {
                peoples[i] = new Point(scanner.nextInt(), scanner.nextInt(), PEOPLE_COLOR);
            }
            deadTime = new int[peoples.length];
            Arrays.fill(deadTime, Integer.MAX_VALUE);

            zones = new Point[scanner.nextInt()];
            for (int i = 0; i < zones.length; i++) {
                zones[i] = new Point(scanner.nextInt(), scanner.nextInt(), ZONES_COLOR);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Scanner scanner = new Scanner(new File(args.length >= 2 ? args[1] : "test.out"));
            moves = new Move[scanner.nextInt()];
            double score = 0;
            double[] scoreParts =new double[2];
            for (int i = 0; i < moves.length; i++) {
                scanner.next(); // MOVE
                moves[i] = new Move(scanner.nextInt(), scanner.nextInt(), new int[scanner.nextInt()]);
                for (int j = 0; j < moves[i].ids.length; j++) {
                    moves[i].ids[j] = scanner.nextInt() - 1;
                }
                double dist = moves[i].dx * moves[i].dx + moves[i].dy * moves[i].dy;
                score += Math.sqrt(dist) * (1 + moves[i].ids.length / (double) taxi.length);
                scoreParts[0] += Math.sqrt(dist);
                scoreParts[1] += Math.sqrt(dist) * moves[i].ids.length / taxi.length;
                if (i % 10 == 0)
                    System.err.printf("%03d\t%.3f\n", i, score);
            }
            System.err.println(Arrays.toString(scoreParts));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    Move[] moves;
    Point[] peoples;
    Point[] zones;
    Point[] taxi;
    int[] deadTime;
}