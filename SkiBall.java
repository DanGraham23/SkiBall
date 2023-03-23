import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;

/**
 * Creates a slightly modified version of SkeeBall using java swing elements
 *
 * @author Daniel Graham, Aimen Harizi, Zohaib Asif
 * @version 4/7/2021
 */
public class SkiBall extends MouseAdapter implements Runnable, ActionListener
{
    //Constants for the size of the game area
    private int LANE_WIDTH = 350;
    private int LANE_HEIGHT = 800;

    //Constant for the powerlevel
    private int POWER_LEVEL = 4;

    //Tracker for number of shots left and its label
    private int shotsLeft = 10;
    private JLabel shots;

    //Tracker for total score and its label 
    private int totalScore = 0;
    private JLabel score;

    //JLabel for displaying the current shot score and current shot message
    private JLabel curShotScore;
    private JLabel curShotMessage;

    //JPanel for the game panel, the shooting and target area
    private JPanel gamePanel;

    //JPanel for the other game buttons and labels
    private JPanel infoPanel;

    //JLabel for the start message
    private JLabel startMessage;

    //JButtons for reset, start and play again game
    private JButton reset;
    private JButton start;
    private JButton playAgain;

    //Shapes list
    private ArrayList<Circle> shapes;

    //Click tracker to see if a line needs to be drawn when shooting
    private int numClicks;

    //Point to draw the shooting line indicator
    private Point point1;
    private Point curPos;

    //Used to calculate the ball landing location and determine if a ball has been shot
    private int delX, delY;
    private boolean shotBall;

    //Determine if the game has been started
    private boolean isStarted;

    /**
     * Runs the main logic for the SkiBall graphics
     */
    @Override
    public void run(){
        //Create the game window
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Set window size and title
        JFrame frame = new JFrame("SkiBall");
        frame.setPreferredSize(new Dimension(600,800));
        frame.setLayout(new BorderLayout());

        //Initialize the shapes list
        shapes = new ArrayList<Circle>();

        //Set numClicks to 0, shotBall to false, isStarted to false
        numClicks = 0;
        shotBall = false;
        isStarted = false;

        //Create the target circles andd add them each to the list
        Circle circ1 = new Circle(200, new Point(80,100), Color.PINK);
        shapes.add(circ1);

        Circle circ2 = new Circle(120, new Point(120,140), Color.GREEN);
        shapes.add(circ2);

        Circle circ3 = new Circle(40, new Point(160,180), Color.BLUE);
        shapes.add(circ3);

        Circle circ4 = new Circle(40, new Point(160,100), Color.RED);
        shapes.add(circ4);

        // JPanel with a paintComponent method
        gamePanel = new JPanel() {

            @Override
            public void paintComponent(Graphics g) {

                // first, we should call the paintComponent method we are
                // overriding in JPanel
                super.paintComponent(g);

                //Draw the circles aka the targets
                for (Circle s : shapes) {
                    g.setColor(s.getColor());
                    s.paint(g);
                }

                //Check if the start button has been clicked
                if (isStarted){
                    //Reset color to black to paint sling if needed
                    g.setColor(Color.BLACK);
                    
                    //Check if the mouse has been clicked to start shooting
                    if (numClicks == 1){

                        //Draw the shooting line and ball. Also update cur shot message. This is if the mouse is moving
                        g.drawLine(point1.x, 550, curPos.x, curPos.y);
                        g.fillOval(curPos.x-5, curPos.y-5, 10, 10);
                        delX = curPos.x - point1.x;
                        delY = curPos.y - 550;

                        curShotMessage.setText("Drag to aim");
                    }

                    //Check if the mouse has been released for a shot
                    if (shotBall == true){
                        //Update variable for game logic
                        shotBall = false;

                        //Update shots left and its label
                        shotsLeft--;
                        shots.setText("Shots left: " + shotsLeft);

                        //Check if a target was hit, start with smaller circles
                        for (int i = shapes.size()-1; i >= 0; i--) {
                            if (shapes.get(i).contains(new Point(curPos.x - (delX * 4), curPos.y - (delY * 4)))) {
                                //Add to total score
                                if (i == 3){
                                    totalScore += 50;
                                    curShotScore.setText(50 + " points");
                                    curShotMessage.setText("Great shot!");

                                }
                                else if (i == 2){
                                    totalScore += 30;
                                    curShotScore.setText(30 + " points");
                                    curShotMessage.setText("Nice one!");

                                }
                                else if (i == 1){
                                    totalScore += 20;
                                    curShotScore.setText(20 + " points");
                                    curShotMessage.setText("Not bad..");
                                }
                                else if (i == 0){ 
                                    totalScore += 10;
                                    curShotScore.setText(10 + " points");
                                    curShotMessage.setText("On the board.");
                                }
                                //Draw landing location
                                g.setColor(shapes.get(i).getColor().darker());
                                g.fillOval(curPos.x - (delX * 4)-5, curPos.y - (delY * 4)-5, 10, 10);
                                break;
                            }else{
                                g.setColor(Color.BLACK);
                                g.fillOval(curPos.x - (delX * 4)-5, curPos.y - (delY * 4)-5, 10, 10);
                                curShotMessage.setText("Missed...");
                            }
                        }
                        score.setText("Total score: " + totalScore);
                    }

                    //Check if all shots used. Displays play again button and sets game to not started.
                    if (shotsLeft == 0){
                        reset.setVisible(false);
                        playAgain.setVisible(true);
                        infoPanel.revalidate();
                        infoPanel.repaint();

                        isStarted = false;

                    }

                }

                //Draw the foul line at its coordinates
                g.setColor(Color.BLACK);
                g.drawLine(0, 550, 350, 550);
            }
        };

        //Set gamepanel size and add to frame
        gamePanel.setPreferredSize(new Dimension(LANE_WIDTH, LANE_HEIGHT));
        gamePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        frame.add(gamePanel, BorderLayout.WEST);

        //Add listeners to the gamePanel
        gamePanel.addMouseListener(this);
        gamePanel.addMouseMotionListener(this);

        //Initilize the infoPanel with BoxLayout
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        //Set infoPanel size and add to frame
        infoPanel.setPreferredSize(new Dimension(240, 800));
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        frame.add(infoPanel, BorderLayout.EAST);

        //Set fonts for the initial game labels
        JLabel gameTitle = new JLabel("Ski Ball");
        Font newFont = new Font(Font.SANS_SERIF,Font.BOLD, 30);
        gameTitle.setFont(newFont);

        startMessage = new JLabel("Press Start Game to Play");
        Font newFont2 = new Font(Font.SANS_SERIF,Font.PLAIN, 18);
        startMessage.setFont(newFont2);

        //Add the initial game labels to the infoPanel
        infoPanel.add(gameTitle);
        infoPanel.add(startMessage);

        //Initialize the start button
        start = new JButton("Start");
        start.addActionListener(this);

        //Initialize the reset button
        reset = new JButton("Reset");
        reset.addActionListener(this);

        //Initialize the play again button
        playAgain = new JButton("Play Again");
        playAgain.addActionListener(this);

        //Initialize the curShotScore label
        curShotScore = new JLabel("");

        //Initialze the curShotMessage label
        curShotMessage = new JLabel("Press below the line to shoot");

        //Initialize the shots left label
        shots = new JLabel("Shots left: " + shotsLeft);

        //Initialize total score label
        score = new JLabel("Total score: " + totalScore);

        //Add mid game labels to infoPanel
        infoPanel.add(score);
        infoPanel.add(curShotMessage);
        infoPanel.add(curShotScore);
        infoPanel.add(shots);

        //Set mid game labels to hidden
        curShotScore.setVisible(false);
        curShotMessage.setVisible(false);
        shots.setVisible(false);

        //Add buttons to infoPanel
        infoPanel.add(start);
        infoPanel.add(reset);
        infoPanel.add(playAgain);

        //Set reset and playAgain to hidden
        reset.setVisible(false);
        playAgain.setVisible(false);

        //Exit operation when the user wants to quit
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Checks if the mouse has been pressed
     * 
     * @param e the action event
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //Check the bounds
        if (isStarted && e.getPoint().x < 350 && e.getPoint().y >= 550){
            numClicks = 1;
            point1 = e.getPoint();
            curPos = point1;
            gamePanel.repaint();
        }
    }

