package teseabattle;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Model implements ConstantModel {

    private final int fieldSize;
    private Navy humanNavy, computerNavy;
    private ArrayList<Point> humanShots, computerShots;
    private final Random random = new Random();
    private GameState gameState = GameState.HUMAN_TO_PLAY;
    private final ArrayList<ModelListener> views;
    private static final String HUMAN_WIN_MESSAGE = "YOU WON!!!";
    private static final String COMPUTER_WIN_MESSAGE = "COMPUTER WON!!!";

    public Model(int fieldSize) {
        this.views = new ArrayList<>();
        this.fieldSize = fieldSize;
        prepareNewGame();
    }

    public final void prepareNewGame() {
        gameState = GameState.HUMAN_TO_PLAY;
        humanNavy = new Navy();
        computerNavy = new Navy();
        humanShots = new ArrayList<>();
        computerShots = new ArrayList<>();
        notifyAllListeners();
    }

    @Override
    public int getHumanShotsNumber() {
        return humanShots.size();
    }

    @Override
    public int getComputerShotsNumber() {
        return computerShots.size();
    }

    @Override
    public int getHumanShotX(int number) {
        return humanShots.get(number).x;
    }

    @Override
    public int getHumanShotY(int number) {
        return humanShots.get(number).y;
    }

    @Override
    public int getComputerShotX(int number) {
        return computerShots.get(number).x;
    }

    @Override
    public int getComputerShotY(int number) {
        return computerShots.get(number).y;
    }

    @Override
    public float getHumanNavyHealth() {
        return humanNavy.calcHealthPercentage();
    }

    @Override
    public float getComputerNavyHealth() {
        return computerNavy.calcHealthPercentage();
    }

    @Override
    public String getStatus() {
        return gameState.getDescription();
    }

    @Override
    public int getHumanShipsCount() {
        return humanNavy.ships.size();
    }

    @Override
    public int getCellNumberHumanShip(int indexShip) {
        return humanNavy.structure[indexShip];
    }

    @Override
    public int getHumanShipCell_x(int indexShip, int indexCell) {
        return humanNavy.ships.get(indexShip).cells.get(indexCell).getX();
    }

    @Override
    public int getHumanShipCell_y(int indexShip, int indexCell) {
        return humanNavy.ships.get(indexShip).cells.get(indexCell).getY();
    }

    @Override
    public boolean isHumanShipCellAlive(int indexShip, int indexCell) {
        return humanNavy.ships.get(indexShip).cells.get(indexCell).isAlive_;
    }

    @Override
    public int getComputerShipsCount() {
        return computerNavy.ships.size();
    }

    @Override
    public int getCellNumberComputerShip(int indexShip) {
        return computerNavy.structure[indexShip];
    }

    @Override
    public int getComputerShipCell_x(int indexShip, int indexCell) {
        return computerNavy.ships.get(indexShip).cells.get(indexCell).getX();
    }

    @Override
    public int getComputerShipCell_y(int indexShip, int indexCell) {
        return computerNavy.ships.get(indexShip).cells.get(indexCell).getY();
    }

    @Override
    public boolean isComputerShipCellAlive(int indexShip, int indexCell) {
        return computerNavy.ships.get(indexShip).cells.get(indexCell).isAlive_;
    }

    public void shootsHuman(int x, int y) {
        if (gameState == GameState.HUMAN_TO_PLAY) {
            if (!isHittingSamePlace(x, y)) {
                humanShots.add(new Point(x, y));
                if (computerNavy.hit(x, y)) {
                    //Если человек попал проверяем победу
                    if (!computerNavy.isActive()) {
                        gameState = GameState.HUMAN_WON;
                    }
                } else {
                    //Если промазал то стреляет ком пока не промахнется
                    gameState = GameState.COMPUTER_TO_PLAY;
                    while (gameState == GameState.COMPUTER_TO_PLAY) {
                        shootsComputer();
                    }
                }
            }
        }
        notifyAllListeners();
    }

    private void shootsComputer() {
        int shotX, shotY;
        do {
            shotX = random.nextInt(fieldSize);
            shotY = random.nextInt(fieldSize);
        } while (isHittingSamePlace(shotX, shotY));
        computerShots.add(new Point(shotX, shotY));
        if (humanNavy.hit(shotX, shotY)) {
            if (!humanNavy.isActive()) {
                gameState = GameState.COMPUTER_WON;
            }
        } else {
            gameState = GameState.HUMAN_TO_PLAY;
        }
        notifyAllListeners();
    }

    private boolean isHittingSamePlace(int x, int y) {
        boolean result = false;
        if (gameState == GameState.HUMAN_TO_PLAY) {
            for (Point p : humanShots) {
                if (p.x == x && p.y == y) {
                    result = true;
                    break;
                }
            }
        } else if (gameState == GameState.COMPUTER_TO_PLAY) {
            for (Point p : computerShots) {
                if (p.x == x && p.y == y) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private enum GameState {
        HUMAN_TO_PLAY("Human to play"),
        COMPUTER_TO_PLAY("Computer to play"),
        HUMAN_WON(HUMAN_WIN_MESSAGE),
        COMPUTER_WON(COMPUTER_WIN_MESSAGE);
        private final String description;

        public String getDescription() {
            return description;
        }

        private GameState(String description) {
            this.description = description;
        }
    }

    private class Navy {

        private float health;
        private final ArrayList<Ship> ships;
        private final int[] structure = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
        private final int TOTAL_CELLS = 20;

        public Navy() {
            this.ships = new ArrayList<>();
            for (int i = 0; i < structure.length; ++i) {
                Ship ship;
                do {
                    int randX = random.nextInt(fieldSize);
                    int randY = random.nextInt(fieldSize);
                    boolean orientation = random.nextBoolean();
                    ship = new Ship(randX, randY, structure[i], orientation);
                } while (ship.isOutOfField() || isOverlapOrTouch(ship));
                ships.add(ship);
            }
        }

        public boolean isActive() {
            return calcHealthPercentage() > 0;
        }

        public boolean hit(int x, int y) {
            boolean result = false;
            for (Ship ship : ships) {
                if (ship.hit(x, y)) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        public float calcHealthPercentage() {
            int numAliveCells = 0;
            for (Ship ship : ships) {
                numAliveCells += ship.numAliveCells();
            }
            return 100 * numAliveCells / TOTAL_CELLS;
        }

        private boolean isOverlapOrTouch(Ship newShip) {
            boolean result = false;
            for (Ship ship : ships) {
                if (ship.isCollision(newShip)) {
                    result = true;
                    break;
                }
            }
            return result;
        }
    }

    public class Ship {

        private final ArrayList<Cell> cells = new ArrayList<>();
        private final boolean isVertivalOrientation;

        public Ship(int posX, int posY, int size, boolean isVerticalPosition) {
            for (int i = 0; i < size; ++i) {
                cells.add(new Cell(posX + i * (isVerticalPosition ? 0 : 1), posY + i * (isVerticalPosition ? 1 : 0)));
            }
            this.isVertivalOrientation = isVerticalPosition;
        }

        public int getSize() {
            return cells.size();
        }

        public boolean isVertical() {
            return isVertivalOrientation;
        }

        public int numAliveCells() {
            int num = 0;
            for (Cell c : cells) {
                num += c.isAlive_ ? 1 : 0;
            }
            return num;
        }

        public boolean isAlive() {
            boolean result = false;
            for (Cell cell : cells) {
                if (cell.isAlive_) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        public boolean hit(int x, int y) {
            boolean result = false;
            for (Cell cell : cells) {
                if (cell.hit(x, y)) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        public boolean isCollision(Ship other) {
            boolean result = false;
            for (Cell cell : cells) {
                if (other.isCollision(cell)) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        public boolean isCollision(Cell other) {
            boolean result = false;
            label:
            for (Cell cell : cells) {
                for (int dx = -1; dx < 2; ++dx) {
                    for (int dy = -1; dy < 2; ++dy) {
                        if (other.getX() == cell.getX() + dx && other.getY() == cell.getY() + dy) {
                            result = true;
                            break label;
                        }
                    }
                }
            }
            return result;
        }

        public boolean isOutOfField() {
            boolean result = false;
            for (Cell cell : cells) {
                if (cell.getY() < 0 || cell.getY() > fieldSize - 1
                        || cell.getX() < 0 || cell.getX() > fieldSize - 1) {
                    result = true;
                    break;
                }
            }
            return result;
        }
    }

    public class Cell {

        private final int x;
        private final int y;
        private boolean isAlive_ = true;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean hit(int x, int y) {
            boolean result = false;
            if (this.x == x && this.y == y) {
                isAlive_ = false;
                result = true;
            }
            return result;
        }

        public boolean isAlive() {
            return isAlive_;
        }
    }

    public void addListener(ModelListener l) {
        views.add(l);
    }

    public void removeListener(ModelListener l) {
        views.remove(l);
    }

    private void notifyAllListeners() {
        for (ModelListener v : views) {
            v.updateView();
        }
    }
}
