package ml_zzh.decisionTree;

public abstract class DecisionTreeNode {
  public abstract String getResult();
  
  public abstract void printRec(int lv, StringBuffer result);
}
