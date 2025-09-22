include "Ex1.dfy"
include "Ex2.dfy"


module Ex3 {
  
  import Ex1=Ex1
  import Ex2=Ex2


  class Node {
    ghost var list : seq<int>; 
    ghost var footprint : set<Node>;

    var data : int; 
    var next : Node?; 

    function Valid() : bool 
      reads this, footprint
      decreases footprint;
    {
      (this in footprint) &&
      ((next == null) ==> list == [ data ] && footprint == { this }) &&
      ((next != null) ==> 
        (next in footprint) && 
        footprint == next.footprint + { this } && 
        (this !in next.footprint) &&
        list == [ data ] + next.list &&
        next.Valid()) 
        //&& Ex1.sorted(list)
    }

    constructor (val : int) 
      ensures Valid() 
        && next == null && list == [ data ] 
        && footprint == { this } 
        && val == data 
    {
      this.data := val; 
      this.next := null; 
      this.list := [ val ]; 
      this.footprint := { this };
    }
    

    /*
     * 3.1 - countDiffOO - 1.5 val 
     * Implement RECURSIVELY 
     */
    method countDiffOO() returns (r : int)
      requires Valid() && Ex1.sorted(this.list)
      ensures Valid() 
      ensures r == |Ex1.removeReps(this.list)|
    {
      if(this.next == null){
        r:=1; return;
      }

      var prev:=this;
      ghost var seq_aux := [prev.data];
      var cur := this.next;
      r:= 1;
      while (cur != null)         invariant cur != null ==> cur.Valid()
                                  invariant cur != null ==> this.list == seq_aux + cur.list
                                  invariant cur == null ==> this.list == seq_aux
                                  invariant prev != null
                                  invariant prev.next == cur
                                  invariant prev.Valid()
                                  decreases prev.footprint
                                  invariant Ex1.sorted(prev.list)
                                  invariant Ex1.sorted(seq_aux)
                                  invariant 0 < r <= |seq_aux|
                                  invariant seq_aux[|seq_aux|-1] == prev.data
                                  invariant r == |Ex1.removeReps(seq_aux)|
                                  
      {
        if(prev.data != cur.data){
          calc == {
            |Ex1.removeReps(seq_aux + [cur.data])|;
            == {Ex1.removeRepsAppend(seq_aux, cur.data);}
            |Ex1.removeReps(seq_aux)| + |[cur.data]|;
            == 
            |Ex1.removeReps(seq_aux)| + 1;
            == 
            r+1;
          }
          r:=r+1;
        } else{
          calc == { 
            |Ex1.removeReps(seq_aux + [cur.data])|;
            == {Ex1.removeRepsAppend2(seq_aux, cur.data);}
            |Ex1.removeReps(seq_aux)|;
            ==
            r;
          }
        }
        
        prev:=cur;
        cur := cur.next;
        auxiliarylemma(seq_aux, prev.data);
        seq_aux := seq_aux + [prev.data];
        
        
      }
      return;

    }    

    lemma auxiliarylemma (s : seq<int> , i : int)
       requires Ex1.sorted(s)
       requires |s| > 0
       requires i >= s[|s| - 1]
       ensures Ex1.sorted(s + [i])
    {
        if(|s| == 1){
          assert Ex1.sorted(s);
          assert Ex1.sorted(s + [i]);
        } else {
          assert (s[1..]+[i]) == (s + [i])[1..];
        }
    }



    /*
     * 3.2 - removeRepsOO - 1.5 val 
     */
    method removeRepsOO() returns (r : Node)
      requires Valid() && Ex2.sortedForAll(this.list)
      ensures r.Valid() && fresh(r.footprint) 
                && Ex2.ssortedForAll(r.list)
                && r.list[0] == old(list[0])
      decreases |this.list|
    { 
      r := new Node(this.data);

      if(this.next == null){
        return;
      } else { 
        Ex2.sortedEquiv2(this.list);
        var aux := this.next.removeRepsOO();
        if(this.data < this.next.data){ 
          r.list := [r.data] + aux.list;
          r.footprint := {r} + aux.footprint; 
          r.next := aux; return;
        } 
      }

    }


    /*
     * 3.3 - removeRepsOOIP - 2 val 
     */

    method removeRepsOOIP() 
      requires Valid() && Ex2.sortedForAll(this.list)
      ensures Valid() && Ex2.ssortedForAll(this.list) 
      ensures this.list[0] == old(this.list[0])
      ensures this.footprint <= old(this.footprint)
      modifies this.footprint
      decreases |this.list|
    { 
    }
  }

  
}