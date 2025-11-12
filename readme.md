# 📚 Guia de Compilação e Execução - Atividades de Programação Concorrente

Este guia contém instruções detalhadas para compilar e executar todas as 12 atividades de programação concorrente em Java.

## 📋 Pré-requisitos

### Verificar Instalação do Java

```bash
# Verificar versão do Java
java -version

# Verificar versão do compilador
javac -version
```

Você precisa do **Java JDK 8 ou superior**. Se não tiver instalado:

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install default-jdk
```

**Windows:**
- Baixe o JDK do site da Oracle: https://www.oracle.com/java/technologies/downloads/
- Ou use o OpenJDK: https://adoptium.net/

**macOS:**
```bash
brew install openjdk
```

---

## 📁 Organização dos Arquivos

Crie uma estrutura de diretórios:

```
atividades-concorrencia/
├── HelloThread.java
├── NThreads.java
├── RaceCondition.java
├── ExclusaoMutua.java
├── Granularidade.java
├── Atomico.java
├── Barreira.java
├── ProdutorConsumidor.java
├── SomaParalela.java
├── MonteCarlo.java
├── ThreadPool.java
├── LeitoresEscritores.java
└── README.md
```

---

## 🔨 Compilação

### Compilar um Arquivo Individual

```bash
javac HelloThread.java
```

### Compilar Todos os Arquivos de Uma Vez

```bash
# Linux/macOS
javac *.java

# Windows (PowerShell)
javac *.java

# Windows (CMD)
for %f in (*.java) do javac %f
```

### Verificar Compilação

Após compilar, você verá arquivos `.class` criados:
```bash
ls *.class
# Ou no Windows:
dir *.class
```

---

## ▶️ Execução

### Executar Atividades Simples (1-8, 12)

```bash
# Atividade 1
java HelloThread

# Atividade 2 (com argumento N=5)
java NThreads 5

# Atividade 2 (com argumento N=10)
java NThreads 10

# Atividade 3
java RaceCondition

# Atividade 4
java ExclusaoMutua

# Atividade 5
java Granularidade

# Atividade 6
java Atomico

# Atividade 7
java Barreira

# Atividade 8
java ProdutorConsumidor

# Atividade 12
java LeitoresEscritores
```

### Executar Atividades com Medição (9, 10, 11)

Estas atividades podem demorar mais e exibem tabelas de desempenho:

```bash
# Atividade 9 - Soma Paralela (pode demorar 1-2 minutos)
java SomaParalela

# Atividade 10 - Monte Carlo (pode demorar 2-3 minutos)
java MonteCarlo

# Atividade 11 - Thread Pool
java ThreadPool
```

---

## 💾 Capturar Saída para Arquivo

Para salvar os resultados em arquivo de texto:

```bash
# Linux/macOS/Windows PowerShell
java SomaParalela > resultado_atividade9.txt

# Capturar saída e erros
java SomaParalela > resultado_atividade9.txt 2>&1
```

---

## 📊 Script para Executar Tudo

### Linux/macOS

Crie um arquivo `executar_todas.sh`:

```bash
#!/bin/bash

echo "==================================="
echo "Compilando todas as atividades..."
echo "==================================="
javac *.java

if [ $? -ne 0 ]; then
    echo "Erro na compilação!"
    exit 1
fi

echo ""
echo "==================================="
echo "Executando atividades..."
echo "==================================="

echo ""
echo "--- Atividade 1 ---"
java HelloThread

echo ""
echo "--- Atividade 2 (N=5) ---"
java NThreads 5

echo ""
echo "--- Atividade 3 ---"
java RaceCondition

echo ""
echo "--- Atividade 4 ---"
java ExclusaoMutua

echo ""
echo "--- Atividade 5 ---"
java Granularidade

echo ""
echo "--- Atividade 6 ---"
java Atomico

echo ""
echo "--- Atividade 7 ---"
java Barreira

echo ""
echo "--- Atividade 8 ---"
java ProdutorConsumidor

echo ""
echo "--- Atividade 9 (pode demorar) ---"
java SomaParalela

echo ""
echo "--- Atividade 10 (pode demorar) ---"
java MonteCarlo

echo ""
echo "--- Atividade 11 ---"
java ThreadPool

echo ""
echo "--- Atividade 12 ---"
java LeitoresEscritores

echo ""
echo "==================================="
echo "Todas as atividades concluídas!"
echo "==================================="
```

Dar permissão e executar:
```bash
chmod +x executar_todas.sh
./executar_todas.sh
```

### Windows (PowerShell)

Crie um arquivo `executar_todas.ps1`:

```powershell
Write-Host "===================================" -ForegroundColor Cyan
Write-Host "Compilando todas as atividades..." -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan
javac *.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "Erro na compilação!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "===================================" -ForegroundColor Cyan
Write-Host "Executando atividades..." -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "--- Atividade 1 ---" -ForegroundColor Yellow
java HelloThread

