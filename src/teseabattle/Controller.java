package teseabattle;

public class Controller {
    private final Model model;

    public Controller(Model model) {
        this.model = model;
    }
    
    public void newGame(){
        model.prepareNewGame();
    }
    
    public void shot(int x, int y){
        model.shootsHuman(x, y);
    }
    
}
