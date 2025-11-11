/**
 * Atividade 5 - Variando a granularidade do lock
 * Objetivo: Medir o impacto de granularidade
 */
public class Granularidade {
    
    private static int contador = 0;
    private static final Object lock = new Object();
    
    /**
     * Versão A: Trava a cada incremento (granularidade FINA)
     */
    static class IncrementoFinoRunnable implements Runnable {
        private final int numIncrementos;
        
        public IncrementoFinoRunnable(int numIncrementos) {
            this.numIncrementos = numIncrementos;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < numIncrementos; i++) {
                synchronized (lock) {
                    contador++; // Lock para cada incremento
                }
            }
        }
    }
    
    /**
     * Versão B: Acumula localmente e trava a cada bloco (granularidade MÉDIA)
     */
    static class IncrementoBlocoRunnable implements Runnable {
        private final int numIncrementos;
        private final int tamanhoBloco;
        
        public IncrementoBlocoRunnable(int numIncrementos, int tamanhoBloco) {
            this.numIncrementos = numIncrementos;
            this.tamanhoBloco = tamanhoBloco;
        }
        
        @Override
        public void run() {
            int acumulador = 0;
            for (int i = 0; i < numIncrementos; i++) {
                acumulador++;
                
                // A cada bloco, transfere para o contador global
                if (acumulador >= tamanhoBloco) {
                    synchronized (lock) {
                        contador += acumulador;
                    }
                    acumulador = 0;
                }
            }
            
            // Transferir resto
            if (acumulador > 0) {
                synchronized (lock) {
                    contador += acumulador;
                }
            }
        }
    }
    
    /**
     * Versão C: Acumula tudo local e trava uma vez no final (granularidade GROSSA)
     */
    static class IncrementoFinalRunnable implements Runnable {
        private final int numIncrementos;
        
        public IncrementoFinalRunnable(int numIncrementos) {
            this.numIncrementos = numIncrementos;
        }
        
        @Override
        public void run() {
            int acumulador = 0;
            
            // Acumular tudo localmente (sem lock)
            for (int i = 0; i < numIncrementos; i++) {
                acumulador++;
            }
            
            // Um único lock no final
            synchronized (lock) {
                contador += acumulador;
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Atividade 5 - Granularidade do Lock ===\n");
        
        int[] numThreads = {2, 4, 8};
        int incrementosPorThread = 1_000_000;
        int tamanhoBloco = 1000;
        
        System.out.println("Configuração:");
        System.out.println("- Incrementos por thread: " + incrementosPorThread);
        System.out.println("- Tamanho do bloco (versão B): " + tamanhoBloco);
        System.out.println();
        
        System.out.println("| Threads | Fino (ms) | Bloco (ms) | Final (ms) | Speedup B/A | Speedup C/A |");
        System.out.println("|---------|-----------|------------|------------|-------------|-------------|");
        
        for (int T : numThreads) {
            long tempoFino = testarGranularidade(T, incrementosPorThread, "fino", tamanhoBloco);
            long tempoBloco = testarGranularidade(T, incrementosPorThread, "bloco", tamanhoBloco);
            long tempoFinal = testarGranularidade(T, incrementosPorThread, "final", tamanhoBloco);
            
            double speedupBloco = (double) tempoFino / tempoBloco;
            double speedupFinal = (double) tempoFino / tempoFinal;
            
            System.out.printf("| %7d | %9d | %10d | %10d | %11.2fx | %11.2fx |\n",
                            T, tempoFino, tempoBloco, tempoFinal, speedupBloco, speedupFinal);
        }
        
        System.out.println("\n--- ANÁLISE ---");
        System.out.println();
        System.out.println("GRANULARIDADE FINA (lock a cada incremento):");
        System.out.println("- Prós: Máxima corretude, simples de implementar");
        System.out.println("- Contras: ALTO overhead, muita contenção");
        System.out.println("- Usa: Milhões de locks → muito tempo em sincronização");
        System.out.println();
        System.out.println("GRANULARIDADE MÉDIA (lock a cada bloco):");
        System.out.println("- Prós: Reduz locks significativamente (1000x menos)");
        System.out.println("- Contras: Ainda há alguma contenção");
        System.out.println("- Usa: Milhares de locks → overhead moderado");
        System.out.println();
        System.out.println("GRANULARIDADE GROSSA (1 lock no final):");
        System.out.println("- Prós: MÍNIMO overhead, quase sem contenção");
        System.out.println("- Contras: Trabalho é praticamente sequencial");
        System.out.println("- Usa: Apenas " + numThreads[numThreads.length-1] + " locks → overhead mínimo");
        System.out.println();
        System.out.println("CONCLUSÃO:");
        System.out.println("Para este problema específico (soma simples), granularidade");
        System.out.println("grossa é MUITO melhor. Em problemas mais complexos, é preciso");
        System.out.println("balancear entre paralelismo e overhead de sincronização.");
    }
    
    static long testarGranularidade(int numThreads, int incrementos, String tipo, int tamanhoBloco) {
        // Aquecimento
        executarGranularidade(numThreads, incrementos, tipo, tamanhoBloco);
        
        // Medir 3 vezes e tirar média
        long[] tempos = new long[3];
        for (int i = 0; i < 3; i++) {
            contador = 0;
            long inicio = System.nanoTime();
            executarGranularidade(numThreads, incrementos, tipo, tamanhoBloco);
            long fim = System.nanoTime();
            tempos[i] = (fim - inicio) / 1_000_000;
        }
        
        return (tempos[0] + tempos[1] + tempos[2]) / 3;
    }
    
    static void executarGranularidade(int numThreads, int incrementos, String tipo, int tamanhoBloco) {
        contador = 0;
        Thread[] threads = new Thread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            switch (tipo) {
                case "fino":
                    threads[i] = new Thread(new IncrementoFinoRunnable(incrementos));
                    break;
                case "bloco":
                    threads[i] = new Thread(new IncrementoBlocoRunnable(incrementos, tamanhoBloco));
                    break;
                case "final":
                    threads[i] = new Thread(new IncrementoFinalRunnable(incrementos));
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
}