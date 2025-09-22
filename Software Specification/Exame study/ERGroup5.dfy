datatype Tree = Leaf(int) | Node(Tree, Tree)


function leaves(t : Tree) : set<int> {
  match t {
    case Leaf(v) => { v }
    case Node(t1, t2) => leaves(t1) + leaves(t2)
  }
}

function makePositive(t : Tree) : Tree {
  match t {
    case Leaf(v) => 
      if (v >= 0) 
        then Leaf(v)
        else Leaf(0)

    case Node(t1, t2) => Node(makePositive(t1), makePositive(t2))
  }
}


lemma makePositiveProp(t : Tree) 
  ensures forall k :: k in leaves(makePositive(t)) ==> k >= 0 
{

}

class TreeNode {
  var left : TreeNode?; 
  var right : TreeNode?; 
  var val : int; 

  ghost var footprint : set<TreeNode>; 
  ghost var tree : Tree; 

  constructor(v : int) 
    ensures this.val == v && this.left == null && this.right == null
    ensures Valid()
  {
    this.left := null; 
    this.right := null; 
    this.val := v; 
    this.footprint := { this }; 
    this.tree := Leaf(v);
  }

  function Valid() : bool 
    reads this, footprint
  {
    this in footprint 
    && 
    (this.left == null <==> this.right == null)
    && 
    if this.left == null 
      then (
        footprint == { this } 
        && 
        tree == Leaf(this.val)
      ) else (
        this.left in footprint 
        && 
        this.right in footprint 
        && 
        footprint == { this } + this.left.footprint + this.right.footprint  
        && 
        this.left.footprint !! this.right.footprint 
        && 
        this !in this.left.footprint 
        && 
        this !in this.right.footprint 
        && 
        this.left.Valid() 
        &&
        this.right.Valid()
        &&
        this.tree == Node(this.left.tree, this.right.tree)
      )
  }

  method copyTree() returns (r : TreeNode) 
    requires Valid()
    ensures r.Valid()
    ensures fresh(r.footprint)
    decreases footprint
  {
    if (this.left == null) {
      assert this.right == null; 
      r := new TreeNode(this.val);
      return; 
    } else {
      assert this.right != null; 
      r := new TreeNode(this.val); 
      var t1 := this.left.copyTree();
      var t2 := this.right.copyTree();
      r.left := t1; 
      r.right := t2; 
      r.footprint := { r } + t1.footprint + t2.footprint; 
      r.tree := Node(t1.tree, t2.tree);
      return;
    }
  }

  method makePositiveM() 
    requires Valid()
    ensures Valid()
    ensures tree == makePositive(old(tree))
    ensures footprint == old(footprint)
    decreases footprint
    modifies footprint
  {
    if (this.left == null) {
      if (this.val < 0) {
        this.val := 0;
        this.tree := Leaf(0);
      }
    } else {
      this.left.makePositiveM(); 
      this.right.makePositiveM();
      this.tree := Node(this.left.tree, this.right.tree); 
    }
  }

}