import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

/**
 * Atividade 7 - Barreira de sincronização (duas fases)
 * Objetivo: Garantir que todas as threads terminem a Fase 1 antes de iniciar a Fase 2
 */
public class Barreira {
    
    /**
     * Worker que executa duas fases com barreira de sincronização
     */
    static class WorkerComBarreira implements Runnable {
        private final int id;
        private final CyclicBarrier barreira;
        
        public WorkerComBarreira(int id, CyclicBarrier barreira) {
            this.id = id;
            this.barreira = barreira;
        }
        
        @Override
        public void run() {
            try {
                // FASE 1: Preparação
                System.out.println("[Thread " + id + "] Iniciando Fase 1...");
                
                // Simular trabalho da Fase 1 (tempo variável)
                Thread.sleep(id * 100); // Threads terminam em tempos diferentes
                
                System.out.println("[Thread " + id + "] Concluiu Fase 1. Aguardando na barreira...");
                
                // BARREIRA: Aguardar todas as threads
                barreira.await();
                
                // FASE 2: Processamento (só começa quando TODAS terminarem Fase 1)
                System.out.println("[Thread " + id + "] Iniciando Fase 2!");
                
                // Simular trabalho da Fase 2
                Thread.sleep(50);
                
                System.out.println("[Thread " + id + "] Concluiu Fase 2.");
                
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Atividade 7 - Barreira de Sincronização ===\n");
        
        int N = 5;
        
        System.out.println("Criando " + N + " threads com barreira...");
        System.out.println("Cada thread termina Fase 1 em tempo diferente.");
        System.out.println("NENHUMA thread pode iniciar Fase 2 até TODAS terminarem Fase 1.\n");
        
        // Criar barreira para N threads
        // A ação no construtor é executada quando todas chegam na barreira
        CyclicBarrier barreira = new CyclicBarrier(N, () -> {
            System.out.println("\n>>> BARREIRA ALCANÇADA! Todas threads terminaram Fase 1.");
            System.out.println(">>> Liberando todas para Fase 2...\n");
        });
        
        Thread[] threads = new Thread[N];
        
        // Criar e iniciar threads
        for (int i = 0; i < N; i++) {
            threads[i] = new Thread(new WorkerComBarreira(i, barreira));
            threads[i].start();
        }
        
        // Aguardar todas
        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("\n=== Todas as threads finalizadas! ===");
        
        // Explicação
        System.out.println("\n--- ANÁLISE DOS LOGS ---");
        System.out.println("Observe que:");
        System.out.println("1. Threads terminam Fase 1 em momentos diferentes");
        System.out.println("2. As primeiras threads AGUARDAM na barreira");
        System.out.println("3. Quando a ÚLTIMA thread chega, a barreira é liberada");
        System.out.println("4. TODAS iniciam Fase 2 simultaneamente");
        System.out.println("5. Sincronização perfeita entre fases!");
        
        System.out.println("\n--- USO DE BARREIRAS ---");
        System.out.println("CyclicBarrier é útil para:");
        System.out.println("- Algoritmos por etapas (iterações sincronizadas)");
        System.out.println("- Simulações com fases temporais");
        System.out.println("- Processamento paralelo com dependências");
        System.out.println("- Coordenação de workers em pipelines");
    }
}

/* EXPLICAÇÃO DA BARREIRA:
 * 
 * CyclicBarrier funciona como um ponto de encontro:
 * 
 * 1. Cada thread chama barreira.await()
 * 2. A thread BLOQUEIA até que todas as N threads chamem await()
 * 3. Quando a última chega, TODAS são liberadas simultaneamente
 * 4. "Cyclic" = pode ser reutilizada para múltiplas fases
 * 
 * Diferença de CountDownLatch:
 * - CountDownLatch: conta regressiva, uma vez só
 * - CyclicBarrier: ponto de encontro, reutilizável
 * 
 * Exemplo de saída esperada:
 * [Thread 0] Fase 1...
 * [Thread 0] Aguardando barreira...
 * [Thread 1] Fase 1...
 * [Thread 1] Aguardando barreira...
 * ...
 * [Thread 4] Fase 1...
 * [Thread 4] Aguardando barreira...
 * >>> BARREIRA ALCANÇADA!
 * [Thread 0] Iniciando Fase 2!
 * [Thread 1] Iniciando Fase 2!
 * ...
 */