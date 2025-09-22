#include "../Ex3.h"
#include "klee/klee.h"
#include <assert.h>



void test31(){

    int pri1=0, pri2=128, data1=0, data2=0;
    int pri3=16484, data3=0;

    Queue q = makeQueue();
    assert(validQueue(q));

    enqueue(q,pri1,data1);
    assert(validQueue(q));

    enqueue(q,pri2,data2);
    assert(validQueue(q));

    enqueue(q,pri3,data3);
    assert(validQueue(q));
  
}

void test32(){

    int pri1=0, pri2=0, data1=0, data2=0;
    int pri3=2147483647, data3=0;

    Queue q = makeQueue();
    assert(validQueue(q));

    enqueue(q,pri1,data1);
    assert(validQueue(q));

    enqueue(q,pri2,data2);
    assert(validQueue(q));

    enqueue(q,pri3,data3);
    assert(validQueue(q));
  
}

void test33(){

    int pri1=0, pri2=2147483647, data1=0, data2=0;
    int pri3=0, data3=0;

    Queue q = makeQueue();
    assert(validQueue(q));

    enqueue(q,pri1,data1);
    assert(validQueue(q));

    enqueue(q,pri2,data2);
    assert(validQueue(q));

    enqueue(q,pri3,data3);
    assert(validQueue(q));
  
}

void test34(){

    int pri1=0, pri2=16384, data1=0, data2=0;
    int pri3=128, data3=0;

    Queue q = makeQueue();
    assert(validQueue(q));

    enqueue(q,pri1,data1);
    assert(validQueue(q));

    enqueue(q,pri2,data2);
    assert(validQueue(q));

    enqueue(q,pri3,data3);
    assert(validQueue(q));
  
}

void test35(){

    int pri1=2147483647, pri2=0, data1=0, data2=0;
    int pri3=0, data3=0;

    Queue q = makeQueue();
    assert(validQueue(q));

    enqueue(q,pri1,data1);
    assert(validQueue(q));

    enqueue(q,pri2,data2);
    assert(validQueue(q));

    enqueue(q,pri3,data3);
    assert(validQueue(q));
  
}

void test36(){

    int pri1=16777216, pri2=2, data1=0, data2=0;
    int pri3=16777216, data3=0;

    Queue q = makeQueue();
    assert(validQueue(q));

    enqueue(q,pri1,data1);
    assert(validQueue(q));

    enqueue(q,pri2,data2);
    assert(validQueue(q));

    enqueue(q,pri3,data3);
    assert(validQueue(q));
  
}
