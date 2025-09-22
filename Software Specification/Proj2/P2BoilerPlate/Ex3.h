

#ifndef EX3
#define EX3

typedef struct NodeS {
	int data; 
	int pri; 
	struct NodeS* next; 
}* Node; 


Node makeNode(int pri, int v);
Node insertPVPair(Node n1, Node n2);
Node removeNode(Node n1, Node n2);
int validPVList(Node n);

typedef struct QueueS {
	Node fst; 
	Node last; 

	int count; 
	int priHead; 
	int priLast; 

}* Queue; 

Queue makeQueue();
void enqueue (Queue q, int pri, int v);
Node dequeue (Queue q);
int validQueue(Queue q); 

#endif