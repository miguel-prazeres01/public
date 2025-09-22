datatype Tree = Leaf(int) | Node(Tree,Tree)

function leaves(t : Tree) : seq<int> {
    match(t) {
        case Leaf(v) => [v]
        case Node(t1,t2) => leaves(t1) + leaves(t2)
    }
}

function makePositive(t : Tree) : Tree {
    match(t) {
        case Leaf(v) => if(v < 0) then Leaf(0)
                        else Leaf(v)
        case Node(t1,t2) => Node(makePositive(t1),makePositive(t2))
    }
}

lemma TreeProp(t : Tree)
    ensures forall k :: 0 <= k < |leaves(makePositive(t))| ==> leaves(makePositive(t))[k] >=0
{
    match(t){
        case Leaf(v)=> 
        case Node(t1,t2)=>
    }
}