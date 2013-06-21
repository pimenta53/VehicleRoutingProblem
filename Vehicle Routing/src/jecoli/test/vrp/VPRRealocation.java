package jecoli.test.vrp;

import java.util.ArrayList;
import java.util.List;

import jecoli.algorithm.components.operator.IReproductionOperator;
import jecoli.algorithm.components.operator.InvalidNumberOfInputSolutionsException;
import jecoli.algorithm.components.operator.InvalidNumberOfOutputSolutionsException;
import jecoli.algorithm.components.operator.ReproductionOperatorType;
import jecoli.algorithm.components.randomnumbergenerator.IRandomNumberGenerator;
import jecoli.algorithm.components.representation.permutations.PermutationRepresentation;
import jecoli.algorithm.components.representation.permutations.PermutationRepresentationFactory;
import jecoli.algorithm.components.solution.ISolution;

public class VPRRealocation implements IReproductionOperator<PermutationRepresentation,PermutationRepresentationFactory>{

	
	public static final int PERFORM_ON_IMPROVE = 1;
	public static final int ALWAYS_PERFORM = 0;
	
	/** The Constant NUMBER_OF_INPUT_SOLUTIONS. */
	protected static final int NUMBER_OF_INPUT_SOLUTIONS = 1;
	
	/** The Constant NUMBER_OF_OUTPUT_SOLUTIONS. */
	protected static final int NUMBER_OF_OUTPUT_SOLUTIONS = 1;

	/** The problem instance. */
	protected VRP problemInstance;
	
	protected int nc;

	/** The solution factory. */
	//protected PermutationRepresentationFactory solutionFactory;

	
	/**
	 * Instantiates a new tsp greedy crossover.
	 * 
	 * @param solutionFactory the solution factory
	 * @param problemInstance the problem instance
	 */
	public VPRRealocation(VRP problemInstance){
		//this.solutionFactory  = solutionFactory;
		this.problemInstance = problemInstance;
		this.nc=problemInstance.getNumberClients();
	}


	/**
	 * Crossover genomes.
	 * 
	 * @param parentGenome1 the parent genome1
	 * @param parentGenome2 the parent genome2
	 * @param childGenome the child genome
	 */
	public void crossoverGenomes(PermutationRepresentation parentGenome,
			PermutationRepresentation childGenome) 
	{
		
		int [] genomeArray = parentGenome.getGenomeAsArray();
		int [][] res = problemInstance.get_routes(genomeArray);
		

		//first_imp_string_relocation (routes, 2);
		best_imp_string_relocation (res, 2);
		
		 
		 int [] res1 = problemInstance.routes_to_array(res);
		 
		
		for(int i=0;i<parentGenome.getGenomeAsArray().length;i++){
			childGenome.setElement(i, res1[i]);
		}
		
	}

	public List<ISolution<PermutationRepresentation>> apply(List<ISolution<PermutationRepresentation>> selectedSolutions, PermutationRepresentationFactory solutioFactory, IRandomNumberGenerator randomNumberGenerator)	throws InvalidNumberOfInputSolutionsException,
	InvalidNumberOfOutputSolutionsException {

			if(selectedSolutions.size() != NUMBER_OF_INPUT_SOLUTIONS)
					throw new InvalidNumberOfInputSolutionsException();

			List<ISolution<PermutationRepresentation>> solutionList = crossover(selectedSolutions,solutioFactory, randomNumberGenerator);

			if(solutionList.size() != NUMBER_OF_OUTPUT_SOLUTIONS)
					throw new InvalidNumberOfOutputSolutionsException();

			return solutionList;
	}	

	/**
	 * Crossover.
	 * 
	 * @param selectedSolutions the selected solutions
	 * 
	 * @return the list< i solution>
	 */
	public List<ISolution<PermutationRepresentation>> crossover(List<ISolution<PermutationRepresentation>> selectedSolutions, PermutationRepresentationFactory solutionFactory, IRandomNumberGenerator randomNumberGenerator)
	{
		List<ISolution<PermutationRepresentation>> resultList = new ArrayList<ISolution<PermutationRepresentation>>(NUMBER_OF_OUTPUT_SOLUTIONS);

		ISolution<PermutationRepresentation> parentSolution = selectedSolutions.get(0);
		
		PermutationRepresentation parentGenome1 = (PermutationRepresentation) parentSolution.getRepresentation();
		
		int isize = parentGenome1.getNumberOfElements();

		ISolution<PermutationRepresentation> child = solutionFactory.generateSolution(isize,randomNumberGenerator);

		PermutationRepresentation childGenome = (PermutationRepresentation) child.getRepresentation();
		
		crossoverGenomes(parentGenome1, childGenome);

		resultList.add(child);

		return resultList;
	}


	public int getNumberOfInputSolutions(){
		return NUMBER_OF_INPUT_SOLUTIONS;
	}

	public int getNumberOfOutputSolutions(){
		return NUMBER_OF_OUTPUT_SOLUTIONS;
	}

	/**
	 * Input solutions have same length.
	 * 
	 * @return true, if successful
	 */
	public boolean inputSolutionsHaveSameLength(){
		return false;
	}



	@Override
	public ReproductionOperatorType getReproductionType() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IReproductionOperator<PermutationRepresentation, PermutationRepresentationFactory> deepCopy()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	double first_imp_string_relocation(int [][] routes, int K)
	{
	 boolean improve = false;
	 double res = 0.0;
	 boolean feasible = (problemInstance.excess_demand(routes) == 0.0);

	 for(int x1 = 1; x1 <= K && !improve; x1++)
	 for(int r1 = 0; r1 < routes.length && !improve; r1++)
	 		for(int r2 = 0; r2 < routes.length && !improve; r2++)
					for(int c1 = 1; c1 <= routes[r1].length-x1 && !improve; c1++)
						for(int c2 = 1; c2 <= routes[r2].length && !improve; c2++)
	 						if(r1 != r2 && routes[r1].length > x1)
							{
	 						  res = imp_string_relocation(routes, r1, r2, x1, c1, c2, feasible);
							  if (res > 0) improve = true;
							 }
	 return res;
	}

