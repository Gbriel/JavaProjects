/*
A GUI for my handwritten character recognition program. 
Overall functionality is fairly limited - backpropagation
training with variable settings like the number of hidden
layer neurons to use.

5/11/2014
*/

import java.awt.image.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;
import static org.imgscalr.Scalr.*;

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
  private static final int DEFAULT_WIDTH = 800;
   private static final int DEFAULT_HEIGHT = 500;

  public CRFrame() {

    JPanel panel = new JPanel();
    GridBagLayout layout = new GridBagLayout();
    panel.setLayout(layout);
    
    JLabel testSetSizeLabel = new JLabel("% data for test set: ");
    JTextField testSetSize = new JTextField("10",3);
    testSetSize.setMinimumSize(new Dimension(40,20));

    JLabel NNInputResLabel = new JLabel("NN Input size (^2): ");
    JComboBox NNInputRes = new JComboBox<>(new Integer[] { 16, 5, 10, 14, 18, 22, 26, 30,40,56,128 });

    
    JLabel hiddenNeuronsLabel = new JLabel("# hidden layer neurons: ");
    JTextField hiddenNeurons = new JTextField("40",3);
    hiddenNeurons.setMinimumSize(new Dimension(20,20));;
    
    JLabel alphaLabel = new JLabel("learning rate (alpha): ");
    JTextField alpha = new JTextField("0.3",3);
    alpha.setMinimumSize(new Dimension(40,20));
    
    JLabel trainRepsLabel = new JLabel("training epochs: ");
    JTextField trainReps = new JTextField("100",3);
    trainReps.setMinimumSize(new Dimension(40,20));
    
    JButton train = new JButton("TRAIN");
    JButton reset = new JButton("RESET WEIGHTS");
    try {
    ImagePanel imageOne = new ImagePanel(createThumbnail(ImageIO.read(new File("Img/Sample001/img001-001.png"))),this);
     imageOne.setPreferredSize(new Dimension(300,300));
    ImagePanel imageTwo = new ImagePanel(createThumbnail(ImageIO.read(new File("Img/Sample001/img001-001.png"))),this);
    imageTwo.setPreferredSize(new Dimension(350,300));
    JMenuBar menuBar = new ImageViewerMenu(new JFileChooser(), new JLabel(),CRFrame.this);
    
    panel.add(testSetSizeLabel,new GBC(0,0).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    panel.add(testSetSize,new GBC(1,0).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    panel.add(hiddenNeuronsLabel,new GBC(0,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    panel.add(hiddenNeurons,new GBC(1,1).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    panel.add(alphaLabel,new GBC(0,2).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    panel.add(alpha,new GBC(1,2).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    panel.add(trainRepsLabel,new GBC(0,3).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    panel.add(trainReps,new GBC(1,3).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    panel.add(NNInputResLabel,new GBC(0,4).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    panel.add(NNInputRes,new GBC(1,4).setAnchor(GBC.WEST).setWeight(0,0).setInsets(5));
    
    panel.add(train,new GBC(0,5,2,1).setAnchor(GBC.CENTER).setWeight(0,0).setInsets(3));
    
    panel.add(imageOne,new GBC(2,0,2,6).setAnchor(GBC.WEST).setFill(GBC.BOTH).setWeight(100,100).setInsets(5));
    panel.add(imageTwo,new GBC(4,0,2,6).setAnchor(GBC.WEST).setFill(GBC.BOTH).setWeight(100,100).setInsets(5));
    add(menuBar);
    add(panel);
    pack();
    setJMenuBar(menuBar);
    } catch (IOException e) {System.err.println(e.getMessage()); System.exit(0);}
  }
  
  public static BufferedImage createThumbnail(BufferedImage img) {
  // Create quickly, then smooth and brighten it.
  img = resize(img, Method.SPEED, 300, OP_ANTIALIAS, OP_BRIGHTER);
 
  return img;
  }
}

class ImagePanel extends JPanel{
  private BufferedImage image;
  private JFrame frame;
  public ImagePanel(BufferedImage im, JFrame f) {
    this.image = im;
    frame = f;
  }
  public void paintComponent(Graphics g) {
    BufferedImage img = resize(image, Method.SPEED, Math.max(Math.min(this.getWidth(),this.getHeight()),300), OP_ANTIALIAS, OP_BRIGHTER);
    g.drawImage(img,0,0,null);
    setPreferredSize(new Dimension(Math.max(this.getWidth(),300),Math.max(this.getHeight(),300)));
  //  frame.pack();
    System.out.println("w: " + this.getWidth() + "h: " + this.getHeight());
  }
} 
