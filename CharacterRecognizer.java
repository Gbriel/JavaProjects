/*
  handwriten digit (and char?) identifier
  via NN. Will take an image (raster?)
  cut the whitespace from top and bottom,
  and stretch it to 256X256.
  
  Will include a main method for testing,
  and would like to keep the code well-
  commented and VC'd via Git.
  
  Uses the StdDraw class from 
  <a href="http://introcs.cs.princeton.edu/15inout">Section 1.5</a>
  By Robert Sedgewick and Kevin Wayne
  
  Gabriel Kopito
  4/18/2014
*/
import java.io.*;
import java.util.*;
import java.nio.file.*;
public class CharacterRecognizer {
  private NeuralNet NN;
  private int size;
  private int imagePix;
  private boolean[][] trainImages;
  private int[] trainTargets;
  private boolean[][] testImages;
  private  int[] testTargets;
  
  public CharacterRecognizer(int size, int hiddenLength,int outputLength, double alpha,int testPercent) {
    this.size = size;
    imagePix = size*size;
    NN = new NeuralNet(imagePix,hiddenLength,outputLength,alpha);
    DataSet data = processData(); 
    int testLength = (int)(0.01*data.targets.length*testPercent);
    //seperate data into test and training sets
    int trainLength = data.targets.length - testLength;
    trainImages = new boolean[trainLength][imagePix];
    trainTargets = new int[trainLength];
    testImages = new boolean[testLength][imagePix];
    testTargets = new int[testLength];
    
    //use reservoir algorithm to choose a random subset of the indicies for testing
    int[] randomIndicies = reservoirAlgorithm(testLength,data.targets.length);
    Arrays.sort(randomIndicies);
    int randomIndex = 0;
    int trainIndex = 0;
    
    //construct test and training sets using auxilliary randomIndicies[]
    for(int i = 0; i < data.targets.length; i++) {
      if(randomIndex < testLength && i == randomIndicies[randomIndex]) {
        testTargets[randomIndex] = data.targets[i];
        testImages[randomIndex] = data.images[i];
        randomIndex++;
      } else {
        trainTargets[trainIndex] = data.targets[i];
        trainImages[trainIndex] = data.images[i];
        trainIndex++;
      }
    }
    /*
    trainAndReport(100);
    for(int i = 50; i < 51; i++) {
      classifyImage(testImages[i],testTargets[i]);
    }
   */
  }
   
  
  //train by #epochs before reporting the classification rate
  public double trainAndReport(int epochs) {
    NN.train(trainImages,trainTargets,epochs);
    return NN.test(testImages,testTargets);
  }
  
  //classifies the entire test set, returns the "correct classification" rate
  public double classifyTestSet(boolean[][] testImages, int[] targets) {
    return NN.test(testImages,targets);
  }
  
  //classify a single image whose correct class is known
  public void classifyImage(boolean[] image, int target) {
  //draw image from pixels
  /*
    for(int i = 0; i < imagePix;i++) {
      if(image[i]) StdDraw.filledSquare((i%size + 1)/(size*1.0),1-(i/size +1)/(size*1.0),1/(size*1.0));
      else StdDraw.square((i%size + 1)/(size*1.0),1-(i/size + 1)/(size*1.0),1/(size*1.0));
    }
    */
    //classify, and output the results
    int result = NN.classify(image);
    String res;
    if(result==target) res = "Correctly";
    else res = "Incorrectly";
    System.out.println(res+ " classified " +target + " as " + result);
  }
  
  public int getClass(boolean[] image) {
    return NN.classify(image);
  }
  
  public DataSet getTestSet() {
    DataSet testset = new DataSet();
    testset.images = testImages;
    testset.targets = testTargets;
    return testset;
  }
  
  //select a random set of indicies to use for the test set
  public int[] reservoirAlgorithm(int k, int length) {
    int[] indicies = new int[k];
    for(int i = 0; i < length; i++) {
      if(i < k) {
        indicies[i] = i;
      } else if (Math.random() < (k*1.0)/(i+1)) {
        indicies[(int)(Math.random()*k)] = i;
      }
    }
    return indicies;
  }
  
  //written specifically to process files called "ImgProcessed<#>"
  private DataSet processData() {
    try{
      boolean[][] images = new boolean[3410][imagePix];
      int index = 0;
      int[] targets = new int[3410];
    /*  Old version using Paths and Scanner. Doesn't work within .jar files?
      Scanner in = new Scanner(Paths.get("ImgProcessed"+size));
      while(in.hasNextLine()) {
      //preprocess the digitdata file via regex
        String[] split = in.nextLine().split(" ");
        
    */
      InputStream is = getClass().getResourceAsStream("ImgProcessed"+size);
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line;
        while ((line = br.readLine()) != null) {
        //preprocess the digitdata file via regex
        String[] split = line.split(" ");
        //create a boolean array, the format used by the NN
        boolean[] px = new boolean[imagePix];
        for(int i = 0; i < imagePix; i++) {
          if(split[i].equals("1")) px[i] =  true;
          else px[i] = false;
        }
        images[index] = px;
        //get the target value
        int target = 0;
     
          target = Integer.parseInt(split[imagePix]);

        targets[index] = target;
        index++;
      }
      br.close();
      isr.close();
      is.close();
      DataSet data = new DataSet();
      data.images = images;
      data.targets = targets;
      return data;
    } catch (IOException e) { System.err.println(e.getMessage() + " arrrg, error reading in image data."); }
    return null;
  }
  //process the data, create a NN, still need to call "trainAndReport()" to train
  public static void main(String[] args) {
    CharacterRecognizer CR = new CharacterRecognizer(16,40,62,0.3,15);
    System.out.println(CR.trainAndReport(50));
  }
  
  
}
class DataSet {
    public boolean[][] images;
    public int[] targets;
  }
