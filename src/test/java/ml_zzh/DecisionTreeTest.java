package ml_zzh;

import java.io.File;

import org.junit.Test;

import ml_zzh.decisionTree.DecisionTree;

public class DecisionTreeTest {
  @Test
  public void createTest(){
    DecisionTree.create(new File("src/test/resources/table4.3"));
  }

}
