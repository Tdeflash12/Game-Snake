import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    enum GameState {
        RUNNING,
        GAME_OVER,
        RESTART_PROMPT
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    Tile food;
    Random random;
    Timer gameLoop;
    GameState gameState;

    int initialSpeed = 150; // Initial speed in milliseconds
    int speedIncreaseFactor = 10; // Speed increase factor after eating food

    int velocityX;
    int velocityY;

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.blue);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;

        gameLoop = new Timer(initialSpeed, this);
        gameLoop.start();

        gameState = GameState.RUNNING;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Grid Lines
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        g.setColor(Color.red);
        g.fillOval(food.x * tileSize, food.y * tileSize, tileSize, tileSize);

        g.setColor(Color.black);
        g.fillOval(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

        for (Tile snakePart : snakeBody) {
            g.fillOval(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
        }

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameState == GameState.GAME_OVER || gameState == GameState.RESTART_PROMPT) {
            g.setColor(Color.red);
            g.drawString("Game Over: " + snakeBody.size(), tileSize - 16, tileSize);
            g.drawString("Press Space Bar to play new game", tileSize - 16, tileSize + 20);
        } else {
            g.drawString("Score: " + snakeBody.size(), tileSize - 16, tileSize);
        }
    }

    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }

    public void move() {
        if (gameState == GameState.RUNNING) {
            if (collision(snakeHead, food)) {
                snakeBody.add(new Tile(food.x, food.y));
                placeFood();
                increaseSpeed();
            }

            for (int i = snakeBody.size() - 1; i >= 0; i--) {
                Tile snakePart = snakeBody.get(i);
                if (i == 0) {
                    snakePart.x = snakeHead.x;
                    snakePart.y = snakeHead.y;
                } else {
                    Tile prevSnakePart = snakeBody.get(i - 1);
                    snakePart.x = prevSnakePart.x;
                    snakePart.y = prevSnakePart.y;
                }
            }

            snakeHead.x += velocityX;
            snakeHead.y += velocityY;

            for (Tile snakePart : snakeBody) {
                if (collision(snakeHead, snakePart)) {
                    gameState = GameState.GAME_OVER;
                }
            }

            if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize > boardWidth ||
                    snakeHead.y * tileSize < 0 || snakeHead.y * tileSize > boardHeight) {
                gameState = GameState.GAME_OVER;
            }
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameState == GameState.GAME_OVER) {
            gameLoop.stop();
            gameState = GameState.RESTART_PROMPT;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameState == GameState.RESTART_PROMPT && e.getKeyCode() == KeyEvent.VK_SPACE) {
            restartGame();
        } else if (gameState == GameState.RUNNING) {
            if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
                velocityX = 0;
                velocityY = -1;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
                velocityX = 0;
                velocityY = 1;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
                velocityX = -1;
                velocityY = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
                velocityX = 1;
                velocityY = 0;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void increaseSpeed() {
        int currentDelay = gameLoop.getDelay();
        int newDelay = Math.max(currentDelay - speedIncreaseFactor, 50); // Cap the minimum speed
        gameLoop.setDelay(newDelay);
    }

    public void restartGame() {
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        placeFood();
        velocityX = 1;
        velocityY = 0;
        gameState = GameState.RUNNING;
        gameLoop.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame snakeGame = new SnakeGame(400, 400);
        frame.add(snakeGame);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        snakeGame.requestFocusInWindow();
    }
}
