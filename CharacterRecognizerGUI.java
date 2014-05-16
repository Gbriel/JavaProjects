/*
A GUI for my handwritten character recognition program. 
Overall functionality is fairly limited - backpropagation
training with variable settings like the number of hidden
layer neurons to use.

5/11/2014
*/
import java.util.LinkedList;
import java.awt.image.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;
import static org.imgscalr.Scalr.*;
import java.awt.event.*;
import java.awt.color.*;
//main method just starts the GUI
public class CharacterRecognizerGUI{
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new CRFrame();
        frame.setTitle("Handwritten-Character Classifier Graphical User-Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
      }
    });
  }
}


class CRFrame extends JFrame {
  public static final char[] keys = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
  //***************** components ***********
  private CharacterRecognizer charRec = null;
  private JTextField testSetSize;
  private JComboBox<Integer> NNInputRes;
  private JTextField hiddenNeurons;
  private JTextField alpha;
  private JTextField trainReps;
  private JButton train;
  private JButton reset;
  private JLabel classRate;
  private JLabel trueClass;
  private JLabel predClass;
  private JLabel trained;
  //instead of having imageTwo be editable, i'd rather have it be a graph... of test & training err rates as a function of epochs
  private ImagePanel imageOne;
//  private ImagePanel imageTwo;
  private JButton next;
  private JButton previous;
  //******************end components section****************
  private int size;
  private int epochsTrained = 0;
  private BufferedImage fromFile = null;
  private boolean[][] testImages;
  private int[] testTargets;
  //index of the currently shown test set image
  private int testIndex = 0;
  
