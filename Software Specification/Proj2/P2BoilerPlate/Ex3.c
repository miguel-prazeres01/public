#include "Ex3.h"
#include<stdlib.h>
#include<stdio.h>
#include<assert.h>

Node makeNode(int pri, int v) {
	Node n = (Node)malloc(sizeof(struct NodeS)); 
	n->pri = pri; 
	n->data = v; 
	n->next = NULL;
	return n; 
}

Node insertPVPair(Node n1, Node n2) { 
	if ((n1 == NULL) || (n1->pri < n2->pri)) {
		n2->next = n1;
		return n2; 
	} else {
		Node prev = n1; 
		Node cur = n1->next; 
		
		while (cur != NULL && cur->pri >= n2->pri) {
			prev = cur; 
			cur = cur->next; 
		}
		
		prev->next = n2; 
		n2->next = cur; 
		return n1; 
	}
}

Node removeNode(Node n1, Node n2) {
	if (n1 == n2) {
		return n1->next;
	} else {
		if (n1->next == NULL) {
			return n1; 
		} else {
			Node aux = removeNode(n1->next, n2);
			n1->next = aux; 
			return n1; 
		}
	}
}

int validPVList(Node n){
	if (n->next == NULL){
		if (n->pri < 0) return 0;
	} else {
		Node prev = n;
		Node cur = n->next;

		while (cur != NULL){
			if(cur->pri < 0){
				return 0;
			}

			if(prev->pri < cur->pri){
				return 0;
			}
			prev = cur;
			cur = cur->next;
		}

		if(prev == n){
			return 0;
		}

		return 1;
	}
	
}


Queue makeQueue() {
	Queue q = (Queue)malloc(sizeof(struct QueueS));
	q->fst = NULL;
	q->last = NULL;
	q->count = 0;
	q->priHead = -1; 
	q->priLast = -1; 
	return q; 
}

void enqueue (Queue q, int pri, int v) {
	Node n = makeNode(pri, v);
	if (q->count == 0) {
		q->fst = n; 
		q->last = n;
		q->priHead = pri;
		q->priLast = pri;
		q->count++; 
	} else if (q->priHead < pri) {
		n->next = q->fst;
		q->fst = n; 
		q->count++; 
		q->priHead = pri; 
	} else if (q->priLast >= pri) {
		q->last->next = n; 
		q->last = n; 
		q->count++; 
		q->priLast = pri;  
	} else {
		insertPVPair(q->fst, n);
		q->count++; 
	}
}

Node dequeue (Queue q) {
	if (q->count == 0) {
		return NULL;
	} else {
		Node ret = q->fst;
		if (q->count == 1) {
			q->fst = NULL; 
			q->last = NULL;
			q->count = 0; 
			q->priHead = -1; 
			q->priLast = -1; 
		} else {
			q->fst = q->fst->next; 
			q->count--; 
			q->priHead = q->fst->pri; 
		}
		return ret; 
	}
}


int validQueue(Queue q){
	Node cur = q->fst;
	if(cur == NULL){
		if(q->count != 0 || q->last!=NULL || q->priHead != -1 || q->priLast != -1){
			return 1;
		}
	} else {
		if (validPVList(q->fst) == 1){
			return 1;
		}

		Node cur = q->fst;
		int count = 1;
		while (cur->next != NULL){
			cur = cur->next;
			count++;
		}
		if(q->last!= cur || q->count != count || q->priHead != q->fst->pri || q->priLast != cur->pri){
			return 0;
		}

		return 1;
	}
}