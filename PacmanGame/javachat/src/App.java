import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        int rowcount = 21;
        int columncount = 19;
        int tilesize = 32;
        int boardwidth = columncount * tilesize;
        int boardheight = rowcount * tilesize;

        JFrame frame = new JFrame("Pacman");
        frame.setVisible(true);
        frame.setSize(boardwidth, boardheight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pacman pacmanGame = new pacman();
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);

            
        
    }
}
