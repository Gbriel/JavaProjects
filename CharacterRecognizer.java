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
  public CharacterRecognizer() {
    NN = new NeuralNet(256,20,10,0.2);
    DataSet data = processData();
    
    //seperate data into test and training sets
    int testLength = 150;
    int trainLength = data.targets.length - testLength;
    boolean[][] trainImages = new boolean[trainLength][256];
    int[] trainTargets = new int[trainLength];
    boolean[][] testImages = new boolean[testLength][256];
    int[] testTargets = new int[testLength];
    
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
    
    //train
    NN.train(trainImages,trainTargets,30);
    //test on the test set
    System.out.println(classifyTestSet(testImages,testTargets));
    
    
  }
  
  //classifies the entire test set, returns the "correct classification" rate
  public double classifyTestSet(boolean[][] testImages, int[] targets) {
    return NN.test(testImages,targets);
  }
  
  //classify a single image whose correct class is known
  public void classifyImage(boolean[] image, int target) {
  //draw image from pixels
    for(int i = 0; i < 256;i++) {
      if(image[i]) StdDraw.filledSquare((i%16 + 1)/16.0,1-(i/16 +1)/16.0,1/16.0);
      else StdDraw.square((i%16 + 1)/16.0,1-(i/16 + 1)/16.0,1/16.0);
    }
    //classify, and output the results
    int result = NN.classify(image);
    String res;
    if(result==target) res = "Correctly";
    else res = "Incorrectly";
    System.out.println(res+ " classified " +target + " as " + result);
  }
  public void table(boolean[][] trainImages, int[] trainTargets, boolean[][] testImages, int[] testTargets) {
      //cycle through alpha, training reps, hidden neurons      
      double[] scores = new double[125];
      System.out.println("score | trnReps | alpha | hidden");
      for(int hiddenNeurons = 1; hiddenNeurons < 6; hiddenNeurons++) {
        for(int alpha = 0; alpha < 5; alpha++) {
          for(int trainingReps = 0; trainingReps < 5; trainingReps++) {    
            NeuralNet NN = new NeuralNet(256,hiddenNeurons*20 + 1,10,alpha*0.1 + 0.01);
            NN.train(trainImages,trainTargets,trainingReps*9 + 1);
            double score = NN.test(testImages,testTargets);
            System.out.println("---------------------------------");
            System.out.printf("%7d %7.2f %7d %7.2f \n",(hiddenNeurons*20 + 1),(alpha*0.1 + 0.01),(trainingReps*9 + 1),score);
          } 
        }      
      }
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
  
  private DataSet processData() {
    try{
      boolean[][] images = new boolean[1593][256];
      int index = 0;
      int[] targets = new int[1593];
      Scanner in = new Scanner(Paths.get("digitdata.txt"));
  
      while(in.hasNextLine()) {

      //preprocess the digitdata file via regex
        String[] split = in.nextLine().split(" ");
        //create a boolean array, the format used by the NN
        boolean[] px = new boolean[256];
        for(int i = 0; i < 256; i++) {
          if(split[i].equals("1.0000")) px[i] =  true;
          else px[i] = false;
        }
        images[index] = px;
        //get the target value
        int target = 0;
        
        for(int i = 256; i < 266; i++) {
          if(split[i].equals("1")) { 
            target = i - 256;
        //    targetSpread[target]++;
            break;
          }
        }
        targets[index] = target;
        index++;
      }
   
      DataSet data = new DataSet();
      data.images = images;
      data.targets = targets;
      return data;
    } catch (IOException e) { System.err.println(e.getMessage()); }
    return null;
  }
  
  public static void main(String[] args) {
    CharacterRecognizer CR = new CharacterRecognizer();
  }
  
  private class DataSet {
    public boolean[][] images;
    public int[] targets;
  }
}

