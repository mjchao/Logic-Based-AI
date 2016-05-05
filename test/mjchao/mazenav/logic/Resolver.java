package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mjchao.mazenav.logic.StatementCNF.Disjunction;
import mjchao.mazenav.logic.StatementCNF.Disjunction.Term;
import mjchao.mazenav.logic.structures.SymbolTracker;

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
		return null;
	}
	
	static void unify( SymbolTracker tracker , Term t1 , Term t2 ) {
		
	}
}
