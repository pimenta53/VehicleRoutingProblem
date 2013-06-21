package jecoli.test.vrp;

import java.util.ArrayList;
import java.util.List;
import jecoli.algorithm.components.algorithm.IAlgorithm;
import jecoli.algorithm.components.algorithm.IAlgorithmResult;
import jecoli.algorithm.components.algorithm.IAlgorithmStatistics;
import jecoli.algorithm.components.algorithm.writer.IAlgorithmResultWriter;
import jecoli.algorithm.components.configuration.InvalidConfigurationException;
import jecoli.algorithm.components.evaluationfunction.IEvaluationFunction;
import jecoli.algorithm.components.operator.container.ReproductionOperatorContainer;
import jecoli.algorithm.components.operator.reproduction.permutation.PermutationEdgeCrossover;
import jecoli.algorithm.components.operator.reproduction.permutation.PermutationNonAdjacentSwapMutation;
import jecoli.algorithm.components.operator.selection.TournamentSelection;
import jecoli.algorithm.components.randomnumbergenerator.DefaultRandomNumberGenerator;
import jecoli.algorithm.components.representation.permutations.PermutationRepresentation;
import jecoli.algorithm.components.representation.permutations.PermutationRepresentationFactory;
import jecoli.algorithm.components.solution.ISolution;
import jecoli.algorithm.components.solution.SolutionCellContainer;
import jecoli.algorithm.components.statistics.StatisticTypeMask;
import jecoli.algorithm.components.statistics.StatisticsConfiguration;
import jecoli.algorithm.components.terminationcriteria.ITerminationCriteria;
import jecoli.algorithm.components.terminationcriteria.IterationTerminationCriteria;
import jecoli.algorithm.singleobjective.evolutionary.EvolutionaryAlgorithm;
import jecoli.algorithm.singleobjective.evolutionary.EvolutionaryConfiguration;
import jecoli.algorithm.singleobjective.evolutionary.RecombinationParameters;
import jecoli.test.tsp.eatsp.EATsp;
import jecoli.test.tsp.eatsp.TspEvaluationFunction;
import jecoli.test.tsp.eatsp.TspGreedyCrossover;
import jecoli.test.tsp.eatsp.TwoOptLocalOptimizationOperator;
import jecoli.test.tsp.libtsp.EucTsp;
import jecoli.test.tsp.libtsp.Tsp;

public class EAVrp2 {

	/** The problem instance. */
	VRP problemInstance;
	
	/** The algorithm. */
	IAlgorithm<PermutationRepresentation> algorithm;
	
	/** The results. */
	IAlgorithmResult<PermutationRepresentation> results;
	
	/** The statistics. */
	IAlgorithmStatistics<PermutationRepresentation> statistics;
	
	/**
	 * Instantiates a new eA tsp.
	 * 
	 * @param problemInstance the problem instance
	 */
	public EAVrp2(VRP problemInstance)
	{
		this.problemInstance = problemInstance;
	}
	

	/**
	 * Configure ea.
	 * 
	 * @param populationSize the population size
	 * @param numberGenerations the number generations
	 * 
	 * @throws Exception the exception
	 * @throws InvalidConfigurationException the invalid configuration exception
	 */
	public void configureEA (int populationSize, int numberGenerations) throws Exception, InvalidConfigurationException
	{
		EvolutionaryConfiguration<PermutationRepresentation,PermutationRepresentationFactory> configuration = new EvolutionaryConfiguration<PermutationRepresentation,PermutationRepresentationFactory>();
				
		IEvaluationFunction<PermutationRepresentation> evaluationFunction = new VRPEvaluationFunction2(problemInstance);
		configuration.setEvaluationFunction(evaluationFunction);
		
		int solutionSize = problemInstance.getNumberClients()-1;
		System.out.println(solutionSize);
		
		PermutationRepresentationFactory solutionFactory = new PermutationRepresentationFactory(solutionSize);
		configuration.setSolutionFactory(solutionFactory);

		configuration.setPopulationSize(populationSize);
		ITerminationCriteria terminationCriteria = new IterationTerminationCriteria(numberGenerations);
		configuration.setTerminationCriteria(terminationCriteria);
		
		configuration.setRandomNumberGenerator(new DefaultRandomNumberGenerator());
		configuration.setProblemBaseDirectory("nullDirectory");
		configuration.setAlgorithmStateFile("nullFile");
		configuration.setSaveAlgorithmStateDirectoryPath("nullDirectory");
		configuration.setAlgorithmResultWriterList(new ArrayList<IAlgorithmResultWriter<PermutationRepresentation>>());
		configuration.setStatisticsConfiguration(new StatisticsConfiguration());
		
		RecombinationParameters recombinationParameters = new RecombinationParameters(populationSize);
		
		configuration.setRecombinationParameters(recombinationParameters);
		
		configuration.setSelectionOperators(new TournamentSelection<PermutationRepresentation>(1,2));
		
		ReproductionOperatorContainer operatorContainer = new ReproductionOperatorContainer();
		operatorContainer.addOperator(0.5, new PermutationEdgeCrossover());
//		operatorContainer.addOperator(0.5, new TspGreedyCrossover(problemInstance));		
		operatorContainer.addOperator(0.5, new PermutationNonAdjacentSwapMutation());
//		operatorContainer.addOperator(0.5, new TwoOptLocalOptimizationOperator((PermutationRepresentationFactory)solutionFactory,problemInstance,true));
		configuration.setReproductionOperatorContainer(operatorContainer);
		
		this.algorithm = new EvolutionaryAlgorithm<PermutationRepresentation,PermutationRepresentationFactory>(configuration);

	}
	
	
	/**
	 * Run.
	 * 
	 * @throws Exception the exception
	 */
	public double run() throws Exception
	{
		results =  algorithm.run();
		statistics = results.getAlgorithmStatistics();
		
		return results.getSolutionContainer().getBestSolutionCellContainer(true).getSolution().getScalarFitnessValue();
	}
	


	
	
	/**
	 * Gets the best solution.
	 * 
	 * @return the best solution
	 */
	public int[][] getBestSolution()
	{
		SolutionCellContainer<PermutationRepresentation> container = results.getSolutionContainer().getBestSolutionCellContainer(false);
		
		ISolution<PermutationRepresentation> solution = container.getSolution();
		
		int [] gn= ((PermutationRepresentation)solution.getRepresentation()).getGenomeAsArray();
	//System.out.println(((PermutationRepresentation)solution.getRepresentation()).stringRepresentation());
		
		
		
	return problemInstance.routes_from_order (gn,true);
	}
	
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		//int numberOfRuns = 30;
		//for(int i=0;i<numberOfRuns;i++)
		//{

			try 
			{
				//FileWriter fw = new FileWriter("/Users/emanuel/Documents/optfluxWorkspace/JecoFinalVersion/results/TspResults2.0.txt",true);
				//BufferedWriter w = new BufferedWriter(fw);
				
				VRP problemInstance  = new VRP("2pp.vrp","2pp.cit");
				//problemInstance.setNumberVehicles(1);
				
				
				EAVrp2 ea = new EAVrp2(problemInstance);
				
				int populationSize = 200;
				int numberGenerations = 2000;
				
				ea.configureEA(populationSize, numberGenerations);
				
				double result = ea.run();
				System.out.println(result);
				
				
				
				// sacar aqui a melhor solução .... !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				int [][] bestSolution = ea.getBestSolution();
				
				System.out.println("Best solution:");
				problemInstance.print_routes(bestSolution);
				
				//fw.append(result+"\n");
				
				//fw.close();
				//w.close();
				
			} 
			catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			} 
		}
		
	//}

}

	
	

