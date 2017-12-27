package ml_zzh.decisionTree;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class DecisionTreeBranchPrepruning extends DecisionTreeBranch {

  public DecisionTreeBranchPrepruning(String[] titles) {
    super(titles);
  }
  
  /**
   * child leaves for prepruning
   */
  private List<DecisionTreeLeaf> testChildrenLeaves;

  /**
   * add leaf.
   * @param e
   */
  public void addTestChildrenLeaf(DecisionTreeLeaf e){
    testChildrenLeaves.add(e);
  }
  
  /**
   * add leaves
   * @param e
   */
  public void addTestChildrenLeaf(Collection<DecisionTreeLeaf> e){
    testChildrenLeaves.addAll(e);
  }
  
  @Override
  public void separatedRecursiveByGiniIndex(){
    boolean canBeSplitted=false;
    for (int i=1; i<childrenLeaves.size(); i++){
      if(!childrenLeaves.get(i).getResult()
          .equals(childrenLeaves.get(0).getResult())){
        canBeSplitted=true;
        break;
      }
    }
    if(!canBeSplitted){
      result=childrenLeaves.get(0).getResult();
      return;
    }
    
    double totalGiniIndex=calGiniIndex(childrenLeaves);
    partitionAttribute=Arrays.asList(titles).stream().map(title->{
      return new AbstractMap.SimpleEntry<String, Double>(title, 
          calGiniIndexGain(totalGiniIndex, title));
    }).reduce((a, b)->{
      return a.getValue()>b.getValue()?a:b;
    }).get().getKey();

    this.separatedByAttribute();
    
    /*
     * judge if this seperated is good
     */
    result=childrenLeaves.stream()
        .collect(Collectors.groupingBy((DecisionTreeLeaf leaf)->leaf.getResult()))
      .entrySet().stream()
        .max((o1, o2)->{
          return new Integer(o1.getValue().size()).compareTo(o2.getValue().size());
        })
        .get()
        .getKey();
    
    double rightNumber=testChildrenLeaves.stream()
        .filter(leaf->{
          return leaf.getResult()==result;
        })
        .count();
    
  }
  
  @Override
  protected void separatedByAttribute(){
    if(!partitionValues.containsKey(partitionAttribute)){
      Multimap<String, DecisionTreeLeaf> map=HashMultimap.create();
      childrenLeaves.forEach(leaf->{
          map.put(leaf.discreteValues.get(partitionAttribute), leaf);
      });
      Multimap<String, DecisionTreeLeaf> testMap=HashMultimap.create();
      testChildrenLeaves.forEach(leaf->{
          testMap.put(leaf.discreteValues.get(partitionAttribute), leaf);
      });
      children=map.asMap()
          .entrySet().stream()
          .map(entry->{
            DecisionTreeBranchPrepruning dtBranch=
                new DecisionTreeBranchPrepruning(titles);
            dtBranch.addChildrenLeaf(entry.getValue());
            dtBranch.addTestChildrenLeaf(testMap.get(entry.getKey()));
            return dtBranch;
          })
          .collect(Collectors.toList());
    } else {
      Multimap<Boolean, DecisionTreeLeaf> map=HashMultimap.create();
      childrenLeaves.forEach(leaf->{
        map.put(leaf.continuousValues.get(partitionAttribute)>
            partitionValues.get(partitionAttribute), leaf);
      });
      Multimap<Boolean, DecisionTreeLeaf> testMap=HashMultimap.create();
      childrenLeaves.forEach(leaf->{
        testMap.put(leaf.continuousValues.get(partitionAttribute)>
            partitionValues.get(partitionAttribute), leaf);
      });
      children=map.asMap().entrySet().stream()
          .map(entry->{
            DecisionTreeBranchPrepruning dtBranch=
                new DecisionTreeBranchPrepruning(titles);
            dtBranch.addChildrenLeaf(entry.getValue());
            dtBranch.addTestChildrenLeaf(testMap.get(entry.getKey()));
            return dtBranch;
          })
          .collect(Collectors.toList());
    }
  }
}
