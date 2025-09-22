/*
 * 1 - Linear Search with array
 */

method LinearSearch (arr : array<int>, v : int) 
  returns (ret: int) 
  ensures ret == -1 ==> forall i :: 0 <= i < arr.Length ==> arr[i] != v 
  ensures ret != -1 ==> 0 <= ret < arr.Length && v == arr[ret] {

    var idx := 0; 
    while (idx < arr.Length) 
        decreases arr.Length - idx
        invariant 0 <= idx <= arr.Length
        invariant forall i :: 0 <= i < idx ==> arr[i] != v {
      if (arr[idx] == v) {
        ret := idx; 
        return; 
      }
      idx := idx + 1; 
    }

    ret := -1; 
    return; 
}


/*
 * 2 - Binary Search with array
 */

predicate sorted (s : seq<int>) {
    forall k1 :: forall k2 :: 
        0 <= k1 < k2 < |s| ==>  s[k1] <= s[k2]
}

method BinSearch(arr : array<int>, v : int) returns (i : int)
    requires sorted(arr[..])
    ensures i == -1 ==> forall k :: 0 <= k < arr.Length ==> arr[k] != v
    ensures i != -1 ==> 0 <= i < arr.Length && arr[i] == v
{
    var l := 0; 
    var r := arr.Length-1; 
    while (l <= r) 
        invariant 0 <= l <= arr.Length
        invariant -1 <= r < arr.Length
        invariant (l <= r) ==> l < arr.Length && r >= 0
        invariant forall k :: 0 <= k < l ==> arr[k] < v
        invariant forall k :: r < k < arr.Length ==> arr[k] > v
        decreases r-l
    {
        var m : int := (l+r)/2; 
        if (arr[m] == v) {
            i := m; return;
        } else {
            if (arr[m] < v) {
                l := m+1; 
            } else {
                r := m-1;
            }
        }
    }

    i := -1; 

}


/**
  3: Find two indexes whose values add up to the given input on an ordered array
 */


predicate NoPairV(s : seq<int>, v : int) {
    forall j, k :: 0 <= j < k < |s| ==> s[j] + s[k] != v 
}

method findIndexes(a : array<int>, v : int) 
    returns (i : int, j : int)
    requires a.Length > 0 
    requires sorted(a[..])
    ensures i != -1 && j != -1 ==> 0 <= i < a.Length && 0 <= j < a.Length && a[i] + a[j] == v
    ensures i == -1 && j == -1  ==> NoPairV(a[..], v) 
    ensures i == -1 || j == -1 ==> i == -1 && j == -1 
    {
        i := 0; 
        j := a.Length-1; 

        while (i<j) 
            invariant i <= j
            invariant i < j ==> 0 <= i < a.Length
            invariant i < j ==> 0 <= j < a.Length
            invariant 0 <= i < a.Length
            invariant 0 <= j < a.Length
            invariant forall k :: 0 <= k < i  ==> a[k] + a[j] < v
            invariant forall k :: j < k < a.Length ==> a[i] + a[k] > v 
            invariant forall k1, k2 :: 0 <= k1 < i <= j < k2 < a.Length ==> a[k1] + a[k2] != v 

            decreases j-i {
            if (a[i] + a[j] == v) {
                return;
            } else {
                if (a[i] + a[j] > v) {
                    j := j - 1;
                    assert i < a.Length; 
                } else {
                    i := i + 1; 
                }
            } 
        }

        assert i == j; 
        assert forall k1, k2 :: 0 <= k1 < i <= j < k2 < a.Length ==> a[k1] + a[k2] != v; 
        i := -1; 
        j := -1; 
        return; 

    }


/**
  4: Find two indexes whose values add up to the given input on an ordered array
 */

method arrayCopy (arr : array<int>) returns (ret : array<int>) 
    ensures fresh(ret)
    ensures arr[..] == ret[..]
{
    ret := new int[arr.Length];

    var i := 0; 
    while (i < arr.Length)
        invariant i <= arr.Length 
        invariant arr[..i] == ret[..i]
        decreases arr.Length - i 
    {
        ret[i] := arr[i];
        i := i+1;
    }
}