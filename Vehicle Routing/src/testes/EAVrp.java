package testes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import jecoli.algorithm.components.algorithm.IAlgorithmResult;
import jecoli.algorithm.components.algorithm.IAlgorithmStatistics;
import jecoli.algorithm.components.algorithm.writer.IAlgorithmResultWriter;
import jecoli.algorithm.components.configuration.InvalidConfigurationException;
import jecoli.algorithm.components.evaluationfunction.IEvaluationFunction;
import jecoli.algorithm.components.operator.container.ReproductionOperatorContainer;
import jecoli.algorithm.components.operator.reproduction.permutation.PermutationNonAdjacentSwapMutation;
import jecoli.algorithm.components.operator.reproduction.permutation.PermutationOnePtCrossover;
import jecoli.algorithm.components.operator.selection.RankingSelection;
import jecoli.algorithm.components.operator.selection.TournamentSelection;
import jecoli.algorithm.components.randomnumbergenerator.DefaultRandomNumberGenerator;
import jecoli.algorithm.components.randomnumbergenerator.IRandomNumberGenerator;
import jecoli.algorithm.components.representation.IRepresentation;
import jecoli.algorithm.components.representation.linear.ILinearRepresentation;
import jecoli.algorithm.components.representation.permutations.PermutationRepresentation;
import jecoli.algorithm.components.representation.permutations.PermutationRepresentationFactory;
import jecoli.algorithm.components.solution.ISolution;
import jecoli.algorithm.components.solution.ISolutionFactory;
import jecoli.algorithm.components.statistics.StatisticsConfiguration;
import jecoli.algorithm.components.terminationcriteria.ITerminationCriteria;
import jecoli.algorithm.components.terminationcriteria.IterationTerminationCriteria;
import jecoli.algorithm.singleobjective.evolutionary.EvolutionaryAlgorithm;
import jecoli.algorithm.singleobjective.evolutionary.EvolutionaryConfiguration;
import jecoli.algorithm.singleobjective.evolutionary.RecombinationParameters;
import jecoli.test.knapsacking.EAKnapsacking;
import jecoli.test.knapsacking.Knapsacking;
import jecoli.test.knapsacking.KnapsackingBinaryEvaluationFunction;
import jecoli.test.knapsacking.KnapsackingCorrectionEvaluation;
import jecoli.test.knapsacking.KnapsackingRepresentationType;


public class EAVrp {
	VRP problemInstance;
	
	/** The algorithm. */
	EvolutionaryAlgorithm algorithm;
	
	
	/** The results. */
	IAlgorithmResult results;
	
	/** The statistics. */
	IAlgorithmStatistics statistics;
	
