package jecoli.test.vrp;

import jecoli.algorithm.components.evaluationfunction.AbstractEvaluationFunction;
import jecoli.algorithm.components.evaluationfunction.IEvaluationFunction;
import jecoli.algorithm.components.evaluationfunction.InvalidEvaluationFunctionInputDataException;
import jecoli.algorithm.components.representation.permutations.PermutationRepresentation;
import jecoli.test.knapsacking.Knapsacking;
import jecoli.test.knapsacking.KnapsackingPermutationEvaluationFunction;

public class VRPEvaluationFunction extends AbstractEvaluationFunction<PermutationRepresentation> {

	VRP vrpInstance;

	/**
	 * Instantiates a new knapsacking permutation evaluation function.
	 * 
	 * @param knapsackingInstance the knapsacking instance
	 */
	public VRPEvaluationFunction(VRP vrpInstance){
		super(true);  // maximization
		this.vrpInstance = vrpInstance;
	}
	
	

	
	
	/* (non-Javadoc)
	 * @see core.EvaluationFunction#evaluate(core.representation.IRepresentation)
	 */
	@Override
	public double evaluate(PermutationRepresentation solution) 
	{
		
		int [] genome = solution.getGenomeAsArray();
		double fitness=0;
		
		double excess= vrpInstance.excess_demand(genome);
		double cost=vrpInstance.total_cost(genome);
		
			fitness=-(excess*0.9+cost*0.1);
			
		
		return fitness;
	}


	@Override
	public IEvaluationFunction<PermutationRepresentation> deepCopy() throws Exception {
		return new VRPEvaluationFunction(new VRP(vrpInstance));
	}

	@Override
	public void verifyInputData()
			throws InvalidEvaluationFunctionInputDataException {
		// TODO Auto-generated method stub
		
	}
}
