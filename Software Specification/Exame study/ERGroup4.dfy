
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

  method findMax() returns (r : int)
    requires Valid()
    ensures forall k :: 0 <= k < |list| ==> list[k] <= r
    ensures exists k :: 0 <= k < |list| ==> list[k] == r
  {

    r := this.data; 
    var cur := this.next; 
    ghost var seq_aux := [ this.data ]; 

    assert r == seq_aux[0];

    ghost var idx := 1; 
    ghost var r_idx := 0; 

    while (cur != null)
      invariant cur != null ==> cur.Valid()
      invariant cur != null ==> list == seq_aux + cur.list; 
      invariant cur == null ==> list == seq_aux; 
      invariant cur != null ==> idx < |list| && cur.data == list[idx]
      invariant forall k :: 0 <= k < |seq_aux| ==> seq_aux[k] <= r
      invariant 0 <= r_idx < idx <= |seq_aux|
      invariant seq_aux == list[..idx]
      invariant r == list[r_idx]
      decreases if (cur != null ) then cur.footprint else {}
    {
      if (cur.data > r) {
        r := cur.data;
        r_idx := idx; 
      } 

      seq_aux := seq_aux + [ cur.data ];
      idx := idx + 1; 
      cur := cur.next; 
    }

    assert list[r_idx] == r; 
    assert forall k :: 0 <= k < |list| ==> list[k] <= r;
    assert exists k :: 0 <= k < |list| ==> list[k] == r;

  }

  method concatList(nl : Node) 
    requires Valid() && nl.Valid()
    requires this.footprint !! nl.footprint
    ensures Valid() 
    ensures this.footprint == old(this.footprint) + nl.footprint
    modifies this, this.footprint
    decreases this.footprint
  {
    if (this.next == null) {
      this.next := nl; 
      this.footprint := { this } + nl.footprint; 
      this.list := [ this.data ] + nl.list; 
    } else {
      assert this.next != null; 
      this.next.concatList(nl);
      this.list := [ this.data ] + next.list; 
      this.footprint := { this } + next.footprint;
    }
  }

  method addList(nl : Node) 
    requires Valid() 
    requires |nl.list| == |list| 
    requires nl.Valid()
    requires this.footprint !! nl.footprint
    ensures Valid() 
    ensures footprint == old(footprint)
    decreases this.footprint
    modifies this, this.footprint
  {
    if (this.next == null) {
      assert nl.next == null; 
      this.data := this.data + nl.data; 
      this.list := [ this.data ];
    } else {
      assert |nl.next.list| == |this.next.list|;
      this.next.addList(nl.next);
      this.data := this.data + nl.data;
      this.list := [ this.data ] + this.next.list; 
    }
  }
}
    