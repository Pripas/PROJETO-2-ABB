
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


//fica repetindo o menu até o usuário escolher a opção 8.
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

    //Essa parte lê o titles.csv transforma cada linha em objeto ProgramaNetFlix 
    // filtra apenas filmes latinos dos 5 países e insere na ABB
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
        System.out.println("  Descartados pelo filtro   : " + contadores[2]);
        System.out.println("──────────────────────────────────────────────");
    }

    // lê o CSV e insere cada registro válido na ABB
    private static int[] lerCSV(String nomeArquivo, ABB<ProgramaNetFlix> arvore) {
    int lidas = 0, inseridas = 0, descartadas = 0;
    File arquivo = new File(nomeArquivo);

    if (!arquivo.exists()) {
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
        reader.readLine();
        String linha;

        while ((linha = reader.readLine()) != null) {
            if (linha.isEmpty()) continue;

            lidas++;
            List<String> campos = parseCsvLinha(linha);

            if (campos.size() < 15) {
                descartadas++;
                continue;
            }

            try {
                ProgramaNetFlix p = criarProgramaDaCampos(campos);


                //antes de inserir na árvore o programa pergunta se é latino se true entra na ABB
                if (p.camposEssenciaisPreenchidos() && ehFilmeLatinoSelecionado(p)) {
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

    // pega os 15 campos do CSV e monta um objeto ProgramaNetFlix
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

    // serve separar os campos da linha do csv respeitando virgulas dentro de aspas
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



    //mostra o submenu com as 5 estatísticas e chama o método correto para cada uma
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
            System.out.println("  1. Top 10 filmes latinos com maior imdb_score");
            System.out.println("  2. N filmes latinos com menor tmdb_popularity");
            System.out.println("  3. Média de imdb_score por país latino       ");
            System.out.println("  4. Filmes por gênero com imdb_score > 7.0    ");
            System.out.println("  5. Média IMDB: curta x longa duração         ");
            System.out.println("  0. Voltar ao menu principal          ");
            System.out.println("──────────────────────────────────────────────");
            System.out.print("Escolha uma opção: ");
            try {
                opcao = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }
           switch (opcao) {
                case 1: analiseTop10MaiorImdb(arvore);              break;
                case 2: analiseFilmesMenosPopulares(arvore, scan);  break;
                case 3: analiseMediaImdbPorPais(arvore, scan); break;
                case 4: analiseFilmesPorGeneroMaiorQueSete(arvore, scan); break;
                case 5: analiseMediaCurtaLongaDuracao(arvore); break;
                case 0: break;
                default: System.out.println("Opção inválida.");
            }
        }
    }


    // Estatística 1 -  exibe os 10 filmes latinos com maior imdb_score!!!!!
    // utiliza percurso em ordem para coletar os filmes da ABB
    private static void analiseTop10MaiorImdb(ABB<ProgramaNetFlix> arvore) {
        List<ProgramaNetFlix> filmes = new ArrayList<ProgramaNetFlix>();

        coletarFilmesEmOrdem(arvore.getRaiz(), filmes);


        //INSERTION SORT -
        /* porque decidimos usar: mais eficiente em listas parcialmente ordenadas(na classe ABB)
         e possuir melhor desempenho prático em pequenos conjuntos de dados */
        for (int i = 1; i < filmes.size(); i++) {
        ProgramaNetFlix atual = filmes.get(i);
        int j = i - 1;

        while (j >= 0 &&
            filmes.get(j).getImdbScore() < atual.getImdbScore()) {

            filmes.set(j + 1, filmes.get(j));
            j--;
        }

    filmes.set(j + 1, atual);
}

        int limite = Math.min(10, filmes.size());

        System.out.println("\n── TOP 10 FILMES LATINOS COM MAIOR IMDB SCORE ──");
        System.out.println(" #  | Título                                    | IMDB | Ano");
        System.out.println("----+-------------------------------------------+------+------");

        for (int i = 0; i < limite; i++) {
            ProgramaNetFlix p = filmes.get(i);
            String titulo = p.getTitle();

            if (titulo.length() > 41) {
                titulo = titulo.substring(0, 38) + "...";
            }

            System.out.println(" " + (i + 1) + "  | "
                    + titulo
                    + espacos(41 - titulo.length())
                    + " | " + p.getImdbScore()
                    + "  | " + p.getReleaseYear());
        }

        System.out.println("────────────────────────────────────────────────");
}
    private static double arredondarDuasCasas(double valor) {
    int temp = (int) (valor * 100);
    return temp / 100.0;
}



    // Estatística 2 - pede um valor N e mostra os N filmes com menor tmdb_popularity. Usa pré-ordem!!!
    private static void analiseFilmesMenosPopulares(ABB<ProgramaNetFlix> arvore, Scanner scan) {
        System.out.print("\nInforme N (quantidade de filmes a exibir): ");
        int n;
        try {
            n = Integer.parseInt(scan.nextLine());
            if (n <= 0) { System.out.println("N deve ser maior que zero."); return; }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido."); return;
        }

        

        // Esses método percorrea ABB e colocam os filmes em uma lista para depois ordenar/analisar - PRE ORDEM!!!!!

        List<ProgramaNetFlix> filmes = new ArrayList<ProgramaNetFlix>();
        coletarFilmesPreOrdem(arvore.getRaiz(), filmes);

        // INSERTION SORT - eficiente e simples de implementar
        for (int i = 1; i < filmes.size(); i++) {
            ProgramaNetFlix atual = filmes.get(i);
            int j = i - 1;

            while (j >= 0 &&
                filmes.get(j).getTmdbPopularity() > atual.getTmdbPopularity()) {

                filmes.set(j + 1, filmes.get(j));
                j--;
    }

    filmes.set(j + 1, atual);
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

    // metodo em ordem!!!!!!!!!!
    private static void coletarFilmesEmOrdem(Node<ProgramaNetFlix> no, List<ProgramaNetFlix> lista) {
        if (no == null) return;

        coletarFilmesEmOrdem(no.getFilhoEsquerdo(), lista);

        if (no.getValue().getShowType().equals("MOVIE")) {
            lista.add(no.getValue());
        }

        coletarFilmesEmOrdem(no.getFilhoDireito(), lista);
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

    // Estatística 3 - Calcula a média do imdb_score para um país escolhido entre BR, MX, AR, CL e CO - Usa pós-ordem!!!!!!!!
    private static void analiseMediaImdbPorPais(ABB<ProgramaNetFlix> arvore, Scanner scan) {
        System.out.println("\nPaíses disponíveis para análise:");
        System.out.println("BR = Brasil");
        System.out.println("MX = México");
        System.out.println("AR = Argentina");
        System.out.println("CL = Chile");
        System.out.println("CO = Colômbia");

        System.out.print("Informe o código do país (ex: BR): ");
        String pais = scan.nextLine().toUpperCase();

        double[] soma = {0.0};
        int[] contador = {0};
        calcularMediaImdbPosOrdem(arvore.getRaiz(), pais, soma, contador);

        System.out.println("\n── Média de imdb_score para produções de: " + pais + " (pós-ordem) ──");
        if (contador[0] == 0) {
            System.out.println("  Nenhum título encontrado para o país \"" + pais + "\".");
            System.out.println("  Verifique se o código informado é BR, MX, AR, CL ou CO.");
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

    // retorna uma String com 'n' espaços- SO PRA FORMATAR (CHAT SUGERIU)
    private static String espacos(int n) {
        String s = "";
        for (int i = 0; i < n; i++) s += " ";
        return s;
    }


    //Estatística 4 - Pede um gênero e lista filmes desse gênero com imdb_score > 7.0 - Usa percurso em largura com fila!!
    private static void analiseFilmesPorGeneroMaiorQueSete(ABB<ProgramaNetFlix> arvore, Scanner scan) {
    System.out.print("\nInforme o gênero desejado (ex: drama, comedy, documentary): ");
    String genero = scan.nextLine().toLowerCase();

    List<ProgramaNetFlix> filmes = new ArrayList<ProgramaNetFlix>();
    coletarFilmesEmLarguraPorGenero(arvore.getRaiz(), filmes, genero);

    System.out.println("\n── FILMES DO GÊNERO \"" + genero + "\" COM IMDB > 7.0 ──");
    System.out.println(" #  | Título                                    | IMDB | Ano");
    System.out.println("----+-------------------------------------------+------+------");

    if (filmes.isEmpty()) {
        System.out.println("Nenhum filme encontrado para esse gênero com imdb_score > 7.0.");
    } else {
        for (int i = 0; i < filmes.size(); i++) {
            ProgramaNetFlix p = filmes.get(i);
            String titulo = p.getTitle();

            if (titulo.length() > 41) {
                titulo = titulo.substring(0, 38) + "...";
            }

            System.out.println(" " + (i + 1) + "  | "
                    + titulo
                    + espacos(41 - titulo.length())
                    + " | " + p.getImdbScore()
                    + "  | " + p.getReleaseYear());
        }
    }

    System.out.println("────────────────────────────────────────────────");
}

    private static void coletarFilmesEmLarguraPorGenero(
        Node<ProgramaNetFlix> raiz,
        List<ProgramaNetFlix> lista,
        String genero) {

    if (raiz == null) return;

    LinkedList<Node<ProgramaNetFlix>> fila = new LinkedList<Node<ProgramaNetFlix>>();
    fila.addLast(raiz);

    while (!fila.isEmpty()) {
        Node<ProgramaNetFlix> atual = fila.pollFirst();
        ProgramaNetFlix p = atual.getValue();

        if (p.getGenres().toLowerCase().contains(genero)
                && p.getImdbScore() > 7.0) {
            lista.add(p);
        }

        if (atual.getFilhoEsquerdo() != null) {
            fila.addLast(atual.getFilhoEsquerdo());
        }

        if (atual.getFilhoDireito() != null) {
            fila.addLast(atual.getFilhoDireito());
        }
    }
}
    //Estatística 5 - Compara a média IMDB de filmes curtos, até 90 minutos, e longos, acima de 90 minutos.
    private static void analiseMediaCurtaLongaDuracao(ABB<ProgramaNetFlix> arvore) {
        double[] somaCurta = {0.0};
        double[] somaLonga = {0.0};
        int[] qtdCurta = {0};
        int[] qtdLonga = {0};

        calcularMediaCurtaLonga(arvore.getRaiz(), somaCurta, qtdCurta, somaLonga, qtdLonga);

        System.out.println("\n── MÉDIA IMDB: CURTA X LONGA DURAÇÃO ──");
        System.out.println("Critério: curta duração até 90 minutos; longa duração acima de 90 minutos.");
        System.out.println("---------------------------------------------------------");
        System.out.println("Categoria        | Quantidade | Média IMDB");
        System.out.println("-----------------+------------+-----------");

        double mediaCurta = qtdCurta[0] > 0 ? somaCurta[0] / qtdCurta[0] : 0;
        double mediaLonga = qtdLonga[0] > 0 ? somaLonga[0] / qtdLonga[0] : 0;

        System.out.println("Curta duração    | " + qtdCurta[0] + espacos(10 - String.valueOf(qtdCurta[0]).length())
                + " | " + arredondarDuasCasas(mediaCurta));

        System.out.println("Longa duração    | " + qtdLonga[0] + espacos(10 - String.valueOf(qtdLonga[0]).length())
                + " | " + arredondarDuasCasas(mediaLonga));

        System.out.println("──────────────────────────────────────────────");
    }

    private static void calcularMediaCurtaLonga(
            Node<ProgramaNetFlix> no,
            double[] somaCurta,
            int[] qtdCurta,
            double[] somaLonga,
            int[] qtdLonga) {

        if (no == null) return;

        calcularMediaCurtaLonga(no.getFilhoEsquerdo(), somaCurta, qtdCurta, somaLonga, qtdLonga);

        ProgramaNetFlix p = no.getValue();

        if (p.getRuntime() <= 90) {
            somaCurta[0] += p.getImdbScore();
            qtdCurta[0]++;
        } else {
            somaLonga[0] += p.getImdbScore();
            qtdLonga[0]++;
        }

        calcularMediaCurtaLonga(no.getFilhoDireito(), somaCurta, qtdCurta, somaLonga, qtdLonga);
    }


// aqui começa: inserir, buscar e remover


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


    //aqui começa: altura, salvar e encerrar

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



        //aqui começa todos utilitários
        //basicamente ajudam a validar entrada, converter valores e aplicar o filtro dos países latinos

    
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


    /*ONDE OCORREU A DEFINIÇAO!!!! quando o usuário chega no menu de análises
     a árvore já contém apenas filmes dos 5 países latino-americanos escolhidos */


        private static boolean ehFilmeLatinoSelecionado(ProgramaNetFlix p) {
        if (!p.getShowType().equalsIgnoreCase("MOVIE")) {
            return false;
        }

        String paises = p.getProductionCountries();

        return paises.contains("'BR'")
            || paises.contains("'MX'")
            || paises.contains("'AR'")
            || paises.contains("'CL'")
            || paises.contains("'CO'");
    }

//------------------------------------------------------------

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
