method BinSearch(arr:array<int>, v:int) returns (ret:int)
    requires sorted(arr[..])
    ensures ret !=-1 ==> 0 <= ret < arr.Length && arr[ret]==v
    ensures ret == -1 ==>forall i::0<= i < arr.Length ==> arr[i] != v
{
    var l:=0;
    var r:= arr.Length - 1;

    while (l<=r) 
                 invariant 0 <= l <= arr.Length
                 invariant -1 <= r < arr.Length 
                 invariant (l <=r) ==> r >= 0 && l < arr.Length
                 invariant forall i :: 0 <= i < l ==> arr[i]<v
                 invariant forall i :: r < i < arr.Length ==> arr[i]>v
    {
        var m:int:=(l+r)/2;
        if(arr[m]==v){
            ret:=m;return;
        } else if (arr[m]<v){   
            l:=m+1;
        } else{                 
            r:= m-1;
        }
    }
    ret := -1;
    return;
}


function sorted (s : seq<int>) : bool{
    forall i,j :: 
        0 <= i < j < |s|
            ==> 
                s[i] <= s[j]
}


method arrCopy (arr:array<int>) returns (r:array<int>)
    ensures fresh(r)
    ensures arr[..] == r[..]    

{
    r:= new int[arr.Length];
    var idx := 0;

    while (idx < arr.Length)  decreases arr.Length - idx
                              invariant idx >= 0 && idx <= arr.Length
                              invariant r[..idx] == arr[..idx]
    
    {
        r[idx] := arr[idx];
        idx := idx + 1;
    }
    return;
}