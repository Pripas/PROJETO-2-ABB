/*
 * Classe Main: ponto de entrada da aplicação.
 * Contém o menu principal e a lógica de leitura e gravação do arquivo CSV.
 *
 * O arquivo titles.csv deve estar na pasta raiz do projeto (mesma pasta de src/ e bin/).
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static int proximoIdShow  = 900000;
    private static int proximoIdMovie = 900000;

    public static void main(String[] args) {
        ABB<ProgramaNetFlix> arvore = new ABB<ProgramaNetFlix>();
        Scanner scan = new Scanner(System.in);

        int opcao = -1;
        while (opcao != 8) {
            exibirMenu();
            System.out.print("Escolha uma opção: ");
            try {
                opcao = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1: opcaoLerArquivo(arvore, scan);         break;
                case 2: opcaoAnalises(arvore, scan);           break;
                case 3: opcaoInserirPrograma(arvore, scan);    break;
                case 4: opcaoBuscarPrograma(arvore, scan);     break;
                case 5: opcaoRemoverPrograma(arvore, scan);    break;
                case 6: opcaoExibirAltura(arvore);             break;
                case 7: opcaoSalvarArquivo(arvore, scan);      break;
                case 8: opcaoEncerrar(arvore);                 break;
                default: System.out.println("Opção inválida. Tente novamente.");
            }
        }
        scan.close();
    }

    // exibe o menu principal
    private static void exibirMenu() {
        System.out.println("\n───────────────────────────────────");
        System.out.println("         MENU PRINCIPAL            ");
        System.out.println("───────────────────────────────────");
        System.out.println("  1. Ler dados de arquivo          ");
        System.out.println("  2. Análises de dados             ");
        System.out.println("  3. Inserir Programa              ");
        System.out.println("  4. Buscar Programa               ");
        System.out.println("  5. Remover Programa              ");
        System.out.println("  6. Exibir Altura da Árvore       ");
        System.out.println("  7. Salvar dados em arquivo       ");
        System.out.println("  8. Encerrar a Aplicação          ");
        System.out.println("───────────────────────────────────");
    }

    // ── FUNCIONALIDADE 1: Ler dados de arquivo ───────────────────────────────
    // solicita o nome do CSV, lê os dados e monta a ABB
    private static void opcaoLerArquivo(ABB<ProgramaNetFlix> arvore, Scanner scan) {
        System.out.print("\nInforme o nome do arquivo CSV (ex: titles.csv): ");
        String nomeArquivo = scan.nextLine();

        if (!arvore.isEmpty()) {
            System.out.print("A árvore já contém dados. Deseja limpar e recarregar? (s/n): ");
            String resp = scan.nextLine().toLowerCase();
            if (!resp.equals("s")) {
                System.out.println("Leitura cancelada.");
                return;
            }
            arvore.liberarArvore();
        }

        int[] contadores = lerCSV(nomeArquivo, arvore);

        System.out.println("\n── Resultado da leitura ──────────────────────");
        System.out.println("  Linhas lidas do arquivo  : " + contadores[0]);
        System.out.println("  Inseridos na ABB         : " + contadores[1]);
        System.out.println("  Descartados (incompletos): " + contadores[2]);
        System.out.println("──────────────────────────────────────────────");
    }

    // lê o CSV e insere cada registro válido na ABB; retorna {totalLidas, inseridas, descartadas}
    private static int[] lerCSV(String nomeArquivo, ABB<ProgramaNetFlix> arvore) {
        int lidas = 0, inseridas = 0, descartadas = 0;
        File arquivo = new File(nomeArquivo);
        if (!arquivo.exists()) {
            // tenta encontrar o arquivo na raiz do projeto via classpath (funciona em qualquer máquina)
            String primeiroCaminho = System.getProperty("java.class.path").split(File.pathSeparator)[0];
            File raizProjeto = new File(primeiroCaminho).getAbsoluteFile().getParentFile();
            File alternativo = new File(raizProjeto, nomeArquivo);
            if (alternativo.exists()) {
                arquivo = alternativo;
            } else {
                System.out.println("Arquivo nao encontrado!");
                System.out.println("Caminho tentado: " + arquivo.getAbsolutePath());
                System.out.println("Coloque o " + nomeArquivo + " nessa pasta ou informe o caminho completo.");
                return new int[]{0, 0, 0};
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            reader.readLine(); // descarta o cabeçalho
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.isEmpty()) continue;
                lidas++;
                List<String> campos = parseCsvLinha(linha);
                if (campos.size() < 15) { descartadas++; continue; }
                try {
                    ProgramaNetFlix p = criarProgramaDaCampos(campos);
                    if (p.camposEssenciaisPreenchidos()) {
                        arvore.inserir(p);
                        inseridas++;
                    } else {
                        descartadas++;
                    }
                } catch (NumberFormatException e) {
                    descartadas++;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
            System.out.println("Verifique se o arquivo está na pasta raiz do projeto.");
        }
        return new int[]{lidas, inseridas, descartadas};
    }

    // monta um ProgramaNetFlix a partir dos campos do CSV
    private static ProgramaNetFlix criarProgramaDaCampos(List<String> c) {
        String id                  = c.get(0);
        String title               = c.get(1);
        String showType            = c.get(2);
        String description         = c.get(3);
        int    releaseYear         = parseIntSeguro(c.get(4));
        String ageCertification    = c.get(5);
        int    runtime             = parseIntSeguro(c.get(6));
        String genres              = c.get(7);
        String productionCountries = c.get(8);
        double seasons             = parseDoubleSeguro(c.get(9));
        String imdbId              = c.get(10);
        double imdbScore           = parseDoubleSeguro(c.get(11));
        int    imdbVotes           = (int) parseDoubleSeguro(c.get(12));
        double tmdbPopularity      = parseDoubleSeguro(c.get(13));
        double tmdbScore           = parseDoubleSeguro(c.get(14));
        return new ProgramaNetFlix(id, title, showType, description,
                                   releaseYear, ageCertification, runtime,
                                   genres, productionCountries, seasons,
                                   imdbId, imdbScore, imdbVotes,
                                   tmdbPopularity, tmdbScore);
    }

    // faz o parse de uma linha CSV respeitando campos entre aspas duplas
    private static List<String> parseCsvLinha(String linha) {
        List<String> campos = new ArrayList<String>();
        String campo = "";
        boolean dentroDeAspas = false;
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"') {
                if (dentroDeAspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                    campo += '"';
                    i++;
                } else {
                    dentroDeAspas = !dentroDeAspas;
                }
            } else if (c == ',' && !dentroDeAspas) {
                campos.add(campo);
                campo = "";
            } else {
                campo += c;
            }
        }
        campos.add(campo); // último campo
        return campos;
    }

    // ── FUNCIONALIDADE 2: Análises de dados ──────────────────────────────────
    // sub-menu de análises de dados
    private static void opcaoAnalises(ABB<ProgramaNetFlix> arvore, Scanner scan) {
        if (arvore.isEmpty()) {
            System.out.println("A árvore está vazia. Use a opção 1 para carregar os dados.");
            return;
        }
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n──────────────────────────────────────────────");
            System.out.println("             MENU DE ANÁLISES                 ");
            System.out.println("──────────────────────────────────────────────");
            System.out.println("  1. N filmes com menor tmdb_popularity       ");
            System.out.println("  2. Média IMDB por país de produção          ");
            System.out.println("  3. [em implementação]                       ");
            System.out.println("  4. [em implementação]                       ");
            System.out.println(" 5. [em implementação]                       ");
            System.out.println("  0. Voltar ao menu principal                 ");
            System.out.println("──────────────────────────────────────────────");
            System.out.print("Escolha uma opção: ");
            try {
                opcao = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }
            switch (opcao) {
                case 1: analise1FilmesMenosPopulares(arvore, scan); break;
                case 2: analise2MediaImdbPorPais(arvore, scan);     break;
                case 3:
                case 4:
                case 5: System.out.println("[em implementação]");   break;
                case 0: break;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    // análise 1: exibe os N filmes com menor tmdb_popularity (percurso pré-ordem)
    private static void analise1FilmesMenosPopulares(ABB<ProgramaNetFlix> arvore, Scanner scan) {
        System.out.print("\nInforme N (quantidade de filmes a exibir): ");
        int n;
        try {
            n = Integer.parseInt(scan.nextLine());
            if (n <= 0) { System.out.println("N deve ser maior que zero."); return; }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido."); return;
        }

        // coleta todos os filmes em pré-ordem
        List<ProgramaNetFlix> filmes = new ArrayList<ProgramaNetFlix>();
        coletarFilmesPreOrdem(arvore.getRaiz(), filmes);

        // bubble sort por tmdb_popularity crescente
        for (int i = 0; i < filmes.size() - 1; i++) {
            for (int j = 0; j < filmes.size() - 1 - i; j++) {
                if (filmes.get(j).getTmdbPopularity() > filmes.get(j + 1).getTmdbPopularity()) {
                    ProgramaNetFlix temp = filmes.get(j);
                    filmes.set(j, filmes.get(j + 1));
                    filmes.set(j + 1, temp);
                }
            }
        }

        int limite = n < filmes.size() ? n : filmes.size();
        System.out.println("\n── " + n + " filmes com menor tmdb_popularity (pré-ordem) ────────");
        System.out.println("  #   | Título                                    | Popularity | IMDB  | Ano");
        System.out.println("------+-------------------------------------------+------------+-------+-----");
        for (int i = 0; i < limite; i++) {
            ProgramaNetFlix p = filmes.get(i);
            String titulo = p.getTitle();
            if (titulo.length() > 41) titulo = titulo.substring(0, 38) + "...";
            System.out.println("  " + (i + 1) + "   | " + titulo
                    + espacos(41 - titulo.length())
                    + " | " + p.getTmdbPopularity()
                    + espacos(10 - String.valueOf(p.getTmdbPopularity()).length())
                    + " | " + p.getImdbScore()
                    + "  | " + p.getReleaseYear());
        }
        System.out.println("──────────────────────────────────────────────────────────────────");
    }

    // percorre a ABB em pré-ordem e coleta apenas filmes (MOVIE) na lista
    private static void coletarFilmesPreOrdem(Node<ProgramaNetFlix> no, List<ProgramaNetFlix> lista) {
        if (no == null) return;
        if (no.getValue().getShowType().equals("MOVIE")) {
            lista.add(no.getValue());
        }
        coletarFilmesPreOrdem(no.getFilhoEsquerdo(), lista);
        coletarFilmesPreOrdem(no.getFilhoDireito(), lista);
    }

    // análise 2: calcula a média de imdb_score por país de produção (percurso pós-ordem)
    private static void analise2MediaImdbPorPais(ABB<ProgramaNetFlix> arvore, Scanner scan) {
        System.out.println("\nExemplos de países presentes no dataset: US, GB, FR, DE, JP, IN, CA, AU, IT, ES, KR, MX, BR");
        System.out.print("Informe o código do país (ex: US): ");
        String pais = scan.nextLine().toUpperCase();

        double[] soma = {0.0};
        int[] contador = {0};
        calcularMediaImdbPosOrdem(arvore.getRaiz(), pais, soma, contador);

        System.out.println("\n── Média de imdb_score para produções de: " + pais + " (pós-ordem) ──");
        if (contador[0] == 0) {
            System.out.println("  Nenhum título encontrado para o país \"" + pais + "\".");
            System.out.println("  Verifique se o código está correto (ex: US, GB, FR).");
        } else {
            double media = soma[0] / contador[0];
            System.out.println("  Títulos encontrados : " + contador[0]);
            System.out.println("  Soma dos scores     : " + soma[0]);
            int mediaInt = (int)(media * 100);
            double mediaArredondada = mediaInt / 100.0;
            System.out.println("  Média IMDB Score    : " + mediaArredondada);
        }
        System.out.println("──────────────────────────────────────────────────────────────────");
    }

    // percorre a ABB em pós-ordem e acumula imdb_score dos títulos do país informado
    private static void calcularMediaImdbPosOrdem(Node<ProgramaNetFlix> no, String pais,
                                                   double[] soma, int[] contador) {
        if (no == null) return;
        calcularMediaImdbPosOrdem(no.getFilhoEsquerdo(), pais, soma, contador);
        calcularMediaImdbPosOrdem(no.getFilhoDireito(), pais, soma, contador);
        // visita o nó (pós-ordem): verifica se o país está na lista de produção
        if (no.getValue().getProductionCountries().contains("'" + pais + "'")) {
            soma[0] += no.getValue().getImdbScore();
            contador[0]++;
        }
    }

    // retorna uma String com 'n' espaços (usado na formatação da tabela)
    private static String espacos(int n) {
        String s = "";
        for (int i = 0; i < n; i++) s += " ";
        return s;
    }

    // ── FUNCIONALIDADE 3: Inserir Programa ───────────────────────────────────
    // coleta dados do novo programa e insere na ABB
    private static void opcaoInserirPrograma(ABB<ProgramaNetFlix> arvore, Scanner scan) {
        System.out.println("\n── Inserir Novo Programa ──────────────────────");
        System.out.print("Tipo (MOVIE ou SHOW): ");
        String tipo = scan.nextLine().toUpperCase();
        if (!tipo.equals("MOVIE") && !tipo.equals("SHOW")) {
            System.out.println("Tipo inválido. Use MOVIE ou SHOW.");
            return;
        }

        String id = tipo.equals("SHOW") ? "ts" + (proximoIdShow++) : "tm" + (proximoIdMovie++);
        System.out.println("ID gerado automaticamente: " + id);

        System.out.print("Título: ");
        String title = scan.nextLine();
        System.out.print("Descrição: ");
        String descricao = scan.nextLine();
        int anoLancamento = lerInteiroPositivo(scan, "Ano de lançamento: ");
        System.out.print("Classificação etária (ex: TV-14, R, PG) [Enter para N/A]: ");
        String cert = scan.nextLine();
        int runtime = lerInteiroPositivo(scan, "Duração em minutos: ");
        System.out.print("Gêneros (ex: ['drama','action']): ");
        String generos = scan.nextLine();
        System.out.print("Países de produção (ex: ['US','BR']): ");
        String paises = scan.nextLine();
        double temporadas = tipo.equals("SHOW") ? lerInteiroPositivo(scan, "Número de temporadas: ") : 0;
        System.out.print("IMDB ID (ex: tt1234567): ");
        String imdbId = scan.nextLine();
        double imdbScore = lerDoublePositivo(scan, "IMDB Score (0.0 a 10.0): ");
        int imdbVotes    = lerInteiroPositivo(scan, "IMDB Votos: ");
        double tmdbPop   = lerDoublePositivo(scan, "TMDB Popularidade: ");
        double tmdbScore = lerDoublePositivo(scan, "TMDB Score (0.0 a 10.0): ");

        ProgramaNetFlix novo = new ProgramaNetFlix(id, title, tipo, descricao, anoLancamento,
            cert, runtime, generos, paises, temporadas, imdbId, imdbScore, imdbVotes, tmdbPop, tmdbScore);
        arvore.inserir(novo);
        System.out.println("\nPrograma inserido com sucesso!");
        System.out.println(novo.toStringDetalhado());
    }

    // ── FUNCIONALIDADE 4: Buscar Programa ────────────────────────────────────
    // solicita o ID e busca o programa na ABB
    private static void opcaoBuscarPrograma(ABB<ProgramaNetFlix> arvore, Scanner scan) {
        if (arvore.isEmpty()) { System.out.println("A árvore está vazia."); return; }
        System.out.print("\nInforme o ID do programa a buscar (ex: ts300399): ");
        String id = scan.nextLine();
        ProgramaNetFlix chave = new ProgramaNetFlix(id,"","","",0,"",0,"","",0,"",0,0,0,0);
        arvore.buscar(chave);
    }

    // ── FUNCIONALIDADE 5: Remover Programa ───────────────────────────────────
    // solicita o ID e remove o programa da ABB
    private static void opcaoRemoverPrograma(ABB<ProgramaNetFlix> arvore, Scanner scan) {
        if (arvore.isEmpty()) { System.out.println("A árvore está vazia."); return; }
        System.out.print("\nInforme o ID do programa a remover: ");
        String id = scan.nextLine();
        ProgramaNetFlix chave = new ProgramaNetFlix(id,"","","",0,"",0,"","",0,"",0,0,0,0);
        boolean removido = arvore.eliminar(chave);
        if (removido) System.out.println("Programa \"" + id + "\" removido com sucesso.");
        else          System.out.println("Programa com ID \"" + id + "\" não encontrado.");
    }

    // ── FUNCIONALIDADE 6: Exibir Altura da Árvore ────────────────────────────
    // exibe a altura atual da ABB
    private static void opcaoExibirAltura(ABB<ProgramaNetFlix> arvore) {
        if (arvore.isEmpty()) {
            System.out.println("A árvore está vazia (altura = -1).");
            return;
        }
        int h = arvore.altura();
        System.out.println("\nAltura da árvore ABB: " + h);
    }

    // ── FUNCIONALIDADE 7: Salvar dados em arquivo ────────────────────────────
    // percorre a ABB em ordem e grava cada registro no CSV de saída
    private static void opcaoSalvarArquivo(ABB<ProgramaNetFlix> arvore, Scanner scan) {
        if (arvore.isEmpty()) { System.out.println("A árvore está vazia. Nada a salvar."); return; }
        System.out.print("\nInforme o nome do arquivo de saída (ex: titles_atualizado.csv): ");
        String nomeArquivo = scan.nextLine();
        salvarEmArquivo(arvore.getRaiz(), nomeArquivo);
    }

    // abre o arquivo e inicia o percurso em ordem para gravação
    private static void salvarEmArquivo(Node<ProgramaNetFlix> raiz, String nomeArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write("id,title,type,description,release_year,age_certification," +
                         "runtime,genres,production_countries,seasons,imdb_id," +
                         "imdb_score,imdb_votes,tmdb_popularity,tmdb_score");
            writer.newLine();
            int[] contador = {0};
            salvarEmOrdem(raiz, writer, contador);
            System.out.println("Dados salvos em \"" + nomeArquivo + "\" (" + contador[0] + " registros).");
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    // percurso em ordem recursivo que grava cada nó no arquivo
    private static void salvarEmOrdem(Node<ProgramaNetFlix> no, BufferedWriter writer,
                                      int[] contador) throws IOException {
        if (no == null) return;
        salvarEmOrdem(no.getFilhoEsquerdo(), writer, contador);
        writer.write(no.getValue().toCSV());
        writer.newLine();
        contador[0]++;
        salvarEmOrdem(no.getFilhoDireito(), writer, contador);
    }

    // ── FUNCIONALIDADE 8: Encerrar a Aplicação ───────────────────────────────
    // libera a ABB e encerra o programa
    private static void opcaoEncerrar(ABB<ProgramaNetFlix> arvore) {
        arvore.liberarArvore();
        System.out.println("\nAplicação encerrada. Até logo!");
    }

    // ── Métodos utilitários (usados por várias funcionalidades) ───────────────
    // lê um inteiro positivo do usuário, repetindo até entrada válida
    private static int lerInteiroPositivo(Scanner scan, String mensagem) {
        int valor = -1;
        while (valor <= 0) {
            System.out.print(mensagem);
            try {
                valor = Integer.parseInt(scan.nextLine());
                if (valor <= 0) System.out.println("Digite um valor maior que zero.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
        return valor;
    }

    // lê um double não-negativo do usuário, repetindo até entrada válida
    private static double lerDoublePositivo(Scanner scan, String mensagem) {
        double valor = -1;
        while (valor < 0) {
            System.out.print(mensagem);
            try {
                valor = Double.parseDouble(scan.nextLine());
                if (valor < 0) System.out.println("Digite um valor não negativo.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número decimal.");
            }
        }
        return valor;
    }

    // converte String para int; retorna 0 se vazia ou inválida
    private static int parseIntSeguro(String s) {
        if (s == null || s.isEmpty()) return 0;
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return 0; }
    }

    // converte String para double; retorna 0.0 se vazia ou inválida
    private static double parseDoubleSeguro(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return 0.0; }
    }
}
