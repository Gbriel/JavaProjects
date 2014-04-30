/*
  A back-propagation trained NN for classifation problems.
*/
import java.io.*;
import java.util.*;
import java.nio.file.*;
public class NeuralNet {
  private InputNeuron[] input;
  private SigmoidNeuron[] hidden;
  private SigmoidNeuron[] output;
  public final double alpha;
  
  //constructor 1.  Will make a 2ndary constructor that will take weights as input
  public NeuralNet(int inputLength, int hiddenLength,int outputLength,double a) {
    alpha = a;
  
    input= new InputNeuron[inputLength];
    hidden = new SigmoidNeuron[hiddenLength];
    output = new SigmoidNeuron[outputLength];
    
    //init the input neurons, since they won't receive the same args
    for(int j = 0; j < input.length; j++)
      input[j] = new InputNeuron();
      
     //init all the other layers, feeding them a reference to their inputs
    for(int j = 0; j < hidden.length; j++)
      hidden[j] = new SigmoidNeuron(input);
    for(int j = 0; j < output.length; j++)
      output[j] = new SigmoidNeuron(hidden);
  }
  
  private void feedforward(boolean[] px) {
    //input the pixel data to the input layer
    for(int i = 0; i < input.length; i++) {
      input[i].setOutput(px[i]);
    }

    for(int i = 0; i < hidden.length; i++) {
      hidden[i].squash();
    }

    for(int i = 0; i < output.length; i++) {
      output[i].squash();
    }
  }
 
  /*
    This constructor attempts to create a new NN trained off weights
    stored in the file weights.txt, throwing an IOException if problems
    arise.
  */
  public NeuralNet() throws Exception {   
    try {   
      Scanner in = new Scanner(Paths.get("weights.txt"));
      int numberOfNeurons;
      //get alpha
      String[] split;
      double[] weights;
      alpha = Double.parseDouble(in.nextLine());
      input = new InputNeuron[Integer.parseInt(in.nextLine())];
      //initialize input neurons
      for(int i = 0; i < input.length; i++)
          input[i] = new InputNeuron();
      //create hidden layer
      hidden = new SigmoidNeuron[Integer.parseInt(in.nextLine())];
      //populate hidden layer
      for(int j = 0; j < hidden.length; j++) {
        hidden[j] = new SigmoidNeuron(input);
      }
      //set weights for hidden layer
      for(int i = 0; i < hidden.length; i++) {
        weights = new double[input.length];
        split = in.nextLine().split(" ");
        if(split.length == input.length) {
          for(int j = 0; j < input.length; j++) {
            weights[j] = Double.parseDouble(split[j]);
          }
          hidden[i].setWeights(weights);
        } else throw new IOException("length of hidden layer weight vectors differs from expected " + split.length + " " + hidden.length);
      }
      //populate output layer
      output = new SigmoidNeuron[Integer.parseInt(in.nextLine())];
      for(int j = 0; j < output.length; j++)
          output[j] = new SigmoidNeuron(hidden);
      //set weights for hidden layer
      for(int i = 0; i < output.length; i++) {
        weights = new double[hidden.length];
        split = in.nextLine().split(" ");
        if(split.length == hidden.length) {
          for(int j = 0; j < hidden.length; j++)
            weights[j] = Double.parseDouble(split[j]);
          output[i].setWeights(weights);
        } else throw new IOException("length of output layer weight vectors differs from expected " + split.length + " " + output.length);
      }
    } catch(IOException e) {
      throw new Exception("Error trying to create new NN from weights.txt: " + e.getMessage());
    }
  }
  
  public void backpropagate(int target) {
    //update deltas and weights for the output layer
    for(int i =0; i < output.length; i++) {
      double out = output[i].output();
      int t = 0;
      if(i == target) t= 1;
      output[i].updateDelta(out*(1-out)*(t-out));
      output[i].updateWeights();
    }
    //update weights for output
    for(int i =0; i < hidden.length; i++) {
      double out = hidden[i].output();
      double sum = 0;
      for(int j = 0; j < output.length; j++) {
        sum += output[j].getDelta()*output[j].getWeight(i);
      }
      hidden[i].updateDelta(out*(1-out)*sum);
      hidden[i].updateWeights();
    }
  }


