package teseabattle;

public interface ConstantModel {
    int getHumanShotsNumber();
    int getHumanShotX(int index);
    int getHumanShotY(int index);
    int getComputerShotsNumber();
    int getComputerShotX(int index);
    int getComputerShotY(int index);
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
    float getHumanNavyHealth();
    float getComputerNavyHealth();
    String getStatus();    
}
