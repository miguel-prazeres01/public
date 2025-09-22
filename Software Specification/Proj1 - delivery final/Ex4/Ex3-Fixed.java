

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Node {

  Node next; 
  int data; 

  public Node(int i) {
    this.data = i; 
    this.next = null; 
  }


  public int countDiffRec () { 
    if (this.next == null) {
      return 1;
    } else {
      if (this.data != next.data) {
        return 1 + this.next.countDiffRec(); 
      } else {
        return this.next.countDiffRec(); 
      }
    }
  }

  public int countDiffIter () {
    int count = 1; 

    int prev = this.data; 

    Node cur = this.next; 

    while (cur != null) {
      if (cur.data != prev) {
        count++;
        prev = cur.data; 
      }
      cur = cur.next; 
    }

    return count;
  }

  public Node removeRepsOO() {
    if (this.next == null) {
      return new Node(this.data);
    } else {
      if (this.data == this.next.data) {
        return this.next.removeRepsOO();
      } else {
        Node n = new Node(this.data);
        Node new_next = this.next.removeRepsOO(); 
        n.next = new_next; 
        return n; 
      }
    }
  }


  public void removeRepsOOIP() {
    if (this.next == null) {
      return;
    } else {
      this.next.removeRepsOOIP();
      if (this.data == this.next.data) {
        this.next = this.next.next; 
      } 
    }
  }

  public void print() {

    Node n = this; 
    while (n != null) {
      System.out.print(n.data);
      System.out.print(" ");
      n = n.next;
    }
    System.out.print("\n");
  }

}

public class Ex3 {
  
  public static void main(String[] args) 
      throws IOException
  {

    BufferedReader reader = new BufferedReader(
      new InputStreamReader(System.in));
 
    String str = reader.readLine();
    int n = Integer.parseInt(str);

    Node node = null;
    Node prev_node = null; 

    for (int i=0; i<n; i++) {
      
      str = reader.readLine();
      int aux = Integer.parseInt(str);
      Node node_aux = new Node(aux);

      if (node == null) {
        node = node_aux;
        prev_node = node_aux;
      } else {
        prev_node.next = node_aux; 
        prev_node = node_aux; 
      }
    }

    if (node == null){
        reader.close();
        return;
    } 

    node.print();

    Node new_node = node.removeRepsOO(); 

    new_node.print(); 

    node.removeRepsOOIP();

    node.print(); 
    reader.close();
  }

}