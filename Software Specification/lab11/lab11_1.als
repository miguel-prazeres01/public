//Ex. 3

//1.
sig User {
	profs : set Profile //C1
}

sig Profile {
	connections : set Profile, //C2
	dBlocks : set Profile, //C6
	iBlocks : set Profile //C10
}

fact C3 {
	no ( (~profs) . profs & connections )
}

fact C4 {
	connections = ~connections
}

fact C5 {
	all p : Profile | #(p.(~profs)) = 1
}

fact C7 {
	no ( (~profs.profs) & dBlocks )  
}

fact C8 {
	no ( (~dBlocks) & dBlocks )
}

fact C9 {
	no (dBlocks & connections)
}

fact C10 {
	iBlocks = dBlocks . (~profs) . profs - dBlocks		
}


//2.

//F2
fun frenemies[u1 : User] : set User {
	{ u1.profs.connections.(~profs)
		&
	u1.profs.dBlocks.(~profs) }
}

//3. 

-- A1
assert a1 {
	all p1:Profile,p2:Profile | 
		not (p1 in p2.iBlocks && p2 in p1.iBlocks)
}

-- A2
assert a2 {
	all p:Profile | 
		p !in p.iBlocks
}

-- A3
assert a3 {
	all p1:Profile,p2:Profile | 
		p2 in p1.iBlocks implies p2 !in p1.connections
}


//run { } for 5 but exactly 4 Profile, exactly 2 User

run { #Profile >= 3 && #User >= 2 } for 4

// check a1 for 5 

// check a2 for 5 

// check a3 for 5 



