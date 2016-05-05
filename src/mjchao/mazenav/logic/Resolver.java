package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.Arrays;
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

	public static boolean proveHypothesis( SymbolTracker tracker , StatementCNF hypothesis , StatementCNF... kb ) {
		List< StatementCNF > statements = Arrays.asList( kb );
		statements.add( StatementCNF.negate( hypothesis , tracker ) );
		return applyResolution( tracker , StatementCNF.andTogether( statements ) );
	}
	
	public static boolean applyResolution( SymbolTracker tracker , StatementCNF... statements ) {
		return applyResolution( tracker , StatementCNF.andTogether( Arrays.asList( statements ) ) );
	}
	
	//TODO test resolution
	public static boolean applyResolution( SymbolTracker tracker , StatementCNF statement ) {
		List< Disjunction > clauses = new ArrayList< Disjunction >();
		for ( Disjunction d : statement.getDisjunctions() ) {
			clauses.add( d );
		}
		List< Disjunction > newClauses = new ArrayList< Disjunction >();
		
		while( true ) {
			
			//attempt to resolve every pair of clauses
			//if any of those pairs yields a contradiction (i.e. P AND !P)
			//then the proof by contradiction succeeds (return true)
			for ( int i=0 ; i<clauses.size() ; ++i ) {
				for ( int j=i+1 ; j<clauses.size() ; ++j ) {
					Disjunction c1 = clauses.get( i );
					Disjunction c2 = clauses.get( j );
					List< Disjunction > resolvents = resolve( c1 , c2 );
					if ( containsEmptyClause( resolvents ) ) {
						return true;
					}
					newClauses.addAll( resolvents );
				}
			}
			
			boolean addedClause = false;
			for ( Disjunction d : newClauses ) {
				if ( !clauses.contains( d ) ) {
					addedClause = true;
					clauses.add( d );
				}
			}
			
			//if there is no more we can infer, then 
			//our proof by contradiction fails (return false)
			if ( !addedClause ) {
				return false;
			}
		}
	}
	
	private static boolean containsEmptyClause( List< Disjunction > clauses ) {
		for ( Disjunction clause : clauses ) {
			if ( clause.size() == 0 ) {
				return true;
			}
		}
		return false;
	}
	
	private static List< Disjunction > resolve( Disjunction clause1 , Disjunction clause2 ) {
		for ( int i=0 ; i<clause1.size() ; ++i ) {
			Term t1 = clause1.getTerm( i );
			for ( int j=0 ; j<clause2.size() ; ++j ) {
				Term t2 = clause2.getTerm( j );
				
				if ( t1.getValue().equals( t2.getValue() ) &&
						t1.negated() != t2.negated() ) {

				}
			}
		}
		return null;
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
			Term termWithVarBindings = t1.clone();
			for ( Substitution sub : substitutions ) {
				if ( sub.original.getValue() instanceof Variable ) {
					termWithVarBindings.bindVariableArg( (Variable) sub.original.getValue() , sub.substitution );
				}
			}
			
			//a skolem function with bound variables can be treated as a
			//normal variable
			return unifyVar( termWithVarBindings , t2 , substitutions );
		}
		else if ( t2.getValue() instanceof SkolemFunction ) {
			Term termWithVarBindings = t2.clone();
			for ( Substitution sub : substitutions ) {
				if ( sub.original.getValue() instanceof Variable ) {
					termWithVarBindings.bindVariableArg( (Variable) sub.original.getValue() , sub.substitution );
				}
			}
			return unifyVar( termWithVarBindings , t1 , substitutions );
		}
		else if ( t1.getValue() instanceof Function && t2.getValue() instanceof Function ) {
			if ( t1.getValue().equals( t2.getValue() ) ) {
				for ( int i=0 ; i<t1.getArgs().length ; ++i ) {
					List< Substitution > subs = unify( t1.getArgs()[ i ] , t2.getArgs()[ i ] , substitutions );
					if ( subs == null ) {
						return null;
					}
				}
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
		
		//TODO: occur check?
		
		//otherwise, we just directly substitute x for var
		Substitution newSubstitution = new Substitution( var , x );
		substitutions.add( newSubstitution );
		return substitutions;
	}
	
	/*
	static List< Substitution > unifySkolem( Term skolem , Term x , List< Substitution > substitutions ) {
		SkolemFunction toUnify = (SkolemFunction) skolem.getValue();
		//check if the skolem function has already been substituted by
		//something else and if so, unify that with the skolem function
		for ( Substitution sub : substitutions ) {
			if ( sub.original.getValue() instanceof SkolemFunction ) {
				SkolemFunction f = (SkolemFunction) sub.original.getValue();
				if ( f.getID() == toUnify.getID() ) {
					//check if the previous substitution was for a skolem function
					//was at least as general as the skolem function we're trying
					//to unify. For example, if there was already a substitution
					//for "$0(?0,?1,?2)" and now we are unifying "$0(CONST1, CONST2, CONST3)"
					//we want to automatically use the more general substitution "$0(?0,?1,?2)"
					//and not create a new substitution
					boolean isCandidateGeneralized = false;
					Term[] toUnifyArgs = skolem.getArgs();
					Term[] candidateArgs = sub.original.getArgs();
					for ( int i=0 ; i<skolem.getArgs().length ; ++i ) {
						if ( toUnifyArgs[ i ].getValue() instanceof Variable )
					}
				}
			}
		}
		//otherwise, we just directly substitute the x for the skolem function
		Substitution newSubstitution = new Substitution( skolem , x );
		substitutions.add( newSubstitution );
		return substitutions;
	}*/
}
