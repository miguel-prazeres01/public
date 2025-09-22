#include "../Ex3.h"
#include "klee/klee.h"
#include <assert.h>


void test41(){
    int pri1=0, data1=0;
    int pri2=2147483647, data2=0;

    Queue q = makeQueue();
    assert(validQueue(q));

    enqueue(q,pri1,data1);
    assert(validQueue(q));

    enqueue(q,pri2,data2);
    assert(validQueue(q));

    dequeue(q);
    assert(validQueue(q));

    dequeue(q);
    assert(validQueue(q));

    dequeue(q);
    assert(validQueue(q));
}

void test42(){
    int pri1=2147483647, data1=0;
    int pri2=0, data2=0;

    Queue q = makeQueue();
    assert(validQueue(q));

    enqueue(q,pri1,data1);
    assert(validQueue(q));

    enqueue(q,pri2,data2);
    assert(validQueue(q));

    dequeue(q);
    assert(validQueue(q));

    dequeue(q);
    assert(validQueue(q));

    dequeue(q);
    assert(validQueue(q));
}

