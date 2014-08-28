package eu.socialsensor.benchmarks;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.socialsensor.graphdatabases.GraphDatabase;
import eu.socialsensor.graphdatabases.Neo4jGraphDatabase;
import eu.socialsensor.graphdatabases.OrientGraphDatabase;
import eu.socialsensor.graphdatabases.SparkseeGraphDatabase;
import eu.socialsensor.graphdatabases.TitanGraphDatabase;
import eu.socialsensor.main.GraphDatabaseBenchmark;
import eu.socialsensor.utils.PermuteMethod;
import eu.socialsensor.utils.Utils;

/**
 * MassiveInsertionBenchmark implementation
 * @author sotbeis
 * @email sotbeis@iti.gr
 */

public class MassiveInsertionBenchmark implements Benchmark{
	
	private static int SCENARIOS = 24;
	private final String resultFile = "MIWResults.txt";
	private String datasetDir;
	
	private double[] orientTimes = new double[SCENARIOS];
	private double[] titanTimes = new double[SCENARIOS];
	private double[] neo4jTimes = new double[SCENARIOS];
	private double[] sparkseeTimes = new double[SCENARIOS];
	
	private int titanScenarioCount = 0;
	private int orientScenarioCount = 0;
	private int neo4jScenarioCount = 0;
	private int sparkseeScenarioCount = 0;
	
	private Logger logger = Logger.getLogger(MassiveInsertionBenchmark.class);
	
	public MassiveInsertionBenchmark(String datasetDir) {
		this.datasetDir = datasetDir;
	}
	
