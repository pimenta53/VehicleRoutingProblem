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
import jecoli.test.MatUtils;

public class VRPSCross implements IReproductionOperator<PermutationRepresentation,PermutationRepresentationFactory>{

	
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
	public VRPSCross(VRP problemInstance){
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
		
		 first_imp_string_cross (res);
		 //best_imp_string_cross (routes);
		
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

	// first improvement
	double first_imp_string_cross (int [][] routes)
	{
	 boolean improved = false;
	 double res = 0.0;
	 boolean feasible = (problemInstance.excess_demand(routes) == 0.0);

	 for(int r1 = 0; r1 < routes.length && !improved; r1++)
	 		for(int r2 = r1+1; r2 < routes.length && !improved; r2++)
					for(int c1 = 2; c1 < routes[r1].length && !improved; c1++)
						for(int c2 = 2; c2 < routes[r2].length && !improved; c2++)
	 					if(routes[r1].length >= 3 && routes[r2].length >= 3)
						{
	 						res = imp_string_cross(routes, r1, r2, c1, c2, feasible);
							if(res > 0.0) improved = true;;
						}
	 return res;
	}


	// best improvement
	double best_imp_string_cross(int [][] routes)
	{
	 double best = 0.0;
	 int br1=0, br2=0, bc1=0, bc2=0;
	 boolean feasible = (problemInstance.excess_demand(routes) == 0.0);

	 for(int r1 = 0; r1 < routes.length; r1++)
	 		for(int r2 = r1+1; r2 < routes.length; r2++)
					for(int c1 = 2; c1 < routes[r1].length; c1++)
						for(int c2 = 2; c2 < routes[r2].length; c2++)
	 					if(routes[r1].length >= 3 && routes[r2].length >= 3)
						{
	 						double imp = imp_string_cross(routes, r1, r2, c1, c2, feasible, false);
							if (imp > best ) { best = imp; br1 = r1; br2 = r2; bc1 = c1; bc2 = c2; }
						}
	 if (best> 0.0) imp_string_cross(routes, br1, br2, bc1, bc2, feasible, true);
	 return best;
	}

	double imp_string_cross(int [][] routes,int r1, int r2, int c1, int c2, boolean feasible) 
	{
		return imp_string_cross(routes, r1, r2, c1, c2, feasible, true);
	}

	double imp_string_cross(int [][] routes, int r1, int r2, int c1, int c2, 
		boolean feasible, boolean sw) 
	{
		return string_cross(routes, r1, r2, c1, c2, PERFORM_ON_IMPROVE, feasible, sw);
	}

	
	// String cross move - only perform if there is improvement
	// returns improvement 
	// if sw is true perform the switch when solution is better else only return value
	// mode = ALWAYS_PERFORM ou PERFORM_ON_IMPROVE
	double string_cross(int [][] routes, int r1, int r2, int c1, int c2, 
		int mode, boolean feasible, boolean sw) 
	{
	 double res;
	 int [] newr1;
	 int [] newr2;
	 int i, j;

	 newr1 = new int[c1+ (routes[r2].length-c2)];
	 newr2 = new int[c2+ (routes[r1].length-c1)];

	 for(i=0; i<c1; i++)
	 	newr1[i] = routes[r1][i];
	 for(j=c2; j < routes[r2].length; j++, i++)
	 	newr1[i] = routes[r2][j]; 

	 for(i=0; i<c2; i++)
	 	newr2[i] = routes[r2][i];
	 for(j=c1; j < routes[r1].length; j++, i++)
	 	newr2[i] = routes[r1][j]; 

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
