

function incrListF(s : seq<int>) : seq<int> 
    decreases s 
  {
    if (s == []) 
      then [] 
      else [ s[0] + 1 ] + incrListF(s[1..])
  }

 function filterF(s : seq<int>) : seq<int> 
    decreases s 
  {
    if (s == []) 
      then [] 
      else if s[0] < 0
        then filterF(s[1..])
        else [ s[0] ] + filterF(s[1..])
  }

  predicate sorted (s : seq<int>) {
    forall i :: forall j :: 0 <= i < j < |s| ==> s[i] <= s[j]
  }



  function mergeF(s1 : seq<int>, s2 : seq<int>) : seq<int> 
    decreases |s2|+|s1|
  {
    if (s2 == [])
      then s1
      else if s1 == []
        then s2 
        else if s1[0] <= s2[0]
          then [ s1[0] ] + mergeF(s1[1..], s2)
          else [ s2[0] ] + mergeF(s1, s2[1..])
  }


class Node {
  var list : seq<int>; 
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


  method prepend (i : int) returns (r : Node) 
    requires Valid()
    ensures Valid() && r.Valid() && r.list == [ i ] + this.list 
  {
    r := new Node; 
    r.next := this; 
    r.data := i; 
    r.list := [ i ] + this.list; 
    r.footprint := { r } + this.footprint; 
  }

  method appendRec (i : int) 
    requires Valid () 
    ensures Valid () 
    ensures fresh(footprint - old(footprint))
    ensures list == old(list) + [ i ]
    decreases footprint
    modifies footprint
  { 

    var original_list := list; 

    if (next == null) {
      var nd := new Node;
      nd.next := null; 
      nd.data := i; 
      nd.list := [ i ]; 
      nd.footprint := { nd };

      this.footprint := { this, nd };
      this.list := [ data, i ];
      this.next := nd; 

    } else {
      next.appendRec(i);
      list := [ data ] + next.list;
      footprint := { this } + next.footprint; 
    }
  }

  method find(i : int) returns (r : bool) 
    requires Valid() 
    ensures r == (i in this.list)
    decreases footprint
  {
    if (data == i) {
      assert list[0] == data; 
      r := true;
    } else {
      if (next == null) {
        r := false; 
      } else {
        r := next.find(i);
        if (r == false) {
        } else {
          assert r == true; 
          var next_list := list[1..];
          assert next_list == next.list; 
        }
      }
    }
  }



  method findIter (i : int) returns (r : bool) 
    requires Valid()
  {
    var cur := this; 
    ghost var seq_aux := []; 
    while (cur != null) 
      invariant cur != null ==> cur.Valid()
      invariant cur != null ==> list == seq_aux + cur.list
      invariant cur == null ==> list == seq_aux 
      decreases |list|-|seq_aux|
    {
      if (cur.data == i) {
        r := true; return;
      }
      seq_aux := seq_aux + [ cur.data ];
      cur := cur.next; 
    }
    r := false; 

  }


  method findInter1(i : int) returns (r:bool)
    requires Valid()
    ensures r == (i in this.list)
  
  {
    if(this.data == i){
      r:=true; return;
    } else{
      var prev:=this;
      var cur:=this.next;
      ghost var seq_aux:=[prev.data];
      
      while(cur != null)    invariant cur != null ==> cur.Valid()
                            invariant cur != null ==> this.list == seq_aux + cur.list
                            invariant i !in seq_aux
                            invariant cur == null ==> this.list == seq_aux
                            invariant prev != null
                            invariant prev.next == cur
                            invariant prev.Valid()
                            decreases prev.footprint
                            
      
      {
        if(cur.data == i) {
          r:=true; return;
        }
        prev:=cur;
        cur:=cur.next;
        seq_aux:= seq_aux + [prev.data];

      }
      r:=false;
      return;
    }
  }


  method incrListNew () returns (r : Node) 
    requires Valid()
    ensures Valid() && r.Valid() && fresh(r.footprint) && r.list == incrListF(this.list)
    decreases footprint 
  {
    r := new Node; 
    r.data := this.data+1; 

    if (this.next == null) {
      r.footprint := { r }; 
      r.list := [ r.data ]; 
      r.next := null; 
    } else {
      var r_next := this.next.incrListNew(); 

      r.footprint := r_next.footprint + { r }; 
      r.list := [ r.data ] + r_next.list; 
      r.next := r_next; 
    }


  }

  
  
  method incrList () 
    requires Valid()
    ensures Valid() && (footprint == old(footprint)) && (this.list == incrListF(old(this.list)))
    modifies footprint 
    decreases footprint 
  {
    if (this.next == null) {
      this.data := this.data + 1; 
      this.list := [ this.data ];

      assert this.Valid();

    } else {
      next.incrList(); 
      this.data := this.data + 1;
      this.list := [ this.data ] + this.next.list;    
    }
  }


  method filter () returns (r : Node?)
    requires Valid() 
    ensures (r != null) ==> (r.Valid() && (r.footprint <= old(footprint)) && (r.list == filterF(old(list))))
    ensures (r == null) ==> [] == filterF(old(list))
    modifies footprint 
    decreases footprint 
  {
    if (this.next == null) {
      if (this.data < 0) {
        r := null; 
      } else {
        r := this; 
      }
    } else {
      if (this.data < 0) {
        r := this.next.filter(); 
      } else {
        r := this;
        var next := this.next.filter();
        r.next := next; 
        if (next == null) {
          r.list := [ r.data ];
          r.footprint := { r };
        } else {
          r.list := [ r.data ] + next.list; 
          r.footprint := { r } + next.footprint; 
        }
      }
    }
  }

  method merge (nd : Node) returns (r : Node)
    requires Valid() && nd.Valid() && (nd.footprint !! footprint) && sorted(nd.list) && sorted(this.list)
    ensures r.Valid() && (r.footprint == old(footprint) + old(nd.footprint)) 
    ensures mergeF(old(list), old(nd.list)) == r.list
    modifies footprint + nd.footprint
    decreases |footprint| + |nd.footprint|
  {
    var old_footprint := this.footprint; 
    var old_nd_footprint := nd.footprint; 
    var old_list := this.list; 
    var old_nd_list := nd.list; 

    if (this.data <= nd.data) {
      if (this.next == null) {
        assert this.Valid(); 
        assert old_footprint == { this };
        assert nd.Valid(); 

        this.next := nd; 
        this.list := [ this.data ] + nd.list; 
        this.footprint := { this } + nd.footprint;  
        r := this; 
        
        assert r.Valid();
        assert mergeF(old_list, old_nd_list) == r.list; 
        assert r.footprint == old_footprint + old_nd_footprint; 

      } else {
        assert nd.Valid(); 
        assert this.next.Valid();
        assert sorted(nd.list);
        
        assert this.next.list == this.list[1..];

        assert sorted(this.next.list);
        var next_nd := this.next.merge(nd); 
        this.next := next_nd; 
        this.list := [ this.data ] + next_nd.list; 
        this.footprint := { this } + next_nd.footprint; 

        r := this; 
      }
    } else {
      if (nd.next == null) {
        nd.next := this;
        nd.list := [ nd.data] + this.list; 
        nd.footprint := { nd } + this.footprint; 
        r := nd;
      } else {
        assert sorted(nd.list);
        assert nd.next.list == nd.list[1..];
        
        var new_this := this.merge(nd.next);
        nd.next := new_this;
        nd.list := [ nd.data ] + new_this.list; 
        nd.footprint := { nd } + new_this.footprint;  

        r := nd; 
      }
    }

  }

}