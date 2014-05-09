public class MetaAnalysisCharRec {
  public MetaAnalysisCharRec() {}
  public static void main(String[] args) {
    MetaAnalysisCharRec analyzer = new MetaAnalysisCharRec();
    analyzer.analyze();
  }
  
  public void analyze() {
    int[] pixSizes = {10,16,25,50};
    int[] hiddenLengths = {50, 100, 160,300};
    double[] thresholds = {0.1, 0.5,0.8};
    ImageProcessor p = new ImageProcessor();
    //stores the winning value combination for each pix size
    //value[][0] = bestscore value[][1] = threshold val, val[][2] = hiddenlength, val[][3] = alpha
    double[][] valueCombos = new double[7][4];
    long startTime = System.currentTimeMillis();
    for(int q = 0; q < 3; q++) {
      double bestScore = 0;
      int bestQ = -1;
      int bestJ = -1;
      int bestK = -1;
      int epochs = -1;
      
      for(int i = 0; i < pixSizes.length; i++) {
        p.process("Img",pixSizes[i],thresholds[q]);
        for(int j = 0; j < 5; j++) {
            CharacterRecognizer charRec = new CharacterRecognizer(pixSizes[i],hiddenLengths[j],63,0.25);
            for(int z = 0; z < 10; z++) {
              double currentScore = charRec.trainAndReport(50);
              if(currentScore > bestScore) {
                bestScore = currentScore;
                bestQ =q;
                bestJ =j;
                epochs = z*10;
                     System.out.printf("\tbest score: %5.4s, #hidden: %d, pix: %d, thresh: %5.4s, epoch: %d, @time %5.4s (min) \n",currentScore,hiddenLengths[j],pixSizes[i],thresholds[q],z*50,((System.currentTimeMillis()-startTime)/60000.0)); 
              }
          }
        }
       valueCombos[i][0] = bestScore;
      valueCombos[i][1] = bestQ;
      valueCombos[i][2] = bestJ;
      valueCombos[i][3] = epochs;
      System.out.printf("pix: %d, best score: %5.4s, #hidden: %d, thresh: %5.4s, epoch: %d, @time %5.4s (min) \n",pixSizes[i],bestScore,hiddenLengths[bestJ],thresholds[bestQ],epochs,((System.currentTimeMillis()-startTime)/60000.0)); 
      }
      
    }
    for(int i = 0; i < 6; i++) {
        System.out.printf("pix: %d, best score: %5.4s, #hidden: %5.1s, thresh: %5.4s, epoch: %5.1s, @time %5.4s (min) \n",pixSizes[i], valueCombos[i][0],valueCombos[i][1],valueCombos[i][2],valueCombos[i][3],((System.currentTimeMillis()-startTime)/60000.0));
    }
  }
}