Write-Host ""
Write-Host "--- Atividade 2 (N=5) ---" -ForegroundColor Yellow
java NThreads 5

Write-Host ""
Write-Host "--- Atividade 3 ---" -ForegroundColor Yellow
java RaceCondition

Write-Host ""
Write-Host "--- Atividade 4 ---" -ForegroundColor Yellow
java ExclusaoMutua

Write-Host ""
Write-Host "--- Atividade 5 ---" -ForegroundColor Yellow
java Granularidade

Write-Host ""
Write-Host "--- Atividade 6 ---" -ForegroundColor Yellow
java Atomico

Write-Host ""
Write-Host "--- Atividade 7 ---" -ForegroundColor Yellow
java Barreira

Write-Host ""
Write-Host "--- Atividade 8 ---" -ForegroundColor Yellow
java ProdutorConsumidor

Write-Host ""
Write-Host "--- Atividade 9 (pode demorar) ---" -ForegroundColor Yellow
java SomaParalela

Write-Host ""
Write-Host "--- Atividade 10 (pode demorar) ---" -ForegroundColor Yellow
java MonteCarlo

Write-Host ""
Write-Host "--- Atividade 11 ---" -ForegroundColor Yellow
java ThreadPool

Write-Host ""
Write-Host "--- Atividade 12 ---" -ForegroundColor Yellow
java LeitoresEscritores

Write-Host ""
Write-Host "===================================" -ForegroundColor Green
Write-Host "Todas as atividades concluídas!" -ForegroundColor Green
Write-Host "===================================" -ForegroundColor Green
```

Executar:
```powershell
.\executar_todas.ps1
```

---

## 🐛 Solução de Problemas

### Erro: "javac não é reconhecido"

**Problema:** Java não está no PATH do sistema.

**Solução Windows:**
1. Encontre onde o Java foi instalado (ex: `C:\Program Files\Java\jdk-17\bin`)
2. Adicione ao PATH:
   - Pesquise "Variáveis de Ambiente"
   - Edite a variável PATH
   - Adicione o caminho do Java

**Solução Linux/macOS:**
```bash
export PATH=$PATH:/usr/lib/jvm/java-17-openjdk/bin
# Adicione ao ~/.bashrc ou ~/.zshrc para tornar permanente
```

### Erro: "OutOfMemoryError"

Se as atividades 9 ou 10 derem erro de memória:

```bash
# Aumentar heap size
java -Xmx2G Atividade9SomaParalela
java -Xmx4G Atividade10MonteCarlo
```

### Erro: "ClassNotFoundException"

Certifique-se de estar no diretório correto onde os arquivos `.class` estão:

```bash
pwd  # Verificar diretório atual
ls *.class  # Ver se os arquivos compilados estão lá
```

### Resultados Diferentes em Cada Execução

**Normal para as seguintes atividades:**
- **Atividade 3**: Race condition causa perdas variáveis
- **Atividade 7**: Ordem de logs pode variar
- **Atividade 8**: Ordem de processamento varia
- **Atividade 10**: Estimativa de π varia levemente

**Deve ser consistente:**
- **Atividades 4-6**: Valor final deve estar correto
- **Atividade 9**: Soma deve bater com sequencial

---

## 📈 Tabelas para Relatório

As atividades 9, 10 e 11 já geram tabelas formatadas. Exemplo de saída:

```
| Threads | Tempo (ms) | Speedup | Eficiência | Correto? |
|---------|------------|---------|------------|----------|
|       1 |       1250 |    1.00 |    100.00% |    Sim ✓ |
|       2 |        680 |    1.84 |     91.91% |    Sim ✓ |
|       4 |        380 |    3.29 |     82.24% |    Sim ✓ |
|       8 |        220 |    5.68 |     71.02% |    Sim ✓ |
```

Você pode copiar essas tabelas diretamente para seu relatório!

---

## 🎯 Dicas Importantes

1. **Feche outros programas** antes de executar as medições (atividades 9-11)
2. **Execute múltiplas vezes** se os resultados variarem muito
3. **Anote as especificações** do seu computador (CPU, RAM, SO)
4. **Não modifique** os valores de teste durante as medições
5. **Aquecimento JIT** já está implementado nos códigos

---

## 📞 Referências

- [Documentação Java Concurrency](https://docs.oracle.com/javase/tutorial/essential/concurrency/)
- [Java Thread API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Thread.html)
- [Executors Framework](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Executors.html)

---

## 🆘 Suporte

Se encontrar problemas:

1. Verifique a versão do Java: `java -version`
2. Confirme que está no diretório correto: `pwd` ou `cd`
3. Verifique se os arquivos foram compilados: `ls *.class`
4. Tente recompilar tudo: `javac *.java`
5. Execute com mais memória: `java -Xmx4G NomeDaClasse`

---