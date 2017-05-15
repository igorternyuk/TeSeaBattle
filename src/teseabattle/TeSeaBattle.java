package teseabattle;

public class TeSeaBattle {

    public void go() {
        Model model = new Model(Constants.FIELD_DIMENSION);
        Controller controller = new Controller(model);
        View view = new View(model, controller);
        model.addListener(view);
    }

    public static void main(String[] args) {
        TeSeaBattle battle = new TeSeaBattle();
        battle.go();
    }

}
