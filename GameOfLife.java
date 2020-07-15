package life;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Random;

////controller////
public class GameOfLife extends JFrame {

    private Timer timer;
    private Processor processor;
    private GenerationLabel generationLabel;
    private AliveLabel aliveLabel;
    private MatrixPanel matrixPanel;
    private ControlTools controlTools;

    private Updater updater = new Updater() {

        ////updates components with information retrieved from processor////
        @Override
        public void passNextGenData(boolean[][] booleanMatrix, int generationAliveCount) {
            generationLabel.setGenerationCount(generationCount);
            processor.setGeneration(generationCount);
            aliveLabel.setGenerationAliveCount(generationAliveCount);
            matrixPanel.setBooleanMatrix(booleanMatrix);
        }

        @Override
        public void passData(int i) {
           matrixSize = i;
        }

        @Override
        public void start() {
            runGameOfLife();
            timer.start();

        }

        @Override
        public void pause() {
            timer.stop();
        }

        @Override
        public void reset() {
            processor = new Processor(updater, matrixSize);
        }
    };

    ////adjustable fields////
    private int generationCount = 1;
    private int maxGenerations = 10;
    private int matrixSize = 10;



    public GameOfLife() {
        super("Game of Life");

        ////initialize components////
        processor = new Processor(updater, matrixSize);
        generationLabel = new GenerationLabel();
        aliveLabel = new AliveLabel();
        matrixPanel = new MatrixPanel(matrixSize);
        controlTools = new ControlTools(updater);
        processor.outputCurrentGeneration();

        ////timer start. Run Game of Life////
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runGameOfLife();

                ////stops timer and halts processor after 10 generations////
                if(generationCount > maxGenerations) {
                    ((Timer) e.getSource()).stop();
                }
                //System.out.println("Action event called");
            }
        });
        timer.setDelay(1000);
        System.out.println("Timer set");


        ////JFrame components////
        setGridBaglayout();
        setVisible(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    public void runGameOfLife() {
        processor.runProcessor();
        generationCount++;

    }

    public void setGridBaglayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        ////Controller Tools////
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = .1;
        gc.insets = new Insets(0,0,0,0);
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.HORIZONTAL;
        add(controlTools,gc);

        ////Generation Label////
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.insets = new Insets(0,4,0,0);
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_START;
        this.add(generationLabel, gc);

        ////Alive Label////
        gc.gridx = 0;
        gc.gridy = 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.insets = new Insets(0,4,0,0);
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_START;
        this.add(aliveLabel, gc);

        ////Map Panel/////
        gc.gridx = 0;
        gc.gridy = 3;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.insets = new Insets(0,0,0,0);
        gc.fill = GridBagConstraints.BOTH;
        //gc.anchor = GridBagConstraints.;
        this.add(matrixPanel, gc);
    }
}

////Components
class ControlTools extends JPanel {
    private JToggleButton start;
    private JButton refresh;
    private JLabel matrixSizeLabel;
    private JTextField matrixSizeField;
    private Updater updater;

    public ControlTools(Updater updater) {
        super();
        this.updater = updater;
        start = new JToggleButton("Start");
        refresh = new JButton("Refresh");
        matrixSizeLabel = new JLabel("Matrix Size: ");
        matrixSizeField = new JTextField(10);

        addActionListeners();

        setGridBagLayout();
        setBorder(BorderFactory.createSoftBevelBorder(1));
    }

    public void addActionListeners() {
        start.addActionListener(e -> {
             updater.passData(matrixSizeField.getText().equals("") ? 0 : Integer.parseInt(matrixSizeField.getText()));
             updater.start();
        });

        refresh.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();

                if(state == ItemEvent.SELECTED) {
                    updater.start();
                } else {
                    updater.pause();
                }
            }
        });
    }

    public void setGridBagLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        ////row1  Start, Refresh////
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = .1;
        gc.insets = new Insets(0,0,0,0);
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.NONE;
        add(start, gc);

        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = .1;
        gc.insets = new Insets(0,0,0,0);
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.NONE;
        add(refresh, gc);

        ////row 2 Label, TextBox////
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 1;
        gc.weighty = .1;
        gc.insets = new Insets(0,4,0,0);
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.NONE;
        add(matrixSizeLabel, gc);

        gc.gridx = 1;
        gc.gridy = 1;
        gc.weightx = 1;
        gc.weighty = .1;
        gc.insets = new Insets(0,0,0,0);
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.NONE;
        add(matrixSizeField, gc);
    }
}

class GenerationLabel extends JLabel {

    public GenerationLabel() {
        super();
        this.setName("GenerationLabel");
    }

    public void setGenerationCount(int generationCount) {
        String str = String.format("Generation: %d", generationCount);
        this.setText(str);
    }
}

class AliveLabel extends JLabel {

    public AliveLabel() {
        super();
        this.setName("AliveLabel");

    }

    public void setGenerationAliveCount(int generationAliveCount) {
        String str = String.format("Alive: %d", generationAliveCount);
        this.setText(str);
    }
}

class MatrixPanel extends JPanel {
    private boolean[][] booleanMatrix;
    private int squareSize;
    final private int panelSize = 300;

    public MatrixPanel(int matrixSize) {
        super();
        Dimension dim = new Dimension();
        dim.width = panelSize;
        dim.height = panelSize;
        setPreferredSize(dim);
        setBorder(BorderFactory.createSoftBevelBorder(1));
        setBackground(Color.WHITE);

        this.squareSize = panelSize/matrixSize;
    }

