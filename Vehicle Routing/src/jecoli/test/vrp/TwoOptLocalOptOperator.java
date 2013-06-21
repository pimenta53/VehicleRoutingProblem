package jecoli.test.vrp;

import jecoli.algorithm.components.operator.IReproductionOperator;
import jecoli.algorithm.components.operator.reproduction.permutation.AbstractPermutationMutationOperator;
import jecoli.algorithm.components.randomnumbergenerator.IRandomNumberGenerator;
import jecoli.algorithm.components.representation.permutations.PermutationRepresentation;
import jecoli.algorithm.components.representation.permutations.PermutationRepresentationFactory;
import jecoli.test.tsp.libtsp.Tsp;

public class TwoOptLocalOptOperator extends AbstractPermutationMutationOperator
{
	
	/** The solution factory. */
	PermutationRepresentationFactory solutionFactory;
	
	/** The problem instance. */
	protected VRP problemInstance;
	
	/** The one pass. */
	boolean onePass = true; // if false do complete 2 opt
	
	/**
	 * Instantiates a new two opt local optimization operator.
	 * 
	 * @param solutionFactory the solution factory
	 * @param problemInstance the problem instance
	 */
	public TwoOptLocalOptOperator(PermutationRepresentationFactory solutionFactory, VRP problemInstance)
	{
		this.problemInstance = problemInstance;
	}

	/**
	 * Instantiates a new two opt local optimization operator.
	 * 
	 * @param solutionFactory the solution factory
	 * @param problemInstance the problem instance
	 * @param onePass the one pass
	 */
	public TwoOptLocalOptOperator(PermutationRepresentationFactory solutionFactory, VRP problemInstance, boolean onePass)
	{
		this.problemInstance = problemInstance;
		this.onePass = onePass;
	}

	protected void mutateGenome(PermutationRepresentation childGenome, IRandomNumberGenerator randomNumberGenerator)
	{
int[] genomeArray = childGenome.getGenomeAsArray();
		
		if (onePass) {
			double fitness = problemInstance.total_cost(genomeArray);
			problemInstance.getTsp().one_pass_2opt(genomeArray, fitness);
		}
		else problemInstance.getTsp().complete_2opt(genomeArray);
		
		for(int i=0; i < childGenome.getNumberOfElements(); i++)
			childGenome.setElement(i, genomeArray[i]);
	}

	@Override
	public IReproductionOperator<PermutationRepresentation, PermutationRepresentationFactory> deepCopy()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void mutateGenome(PermutationRepresentation childGenome,
			PermutationRepresentationFactory solutionFactory,
			IRandomNumberGenerator randomNumberGenerator) {
		mutateGenome(childGenome, randomNumberGenerator);
		
	}
	
}
