 /**
 * Java. Game TeSeaBattle 
 *
 * @author Igor Ternyuk
 * @version 1.0 dated May 14, 2017
 */
package teseabattle;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class View implements ModelListener{
    private final String TITLE_OF_PROGRAM = "TeSeaBattle";
    private final String TITLE_OF_HUMAN_FIELD = "Human's ships";
    private final String TITLE_OF_COMPUTER_FIELD = "Computer's ships";
    private final String BTN_NEW_GAME_LABEL = "New Game";
    private final String BTN_QUIT_GAME_LABEL = "Quit game";
    private final Dimension btnDim = new Dimension(120, 30);
    private final int WINDOW_WIDTH = 900, WINDOW_HEIGHT = 600;
    private final int FIELD_SIZE = 450;
    private final int FIELD_DIMENSION = 10;
    private final int CELL_SIZE = FIELD_SIZE / FIELD_DIMENSION;
    private final int SHOT_DIAMETER = 16;
    private final int FONT_SIZE = 30;
    private final int SEPARATING_LINE_WIDTH = 5;
    private final Color DARK_GREEN = new Color(56, 107, 34);
    private final Font font;
    Model model;
    Controller controller;
    JFrame window;
    Canvas canvas; //Renderer
    JPanel btnPanel;
    JButton btnNewGame, btnQuit;
    
    public View(){
        this.font = new Font("Arial", Font.BOLD, FONT_SIZE);
        model = new Model(FIELD_DIMENSION);
        controller = new Controller(model);
        window = new JFrame(TITLE_OF_PROGRAM);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setResizable(false);
        window.setFocusable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        canvas = new Canvas();
        canvas.setBackground(Color.CYAN.darker());
        canvas.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
                int cursorX = e.getX() / CELL_SIZE;
                int cursorY = e.getY() / CELL_SIZE;
                boolean isPointInsideOfTheComputerField = cursorX >= 0 &&
                        cursorX < FIELD_DIMENSION && cursorY >= 0 
                        && cursorY < FIELD_DIMENSION;
                if(isPointInsideOfTheComputerField && 
                   e.getButton() == MouseEvent.BUTTON1){
                    controller.shot(cursorX, cursorY);
                }
            }
        });
        
        btnPanel = new JPanel();
        
        btnNewGame = new JButton(BTN_NEW_GAME_LABEL);
        btnNewGame.setPreferredSize(btnDim);
        btnNewGame.setFocusable(false);
        btnNewGame.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.newGame();
            }
        });
        
        btnQuit = new JButton(BTN_QUIT_GAME_LABEL);
        btnQuit.setPreferredSize(btnDim);
        btnQuit.setFocusable(false);
        btnQuit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        btnPanel.add(btnNewGame);
        btnPanel.add(btnQuit);
        
        window.add(BorderLayout.CENTER, canvas);
        window.add(BorderLayout.SOUTH, btnPanel);
        window.setVisible(true);
    }
    
    public void go(){
        model.addListener(this);
    }
    
    @Override
    public void updateView() {
        canvas.repaint();
    }
    
    public void draw(Graphics2D g){
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Drawing of the field grid
        
        for (int i = 0; i <= FIELD_DIMENSION; ++i) {
            g.drawLine(0, i * CELL_SIZE, WINDOW_WIDTH, i * CELL_SIZE);
        }

        for (int i = 0; i <= 2 * FIELD_DIMENSION; ++i) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, FIELD_DIMENSION * CELL_SIZE);
        }
        
        //Drawing of the human's shots
        final int humanShotsNumber = model.getHumanShotsNumber();
        if (humanShotsNumber > 0) {
            for (int i = 0; i < humanShotsNumber; ++i) {
                g.setColor(Color.YELLOW);
                g.fillOval(model.getHumanShotX(i) * CELL_SIZE + (CELL_SIZE - SHOT_DIAMETER) / 2 + 1,
                        model.getHumanShotY(i) * CELL_SIZE + (CELL_SIZE - SHOT_DIAMETER) / 2 + 1,
                        SHOT_DIAMETER, SHOT_DIAMETER);
            }

        }

        //Drawing of the computer's shots
        final int computerShotsNumber = model.getComputerShotsNumber();
        if (computerShotsNumber > 0) {
            for (int i = 0; i < computerShotsNumber; ++i) {
                g.setColor(Color.YELLOW);
                g.fillOval(FIELD_DIMENSION * CELL_SIZE + model.getComputerShotX(i)
                        * CELL_SIZE + (CELL_SIZE - SHOT_DIAMETER) / 2 + 1,
                        model.getComputerShotY(i) * CELL_SIZE + (CELL_SIZE - SHOT_DIAMETER) / 2 + 1,
                        SHOT_DIAMETER, SHOT_DIAMETER);
            }
        }
        
        //Drawing of the human's ships
        
        for (int i = 0; i < model.getHumanShipsCount(); ++i) {
            final int numCells = model.getCellNumberHumanShip(i); //Сколько ячеек в текущем корабле
            for (int j = 0; j < numCells; ++j) {
                g.setColor(model.isHumanShipCellAlive(i, j) ? Color.WHITE.darker() : Color.RED);
                final int x = model.getHumanShipCell_x(i, j);
                final int y = model.getHumanShipCell_y(i, j);
                g.fill3DRect(FIELD_DIMENSION * CELL_SIZE + x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE, true);
            }
        }

        //Drawing of the computer's ships
                
        for (int i = 0; i < model.getComputerShipsCount(); ++i) {
            final int numCells = model.getCellNumberComputerShip(i); //Сколько ячеек в текущем корабле
            for (int j = 0; j < numCells; ++j) {
                if (!model.isComputerShipCellAlive(i, j)) {
                    g.setColor(Color.RED);
                    final int x = model.getComputerShipCell_x(i, j);
                    final int y = model.getComputerShipCell_y(i, j);
                    g.fill3DRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE, true);
                }
            }
        }
        
        //Drawing the game info
        
        g.setColor(DARK_GREEN);
        g.setFont(font);
        g.drawString("Human ships: " + model.getHumanNavyHealth() + "%", 40, FIELD_DIMENSION * CELL_SIZE + 40);
        g.drawString("Computer ships: " + model.getComputerNavyHealth() + "%", FIELD_DIMENSION * CELL_SIZE + 40, FIELD_DIMENSION * CELL_SIZE + 40);
        g.setColor(Color.BLUE);
        g.drawString(model.getStatus(), WINDOW_WIDTH / 2 - 100, FIELD_DIMENSION * CELL_SIZE + 70);

        //Drawing the separating line
        
       g.setColor(DARK_GREEN);
       g.setStroke(new BasicStroke(SEPARATING_LINE_WIDTH));
       g.drawLine(WINDOW_WIDTH / 2, 1, WINDOW_WIDTH / 2, FIELD_DIMENSION * CELL_SIZE - 2);
       
    }
    
    private class Canvas extends JPanel {
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            draw((Graphics2D)g);
        }
    }
}
