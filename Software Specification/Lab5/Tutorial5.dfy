/*
 * 1 - Increment all the elements of the array by 1
 */
method arrIncrement (a : array<int>) 
  ensures forall k :: 0 <= k < a.Length ==> a[k] == old(a[k])+1
  modifies a
{
  var i := 0; 
  while (i < a.Length) 
    invariant 0 <= i <= a.Length
    invariant forall k :: 0 <= k < i ==> a[k] == old(a[k]) + 1
    invariant forall k :: i <= k < a.Length ==> a[k] == old(a[k])
    decreases a.Length -i
  {
    a[i] := a[i] + 1; 
    i := i + 1; 
  }
}


/*
 * 2 - Swap the elements i and j of the array given as input
 */

method swap (a : array<int>, i : int, j : int)
  requires 0 <= i < a.Length
  requires 0 <= j < a.Length   
  ensures a[i] == old(a[j]) && a[j] == old(a[i])
  ensures forall  k :: 0 <= k < a.Length && k != i && k != j ==> a[k] == old(a[k]) 
  ensures multiset(a[..]) == multiset(old(a[..]))
  modifies a { 

    var tmp := a[i]; 
    a[i] := a[j]; 
    a[j] := tmp;
  }


/*
 * 3 - Implement the partition method of QuickSort 
 *   - Your specification must not forget the elements of the origianl array 
 */


method Partition(a : array<int>) 
    returns (i : int, v : int)
    requires a.Length > 0 
    ensures 0 <= i < a.Length
    ensures forall k :: 0 <= k < i ==> a[k] < v
    ensures forall k :: i < k < a.Length ==> a[k] >= v
    ensures a[i] == v
    modifies a {

  i := 0; 
  var j := 0;
  var r := a.Length - 1; 
  v := a[r];    

  while (j <= r-1) 
      invariant 0 <= i <= j <= r
      invariant forall k :: 0 <= k < i ==> a[k] < v
      invariant forall k :: i <= k < j ==> a[k] >= v
      invariant a[r] == v
      decreases r-j {
    if (a[j] < v) {
      swap(a, i, j);
      i := i+1;
    } 
    j := j+1;   
  }

  swap(a, i, r);
  return;
}


/*
 * 4 - Implement the insert method of InsertionSort 
 *   - Your specification must not forget the elements of the origianl array 
 */


predicate sorted (s : seq<int>) {
    forall i :: forall j :: 0 <= i < j < |s| ==> s[i] <= s[j]                 
}

method Insert(a : array<int>, k : int) 
  requires 0 < k < a.Length 
  requires sorted(a[..k]) 
  ensures sorted(a[..(k+1)])
  ensures multiset(a[..]) == old(multiset(a[..]))
  modifies a {

    var i := k;
    var v := a[k];  

    while (i > 0) 
      invariant i < a.Length
      invariant k < a.Length
      invariant i <= k 
      invariant i >= 0
      invariant sorted(a[..i]) 
      invariant (i < k) ==> sorted(a[(i+1)..(k+1)])
      invariant forall j1 :: i < j1 <= k ==> a[i] <= a[j1] 
      invariant forall j1 :: forall j2 :: (0 <= j1 < i) && (i < j2 <= k) ==> a[j1] <= a[j2] 
      invariant multiset(a[..]) == old(multiset(a[..]))
      decreases i 
      {
        if (a[i-1] <= a[i]) { 
          return; 
        } else {

          ghost var old_right := if (i == k) then [] else a[(i+1)..(k+1)]; 
        
       
          swap(a, i-1, i);
          
          ghost var new_right := if (i == k) then [] else a[(i+1)..(k+1)]; 
          assert new_right == old_right;
          assert sorted (new_right); 

          i := i - 1; 
          
        }
    }
  
}