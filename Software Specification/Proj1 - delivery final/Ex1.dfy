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
      else s[0] < s[1] && ssorted(s[1..])
  }


  /*
  * 1.1  
  */ 
  function removeReps(s : seq<int>) : seq<int> {
    if (|s| <= 1)
      then s
      else if (s[0] == s[1])
          then removeReps(s[1..])
          else [s[0]] + removeReps(s[1..])
  }

  lemma removeRepsLength(s : seq<int>)
    ensures |removeReps(s)| <= |s|
  {

  }

  lemma removeRepsLemma(s : seq<int>)
    requires |s| >=1
    ensures |removeReps(s)| >= 1
  {

  }



  /*
  *  1.2 - 0.5 val
  *  sorted(s) ==> ssorted(removeReps(s)) 
  */
  lemma removeRepsSorted(s : seq<int>)
    ensures sorted(s) ==> ssorted(removeReps(s))
  {
    
    if (|s|==0){
        assert removeReps(s)==[];
        assert ssorted(s)==ssorted([]);
    } else if (|s| == 1){
        assert removeReps(s)==s;
    } else if (|s| > 1){
        if (s[0] < s[1]){
            assert removeReps(s) == [s[0]] + removeReps(s[1..]);
            removeRepsSorted(s[1..]);
            assert |s[1..]| >= 1;
            auxiliaryLemma(s[1..]);
            assert sorted(s[1..]) ==> ssorted (removeReps(s[1..]));
            assert ssorted([s[0]] + removeReps(s[1..])) == ssorted(removeReps(s));
            

        } else if (s[0] == s[1]){
            assert removeReps(s) == removeReps(s[1..]);
            removeRepsSorted(s[1..]);
            assert sorted(s[1..]) ==> ssorted(removeReps(s[1..]));
            assert ssorted(removeReps(s[1..])) == ssorted(removeReps(s));
        }
    }
    
  }

  lemma auxiliaryLemma(s: seq<int>)
    requires |s|>=1
    ensures |removeReps(s)|>=1
    ensures removeReps(s)[0]==s[0]
  {
    assert removeReps(s)== removeReps([s[0]]+ s[1..]);
  }


  /*
    * 1.3 - 1 val   
  */
  lemma removeRepsMonotonicity(s : seq<int>, i : int)
      requires  0 <= i < |s|
      ensures |removeReps(s)| >= |removeReps(s[i..])|
      decreases s
  {
     if(|s| <= 1){
            assert i==0 ==> removeReps(s[0..])==s;
            assert i==1 ==> removeReps(s[1..])==[];
            assert removeReps(s)==s;
        } else if (|s| > 1){
            if(i == 0){
                assert s == s[0..];
            } else if (i > 0){
                if (s[0]==s[1]){
                    assert removeReps(s)== removeReps(s[1..]);
                    assert s[i..]==s[1..][(i-1)..];
                    assert removeReps(s[i..]) == removeReps(s[1..][(i-1)..]);
                    removeRepsMonotonicity(s[1..],i-1);
                    assert |removeReps(s[1..])| >= |removeReps(s[1..][(i-1)..])|;
                    assert |removeReps(s[1..])| >= |removeReps(s[i..])|;
                    assert |removeReps(s)| == |removeReps(s[1..])| 
                    >= |removeReps(s[i..])|; 
                } else {
                    assert removeReps(s)==[s[0]] + removeReps(s[1..]);
                    assert s[i..]==s[1..][(i-1)..];
                    assert removeReps(s[i..]) == removeReps(s[1..][(i-1)..]);
                    removeRepsMonotonicity(s[1..],i-1);
                    assert |removeReps(s[1..])| >= |removeReps(s[1..][(i-1)..])|;
                    assert |removeReps(s[1..])| >= |removeReps(s[i..])|;
                    assert |removeReps(s)| == |[s[0]] + removeReps(s[1..])|
                    == 1 + |removeReps(s[1..])| >= 1 + |removeReps(s[i..])|
                    >= |removeReps(s[i..])|; 
                }
            }
        }
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
     if (|s| == 1){
            calc == {
                removeReps(s + [v]);
                == {assert s[|s| -1] < v;} 
                [(s + [v])[0]] + removeReps((s + [v])[1..]);
                == 
                [s[0]] + removeReps([v]);
                == 
                [s[0]] + [v];
                ==
                removeReps(s) + [v];
            }
        }
        else if (|s| > 1) {
            if (s[0] < s[1]){
                calc == {
                    removeReps(s + [v]);
                    == {assert (s + [v])[1..] == s[1..] + [v];}
                    [s[0]] + removeReps(s[1..] + [v]);
                    == {removeRepsAppend(s[1..],v);}
                    [s[0]] + removeReps(s[1..]) + [v]; 
                    ==
                    removeReps(s) + [v];
                }
            } else if (s[0] == s[1]){
                calc == {
                    removeReps(s + [v]);
                    == {assert (s + [v])[1..] == s[1..] + [v];}
                    removeReps(s[1..] + [v]);
                    == {removeRepsAppend(s[1..],v);}
                    removeReps(s[1..]) + [v]; 
                    ==
                    removeReps(s) + [v];
                }
            }
        }
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
    if (|s| == 1){
            calc == {
                removeReps(s + [v]);
                == {assert (s + [v])[1..] == s[1..] + [v] && (s + [v])[0] == s[0];} 
                removeReps(s[1..] + [v]);
                == 
                removeReps([v]);
            }
        }
        else if (|s| > 1) {
            if (s[0] < s[1]){
                calc == {
                    removeReps(s + [v]);
                    == {assert (s + [v])[1..] == s[1..] + [v];}
                    [s[0]] + removeReps(s[1..] + [v]);
                    == {removeRepsAppend2(s[1..],v);}
                    [s[0]] + removeReps(s[1..]); 
                    ==
                    removeReps(s);
                }
            } else if (s[0] == s[1]){
                calc == {
                    removeReps(s + [v]);
                    == {assert (s + [v])[1..] == s[1..] + [v];}
                    removeReps(s[1..] + [v]);
                    == {removeRepsAppend2(s[1..],v);}
                    removeReps(s[1..]); 
                }
            }
        }
  }

}
