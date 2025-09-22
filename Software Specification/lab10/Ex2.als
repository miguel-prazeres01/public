abstract sig Person { // C1
	siblings: set Person, // C4
	mother: lone Woman, // C6
	father: lone Man // C6
}

sig Man extends Person { // C2

}

sig Woman extends Person { // C2

}

sig Married in Person { // C3
	spouse: Person // C5
}

// C7
fact siblings_def {
	all p1:Person, p2:Person |
		(some p1.(mother+father) & p2.(mother+father)) and p1 != p2 iff p2 in p1.siblings
	//siblings = (mother+father).~(mother+father)
}

// C8
fact spouse_symmetric {
	all p1:Person, p2:Person |
		p1.spouse = p2 implies p2.spouse = p1
}

// P1: p1 is an ancestor of p2
pred Ancestor[p1:Person, p2:Person] {
	p1 in p2.^(father+mother) + p2
}

// P2
pred BloodRelative[p1:Person, p2:Person] {
	some p:Person | 
		Ancestor[p,p1] and Ancestor[p,p2]
}

// C9 - no spouses blood relatives
fact no_blood_relative_marriages {
	all p1:Married, p2:Married | 
		p1.spouse = p2 implies not BloodRelative[p1,p2]
}

// C10 - no children with blood relatives
fact no_children_with_blood_relatives {
	all p:Person |
		p.mother != none && p.father != none 
			implies not BloodRelative[p.mother, p.father]
}

// C11 - no Person-parent cycles 
fact not_own_parent {
	all p:Person |
		p !in p.^(mother+father)
}

// A1: a person cannot be a sibling of his father
assert a1 {
	all p:Person |
		some p.father implies p.father !in p.siblings 
}

// A2: the siblings relation is symmetric 
assert a2 {
	all p1:Person,p2:Person | 
		p2 in p1.siblings implies p1 in p2.siblings
		
}


run {some Person.spouse && some Married && some Person.mother && some Person.father && some Person.siblings  } for exactly 5 Person
