/*
  A back-propagation trained NN for classifying handwritten digits,
  though, potentially, generalizable to other images.
*/

public class NeuralNet {
  private Neuron[] input = new Neuron[256];
  private Neuron[] hidden = new Neuron[20];
  private Neuron[] output = new Neuron[10];
  private Neuron[][] neurons = new Neuron[3][];
  
  //constructor 1.  Will make a 2ndary constructor that will take weights as input
  public NeuralNet() {
  //create 2D neuron array of the layers in order
    neurons[0] = input;
    neurons[1] = hidden;
    neurons[2] = output;
    //init the input neurons, since they won't receive the same args
    for(int j = 0; j < input.length; j++)
        input[j] = new Neuron();
     //init all the other layers, feeding them a reference to their inputs
    for(int i = 0; i < 3; i++) {
      for(int j = 1; j < neurons[i].length; j++)
        neurons[i][j] = new Neuron(neurons[i-1]);
    }
  }
  
  private void feedforward(boolean[] px) {
    //input the pixel data to the input layer
    for(int i = 0; i < 256; i++) {
      input[i].setOutput(px[i]);
    }

    for(int i = 0; i < hidden.length; i++) {
      hidden[i].squash();
    }

    for(int i = 0; i < output.length; i++) {
      output[i].squash();
    }

  }

  public void backpropagate(int target) {
    //update deltas and weights for the output layer
    for(int i =0; i < output.length; i++) {
      double out = output[i].output();
      output[i].updateDelta(out*(1-out)*(target-out));
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

  public void train() {
  //read in images from a file
  
  //then classify each several times, probably.
  }
  
}

class Neuron {
  private Neuron[] inputs;
  private double[] weights;
  private double output;
  private double delta;
  private double input;
  public static final double alpha = .1;
  
  public Neuron() {
  
  }
  public double getWeight(int i) {
    return weights[i];
  }
  public void setOutput(boolean b) {
    if(b == true) output = 1;
    else output = 0;
  }
  public Neuron(Neuron[] inputs) {
    inputs = inputs;
    for(int i = 0; i < inputs.length; i++)
      weights[i] = .5 - Math.random();
  }
  
  public void squash() {
    input = 0;
    for(int i = 0; i < inputs.length; i++) 
      input += inputs[i].output()*weights[i];
    output = 1/(1+Math.exp(-input));
  }
  
  public void updateWeights() {
    for(int i = 0; i < inputs.length; i++) {
      weights[i] += alpha*input*delta;
    }
  }
  
  public double output() { return output; }

  public void updateDelta(double d) {
    delta = d;
  }

  public double getDelta() { return delta; }
  
}

