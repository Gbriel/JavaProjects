/*
  A back-propagation trained NN for classifation problems.
*/

public class NeuralNet {
  private InputNeuron[] input;
  private SigmoidNeuron[] hidden;
  private SigmoidNeuron[] output;
  
  //constructor 1.  Will make a 2ndary constructor that will take weights as input
  public NeuralNet(int inputLength, int hiddenLength,int outputLength) {
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
    for(int j = 0; j < reps; j++) {
      for(int i = 0; i < targets.length; i++) {
        feedforward(images[i]);
        backpropagate(targets[i]);
      }
    }
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
  
}

abstract class Neuron {
  public abstract double output();
}

class InputNeuron extends Neuron {
  private double output;
  public InputNeuron() {
  }
  public void setOutput(boolean b) {
    if(b) output = 1;
    else output = 0;
  }
  public double output() { return output; }
}

class SigmoidNeuron extends Neuron {

  private Neuron[] inputs;
  private double[] weights;
  private double delta;
  private double input;
  private double output;
  public static final double alpha = .1 ;
  
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

