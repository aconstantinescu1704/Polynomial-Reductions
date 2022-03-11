import javax.swing.plaf.synth.SynthDesktopIconUI;
import java.io.*;

class Reclame extends Task{
    private int nodes;
    private int edges;
    private int [][]graph;
    private String truthValue;
    private int k;

    public Reclame() {
        graph = new int[100][100];
        k = 2;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Reclame reclame = new Reclame();
        reclame.solve();
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        while (true) {
            formulateOracleQuestion();
            askOracle();
            decipherOracleAnswer();
            if (truthValue.equals("True")) {
                writeAnswer();
                break;
            } else {
                k++;
            }
        }
    }

    @Override
    public void readProblemData() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String[] input = reader.readLine().split(" ");
        nodes = Integer.parseInt(input[0]);
        edges = Integer.parseInt(input[1]);

        for (int i = 1; i <= nodes; i++) {
            for (int j = 1; j <= nodes; j++) {
                graph[i][j] = 0;
            }
        }

        for (int i = 0; i < edges; i++) {
            String[] input2 = reader.readLine().split(" ");
            int u = Integer.parseInt(input2[0]);
            int v = Integer.parseInt(input2[1]);
            graph[u][v] = 1;
            graph[v][u] = 1;
        }

        reader.close();

    }

    public int generateIndex(int i, int v) {
        return k * (i - 1) + v;
    }

    public int decodifIndex(int index) {
        if(index % k == 0)
            return index / k;
        return index / k + 1;
    }


    @Override
    public void formulateOracleQuestion() throws IOException {
        File obj = new File("sat.cnf");
        FileWriter myWriter = new FileWriter("sat.cnf");
        int nrVariables = k * nodes;
        int nrClauses = k + nodes * k * (k - 1) / 2 + edges + k * nodes * (nodes - 1) / 2;

        // restriction a
        // these clauses force that there is at least one node for each element of the cover
        myWriter.write("p cnf " + nrVariables + " " + nrClauses + "\n");
        for (int i = 1; i <= k; i++) {
            for (int j = 1; j <= nodes; j++) {
                myWriter.write((generateIndex(j, i) + " "));
            }
            myWriter.write((0 + "\n"));
        }

        // restriction b
        // these clauses force that any node is not considered on more than one position in k-cover
        for (int i = 1; i <= nodes; i++) {
            for (int j = 1; j <= k; j++) {
                for (int q = j + 1; q <= k; q++) {
                    myWriter.write((-1 * generateIndex(i, j) + " " + -1 * generateIndex(i, q)
                                + " " + 0 + "\n"));
                }
            }
        }

        // restriction c
        // these clauses force for every existing edge [u, v] at least one node to be the k-cover
        for (int i = 1; i <= nodes; i++) {
            for (int j = i + 1; j <= nodes; j++) {
                if (graph[i][j] == 1) {
                    for (int q = 1; q <= k; q++) {
                        myWriter.write((generateIndex(i, q) + " "));
                    }
                    for (int q = 1; q <= k; q++) {
                        myWriter.write((generateIndex(j, q) + " "));
                    }
                    myWriter.write((0 + "\n"));
                }
            }
        }

        // restriction d
        // these clauses force the existence of only one node on the ith position in the k-cover
        for (int i = 1; i <= k; i++) {
            for (int j = 1; j <= nodes; j++) {
                for (int q = j + 1; q <= nodes; q++) {
                    myWriter.write((-1 * generateIndex(j, i) + " " + -1 * generateIndex(q, i)
                                + " " + 0 + "\n"));

                }
            }
        }

        myWriter.close();
    }

    /**
     * reads and stores the truth value decided by the Oracle
     * @throws IOException
     */
    @Override
    public void decipherOracleAnswer() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("sat.sol"));
        truthValue = br.readLine();
        br.close();
    }

    /**
     * writes the output : the numbers of people who make up the maximum group that can be influenced
     * @throws IOException
     */
    @Override
    public void writeAnswer() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("sat.sol"));
        br.readLine();
        String numberString =  br.readLine();
        int number = Integer.parseInt(numberString);
        int[] output = new int[number];

        String[] values = br.readLine().split(" ");
        int indx = 0;

        // we extract from the array received by the Oracle
        // the positive variables which form the chosen cover
        for (int i = 0; i < number; i++) {
            if (Integer.parseInt(values[i]) > 0) {
                output[indx] = Integer.parseInt(values[i]);
                System.out.printf(decodifIndex(output[indx]) + " ");
                indx++;
            }
        }
        br.close();

    }
}