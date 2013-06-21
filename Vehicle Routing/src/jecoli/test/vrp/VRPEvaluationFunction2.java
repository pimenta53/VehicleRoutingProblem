package jecoli.test.vrp;

import jecoli.algorithm.components.evaluationfunction.AbstractEvaluationFunction;
import jecoli.algorithm.components.evaluationfunction.IEvaluationFunction;
import jecoli.algorithm.components.evaluationfunction.InvalidEvaluationFunctionInputDataException;
import jecoli.algorithm.components.representation.permutations.PermutationRepresentation;

public class VRPEvaluationFunction2 extends AbstractEvaluationFunction<PermutationRepresentation>{

	VRP vrpInstance;

	/**
	 * Instantiates a new knapsacking permutation evaluation function.
	 * 
	 * @param knapsackingInstance the knapsacking instance
	 */
	public VRPEvaluationFunction2(VRP vrpInstance){
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
		int [][] routes = vrpInstance.routes_from_order (genome,true);
		double fitness=0;
		
		double cost=vrpInstance.total_cost(routes);
		fitness=-cost;
		
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
	
	

