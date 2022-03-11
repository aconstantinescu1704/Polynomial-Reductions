import java.io.*;

class Registre extends Task {

    private int nodes;
    private int edges;
    private int k;
    private int [][]graph;
    private int []output;

    public Registre() {
        graph = new int[100][100];
        output = new int[5000];
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Registre registre = new Registre();
        registre.solve();
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
        k = Integer.parseInt(input[2]);

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
    public int decodifNod(int index) {
        if(index % k == 0)
            return index / k;
        return index / k + 1;
    }
    public int decodifReg(int index) {
        if(index % k == 0)
            return k;
        return index % k;
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        File obj = new File("sat.cnf");
        FileWriter myWriter = new FileWriter("sat.cnf");
        int nrVariables = k * nodes;
        int nrClauses = nodes + edges * k + nodes * k * (k - 1) / 2;

        myWriter.write("p cnf " + nrVariables + " " + nrClauses + "\n");

        // restriction a
        // these clauses make sure that every node is in at least one register
        for (int i = 1; i <= nodes; i++) {
            for (int j = 1; j <= k; j++) {
                myWriter.write((generateIndex(i, j) + " "));
            }
            myWriter.write((0 + "\n"));
        }

        // restriction b
        // these clauses force for every edge [i, j] that both nodes cannot be in the same register
        for (int i = 1; i <= nodes; i++) {
            for (int j = i + 1; j <= nodes; j++) {
                if (graph[i][j] == 1) {
                    for (int q = 1; q <= k; q++) {
                        myWriter.write((-1 * generateIndex(i, q) + " " + -1 * generateIndex(j, q)
                                + " " + 0 + "\n"));
                    }
                }
            }
        }
        // restriction c
        // these clauses force that a node should be considered in only one register
        for (int i = 1; i <= nodes; i++) {
            for (int j = 1; j <= k; j++) {
                for (int q = j + 1; q <= k; q++) {
                    myWriter.write((-1 * generateIndex(i, j) + " " + -1 * generateIndex(i, q)
                                + " " + 0 + "\n"));
                }
            }
        }
        myWriter.close();
    }

    /**
     * decipher the received answer by decoding the codified variables and
     * writing at stdout an array of numbers representing the register number assigned to each variable.
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
                    int val =  Integer.parseInt(values[i]);
                    output[decodifNod(val)] = decodifReg(val);
                }
            }
        }
        br.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        for(int i = 1; i <= nodes; i++) {
            System.out.printf(output[i] + " ");
        }
    }
}