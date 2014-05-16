import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

/**
 * A frame that has a menu for loading an image and a display area for the loaded image.
 */
public class ImageViewerMenu extends JMenuBar
{

   private JLabel label;
   private JFileChooser chooser;
   private ImagePanel panel;
   private CRFrame frame;
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
                  //display the image
                    BufferedImage bf = ImageIO.read(new File(name));
                    frame.updateClassificationForImageFromFile(bf);
                    panel.setImage(bf);
                    //pass to frame for conversion to boolean[], classification

                  } catch(IOException e) { System.err.println(e.getMessage()); }
               }
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

      // use a label to display the images

      // set up file chooser

      // accept all image files ending with .jpg, .jpeg, .gif
      /*
      final ExtensionFileFilter filter = new ExtensionFileFilter();
      filter.addExtension("jpg");
      filter.addExtension("jpeg");
      filter.addExtension("gif");
      filter.setDescription("Image files");
      */
      FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "gif","png");
      chooser.setFileFilter(filter);

      chooser.setAccessory(new ImagePreviewer(chooser));

      chooser.setFileView(new FileIconView(filter, new ImageIcon("palette.gif")));
   }
}
