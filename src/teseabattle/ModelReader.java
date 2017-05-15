package teseabattle;

public interface ModelReader {
    int getHumanShipsCount();
    int getCellNumberHumanShip(int indexShip);
    int getHumanShipCell_x(int indexShip, int indexCell);
    int getHumanShipCell_y(int indexShip, int indexCell);
    boolean isHumanShipCellAlive(int indexShip, int indexCell);
    int getComputerShipsCount();
    int getCellNumberComputerShip(int indexShip);
    int getComputerShipCell_x(int indexShip, int indexCell);
    int getComputerShipCell_y(int indexShip, int indexCell);
    boolean isComputerShipCellAlive(int indexShip, int indexCell);    
}
