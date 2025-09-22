
include "Ex1.dfy"

/*
 * Group II -  
 */ 

module Ex2 {
  import Ex1=Ex1


  /*
   * 2.1 - countDiffArr - 1.5 val 
  */
  method countDiff(arr : array<int>) returns (ret : int) 
    requires Ex1.sorted(arr[..])
    ensures ret == |Ex1.removeReps(arr[..])|
  {
    var idx := arr.Length-1;
    if(arr.Length == 0){
      ret:=0;
      {assert ret == |Ex1.removeReps([])|;}
      return;
    }
    ret:=1;
    while (idx > 0)     invariant 0 <= idx < arr.Length 
                        invariant Ex1.sorted(arr[idx..]) ==> ret == |Ex1.removeReps(arr[idx..])|
                            
    {
      if(arr[idx] > arr[idx-1]){
        ret:= ret + 1;
      }
      idx := idx - 1;
    } 
    {assert ret == |Ex1.removeReps(arr[..])|;}
    return;
        //do right to left
  }

function sortedForAll (s : seq<int>) : bool {
  forall i, j :: 0 <= i < j < |s| ==> s[i] <= s[j]
}

  /*
   * 2.2 - removeRepsArr -> implement and prove method - 1.5 val 
  */
  method removeRepsArr(arr : array<int>) returns (ret : array<int>) 
    requires Ex1.sorted(arr[..])
    ensures ret[..] == Ex1.removeReps(arr[..])
    ensures ret.Length == |Ex1.removeReps(arr[..])|
  {
    var idx := arr.Length-1;
    var size := countDiff(arr);
    var idx1 := size-1;
    ret:= new int[size];
    if(ret.Length == 0){
      {assert ret[..] == Ex1.removeReps([]);}
      return;
    }
    if(ret.Length == 1){
      if(arr.Length==1){
          ret[idx1]:=arr[idx];
          {assert ret[..] == Ex1.removeReps(arr[..]);}
      }
      else{
          ret[idx1]:=arr[0]; 
          {assert ret[..] == Ex1.removeReps(arr[..]);}
      }
      
      return;
    }
    Ex1.removeRepsMonotonicity(ret[..], idx1);
    Ex1.removeRepsMonotonicity(arr[..], idx); 
    while (idx > 0 && idx1 > 0)          invariant 0 <= idx1 < ret.Length
                                         invariant 0 <= idx < arr.Length
                                         invariant Ex1.sorted(arr[idx..])==>sortedForAll(arr[idx..])
                                         invariant Ex1.sorted(arr[idx..]) ==> ret.Length == |Ex1.removeReps(arr[..])|
                                         invariant Ex1.sorted(arr[idx..])  ==> |Ex1.removeReps(arr[..])| == ret.Length
                                         invariant Ex1.sorted(arr[idx..])  ==> Ex1.removeReps(arr[..]) == ret[..]
                                         decreases idx1
                                         decreases idx
    {
      if(arr[idx] > arr[idx-1]){
        ret[idx1]:= arr[idx]; 
        idx1 := idx1 - 1;
      }
      idx := idx - 1;
    }
    ret[idx1]:=arr[idx];
    {assert ret[..] == Ex1.removeReps(arr[..]);}
    return;
  }



  /*
   * 2.3 - removeRepsArrIP -> implement and prove method - 2 val 
  */
  method removeRepsArrIP(arr : array<int>) returns (ret : int)
    requires Ex1.sorted(arr[..])
    ensures 
      old(arr.Length) > 0 ==> 
        0 <= ret <= arr.Length 
          && 
        Ex1.removeReps(old(arr[..])) == arr[..ret]
          &&
        forall k :: ret <= k < arr.Length ==> arr[k] == 0
    modifies arr
  {
  }

}