  public void train(boolean[][] images, int[] targets, int reps) {
  //loop through the same set of training examples
    for(int j = 0; j < reps; j++) {
      //train on each example in the set
      for(int i = 0; i < targets.length; i++) {
        //run the NN, then adjust
        feedforward(images[i]);
        backpropagate(targets[i]);
      }
    }
    
    //update weights file
    updateWeights();
  }
  
  private void updateWeights() {
    try {
      FileWriter fw = new FileWriter("weights.txt");
      fw.write(alpha + "\n");
      fw.write(input.length + "\n");
      fw.write(hidden.length + "\n");
      for(int i = 0; i < hidden.length; i++) {
        double[] weights = hidden[i].getWeights();
        for(int j = 0; j < weights.length; j++) 
          fw.write(weights[j] + " ");
        fw.write("\n");
      }
      fw.write(output.length + "\n");
      for(int i = 0; i < output.length; i++) {
        double[] weights = output[i].getWeights();
        for(int j = 0; j < weights.length; j++) 
          fw.write(weights[j] + " ");
        fw.write("\n");
      }
      fw.close();
    } catch(IOException e) {
      System.err.println(e.getMessage());
    }
  }
  
  public int classify(boolean[] image) {
    feedforward(image);
    double lowestVal = 0;
    int lowestIndex = 0;
      for(int j = 0; j<10; j++) {
        if(lowestVal < output[j].output()) {
          lowestVal = output[j].output();
          lowestIndex = j;
        }
      }
    return lowestIndex;
  }
  
  public double test(boolean[][] images, int[] targets) {
    
    double classificationRate = 0;
  //then classify each several times, probably.
    for(int i = 0; i < targets.length; i++) {
      feedforward(images[i]);
      double lowestVal = 0;
      double lowestIndex = 0;
      for(int j = 0; j<10; j++) {
        if(lowestVal < output[j].output()) {
          lowestVal = output[j].output();
          lowestIndex = j;
        }
      }
      if (lowestIndex == targets[i]) classificationRate++;
    }
    classificationRate /= targets.length;
 //   System.out.println("*** "+ classificationRate + " ***\n");
    return classificationRate;
  }
  
  private abstract class Neuron {
    public abstract double output();
  }

  private class InputNeuron extends Neuron {
    private double output;
    public InputNeuron() {
    }
    public void setOutput(boolean b) {
      if(b) output = 1;
      else output = 0;
    }
    public double output() { return output; }
  }

  private class SigmoidNeuron extends Neuron {

    private Neuron[] inputs;
    private double[] weights;
    private double delta;
    private double input;
    private double output;
  
    public SigmoidNeuron(Neuron[] inputs) {
      this.inputs = inputs;
      weights = new double[inputs.length];
      for(int i = 0; i < inputs.length; i++) {
        weights[i] = 2*(.5 - Math.random());
      }
    }
    public double output() { return output; }
    
    public double getWeight(int i) {
      return weights[i];
    }
    
    public double[] getWeights() { return weights; }
    
    public void setWeights(double[] w) {
      this.weights = w;
    }
    
    public void printWeights() {
      for(int i = 0; i < inputs.length; i++) System.out.println(weights[i]);
    }
  
    public void squash() {
      input = 0;
      for(int i = 0; i < inputs.length; i++) {
        input += inputs[i].output()*weights[i];
      }
      output = 1/(1+Math.exp(-input));
    }
  
    public void updateWeights() {
      for(int i = 0; i < inputs.length; i++) {
        weights[i] += alpha*inputs[i].output()*delta;
      }
    }
  
    public void updateDelta(double d) {
      delta = d;
    }

    public double getDelta() { return delta; }
  
  }
}