	IRandomNumberGenerator randomGenerator = new DefaultRandomNumberGenerator();
	
	
	/**
	 * Instantiates a new eA knapsacking.
	 * 
	 * @param problemInstance the problem instance
	 */
	public EAVrp(VRP problemInstance)
	{
		this.problemInstance = problemInstance;
	}
	
	
	public void configureEvolutionaryAlgorithm(int populationSize, int numberGenerations) 
			throws Exception, InvalidConfigurationException
		{
			IEvaluationFunction<?> evaluationFunction = null;
			
			ISolutionFactory solutionFactory = null;
			//	ATENÇÃO AQUI
			//ISolution<IRepresentation> solution=null;
			
			
			ReproductionOperatorContainer operatorContainer = new ReproductionOperatorContainer();
			
			
			int solutionSize = (problemInstance.getNumberClients()+problemInstance.getNumberVehicles()-1);
			
			// PERMUTAÇÕES
			
				evaluationFunction = new VRPEvaluationFunction(problemInstance);
				
				// REPRESENTAÇÃO COM FACTORY
				solutionFactory = new PermutationRepresentationFactory(solutionSize);
			
				//	REPRESENTAÇÃO SEM FACTORY
				//List<Integer> l =problemInstance.sol_asList(problemInstance.getSol());
				//solution = (ISolution<IRepresentation>) new PermutationRepresentation(l);
				
				operatorContainer.addOperator(0.5,new PermutationOnePtCrossover());
				operatorContainer.addOperator(0.5,new PermutationNonAdjacentSwapMutation());	
			
			

			EvolutionaryConfiguration configuration = new EvolutionaryConfiguration();
			configuration.setEvaluationFunction(evaluationFunction);
			
			
			configuration.setSolutionFactory(solutionFactory);
			
			
			configuration.setReproductionOperatorContainer(operatorContainer);
			
			configuration.setPopulationSize(populationSize);
			
			RecombinationParameters recombinationParameters = new RecombinationParameters(populationSize);
			configuration.setRecombinationParameters(recombinationParameters);
			
			configuration.setRandomNumberGenerator(randomGenerator);
			configuration.setProblemBaseDirectory("nullDirectory");
			configuration.setAlgorithmStateFile("nullFile");
			configuration.setSaveAlgorithmStateDirectoryPath("nullDirectory");
			configuration.setAlgorithmResultWriterList(new ArrayList<IAlgorithmResultWriter<IRepresentation>>());
			configuration.setStatisticsConfiguration(new StatisticsConfiguration());
		
			
			
			// BEST SELECTION
			//configuration.setSelectionOperators(new BestSelection<IRepresentation>());
			
			// POR ROLLET
			//configuration.setSelectionOperators(new RouletteWheelSelection<IRepresentation>());
			
			// 	POR TORNEIO
			configuration.setSelectionOperators(new TournamentSelection<IRepresentation>(1,2));
			configuration.setSurvivorSelectionOperator(new TournamentSelection<IRepresentation>(1,2));
			
			// POR RANKING
			//configuration.setSelectionOperators(new RankingSelection());
			
			
			ITerminationCriteria terminationCriteria = new IterationTerminationCriteria(numberGenerations);
			configuration.setTerminationCriteria(terminationCriteria);
			
			algorithm = new EvolutionaryAlgorithm<IRepresentation, ISolutionFactory<IRepresentation>>(configuration);
		}
		

	/**
	 * Gets the best solution.
	 * 
	 * @return the best solution
	 */
	public int[][] getBestSolution ()
	{
		ISolution bestSolution = results.getSolutionContainer().getBestSolutionCellContainer(true).getSolution();
		
		System.out.println("fitness:" + bestSolution.getScalarFitnessValue());
		
		int [][] res = null; 
			PermutationRepresentation genome = (PermutationRepresentation)bestSolution.getRepresentation();
			int [] genomeArray = genome.getGenomeAsArray();
			res = problemInstance.get_routes(genomeArray);
		
		return res;
	}

	/**
	 * Run.
	 * 
	 * @throws Exception the exception
	 */
	public void run() throws Exception
	{
		try 
		{
			FileWriter fw = new FileWriter("/home/apr53/teste.txt",true);
			BufferedWriter w = new BufferedWriter(fw);
			
			results =  algorithm.run();
			statistics = results.getAlgorithmStatistics();
			
			w.append(results.getSolutionContainer().getBestSolutionCellContainer(true).getSolution().getScalarFitnessValue()+"\n");
			
			w.close();
			fw.close();
		}catch (Exception e) {
		}
	}
	
	/**
	 * Prints the stats per iteration.
	 */
	public void printStatsPerIteration ()
	{
		System.out.println("Iteration\tBest\t\tMean\n");
		for(int i=0; i < statistics.getNumberOfIterations(); i+=20)
		{
			System.out.print(i+"\t");
			System.out.print(statistics.getAlgorithmIterationStatisticCell(i).getScalarFitnessCell().getMaxValue()+"\t");
			System.out.print(statistics.getAlgorithmIterationStatisticCell(i).getScalarFitnessCell().getMean()+"\n");
		}	
	}
	
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) 
	{
		
		
		
		
		try {
			
			VRP problemInstance  = new VRP("1pp.vrp","1pp.cit");
			//problemInstance.setNumberVehicles(20);
			
			int populationSize = 400;
			int numberGenerations = 1000;
			
			EAVrp ea = new EAVrp(problemInstance);
			
			ea.configureEvolutionaryAlgorithm(populationSize, numberGenerations);
			ea.run();
			
			// sacar aqui a melhor solução .... !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			int [][] bestSolution = ea.getBestSolution();
			
			System.out.println("Best solution:");
			problemInstance.print_routes(bestSolution);
			
			
		}
		catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
}