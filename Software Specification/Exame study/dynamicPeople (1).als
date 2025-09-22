/*
Temporal Operators

- always p - p holds from current state forward
- historically p - p holds from current state backward
- after p - p holds in the next state
- before p - p holds in the previous state
- eventually p - p holds in the current state or a later on
- once p - p holds in current state or an earlier one
- p until q - p holds continuously until q holds
- q since q - p has held continuously since last time q held

A state transformer is modeled as a predicate over two states: 
1. the state right before the transition (current state) and
2. the state right after it (next state)

Important: a constraint with no temporal operators 
   applies to the initial state of the system. 
*/



enum Liveness {
	Alive, 
	Dead, 
	Unborn
}

sig Person { 
	var spouse: lone Person,
	var children: set Person,  
	var liveness: Liveness
}



fun parents[] : Person->Person {
	~children
}

fun siblings[] : Person->Person {
	(~children).children - iden 
}

pred BloodRelatives[p: Person, q: Person] {
	some (p.*parents & q.*parents)
}

pred isAlive[p:Person] {
	p.liveness = Alive
}

pred isDead[p:Person] {
	p.liveness = Dead
}

pred isUnborn[p:Person] {
	p.liveness = Unborn
}

pred newBorn[p:Person] {
	isAlive[p] and before isUnborn[p]
}

pred isMarried[p:Person] {
	some p.spouse
}

-- F1 - People cannot be their own ancestors
fact F1 {
	always (no (^parents & iden))
}

-- F2 - No one can have more than two parents
fact F2 {
	always (all p:Person | #(p.parents) <= 2)  
}

-- F3 - Spouse relation is symmetric
fact F3 {
	always (spouse = ~spouse)  
}

-- F4 - A person cannot have children with a relative
fact F4 {
	always (
		all p1:Person,p2:Person,p:Person | 
			(p in p1.children and p in p2.children and p1 != p2) implies 
				not BloodRelatives[p1,p2]
	)
}

-- F5 - A spouse cannot be a relative 
fact F5 {
	always (
		all p1:Person,p2:Person | 
			p2 = p1.spouse implies not BloodRelatives[p1,p2]
	)
}

-- F6 - dead people stay dead
fact F6 {
	always (
		all p:Person | 
			isDead[p] implies (after isDead[p])
	)
}

-- F7 - dead people were once alive
fact F7 {
	always (
		all p:Person | 
			isDead[p] implies (once isAlive[p])
	)
}

-- F9 - Living people never become unborn
fact F9 {
	always (
		all p:Person | 
			isAlive[p] implies after (isAlive[p] or isDead[p])
	)
}

-- F11 - Newborns have a mother and have a father
fact F11 {
	always (
		all p:Person | 
			newBorn[p] implies 
				some p1,p2:Person | p.parents = p1+p2
	)
}

-- F12 - Children were born from previously alive parents
fact F12 {
	always (
		all p1, p2:Person | 
			newBorn[p1] and p2 in p1.parents implies once isAlive[p2] 
	)
}

-- F13 - You cannot stop having a child 
fact F13 {
	always (
		all p1, p2: Person |
			p2 in p1.children implies (p2 in p1.children since newBorn[p2])
	)
}

-- F14 - Unborn people cannot me married
fact F14 {
	always (
		all p : Person | p.liveness = Unborn implies p.spouse = none 
	)
}

pred getMarried[p1:Person, p2:Person] {
-- Pre-conditions 
	-- Pre1 - p1 and p2 must be alive 
	isAlive[p1] and isAlive[p2]
	-- Pre2 - p1 and p2 must be singe
	no (p1+p2).spouse
	-- Pre3 - p1 and p2 cannot be blood relatives
	!BloodRelatives[p1,p2]

-- Post-conditions 
	-- Post1 - p1 is a spouse of p2 and vice-versa 
	p1.spouse' = p2 and p2.spouse' = p1

-- Frame-conditions 
	-- Frame 1 - children does not change
	all p : Person | p.children' = p.children 
	-- Frame 2 - liveness does not change
	all p : Person | p.liveness' = p.liveness 
	-- Frame 3 - spouse only changes for p1 and p2
	all p : Person | p != p1 and p != p2 implies p.spouse' = p.spouse
}

pred beBorn[p1:Person, p2:Person, p:Person] {
-- Pre-conditions 
	-- Pre1 - p must be unborn 
	isUnborn[p]
	-- Pre2 - the parents must have been alive 
	before isAlive[p1] and isAlive[p2]
	-- Pre3 - the parents cannot be blood related
	not BloodRelatives[p1,p2]

-- Post-conditions 
	-- Post1 - p becomes alive
	p.liveness' = Alive 
	-- Post2 - p is added to the children 
	p1.children' = p1.children + p
	p2.children' = p2.children + p 

-- Frame conditions 
	-- Frame 1 - spouse does not change
	spouse' = spouse
	-- Frame 2 - children only changes for p1 and p2
	all p : Person | p != p1 and p != p2 implies p.children' = p.children
	-- Frame 3 - liveness only changes for p
	all p3 : Person | p3 != p implies p3.liveness' = p3.liveness
}

pred die[p:Person] {
-- Pre-conditions 
	-- Pre1 - p must be living 
	isAlive[p]
	
-- Post-conditions 
	-- Post1 - p must be dead 
	p.liveness' = Dead

-- Frame conditions 
	-- Frame 1 - children does not change
	children' = children
	-- Frame 2 - spouse does not change
	spouse' = spouse
	-- Frame 3 - liveness only changes for p 
	liveness' = liveness - (p->Alive) + (p->Dead)
}




// Initial conditions 
fact init {
	no children 
	no spouse
	Person.liveness = Alive+Unborn
}

pred stutter {
	children' = children
	spouse' = spouse
	liveness' = liveness
}

pred transition[] {
	(some p1,p2:Person | getMarried[p1,p2])
	or 
	(some p1,p2,p3: Person | beBorn[p1,p2,p3])
	or 
	(some p:Person | die[p])
	or 
	stutter[]
}

pred final[] {
	 some spouse
}

pred System[] {
	always transition[]
	and 
	eventually final[]
	and 
	eventually once (some p1,p2,p3: Person | beBorn[p1,p2,p3])
	and 
	eventually once (some p:Person | die[p])
}

//run {#Person > 3 and some liveness.Alive} for 5

//run { eventually once (some p1, p2: Person | getMarried[p1,p2]) } for 5 but 9 steps

run { System[] } for 5 but 9 steps