    /**
     * Checks if the mouse has been released
     * 
     * @param e the action event
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        //Check the bounds
        if (isStarted && numClicks == 1){
            //If the mouse was released, then reset click counter, set shotBall to true and repaint
            numClicks = 0;
            shotBall = true;
            gamePanel.repaint();
        }

    }

    /**
     * Checks if the mouse has been dragged
     * 
     * @param e the action event
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        //If the mouse was released, then reset click counter, set shotBall to true and repaint
        //If the mouse is dragged, get the new position and repaint
        curPos = e.getPoint();
        gamePanel.repaint();
    }

    /**
     * Checks for any presses of JButtons
     * 
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == start){
            //Perform neccessary start functions here ***
            isStarted = true;

            //Swap the start button for the reset button
            start.setVisible(false);
            reset.setVisible(true);

            //Hide starting message
            startMessage.setVisible(false);

            //Set mid game labels to visible
            curShotScore.setVisible(true);
            curShotMessage.setVisible(true);
            shots.setVisible(true);

            infoPanel.repaint();
            infoPanel.revalidate();
        }else if(e.getSource() == reset){
            //Perform neccessary reset functions here ***
            isStarted = true;

            //Reset the variables and labels for the game
            shotsLeft = 10;
            totalScore = 0;
            shots.setText("Shots left: " + 10);
            curShotScore.setText("");
            curShotMessage.setText("Press below the line to shoot");
            score.setText("Total score: " + 0);

            //Make the reset button visible still
            reset.setVisible(true);

            infoPanel.repaint();
            infoPanel.revalidate();
        }else if (e.getSource() == playAgain){
            //Perform neccessary play again functions here***
            isStarted = true;

            //Reset the variables and labels for the game
            shotsLeft = 10;
            totalScore = 0;
            shots.setText("Shots left: " + 10);
            curShotScore.setText("");
            curShotMessage.setText("Press below the line to shoot");
            score.setText("Total score: " + 0);

            //Swap the play again button for the reset button
            playAgain.setVisible(false);
            reset.setVisible(true);

            infoPanel.repaint();
            infoPanel.revalidate();
        }
    }

    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(new SkiBall());
    }
}

class Circle{

    //The size of the current circle 
    private int size;

    //The upper left point of the circle
    private Point upperLeft;

    //The color of the circle
    private Color color;

    public Circle(int size, Point upperLeft, Color color) {
        this.size = size;
        this.upperLeft = upperLeft;
        this.color = color;
    }

    public boolean contains(Point p){
        Point circleCenter =
            new Point(upperLeft.x + size/2, upperLeft.y + size/2);
        return circleCenter.distance(p) <= size/2;
    }

    public void paint(Graphics g){
        g.fillOval(upperLeft.x, upperLeft.y, size, size);
        g.setColor(color);
    }

    public Color getColor(){
        return color;
    }
}