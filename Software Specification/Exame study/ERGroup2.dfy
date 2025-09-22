
/*
  * Ex 1
 */

function sorted (s : seq<int>) : bool {
  if (|s| < 2)
    then true
    else s[0] <= s[1] && sorted(s[1..])
}


function removeReps(s : seq<int>) : seq<int> {
  if (|s| < 2)
    then s 
    else if (s[0] == s[1])
      then removeReps(s[1..])
      else [ s[0] ] + removeReps(s[1..])
}


function sortedForAll (s : seq<int>) : bool {
  forall i, j :: 0 <= i < j < |s| ==> s[i] <= s[j]
}

lemma removeRepsAppend(s : seq<int>, v : int)
  requires sortedForAll(s) 
  requires |s| > 0 ==> v > s[|s|-1]
  ensures removeReps(s+[v]) == removeReps(s) + [ v ]
{
  if (s == []) {
    calc == {
      removeReps(s + [v]);
        ==
      removeReps([] + [v]);
        ==
      removeReps([v]);
        ==
      [v];
        ==
      removeReps([]) + [ v ];
        ==
      removeReps(s) + [ v ];
    }
  } else if (|s| == 1) {
    calc == {
        removeReps(s + [v]);
          ==
        removeReps([s[0]] + [v]);
          ==
        removeReps([s[0], v]);
          ==
        [s[0], v];
          ==
        removeReps([ s[0] ]) + [ v ];
          ==
        removeReps(s) + [ v ];
      }
  } else {
    
    if (s[0] == s[1]) {
      calc == {
        removeReps(s + [v]);
          == { 
              assert (s + [v])[0] == s[0];
              assert (s + [v])[1..] == s[1..] + [ v ];
             }
        removeReps(s[1..] + [v]);
          ==
        removeReps(s[1..]) + [v];
          == 
        removeReps( [ s[0] ] + s[1..]) + [ v ];
          ==
        removeReps(s) + [ v ];
      }
    } else {
      calc == {
        removeReps(s + [v]);
          ==
        [ s[0] ] + removeReps((s + [v])[1..]);
          ==  { assert (s + [v])[1..] == s[1..] + [ v ]; }
        [ s[0] ] + removeReps(s[1..] + [ v ]);
          ==
        [ s[0] ] + removeReps(s[1..]) + [ v ];
          ==
        removeReps(s) + [ v ];
      }
    }
  }
}

lemma removeRepsAppend2(s : seq<int>, v : int)
  requires sortedForAll(s) 
  requires |s| > 0 && v == s[|s|-1]
  ensures removeReps(s+[v]) == removeReps(s) 
{
  if (|s| <= 1) {

  } else {
    if (s[0] == s[1]) {
      calc == {
        removeReps(s+[v]); 
          == { assert (s+[v])[1..] == s[1..]+[v]; }
        removeReps(s[1..] + [v]);
          ==
        removeReps(s[1..]);
          ==
        removeReps(s); 
      }
    } else {
      calc == {
        removeReps(s+[v]); 
          == { assert (s+[v])[1..] == s[1..] + [ v ]; }
        [ s[0] ] + removeReps(s[1..]+[v]);
          == { removeRepsAppend2(s[1..], v); }
        [ s[0] ] + removeReps(s[1..]); 
          ==
        removeReps(s);
      }
    }
  }

}


method countDiff(arr : array<int>) returns (ret : int) 
    requires sortedForAll(arr[..])
    ensures ret == |removeReps(arr[..])|
{
    if (arr.Length <= 1) {
        ret := arr.Length;
        return; 
    }
    ret := 1;
    var cur := 1;
    while (cur < arr.Length)
    // Invariant 
    invariant 1 <= cur <= arr.Length
    invariant ret == |removeReps(arr[..cur])|
    decreases arr.Length - cur
    {

        assert arr[..cur] + [ arr[cur] ] == arr[..(cur+1)];
        if (arr[cur-1]  != arr[cur]){
            ret := ret + 1;
            // Lemma application 1
            
            removeRepsAppend(arr[..cur], arr[cur]);
           
            // Assert 1
            assert ret == |removeReps(arr[..cur+1])|;
        } else {
            // Assertion 2
            removeRepsAppend2(arr[..cur], arr[cur]);
            assert ret == |removeReps(arr[..cur+1])|;
        }
        cur := cur +1; 
    }
    assert arr[..] == arr[..arr.Length];

}

/*
  * Ex 2
 */

function filterF(s : seq<int>) : seq<int> {
  if(s == [])
    then s 
    else 
      if s[0] < 0 
        then filterF(s[1..])
        else [ s[0] ] + filterF(s[1..])
}

lemma filterFProp1(s : seq<int>, v : int) 
  requires v >= 0 
  ensures filterF(s + [v]) == filterF(s) + [ v ]
{
  if(s == []) {

  } else {
    assert (s + [v])[1..] == s[1..] + [ v ];
  }
}

lemma filterFProp2(s : seq<int>, v : int) 
  requires v < 0 
  ensures filterF(s + [v]) == filterF(s) 
{
  if(s == []) {

  } else {
    assert (s + [v])[1..] == s[1..] + [ v ];
  }
}

method countNonNegative (arr : array <int >) returns (i : int) 
  ensures i == |filterF(arr[..])|
{
  i := 0; 
  var cur := 0; 
  while (cur < arr.Length)
    invariant 0 <= cur <= arr.Length
    invariant i == |filterF(arr[..cur])|
  {
    if (arr[cur] >= 0) {
      i := i+1;
      filterFProp1(arr[..cur], arr[cur]);
    } else {
      filterFProp2(arr[..cur], arr[cur]);
    }
    assert arr[..(cur+1)] == arr[..cur] + [ arr[cur] ];

    cur := cur + 1; 
  }
  assert arr[..arr.Length] == arr[..];
}


/*
  * Ex 3
 */

 method findMax(arr : array<int>) returns (index : int) 
    requires arr.Length > 0
    ensures 0 <= index < arr.Length
    ensures forall i :: 0 <= i < arr.Length ==> arr[i] <= arr[index] 
{
    var curMax := arr[0]; 
    index := 0;
    var cur := 1;
    while (cur < arr.Length) 
        invariant 0 <= index < cur <= arr.Length 
        invariant forall i :: 0 <= i < cur ==> arr[i] <= arr[index]
        invariant curMax == arr[index]
        decreases arr.Length - cur
    {
       if (arr[cur] > curMax) { 
            curMax := arr[cur]; 
            index := cur;
        }
        cur := cur + 1; 
    }
}