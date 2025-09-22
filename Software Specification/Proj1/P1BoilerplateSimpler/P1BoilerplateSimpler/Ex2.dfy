
include "Ex1.dfy"

/*
 * Group II -  
 */ 

module Ex2 {
  import Ex1=Ex1


  /*
   * 2.1 - countDiffArr - 1.5 val 
     -- No changes to original 
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
  }


  /*
   * 2.2 - removeRepsArr -> implement and prove method - 1.5 val 
     -- No changes to original 
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

  method removeDiffIP2(arr : array<int>) returns (ret : int)
    requires sortedForAll(arr[..])
    ensures 0 <= ret <= arr.Length
    ensures ssortedForAll(arr[..ret])
    ensures forall k :: ret <= k < arr.Length ==> arr[k] == 0 
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
    sortedEquiv2(arr[..]);
    ret := countDiff(arr);
    var idx := arr.Length-1;
  
    var arrA:= new int[ret];
    arrA:=removeRepsArr(arr);
    Ex1.auxiliaryLemma(arr[..]);
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
        if(idx==0){
          break;
        }
        idx := idx -1;
      }
    }
    assert forall k :: ret <= k < arr.Length ==> arr[k] == 0;
    return;
  }




    /*sortedEquiv2(arr[..]);
    ret := countDiff(arr);
    var idx := 0;
    Ex1.removeRepsLength(arr[..]);
    var arrA:= new int[ret];
    arrA:=removeRepsArr(arr);

    while (idx < arr.Length)       invariant 0 <= idx <= arr.Length
                                   invariant ssortedForAll(arr[..ret])
                                   invariant forall k :: ret <= k < arr.Length ==> arr[k] == 0
                                   invariant forall k :: 0 <= k < ret ==> arr[k] == arrA[k]
                             
    {
      if(idx < ret){
        arr[idx]:= arrA[idx];
        idx := idx + 1;
      }
      else{
        arr[idx]:= 0;
        idx := idx + 1;
      }
    }

    return;
    */
  


    /*if(arr.Length == 0){
      ret:=0; return;
    }

    sortedEquiv2(arr[..]);
    var size := countDiff(arr);
    var idx := 1;
    var aux := arr[0];
    ret :=1;
    while (idx < arr.Length) invariant 1 <= idx <=arr.Length
                             invariant 1 <= ret <= arr.Length
                             invariant ret <= idx
                             invariant ssortedForAll(arr[..ret])

    {
      if (ret == size+1){
        //assert forall k :: ret <= k < idx ==> arr[k] == 0;
        arr[idx]:=0;
      }else if(arr[idx] != aux){
        Ex1.removeRepsMonotonicity(arr[..], ret);
        arr[ret]:=arr[idx];
        ret := ret + 1;
      }
      idx := idx + 1;
    } 

    return;
  */
    

  /* To prove this specification, you will need the following alternative 
     predicates and lemmas */
  
function sortedForAll (s : seq<int>) : bool {
  forall i, j :: 0 <= i < j < |s| ==> s[i] <= s[j]
}

function ssortedForAll (s : seq<int>) : bool {
  forall i, j :: 0 <= i < j < |s| ==> s[i] < s[j]
}

lemma sortedEquiv1(s : seq<int>) 
  requires Ex1.sorted(s)
  ensures sortedForAll(s)
{

}

lemma sortedEquiv2(s : seq<int>) 
  requires sortedForAll(s)
  ensures Ex1.sorted(s)
{

}

lemma ssortedEquiv(s : seq<int>)
  requires Ex1.ssorted(s)
  ensures ssortedForAll(s)
{

}

lemma ssortedEquiv1(s : seq<int>)
  requires ssortedForAll(s) 
  ensures Ex1.ssorted(s)
{

}


}


