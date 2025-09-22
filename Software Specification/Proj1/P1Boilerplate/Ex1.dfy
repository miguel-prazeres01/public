/*
 * Group I - 6 val 
 */ 

module Ex1 {

  function sorted (s : seq<int>) : bool {
    if (|s| < 2)
      then true
      else s[0] <= s[1] && sorted(s[1..])
  }

  function ssorted (s : seq<int>) : bool {
    if (|s| < 2) 
      then true
      else s[0] < s[1] && sorted(s[1..])
  }


  /*
  * 1.1  
  */ 
  function removeReps(s : seq<int>) : seq<int> {

  }


  /*
  *  1.2 - 0.5 val
  *  sorted(s) ==> ssorted(s) 
  */



  /*
    * 1.3 - 1 val   
  */
  lemma removeRepsMonotonicity(s : seq<int>, i : int)
      requires  0 <= i < |s|
      ensures |removeReps(s)| >= |removeReps(s[i..])|
      decreases s
  {

  }


  /*
    * 1.4 - 1.5 val
    * calculational style   
  */
  lemma removeRepsAppend(s : seq<int>, v : int)
    requires sorted(s) 
    requires |s| > 0 ==> v > s[|s|-1]
    ensures removeReps(s+[v]) == removeReps(s) + [ v ]
  {
    
  }


  /*
    * 1.5 - 1.5 val
    * calculational style   
  */
  lemma removeRepsAppend2(s : seq<int>, v : int)
    requires sorted(s) 
    requires |s| > 0 && v == s[|s|-1]
    ensures removeReps(s+[v]) == removeReps(s) 
  {
    
  }

}
