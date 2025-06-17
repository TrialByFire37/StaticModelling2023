import dnl.utils.text.table.TextTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartUtils;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.jfree.chart.plot.PlotOrientation.VERTICAL;

public class Operations {
  private static double kSumFunc(double[] uArr, int n, double M, int f) {
    return IntStream.range(0, n - f).mapToDouble(k -> (uArr[k] - M) * (uArr[k + f] - M)).sum();
  }

  public static void statsBatch(int[] nArr) throws IOException {
    int counter = 0;
    int unit = 1;
    DecimalFormat frm = new DecimalFormat("#0.000000");
    String[] colNamesMain = {"n", "M (wait.)", "Expected M", "M Error", "D (disp.)",
            "Expected D", "D Error"};
    Object[][] colDataMain = new Object[nArr.length][colNamesMain.length];

    for (int i: nArr) {
      double[] uArr = new double[i];
      double[] arrDispNonFinal = new double[i];

      uArr = Arrays.stream(uArr).map(k -> ThreadLocalRandom.current().nextDouble(0, 1)).toArray();
      double M = Arrays.stream(uArr).sum() / i;

      double[] finalUArr = uArr;
      IntStream.range(0, i).forEach(j -> arrDispNonFinal[j] = Math.pow(finalUArr[j] - M, 2));
      double D = Arrays.stream(arrDispNonFinal).sum() / i;

      double[] kArr = IntStream.range(1, i+1).mapToDouble(f -> kSumFunc(finalUArr, i, M, f) / kSumFunc(finalUArr, i, M, 0)).toArray();

      double delta = 1. / 100;
      double[] fArr = IntStream.range(1, 101)
              .mapToDouble(j -> {
                double x = j * delta;
                double xPrev = (j - 1) * delta;
                return Arrays.stream(finalUArr).filter(k -> (k < x && k >= xPrev)).count() / ((double) i * delta);
              })
              .toArray();

      double[] FArr = IntStream.range(1, 101)
              .mapToDouble(j -> {
                double x = 0.01 * j;
                return Arrays.stream(finalUArr).filter(k -> (k < x)).count() / (double) i;
              })
              .toArray();

      colDataMain[counter][0] = i;
      colDataMain[counter][1] = frm.format(M);
      colDataMain[counter][2] = 0.5;
      colDataMain[counter][3] = frm.format(M - 0.5);
      colDataMain[counter][4] = frm.format(D);
      colDataMain[counter][5] = 0.08333;
      colDataMain[counter][6] = frm.format(D - 0.08333);
      counter++;

      XYSeries kGraph = new XYSeries("Correlation Graph");
      double[] finalKArr = kArr;
      IntStream.range(0,i).forEach(j -> kGraph.add(j, finalKArr[j]));
      XYSeriesCollection kDataset = new XYSeriesCollection();
      kDataset.addSeries(kGraph);
      JFreeChart kChart = ChartFactory.createXYLineChart(
              "Correlation Chart (n = " + i + ")",
              "f",
              "K(f)",
              kDataset
              );
      XYPlot kPlot = (XYPlot) kChart.getPlot();
      NumberAxis kXAxis = (NumberAxis) kPlot.getDomainAxis();
      kXAxis.setTickUnit(new NumberTickUnit(unit));
      unit *= 10;
      File KXYChart = new File("./src/main/graphs/KXYLineChart" + i + ".png");
      ChartUtils.saveChartAsPNG(KXYChart, kChart, 640, 480);

      XYSeriesCollection fDataset = new XYSeriesCollection();
      XYSeries fSeries = new XYSeries("");
      IntStream.range(0,100).forEach(j -> fSeries.add(j, fArr[j]));
      fDataset.addSeries(fSeries);
      JFreeChart fHistogram = ChartFactory.createHistogram(
              "f(x) Chart (n = " + i + ")",
              null,
              "f(x)",
              fDataset,
              VERTICAL,
              false,
              false,
              false
      );
      File fHistogramChart = new File("./src/main/graphs/fHistogramChart" + i + ".png");
      ChartUtils.saveChartAsPNG(fHistogramChart, fHistogram, 640, 480);

      XYSeries FGraph = new XYSeries("F(x) Graph");
      IntStream.range(0,100).forEach(j -> FGraph.add(j, FArr[j]));
      XYSeriesCollection FDataset = new XYSeriesCollection();
      FDataset.addSeries(FGraph);
      JFreeChart FChart = ChartFactory.createXYLineChart(
              "F(x) Chart (n = " + i + ")",
              null,
              "F(x)",
              FDataset
      );
      File FXYChart = new File("./src/main/graphs/FXYLineChart" + i + ".png");
      ChartUtils.saveChartAsPNG(FXYChart, FChart, 640, 480);
    }

    TextTable ttMain = new TextTable(colNamesMain, colDataMain);
    ttMain.printTable();
  }
}
