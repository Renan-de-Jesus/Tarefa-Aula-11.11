import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Atividade 12 - Leitores–Escritores
 * Objetivo: Permitir muitas leituras concorrentes e escrita exclusiva
 */
public class LeitoresEscritores {
    
    // "Banco de dados" simulado
    private static Map<String, String> bancoDados = new HashMap<>();
    
    // ReadWriteLock permite múltiplos leitores OU 1 escritor
    private static ReadWriteLock rwLock = new ReentrantReadWriteLock();
    
    // Lock simples para comparação
    private static final Object simpleLock = new Object();
    
    /**
     * Leitor: consulta dados do banco
     */
    static class Leitor implements Runnable {
        private final int id;
        private final int numConsultas;
        private final boolean usarRWLock;
        
        public Leitor(int id, int numConsultas, boolean usarRWLock) {
            this.id = id;
            this.numConsultas = numConsultas;
            this.usarRWLock = usarRWLock;
        }
        
        @Override
        public void run() {
            Random rand = new Random(id);
            
            for (int i = 0; i < numConsultas; i++) {
                String chave = "chave" + rand.nextInt(10);
                String valor;
                
                if (usarRWLock) {
                    // Usar lock de LEITURA (permite múltiplos leitores)
                    rwLock.readLock().lock();
                    try {
                        valor = bancoDados.get(chave);
                    } finally {
                        rwLock.readLock().unlock();
                    }
                } else {
                    // Usar lock simples (exclusivo)
                    synchronized (simpleLock) {
                        valor = bancoDados.get(chave);
                    }
                }
                
                // Simular processamento da leitura
                if (valor != null && valor.length() > 0) {
                    // Trabalho trivial
                }
            }
        }
    }
    
    /**
     * Escritor: atualiza dados no banco
     */
    static class Escritor implements Runnable {
        private final int id;
        private final int numAtualizacoes;
        private final boolean usarRWLock;
        
        public Escritor(int id, int numAtualizacoes, boolean usarRWLock) {
            this.id = id;
            this.numAtualizacoes = numAtualizacoes;
            this.usarRWLock = usarRWLock;
        }
        
