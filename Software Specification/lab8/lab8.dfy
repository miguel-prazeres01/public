class KVNode {

    var data : int;
    var key : int;
    var next :KVNode?;

    ghost var kv_map : map<int,int>;
    ghost var footprint : set<KVNode>;


    function Valid():bool
        reads this,this.footprint
    {
        this in footprint && this.data>=0
        && 
        if(next == null)
            then this.footprint == {this}
            &&
            this.kv_map == map[this.key := this.data]
        else 
            this.next in this.footprint
            &&
            this.footprint == {this} + this.next.footprint 
            && 
            this.kv_map == this.next.kv_map[this.key:=this.data]
            && 
            this !in this.next.footprint
            &&
            this.next.Valid()
            &&
            this.key !in this.next.kv_map.Keys
    }

    constructor(k:int,v:int)
        requires v >= 0
        ensures this.Valid() && this.data == v && this.next == null &&
            this.key==k && this.footprint == {this} && this.kv_map==map[k:=v]
    {
        this.data :=v;
        this.next := null;
        this.key := k;
        this.footprint := {this};
        this.kv_map := map[k:=v];

    }

    
    method getKeyVal (k:int) returns (r:int)
        requires k >= 0 && Valid()
        ensures k in this.kv_map.Keys ==> r==this.kv_map[k]
        ensures k !in this.kv_map.Keys ==> r==-1


    {
        r:=-1;
        var cur:=this;
        ghost var set_aux := {};

        while (cur != null)     invariant k !in set_aux
                                invariant cur != null ==> this.kv_map.Keys == set_aux + cur.kv_map.Keys
                                invariant cur == null ==> this.kv_map.Keys == set_aux
                                invariant cur != null ==> cur.kv_map.Keys !! set_aux
                                invariant cur != null ==> cur.Valid()
                                invariant cur != null ==> (forall k:: k in cur.kv_map.Keys ==> this.kv_map[k] == cur.kv_map[k])
                                decreases |this.kv_map.Keys| - |set_aux|
        
        
        {
            if(cur.key == k){
                r:= cur.data; return;
            }
            
            set_aux := set_aux + {cur.key};
            cur:=cur.next;
        }
        return;


    }

}




class HashTbl {

    var tbl: array<KVNode?>;

    ghost var footprint: set <KVNode>;
    ghost var kv_map : map<int,int>;


    function Valid() : bool 
        reads this,this.footprint,this.tbl
    {

        tbl.Length >0 &&
        
        (forall i :: 0 <= i < tbl.Length ==> 
            tbl[i] != null 
            ==> tbl[i] in this.footprint && tbl[i].footprint <= this.footprint
            && tbl[i].Valid() 
            && (forall k :: k in tbl[i].kv_map.Keys ==> k in this.kv_map && tbl[i].kv_map[k] == this.kv_map[k])
            && (forall k :: k in tbl[i].kv_map.Keys ==> k%tbl.Length == i))
        &&
        (forall i,j :: 0 <= i < j < tbl.Length ==>
            tbl[i]!=null && tbl[j]!=null ==> tbl[i].footprint !! tbl[j].footprint)
        &&
        forall k:: k in this.kv_map 
        ==>
        tbl[k%tbl.Length] != null
        && k in tbl[k%tbl.Length].kv_map &&
        this.kv_map[k] == tbl[k%tbl.Length].kv_map[k]

        
    }


    method getKeyVal (k: int) returns (r:int)
        requires this.Valid() && k >0
        ensures k in this.kv_map ==> r == kv_map[k]
        ensures k !in this.kv_map ==> r ==-1
    
    {
        var aux := tbl[k%this.tbl.Length];
        if(aux!=null){
            r := aux.getKeyVal(k); return;
        } else {
            r:=-1; return;
        }
    }

}