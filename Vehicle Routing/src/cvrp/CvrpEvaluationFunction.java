package cvrp;

import jecoli.algorithm.components.evaluationfunction.AbstractEvaluationFunction;
import jecoli.algorithm.components.evaluationfunction.IEvaluationFunction;
import jecoli.algorithm.components.evaluationfunction.InvalidEvaluationFunctionInputDataException;
import jecoli.algorithm.components.representation.permutations.PermutationRepresentation;


/**
 * The Class CvrpvaluationFunction: fitness function for the Vehicle Routing Problem.
 */
public class CvrpEvaluationFunction extends AbstractEvaluationFunction<PermutationRepresentation>
{

  /** The problem instance. */
  Cvrp problemInstance;

  /**
   * Instantiates a new cvrp evaluation function.
   *
   * @param problemInstance the problem instance
   */
  public CvrpEvaluationFunction(Cvrp problemInstance){
    super(false);  // minimization
    this.problemInstance = problemInstance;
  }


  @Override
  public double evaluate(PermutationRepresentation solution){
    //aqui pode ser necessario mudar este metodo de obter o genome
    // Ž preciso ver qual a melhor forma de avaliarmos o custo total
    int [] genome = solution.getGenomeAsArray();
    double fitness = problemInstance.total_cost(genome);
    return fitness;
  }

  @Override
  public IEvaluationFunction<PermutationRepresentation> deepCopy() {
    return new CvrpEvaluationFunction(problemInstance);
  }


  @Override
  public void verifyInputData()
      throws InvalidEvaluationFunctionInputDataException {
    // TODO Auto-generated method stub

  }
}
