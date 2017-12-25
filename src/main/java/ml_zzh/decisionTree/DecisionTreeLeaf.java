package ml_zzh.decisionTree;

import java.util.HashMap;

import ml_zzh.decisionTree.exception.DecisionTreeLeafInitException;

public class DecisionTreeLeaf {
  public HashMap<String, String> discreteValues=new HashMap<>();
  public HashMap<String, Double> continuousValues=new HashMap<>();
  private String result;
  
  /**
   * The result default be the last of init value
   * @date Dec 22, 2017
   * @param titles
   * @param initValue
   * @throws DecisionTreeLeafInitException
   */
  public DecisionTreeLeaf(String[] titles, String initValue) 
      throws DecisionTreeLeafInitException {
    String values[]= initValue.split(",");
    if(values.length!=titles.length){
      throw new DecisionTreeLeafInitException();
    }
    int i=0;
    for (; i<titles.length-1; i++){
      try {
        double d=Double.parseDouble(values[i]);
        continuousValues.put(titles[i], d);
      } catch (Exception e){
        discreteValues.put(titles[i], values[i]);
      }
    }
    result=values[i];
  }
  
  public String getResult(){
    return result;
  }
  
  @Override
  public String toString(){
    StringBuffer sb=new StringBuffer();
    sb.append(discreteValues.toString());
    sb.append(continuousValues.toString());
    sb.append("\n");
    sb.append(result);
    return sb.toString();
  }
}
