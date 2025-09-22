
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
    var idx := arr.Length-2;
    if(arr.Length == 0){
      ret:=0;
      return;
    } 

    if(arr.Length == 1){
      ret:=1;
      return;
    }

 
    ret:=1;
    while (idx >= 0)     invariant 0 <= idx < arr.Length 
                         invariant Ex1.sorted(arr[(idx+1)..]) ==> ret == |Ex1.removeReps(arr[(idx+1)..])|
                         decreases idx
                            
    {
      if(arr[idx] < arr[idx+1]){
        ret:= ret + 1;
      }
      if (idx == 0){
        break;
      }
      idx := idx - 1;
      
    } 
    return;
        //do right to left
  }


  /*
   * 2.2 - removeRepsArr -> implement and prove method - 1.5 val 
  */
  method removeRepsArr(arr : array<int>) returns (ret : array<int>) 
    requires Ex1.sorted(arr[..])
    ensures ret[..] == Ex1.removeReps(arr[..])
  {
    var i := arr.Length-2;
    var size := countDiff(arr);
    var j := size-2;
    ret:= new int[size];

    if(arr.Length == 0 || ret.Length == 0 ){
      return;
    }

    if(arr.Length==1){
      ret[0]:=arr[0];
      return; 
    }
    ret[j+1]:=arr[i+1];
    Ex1.auxiliaryLemma(arr[..]);
    while (i > 0)         invariant -1 <= j < ret.Length
                          invariant 0 <= i < arr.Length
                          invariant ret[(j+1)..] == Ex1.removeReps(arr[(i+1)..])
    {
      if(arr[i] != arr[i+1]){
        Ex1.removeRepsMonotonicity(arr[..], i);
        ret[j]:= arr[i];
        j := j - 1; 
      }
      i := i - 1;
    }
    ret[0] := arr[0];
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
    if (arr.Length == 0){
      ret:=0;
      return;
    }
    if (arr.Length == 1){
      ret:=1;
      return;
    }

    ret := countDiff(arr);
    var idx := arr.Length-1;
  
    var arrA:= new int[ret];
    arrA:=removeRepsArr(arr);
    assert arr.Length >= 1;
    Ex1.removeRepsLemma(arr[..]);
    Ex1.removeRepsLength(arr[..]);

    while (idx >= 0)      
                          invariant arrA.Length >=1 
                          invariant 0 <= idx <= arr.Length
                          invariant 0 <= ret <= arr.Length 
                          decreases idx                   
                             
    {
      
      if(idx >= ret){
        arr[idx]:= 0;
        idx := idx -1;
      }
      else{
        arr[idx]:= arrA[idx];
        if(idx != 0){
          idx := idx -1;
        }
      }
    }
    assert forall k :: ret <= k < arr.Length ==> arr[k] == 0;
    return;
  }

}


