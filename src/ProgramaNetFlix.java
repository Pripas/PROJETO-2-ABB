/* Classe ProgramaNetFlix: representa um título do dataset titles.csv.
 * Implementa Comparable para que a ABB possa comparar pelo campo "id".
 */
public class ProgramaNetFlix implements Comparable<ProgramaNetFlix> {

    private String id;
    private String title;
    private String showType;
    private String description;
    private int    releaseYear;
    private String ageCertification;
    private int    runtime;
    private String genres;
    private String productionCountries;
    private double seasons;
    private String imdbId;
    private double imdbScore;
    private int    imdbVotes;
    private double tmdbPopularity;
    private double tmdbScore;

    public ProgramaNetFlix(String id, String title, String showType, String description,
                           int releaseYear, String ageCertification, int runtime,
                           String genres, String productionCountries, double seasons,
                           String imdbId, double imdbScore, int imdbVotes,
                           double tmdbPopularity, double tmdbScore) {
        this.id                  = id;
        this.title               = title;
        this.showType            = showType;
        this.description         = description;
        this.releaseYear         = releaseYear;
        this.ageCertification    = ageCertification;
        this.runtime             = runtime;
        this.genres              = genres;
        this.productionCountries = productionCountries;
        this.seasons             = seasons;
        this.imdbId              = imdbId;
        this.imdbScore           = imdbScore;
        this.imdbVotes           = imdbVotes;
        this.tmdbPopularity      = tmdbPopularity;
        this.tmdbScore           = tmdbScore;
    }

    // ordena pelo id lexicograficamente
    @Override
    public int compareTo(ProgramaNetFlix outro) {
        if      (id.compareTo(outro.getId()) < 0)  return -1;
        else if (id.compareTo(outro.getId()) == 0) return  0;
        else                                        return  1;
    }

    public String getId()                  { return id; }
    public String getTitle()               { return title; }
    public String getShowType()            { return showType; }
    public String getDescription()         { return description; }
    public int    getReleaseYear()         { return releaseYear; }
    public String getAgeCertification()    { return ageCertification; }
    public int    getRuntime()             { return runtime; }
    public String getGenres()              { return genres; }
    public String getProductionCountries() { return productionCountries; }
    public double getSeasons()             { return seasons; }
    public String getImdbId()              { return imdbId; }
    public double getImdbScore()           { return imdbScore; }
    public int    getImdbVotes()           { return imdbVotes; }
    public double getTmdbPopularity()      { return tmdbPopularity; }
    public double getTmdbScore()           { return tmdbScore; }

    public void setId(String id)                              { this.id = id; }
    public void setTitle(String title)                        { this.title = title; }
    public void setShowType(String showType)                  { this.showType = showType; }
    public void setDescription(String description)            { this.description = description; }
    public void setReleaseYear(int releaseYear)               { this.releaseYear = releaseYear; }
    public void setAgeCertification(String ageCertification)  { this.ageCertification = ageCertification; }
    public void setRuntime(int runtime)                       { this.runtime = runtime; }
    public void setGenres(String genres)                      { this.genres = genres; }
    public void setProductionCountries(String countries)      { this.productionCountries = countries; }
    public void setSeasons(double seasons)                    { this.seasons = seasons; }
    public void setImdbId(String imdbId)                      { this.imdbId = imdbId; }
    public void setImdbScore(double imdbScore)                { this.imdbScore = imdbScore; }
    public void setImdbVotes(int imdbVotes)                   { this.imdbVotes = imdbVotes; }
    public void setTmdbPopularity(double tmdbPopularity)      { this.tmdbPopularity = tmdbPopularity; }
    public void setTmdbScore(double tmdbScore)                { this.tmdbScore = tmdbScore; }

    // valida se os campos obrigatórios estão preenchidos antes de inserir na ABB
    public boolean camposEssenciaisPreenchidos() {
        return !id.isEmpty()
            && !title.isEmpty()
            && !showType.isEmpty()
            && !description.isEmpty()
            && releaseYear > 0
            && runtime > 0
            && !genres.isEmpty()
            && !productionCountries.isEmpty()
            && !imdbId.isEmpty()
            && imdbScore > 0
            && imdbVotes > 0
            && tmdbPopularity > 0
            && tmdbScore > 0;
    }

    // retorna resumo do programa para exibição nos percursos
    @Override
    public String toString() {
        return "ID: " + id + " | " + showType + " | " + releaseYear + " | " + title +
               " | IMDB: " + imdbScore + " | TMDB: " + tmdbScore;
    }

    // retorna todos os dados formatados para exibição no menu
    public String toStringDetalhado() {
        return "\n========== DETALHES DO PROGRAMA ==========\n"
             + "  ID              : " + id + "\n"
             + "  Título          : " + title + "\n"
             + "  Tipo            : " + showType + "\n"
             + "  Descrição       : " + description + "\n"
             + "  Ano             : " + releaseYear + "\n"
             + "  Classificação   : " + (ageCertification.isEmpty() ? "N/A" : ageCertification) + "\n"
             + "  Duração (min)   : " + runtime + "\n"
             + "  Gêneros         : " + genres + "\n"
             + "  Países          : " + productionCountries + "\n"
             + "  Temporadas      : " + (seasons == 0 ? "N/A (Filme)" : (int) seasons) + "\n"
             + "  IMDB ID         : " + imdbId + "\n"
             + "  IMDB Score      : " + imdbScore + "\n"
             + "  IMDB Votos      : " + imdbVotes + "\n"
             + "  TMDB Popularidade: " + tmdbPopularity + "\n"
             + "  TMDB Score      : " + tmdbScore + "\n"
             + "==========================================";
    }

    // serializa o objeto em formato CSV
    public String toCSV() {
        return id + ",\"" + title + "\"," + showType + ",\"" + description + "\"," +
               releaseYear + "," + ageCertification + "," + runtime + ",\"" + genres + "\",\"" +
               productionCountries + "\"," + (int) seasons + "," + imdbId + "," +
               imdbScore + "," + imdbVotes + "," + tmdbPopularity + "," + tmdbScore;
    }
}
