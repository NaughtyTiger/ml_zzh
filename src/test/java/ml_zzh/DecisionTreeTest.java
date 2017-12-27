package ml_zzh;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import ml_zzh.decisionTree.DecisionTree;

public class DecisionTreeTest {
  @Test
  public void createByEntropyGainTest(){
    DecisionTree tree=DecisionTree.createByEntropyGain(new File("src/test/resources/table4.3"));
    System.out.println(tree.root);
  }
  
//  @Test
  public void test(){
    ArrayList<Integer> list=new ArrayList<>();
    list.add(1);
    System.out.println(list.stream()
        .max((o1, o2)->{
          return o1.compareTo(o2);
        })
        .get());
  }
}
