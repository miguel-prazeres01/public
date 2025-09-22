function absF (x : int) : int
{
    if(x >= 0)
        then x
        else -x
}

method abs(x : int) returns (r : int)
    ensures r == absF(x)
{
    if (x >= 0) {
        r := x;
    } else {
        r := -x;
    }
}

method testAbs() {
    var x := -3;
    var y := abs(x);

    assert y >= 0;

    assert y == 3;
}


method prod2(x : nat, y : nat) returns (r : nat) //ver invariantesss  -> tabela e procurar fórmula
    ensures r == x*y
{
    var aux := y;
    r := 0;
    while (aux > 0)
        invariant r == x*(y-aux)
    {
        r := r + x;
        aux := aux - 1; 
    }
}

function fibF(n : nat) : nat {
    if (n <=1 )
        then 1
        else fibF(n - 1) + fibF(n - 2)
}

method fib (n : nat ) returns (r : nat)
    ensures r == fibF(n)
{
    if( n <= 1 ){
        r:=1;
        return;
    }
    var prevprev:=1;
    var prev:=1;
    var curr:=1;
    
    while ( curr < n)
        invariant prev == fibF(curr) && prevprev == fibF(curr-1) && curr >=1 && curr <=n
    {
        var aux := prevprev;
        prevprev := prev;
        prev := aux + prev;
        curr := curr + 1;
    }
    r:= prev;
}

function factF(n : nat): nat {
    if( n <= 1)
        then 1
        else factF(n-1) * n
}

method fact (n : nat) returns (r : nat)
    ensures r == factF(n)
{
    r := 1;
    var aux := 1;
    while (aux <= n)
        invariant r == factF(aux - 1) && aux >= 1 && aux <= n + 1
    {
        r:= r * aux;
        aux := aux + 1;
    }
}

