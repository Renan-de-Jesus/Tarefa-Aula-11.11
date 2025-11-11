import java.util.Random;

/**
 * Atividade 10 - Estimativa de π (Monte Carlo)
 * Objetivo: Paralelizar um experimento estatístico
 */
public class MonteCarlo {
    
    /**
     * Worker que sorteia pontos e conta quantos caem no círculo
     */
    static class SimuladorMonteCarlo implements Runnable {
        private final long numPontos;
        private final long[] contadores;
        private final int indice;
        private final long seed;
        
        public SimuladorMonteCarlo(long numPontos, long[] contadores, int indice, long seed) {
            this.numPontos = numPontos;
            this.contadores = contadores;
            this.indice = indice;
            this.seed = seed;
        }
        
        @Override
        public void run() {
            Random rand = new Random(seed);
            long dentroCirculo = 0;
            
            for (long i = 0; i < numPontos; i++) {
                // Sortear ponto no quadrado unitário [0,1] x [0,1]
                double x = rand.nextDouble();
                double y = rand.nextDouble();
                
                // Verificar se está dentro do círculo de raio 0.5 centrado em (0.5, 0.5)
                double dx = x - 0.5;
                double dy = y - 0.5;
                double distancia = Math.sqrt(dx * dx + dy * dy);
                
                if (distancia <= 0.5) {
                    dentroCirculo++;
                }
            }
            
            contadores[indice] = dentroCirculo;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Atividade 10 - Estimativa de π (Monte Carlo) ===\n");
        
        int[] numThreadsArray = {1, 2, 4, 8};
        long[] tamanhoLoteArray = {1_000_000L, 10_000_000L, 100_000_000L};
        
        System.out.println("Método Monte Carlo:");
        System.out.println("- Sorteia pontos aleatórios no quadrado [0,1]×[0,1]");
        System.out.println("- Conta quantos caem dentro do círculo raio 0.5");
        System.out.println("- Estimativa: π ≈ 4 × (dentro / total)");
        System.out.println("- π real = " + Math.PI + "\n");
        
        for (long K : tamanhoLoteArray) {
            System.out.println("\n=== LOTE: " + formatarNumero(K) + " pontos por thread ===\n");
            System.out.println("| Threads | Tempo (ms) | π estimado | Erro absoluto | Speedup |");
            System.out.println("|---------|------------|------------|---------------|---------|");
            
            long tempoBase = 0;
            
            for (int T : numThreadsArray) {
                long totalPontos = K * T;
                
                // Medir tempo
                long tempo = medirTempo(T, K);
                
                // Calcular estimativa de π
                double piEstimado = estimarPi(T, K);
                double erro = Math.abs(piEstimado - Math.PI);
                
                double speedup = (T == 1) ? 1.0 : (double) tempoBase / tempo;
                if (T == 1) tempoBase = tempo;
                
                System.out.printf("| %7d | %10d | %10.6f | %13.6f | %7.2fx |\n",
                                T, tempo, piEstimado, erro, speedup);
            }
        }
        
        System.out.println("\n--- ANÁLISE: TAMANHO DO LOTE × PARALELISMO ---");
        System.out.println();
        System.out.println("LOTE PEQUENO (1M pontos/thread):");
        System.out.println("- Overhead de threads domina");
        System.out.println("- Speedup baixo (threads têm pouco trabalho)");
        System.out.println("- Eficiência reduzida");
        System.out.println();
        System.out.println("LOTE MÉDIO (10M pontos/thread):");
        System.out.println("- Melhor balanço overhead vs trabalho");
        System.out.println("- Speedup mais próximo do ideal");
        System.out.println("- Boa eficiência");
        System.out.println();
        System.out.println("LOTE GRANDE (100M pontos/thread):");
        System.out.println("- Overhead desprezível");
        System.out.println("- Speedup quase linear");
        System.out.println("- Máxima eficiência");
        System.out.println();
        System.out.println("CONCLUSÃO:");
        System.out.println("Quanto MAIOR o trabalho por thread, MELHOR o speedup!");
        System.out.println("Paralelismo só compensa se o trabalho for substancial.");
        System.out.println();
        System.out.println("PRECISÃO DA ESTIMATIVA:");
        System.out.println("Erro decresce com √N (lei dos grandes números)");
        System.out.println("Dobrar pontos → reduz erro pela metade");
    }
    
    /**
     * Estima π usando Monte Carlo paralelo
     */
    static double estimarPi(int numThreads, long pontosPorThread) {
        Thread[] threads = new Thread[numThreads];
        long[] contadores = new long[numThreads];
        
        // Criar e iniciar threads
        for (int i = 0; i < numThreads; i++) {
            long seed = System.nanoTime() + i;
            threads[i] = new Thread(new SimuladorMonteCarlo(pontosPorThread, contadores, i, seed));
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
        
        // Agregar contadores
        long totalDentro = 0;
        for (long contador : contadores) {
            totalDentro += contador;
        }
        
        long totalPontos = numThreads * pontosPorThread;
        
        // π ≈ 4 × (área do círculo / área do quadrado)
        // Círculo raio 0.5 tem área π/4
        // Quadrado unitário tem área 1
        // Razão = (dentro/total) ≈ (π/4)/1
        // Logo: π ≈ 4 × (dentro/total)
        return 4.0 * totalDentro / totalPontos;
    }
    
    /**
     * Mede tempo de execução
     */
    static long medirTempo(int numThreads, long pontosPorThread) {
        // Aquecimento
        estimarPi(numThreads, pontosPorThread / 10);
        
        // Medir 3 vezes
        long[] tempos = new long[3];
        for (int i = 0; i < 3; i++) {
            long inicio = System.nanoTime();
            estimarPi(numThreads, pontosPorThread);
            long fim = System.nanoTime();
            tempos[i] = (fim - inicio) / 1_000_000;
        }
        
        return (tempos[0] + tempos[1] + tempos[2]) / 3;
    }
    
    /**
     * Formata número grande com separadores
     */
    static String formatarNumero(long num) {
        if (num >= 1_000_000) {
            return (num / 1_000_000) + "M";
        } else if (num >= 1_000) {
            return (num / 1_000) + "K";
        }
        return String.valueOf(num);
    }
}

/* MÉTODO MONTE CARLO PARA π:
 * 
 * Geometria:
 * - Quadrado unitário [0,1]×[0,1] tem área = 1
 * - Círculo raio 0.5 centrado em (0.5, 0.5) tem área = π×(0.5)² = π/4
 * - Círculo está inscrito no quadrado
 * 
 * Estimativa:
 * - Sortear N pontos aleatórios no quadrado
 * - Contar quantos caem dentro do círculo (M)
 * - Razão M/N ≈ (π/4) / 1 = π/4
 * - Logo: π ≈ 4 × (M/N)
 * 
 * Paralelização:
 * - Cada thread sorteia K pontos independentemente
 * - Usa seed diferente para garantir independência
 * - Soma contadores no final (reduce)
 * - Operação embaraçosamente paralela (sem dependências)
 * 
 * Características:
 * - Trabalho balanceado automaticamente
 * - Sem comunicação entre threads
 * - Speedup quase linear com threads suficientes
 * - Ideal para demonstrar benefícios do paralelismo
 */