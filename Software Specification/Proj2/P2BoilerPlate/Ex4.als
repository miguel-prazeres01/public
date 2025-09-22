sig PVNode {
   next : lone PVNode,
   data : one Int,
   pri : one Int
}

sig PQueue {
   count : one Int,
   priHead : one Int,
   priLast : one Int,
   last : lone PVNode,
   first : lone PVNode
}

fact F1 { //The next node of a last node of a list must be null
   no (PQueue.last.^next)     
}

fact F2 { // The priority values of the nodes of a list must be ordered
   all p1,p2:PVNode | 
      p1 != p2 && p2 in p1.next 
         implies p2.pri <= p1.pri
}

fact F3 { //Priority is a non negative integer
   all p: PVNode | p.pri >=0
}

fact F4 { //first must point to the first element of the list
          //last must point to the last element of the list
          //priHead must hold the value of the priority of the first node of the list
          //priLast must hold the value of the priority of the last node of the list
   all q : PQueue | q.priHead = q.first.pri && q.priLast = q.last.pri && q.count = #(q.first.^next + 1)
}

fact F5 { //A list must belong to a single queue
   all q1,q2 : PQueue | q1 != q2 implies q1.first != q2.first and q1.last != q2.last
}

fact F6 { // All nodes must belong to a queue
   PVNode = PQueue.first.*next
}

fact F7 { // The last must point to the last element of the list
   all q : PQueue | q.last in q.first.^next 
}

fact Aux { // just to generate different values
   all p1,p2:PVNode | p1!=p2 implies p1.pri != p2.pri && p1.data != p2.data 
}


run {#PQueue = 2 and #PVNode >=6} for 14