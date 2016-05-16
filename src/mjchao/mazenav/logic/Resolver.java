package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.List;

import mjchao.mazenav.logic.StatementCNF.Disjunction;
import mjchao.mazenav.logic.StatementCNF.Disjunction.Term;
import mjchao.mazenav.logic.structures.Function;
import mjchao.mazenav.logic.structures.SkolemFunction;
import mjchao.mazenav.logic.structures.SymbolTracker;
import mjchao.mazenav.logic.structures.Variable;

/**
 * Performs the resolution algorithm on a conjunction of disjunctions
 * 
 * @author mjchao
 */
class Resolver {

	/**
	 * Determines if the given hypothesis is always true given
	 * our knowledgebase of known facts.
	 * 
	 * @param tracker			keeps track of symbols
	 * @param hypothesis		the hypothesis to try and prove
	 * @param kb				the statements in our knowledgebase that
	 * 							we know to be true
	 * @return					true if the hypothesis is always true given
	 * 							the knowledgebase. false otherwise.
	 */
	public static boolean proveHypothesis( SymbolTracker tracker , StatementCNF hypothesis , StatementCNF... kb ) {
		List< StatementCNF > statements = new ArrayList< StatementCNF >();
		for ( StatementCNF s : kb ) {
			statements.add( s );
		}
		statements.add( StatementCNF.negate( hypothesis , tracker ) );
		return applyResolution( tracker , StatementCNF.andTogether( statements , tracker ) , hypothesis );
	}
	
	/**
	 * Represents a disjunction in the search tree of the resolution
	 * algorithm
	 */
	static class Resolvent {
		
		public Disjunction disjunction;
		public List< Resolvent > parents = new ArrayList< Resolvent >();
		
		public Resolvent( Disjunction d ) {
			this.disjunction = d;
		}
		
		@Override
		public String toString() {
			return this.disjunction.toString();
		}
	}
	
	/**
	 * Applies the resolution algorithm combined with factoring
	 * to try and prove a hypothesis by contradiction 
	 * 
	 * @param tracker		keeps track of symbols
	 * @param statement		a single statement that is KB AND !Hypothesis
	 * @param hypothesis	the hypothesis we're trying to prove. this is
	 * 						required because we need to check that terms
	 * 						we unify do not appear in the hypothesis
	 * @return				true if KB AND !Hypothesis is always false (i.e.
	 * 						the proof by contradiction succeeds). false if
	 * 						we could not complete the proof by contradiction
	 */
	static boolean applyResolution( SymbolTracker tracker , StatementCNF statement , StatementCNF hypothesis ) {
		List< Resolvent > clauses = new ArrayList< Resolvent >();
		for ( Disjunction d : statement.getDisjunctions() ) {
			clauses.add( new Resolvent(factor(d , hypothesis)) );
		}
		
		List< Resolvent > justAddedClauses = new ArrayList< Resolvent >( clauses );
		
		while( true ) {
			List< Resolvent > newClauses = new ArrayList< Resolvent >();
			
			//attempt to resolve every pair of clauses
			//if any of those pairs yields a contradiction (i.e. P AND !P)
			//then the proof by contradiction succeeds (return true)
			for ( int i=0 ; i<clauses.size() ; ++i ) {
				for ( int j=0 ; j<justAddedClauses.size() ; ++j ) {
					if ( !clauses.get( j ).parents.contains( clauses.get( i ) ) &&
							clauses.get( i ).parents.size() > 0 && clauses.get( j ).parents.size() > 0 ) {
						continue;
					}
					Disjunction c1 = clauses.get( i ).disjunction;
					Disjunction c2 = justAddedClauses.get( j ).disjunction;
					List< Disjunction > resolvents = resolve( c1 , c2 , hypothesis );
					if ( containsEmptyClause( resolvents ) ) {
						return true;
					}
					
					for ( Disjunction d : resolvents ) {
						Resolvent newSearchState = new Resolvent( d );
						newSearchState.parents.add( clauses.get( i ) );
						newSearchState.parents.add( clauses.get( j ) );
						newClauses.add( newSearchState );
					}
				}
			}
			
			boolean addedClause = false;
			justAddedClauses.clear();
			for ( Resolvent d : newClauses ) {
				Disjunction toAdd = factor(d.disjunction , hypothesis);
				if ( !isDuplicateClause( clauses , toAdd , hypothesis) ) {
					addedClause = true;
					d.disjunction = toAdd;
					clauses.add( d );
					justAddedClauses.add( d );
				}
			}
			
			//if there is no more we can infer, then 
			//our proof by contradiction fails (return false)
			if ( !addedClause ) {
				return false;
			}
		}
	}
	
