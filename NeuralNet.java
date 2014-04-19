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
  
  public void train() {
  //read in images from a file
  
  //then classify each several times, probably.
  }
  
}

class Neuron {
  private Neuron[] inputs;
  private double[] weights;
  private int output;
  public static final double alpha = .1;
  
  public Neuron() {
  
  }
  
  public Neuron(Neuron[] inputs) {
    inputs = inputs;
    for(int i = 0; i < inputs.length; i++)
      weights[i] = .5 - Math.random();
  }
  
  private double squash() {
    double sum = 0;
    for(int i = 0; i < inputs.length; i++) 
      sum += inputs[i].output()*weights[i];
    return 1/(1+Math.exp(-sum));
  }
  
  public void updateOutput() {
    if(squash() > .5) output = 1;
    else output = 0;
  }
  
  public void updateWeights() {
    for(int i = 0; i < inputs.length; i++) {
      weights[i] += alpha*(1-
    }
  }
  
  public int output() { return output; }
  
}

class SigmoidNeuron extends Neuron {
  
}
