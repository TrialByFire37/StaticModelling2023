import dnl.utils.text.table.TextTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static org.jfree.chart.plot.PlotOrientation.VERTICAL;

public class Tools {
  public static double M(double[] uArr) {
    return Arrays.stream(uArr)
            .average()
            .orElse(0);
  }

  public static double D(double[] uArr, double M) {
    return Arrays.stream(uArr)
            .map(num -> Math.pow((num - M), 2))
            .average()
            .orElse(0);
  }

  private static double[] func(double[] uArr, char flag, String dist) {
    int intervals = (int) (1 + Math.log(uArr.length) / Math.log(2));
    double min = Arrays.stream(uArr).min().getAsDouble();
    double max = Arrays.stream(uArr).max().getAsDouble();

    double delta = (max - min) / intervals;
    delta = delta < 1 ? 1. : delta > 1 ? Math.round(delta * 10) / 10.0 : delta;

    double first = Objects.equals(dist, "RNSTUD") || Objects.equals(dist, "RNNRM") ? (-intervals) / 2. * delta :
            (max - min) / 2. - intervals/2. * delta;
    double[] func = new double[intervals];

    if (flag == 'f') {
      for (int i = 1; i <= intervals; i++) {
        double x = first + delta * i;
        double xPrev = first + delta * (i - 1);
        func[i - 1] = Arrays.stream(uArr).filter(k -> (k < x && k >= xPrev)).count() / ((double) uArr.length * delta);
      }
    }
    else if (flag == 'F') {
      for (int i = 1; i <= intervals; i++) {
        double x = first + delta * i;
        func[i - 1] = Arrays.stream(uArr).filter(k -> (k < x)).count() / (double) uArr.length;
      }
    } else {
      throw new IllegalArgumentException("Flag must be either \"F\" or \"f\" only.");
    }

    return func;
  }

  public static void statPrint(ArrayList<double[]> list, String flag) throws IOException {
    // tables and main values
    String[] colNamesMain;
    DecimalFormat frm = new DecimalFormat("#0.000000");
    colNamesMain = switch (flag) {
      case "RNUNI" -> new String[]{"Estimation", "RNUNI", "Theoretical value", "Error"};
      case "RNNRM" -> new String[]{"Moment", "RNNRM1", "RNNRM2", "Theoretical value", "Error 1", "Error 2"};
      case "RNEXP" -> new String[]{"Moment", "RNEXP", "Theoretical value", "Error"};
      case "RNCHIS" -> new String[]{"Moment", "RNCHIS", "Theoretical value", "Error"};
      case "RNSTUD" -> new String[]{"Moment", "RNSTUD", "Theoretical value", "Error"};
      default -> new String[]{};
    };
    Object[][] colDataMain = new Object[2][colNamesMain.length];
    colDataMain[0][0] = "E";
    colDataMain[1][0] = "V";

    int k = 0;
    ArrayList<Double> MList = new ArrayList<>();
    ArrayList<Double> DList = new ArrayList<>();
    ArrayList<double[]> fList = new ArrayList<>();
    ArrayList<double[]> FList = new ArrayList<>();
    for (double[] uArr: list) {
      MList.add(M(uArr));
      DList.add(D(uArr, M(uArr)));
      fList.add(func(uArr, 'f', flag));
      FList.add(func(uArr, 'F', flag));
      k++;
      colDataMain[0][k] = frm.format(MList.get(k-1));
      colDataMain[1][k] = frm.format(DList.get(k-1));
    }

    switch (flag) {
      case "RNUNI" -> {
        colDataMain[0][k + 1] = 50.5;
        colDataMain[1][k + 1] = 833.25;
        colDataMain[0][k + 2] = frm.format(MList.get(k-1) - 50.5);
        colDataMain[1][k + 2] = frm.format(DList.get(k-1) - 833.25);
      }
      case "RNNRM" -> {
        colDataMain[0][k + 1] = 0.;
        colDataMain[1][k + 1] = 1.0;
        colDataMain[0][k + 2] = frm.format(MList.get(k-2) - 0.);
        colDataMain[1][k + 2] = frm.format(DList.get(k-2) - 1.0);
        colDataMain[0][k + 3] = frm.format(MList.get(k-1) - 0.);
        colDataMain[1][k + 3] = frm.format(DList.get(k-1) - 1.0);

      }
      case "RNEXP" -> {
        colDataMain[0][k + 1] = 1.0;
        colDataMain[1][k + 1] = 1.0;
        colDataMain[0][k + 2] = frm.format(MList.get(k-1) - 1.0);
        colDataMain[1][k + 2] = frm.format(DList.get(k-1) - 1.0);
      }
      case "RNCHIS" -> {
        colDataMain[0][k + 1] = 10.0;
        colDataMain[1][k + 1] = 20.0;
        colDataMain[0][k + 2] = frm.format(MList.get(k-1) - 10.0);
        colDataMain[1][k + 2] = frm.format(DList.get(k-1) - 20.0);
      }
      case "RNSTUD" -> {
        colDataMain[0][k + 1] = 0.0;
        colDataMain[1][k + 1] = 1.25;
        colDataMain[0][k + 2] = frm.format(MList.get(k-1) - 0.0);
        colDataMain[1][k + 2] = frm.format(DList.get(k-1) - 1.25);
      }
    }

    TextTable ttMain = new TextTable(colNamesMain, colDataMain);
    ttMain.printTable();

    //graphs
    int counter = 1;
    for (double[] f: fList) {
      XYSeriesCollection fDataset = new XYSeriesCollection();
      XYSeries fSeries = new XYSeries("f(x) Graph");
      IntStream.range(0, f.length).forEach(j -> fSeries.add(j, f[j]));
      fDataset.addSeries(fSeries);
      JFreeChart fHistogram = ChartFactory.createHistogram(
              "f(x) Chart (N = " + Constants.N + ")",
              null,
              "f(x)",
              fDataset,
              VERTICAL,
              false,
              false,
              false
      );
      File fHistogramChart = flag.equals("RNNRM") ?
              new File("./src/main/graphs/fHistogramChart" + Constants.N + flag + "_" + counter + ".png") :
              new File("./src/main/graphs/fHistogramChart" + Constants.N + flag + ".png");
      ChartUtils.saveChartAsPNG(fHistogramChart, fHistogram, 640, 480);
      counter++;
    }

    counter = 1;
    for (double[] F: FList) {
      XYSeriesCollection FDataset = new XYSeriesCollection();
      XYSeries FSeries = new XYSeries("F(x) Graph");
      IntStream.range(0, F.length).forEach(j -> FSeries.add(j, F[j]));
      FDataset.addSeries(FSeries);
      JFreeChart FChart = ChartFactory.createXYLineChart(
              "F(x) Chart (n = " + Constants.N + ")",
              null,
              "F(x)",
              FDataset
      );
      File FXYChart = flag.equals("RNNRM") ?
              new File("./src/main/graphs/FXYLineChart" + Constants.N + flag + "_" + counter + ".png") :
              new File("./src/main/graphs/FXYLineChart" + Constants.N + flag + ".png");
      ChartUtils.saveChartAsPNG(FXYChart, FChart, 640, 480);
      counter++;
    }
  }

