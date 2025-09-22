function sorted ( s : seq < int >) : bool {
    if (| s | < 2)  
        then true
        else s [0] ≤ s [1] ∧ sorted ( s [1..])
}


function ssorted ( s : seq < int >) : bool {
    if (| s | < 2)
        then true
        else s [0] < s [1] ∧ sorted ( s [1..])
}


function removeReps ( s : seq < int >) : seq < int > {
    if (| s | < 2)
        then s
        else if ( s [0] == s [1])
            then removeReps ( s [1..])
            else [ s [0] ] + removeReps ( s [1..])
}




method countDiff(arr : array<int>) returns (ret : int)
    requires sorted(arr[..])
    ensures ret == |removeReps(arr[..])|
{
    if(arr.Length <= 1){
        ret := arr.Length;
        return;
    }
    ret := 1;
    var cur := 1;
    while (cur != arr.Length)        decreases arr.Length-cur
                                    invariant 1 <= cur <= arr.Length
                                    invariant ret == |removeReps(arr[..cur])|


    {
        if (arr[cur-1] != arr[cur]){
            assert ret + 1 == |removeReps(arr[..(cur+1)])|;
            ret := ret + 1;
        } else {
            assert ret == |removeReps(arr[..(cur+1)])|;
        }
        cur := cur + 1;
    }
}










function filterF(s : seq<int> ) : seq<int> 
{
    if(s == []) 
        then s
        else if (s[0]<0) 
            then filterF(s[1..])
            else [s[0]] + filterF(s[1..])
}



method countNonNegative (arr : array<int>) returns (i : int)
    requires arr.Length > 0
    ensures i == |filterF(arr[..])|

{
    i:=0; var cur:= 0;
    while (cur < arr.Length)        decreases arr.Length - cur      
                                    invariant 0 <= cur <= arr.Length
                                    invariant i == |filterF(arr[..cur])|

    {
        if(arr[cur] >= 0){
            assert i+1 == |filterF(arr[..(cur+1)])|;
            i := i+1;
        } else {
            assert i == |filterF(arr[..(cur+1)])|;
        }
        cur := cur + 1;
    }
}


method findMax(arr : array<int>) returns (index : int)
    requires arr.Length >= 1
    ensures 0 <= index < arr.Length
    ensures forall k :: 0 <= k < arr.Length ==> arr[index] >= arr[k]

{
    var curMax := arr[0];
    index := 0;
    var cur := 1;

    while (cur < arr.Length)    decreases arr.Length - cur
                                invariant 0 <= index < cur <= arr.Length
                                invariant forall k :: 0 <= k < cur ==> arr[k] <= arr[index]
                                invariant curMax == arr[index]

    {
        if (arr[cur] > curMax) {
            curMax := arr[cur];
            index := cur;
        }
        cur := cur + 1;
    }
}