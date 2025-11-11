import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Atividade 8 - Produtor–Consumidor com fila bloqueante
 * Objetivo: Usar fila thread-safe para coordenar produtores/consumidores
 */
public class ProdutorConsumidor {
    
    // Item sentinela para sinalizar término
    private static final Integer POISON_PILL = -1;
    
    // Contadores atômicos para estatísticas
    private static AtomicInteger totalProduzido = new AtomicInteger(0);
    private static AtomicInteger totalConsumido = new AtomicInteger(0);
    
    /**
     * Produtor: insere itens na fila
     */
    static class Produtor implements Runnable {
        private final int id;
        private final BlockingQueue<Integer> fila;
        private final int numItens;
        
        public Produtor(int id, BlockingQueue<Integer> fila, int numItens) {
            this.id = id;
            this.fila = fila;
            this.numItens = numItens;
        }
        
        @Override
        public void run() {
            try {
                for (int i = 0; i < numItens; i++) {
                    int item = id * 1000 + i; // ID único do item
                    fila.put(item); // Bloqueia se fila cheia
                    totalProduzido.incrementAndGet();
                    
                    if (i % 25 == 0) { // Log a cada 25 itens
                        System.out.println("[Produtor " + id + "] Produziu item " + item + 
                                         " (total: " + totalProduzido.get() + ")");
                    }
                    
                    // Simular trabalho de produção
                    Thread.sleep(1);
                }
                
                System.out.println("[Produtor " + id + "] Finalizou produção de " + 
                                 numItens + " itens.");
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Consumidor: retira e processa itens da fila
     */
    static class Consumidor implements Runnable {
        private final int id;
        private final BlockingQueue<Integer> fila;
        
        public Consumidor(int id, BlockingQueue<Integer> fila) {
            this.id = id;
            this.fila = fila;
        }
        
        @Override
        public void run() {
            try {
                int consumidos = 0;
                
                while (true) {
                    Integer item = fila.take(); // Bloqueia se fila vazia
                    
                    // Verificar sentinela de término
                    if (item.equals(POISON_PILL)) {
                        System.out.println("[Consumidor " + id + "] Recebeu sinal de término. " +
                                         "Consumiu " + consumidos + " itens.");
                        fila.put(POISON_PILL); // Recolocar para outros consumidores
                        break;
                    }
                    
                    // Processar item
                    consumidos++;
                    totalConsumido.incrementAndGet();
                    
                    if (consumidos % 25 == 0) { // Log a cada 25 itens
                        System.out.println("[Consumidor " + id + "] Processou item " + item + 
                                         " (total: " + totalConsumido.get() + ")");
                    }
                    
                    // Simular trabalho de consumo
                    Thread.sleep(2);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Atividade 8 - Produtor-Consumidor ===\n");
        
        int numProdutores = 2;
        int numConsumidores = 2;
        int itensPorProdutor = 100;
        int totalItens = numProdutores * itensPorProdutor;
        
        System.out.println("Configuração:");
        System.out.println("- Produtores: " + numProdutores);
        System.out.println("- Consumidores: " + numConsumidores);
        System.out.println("- Itens por produtor: " + itensPorProdutor);
        System.out.println("- Total de itens: " + totalItens);
        System.out.println();
        
        // Criar fila bloqueante (capacidade limitada)
        BlockingQueue<Integer> fila = new LinkedBlockingQueue<>(50);
        
        // Criar e iniciar produtores
        Thread[] produtores = new Thread[numProdutores];
        for (int i = 0; i < numProdutores; i++) {
            produtores[i] = new Thread(new Produtor(i, fila, itensPorProdutor));
            produtores[i].start();
        }
        
        // Criar e iniciar consumidores
        Thread[] consumidores = new Thread[numConsumidores];
        for (int i = 0; i < numConsumidores; i++) {
            consumidores[i] = new Thread(new Consumidor(i, fila));
            consumidores[i].start();
        }
        
        // Aguardar produtores terminarem
        for (int i = 0; i < numProdutores; i++) {
            try {
                produtores[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("\nTodos os produtores finalizaram!");
        
        // Enviar sinal de término (poison pill)
        try {
            fila.put(POISON_PILL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Aguardar consumidores terminarem
        for (int i = 0; i < numConsumidores; i++) {
            try {
                consumidores[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("\n=== RESULTADO ===");
        System.out.println("Total produzido: " + totalProduzido.get());
        System.out.println("Total consumido: " + totalConsumido.get());
        System.out.println("Fila final: " + fila.size() + " itens");
        
        boolean correto = (totalProduzido.get() == totalConsumido.get() && 
                          totalProduzido.get() == totalItens);
        System.out.println("Correto: " + (correto ? "SIM ✓" : "NÃO ✗"));
        
        System.out.println("\n--- VANTAGENS DA BLOCKINGQUEUE ---");
        System.out.println("1. Thread-safe: Sincronização automática");
        System.out.println("2. Bloqueia automaticamente:");
        System.out.println("   - put() bloqueia se fila cheia");
        System.out.println("   - take() bloqueia se fila vazia");
        System.out.println("3. Controle de fluxo: Produtor não sobrecarrega consumidor");
        System.out.println("4. Sem race conditions: Implementação correta garantida");
        System.out.println("5. Poison pill pattern: Sinalização elegante de término");
    }
}

/* BLOCKINGQUEUE:
 * 
 * Métodos principais:
 * - put(E e): Insere, espera se cheia
 * - take(): Remove, espera se vazia
 * - offer(E e): Insere, retorna false se cheia
 * - poll(): Remove, retorna null se vazia
 * 
 * Implementações:
 * - LinkedBlockingQueue: Lista encadeada, capacidade opcional
 * - ArrayBlockingQueue: Array fixo, capacidade obrigatória
 * - PriorityBlockingQueue: Heap, sem limite
 * 
 * POISON PILL:
 * Item especial que sinaliza fim da produção.
 * Consumidor o reconhece e reinsere para outros consumidores.
 */