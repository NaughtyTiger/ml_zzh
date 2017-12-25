package ml_zzh;

import java.io.File;

import org.junit.Test;

import ml_zzh.decisionTree.DecisionTree;

public class DecisionTreeTest {
  @Test
  public void createTest(){
    DecisionTree tree=DecisionTree.create(new File("src/test/resources/table4.3"));
    System.out.println(tree.root);
  }
  
//  @Test
  public void debugTest(){
    DecisionTree.create(new File("src/test/resources/table4.3.small"));
  }
}
