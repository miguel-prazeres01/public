include "Ex1.dfy"


module Ex3 {
  
  import Ex1=Ex1


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
     */
    method countDiffOO() returns (r : int)
      requires Valid() && Ex1.sorted(this.list)
      ensures Valid() 
      ensures r == |Ex1.removeReps(this.list)|
    {

    }    



    /*
     * 3.2 - removeRepsOO - 1.5 val 
     */
    method removeRepsOO() returns (r : Node)
      requires Valid() && Ex1.sorted(this.list)
      ensures r.Valid() && fresh(r.footprint) 
                && r.list == Ex1.removeReps(this.list)
      decreases |this.list|
    { 
    }


    /*
     * 3.3 - removeRepsOOIP - 2 val 
     */

    method removeRepsOOIP() 
    { 
    }
  }

  
}