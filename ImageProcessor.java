import java.io.*;
import java.awt.*;
import java.nio.file.*;
import java.awt.image.*;
import javax.imageio.*;
/*
specifically tailored to process the images in the files
JavaProjects/Img/*
process images for a handwritten character recognition program.
this class just has a static method that takes an image file 
and a trimming argument and returns a trimmed & stretched 
256 slot boolean array.

0-9 1-10
A-Z 11-36
a-z 37-62

one processing step to consider going forward is
Convolving - edge detection & inversion are useful
tools for analyzing photographic images
*/
public class ImageProcessor {
  int lowestDim = 500;
  int lowestForCurrentCatagory = 600;
  public static void main(String[] args) {
    ImageProcessor ip = new ImageProcessor();
    ip.process("Img",16,0.3);
  }
  
  public ImageProcessor() {
  }
  
  public void process(String filename,int size, double threshold) {
    Path images = Paths.get(filename);
   /*
    try{
    BufferedImage b = ImageIO.read(new File("Img/Sample039/img039-001.png"));
                 processByMagnitude(b);
     } catch(IOException e) {System.out.println("err0");}
  */
  int imgCount = 0;
    try (DirectoryStream<Path> entries = Files.newDirectoryStream(images)) {
      FileWriter fw = new FileWriter(filename+"Processed");
      for(Path p : entries) {
        File f = p.toFile();
        if(f.isDirectory()) {
          int category =Integer.parseInt(f.getName().substring(7,9)) -1;
     //     System.out.println(category);
          lowestForCurrentCatagory=600;
           try (DirectoryStream<Path> ims = Files.newDirectoryStream(Paths.get(f.toString()))) {
             for(Path p2 : ims) {
               
               try {
               
                 BufferedImage bf = ImageIO.read(p2.toFile());
                 
     //            System.out.println(p2.toFile());
                 int[][] shrunk = shrink(trim(processByMagnitude(bf)),size,threshold);
                 for(int i = 0; i < shrunk.length; i++) {
                   for(int j = 0; j < shrunk.length; j++) {
                     fw.write(shrunk[i][j] + " ");
                   }
                 }
                 fw.write(category + "\n");
                 
                 imgCount++;
               } catch(IOException e) {System.err.println("err2");}
             }
           }
        }
   //     System.out.println("\t" + lowestForCurrentCatagory);
      }
      fw.close();
    } catch(IOException e) {System.err.println("err1");}
  //  System.out.println(lowestDim);
 //   System.out.println("lc: " + imgCount);
  }
  
  //converts image file to int[][]
  public int[][] processByMagnitude(BufferedImage im) {
     int h = im.getHeight();
     int w = im.getWidth();
     int onesCount = 0;
     int[][] results = new int[h][w];
     for (int row = 0; row < h; row++) {
	for (int col = 0; col < w; col++) {
           Color p = new Color(im.getRGB(col, row));
	   if(p.getRed()+p.getGreen()+p.getBlue() < 400) {
		results[row][col] = 1;
		onesCount++;
	   } else {
		results[row][col] = 0;
	   }
        }
      }
   //   System.out.println(onesCount);
      return results;
    }
    
    //trims whitespace from around the image such that the resulting image is a square
    public int[][] trim(int[][] ogIm) {
   //   System.out.print(ogIm.length + " " + ogIm[0].length);
      
      int lowColBound = 0;
      //check columns, going from 0 up 
      outerLoop:
      for (int col = 0; col < ogIm[0].length; col++) {
	for (int row = 0; row < ogIm.length; row++) {
	  if(ogIm[row][col] != 0) break outerLoop;
	}
	lowColBound++;
      }
      
      int highColBound = ogIm[0].length;
      //check columns, going from n down 
      outerLoop:
      for (int col = ogIm[0].length-1; col >=0 ; col--) {
	for (int row = 0; row < ogIm.length; row++) {
	  if(ogIm[row][col] !=0) break outerLoop;
	}
	highColBound--;
      }
      
      int lowRowBound = 0;
      //check rows, going from 0 up 
      outerLoop:
      for (int row = 0; row < ogIm.length; row++) {
	for (int col = 0; col < ogIm[0].length; col++) {
	  if(ogIm[row][col] != 0) break outerLoop;
	}
	lowRowBound++;
      }
      
      int highRowBound = ogIm.length;
      //check rows, going from n down 
      outerLoop:
      for (int row = ogIm.length-1; row >= 0; row--) {
	for (int col = 0; col < ogIm[0].length; col++) {
	  if(ogIm[row][col] !=0) break outerLoop;
	}
	highRowBound--;
      }
      
      int nh = highRowBound - lowRowBound;
      int nw = highColBound-lowColBound;
 //           System.out.print(" -> " + nh + " " + nw + "\n");
 
     //make the new array a square in the fatter dimension, so we wont get weird shrinking effects when we force it into a square in the shrinking method (to make it a uniform size image)
     
     //if the new height is > width
     
     //need to allow padding to shift in case it overlaps borders... or just fill padded spaces with 0's?
     int[][] trimmed;
      if(nh > nw) {
        trimmed = new int[nh][nh];
        int paddingL = (nh - nw)/2;
        for(int i = 0; i < nh; i++) {
          for(int j = 0; j < nh; j++) {
            if(lowColBound -paddingL+ j >= 0  && lowColBound -paddingL+ j < ogIm[0].length) {
              trimmed[i][j] = ogIm[lowRowBound+i][lowColBound -paddingL+ j];
            } else trimmed[i][j] = 0;
          }
        }
        //if the new width is the gr8er dim...
      } else {
        trimmed = new int[nw][nw];
        int paddingL = (nw - nh)/2;
        for(int i = 0; i < nw; i++) {
          for(int j = 0; j < nw; j++) {
            if(lowRowBound-paddingL+i >= 0 && lowRowBound-paddingL+i < ogIm.length) {
          //    System.out.println((lowRowBound-paddingL+i) + " " + (lowColBound + j));
              try {
              trimmed[i][j] = ogIm[lowRowBound-paddingL+i][lowColBound + j];
              } catch (ArrayIndexOutOfBoundsException e) { System.err.println((lowRowBound-paddingL+i) + " " + (lowColBound + j)); }
            } else {
              trimmed[i][j] = 0;
            }
          }
        }
      }
      int lowest = Math.max(nh,nw);
      if(lowest < lowestDim) lowestDim = lowest;
      if(lowest < lowestForCurrentCatagory) lowestForCurrentCatagory = lowest;
      return trimmed;
    }
    
    //shrink the image, into the given dimensions... new img will be a matrix sideLengthXsideLength big.
    // + return boolean[][]
    public int[][] shrink(int[][] toShrink, int sideLength, double threshold) {
      int h = toShrink.length;
      int w = toShrink[0].length;
      int oldRowsToNew = h/sideLength;
      int oldColsToNew = w/sideLength;
      int oldPixToNew = oldRowsToNew*oldColsToNew;
      int[][] shrunk = new int[sideLength][sideLength];
      for(int i = 0; i < sideLength; i++) {
        for(int j = 0; j < sideLength; j++) {
          int filled = 0;
          for(int k = i*oldRowsToNew; k < (i+1)*oldRowsToNew; k++) {
            for(int z = j*oldColsToNew; z < (j+1)*oldColsToNew; z++) {
              if (toShrink[k][z] == 1) filled++;
            }
          }
          if((filled*1.0)/oldPixToNew > threshold) shrunk[i][j]=1;
          else shrunk[i][j] = 0;
        }
      }
      return shrunk;
    }
}
