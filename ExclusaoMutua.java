/**
 * Atividade 4 - Corrigindo com exclusão mútua
 * Objetivo: Proteger a seção crítica e eliminar perda
 */
public class ExclusaoMutua {
    
    // Contador compartilhado
    private static int contador = 0;
    
    // Lock para exclusão mútua
    private static final Object lock = new Object();
    
    /**
     * Runnable SEM proteção (para comparação)
     */
    static class IncrementadorSemLock implements Runnable {
        private final int numIncrementos;
        
        public IncrementadorSemLock(int numIncrementos) {
            this.numIncrementos = numIncrementos;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < numIncrementos; i++) {
                contador++; // SEM proteção
            }
        }
    }
    
    /**
     * Runnable COM proteção (synchronized)
     */
    static class IncrementadorComLock implements Runnable {
        private final int numIncrementos;
        
        public IncrementadorComLock(int numIncrementos) {
            this.numIncrementos = numIncrementos;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < numIncrementos; i++) {
                synchronized (lock) { // Seção crítica protegida
                    contador++;
                }
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Atividade 4 - Exclusão Mútua ===\n");
        
        int[] numThreads = {2, 4, 8};
        int incrementosPorThread = 500_000;
        
        System.out.println("Incrementos por thread: " + incrementosPorThread);
        System.out.println("\n| Threads | Sem Lock (ms) | Com Lock (ms) | Valor Sem Lock | Valor Com Lock | Correto? |");
        System.out.println("|---------|---------------|---------------|----------------|----------------|----------|");
        
        for (int T : numThreads) {
            int valorEsperado = T * incrementosPorThread;
            
            // Teste SEM lock
            contador = 0;
            long tempoSemLock = executarTeste(T, incrementosPorThread, false);
            int valorSemLock = contador;
            
            // Teste COM lock
            contador = 0;
            long tempoComLock = executarTeste(T, incrementosPorThread, true);
            int valorComLock = contador;
            
            boolean correto = (valorComLock == valorEsperado);
            
            System.out.printf("| %7d | %13d | %13d | %14d | %14d | %8s |\n",
                            T, tempoSemLock, tempoComLock, valorSemLock, valorComLock,
                            correto ? "Sim" : "Não");
        }
        
        System.out.println("\n--- ANÁLISE DO CUSTO DO LOCK ---");
        System.out.println("O synchronized garante:");
        System.out.println("1. Exclusão mútua: apenas 1 thread por vez na seção crítica");
        System.out.println("2. Visibilidade: mudanças são visíveis para outras threads");
        System.out.println("3. Ordenação: previne reordenação de instruções");
        System.out.println();
        System.out.println("Custo do lock:");
        System.out.println("- Overhead de aquisição/liberação do lock (operações de SO)");
        System.out.println("- Contenção: threads esperando para adquirir o lock");
        System.out.println("- Serialização: threads executam sequencialmente na seção crítica");
        System.out.println();
        System.out.println("Quanto mais threads, maior a contenção e o overhead!");
    }
    
    /**
     * Executa teste com medição de tempo
     * @param comLock true para usar synchronized, false para executar sem proteção
     */
    static long executarTeste(int numThreads, int incrementos, boolean comLock) {
        Thread[] threads = new Thread[numThreads];
        
        // Aquecimento (descartado)
        executarUmaVez(numThreads, incrementos, comLock);
        
        // Medições reais (média de 3 execuções)
        long[] tempos = new long[3];
        for (int i = 0; i < 3; i++) {
            contador = 0;
            long inicio = System.nanoTime();
            
            // Criar e iniciar threads
            for (int j = 0; j < numThreads; j++) {
                if (comLock) {
                    threads[j] = new Thread(new IncrementadorComLock(incrementos));
                } else {
                    threads[j] = new Thread(new IncrementadorSemLock(incrementos));
                }
                threads[j].start();
            }
            
            // Aguardar todas
            for (int j = 0; j < numThreads; j++) {
                try {
                    threads[j].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            long fim = System.nanoTime();
            tempos[i] = (fim - inicio) / 1_000_000; // Converter para ms
        }
        
        // Retornar média
        return (tempos[0] + tempos[1] + tempos[2]) / 3;
    }
    
    static void executarUmaVez(int numThreads, int incrementos, boolean comLock) {
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            if (comLock) {
                threads[i] = new Thread(new IncrementadorComLock(incrementos));
            } else {
                threads[i] = new Thread(new IncrementadorSemLock(incrementos));
            }
            threads[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}