include "Ex1.dfy"


module Ex3 {
  
  import Ex1=Ex1

function removeNeg(s:seq<int>) : seq<int> {
      if(s == [])
        then []
        else
          if(s[0] < 0)
            then removeNeg(s[1..])
          else [s[0]] + removeNeg(s[1..])
    }



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
    
    method prepared (i:int) returns (r: Node) 
      requires this.Valid()
      ensures r.Valid() && r.list == [i] + this.list && fresh(r)
    {
      r := new Node(i);
      r.next := this;
      r.list := [i] + this.list;
      r.footprint := {r} + this.footprint;
    }

    method append(i:int) 
      requires this.Valid()
      ensures this.list == old(this.list) + [i]
      ensures fresh(this.footprint - old(this.footprint))
      ensures this.Valid()
      modifies this,this.footprint
      decreases this.footprint
    {
      var n:= new Node(i);
      
      if(this.next == null){
        this.next:=n;
        this.list:= [this.data,i];
        this.footprint:={this,n};
      } else {
        this.next.append(i);
        this.list:= [this.data] + this.next.list;
        this.footprint:= {this} + this.next.footprint;
      }
    }


    method find(i:int) returns (r:bool)
      requires Valid()
      ensures r==true ==> i in this.list
      ensures r==false ==> i !in this.list
      ensures r == (i in this.list)
      decreases this.footprint

    {
      if(this.data == i){
        r:=true;
        return;
      } else {
        if(this.next == null) {
          r:=false;
          return;
        }
        else {
          r:=this.next.find(i);
          return;
        }
      }
    }

    method filter() returns (r:Node?)
      requires this.Valid()
      ensures r!=null ==> r.Valid()
      ensures r!=null ==> r.footprint <= old(this.footprint)
      ensures r!=null ==> r.list == removeNeg(old(this.list))
      ensures r==null ==> removeNeg(old(this.list)) == []
      decreases this.footprint
      modifies this.footprint,this

    {
      if (this.data < 0){
        if (this.next != null){
          r := this.next.filter();
          return;
        } else {
          r:=null; return;
        }
      } else {
        if(this.next == null){
          r:=this; return;
        } 
        else {
          r:=this;
          var aux := this.next.filter();
          r.next := aux;
          if(aux == null){
            r.list := [r.data];
            r.footprint:= {r}; return;
          } else {
            r.list := [r.data] + aux.list;
            r.footprint := {r} + aux.footprint; return;
          }
          
        }
      }
    }

    /*
     * 3.1 - countDiffOO - 1.5 val 
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
      requires Valid() && Ex1.sorted(this.list)
      ensures r.Valid() && fresh(r.footprint) 
                && r.list == Ex1.removeReps(this.list)
      decreases |this.list|
    { 
      r:= new Node(this.data);

      if(this.next == null){
        return;
      } else {
        var aux := this.next.removeRepsOO();
        assert aux.list == Ex1.removeReps(this.next.list);
        if(this.data < aux.data){
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
    { 
    }
  }

  
}