    @Override
    protected void paintComponent(Graphics g) {
        ////generates a painted object corresponding to the boolean matrix, updates following use repaint()/////
        super.paintComponent(g);
        for(int i = 0; i < panelSize; i += squareSize) {
            for(int j = 0; j < panelSize; j += squareSize) {
                if(booleanMatrix[i/squareSize][j/squareSize]) {
                    g.setColor(Color.BLACK);
                    g.fillRect(j, i, squareSize, squareSize);
                } else {
                    g.setColor(Color.WHITE);
                    g.clearRect(i,j,squareSize,squareSize);
                }
            }
        }
    }

    public void setBooleanMatrix(boolean[][] booleanMatrix) {
        ////updates booleanMatrix from processor, repaints jpanel////
        this.booleanMatrix = booleanMatrix;
        this.repaint();
    }
}

class Processor {
    ////contains all logic components////
    final private static Random random = new Random(System.currentTimeMillis());

    private int matrixSize;
    private Organism[][] matrix;
    private int aliveCount;
    private Updater updater;
    private int generation;

    public Processor(Updater updater, int matrixSize) {
        this.matrix = new Organism[matrixSize][matrixSize];
        this.updater = updater;
        this.matrixSize = matrixSize;

        populateMap();
    }

    public void runProcessor() {
        ////core logic for determining organism survival to next generation////

        getNextGenerationData();
        setNextGeneration();
        outputCurrentGeneration();
    }

    public void populateMap() {
        int aliveCount = 0;
        ////populates map with Organisms////
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix.length; j++) {
                matrix[i][j] = new Organism(i, j);
                if (matrix[i][j].getAlive()) {
                    aliveCount++;
                }
            }
        }
        this.aliveCount = aliveCount;
    }

    public void getNextGenerationData() {
        ////iterates through matrix; I,J and Alive are set for each organism.////
        ////the status of all neighboring organisms is checked and a neighborAliveCount is generated////
        ////the organisms field willSurvive is set based on the neighborAliveCount////
        ////the nextGenAliveCount will return the total alive organisms for the next generation////

        int i;
        int j;
        boolean alive;
        int nextGenAliveCount = 0;

        for(Organism[] row : matrix) {
            for (Organism org: row) {

                i = org.getI();
                j = org.getJ();
                alive = org.getAlive();
                int neighborsAliveCount = 0;

                if(getStatus(((i -1 + matrixSize) % matrixSize), j)) {
                    neighborsAliveCount++;
                }
                if(getStatus(((i - 1 + matrixSize) % matrixSize), ((j - 1 + matrixSize) % matrixSize))) {
                    neighborsAliveCount++;
                }
                if(getStatus(((i - 1 + matrixSize) % matrixSize), ((j + 1 + matrixSize) % matrixSize))) {
                    neighborsAliveCount++;
                }
                if(getStatus((i), ((j - 1 + matrixSize) % matrixSize))) {
                    neighborsAliveCount++;
                }
                if(getStatus((i), ((j + 1 + matrixSize) % matrixSize))) {
                    neighborsAliveCount++;
                }
                if(getStatus(((i + 1 + matrixSize) % matrixSize), (j))) {
                    neighborsAliveCount++;
                }
                if(getStatus(((i + 1 + matrixSize) % matrixSize), ((j - 1 + matrixSize) % matrixSize))) {
                    neighborsAliveCount++;
                }
                if(getStatus(((i + 1 + matrixSize) % matrixSize), ((j + 1 + matrixSize) % matrixSize))) {
                    neighborsAliveCount++;
                }
                if(alive) {
                    if(neighborsAliveCount != 2 && neighborsAliveCount != 3) {
                        org.setWillSurvive(false);
                    } else {
                        org.setWillSurvive(true);
                        nextGenAliveCount++;
                    }
                } else if (!alive) {
                    if(neighborsAliveCount == 3) {
                        org.setWillSurvive(true);
                        nextGenAliveCount++;
                    } else {
                        org.setWillSurvive(false);
                    }
                }
            }
        }
        aliveCount = nextGenAliveCount;
    }

    public void setNextGeneration() {
        for(Organism[] line: matrix) {
            for(Organism org: line) {
                org.setNextGeneration();
            }
        }
    }

    public void outputCurrentGeneration() {
        boolean[][] booleanMatrix = new boolean[matrixSize][matrixSize];

        ////converts organism matrix to easily movable boolean matrix////
        for(Organism[] row: matrix) {
            for(Organism org: row) {
                int i = org.getI();
                int j = org.getJ();
                booleanMatrix[i][j] = org.getAlive();
            }
        }

        ////passes boolean matrix to Game of Life controller and then to the MapPanel////
        updater.passNextGenData(booleanMatrix, aliveCount);
    }

    public boolean getStatus(int i, int j) {
        ////retrieves Organism alive status////
        return matrix[i][j].getAlive();
    }

    public void setMatrixSize(int matrixSize) {
        this.matrixSize = matrixSize;
    }

    public void setUpdater(Updater updater) {
        this.updater = updater;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    class Organism{
        private Boolean alive;
        private int i;
        private int j;
        private boolean willSurvive;

        public Organism(int i, int j) {
            this.alive = random.nextBoolean();
            this.i = i;
            this.j = j;
        }

        public void setWillSurvive(boolean willSurvive) {
            this.willSurvive = willSurvive;
        }

        public void setNextGeneration() {
            this.alive = this.willSurvive;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        public Boolean getAlive() {
            return alive;
        }
    }
}

interface Updater {
    ////connects controller to components////
    void passNextGenData(boolean[][] booleanMatrix, int generationAliveCount);

    void passData(int i);

    void start();

    void pause();

    void reset();
}

////Run Program////
class Main{
    public static void main(String[] args) {
        GameOfLife component = new GameOfLife();

    }
}