  public static double[] RNUNI(int N, double a, double b) {
    double[] uniArr = new double[N];
    for (int i = 0; i < N; i++) {
      uniArr[i] = (b-a) * ThreadLocalRandom.current().nextDouble() + a;
    }
    return uniArr;
  }

  public static double[] RNNRM1(int N) {
    double[] nrm1Arr = new double[N];
    for (int i = 0; i < N; i++) {
      double u1 = ThreadLocalRandom.current().nextDouble();
      double u2 = ThreadLocalRandom.current().nextDouble();
      nrm1Arr[i] = Math.sqrt(-2 * Math.log(u2)) * Math.cos(2 * Math.PI * u1); 
    }
    return nrm1Arr;
  }

  public static double[] RNNRM2(int N) {
    double[] nrm2Arr = new double[N];
    for (int i = 0; i < N; i++) {
      int n = 12; // number of uniformly distributed random numbers
      double sum = DoubleStream
              .generate(ThreadLocalRandom.current()::nextDouble)
              .limit(n)
              .sum();
      nrm2Arr[i] = sum - n / 2.0; 
    }
    return nrm2Arr;
  }

  public static double[] RNEXP(int N, double beta) {
    double[] expArr = new double[N];
    for (int i = 0; i < N; i++) {
      expArr[i] = -beta * Math.log(ThreadLocalRandom.current().nextDouble());
    }
    return expArr;
  }

  public static double RNCHIS(int n) {
    double sum = 0;
    for (int j = 0; j < n; j++) {
      double z = ThreadLocalRandom.current().nextGaussian();
      double z_squared = z * z;
      sum += z_squared;
    }
    return sum;
  }


  public static double[] generRNCHIS(int N, int n) {
    double[] chisArr = new double[N];
    for (int i = 0; i < N; i++) {
      chisArr[i] = RNCHIS(n);
    }
    return chisArr;
  }

  public static double[] RNSTUD(int N, int n) {
    double[] studArr = new double[N];
    for (int i = 0; i < N; i++) {
      studArr[i] = ThreadLocalRandom.current().nextGaussian() / Math.sqrt(RNCHIS(n) / n);
    }
    return studArr;
  }
}
