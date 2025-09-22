abstract sig Person {
    siblings : set Person,
    mother : lone Person,
    father : lone Person,
    spouse : lone Person
}

sig Man extends Person {}

sig Woman extends Person{}

fact C7 {
    
    siblings = (father + mother) . (~(father + mother)) - iden
    
    /*all p1:Person, p2:Person | p1 != p2 implies
        (p1.father = p2.father or p1.mother = p2.mother 
        iff p1 in p2.siblings and p2 in p1.siblings)

    all p:Person | p !in p.siblings

    all p1:Person, p2:Person |
        p1 in p2.siblings implies p2 in p1.siblings*/
}

fact O1 {
    //all p:Person | p !in p.^(father + mother)
    no (^(mother + father) & iden)
}

fact O2 {
    /*all p:Person |
        p.mother in Woman
    all p:Person |
        p.father in Man*/
    
    Person.mother in Woman
    Person.father in Man
}

fact C8 {
    spouse = ~spouse
    no (spouse & iden)
}

fact Aux {
    #Person >= 4
    #siblings >=2
    #spouse >=2
}

fun bloodRelatives [p1:Person] : set Person {
    {p2:Person | 
        some p:Person |
            p in p1.*(mother + father) 
            && 
            p in p2.*(mother + father)
    }
}

fact C9 {
    all p:Person | p.spouse != none implies
        p.spouse !in bloodRelatives[p]
}

fact C10 {
    all p:Person | p.father != none && p.mother != none implies
        p.father !in bloodRelatives[p.mother]
}

run {} for 5