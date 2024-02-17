import javax.swing.*;
public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 600;  //(IF 1000 DOWN INCREASE)
        int boardHeight = 550;
        JFrame frame = new JFrame("Snake Game");
        frame.setVisible(true); //(if false nothing will appear)
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false); //(if true full screen)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);
        frame.add(snakeGame);
        frame.pack();
        snakeGame.requestFocus();
    }
}