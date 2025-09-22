method arrIncrement(arr : array<int>)
    ensures forall k :: 0 <= k < arr.Length ==> arr[k] == old(arr[k]) + 1
    modifies arr
{
    var idx := 0;
    ghost var old_arr := arr[..];
    while (idx < arr.Length)    invariant 0 <= idx <= arr.Length
                                invariant forall k :: 0 <= k < idx ==> arr[k]==old_arr[k]+1;
                                invariant forall k :: idx <= k < arr.Length ==> arr[k] == old_arr[k];
    {
        arr[idx] := arr[idx] + 1;
        idx := idx + 1;
    }
}

method swap (arr : array<int>, i: int, j: int)
    requires 0 <= i < arr.Length && 0 <= j < arr.Length
    ensures arr[i] == old(arr[j]) && arr[j] == old(arr[i])
    ensures forall k :: 0 <= k < arr.Length && k != j && k != i ==> arr[k] == old(arr[k])
    ensures multiset(arr[..]) == multiset(old(arr[..]))
    modifies arr
{

    var tmp := arr[i];
    arr[i] := arr[j];
    arr[j] := tmp;
}


method partition ( arr: array<int>) returns (v:int, i:int)
    requires arr.Length > 0
    ensures 0 <= i < arr.Length
    ensures forall k :: 0 <= k < i ==> arr[k] < v
    ensures forall k :: i < k <= arr.Length-1 ==> arr[k] >=v
    modifies arr
    ensures arr[i]==v
    ensures multiset(arr[..]) == multiset(old(arr[..]))

{
    v:= arr[arr.Length -1];
    i:=0; var j:=0;

    while (j < arr.Length-1) invariant 0 <= i <= j <= arr.Length-1
                             invariant forall k :: 0 <= k < i ==> arr[k] < v
                             invariant forall k :: i <= k < j ==> arr[k] >=v
                             invariant v == arr[arr.Length-1]
                             invariant multiset(arr[..]) == multiset(old(arr[..]))
    
    {
        if(arr[j] < v){
            swap(arr,i,j);
            i:= i+1;
        }
        j:=j+1;
    }
    swap(arr,i,j);
}