  public CRFrame() {
//not sure if this panel is necessary... might even be counterproductive. 
    JPanel panel = new JPanel();
    GridBagLayout layout = new GridBagLayout();
    panel.setLayout(layout);
    
    //create labels/input fields for training/NN input settings
    JLabel testSetSizeLabel = new JLabel("% data for test set: ");
    testSetSize = new JTextField("10",3);
    testSetSize.setMinimumSize(new Dimension(40,20));

    JLabel NNInputResLabel = new JLabel("NN Input size (^2): ");
    NNInputRes = new JComboBox<Integer>(new Integer[] { 16, 5, 10, 14, 18, 22, 26, 30,40,56,128 });

    
    JLabel hiddenNeuronsLabel = new JLabel("# hidden layer neurons: ");
    hiddenNeurons = new JTextField("100",3);
    hiddenNeurons.setMinimumSize(new Dimension(40,20));;
    
    JLabel alphaLabel = new JLabel("learning rate (alpha): ");
    alpha = new JTextField("0.3",3);
    alpha.setMinimumSize(new Dimension(40,20));
    
    JLabel trainRepsLabel = new JLabel("epochs to train: ");
    trainReps = new JTextField("100",3);
    trainReps.setMinimumSize(new Dimension(40,20));
    
    trained = new JLabel("Epochs Trained: 0");
    
    train = new JButton("TRAIN");
    train.addActionListener(new TrainAction());
    reset = new JButton("RESET NN");
    reset.addActionListener(new ResetAction());
    //initialize NN and stuff (CharRecognizer)
    double a = Double.parseDouble(alpha.getText());
    int hidden = Integer.parseInt(hiddenNeurons.getText());
    int testSet = Integer.parseInt(testSetSize.getText());
    size = (Integer)NNInputRes.getSelectedItem();
    charRec = new CharacterRecognizer(size,hidden,62,a,testSet);
    DataSet testdata = charRec.getTestSet();
    testImages = testdata.images;
    testTargets = testdata.targets;
    testIndex = 0;
    
    trueClass = new JLabel("Class: " + keys[testTargets[0]]);
    predClass = new JLabel("Predicted Class: "+ keys[charRec.getClass(testImages[0])]);
    
    classRate = new JLabel("Classification Rate: ");
    //these should be initialized to a blank grid and a blank pic with some text
    imageOne = new ImagePanel(imgFromBool(testImages[0]));
     imageOne.setPreferredSize(new Dimension(300,300));
 //   imageTwo = new ImagePanel(createThumbnail(ImageIO.read(new File("Img/Sample001/img001-001.png"))));
 //   imageTwo.setPreferredSize(new Dimension(350,300));
    next = new JButton("Next Image");
    next.addActionListener(new NextAction());
    previous = new JButton("Previous Image");
    previous.addActionListener(new PreviousAction());
    //create menu bar using the helper class. This should probably become just a button below the sample pic on the right side of the frame
    
    //add the items to the gridbaglayout using the GBC helper class (corejava v1ch9)
    panel.add(testSetSizeLabel,new GBC(0,0).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    panel.add(testSetSize,new GBC(1,0).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    panel.add(hiddenNeuronsLabel,new GBC(0,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    panel.add(hiddenNeurons,new GBC(1,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    panel.add(alphaLabel,new GBC(0,2).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    panel.add(alpha,new GBC(1,2).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    panel.add(trainRepsLabel,new GBC(0,4).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    panel.add(trainReps,new GBC(1,4).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    panel.add(NNInputResLabel,new GBC(0,3).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    panel.add(NNInputRes,new GBC(1,3).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    panel.add(trained,new GBC(0,5,1,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    panel.add(train,new GBC(1,5,1,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(3));
    panel.add(reset,new GBC(0,7,2,1).setAnchor(GBC.CENTER).setWeight(0,0).setInsets(3));
    panel.add(classRate,new GBC(0,6,2,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(10));
    panel.add(imageOne,new GBC(2,0,2,6).setAnchor(GBC.WEST).setFill(GBC.BOTH).setWeight(100,100).setInsets(5));
//    panel.add(imageTwo,new GBC(4,0,2,5).setAnchor(GBC.WEST).setFill(GBC.BOTH).setWeight(100,100).setInsets(5));
    
    panel.add(predClass, new GBC(2,6).setAnchor(GBC.WEST).setWeight(0,0).setInsets(3));
    panel.add(trueClass, new GBC(3,6).setAnchor(GBC.CENTER).setWeight(0,0).setInsets(3));
    panel.add(previous,new GBC(2,7,1,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(3));
    panel.add(next,new GBC(3,7,1,1).setAnchor(GBC.CENTER).setWeight(0,0).setInsets(3));
    JMenuBar menuBar = new ImageViewerMenu(new JFileChooser(),imageOne,this);
    add(menuBar);
    add(panel);
    pack();
    setJMenuBar(menuBar);
  }
  
  public static BufferedImage createThumbnail(BufferedImage img) {
  // Create quickly, then smooth and brighten it.
  img = resize(img, Method.SPEED, 300, OP_ANTIALIAS, OP_BRIGHTER);
 
  return img;
  }
  
  public BufferedImage imgFromBool(boolean[] im) {
    BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster raster = bi.getRaster();
    for(int i = 0; i < size*size; i++) {
      if(!im[i]) raster.setSample(i%size,i/size,0,255);
      else raster.setSample(i%size,i/size,0,0);
    }
   return bi;
   
  }
  
  public void updateClassificationForImageFromFile(BufferedImage bf) {
    int[][] shrunk = ImageProcessor.shrink(ImageProcessor.trim(ImageProcessor.processByMagnitude(bf)),size,0.3);
    boolean[] bools = new boolean[size*size];
    for(int i = 0; i < size; i++) {
      for(int j = 0; j < size; j++) {
        if((shrunk[i][j])==1)bools[i*size + j] = true;
        else bools[i*size + j] = false;
      }
    }
    predClass.setText("Predicted Class: " + keys[charRec.getClass(bools)]);
    trueClass.setText("Class: ?"); 
  }
  
  //reset the characterrecognizer, if already initialized
  private class ResetAction implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      double a = Double.parseDouble(alpha.getText());
      int hidden = Integer.parseInt(hiddenNeurons.getText());
      int testSet = Integer.parseInt(testSetSize.getText());
      size = (Integer)NNInputRes.getSelectedItem();
      if(a > 0.0001 && hidden > 0 && testSet > 0) {
        charRec = new CharacterRecognizer(size,hidden,62,a,testSet);
        DataSet testdata = charRec.getTestSet();
        testImages = testdata.images;
        testTargets = testdata.targets;
        testIndex = 0;
        imageOne.setImage(imgFromBool(testImages[0]));
        predClass.setText("Predicted Class: " + keys[charRec.getClass(testImages[0])]);
        trueClass.setText("Class: "  + keys[testTargets[0]]);
        classRate.setText("Classification Rate: ");
        trained.setText("Epochs Trained: 0");
        epochsTrained =0;
        repaint();
      }// else throw new java.util.InputMismatchException();
    //  } catch (Exception e) { System.err.println(e.getMessage() + " please enter valid parameters. Stack... " + e.getStackTrace()); }
    }
  }
  //react to the train button being pressed
  private class TrainAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      double classificationRate=-1;
      int epochs = Integer.parseInt(trainReps.getText());
      epochsTrained+=epochs;
      if(epochs > 0) {
        classificationRate= charRec.trainAndReport(epochs);
      }
      classRate.setText("Classification Rate: " + classificationRate);
      predClass.setText("Predicted Class: " + keys[charRec.getClass(testImages[testIndex])]);
      trueClass.setText("Class: "  + keys[testTargets[testIndex]]);
      trained.setText("Epochs Trained: " + epochsTrained);
    }
  }


private class NextAction implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    testIndex = (testIndex+1)%testImages.length;
    imageOne.setImage(imgFromBool(testImages[testIndex]));
    predClass.setText("Predicted Class: " + keys[charRec.getClass(testImages[testIndex])]);
    trueClass.setText("Class: "  + keys[testTargets[testIndex]]);
    repaint();
  }
}

private class PreviousAction implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    if(testIndex == 0) testIndex = testImages.length;
    testIndex--;
    imageOne.setImage(imgFromBool(testImages[testIndex]));
    predClass.setText("Predicted Class: " + keys[charRec.getClass(testImages[testIndex])]);
    trueClass.setText("Class: "  + keys[testTargets[testIndex]]);
    repaint();
  }
}

//gotta make a method to build bufferedImages from boolean arrays, and another to draw directly from pixel arrays, maybe? 
private class ClickListener extends MouseAdapter {
  public void mouseClicked(MouseEvent e) {
    
  }
} 
}
class ImagePanel extends JPanel{
  private BufferedImage image;
  public ImagePanel(BufferedImage im) {
    this.image = im;
  }
  
  public void setImage(BufferedImage im) {
    this.image = im;
    updateUI();
    repaint();
  }
  public void paintComponent(Graphics g) {
   int dim = Math.min(this.getWidth(),this.getHeight());
    BufferedImage img = resize(image, Method.SPEED, dim, OP_ANTIALIAS, OP_BRIGHTER);
    g.drawImage(img,0,0,null);
    //imagescalr package shows it's use.
    int prefDim = Math.max(dim,300);
    setPreferredSize(new Dimension(prefDim,prefDim));
  }
}
