#include "../Ex3.h"
#include "klee/klee.h"
#include <assert.h>


void test11(){
    int pri1=0, pri2=128 , data1=0, data2=0;
    int pri3=16384, data3=0;


    Node n1 = makeNode(pri1,data1);
    Node n2 = makeNode(pri2,data2);
    Node n3 = makeNode(pri3,data3);
    
    assert(validPVList(n1) && validPVList(n2) && validPVList(n3));

    n1 = insertPVPair(n1,n2);
    assert (validPVList(n1));

    n1 = insertPVPair(n1,n3);
    assert (validPVList(n1));

    n1 = removeNode(n1,n2);
    assert (validPVList(n1));

    n1 = removeNode(n1,n3);
    assert (validPVList(n1));     
}

void test12(){
    int pri1=0, pri2=16384 , data1=0, data2=0;
    int pri3=128, data3=0;


    Node n1 = makeNode(pri1,data1);
    Node n2 = makeNode(pri2,data2);
    Node n3 = makeNode(pri3,data3);
    
    assert(validPVList(n1) && validPVList(n2) && validPVList(n3));

    n1 = insertPVPair(n1,n2);
    assert (validPVList(n1));

    n1 = insertPVPair(n1,n3);
    assert (validPVList(n1));

    n1 = removeNode(n1,n2);
    assert (validPVList(n1));

    n1 = removeNode(n1,n3);
    assert (validPVList(n1)); 
}

void test13(){
    int pri1=0, pri2=0 , data1=0, data2=0;
    int pri3=2147483647, data3=0;


    Node n1 = makeNode(pri1,data1);
    Node n2 = makeNode(pri2,data2);
    Node n3 = makeNode(pri3,data3);
    
    assert(validPVList(n1) && validPVList(n2) && validPVList(n3));

    n1 = insertPVPair(n1,n2);
    assert (validPVList(n1));

    n1 = insertPVPair(n1,n3);
    assert (validPVList(n1));

    n1 = removeNode(n1,n2);
    assert (validPVList(n1));

    n1 = removeNode(n1,n3);
    assert (validPVList(n1)); 
}

void test14(){
    int pri1=0, pri2=2147483647 , data1=0, data2=0;
    int pri3=0, data3=0;


    Node n1 = makeNode(pri1,data1);
    Node n2 = makeNode(pri2,data2);
    Node n3 = makeNode(pri3,data3);
    
    assert(validPVList(n1) && validPVList(n2) && validPVList(n3));

    n1 = insertPVPair(n1,n2);
    assert (validPVList(n1));

    n1 = insertPVPair(n1,n3);
    assert (validPVList(n1));

    n1 = removeNode(n1,n2);
    assert (validPVList(n1));

    n1 = removeNode(n1,n3);
    assert (validPVList(n1)); 
}

void test15(){
    int pri1=2147483647, pri2=0 , data1=0, data2=0;
    int pri3=0, data3=0;


    Node n1 = makeNode(pri1,data1);
    Node n2 = makeNode(pri2,data2);
    Node n3 = makeNode(pri3,data3);
    
    assert(validPVList(n1) && validPVList(n2) && validPVList(n3));

    n1 = insertPVPair(n1,n2);
    assert (validPVList(n1));

    n1 = insertPVPair(n1,n3);
    assert (validPVList(n1));

    n1 = removeNode(n1,n2);
    assert (validPVList(n1));

    n1 = removeNode(n1,n3);
    assert (validPVList(n1)); 
}

void test16(){
    int pri1=16777216, pri2=2 , data1=0, data2=0;
    int pri3=16777216, data3=0;


    Node n1 = makeNode(pri1,data1);
    Node n2 = makeNode(pri2,data2);
    Node n3 = makeNode(pri3,data3);
    
    assert(validPVList(n1) && validPVList(n2) && validPVList(n3));

    n1 = insertPVPair(n1,n2);
    assert (validPVList(n1));

    n1 = insertPVPair(n1,n3);
    assert (validPVList(n1));

    n1 = removeNode(n1,n2);
    assert (validPVList(n1));

    n1 = removeNode(n1,n3);
    assert (validPVList(n1)); 
}
