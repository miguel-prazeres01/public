
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

  }


  /*
   * 2.2 - removeRepsArr -> implement and prove method - 1.5 val 
  */
  method removeRepsArr(arr : array<int>) returns (ret : array<int>) 
    requires Ex1.sorted(arr[..])
    ensures ret[..] == Ex1.removeReps(arr[..])
  {
   
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


