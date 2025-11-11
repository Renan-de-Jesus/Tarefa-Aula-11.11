/**
 * Atividade 1 - Uma thread "hello"
 * Objetivo: Criar, iniciar e aguardar o término de uma thread
 */
public class ThreadHello {
    
    /**
     * Classe que implementa Runnable para executar em uma thread
     */
    static class HelloRunnable implements Runnable {
        @Override
        public void run() {
            // Executa na thread criada
            System.out.println("Hello da Thread: " + Thread.currentThread().getName());
            System.out.println("Thread ID: " + Thread.currentThread().getId());
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Atividade 1 - Thread Hello ===");
        System.out.println("Main thread: " + Thread.currentThread().getName());
        
        // 1. Criar a thread
        Thread thread = new Thread(new HelloRunnable());
        
        // 2. Iniciar a thread
        thread.start();
        
        try {
            // 3. Aguardar conclusão da thread (join)
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Thread finalizada. Voltando à main.");
        
        // Explicação do ciclo de vida
        System.out.println("\n--- Ciclo de Vida da Thread ---");
        System.out.println("1. NEW: Thread criada mas não iniciada");
        System.out.println("2. RUNNABLE: Após start(), pronta para executar");
        System.out.println("3. RUNNING: Executando o método run()");
        System.out.println("4. TERMINATED: Após conclusão do run()");
        System.out.println("5. join() faz a main aguardar a conclusão");
    }
}