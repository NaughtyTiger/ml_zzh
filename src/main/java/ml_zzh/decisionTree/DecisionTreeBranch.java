package ml_zzh.decisionTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class DecisionTreeBranch extends DecisionTreeNode {
  /*
   * child nodes
   */
  private List<DecisionTreeNode> children;
  private List<DecisionTreeLeaf> childrenLeaves;
  
  /*
   * if one branch has one sure result, set it to result
   * Maybe only one childrenLeaves
   * Or all childrenLeaves has same result
   */
  private String result;
  
  /*
   * this branch spilt by those two condition
   */
  public String partitionAttribute;
  private HashMap<String, Double> partitionValues=new HashMap<>();

  /*
   * all titles of para
   */
  private String[] titles;
  
  /*
   * entropy. intermediate varibables to cal entropy gain
   */
  private double resultEntropy;
  
  
  public DecisionTreeBranch(String[] titles){
    children=new ArrayList<>();
    childrenLeaves=new ArrayList<>();
    
    this.titles=titles;
  }
  
  @Override
  public String toString(){
    StringBuffer result=new StringBuffer();
    printRec(0, result);
    return result.toString();
  }
  
  public void printRec(int lv, 
      StringBuffer result){
    if(this.getClass()==DecisionTreeBranch.class){
      DecisionTreeBranch branch=(DecisionTreeBranch)this;
      for(int i=0; i<lv; i++){
        result.append("--");
      }
      // describe this node
      if(branch.partitionAttribute==null){
        result.append(this.result);
      } else if(branch.partitionValues.containsKey(
          branch.partitionAttribute)){
        result.append(branch.partitionAttribute);
        result.append(":"+branch.partitionValues
            .get(branch.partitionAttribute));
      } else {
        result.append(branch.partitionAttribute);
        result.append(":");
        result.append(" ");
        children.forEach(child->{
          if(child.getClass()==DecisionTreeBranch.class){
            result.append(((DecisionTreeBranch)child)
                .childrenLeaves.get(0)
                .discreteValues.get(branch.partitionAttribute)+" ");
          } else {
            result.append(((DecisionTreeLeaf)child)
                .discreteValues.get(branch.partitionAttribute)+" ");
          }
        });
      }
      result.append("\n");

      // add child node
      children.forEach(child->{
        child.printRec(lv+1, result);
      });
    }
  }
  
  /**
   * add leaf.
   * @param e
   */
  public void addChildrenLeaf(DecisionTreeLeaf e){
    childrenLeaves.add(e);
  }
  
  /**
   * add leaves
   * @param e
   */
  public void addChildrenLeaf(Collection<DecisionTreeLeaf> e){
    childrenLeaves.addAll(e);
  }

  /**
   * return the result of this branch if there is only one possibility
   * return null otherwise
   */
  public String getResult(){
    return result;
  }
  
  public void separatedRecursive(){
    if(childrenLeaves.size()<=1){
      children=new ArrayList<DecisionTreeNode>(childrenLeaves);
      result=childrenLeaves.get(0).getResult();
      return;
    }
    boolean canBeSpilted=false;
    for (int i=1; i<childrenLeaves.size(); i++){
      if(!childrenLeaves.get(i).getResult()
          .equals(childrenLeaves.get(0).getResult())){
        canBeSpilted=true;
        break;
      }
    }
    if(!canBeSpilted){
      result=childrenLeaves.get(0).getResult();
      return;
    }
    resultEntropy=calEntropy(childrenLeaves);

    double biggestGain=0.0;
    for(int i=0; i<titles.length-1; i++){
      double gain=calEntropyGain(titles[i]);
      if(gain>biggestGain){
        biggestGain=gain;
        partitionAttribute=titles[i];
      }
    }
    if(!partitionValues.containsKey(partitionAttribute)){
      Multimap<String, DecisionTreeLeaf> map=HashMultimap.create();
      childrenLeaves.forEach(leaf->{
              map.put(leaf.discreteValues.get(partitionAttribute), leaf);
          });
      children=map.asMap().entrySet().stream()
          .map(entry->{
            DecisionTreeBranch dtBranch=new DecisionTreeBranch(titles);
            dtBranch.addChildrenLeaf(entry.getValue());
            return dtBranch;
          })
          .collect(Collectors.toList());
      children.forEach(br->{
        if(br.getClass()==DecisionTreeBranch.class){
          ((DecisionTreeBranch)br).separatedRecursive();
        }
      });
    } else {
      Multimap<Boolean, DecisionTreeLeaf> map=HashMultimap.create();
      childrenLeaves.forEach(leaf->{
        map.put(leaf.continuousValues.get(partitionAttribute)>
            partitionValues.get(partitionAttribute), leaf);
      });
      children=map.asMap().entrySet().stream()
          .map(entry->{
            DecisionTreeBranch dtBranch=new DecisionTreeBranch(titles);
            dtBranch.addChildrenLeaf(entry.getValue());
            return dtBranch;
          })
          .collect(Collectors.toList());
      children.forEach(br->{
        if(br.getClass()==DecisionTreeBranch.class){
          ((DecisionTreeBranch)br).separatedRecursive();
        }
      });
    }
  }
  
  private double calEntropyGain(String title){
    if(childrenLeaves.get(0).discreteValues.containsKey(title)){
      return resultEntropy-childrenLeaves.stream()
          .collect(Collectors.groupingBy(x->x.discreteValues.get(title)))
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
