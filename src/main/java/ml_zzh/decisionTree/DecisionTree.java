package ml_zzh.decisionTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ml_zzh.decisionTree.exception.DecisionTreeLeafInitException;

public class DecisionTree {
  DecisionTreeBranch root;
  
  private DecisionTree(){
    
  }
  
  public static DecisionTree create(File file){
    DecisionTree tree=new DecisionTree();
    try {
      BufferedReader br=new BufferedReader(new FileReader(file));
      String titles[]=br.readLine().split(",");
      tree.root=new DecisionTreeBranch(titles);
      while (br.ready()){
        String values=br.readLine();
        try {
          DecisionTreeLeaf leaf=new DecisionTreeLeaf(titles,values);
          tree.root.addChildrenLeaf(leaf);
        } catch (DecisionTreeLeafInitException e) {
          e.printStackTrace(System.err);
        }
      }
      tree.root.separatedRecursive();
      br.close();
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
    return tree;
  }
}
