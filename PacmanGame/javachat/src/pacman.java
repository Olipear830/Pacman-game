import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class pacman extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x, y, width, height;
        Image image;

        int startX, startY;
        char direction = 'U';
        char nextDirection = 'U'; // NEW
        int velocityX = 0, velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            this.nextDirection = direction;
        }

        void tryToChangeDirection(HashSet<Block> walls) {
            int gridX = (x / tilesize) * tilesize;
            int gridY = (y / tilesize) * tilesize;

            boolean alignedHorizontally = y % tilesize == 0;
            boolean alignedVertically = x % tilesize == 0;

            if ((nextDirection == 'L' || nextDirection == 'R') && !alignedVertically) return;
            if ((nextDirection == 'U' || nextDirection == 'D') && !alignedHorizontally) return;

            int tempVelocityX = 0, tempVelocityY = 0;
            if (nextDirection == 'U') tempVelocityY = -tilesize / 4;
            else if (nextDirection == 'D') tempVelocityY = tilesize / 4;
            else if (nextDirection == 'L') tempVelocityX = -tilesize / 4;
            else if (nextDirection == 'R') tempVelocityX = tilesize / 4;

            int tempX = x + tempVelocityX;
            int tempY = y + tempVelocityY;
            Block tempBlock = new Block(null, tempX, tempY, width, height);

            boolean canMove = true;
            for (Block wall : walls) {
                if (collision(tempBlock, wall)) {
                    canMove = false;
                    break;
                }
            }

            if (canMove) {
                if (nextDirection == 'L' || nextDirection == 'R') y = gridY;
                else if (nextDirection == 'U' || nextDirection == 'D') x = gridX;
                direction = nextDirection;
                updateVelocity();
            }
        }

        void updateVelocity() {
            if (direction == 'U') { velocityX = 0; velocityY = -tilesize / 4; }
            else if (direction == 'D') { velocityX = 0; velocityY = tilesize / 4; }
            else if (direction == 'L') { velocityX = -tilesize / 4; velocityY = 0; }
            else if (direction == 'R') { velocityX = tilesize / 4; velocityY = 0; }
        }

        void reset() {
            x = startX;
            y = startY;
        }
    }

    private int rowcount = 21;
    private int columncount = 19;
    private int tilesize = 32;
    private int boardwidth = columncount * tilesize;
    private int boardheight = rowcount * tilesize;

    private Image wallImage, blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "X       bpo       X",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls, foods, ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = { 'U', 'D', 'L', 'R' };
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    pacman() {
        setPreferredSize(new Dimension(boardwidth, boardheight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueghost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeghost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkghost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redghost.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadmap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
            ghost.updateVelocity();
        }

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadmap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowcount; r++) {
            for (int C = 0; C < columncount; C++) {
                char tile = tileMap[r].charAt(C);
                int X = C * tilesize;
                int Y = r * tilesize;

                if (tile == 'X') walls.add(new Block(wallImage, X, Y, tilesize, tilesize));
                else if (tile == 'b') ghosts.add(new Block(blueGhostImage, X, Y, tilesize, tilesize));
                else if (tile == 'o') ghosts.add(new Block(orangeGhostImage, X, Y, tilesize, tilesize));
                else if (tile == 'p') ghosts.add(new Block(pinkGhostImage, X, Y, tilesize, tilesize));
                else if (tile == 'r') ghosts.add(new Block(redGhostImage, X, Y, tilesize, tilesize));
                else if (tile == 'P') pacman = new Block(pacmanRightImage, X, Y, tilesize, tilesize);
                else if (tile == ' ') foods.add(new Block(null, X + 14, Y + 14, 4, 4));
            }
        }

        pacman.startX = pacman.x;
        pacman.startY = pacman.y;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        for (Block ghost : ghosts)
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        for (Block wall : walls)
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        g.setColor(Color.WHITE);
        for (Block food : foods)
            g.fillRect(food.x, food.y, food.width, food.height);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString(gameOver ? "Game Over: " + score : "x" + score, tilesize / 2, tilesize / 2);
    }

    public void move() {
        pacman.tryToChangeDirection(walls);

        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

      for (Block ghost : ghosts) {
    if (collision(ghost, pacman)) {
        lives--;
        if (lives == 0) {
            gameOver = true;
            return;
        }
        resetPositions();
    }

    ghost.x += ghost.velocityX;
    ghost.y += ghost.velocityY;

    for (Block wall : walls) {
        if (collision(ghost, wall)) {
            ghost.x -= ghost.velocityX;
            ghost.y -= ghost.velocityY;

            
            char[] shuffled = directions.clone();
            for (int i = 0; i < shuffled.length; i++) {
                int j = random.nextInt(shuffled.length);
                char tmp = shuffled[i];
                shuffled[i] = shuffled[j];
                shuffled[j] = tmp;
            }

            for (char newDir : shuffled) {
                ghost.updateDirection(newDir);
                ghost.tryToChangeDirection(walls);
                if (ghost.velocityX != 0 || ghost.velocityY != 0) break;
            }
            break;
        }
    }
}
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
                break;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadmap();
            resetPositions();
            lives = 3;
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
            ghost.updateVelocity();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) gameLoop.stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
            pacman.image = pacmanUpImage;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
            pacman.image = pacmanDownImage;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
            pacman.image = pacmanLeftImage;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
            pacman.image = pacmanRightImage;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}