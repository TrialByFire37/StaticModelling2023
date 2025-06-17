import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.jfree.chart.plot.PlotOrientation.VERTICAL;

public class Individual {
  public static void indiv(int N, int n, double p) throws IOException {
    int[] binArr = Tools.IRNBIN(N, n, p);
    int intervals = (int) (1 + Math.log(binArr.length) / Math.log(2));

    int min = Arrays.stream(binArr).min().getAsInt();
    int max = Arrays.stream(binArr).max().getAsInt();

    double intervalSize = (double) (max - min) / intervals;

    double[] expectedFreq = new double[intervals];
    for (int i = 0; i < intervals; i++) {
      double lowerBound = min + i * intervalSize;
      double upperBound = lowerBound + intervalSize;
      expectedFreq[i] = binomCDF(upperBound, n, p) - binomCDF(lowerBound, n, p);
      expectedFreq[i] *= N;
    }

    HistogramDataset dataset = new HistogramDataset();
    double[] binArrDouble = Arrays.stream(binArr).asDoubleStream().toArray();
    dataset.addSeries("Histogram Graph", binArrDouble, intervals);
    JFreeChart histogram = ChartFactory.createHistogram(
            "Histogram Chart",
            "X",
            "Frequency",
            dataset,
            VERTICAL,
            false,
            false,
            false
    );
    File histogramChart = new File("./src/main/graphs/indivHistogramChart.png");
    ChartUtils.saveChartAsPNG(histogramChart, histogram, 640, 480);

    int[] observedFreq = new int[intervals];
    for (int i = 0; i < binArr.length; i++) {
      int intervalIndex = (int) Math.floor((binArr[i] - min) / intervalSize);
      if (intervalIndex < 0) {
        intervalIndex = 0;
      } else if (intervalIndex >= intervals) {
        intervalIndex = intervals - 1;
      }
      observedFreq[intervalIndex]++;
    }

    for (int i = 0; i < intervals; i++) {
      System.out.printf("Interval %d: Observed = %d, Expected = %.2f%n", i, observedFreq[i], expectedFreq[i]);
    }

    double chiSquared = 0;
    for (int i = 0; i < intervals; i++) {
      double deviation = observedFreq[i] - expectedFreq[i];
      chiSquared += deviation * deviation / expectedFreq[i];
    }
    int df = intervals - 1;

    System.out.printf("%nChi-squared = %.2f%n", chiSquared);
    System.out.printf("Degrees of freedom = %d%n", df);
  }

  private static double binomCDF(double k, int n, double p) {
    double sum = 0;
    for (int i = 0; i <= k; i++) {
      sum += binomP(i, n, p);
    }
    return sum;
  }

  private static double binomP(int r, int n, double p) {
    return binomCoef(n, r) * Math.pow(p, r) * Math.pow(1 - p, n - r);
  }

  private static long binomCoef(int n, int k) {
    long result = 1;
    for (int i = 1; i <= k; i++) {
      result *= n - k + i;
      result /= i;
    }
    return result;
  }
}
