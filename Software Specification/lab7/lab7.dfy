datatype tree = Leaf(int) | LNode(int,tree) 
                | RNode(int,tree) | Node(int,tree,tree)

function isBST(t:tree) : bool {
    match (t) {
        case Leaf(i) => true
        case LNode(i,tl) => forall k:: k in treeValues(tl) ==> k < i && isBST(tl)
        case RNode(i,tr) => forall k:: k in treeValues(tr) ==> k > i && isBST(tr)
        case Node(i, tl ,tr) => forall k:: k in treeValues(tl) ==> k < i && isBST(tl) && 
                                    forall k:: k in treeValues(tr) ==> k > i && isBST(tr)
    }
}


function treeValues(t : tree) : set<int> {
    match (t) {
        case Leaf(i) => {i}
        case LNode(i,tl) => {i} +  treeValues(tl)
        case RNode(i,tr) => {i} + treeValues(tr)
        case Node(i,tl,tr) => {i} + treeValues(tl) + treeValues(tr)  
        }
}


function BSTInsert(t: tree, i: int) : tree {
    match (t) {
        case Leaf(k) => if(k == i) then t
            else if(k > i) 
                then LNode(k,Leaf(i))
                else RNode(i, Leaf(k))
        case LNode(k,tl) => 
            if(i == k)
                then t
                else if(i>k)
                    then Node(k,tl,Leaf(i))
                    else LNode(k,BSTInsert(tl,i))
        case RNode(k,tr) =>
            if(i == k)
                then t
                else if(i>k)
                    then RNode(k,BSTInsert(tr,i))
                    else Node(k,Leaf(i),tr)
        case Node(k,tl,tr) => 
            if(i == k) 
                then t
                else if (i<k)
                    then Node(k, BSTInsert(tl,i),tr)
                    else Node(k,tl,BSTInsert(tr,i))
    }
}

function BSTFind(t: tree, i:int) : bool {
    match(t) {
        case Leaf(k) => k == i
        case LNode(k,tl) => k == i || (i<k && BSTFind(tl,i))
        case RNode(k,tr) => k == i || (i>k && BSTFind(tr,i))
        case Node(k,tl,tr) => k == i || (i<k && BSTFind(tl,i)) || (i>k && BSTFind(tr,i))
    }
}

lemma BSTFindProp(t: tree, i:int) 
    requires isBST(t)
    ensures BSTFind(t,i) == (i in treeValues(t))
{
    match(t) {
        case Leaf(k) => 
            calc == {
                BSTFind(t,i);
                ==
                k == i;
                ==
                i in {k};
                ==
                i in treeValues(t);
        }
        case Node(k,tl,tr) =>
            if(k==i){
                calc == {
                    BSTFind(t,i);
                    ==
                    true;
                    ==
                    i in treeValues(t);
                }
            } else if (i < k) {
                calc == {
                    BSTFind(t,i);
                    ==
                    BSTFind(tl,i);
                    == //{BSTFindProp(tl,i);}
                    i in treeValues(tl);
                    ==
                    i in treeValues(t);
                }
            } else if( i > k) {
                calc == {
                    BSTFind(t,i);
                    ==
                    BSTFind(tr,i);
                    ==
                    i in treeValues(tr);
                    ==
                    i in treeValues(t);
                }
            }
        case RNode(k,tr) =>

        case LNode(k,lt) =>
    }
}