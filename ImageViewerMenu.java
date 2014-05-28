import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.awt.*;

/**
 * A frame that has a menu for loading an image and a display area for the loaded image.
 */
public class ImageViewerMenu extends JMenuBar
{

   private JLabel label;
   private JFileChooser chooser;
   private ImagePanel panel;
   private CRFrame frame;
   private String helpcontent = "\tThis program is a work in progress. It's primary purpose is to test Neural Networks created with various parameter combinations, in order to compare their classification rates. This program is currently trained to recognize 62 possible classes (0-9,a-z,A-Z). You can test your own input images via the menu bar's 'open' option. \n\tHere is a brief explaination of the various fields (top to bottom, left to right):\n\nFirst Column\n\n\t % data: The percent of the program's image database to be used exclusively for testing. There is no overlap between the testing and training sets, and the larger the test set, the smaller the training set. \n\t# hidden neurons: The number of hidden layer neurons used by the underlying Neural Net. Note that as with most of the other input fields, this only is updated when the NN is reset.\n\tlearning rate: This reflects the amount the neural net will update its weights after each training epoch. A higher rate means change will happen faster, but could also mean the NN will fail to settle on a setting that minimizes the error rate.\n\tNN input size: This is the resolution that the NN receives the image as. A setting of 16 means that the neural net will get 16X16=256pixels of input. The finer grained the input, the more input neurons the program will use, but after a certain point, there will be vastly more input pixels than training example images. (There are only ~60 of each character for training.\n\tEpochs to train: The number of times the neural net should loop over the same set of training inputs. Excessive repetitions often lead to overfitting.\n\tEpochs Trained: The number of epochs the current NN has been trained. The initial NN is untrained.\n\tTrain: Train the current NN by the number of epochs listed in 'epochs to train'.\n\tClassification Rate: The % of the test set images correctly classified.\n\tReset NN: Create a new Neural Net, using the values in the fields above as its parameters.\n\n Second Column \n\n\t(Image) This shows as default an image from the test set (whose predicted and correct classifications are displayed below it). If an image is selected from the 'menu bar' it will show in this space, though its proper classification will display as '?'.";
   public ImageViewerMenu(JFileChooser c,ImagePanel ip,CRFrame f)
   {      

      // set up menu bar
      this.chooser = c;
      this.panel = ip;
      JMenu menu = new JMenu("File");
      this.add(menu);
      this.frame = f;
      JMenuItem openItem = new JMenuItem("Open");
      menu.add(openItem);
      openItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               chooser.setCurrentDirectory(new File("."));
   
               // show file chooser dialog
               int result = chooser.showOpenDialog(ImageViewerMenu.this);
   
               // if image file accepted, set it as icon of the label
               if (result == JFileChooser.APPROVE_OPTION)
               {
                  String name = chooser.getSelectedFile().getPath();
                  try {
                  //display the image and update the displayed classification
                    BufferedImage bf = ImageIO.read(new File(name));
                    frame.updateClassificationForImageFromFile(bf);
                    panel.setImage(bf);

                  } catch(IOException e) { System.err.println(e.getMessage()); }
               }
            }
         });
 
      JMenuItem helpItem = new JMenuItem("Help");
      menu.add(helpItem);
      helpItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               
   
               // show file chooser dialog
               JDialog help = new JDialog(null,"HELP",Dialog.ModalityType.MODELESS);
               JTextArea content = new JTextArea(helpcontent);
               content.setEditable(false);
               content.setLineWrap(true);
               content.setWrapStyleWord(true);
               JScrollPane scrollpane = new JScrollPane(content);
               
               help.add(scrollpane);
               help.setSize(500,300);
               help.setVisible(true);
              
            }
         });

 
      JMenuItem exitItem = new JMenuItem("Exit");
      menu.add(exitItem);
      exitItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               System.exit(0);
            }
         });

//filter to show only appropriate images. This can be broken in the program by selecting all files, but that's fine... plus, java can surely read lots of filetypes that aren't listed below.
      FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "gif","png");
      chooser.setFileFilter(filter);

      chooser.setAccessory(new ImagePreviewer(chooser));

      chooser.setFileView(new FileIconView(filter, new ImageIcon("palette.gif")));
   }
}
