import java.io.*;

class Retele extends Task{

    private int nodes;
    private int edges;
    private int clique;
    private final int [][]graph;

    public Retele() {
        graph = new int[100][100];
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Retele retele = new Retele();
        retele.solve();
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String[] input = reader.readLine().split(" ");
        nodes = Integer.parseInt(input[0]);
        edges = Integer.parseInt(input[1]);
        clique = Integer.parseInt(input[2]);

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

    public int power10(int number) {
        int temp = 1;
        while (temp <= number) {
            temp *= 10;
        }
        return temp;
    }

    public int generateIndex(int i, int v) {
        return i * power10(v) + v;
    }

    @Override
    public void formulateOracleQuestion() throws IOException {

        FileWriter myWriter = new FileWriter("sat.cnf");
        int nrMissingEdges = 0;
        for (int i = 1; i <= nodes; i++) {
            for (int j = 1; j <= nodes; j++) {
                if (graph[i][j] == 0 && i < j) {
                    nrMissingEdges++;
                }
            }
        }
        int nrVariables = clique * nodes;
        int nrClauses = clique + nrMissingEdges * clique * (clique - 1)
                + (clique - 1) * clique * nodes / 2 + (nodes - 1) * nodes * clique / 2;

        // restriction a
        // these clauses force the existence an ith vertex (at least one) for every 1 <= i <= clique
        myWriter.write("p cnf " + nrVariables + " " + nrClauses + "\n");
        for (int i = 1; i <= clique; i++) {
            for (int j = 1; j <= nodes; j++) {
                myWriter.write((generateIndex(i, j) + " "));
            }
            myWriter.write((0 + "\n"));
        }

        // restriction b
        // for every non-edge [i, j] we make sure that both edges are in the clique
        for (int i = 1; i <= nodes; i++) {
            for (int j = i + 1; j <= nodes; j++) {
                if (graph[i][j] == 0) {
                    for (int k = 1; k <= clique; k++) {
                        for (int q = 1; q <= clique; q++) {
                            if(k != q) {
                                myWriter.write((-1 * generateIndex(k, i) + " " + -1 * generateIndex(q, j)
                                        + " " + 0 + "\n"));
                            }
                        }
                    }
                }
            }
        }
        // restriction c
        // these clauses make sure that one node is not considered on different positions in the clique
        for (int i = 1; i <= nodes; i++) {
            for (int k = 1; k <= clique; k++) {
                for (int q = k + 1; q <= clique; q++) {
                    myWriter.write((-1 * generateIndex(k, i) + " " + -1 * generateIndex(q, i)
                                + " " + 0 + "\n"));
                }
            }
        }

        // these clauses make sure that a position is not taken by more than one node
        for (int k = 1; k <= clique; k++) {
            for (int i = 1; i <= nodes; i++) {
                for (int j = i + 1; j <= nodes; j++) {
                    myWriter.write((-1 * generateIndex(k, i) + " " + -1 * generateIndex(k, j)
                                + " " + 0 + "\n"));
                }
            }
        }
        myWriter.close();
    }

    /**
     * if the received answer from Oracle is true, we decipher the Oracle and
     * return an array of numbers representing persons forming a K-size group
     * @throws IOException
     */
    @Override
    public void decipherOracleAnswer() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("sat.sol"));
        String truthValue = br.readLine();
        System.out.println(truthValue);

        if (truthValue.equals("True")) {
            String numberString =  br.readLine();
            int number = Integer.parseInt(numberString);

            String[] values = br.readLine().split(" ");
            for (int i = 0; i < number; i++) {
                if (Integer.parseInt(values[i]) > 0) {
                    // we return the node from the k-clique by decoding the initial codification
                    System.out.printf(Integer.parseInt(values[i]) % 10 + " ");
                }
            }
        }
        br.close();
    }

    @Override
    public void writeAnswer() throws IOException {

    }
}