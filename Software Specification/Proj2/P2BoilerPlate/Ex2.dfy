
include "Ex1.dfy"

class PriQueue {
  var fst : PVNode?; 

  ghost var pvs: multiset<(int,int)>;
  ghost var footprint : set<PVNode>;

  function ValidQ() : bool
    reads this, fst, if fst == null then {} else fst.footprint 
  { 
    if fst != null then
    fst.footprint == this.footprint &&
    multiset(fst.list) == this.pvs && fst.Valid()
    else
    this.footprint == {} && this.pvs == multiset([])
  }
  
  method enqueue (pri: int, v: int)
    requires pri >= 0 && ValidQ()
    ensures ValidQ()
    ensures pvs == old(pvs) + multiset{(pri,v)}
    modifies this, this.footprint
  {
    if(fst != null){
      fst := fst.insertPVPair(pri,v);
      pvs := multiset(fst.list);
      footprint := fst.footprint;

    } else {
      fst := new PVNode(v,pri);
      pvs := multiset{(pri,v)};
      footprint := {fst};

    }
    
  }

  method dequeue () returns (r : (int, int)) 
    requires ValidQ()
    ensures ValidQ()
    ensures r == (-1,-1) ==> fst == null
    ensures (fst != null && old(fst!=null)) ==> r.0 == old(fst.pri) && 
                            r.1 == old(fst.data) && fst == old(fst.next)     
    modifies this,this.footprint
  {
    if(fst == null){
      r:= (-1,-1);
      this.footprint := {};
      this.pvs := multiset([]);
      return;
    } else {
      if(fst.next == null){
        r := (fst.pri,fst.data);
        fst := null;
        this.footprint := {};
        this.pvs := multiset([]);
      } else {
        r := (fst.pri,fst.data);
        fst := fst.next;
        this.footprint := fst.footprint;
        this.pvs := multiset(fst.list);
      }
    }
  }

    
  
}