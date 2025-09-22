//New ex


sig User {
    profs : set Profile
}

sig Profile {
    connections : set Profile,
    dBlocks : set Profile,
    iBlocks : set Profile
}

fact C3 {
    all p1,p2 : Profile , u : User | 
        p1 in u.profs and p2 in u.profs 
            implies p1 !in p2.connections

    //no (connections & (~profs).profs)
}


fact C4 {
    all p1,p2 : Profile |
        p1 in p2.connections 
            implies p2 in p1.connections

    //connections.(~connections) = iden
    //connections = ~connections 
}

fact C5 {
    /*all p : Profile | 
        some u : User |
            p in u.profs*/
    //Profile = User.profs

    /*all p: Profile , u1,u2: User | 
        p in u1.profs and u1 != u2 
            implies p !in u2.profs*/
    
    all p:Profile | #(p.(~profs)) = 1
    // TIP : p.(~profs) === profs.p
}

fact C7 {
    all p1,p2 : Profile, u: User | 
        p1 in u.profs and p2 in u.profs 
            implies p1 !in p2.dBlocks
    
    // no (dBlocks & (~profs).profs)
}

fact C8 {
    /*all p1,p2 : Profile | 
        p1 in p2.dBlocks 
            implies 
                p2 !in p1.dBlocks*/
    no (dBlocks & ~dBlocks)
}

fact C9 { // P2 is not in the connections of P1
    all p1,p2: Profile |
        p2 in p1.dBlocks
            implies p2 !in p1.connections
    // no (dBlocks & Connections)
}

fact C10 {
    /*all p1,p3 : Profile , u1,u2 : User | some p2 : Profile |
        (p1 in u1.profs and p2 in u2.profs and u1 != u2 and p2 in p1.dBlocks
        and p3 in u2.profs and p3!=p2 and p3 != p1) iff p3 in p1.iBlocks*/

    iBlocks = dBlocks.(~profs).profs - dBlocks
}

run {#User = 2 and #Profile = 2 and some dBlocks } for 7




