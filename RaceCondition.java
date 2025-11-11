/**
 * Atividade 3 - Condição de corrida na prática
 * Objetivo: Observar race condition em variável compartilhada
 */
public class RaceCondition {
    
    // Contador compartilhado (SEM proteção)
    private static int contador = 0;
    
    /**
     * Runnable que incrementa o contador N vezes
     */
    static class IncrementadorRunnable implements Runnable {
        private final int numIncrementos;
        
        public IncrementadorRunnable(int numIncrementos) {
            this.numIncrementos = numIncrementos;
        }
        
        @Override
        public void run() {
            // Incrementa sem proteção - RACE CONDITION!
            for (int i = 0; i < numIncrementos; i++) {
                contador++; // Operação NÃO atômica
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Atividade 3 - Race Condition ===\n");
        
        int numThreads = 4;
        int incrementosPorThread = 1_000_000;
        int valorEsperado = numThreads * incrementosPorThread;
        
        System.out.println("Configuração:");
        System.out.println("- Threads: " + numThreads);
        System.out.println("- Incrementos por thread: " + incrementosPorThread);
        System.out.println("- Valor esperado: " + valorEsperado + "\n");
        
        // Executar múltiplas vezes para ver variação
        for (int teste = 1; teste <= 3; teste++) {
            contador = 0; // Resetar
            
            Thread[] threads = new Thread[numThreads];
            
            // Criar e iniciar threads
            for (int i = 0; i < numThreads; i++) {
                threads[i] = new Thread(new IncrementadorRunnable(incrementosPorThread));
                threads[i].start();
            }
            
            // Aguardar todas
            for (int i = 0; i < numThreads; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            int valorObtido = contador;
            int perda = valorEsperado - valorObtido;
            double percPerda = (perda * 100.0) / valorEsperado;
            
            System.out.println("Teste " + teste + ":");
            System.out.println("  Valor esperado: " + valorEsperado);
            System.out.println("  Valor obtido:   " + valorObtido);
            System.out.println("  Perda:          " + perda + " (" + 
                             String.format("%.2f", percPerda) + "%)");
            System.out.println();
        }
        
        // Explicação
        System.out.println("--- POR QUE OCORRE PERDA? ---");
        System.out.println("A operação contador++ é compilada em 3 instruções:");
        System.out.println("1. LER valor atual da memória");
        System.out.println("2. INCREMENTAR o valor");
        System.out.println("3. ESCREVER de volta na memória");
        System.out.println();
        System.out.println("INTERLEAVING (exemplo de perda):");
        System.out.println("Contador = 100");
        System.out.println("Thread A: LER(100) -> INCREMENTAR(101) -> [interrupção]");
        System.out.println("Thread B: LER(100) -> INCREMENTAR(101) -> ESCREVER(101)");
        System.out.println("Thread A: [retoma] -> ESCREVER(101)");
        System.out.println("Resultado: 101 ao invés de 102 (perda de 1 incremento)");
        System.out.println();
        System.out.println("Com milhões de incrementos e várias threads,");
        System.out.println("milhares de incrementos são perdidos por race condition!");
    }
}
