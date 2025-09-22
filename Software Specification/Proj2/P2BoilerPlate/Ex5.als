sig PVNode {
   var nextN : lone PVNode,
   data : one Int,
   pri : one Int,
}

sig PQueue {
   var count : one Int,
   var priHead : one Int,
   var priLast : one Int,
   var lastN : lone PVNode,
   var firstN : lone PVNode
}


/*enum Liveness {
   OnQueue,
   OffQueue
}*/


pred InsertPVPairBegining[p1:PVNode, p2:PVNode]{
--Pre-conditions
   --Pre1 - p2 cannot be in p1 
   historically p2 not in p1.*nextN
   --Pre2 - p2.pri >= p1.pri
   always p2.pri >= p1.pri
   --Pre3 - p2 is a single node
   historically no p2.nextN

--Post-conditions
   --Post1 - p2 is now a new list with p1 as its next
   p2.nextN' = p1

--Frame-conditions
   --Frame 1 -- nextN only changes for p2
   nextN' = nextN + (p2 -> p1)

}

pred InsertPVPairMiddle[p1:PVNode, p2:PVNode]{
--Pre-conditions
   --Pre1 - p2 cannot be in p1 
   historically p2 not in p1.*nextN
   --Pre2 - p2.pri < p1.pri and p2.pri > last node of p1
   always p2.pri < p1.pri && p2.pri > (p1.^nextN & (PVNode - nextN.PVNode)).pri  
   --Pre3 - p2 is a single node
   historically no p2.nextN
   --Pre4 - #p1 >= 2
   historically #(p1.^nextN) > 1

--Post-conditions
   --Post1 - p2 is now in the transitive closure of p1 and in the correct position in the list
   some p3,p4 : PVNode | p3!=p4 && p2!=p3 && p1!=p2 && 
      p2 !in p1.^nextN && p3 in p1.^nextN && p4 in p1.^nextN && 
         p3.pri > p2.pri && p4.pri <= p2.pri && p3.nextN = p4
            implies p3.nextN' = p2 && p2.nextN' = p4


--Frame-conditions
   --Frame 1 -- nextN only changes for the node that is added and the nodes before and after
   some p3,p4 : PVNode |p2!=p3 && p3!=p4 && p3.nextN = p2 && p2.nextN = p4 
      implies nextN' = nextN - (p3->p4) + (p3->p2) + (p2->p4)
   
}

pred InsertPVPairEnd[p1:PVNode, p2:PVNode]{
--Pre-conditions
   --Pre1 - p2 cannot be in p1 
   historically p2 not in p1.*nextN
   --Pre2 - p2.pri < p1.pri and p2.pri > last node of p1
   always p2.pri <= (p1.^nextN & (PVNode - nextN.PVNode)).pri  
   --Pre3 - p2 is a single node
   historically no p2.nextN

--Post-conditions
   --Post1 - p2 is now the last element of the list
   (p1.^nextN & (PVNode - nextN.PVNode)).nextN' = p2


--Frame-conditions
   --Frame 1 -- nextN only changes for the last node
   nextN' = nextN + ((p1.^nextN & (PVNode - nextN.PVNode)) -> p2)
}


pred enqueueBegining[q:PQueue, p:PVNode]{
--Pre-conditions
   --Pre1 - p cannot be in q already 
   historically p not in q.firstN.*nextN
   --Pre2 - p is a single node
   historically no p.nextN
   --Pre3 - p will be the first element of the queue
   always p.pri >= (q.firstN).pri

--Post-conditions
   --Post1 - p is inserted in the begining of the queue
   InsertPVPairBegining[q.firstN,p]
   --Post2 - p is the new head of the queue
   q.firstN' = p
   --Post3 - p.pri is the new pri of the priHead
   q.priHead' = p.pri

--Frame-conditions
   --Frame1 - first only changes for q->first
   firstN' = firstN - (q->q.firstN) + (q->p)
   --Frame2 - q.lastN doesn't change
   q.lastN' = q.lastN
   --Frame3 - q.priLast doesn't change
   q.priLast' = q.priLast

   --Frame4 - all the other queues stay the same
   all q1 : PQueue | q1 != q implies q1.count' = q1.count
   all q1 : PQueue | q1 != q implies q1.firstN' = q1.firstN
   all q1 : PQueue | q1 != q implies q1.lastN' = q1.lastN
   all q1 : PQueue | q1 != q implies q1.priHead' = q1.priHead
   all q1 : PQueue | q1 != q implies q1.priLast' = q1.priLast

}


