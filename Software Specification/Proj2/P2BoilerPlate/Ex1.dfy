
function sortedPVSeq(s : seq<(int,int)>) : bool {
  forall i :: 0 <= i < |s| ==> s[i].0 >= 0 
    && 
  forall i,j :: 0 <= i < j < |s| ==> s[i].0 >= s[j].0
}

function max(i : int, j : int) : int {
  if (i >= j)
    then i 
    else j 
}

class PVNode {
  ghost var list : seq<(int,int)>; 
  ghost var footprint : set<PVNode>;

  var data : int; 
  var pri : int; 
  var next : PVNode?; 

  function Valid() : bool 
    reads this, footprint
    decreases footprint;
  {
    this in this.footprint && this.pri >=0
    &&
    if(this.next == null)
      then this.footprint == {this}
      &&
      this.list == [(this.pri,this.data)]
    else
      this.next in this.footprint
      &&
      this.footprint == {this} + this.next.footprint
      &&
      this.list == [(this.pri,this.data)] + this.next.list
      &&
      sortedPVSeq(this.list)
      &&
      this.pri >= this.next.pri
      &&
      this !in this.next.footprint
      &&
      this.next.Valid()
  }

  constructor (i : int, p : int) 
    requires p >=0
    ensures this.Valid() && this.data == i && this.next == null && this.pri == p 
      && this.footprint == {this} && this.list == [(this.pri,this.data)] 

  {
    this.data := i;
    this.pri := p;
    this.footprint := {this};
    this.list := [(this.pri,this.data)];
    this.next := null;
  }


  lemma sortedPVSeqLemma(s : seq<(int,int)>, pri : int, data: int)
    requires sortedPVSeq(s)
    requires |s| >= 1
    requires pri >= s[0].0
    ensures sortedPVSeq([(pri,data)] + s)
  {

  }

  lemma sortedPVSeqLemma1(s : seq<(int,int)>)
    requires sortedPVSeq(s)
    requires |s| >= 2
    ensures sortedPVSeq([s[0]] + s[2..])
  {

  }

  lemma sortedPVSeqLemma2(s : seq<(int,int)>)
    requires sortedPVSeq(s)
    requires |s| >=1
    ensures forall i :: 0 <= i < |s| ==> sortedPVSeq(s[i..])
  {

  }
 

  method insertPVPair (pri: int, v: int) returns (r : PVNode) 
    requires Valid()
    requires pri >= 0
    ensures r.Valid() 
    ensures r.list[0].0 <= max(old(this.list[0].0), pri)
    ensures multiset(r.list) == multiset(old(this.list)) + multiset{(pri, v)}
    ensures fresh(r.footprint - old(this.footprint))
    modifies footprint
    decreases footprint
  {   
    if(this.next == null){
      var aux := new PVNode(v,pri);
      if(this.pri <= pri){
        sortedPVSeqLemma(this.list,pri,v);
        aux.footprint := aux.footprint + this.footprint;
        aux.list := aux.list + this.list;
        aux.next := this;
        r:=aux; 
      }
      else {
        this.next := aux;
        this.footprint := this.footprint + {aux};
        this.list := this.list + [(aux.pri,aux.data)];
        r:=this;
      }
      return;
    } else {
      if(this.pri <= pri){
        var aux := new PVNode(v,pri);
        sortedPVSeqLemma(this.list,pri,v);
        aux.footprint := aux.footprint + this.footprint;
        aux.list := aux.list + this.list;
        aux.next := this;
        r:=aux;
        return; 
      } else if (this.next.pri <= pri){
        var aux := new PVNode(v,pri);
        sortedPVSeqLemma(this.next.list,pri,v);
        aux.footprint := aux.footprint + this.next.footprint;
        aux.list := aux.list + this.next.list;
        aux.next := this.next;
        
        this.next := aux;
        this.footprint := {this} + aux.footprint;
        this.list := [(this.pri,this.data)] + aux.list;
        r := this;
        return;

      } else {
        var aux := this.next.insertPVPair(pri,v);
        this.footprint := {this} + aux.footprint;
        this.list := [(this.pri,this.data)] + aux.list;
        this.next := aux;
        r := this;
        return;
      }
      
    }
           
  }
  

  method removeNode(n : PVNode) returns (r: PVNode?) 
    requires Valid()
    requires n.Valid()
    ensures (r!=null) ==> |r.list| <= |old(this.list)| 
    ensures (r!=null) ==> |r.footprint| <= |old(this.footprint)|
    ensures (r!=null && this == n) ==> this !in r.footprint
    ensures (r!=null && this != n) ==> this in r.footprint
    ensures (r== null) ==> this == n
    //ensures (r!= null) ==> r.pri <= max(n.pri, this.pri)
    //ensures (r!=null) ==> r.Valid()
    modifies this.footprint
    decreases this.footprint

  {   
    if(this.next == null){
      if(this == n){
        r:=null;
        return;
      }
      else{
        r:=this;
        return;
      }
    } else {
      if(this == n){
        r:=this.next;
        return;
      } else if(this.next == n){
        if(this.next.next != null){
          if(this.next.next == n){
            this.footprint := {this};
            this.list := [(this.pri,this.data)];
            this.next :=null;
            r := this;
            return;
          } else {
          var aux := this.list;
          this.footprint := {this} + this.next.next.footprint;
          this.list := [(this.pri,this.data)] + this.next.next.list;
          this.next := this.next.next;
          r := this;
          assert this.list == [aux[0]] + aux[2..];
          assert sortedPVSeq(this.list);
          assert this.Valid();
          return;
          }
        } else {
          this.next := null;
          this.footprint := {this};
          this.list := [(this.pri,this.data)];
          r := this;
          return;
        }
      } else {
        assert this.next != n;
        assert this.next.pri <= this.pri;
        var aux := this.next.removeNode(n);
        //assert aux in this.footprint;
        r:=this;
        assert sortedPVSeq(r.list);
        r.next := aux;
        if(aux == null){
          r.footprint := {r};
          r.list := [(r.pri,r.data)];
          return;
        } else{
          //assert aux.pri <= this.pri;
          r.footprint := {r} + aux.footprint;
          r.list := [(r.pri,r.data)] + aux.list;
          //sortedPVSeqLemma(aux.list,r.pri,r.data);
          return;
        }
        
      }
    }

  }

}