        @Override
        public void run() {
            Random rand = new Random(id + 1000);
            
            for (int i = 0; i < numAtualizacoes; i++) {
                String chave = "chave" + rand.nextInt(10);
                String valor = "valor_" + id + "_" + i;
                
                if (usarRWLock) {
                    // Usar lock de ESCRITA (exclusivo)
                    rwLock.writeLock().lock();
                    try {
                        bancoDados.put(chave, valor);
                    } finally {
                        rwLock.writeLock().unlock();
                    }
                } else {
                    // Usar lock simples (exclusivo)
                    synchronized (simpleLock) {
                        bancoDados.put(chave, valor);
                    }
                }
                
                // Simular trabalho adicional
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Atividade 12 - Leitores-Escritores ===\n");
        
        int numLeitores = 5;
        int numEscritores = 2;
        int consultasPorLeitor = 1000;
        int atualizacoesPorEscritor = 50;
        
        System.out.println("Configuração:");
        System.out.println("- Leitores: " + numLeitores);
        System.out.println("- Escritores: " + numEscritores);
        System.out.println("- Consultas por leitor: " + consultasPorLeitor);
        System.out.println("- Atualizações por escritor: " + atualizacoesPorEscritor);
        System.out.println();
        
        // Inicializar banco de dados
        inicializarBanco();
        
        System.out.println("Comparando ReadWriteLock vs Lock Simples:\n");
        
        // Teste com ReadWriteLock
        long tempoRWLock = testar(numLeitores, numEscritores, 
                                  consultasPorLeitor, atualizacoesPorEscritor, true);
        
        // Teste com lock simples
        long tempoSimples = testar(numLeitores, numEscritores,
                                   consultasPorLeitor, atualizacoesPorEscritor, false);
        
        double speedup = (double) tempoSimples / tempoRWLock;
        double melhoria = ((double) (tempoSimples - tempoRWLock) / tempoSimples) * 100;
        
        System.out.println("\n| Abordagem      | Tempo (ms) |");
        System.out.println("|----------------|------------|");
        System.out.printf("| ReadWriteLock  | %10d |\n", tempoRWLock);
        System.out.printf("| Lock Simples   | %10d |\n", tempoSimples);
        System.out.println();
        System.out.printf("Speedup: %.2fx\n", speedup);
        System.out.printf("Melhoria: %.1f%%\n", melhoria);
        
        System.out.println("\n--- POR QUE LEITORES-ESCRITORES ESCALA MELHOR ---");
        System.out.println();
        System.out.println("LOCK SIMPLES (synchronized):");
        System.out.println("- Acesso EXCLUSIVO sempre");
        System.out.println("- Leitores bloqueiam outros leitores (desnecessário!)");
        System.out.println("- Serializa TODAS as operações");
        System.out.println("- Baixa concorrência");
        System.out.println();
        System.out.println("READ-WRITE LOCK:");
        System.out.println("- Múltiplos LEITORES simultâneos (não conflitam)");
        System.out.println("- Escritor tem acesso EXCLUSIVO (bloqueia tudo)");
        System.out.println("- Alta concorrência nas leituras");
        System.out.println("- Ideal quando leituras >> escritas");
        System.out.println();
        System.out.println("QUANDO USAR:");
        System.out.println("✓ ReadWriteLock:");
        System.out.println("  - Muitas leituras, poucas escritas");
        System.out.println("  - Leituras são rápidas");
        System.out.println("  - Ex: caches, configurações, índices");
        System.out.println();
        System.out.println("✓ Lock Simples:");
        System.out.println("  - Escritas frequentes");
        System.out.println("  - Seções críticas pequenas");
        System.out.println("  - Simplicidade é prioridade");
        System.out.println();
        System.out.println("CARACTERÍSTICAS DO RWLOCK:");
        System.out.println("- Read lock: compartilhado, não exclusivo");
        System.out.println("- Write lock: exclusivo, bloqueia tudo");
        System.out.println("- Fairness: pode priorizar leitores ou escritores");
        System.out.println("- Overhead: maior que lock simples");
        System.out.println();
        System.out.println("NESTE TESTE:");
        System.out.println("Razão leitura:escrita = " + 
                         (numLeitores * consultasPorLeitor) + ":" + 
                         (numEscritores * atualizacoesPorEscritor));
        System.out.println("Com muitas leituras, RWLock permite paralelismo!");
    }
    
    /**
     * Inicializa o banco de dados com valores
     */
    static void inicializarBanco() {
        bancoDados.clear();
        for (int i = 0; i < 10; i++) {
            bancoDados.put("chave" + i, "valor_inicial_" + i);
        }
    }
    
    /**
     * Executa teste com a abordagem especificada
     */
    static long testar(int numLeitores, int numEscritores, 
                      int consultas, int atualizacoes, boolean usarRWLock) {
        
        String tipo = usarRWLock ? "ReadWriteLock" : "Lock Simples";
        System.out.println("Testando com " + tipo + "...");
        
        // Aquecimento
        executar(numLeitores, numEscritores, consultas / 10, atualizacoes / 10, usarRWLock);
        
        // Medir 3 vezes
        long[] tempos = new long[3];
        for (int i = 0; i < 3; i++) {
            inicializarBanco();
            long inicio = System.nanoTime();
            executar(numLeitores, numEscritores, consultas, atualizacoes, usarRWLock);
            long fim = System.nanoTime();
            tempos[i] = (fim - inicio) / 1_000_000;
        }
        
        long media = (tempos[0] + tempos[1] + tempos[2]) / 3;
        System.out.println(tipo + " concluído: " + media + " ms");
        
        return media;
    }
    
    /**
     * Executa uma bateria de leitores e escritores
     */
    static void executar(int numLeitores, int numEscritores,
                        int consultas, int atualizacoes, boolean usarRWLock) {
        
        Thread[] threads = new Thread[numLeitores + numEscritores];
        
        // Criar leitores
        for (int i = 0; i < numLeitores; i++) {
            threads[i] = new Thread(new Leitor(i, consultas, usarRWLock));
            threads[i].start();
        }
        
        // Criar escritores
        for (int i = 0; i < numEscritores; i++) {
            threads[numLeitores + i] = new Thread(new Escritor(i, atualizacoes, usarRWLock));
            threads[numLeitores + i].start();
        }
        
        // Aguardar todos
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/* READWRITELOCK:
 * 
 * Regras de acesso:
 * 1. Múltiplos threads podem ter read lock simultaneamente
 * 2. Apenas 1 thread pode ter write lock
 * 3. Write lock é exclusivo (bloqueia reads e writes)
 * 4. Read lock bloqueia apenas writes
 * 
 * Implementação típica:
 * - Contador de leitores ativos
 * - Flag de escritor ativo
 * - Fila de espera (leitores e escritores)
 * 
 * Políticas de fairness:
 * - Fair: Ordem de chegada (evita starvation)
 * - Non-fair: Pode favorecer reads (mais throughput)
 * 
 * Quando NÃO usar:
 * - Escritas muito frequentes
 * - Seções críticas muito curtas
 * - Overhead do RWLock domina
 */