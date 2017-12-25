package ml_zzh.decisionTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DecisionTreeBranch {
  /*
   * child nodes
   */
  @SuppressWarnings("unused")
  private List<DecisionTreeNode> children;
  private List<DecisionTreeLeaf> childrenLeaves;
  
  /*
   * this branch spilt by those two condition
   */
  public String partitionAttribute;
  public int partitionValue; // if partition value is continuous, partitionValue is needed

  /*
   * all titles of para
   */
  private String[] titles;
  
  /*
   * entropy. intermediate varibables to cal entropy gain
   */
  private double resultEntropy;
  
  private HashMap<String, Double> partitionValues=new HashMap<>();
  
  public DecisionTreeBranch(String[] titles){
    children=new ArrayList<>();
    childrenLeaves=new ArrayList<>();
    
    this.titles=titles;
  }
  
  /**
   * add leaf. TODO
   * @param e
   */
  public void addChildrenLeaf(DecisionTreeLeaf e){
    childrenLeaves.add(e);
  }
  
  public void separatedRecursive(){
    resultEntropy=calEntropy(childrenLeaves);
    System.out.println("entropy of all: "+resultEntropy);

    // 
    double biggestGain=0.0;
    for(int i=0; i<titles.length-1; i++){
      double gain=calEntropyGain(titles[i]);
      System.out.println("gain of "+titles[i]+": "+gain);
      if(gain>biggestGain){
        biggestGain=gain;
      }
    }
  }
  
  private double calEntropyGain(String title){
    if(childrenLeaves.get(0).discreteValues.containsKey(title)){
      return resultEntropy-childrenLeaves.stream()
          .collect(Collectors.groupingBy(person->person.discreteValues.get(title)))
        .entrySet().stream()
          .mapToDouble(entry->{
            double proportion=1.0*entry.getValue().size()/childrenLeaves.size();
            return proportion*calEntropy(entry.getValue());
          })
          .sum();
    } else {
      @SuppressWarnings("unchecked")
      List<DecisionTreeLeaf> values=((ArrayList<DecisionTreeLeaf>)
          ((ArrayList<DecisionTreeLeaf>) childrenLeaves).clone());
      values.sort((o1,o2)->{
        return o1.continuousValues.get(title)
            .compareTo( o2.continuousValues.get(title));
      });
      double max=0.0;
      for(int i=1; i<values.size()-1; i++){
        double gain=resultEntropy
            - (i+1)*1.0/values.size()*calEntropy(values.subList(0, i+1))
            - (values.size()-i-1)*1.0/values.size()
                *calEntropy(values.subList(i+1, values.size()));
        if(gain>max){
          max=gain;
          partitionValues.put(title, 
              (values.get(i).continuousValues.get(title)
              +values.get(i+1).continuousValues.get(title))/2);
        }
      }
      return max;
    }
  }

  private double calEntropy(Collection<DecisionTreeLeaf> leaves){
    return leaves.stream()
        .collect(Collectors.groupingBy(DecisionTreeLeaf::getResult))
      .entrySet().stream()
        .mapToDouble(entry->{
          double proportion=1.0*entry.getValue().size()/leaves.size();
          return -proportion*Math.log(proportion)/Math.log(2);
        })
        .sum();
  }
  
}
