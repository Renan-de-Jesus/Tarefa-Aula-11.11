/**
 * Atividade 9 - Soma paralela de vetor (map–reduce)
 * Objetivo: Dividir um vetor grande entre threads e reduzir a soma
 */
public class SomaParalela {
    
    /**
     * Worker que calcula soma parcial de uma partição do vetor
     */
    static class SomadorParcial implements Runnable {
        private final int[] vetor;
        private final int inicio;
        private final int fim;
        private final long[] resultadoParcial;
        private final int indiceResultado;
        
        public SomadorParcial(int[] vetor, int inicio, int fim, 
                             long[] resultadoParcial, int indiceResultado) {
            this.vetor = vetor;
            this.inicio = inicio;
            this.fim = fim;
            this.resultadoParcial = resultadoParcial;
            this.indiceResultado = indiceResultado;
        }
        
        @Override
        public void run() {
            long soma = 0;
            for (int i = inicio; i < fim; i++) {
                soma += vetor[i];
            }
            resultadoParcial[indiceResultado] = soma;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Atividade 9 - Soma Paralela (Map-Reduce) ===\n");
        
        // Configuração
        int tamanhoVetor = 50_000_000; // 50 milhões
        int[] numThreadsArray = {1, 2, 4, 8};
        
        System.out.println("Gerando vetor com " + tamanhoVetor + " elementos...");
        int[] vetor = gerarVetor(tamanhoVetor);
        System.out.println("Vetor gerado!\n");
        
        // Calcular soma sequencial (referência)
        System.out.println("Calculando soma sequencial (referência)...");
        long somaSequencial = somaSequencial(vetor);
        long tempoSequencial = medirTempoSequencial(vetor);
        System.out.println("Soma sequencial: " + somaSequencial);
        System.out.println("Tempo sequencial: " + tempoSequencial + " ms\n");
        
        // Tabela de resultados
        System.out.println("| Threads | Tempo (ms) | Speedup | Eficiência | Correto? |");
        System.out.println("|---------|------------|---------|------------|----------|");
        
        for (int numThreads : numThreadsArray) {
            long tempo = medirTempoParalelo(vetor, numThreads);
            long soma = somaParalela(vetor, numThreads);
            
            double speedup = (double) tempoSequencial / tempo;
            double eficiencia = speedup / numThreads;
            boolean correto = (soma == somaSequencial);
            
            System.out.printf("| %7d | %10d | %7.2f | %9.2f%% | %8s |\n",
                            numThreads, tempo, speedup, eficiencia * 100, 
                            correto ? "Sim ✓" : "Não ✗");
        }
        
        System.out.println("\n--- ANÁLISE ---");
        System.out.println();
        System.out.println("POR QUE NÃO CHEGOU NO SPEEDUP IDEAL?");
        System.out.println();
        System.out.println("1. OVERHEAD DE CRIAÇÃO DE THREADS:");
        System.out.println("   - Criar e iniciar threads tem custo");
        System.out.println("   - join() tem overhead de sincronização");
        System.out.println();
        System.out.println("2. OVERHEAD DE MEMÓRIA:");
        System.out.println("   - Cada thread acessa memória");
        System.out.println("   - Cache thrashing: threads competem por cache");
        System.out.println("   - False sharing: linhas de cache compartilhadas");
        System.out.println();
        System.out.println("3. GRANULARIDADE:");
        System.out.println("   - Com 8 threads, cada uma processa menos dados");
        System.out.println("   - Overhead relativo aumenta");
        System.out.println();
        System.out.println("4. LIMITAÇÕES DE HARDWARE:");
        System.out.println("   - Número de cores físicos vs lógicos");
        System.out.println("   - Largura de banda da memória RAM");
        System.out.println("   - Contenção no barramento de memória");
        System.out.println();
        System.out.println("SPEEDUP TÍPICO ESPERADO:");
        System.out.println("- 2 threads: ~1.8-1.9x (90-95% eficiência)");
        System.out.println("- 4 threads: ~3.2-3.6x (80-90% eficiência)");
        System.out.println("- 8 threads: ~5.0-6.5x (62-81% eficiência)");
    }
    
    /**
     * Gera vetor com valores aleatórios
     */
    static int[] gerarVetor(int tamanho) {
        int[] vetor = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            vetor[i] = i % 100; // Valores pequenos para evitar overflow
        }
        return vetor;
    }
    
    /**
     * Soma sequencial (referência)
     */
    static long somaSequencial(int[] vetor) {
        long soma = 0;
        for (int valor : vetor) {
            soma += valor;
        }
        return soma;
    }
    
    /**
     * Soma paralela dividindo vetor entre threads
     */
    static long somaParalela(int[] vetor, int numThreads) {
        Thread[] threads = new Thread[numThreads];
        long[] somasParciais = new long[numThreads];
        
        int tamanhoParticao = vetor.length / numThreads;
        
        // Criar e iniciar threads
        for (int i = 0; i < numThreads; i++) {
            int inicio = i * tamanhoParticao;
            int fim = (i == numThreads - 1) ? vetor.length : (i + 1) * tamanhoParticao;
            
            threads[i] = new Thread(new SomadorParcial(vetor, inicio, fim, 
                                                       somasParciais, i));
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
        
        // Reduzir: somar resultados parciais
        long somaTotal = 0;
        for (long somaParcial : somasParciais) {
            somaTotal += somaParcial;
        }
        
        return somaTotal;
    }
    
    /**
     * Mede tempo da versão sequencial
     */
    static long medirTempoSequencial(int[] vetor) {
        // Aquecimento
        somaSequencial(vetor);
        
        // Medir 3 vezes
        long[] tempos = new long[3];
        for (int i = 0; i < 3; i++) {
            long inicio = System.nanoTime();
            somaSequencial(vetor);
            long fim = System.nanoTime();
            tempos[i] = (fim - inicio) / 1_000_000;
        }
        
        return (tempos[0] + tempos[1] + tempos[2]) / 3;
    }
    
    /**
     * Mede tempo da versão paralela
     */
    static long medirTempoParalelo(int[] vetor, int numThreads) {
        // Aquecimento
        somaParalela(vetor, numThreads);
        
        // Medir 3 vezes
        long[] tempos = new long[3];
        for (int i = 0; i < 3; i++) {
            long inicio = System.nanoTime();
            somaParalela(vetor, numThreads);
            long fim = System.nanoTime();
            tempos[i] = (fim - inicio) / 1_000_000;
        }
        
        return (tempos[0] + tempos[1] + tempos[2]) / 3;
    }
}

/* MAP-REDUCE:
 * 
 * MAP (Dividir):
 * - Dividir vetor em N partições contíguas
 * - Cada thread processa uma partição
 * - Calcula resultado parcial (soma local)
 * 
 * REDUCE (Combinar):
 * - Após todas threads terminarem (join)
 * - Combinar todos os resultados parciais
 * - Produzir resultado final
 * 
 * VANTAGENS:
 * - Simples de implementar
 * - Balanceamento automático (partições iguais)
 * - Sem sincronização durante cálculo
 * - Escalável para muitos cores
 */