	@Override
	public void startBenchmark() {
		logger.setLevel(Level.INFO);
		System.out.println("");
		logger.info("Executing Massive Insertion Benchmark . . . .");
		
		Utils utils = new Utils();
		Class<MassiveInsertionBenchmark> c = MassiveInsertionBenchmark.class;
		Method[] methods = utils.filter(c.getDeclaredMethods(), "MassiveInsertionBenchmark");
		PermuteMethod permutations = new PermuteMethod(methods);
		int cntPermutations = 1;
		while(permutations.hasNext()) {
			System.out.println("");
			logger.info("Scenario " + cntPermutations++);
			for(Method permutation : permutations.next()) {
				try {
					permutation.invoke(this, null);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		System.out.println("");
		logger.info("Single Insertion Benchmark finished");
		
		double meanOrientTime = utils.calculateMean(orientTimes);
		double meanTitanTime = utils.calculateMean(titanTimes);
		double meanNeo4jTime = utils.calculateMean(neo4jTimes);
		double meanSparkseeTime = utils.calculateMean(sparkseeTimes);
		
		double varOrientTime = utils.calculateVariance(meanOrientTime, orientTimes);
		double varTitanTime = utils.calculateVariance(meanTitanTime, titanTimes);
		double varNeo4jTime = utils.calculateVariance(meanNeo4jTime, neo4jTimes);
		double varSparkseeTime = utils.calculateVariance(meanSparkseeTime, sparkseeTimes);
		
		double stdOrientTime = utils.calculateStdDeviation(varOrientTime);
		double stdTitanTime = utils.calculateStdDeviation(varTitanTime);
		double stdNeo4jTime = utils.calculateStdDeviation(varNeo4jTime);
		double stdSparkseeTime = utils.calculateStdDeviation(varSparkseeTime);
		
		
		String resultsFolder = GraphDatabaseBenchmark.inputPropertiesFile.getProperty("RESULTS_PATH");
		String output = resultsFolder+resultFile;
		System.out.println("");
		logger.info("Write results to "+output);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(output));
			out.write("########################################################");
			out.write("\n");
			out.write("######### Massive Insertion Benchmark Results ##########");
			out.write("\n");
			out.write("########################################################");
			out.write("\n");
			out.write("\n");
			out.write("OrientDB execution time");
			out.write("\n");
			out.write("Mean Value: " + meanOrientTime);
			out.write("\n");
			out.write("STD Value: " + stdOrientTime);
			out.write("\n");
			out.write("\n");
			out.write("Titan execution time");
			out.write("\n");
			out.write("Mean Value: " + meanTitanTime);
			out.write("\n");
			out.write("STD Value: " + stdTitanTime);
			out.write("\n");
			out.write("\n");
			out.write("Neo4j execution time");
			out.write("\n");
			out.write("Mean Value: " + meanNeo4jTime);
			out.write("\n");
			out.write("STD Value: " + stdNeo4jTime);
			out.write("\n");
			out.write("\n");
			out.write("Sparksee execution time");
			out.write("\n");
			out.write("Mean Value: " + meanSparkseeTime);
			out.write("\n");
			out.write("STD Value: " + stdSparkseeTime);
			out.write("\n");
			out.write("########################################################");
			
			out.flush();
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void orientMassiveInsertionBenchmark() {
		GraphDatabase orientGraphDatabase = new OrientGraphDatabase();
		orientGraphDatabase.createGraphForMassiveLoad(GraphDatabaseBenchmark.ORIENTDB_PATH);
		long start = System.currentTimeMillis();
		orientGraphDatabase.massiveModeLoading(datasetDir);
		long orientTime = System.currentTimeMillis() - start;
		orientGraphDatabase.shutdownMassiveGraph();
		orientGraphDatabase.delete(GraphDatabaseBenchmark.ORIENTDB_PATH);
		orientTimes[orientScenarioCount] = orientTime / 1000.0;
		orientScenarioCount++;
	}

	@SuppressWarnings("unused")
	private void titanMassiveInsertionBenchmark() {
		GraphDatabase titanGraphDatabase = new TitanGraphDatabase();
		titanGraphDatabase.createGraphForMassiveLoad(GraphDatabaseBenchmark.TITANDB_PATH);
		long start = System.currentTimeMillis();
		titanGraphDatabase.massiveModeLoading(datasetDir);
		long titanTime = System.currentTimeMillis() - start;
		titanGraphDatabase.shutdownMassiveGraph();
		titanGraphDatabase.delete(GraphDatabaseBenchmark.TITANDB_PATH);
		titanTimes[titanScenarioCount] = titanTime / 1000.0;
		titanScenarioCount++;
	}
	
	@SuppressWarnings("unused")
	private void neo4jMassiveInsertionBenchmark() {
		GraphDatabase neo4jGraphDatabase = new Neo4jGraphDatabase();
		neo4jGraphDatabase.createGraphForMassiveLoad(GraphDatabaseBenchmark.NEO4JDB_PATH);
		long start = System.currentTimeMillis();
		neo4jGraphDatabase.massiveModeLoading(datasetDir);
		long neo4jTime = System.currentTimeMillis() - start;
		neo4jGraphDatabase.shutdownMassiveGraph();
		neo4jGraphDatabase.delete(GraphDatabaseBenchmark.NEO4JDB_PATH);
		neo4jTimes[neo4jScenarioCount] = neo4jTime / 1000.0;
		neo4jScenarioCount++;
	}
	
	@SuppressWarnings("unused")
	private void sparkseeMassiveInsertionBenchmark() {
		GraphDatabase sparkseeGraphDatabase = new SparkseeGraphDatabase();
		sparkseeGraphDatabase.createGraphForMassiveLoad(GraphDatabaseBenchmark.SPARKSEEDB_PATH);
		long start = System.currentTimeMillis();
		sparkseeGraphDatabase.massiveModeLoading(datasetDir);
		long sparkseeTime = System.currentTimeMillis() - start;
		sparkseeGraphDatabase.shutdownMassiveGraph();
		sparkseeGraphDatabase.delete(GraphDatabaseBenchmark.SPARKSEEDB_PATH);
		sparkseeTimes[sparkseeScenarioCount] = sparkseeTime / 1000.0;
		sparkseeScenarioCount++;
	}
	
}
