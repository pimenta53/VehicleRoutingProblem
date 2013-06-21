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

public class VRPOptInversion implements IReproductionOperator<PermutationRepresentation,PermutationRepresentationFactory>{

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
	public VRPOptInversion(VRP problemInstance){
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
		int[] res1 = improve_routes_2opt(parentGenome.getGenomeAsArray());
		//int[] res2=order_inversion_improve(parentGenome.getGenomeAsArray());
		
		//double excess1= problemInstance.excess_demand(res1);
		//double cost1=problemInstance.total_cost(res1);
		
	//	double excess2= problemInstance.excess_demand(res2);
		//double cost2=problemInstance.total_cost(res2);
		
		//int [] candidate =res1;// new int [parentGenome.getGenomeAsArray().length];
		
		/*
		if(excess1<excess2) candidate=res1;
		else if(excess1>excess2) candidate=res2;
		// falham os anteriores
		if(excess1==excess2){
			if(cost1<cost2) candidate=res1;
			else if(cost1>cost2) candidate=res2;
		}
		*/
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

	int[] inversion_improve(int [] sol)
	{
	 int i, j, l;
	 int s = sol.length;
	 int [] newsol = new int[s];
	 int pos = MatUtils.irandom(s-1);
	 int k = MatUtils.irandom(s-2);
	 int n = (int)(k/2);

	 for(i=0; i < s; i++) newsol[i] = sol[i];
	 for(i=0, j=pos, l=(pos+k-1)%s; i<n;i++,j=(j+1)%s) {
			newsol[j] = sol[l];
			newsol[l] = sol[j];
	    	if(l > 0) l--;
	    	else l=s-1;
	  	}
	 improve_routes_2opt(newsol);

	 if (problemInstance.total_cost(newsol) < problemInstance.total_cost(sol))
	 	return newsol;
	 else return sol;
	}


	int[] order_inversion_improve(int [] sol)
	{
	 int i, j, l;
	 int [] newsol = new int[nc];
	 int pos = MatUtils.irandom(nc-1);
	 int k = MatUtils.irandom(nc-2);
	 int n = (int)(k/2);

	 for(i=0; i < nc; i++) newsol[i] = sol[i];
	 for(i=0, j=pos, l=(pos+k-1)%nc; i<n;i++,j=(j+1)%nc) {
			newsol[j] = sol[l];
			newsol[l] = sol[j];
	    	if(l > 0) l--;
	    	else l=nc-1;
	  	}

	 int [][] r = problemInstance.routes_from_order(sol);
	 int [][] r1 = problemInstance.routes_from_order (newsol);
	 //improve_routes_2opt(newsol);

	 if (problemInstance.total_cost(r1) < problemInstance.total_cost(r))
	 	return newsol;
	 else return sol;
	}

	
	// Improvement heuristics
	// within routes

	int[] improve_routes_2opt(int[] sol)
	{
		int[] res;
		int[][] routes = problemInstance.get_routes(sol);
		for(int i=0; i< routes.length; i++)
			if(routes[i].length > 2) problemInstance.getTsp().one_pass_2opt(routes[i]);
		res = problemInstance.routes_to_array(routes);
		return res;
	}

	
	// auxiliar
	void improve_routes_2opt(int[][] routes)
	{
		for(int i=0; i< routes.length; i++)
		{
			problemInstance.getTsp().one_pass_2opt(routes[i]);
		}
	}


	
}
