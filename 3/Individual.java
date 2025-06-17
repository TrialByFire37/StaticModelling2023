import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.jfree.chart.plot.PlotOrientation.VERTICAL;

public class Individual {
  public static void indiv(int N, double theta, double xi, double signLev) throws IOException {
    double[] cauArr = new double[N];
    for (int i = 0; i < N; i++) {
      cauArr[i] = xi + theta * Math.tan(Math.PI * (ThreadLocalRandom.current().nextDouble() - 0.5));
    }
    Arrays.sort(cauArr);

    System.out.println("Sample: ");
    Arrays.stream(cauArr).forEach(s -> System.out.println(s));

    int intervals = (int) (1 + Math.log(cauArr.length) / Math.log(2));

    HistogramDataset dataset = new HistogramDataset();
    dataset.addSeries("Histogram Graph", cauArr, intervals);
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

    double[] empiricalCDF = new double[N];
    for (int i = 0; i < N; i++) {
      empiricalCDF[i] = (i + 1.0) / N;
    }

    double[] theoreticalCDF = new double[N];
    for (int i = 0; i < N; i++) {
      double x = i / N;
      theoreticalCDF[i] = (1.0 / Math.PI) * Math.atan((x - xi) / theta) + 0.5;
    }

    double D = 0.0;
    for (int i = 0; i < N; i++) {
      double diff = Math.abs(theoreticalCDF[i] - empiricalCDF[i]);
      if (diff > D) {
        D = diff;
      }
    }

    double criticalValue = 1.22;
    double testValue = D / Math.sqrt(N);
    System.out.println();
    System.out.println("D = " + D);
    System.out.println("Critical value = " + criticalValue);
    System.out.println("Test value = " + testValue);
    if (testValue <= criticalValue) {
      System.out.println("Empirical distribution agrees with theoretical distribution.");
    } else {
      System.out.println("Empirical distribution does not agree with theoretical distribution.");
    }
  }
}