	double best_imp_string_relocation(int [][] routes, int K)
	{
	 double best = 0.0;
	 int bx1=0, br1=0, br2=0, bc1=0, bc2=0;
	 boolean feasible = (problemInstance.excess_demand(routes) == 0.0);

	 // find best move
	 for(int x1 = 1; x1 <= K; x1++)
	 for(int r1 = 0; r1 < routes.length; r1++)
	 		for(int r2 = 0; r2 < routes.length; r2++)
					for(int c1 = 1; c1 <= routes[r1].length-x1; c1++)
						for(int c2 = 1; c2 <= routes[r2].length; c2++)
	 						if(r1 != r2 && routes[r1].length > x1)
							{
	 						 double imp = imp_string_relocation(routes, r1, r2, x1, c1, c2, feasible, false);
							 if(imp>best) { best = imp; bx1 = x1; br1 = r1; br2 = r2; bc1 = c1; bc2 = c2; }
							 }
	 // execute best move
	 if(best > 0.0) imp_string_relocation(routes, br1, br2, bx1, bc1, bc2, feasible, true);
	 return best;
	}

	// String exchange move - always perform

	double imp_string_relocation(int [][] routes, int r1, int r2, int x1, int c1, int c2, boolean feasible)
	{
	 return imp_string_exchange(routes, r1, r2, x1, 0, c1, c2, feasible);
	}

	double imp_string_relocation(int [][] routes, int r1, int r2, int x1, int c1, int c2, 
		boolean feasible, boolean sw)
	{

	 return imp_string_exchange(routes, r1, r2, x1, 0, c1, c2, feasible, sw);
	}

	double string_relocation(int [][] routes, int r1, int r2, int x1, int c1, int c2, 
		int mode, boolean feasible, boolean sw)
	{
	 return string_exchange(routes, r1, r2, x1, 0, c1, c2, mode, feasible, sw);
	}

	double imp_string_exchange(int [][] routes, int r1, int r2, int x1, int x2, int c1, int c2,
			boolean feasible)
		{
		 return imp_string_exchange(routes, r1, r2, x1, x2, c1, c2, feasible, true);
		}
		double imp_string_exchange(int [][] routes, int r1, int r2, int x1, int x2, int c1, int c2, 
			boolean feasible, boolean sw)
		{
		 return string_exchange(routes, r1, r2, x1, x2, c1, c2, PERFORM_ON_IMPROVE, feasible, sw);
		}

		// String exchange move - only when improves 
		// returns cost of new solution
		// mode = ALWAYS_PERFORM ou PERFORM_ON_IMPROVE
		double string_exchange(int [][] routes, int r1, int r2, int x1, int x2, int c1, int c2, 
			int mode, boolean feasible, boolean sw)
		{
		 int [] newr1;
		 int [] newr2;
		 int i, j, k;
		 double res;

		 newr1 = new int[routes[r1].length-x1+x2];
		 newr2 = new int[routes[r2].length-x2+x1];

		 for(i=0, k=0; i<c1; i++, k++)
		 	newr1[k] = routes[r1][i];
		 for(j=c2; j< c2+x2; j++, k++)
		 	newr1[k] = routes[r2][j]; 
		 for(; i<c1+x1; i++);
		 for(; i< routes[r1].length; i++, k++)
		 	newr1[k] = routes[r1][i];

		 for(i=0, k=0; i<c2; i++, k++)
		 	newr2[k] = routes[r2][i];
		 for(j=c1; j< c1+x1; j++, k++)
		 	newr2[k] = routes[r1][j]; 
		 for(; i<c2+x2; i++);
		 for(; i< routes[r2].length; i++, k++)
		 	newr2[k] = routes[r2][i];

		 if (!feasible)
		 {
		 	double oldpen = problemInstance.excess_demand_route(routes[r1]) + problemInstance.excess_demand_route(routes[r2]);
		 	double newpen = problemInstance.excess_demand_route(newr1) + problemInstance.excess_demand_route(newr2);
		 	if(mode == ALWAYS_PERFORM || newpen < oldpen) 
			{
		 		if (sw) {
					routes[r1] = newr1;
		 			routes[r2] = newr2;
					}
				res = oldpen - newpen;
			}
			else res = 0.0;
		 }
		 else
		 {
		 	double newpen = problemInstance.excess_demand_route(newr1) + problemInstance.excess_demand_route(newr2);
			if(newpen > 0.0 && mode != ALWAYS_PERFORM) res = 0.0;
			else
			{
				double oldcost = problemInstance.getTsp().cost(routes[r1])+problemInstance.getTsp().cost(routes[r2]);
				problemInstance.getTsp().one_pass_2opt(newr1);
				problemInstance.getTsp().one_pass_2opt(newr2);
		 		double newcost = problemInstance.getTsp().cost(newr1)+problemInstance.getTsp().cost(newr2);

		 		if(mode == ALWAYS_PERFORM || oldcost > newcost)
		 		{
		 			if (sw) {
						routes[r1] = newr1;
		 				routes[r2] = newr2;
						}
					res = oldcost - newcost;
		 		}
		 	else res = 0.0;
			}
		  }
		 return res;
		}

}
