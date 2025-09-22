function SumSeqF(s : seq<int>) : int { //Ex1
    if ( s == [])
        then 0
        else s[0] + SumSeqF(s[1..])
}

lemma SumSeqDistProp (s1 : seq<int>, s2 : seq<int>)  //Ex1
    ensures SumSeqF(s1 + s2) == SumSeqF(s1) + SumSeqF(s2)
{
    if (s1 == []){
        calc == {
            SumSeqF(s1 + s2);
            == {assert [] + s2 == s2;}
            SumSeqF(s2);
            ==
            SumSeqF(s1) + SumSeqF(s2);
        }
    } else {
        calc == {
            SumSeqF(s1 + s2);
            == {assert (s1 + s2)[1..] == s1[1..] + s2;}
            s1[0] + SumSeqF(s1[1..]+s2);
            == {SumSeqDistProp(s1[1..],s2);}
            SumSeqF(s1) + SumSeqF(s2);
        }
    }
}


method SumSeqIter(s : seq<int>) returns (r: int)
    ensures r == SumSeqF(s)
{
    r:=0;
    var idx:=0;
    while(idx < |s|) invariant idx >= 0 && idx <= |s|
                    invariant r == SumSeqF(s[..idx])
    {
    calc {
        SumSeqF(s[..(idx + 1)]);
        == { assert s[.. (idx + 1)] == s[..idx] + [s[idx]];}
        SumSeqF(s[..idx] + [s[idx]]);
        == { SumSeqDistProp(s[..idx], [s[idx]]);}
        SumSeqF(s[..idx]) + s[idx];
        == r + s[idx];
    } 

        r:= r + s[idx];
        idx := idx + 1;
    } {assert s[..|s|] == s;}
    return;
}


function CountPositiveF (s : seq<int>) : int {
    if s == []
        then 0 
        else if (s[0] >= 0) 
            then 1 + CountPositiveF(s[1..]) 
        else CountPositiveF(s[1..])
}

lemma CountPositiveDistribProp (s1 : seq<int> , s2 : seq<int> )
    ensures CountPositiveF(s1 + s2) == CountPositiveF(s1) + CountPositiveF(s2)
{
    if(s1 == []){
        calc == {
            CountPositiveF(s1 + s2);
            == {assert [] + s2 == s2;}
            CountPositiveF(s2);
            == 
            CountPositiveF(s1) + CountPositiveF(s2);
        }
    } else if (s1[0] >= 0){
        calc == {
            CountPositiveF(s1 + s2);
            == {assert (s1+s2)[1..] == s1[1..] + s2;}
            1 + CountPositiveF(s1[1..] + s2);
            == { CountPositiveDistribProp(s1[1..],s2);}
            1 + CountPositiveF(s1[1..]) + CountPositiveF(s2);
            ==
            CountPositiveF(s1) + CountPositiveF(s2);
         }
    } else {
        calc == {
            CountPositiveF(s1 + s2);
            == {assert (s1+s2)[1..] == s1[1..] + s2;}
            CountPositiveF(s1[1..] + s2);
            == { CountPositiveDistribProp(s1[1..],s2);}
            CountPositiveF(s1[1..]) + CountPositiveF(s2);
            ==
            CountPositiveF(s1) + CountPositiveF(s2);
         }
    }

    
}

method CountPositiveIter (s : seq<int>) returns (r:int)
    ensures r == CountPositiveF(s)
{
    r := 0;
    var idx:= 0;

    while(idx < |s|)    invariant idx >= 0 && idx <= |s|
                        invariant r == CountPositiveF(s[..idx])
    {
        if(s[idx] >= 0){
            calc == {
                CountPositiveF(s[..(idx+1)]);
                == {assert s[..(idx+1)] == s[..idx] + [s[idx]];}
                CountPositiveF(s[..idx] + [s[idx]]);
                == {CountPositiveDistribProp(s[..idx],[s[idx]]);}
                CountPositiveF(s[..idx]) + 1;
                == r + 1;
            }
            r := r + 1;
            idx := idx + 1;
        } else {
            calc == {
                CountPositiveF(s[..(idx+1)]);
                == {assert s[..(idx+1)] == s[..idx] + [s[idx]];}
                CountPositiveF(s[..idx] + [s[idx]]);
                == {CountPositiveDistribProp(s[..idx],[s[idx]]);}
                CountPositiveF(s[..idx]);
                == r;
            }
            idx := idx + 1;
        }
    } {assert s[..|s|] == s;}
    return;
}