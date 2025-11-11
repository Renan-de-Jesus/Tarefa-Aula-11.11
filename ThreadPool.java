import java.util.concurrent.*;

/**
 * Atividade 11 - Pool de threads (Executors)
 * Objetivo: Reduzir overhead de criação/encerramento repetidos
 */
public class ThreadPool {
    
    /**
     * Tarefa simples: processa um bloco de um array
     */
    static class TarefaProcessamento implements Runnable {
        private final int id;
        private final int[] dados;
        private final int inicio;
        private final int fim;
        private final long[] resultados;
        
        public TarefaProcessamento(int id, int[] dados, int inicio, int fim, long[] resultados) {
            this.id = id;
            this.dados = dados;
            this.inicio = inicio;
            this.fim = fim;
            this.resultados = resultados;
        }
        
        @Override
        public void run() {
            long soma = 0;
            for (int i = inicio; i < fim; i++) {
                // Processamento simples: soma
                soma += dados[i];
                
                // Simular algum trabalho adicional
                if (dados[i] % 2 == 0) {
                    soma += dados[i] * 2;
                }
            }
            resultados[id] = soma;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Atividade 11 - Pool de Threads ===\n");
        
        // Configuração
        int tamanhoArray = 10_000_000;
        int[] numTarefasArray = {100, 500, 1000};
        int poolSize = 4;
        
        System.out.println("Configuração:");
        System.out.println("- Tamanho do array: " + tamanhoArray);
        System.out.println("- Pool size: " + poolSize + " threads");
        System.out.println("- Comparando: Pool vs Criar thread por tarefa\n");
        
        // Gerar dados
        int[] dados = new int[tamanhoArray];
        for (int i = 0; i < tamanhoArray; i++) {
            dados[i] = i % 100;
        }
        
        System.out.println("| Tarefas | Pool (ms) | Thread/Tarefa (ms) | Speedup | Economiza |");
        System.out.println("|---------|-----------|--------------------|---------|-----------| ");
        
        for (int M : numTarefasArray) {
            long tempoPool = medirComPool(dados, M, poolSize);
            long tempoThreadPorTarefa = medirThreadPorTarefa(dados, M);
            
            double speedup = (double) tempoThreadPorTarefa / tempoPool;
            double economia = ((double) (tempoThreadPorTarefa - tempoPool) / tempoThreadPorTarefa) * 100;
            
            System.out.printf("| %7d | %9d | %18d | %7.2fx | %8.1f%% |\n",
                            M, tempoPool, tempoThreadPorTarefa, speedup, economia);
        }
        
        System.out.println("\n--- ANÁLISE: QUANDO O POOL COMPENSA ---");
        System.out.println();
        System.out.println("POOL DE THREADS COMPENSA quando:");
        System.out.println();
        System.out.println("1. MUITAS TAREFAS PEQUENAS:");
        System.out.println("   - Overhead de criar/destruir threads é significativo");
        System.out.println("   - Pool reutiliza threads → economia massiva");
        System.out.println("   - Exemplo: 1000 tarefas = 1000 criações evitadas!");
        System.out.println();
        System.out.println("2. TAREFAS CURTAS:");
        System.out.println("   - Tempo de execução < tempo de criação da thread");
        System.out.println("   - Pool mantém threads vivas e prontas");
        System.out.println();
        System.out.println("3. CARGA VARIÁVEL:");
        System.out.println("   - Pool se adapta automaticamente");
        System.out.println("   - Threads ficam ociosas quando não há trabalho");
        System.out.println();
        System.out.println("4. CONTROLE DE RECURSOS:");
        System.out.println("   - Limita número de threads simultâneas");
        System.out.println("   - Evita sobrecarga do sistema");
        System.out.println();
        System.out.println("NÃO COMPENSA quando:");
        System.out.println("- Poucas tarefas grandes (overhead negligenciável)");
        System.out.println("- Tarefas bloqueiam muito (threads ficam paradas)");
        System.out.println("- Tarefas têm dependências complexas");
        System.out.println();
        System.out.println("TIPOS DE POOLS:");
        System.out.println("- FixedThreadPool: N threads fixas");
        System.out.println("- CachedThreadPool: Cria sob demanda, reusa ociosas");
        System.out.println("- SingleThreadExecutor: 1 thread, garante ordem");
        System.out.println("- ScheduledThreadPool: Para tarefas agendadas");
        System.out.println();
        System.out.println("NESTE TESTE:");
        System.out.println("Com 1000 tarefas, pool é ~" + 
                         String.format("%.1f", medirThreadPorTarefa(dados, 1000) / 
                                              (double) medirComPool(dados, 1000, poolSize)) +
                         "x mais rápido!");
    }
    
    /**
     * Executa tarefas usando pool de threads
     */
    static long executarComPool(int[] dados, int numTarefas, int poolSize) {
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        long[] resultados = new long[numTarefas];
        
        int tamanhoBloco = dados.length / numTarefas;
        
        // Enviar tarefas para o pool
        for (int i = 0; i < numTarefas; i++) {
            int inicio = i * tamanhoBloco;
            int fim = (i == numTarefas - 1) ? dados.length : (i + 1) * tamanhoBloco;
            
            pool.execute(new TarefaProcessamento(i, dados, inicio, fim, resultados));
        }
        
        // Finalizar pool e aguardar conclusão
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Agregar resultados
        long total = 0;
        for (long r : resultados) {
            total += r;
        }
        
        return total;
    }
    
    /**
     * Executa criando uma thread por tarefa
     */
    static long executarThreadPorTarefa(int[] dados, int numTarefas) {
        Thread[] threads = new Thread[numTarefas];
        long[] resultados = new long[numTarefas];
        
        int tamanhoBloco = dados.length / numTarefas;
        
        // Criar e iniciar todas as threads
        for (int i = 0; i < numTarefas; i++) {
            int inicio = i * tamanhoBloco;
            int fim = (i == numTarefas - 1) ? dados.length : (i + 1) * tamanhoBloco;
            
            threads[i] = new Thread(new TarefaProcessamento(i, dados, inicio, fim, resultados));
            threads[i].start();
        }
        
        // Aguardar todas
        for (int i = 0; i < numTarefas; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // Agregar resultados
        long total = 0;
        for (long r : resultados) {
            total += r;
        }
        
        return total;
    }
    
    /**
     * Mede tempo usando pool
     */
    static long medirComPool(int[] dados, int numTarefas, int poolSize) {
        // Aquecimento
        executarComPool(dados, numTarefas, poolSize);
        
        // Medir 3 vezes
        long[] tempos = new long[3];
        for (int i = 0; i < 3; i++) {
            long inicio = System.nanoTime();
            executarComPool(dados, numTarefas, poolSize);
            long fim = System.nanoTime();
            tempos[i] = (fim - inicio) / 1_000_000;
        }
        
        return (tempos[0] + tempos[1] + tempos[2]) / 3;
    }
    
    /**
     * Mede tempo criando thread por tarefa
     */
    static long medirThreadPorTarefa(int[] dados, int numTarefas) {
        // Aquecimento
        executarThreadPorTarefa(dados, numTarefas);
        
        // Medir 3 vezes
        long[] tempos = new long[3];
        for (int i = 0; i < 3; i++) {
            long inicio = System.nanoTime();
            executarThreadPorTarefa(dados, numTarefas);
            long fim = System.nanoTime();
            tempos[i] = (fim - inicio) / 1_000_000;
        }
        
        return (tempos[0] + tempos[1] + tempos[2]) / 3;
    }
}