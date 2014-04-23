/*
  handwriten digit (and char?) identifier
  via NN. Will take an image (raster?)
  cut the whitespace from top and bottom,
  and stretch it to 256X256.
  
  Will include a main method for testing,
  and would like to keep the code well-
  commented and VC'd via Git.
  
  Gabriel Kopito
  4/18/2014
*/
import java.io.*;
import java.util.*;
import java.nio.file.*;
public class CharacterRecognizer {
//main method will initialize and train NN
// read in images from files for training 
//and testing. Thus far it's just a main method...
//what will it be later? create multiple NN's from 
//saved weights and use a pool of threads to classify?
//oh, this will only ever worry about single characters,
//while higher classes will worry about documents.
  public CharacterRecognizer() {
    NeuralNet NN = new NeuralNet(256,40,10);
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
    
    //plot the efficacy of the NN on the test set vs. the number of times through the training loop
    double trainReps = 100;
    for(int i = 0; i < trainReps; i++) {
      NN.train(trainImages,trainTargets,i);
      StdDraw.point(i/trainReps,NN.test(testImages,testTargets));
    }
    
    //todo... add a graph showing the results of adjusting alpha. See if we hit any local minima when it gets too low, or fail to converge with it too large. We could create a table showing for each alpha (.05 increments?) the max test score it got (average three trials at that number of trainReps) and the number of trainReps it took to get that. Output the one with the highest test score and the lowest train reps.
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

