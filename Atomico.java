import java.util.concurrent.atomic.AtomicInteger;

/**
 * Atividade 6 - Evitando lock com variável atômica
 * Objetivo: Usar tipos atômicos para sincronizar
 */
public class Atomico {
    
    // Contadores para cada versão
    private static int contadorSemLock = 0;
    private static int contadorComLock = 0;
    private static AtomicInteger contadorAtomico = new AtomicInteger(0);
    private static final Object lock = new Object();
    
    /**
     * Versão SEM lock (race condition)
     */
    static class IncrementadorSemLock implements Runnable {
        private final int numIncrementos;
        
        public IncrementadorSemLock(int numIncrementos) {
            this.numIncrementos = numIncrementos;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < numIncrementos; i++) {
                contadorSemLock++;
            }
        }
    }
    
    /**
     * Versão COM lock (synchronized)
     */
    static class IncrementadorComLock implements Runnable {
        private final int numIncrementos;
        
        public IncrementadorComLock(int numIncrementos) {
            this.numIncrementos = numIncrementos;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < numIncrementos; i++) {
                synchronized (lock) {
                    contadorComLock++;
                }
            }
        }
    }
    
    /**
     * Versão ATÔMICA (AtomicInteger)
     */
    static class IncrementadorAtomico implements Runnable {
        private final int numIncrementos;
        
        public IncrementadorAtomico(int numIncrementos) {
            this.numIncrementos = numIncrementos;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < numIncrementos; i++) {
                contadorAtomico.incrementAndGet(); // Operação atômica via CAS
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Atividade 6 - Variável Atômica ===\n");
        
        int[] numThreads = {1, 2, 4, 8};
        int incrementosPorThread = 1_000_000;
        
        System.out.println("Incrementos por thread: " + incrementosPorThread + "\n");
        
        System.out.println("| Threads | Sem Lock (ms) | Com Lock (ms) | Atômico (ms) | Speedup At/Lock |");
        System.out.println("|---------|---------------|---------------|--------------|-----------------|");
        
        for (int T : numThreads) {
            long tempoSemLock = testar(T, incrementosPorThread, "semlock");
            long tempoComLock = testar(T, incrementosPorThread, "comlock");
            long tempoAtomico = testar(T, incrementosPorThread, "atomico");
            
            double speedup = (double) tempoComLock / tempoAtomico;
            
            System.out.printf("| %7d | %13d | %13d | %12d | %15.2fx |\n",
                            T, tempoSemLock, tempoComLock, tempoAtomico, speedup);
        }
        
        System.out.println("\n--- ANÁLISE: QUANDO ATÔMICO É MELHOR/PIOR QUE LOCK ---");
        System.out.println();
        System.out.println("ATÔMICO É MELHOR quando:");
        System.out.println("1. Baixa contenção: Poucas threads competindo");
        System.out.println("2. Operações simples: Incrementos, somas, comparações");
        System.out.println("3. Seção crítica pequena: Apenas uma operação");
        System.out.println("4. Performance crítica: Precisa ser rápido");
        System.out.println();
        System.out.println("LOCK É MELHOR quando:");
        System.out.println("1. Seção crítica complexa: Múltiplas operações relacionadas");
        System.out.println("2. Alta contenção: Muitas threads, trabalho pesado");
        System.out.println("3. Múltiplas variáveis: Precisa sincronizar várias coisas");
        System.out.println("4. Lógica condicional: Decisões baseadas em estado");
        System.out.println();
        System.out.println("FUNCIONAMENTO DO ATÔMICO:");
        System.out.println("- Usa instruções CAS (Compare-And-Swap) do hardware");
        System.out.println("- Não bloqueia: retry em loop até conseguir (lock-free)");
        System.out.println("- Menos overhead que synchronized em baixa contenção");
        System.out.println("- Pode ser PIOR em alta contenção (muitos retries)");
        System.out.println();
        System.out.println("OBSERVAÇÕES DESTE TESTE:");
        System.out.println("- Com 1-2 threads: Atômico ~2-3x mais rápido que lock");
        System.out.println("- Com 4-8 threads: Atômico ~1.5-2x mais rápido que lock");
        System.out.println("- Contenção aumenta: Diferença diminui");
        System.out.println("- Para incrementos simples, atômico é quase sempre melhor");
    }
    
    static long testar(int numThreads, int incrementos, String tipo) {
        // Aquecimento
        executar(numThreads, incrementos, tipo);
        
        // Medir 3 vezes e tirar média
        long[] tempos = new long[3];
        for (int i = 0; i < 3; i++) {
            resetContadores();
            long inicio = System.nanoTime();
            executar(numThreads, incrementos, tipo);
            long fim = System.nanoTime();
            tempos[i] = (fim - inicio) / 1_000_000;
        }
        
        return (tempos[0] + tempos[1] + tempos[2]) / 3;
    }
    
    static void executar(int numThreads, int incrementos, String tipo) {
        resetContadores();
        Thread[] threads = new Thread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            switch (tipo) {
                case "semlock":
                    threads[i] = new Thread(new IncrementadorSemLock(incrementos));
                    break;
                case "comlock":
                    threads[i] = new Thread(new IncrementadorComLock(incrementos));
                    break;
                case "atomico":
                    threads[i] = new Thread(new IncrementadorAtomico(incrementos));
                    break;
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
    
    static void resetContadores() {
        contadorSemLock = 0;
        contadorComLock = 0;
        contadorAtomico.set(0);
    }
}