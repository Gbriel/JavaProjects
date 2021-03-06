/*
A GUI for my handwritten character recognition program. 
Overall functionality is fairly limited - NN uses backpropagation
training. This program allows the user to set variables like 
the number of hidden layer neurons, the learning rate parameter, etc.
5/11/2014
Gabriel Kopito

A few of the classes used are taken from, or are modified versions
of classes from CoreJava, Volume 1, 9th edition.
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


public class CharacterRecognizerGUI{

//main method just starts the GUI
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

//the image files are classified by a number, this keys array allows for conversion of that number to the corresponding symbol.
  public static final char[] keys = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
  
  /***************** JComponents ***********
  for displaying and or inputing information
  */
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
  private ImagePanel imageOne;
  private JButton next;
  private JButton previous;
  //******************end JComponents section****************
  
  /*
  the width/height of the image... a setting of 16 corresponds to feeding the NN 
  256 pixel resolution of the image, while a value of 56 corresponds to sending
  a 56x56 image...
  */
  private int size;
  
  //a counter for how trained the current NN is
  private int epochsTrained = 0;
  
  // the test set images for a given CharacterRecognizer instance... and their classes
  private boolean[][] testImages;
  private int[] testTargets;
  //index of the currently shown test set image
  private int testIndex = 0;
  
  //most of the work happens here. 
  public CRFrame() {

    GridBagLayout layout = new GridBagLayout();
    setLayout(layout);
    
    //create labels/input fields for training/NN input settings
    
    //this controls the % of the dataset to be reserved for testing
    JLabel testSetSizeLabel = new JLabel("% data for test set: ");
    testSetSize = new JTextField("10",3);
    testSetSize.setMinimumSize(new Dimension(40,20));

    //the number of "pixels" the image should be scaled down to, in order to be used as input for the NN
    JLabel NNInputResLabel = new JLabel("NN Input size (^2): ");
    NNInputRes = new JComboBox<Integer>(new Integer[] { 16, 5, 10, 14, 18, 22, 26, 30,40,56});

    //the number of hidden neurons the NN will be instantiated with
    JLabel hiddenNeuronsLabel = new JLabel("# hidden layer neurons: ");
    hiddenNeurons = new JTextField("100",3);
    hiddenNeurons.setMinimumSize(new Dimension(40,20));;
    
    //the learning rate parameter (adjusts the rate at which the NNs weights change)
    JLabel alphaLabel = new JLabel("learning rate (alpha): ");
    alpha = new JTextField("0.3",3);
    alpha.setMinimumSize(new Dimension(40,20));
    
    //epochs to train in one click of the train button
    JLabel trainRepsLabel = new JLabel("epochs to train: ");
    trainReps = new JTextField("100",3);
    trainReps.setMinimumSize(new Dimension(40,20));
    
    //just a label displaying how trained the NN is
    trained = new JLabel("Epochs Trained: 0");
  
  // the buttons for training and reseting, initialized with their appropriate actions...  
    train = new JButton("TRAIN");
    train.addActionListener(new TrainAction());
    reset = new JButton("RESET NN");
    reset.addActionListener(new ResetAction());
    
    //*********initialize NN and stuff (CharRecognizer) *****************
    //initialize the NN on the default settings. NN is not trained.
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
    //******************************************************************8
     
    // add the jcomponents to the frame one by one.
    imageOne = new ImagePanel(imgFromBool(testImages[0]));
     imageOne.setPreferredSize(new Dimension(300,300));
    next = new JButton("Next Image");
    next.addActionListener(new NextAction());
    previous = new JButton("Previous Image");
    previous.addActionListener(new PreviousAction());
    //create menu bar using the helper class. This should probably become just a button below the sample pic on the right side of the frame
    
    //add the items to the gridbaglayout using the GBC helper class (corejava v1ch9)
    add(testSetSizeLabel,new GBC(0,0).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    add(testSetSize,new GBC(1,0).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    add(hiddenNeuronsLabel,new GBC(0,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    add(hiddenNeurons,new GBC(1,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    add(alphaLabel,new GBC(0,2).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    add(alpha,new GBC(1,2).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    add(trainRepsLabel,new GBC(0,4).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    add(trainReps,new GBC(1,4).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    add(NNInputResLabel,new GBC(0,3).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    add(NNInputRes,new GBC(1,3).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    add(trained,new GBC(0,5,1,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    add(train,new GBC(1,5,1,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(3));
    add(reset,new GBC(0,7,2,1).setAnchor(GBC.CENTER).setWeight(0,0).setInsets(3));
    add(classRate,new GBC(0,6,2,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(10));
    add(imageOne,new GBC(2,0,2,6).setAnchor(GBC.WEST).setFill(GBC.BOTH).setWeight(100,100).setInsets(5));
    
    add(predClass, new GBC(2,6).setAnchor(GBC.WEST).setWeight(0,0).setInsets(3));
    add(trueClass, new GBC(3,6).setAnchor(GBC.CENTER).setWeight(0,0).setInsets(3));
    add(previous,new GBC(2,7,1,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(3));
    add(next,new GBC(3,7,1,1).setAnchor(GBC.CENTER).setWeight(0,0).setInsets(3));
    
    //this menu bar is for loaded images from outside the test set for testing purposes. It's spiffy, displaying a thumbnail of the selected image. Code is modified from/taken from CoreJava v1.
    JMenuBar menuBar = new ImageViewerMenu(new JFileChooser(),imageOne,this);
    add(menuBar);
    setJMenuBar(menuBar);
  }
  
  //convert a boolean[] to a buffered image...
  public BufferedImage imgFromBool(boolean[] im) {
    BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster raster = bi.getRaster();
    for(int i = 0; i < size*size; i++) {
      if(!im[i]) raster.setSample(i%size,i/size,0,255);
      else raster.setSample(i%size,i/size,0,0);
    }
   return bi;
  }
  
  //classify, and update the displayed classification, for an image accessed from outside the program's usual test/training sets
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
  
  //reset the CharacterRecognizer
  private class ResetAction implements ActionListener {
    public void actionPerformed(ActionEvent event) {
    //pull values from their input fields
      double a = Double.parseDouble(alpha.getText());
      int hidden = Integer.parseInt(hiddenNeurons.getText());
      int testSet = Integer.parseInt(testSetSize.getText());
      size = (Integer)NNInputRes.getSelectedItem();
      //test a few parameters for legal values...
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
      }
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

//advance the displayed picture to the next one in the testImages array, update the classification info
  private class NextAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      testIndex = (testIndex+1)%testImages.length;
      imageOne.setImage(imgFromBool(testImages[testIndex]));
      predClass.setText("Predicted Class: " + keys[charRec.getClass(testImages[testIndex])]);
      trueClass.setText("Class: "  + keys[testTargets[testIndex]]);
      repaint();
    }
  }

//change the displayed image to the previous index of the testImages array, update the classification info
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
}

/*
Displays the an image. Has auto resizing capabilities.
*/
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
