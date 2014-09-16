package files;

import java.util.ArrayList;

public class RouletteWheel
{
  public int rouletteWheelSelection(ArrayList<Double> candidates)
  {
    double r = Math.random();
    int selectedCandidate = 0;
    for (int i = 0; i < candidates.size(); i++) {
      if (r <= ((Double)candidates.get(i)).doubleValue())
      {
        selectedCandidate = i;
        break;
      }
    }
    return selectedCandidate;
  }
  
  public ArrayList<Double> addUp(ArrayList<Double> candidates)
  {
    ArrayList<Double> addUp = new ArrayList();
    addUp.add(candidates.get(0));
    for (int i = 1; i < candidates.size(); i++) {
      addUp.add(Double.valueOf(((Double)addUp.get(i - 1)).doubleValue() + ((Double)candidates.get(i)).doubleValue()));
    }
    return addUp;
  }
}