pred enqueueMiddle[q:PQueue, p:PVNode]{
--Pre-conditions
   --Pre1 - p cannot be in q already 
   historically p not in q.firstN.*nextN
   --Pre2 - p is a single node
   historically no p.nextN
   --Pre3 - p.pri must be in between of the first and the last nodes
   always p.pri < (q.firstN).pri
   always p.pri > (q.lastN).pri

--Post-conditions
   --Post1 - p is inserted in the middle of q.firstN
   InsertPVPairMiddle[q.firstN,p]
   
--Frame-conditions
   --Frame1 - q.firstN doesn't change
   q.firstN' = q.firstN
   --Frame2 - q.priHead doesn't change
   q.priHead' = q.priHead
   --Frame3 - q.lastN doesn't change
   q.lastN' = q.lastN
   --Frame4 - q.priLast doesn't change
   q.priLast' = q.priLast
   --Frame5 - all the other queues stay the same
   all q1 : PQueue | q1 != q implies q1.count' = q1.count
   all q1 : PQueue | q1 != q implies q1.firstN' = q1.firstN
   all q1 : PQueue | q1 != q implies q1.lastN' = q1.lastN
   all q1 : PQueue | q1 != q implies q1.priHead' = q1.priHead
   all q1 : PQueue | q1 != q implies q1.priLast' = q1.priLast
}

pred enqueueLast[q:PQueue, p:PVNode]{
--Pre-conditions
   --Pre1 - p cannot be in q already 
   historically p not in q.firstN.*nextN
   --Pre2 - p is a single node
   historically no p.nextN
   --Pre3 - p.pri smaller or equal than the last element of the queue
   always p.pri <= (q.lastN).pri

--Post-conditions
   --Post1 - p is added to end the queue
   InsertPVPairEnd[q.firstN,p]
   --Post2 - q.lastN is now p
   q.lastN' = p
   --Post3 - q.priLast is now p.pri
   q.priLast' = p.pri

--Frame-conditions
   --Frame1 - q.firstN doesn't change   
   q.firstN' = q.firstN
   --Frame2 - q.priHead doesn't change
   q.priHead' = q.priHead
   --Frame3 - all the other queues stay the same
   all q1 : PQueue | q1 != q implies q1.count' = q1.count
   all q1 : PQueue | q1 != q implies q1.firstN' = q1.firstN
   all q1 : PQueue | q1 != q implies q1.lastN' = q1.lastN
   all q1 : PQueue | q1 != q implies q1.priHead' = q1.priHead
   all q1 : PQueue | q1 != q implies q1.priLast' = q1.priLast
}


pred enqueue[q:PQueue , p:PVNode]{
   enqueueBegining[q,p]
   or 
   enqueueMiddle[q,p]
   or 
   enqueueLast[q,p]
}

pred dequeue[q:PQueue]{
--Pre-conditions
   --Pre1 - q cannot be empty
   historically #q.firstN = 1

--Post-conditions
   --Post1 - the new first is its nextN
   q.firstN' = q.firstN.nextN
   --Post2 - the new priHead is the pri of its nextN
   q.priHead' = q.firstN.nextN.pri

--Frame-conditions
   --Frame1 - q.lastN doesn't change   
   q.lastN' = q.lastN
   --Frame2 - q.priLast doesn't change
   q.priLast' = q.priLast
   --Frame3 - all the other queues stay the same
   all q1 : PQueue | q1 != q implies q1.count' = q1.count
   all q1 : PQueue | q1 != q implies q1.firstN' = q1.firstN
   all q1 : PQueue | q1 != q implies q1.lastN' = q1.lastN
   all q1 : PQueue | q1 != q implies q1.priHead' = q1.priHead
   all q1 : PQueue | q1 != q implies q1.priLast' = q1.priLast

}

fact init {
   #PQueue = 1
   #PVNode = 3
   all q:PQueue | q.count = 2
}

pred stutter{
   nextN' = nextN
   count' = count
   priHead' = priHead
   priLast' = priLast
   lastN' = lastN
   firstN' = firstN
}

pred transition[]{
   (some q:PQueue, n:PVNode | enqueue[q,n])
   or 
   (some q:PQueue | dequeue[q])
   or
   stutter[]
}

pred final[]{
   some q:PQueue | q.count >= 3
}


pred System[]{
   always transition[]
   and 
   eventually final[]
}


fact F1 { //The next node of a last node of a list must be null
   always no (PQueue.lastN.^nextN)     
}

fact F2 { // The priority values of the nodes of a list must be ordered
   always (all p1,p2:PVNode | 
      p1 != p2 && p2 in p1.nextN 
         implies p2.pri <= p1.pri)
}

fact F3 { //Priority is a non negative integer
   always all p: PVNode | p.pri >=0
}

fact F4 { //first must point to the first element of the list
          //last must point to the last element of the list
          //priHead must hold the value of the priority of the first node of the list
          //priLast must hold the value of the priority of the last node of the list
   always ( all q : PQueue | q.priHead = q.firstN.pri && q.priLast = q.lastN.pri && q.count = #(q.firstN.^nextN + 1))
}

fact F5 { //A list must belong to a single queue
   always (all q1,q2 : PQueue | q1 != q2 implies q1.firstN != q2.firstN and q1.lastN != q2.lastN)
}

/*fact F6 { // All nodes must belong to a queue
   always PVNode = PQueue.first.*nextN
}*/

fact F7 { // The last must point to the last element of the list
   always (all q : PQueue | q.lastN in q.firstN.^nextN )
}

fact Aux1 { // just to generate different values
   always (all p1,p2:PVNode | p1!=p2 implies p1.pri != p2.pri && p1.data != p2.data)
}


run {System[]} for 4 but 3 steps