	/**
	 * @param clauses
	 * @param toAdd
	 * @return				if the current list of clauses already contains
	 * 						a clause that unified with the clause to be added
	 */
	static boolean isDuplicateClause( List< Resolvent > clauses , Disjunction toAdd , StatementCNF hypothesis ) {
		
		//clauses.contains checks that terms are identical up to reordering
		if ( clauses.contains( toAdd ) ) {
			return true;
		}
		
		//we'll add another check that if terms in the given ordering
		//all unify together
		for ( Resolvent r : clauses ) {
			Disjunction d = r.disjunction;
			if ( d.size() != toAdd.size() ) {
				continue;
			}
			
			boolean allTermsSame = true;
			for ( int i=0 ; i<d.size() ; ++i ) {
				if ( findResolveUnification( d.getTerm( i ) , toAdd.getTerm( i ) , hypothesis ) == null ) {
					allTermsSame = false;
					break;
				}
			}
			
			if ( allTermsSame ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Factors a clause by removing any redundant terms that
	 * can be unified together
	 * 
	 * @param d
	 * @return		the original clause minus any redundant terms
	 */
	static Disjunction factor( Disjunction d , StatementCNF hypothesis ) {
		Disjunction rtn = new Disjunction();
		for ( int i=0 ; i<d.size() ; ++i ) {
			Term t1 = d.getTerm( i );
			boolean isDuplicate = false;
			for ( int j=i+1 ; j<d.size() ; ++j ) {
				Term t2 = d.getTerm( j );
				if ( findResolveUnification( t1 , t2 , hypothesis ) != null ) {
					isDuplicate = true;
					break;
				}
			}
			
			if ( !isDuplicate ) {
				rtn.addTerm( t1 );
			}
		}
		return rtn;
	}
	
	/**
	 * @param clauses		
	 * @return			if any of the given clauses is the empty clause
	 *					(i.e. contains no terms)
	 */
	static boolean containsEmptyClause( List< Disjunction > clauses ) {
		for ( Disjunction clause : clauses ) {
			if ( clause.size() == 0 ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines all possible results from resolving the two
	 * given clauses. Two clauses can be resolved if a term
	 * in the first clause unifies with the negation of a term
	 * in the second clause.
	 * 
	 * @param clause1
	 * @param clause2
	 * @return
	 */
	static List< Disjunction > resolve( Disjunction clause1 , Disjunction clause2 , StatementCNF hypothesis ) {
		List< Disjunction > rtn = new ArrayList< Disjunction >();
		for ( int i=0 ; i<clause1.size() ; ++i ) {
			Term t1 = clause1.getTerm( i );
			for ( int j=0 ; j<clause2.size() ; ++j ) {
				Term t2 = clause2.getTerm( j ).clone();
				
				//negate the second term and try to resolve it with the first
				t2.negate();
				
				List< Substitution > subs = findResolveUnification( t1 , t2 , hypothesis );
				if ( subs != null ) {
					Disjunction newClause = buildResolveClause( clause1 , clause1.getTerm( i ) , clause2 , clause2.getTerm( j ) , subs );
					rtn.add( newClause );
				}
			}
		}
		return rtn;
	}
	
	/**
	 * Reports the unifications necessary to make two terms identical so that
	 * the clauses to which they belong can be resolved. If the two terms 
	 * cannot be made identical, then null is returned.
	 * 
	 * @param t1
	 * @param t2
	 * @param hypothesis		the hypothesis currently being proved.
	 * 							this information is necessary so that
	 * 							we do not unify parts of the hypothesis
	 * 							and make it less general
	 * @return
	 */
	static List< Substitution > findResolveUnification( Term t1 , Term t2 , StatementCNF hypothesis ) {
		//we cannot resolve different variables. Suppose we are presented with
		//P AND Q. Obviously substituting P/!Q will result in the empty
		//clause, but that's not valid, so we want to skip this type of
		//unification
		if ( t1.getValue() instanceof Variable &&  t2.getValue() instanceof Variable ) {
			Variable var1 = (Variable) t1.getValue();
			Variable var2 = (Variable) t2.getValue();
			if ( (!var1.universallyQuantified() && !var2.universallyQuantified()) &&
					!var1.equals( var2 ) ) {
				return null;
			}
		}
		
		//we cannot unify two functions if one is negated and the other
		//is not
		if ( t1.getValue() instanceof Function &&
				t2.getValue() instanceof Function &&
				t1.negated() != t2.negated() ) {
			return null;
		}
		
		//we cannot unify two skolem functions either if one is negated
		//and the other is not. we don't unify EXISTS(x) with !EXISTS(y).
		//we compare t1.negated() == t2.negated() here because
		//the resolve function negated t2 already.
		if ( t1.getValue() instanceof SkolemFunction && 
				t2.getValue() instanceof SkolemFunction && 
				t1.negated() == t2.negated() ) {
			return null;
		}
		
		List< Substitution > subs = unify( t1 , t2 , new ArrayList< Substitution >() );
		if ( subs != null ) {

			boolean validUnification = true;
			for ( Substitution sub : subs ) {
				
				//we cannot unify variables in the hypothesis with
				//other things because that would make the hypothesis
				//less general
				if ( (hypothesis.containsTerm( sub.original ) && sub.original.getValue() instanceof Variable) ) {
					validUnification = false;
					break;
				}
				
				
				if ( sub.original.getValue() instanceof SkolemFunction && 
						!(sub.substitution.getValue() instanceof Variable || sub.substitution.getValue() instanceof SkolemFunction) ) {
					validUnification = false;
					break;
				}
			}
			if ( !validUnification ) {
				return null;
			}
			return subs;
		}
		return null;
	}
	
	/**
	 * Given two clauses and the unifications necessary to resolve two terms
	 * in those clauses, combines the two clauses into a single resolvent
	 * that contains the substitutions specified by the unifications.
	 * 
	 * @param clause1			the first clause to resolve
	 * @param resolvedTerm1		the term in the first clause that allows the
	 * 							two clauses to be resolved
	 * @param clause2			the second clause to resolve
	 * @param resolvedTerm2		the term in the second clause that allows the
	 * 							two clauses to be resolved
	 * @param subs				substitutions necessary to resolve the two clauses
	 * @return					a single clause that is the resolvent of
	 * 							the original two clauses
	 */
	static Disjunction buildResolveClause( Disjunction clause1 , Term resolvedTerm1 , Disjunction clause2 , Term resolvedTerm2 , List< Substitution > subs ) {
		Disjunction newClause = new Disjunction();
		for ( int k=0 ; k<clause1.size() ; ++k ) {
			Term toAdd = clause1.getTerm( k );
			if ( toAdd != resolvedTerm1 ) {
				for ( Substitution sub : subs ) {
					if ( sub.original.equalsIgnoringNegated( toAdd ) ) {
						if ( sub.original.negated() == toAdd.negated() ) {
							toAdd = sub.substitution;
						}
						else {
							toAdd = sub.substitution.clone();
							toAdd.negate();
						}
					}
					else {
						toAdd = toAdd.clone();
						toAdd.substituteArg( sub.original , sub.substitution );
					}
				}
				newClause.addTerm( toAdd );
			}
		}
		for ( int k=0 ; k<clause2.size() ; ++k ) {
			Term toAdd = clause2.getTerm( k );
			if ( toAdd != resolvedTerm2 ) {
				for ( Substitution sub : subs ) {
					if ( sub.original.equalsIgnoringNegated( toAdd ) ) {
						if ( sub.original.negated() == toAdd.negated() ) {
							toAdd = sub.substitution.clone();
						}
						else {
							toAdd = sub.substitution.clone();
							toAdd.negate();
						}
					}
					else {
						toAdd = toAdd.clone();
						toAdd.substituteArg( sub.original , sub.substitution );
					}
				}
				newClause.addTerm( toAdd );
			}
		}
		return newClause;
	}
	
	/**
	 * Represents a substitution made by the unifier
	 * 
	 * @author mjchao
	 */
	static class Substitution {
		public final Term original;
		public final Term substitution;
		
		public Substitution( Term original , Term substitution ) {
			this.original = original;
			this.substitution = substitution;
		}
		
		@Override
		public String toString() {
			return original + "/" + substitution;
		}
	}
	
	/**
	 * Attempts to unify the two given terms so that they are
	 * equivalent
	 * 
	 * @param t1
	 * @param t2
	 * @param substitutions		the substitutions made by the unification algorithm so far 
	 * @return					the list of substitutions required to unify t1 with t2
	 * 							or null if t1 cannot be unified with t2
	 */
	static List< Substitution > unify( Term t1 , Term t2 , List< Substitution > substitutions ) {
		if ( t1.equals( t2 ) ) {
			return substitutions;
		}
		else if ( t1.getValue() instanceof Variable ) {
			return unifyVar( t1 , t2 , substitutions );
		}
		else if ( t2.getValue() instanceof Variable ) {
			return unifyVar( t2 , t1 , substitutions );
		}
		else if ( t1.getValue() instanceof SkolemFunction ) {
			
			//a skolem function can be treated as a
			//normal variable.
			return unifyVar( t1 , t2 , substitutions );
		}
		else if ( t2.getValue() instanceof SkolemFunction ) {
			return unifyVar( t2 , t1 , substitutions );
		}
		else if ( t1.getValue() instanceof Function && t2.getValue() instanceof Function ) {
			if ( t1.getValue().equals( t2.getValue() ) ) {
				for ( int i=0 ; i<t1.getArgs().length ; ++i ) {
					List< Substitution > subs = unify( t1.getArgs()[ i ] , t2.getArgs()[ i ] , substitutions );
					if ( subs == null ) {
						return null;
					}
				}
				return substitutions;
			}
			else {
				
				//we will not unify different functions because
				//determining if the outputs of two different functions
				//will be the same is at least as hard as the halting problem
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Unifies a variable with another term
	 * 
	 * @param var				a term that is a variable
	 * @param x					another term
	 * @param substitutions		list of substitutions built up so far
	 * @return					list of substitutions required to unify var with x
	 * 							(the parameter <code>substitutions</code> is directly
	 * 							updated to include any new substitutions performed
	 * 							in this unification algorithm) 
	 */
	static List< Substitution > unifyVar( Term var , Term x , List< Substitution > substitutions ) {
		
		//check if the variable has already been substituted by something else
		//and if so, unify that with x
		for ( Substitution sub : substitutions ) {
			if ( sub.original.equals( var ) ) {
				return unify( sub.substitution , x , substitutions );
			}
		}
		
		//check if x has already been substituted by something else
		//and if so, unify the variable with that
		for ( Substitution sub : substitutions ) {
			if ( sub.original.equals( x ) ) {
				return unify( var , sub.substitution , substitutions );
			}
		}
		
		//perform an occur check. For example, x cannot unify with f(x)
		if ( var.containsTermIgnoringNegated( x ) || x.containsTermIgnoringNegated( var ) ) {
			return null;
		}
		
		//otherwise, we just directly substitute x for var
		Substitution newSubstitution = new Substitution( var , x );
		substitutions.add( newSubstitution );
		return substitutions;
	}
}
