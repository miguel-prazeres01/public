datatype option<T> = Some(T) | None


class DynArray {

  var arr: array<int>; 
  var tbl: array<bool>; 

  var dyn_size: int; 
  const alloc_size_const : int := 1000;  

  constructor (s : int) 
  {
    if (s < alloc_size_const) {
      arr := new int[alloc_size_const];
      tbl := new bool[alloc_size_const];
      dyn_size := alloc_size_const;
    } else {
      arr := new int[s];
      tbl := new bool[s];
      dyn_size := s; 
    }

    new;
    forall k | 0 <= k < tbl.Length {
      tbl[k] := false; 
    }
  }

  function Valid () : bool 
    reads this
  {
    alloc_size_const <= dyn_size == arr.Length == tbl.Length 
  }

  method lookup (i : int) returns (r : (bool, int))
    requires Valid()
    ensures if (i >= 0 && i < dyn_size && tbl[i]) then r == (true, arr[i]) else r == (false, -1)
  {
    if ((i < 0) || (i >= dyn_size)) {
      r := (false, -1); return; 
    } else {
      assert i < dyn_size; 
      assert i < tbl.Length; 
      if (tbl[i]) {
        r := (true, arr[i]); return; 
      } else {
        r := (false, -1); return; 
      }
    }
  }



  method store1 (i : int, v : int) returns (r : bool)
    requires Valid()
    ensures Valid()
    ensures r ==> 0 <= i < arr.Length && arr[i] == v
    ensures r && i < old(dyn_size) 
              ==> this.arr.Length == old(this.arr.Length) 
                    && arr[..] == old(arr[..])[i := v]
    ensures r && i > old(dyn_size) ==> arr[..old(dyn_size)] == old(arr[..])
    ensures !r ==> i < 0

    modifies this, this.arr, this.tbl
  {
    ghost var old_arr := arr[..];
    ghost var old_tbl := tbl[..];
    ghost var old_dyn_size := dyn_size; 

    if (i < 0) {
      r := false; return; 
    } else {
      if (i >= dyn_size) {
        var new_arr := new int[i+alloc_size_const];  
        var new_tbl := new bool[i+alloc_size_const];

        assert new_arr.Length > tbl.Length; 
        assert tbl.Length == old_dyn_size == |old_arr| == |old_tbl|; 

        var k := 0; 
        while (k < tbl.Length) 
          invariant 0 <= k <= tbl.Length
          invariant tbl.Length == arr.Length
          invariant new_arr.Length > tbl.Length
          invariant tbl.Length == arr.Length == old_dyn_size == |old_arr| == |old_tbl|
          invariant old_arr[..k] == new_arr[..k];
          invariant old_tbl[..k] == new_tbl[..k];
          invariant arr[k..] == old_arr[k..]
          invariant tbl[k..] == old_tbl[k..]
          invariant new_arr.Length == new_tbl.Length == i+alloc_size_const
        {
          new_arr[k] := arr[k];
          new_tbl[k] := tbl[k]; 
          k := k+1;
        }

        assert new_arr[..old_dyn_size] == old_arr;

        this.arr := new_arr; 
        this.tbl := new_tbl; 
        this.dyn_size := i+alloc_size_const; 
        assert this.Valid(); 
        assert i < new_arr.Length; 
        assert i < new_tbl.Length; 
      } 

      assert i < old(dyn_size) ==> (0 <= i < arr.Length && this.arr.Length == old(this.arr.Length));
      assert i < this.arr.Length;
      assert i < this.tbl.Length; 

      this.arr[i] := v; 
      this.tbl[i] := true; 
      r := true; 

      assert Valid();
    }
  }


}