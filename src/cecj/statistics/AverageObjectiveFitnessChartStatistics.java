package cecj.statistics;

import ec.EvolutionState;
import ec.display.chart.XYSeriesChartStatistics;
import ec.util.Parameter;

public class AverageObjectiveFitnessChartStatistics extends XYSeriesChartStatistics {

	private static final String P_POP = "pop";
	private static final String P_SIZE = "subpops";
	private static final String P_FREQUENCY = "frequency";
	private static final String P_FITNESS_CALCULATOR = "fitness-calc";

	private int numSubpopulations;
	private int[] seriesID;
	private int frequency;

	private ObjectiveFitnessCalculator fitnessCalc;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter fitnessCalcParameter = base.push(P_FITNESS_CALCULATOR);
		fitnessCalc = (ObjectiveFitnessCalculator) state.parameters
			.getInstanceForParameter(fitnessCalcParameter, null, ObjectiveFitnessCalculator.class);
		fitnessCalc.setup(state, fitnessCalcParameter);

		Parameter frequencyParam = base.push(P_FREQUENCY);
		frequency = state.parameters.getIntWithDefault(frequencyParam, null, 1);

		Parameter popSizeParameter = new Parameter(P_POP).push(P_SIZE);
		numSubpopulations = state.parameters.getInt(popSizeParameter, null, 0);
		seriesID = new int[numSubpopulations];
		for (int i = 0; i < numSubpopulations; ++i) {
			seriesID[i] = addSeries("SubPop " + i);
		}
	}

	@Override
	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);

		if (state.generation % frequency != 0) {
			return;
		}

		for (int subPop = 0; subPop < numSubpopulations; ++subPop) {
			double averageFitness = 0;
			for (int i = 0; i < state.population.subpops[subPop].individuals.length; ++i) {
				averageFitness += fitnessCalc
					.calculateObjectiveFitness(state,
												state.population.subpops[subPop].individuals[i]);
			}
			averageFitness /= state.population.subpops[subPop].individuals.length;

			addDataPoint(seriesID[subPop], state.generation, averageFitness);
		}
	}
}
