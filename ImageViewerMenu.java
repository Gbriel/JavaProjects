import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * A frame that has a menu for loading an image and a display area for the loaded image.
 */
public class ImageViewerMenu extends JMenuBar
{

   private JLabel label;
   private JFileChooser chooser;
   private JFrame frame;

   public ImageViewerMenu(JFileChooser c,JLabel l,JFrame f)
   {      

      // set up menu bar
      this.chooser = c;
      this.label = l;
      this.frame = f;
      JMenu menu = new JMenu("File");
      this.add(menu);

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
                  label.setIcon(new ImageIcon(name));
                  frame.pack();
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
      add(label);

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
