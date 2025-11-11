/**
 * Atividade 2 - N threads imprimindo o próprio índice
 * Objetivo: Criar N threads e passar um argumento (o índice)
 */
public class NThread {
    
    /**
     * Runnable que recebe e imprime seu índice
     */
    static class IndexedRunnable implements Runnable {
        private final int index;
        
        // Construtor recebe o índice como parâmetro
        public IndexedRunnable(int index) {
            this.index = index;
        }
        
        @Override
        public void run() {
            System.out.println("Thread " + index + " executando (ID: " + 
                             Thread.currentThread().getId() + ")");
        }
    }
    
    public static void main(String[] args) {
        // Ler N da linha de comando ou usar valor padrão
        int N = 5; // Valor padrão
        
        if (args.length > 0) {
            try {
                N = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Argumento inválido. Usando N=5");
            }
        }
        
        System.out.println("=== Atividade 2 - N Threads ===");
        System.out.println("Criando " + N + " threads...\n");
        
        // Array para armazenar as threads
        Thread[] threads = new Thread[N];
        
        // Criar e iniciar todas as threads
        for (int i = 0; i < N; i++) {
            threads[i] = new Thread(new IndexedRunnable(i));
            threads[i].start();
        }
        
        // Aguardar conclusão de todas
        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("\nTodas as " + N + " threads finalizaram!");
        
        // Teste com N=10
        System.out.println("\n--- Teste com N=10 ---");
        testWithN(10);
    }
    
    /**
     * Método auxiliar para testar com diferentes valores de N
     */
    static void testWithN(int N) {
        Thread[] threads = new Thread[N];
        
        for (int i = 0; i < N; i++) {
            threads[i] = new Thread(new IndexedRunnable(i));
            threads[i].start();
        }
        
        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("Teste com N=" + N + " concluído!");
    }
}

/* COMO PASSAR DADOS PARA UMA THREAD EM JAVA:
 * 
 * 1. Via Construtor (método usado acima):
 *    - Criar uma classe que implementa Runnable
 *    - Adicionar campos privados para armazenar os dados
 *    - Criar construtor que recebe e armazena os dados
 *    - Usar os dados no método run()
 * 
 * 2. Via Lambda (alternativa moderna):
 *    Thread t = new Thread(() -> {
 *        System.out.println("Index: " + i);
 *    });
 *    Obs: 'i' precisa ser final ou effectively final
 * 
 * 3. Via classe anônima:
 *    final int idx = i;
 *    Thread t = new Thread(new Runnable() {
 *        public void run() {
 *            System.out.println("Index: " + idx);
 *        }
 *    });
 */