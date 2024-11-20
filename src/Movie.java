import java.util.ArrayList;


//id,name,original_language,vote_count,vote_average,first_air_date,last_air_date,in_production,popularity,genres

public class Movie implements Comparable<Movie> {
    private Integer id;
    private String name;
    private String original_language;
    private Integer vote_count;
    private Double vote_average;
    private String first_air_date;
    private String last_air_date;
    private Boolean in_production;
    private Double popularity;
    private ArrayList<String> genres;

    public Movie(Integer id, String name, String original_language, Integer vote_count, Double vote_average,
                 String first_air_date, String last_air_date, Boolean in_production, double popularity,
                 String genres) {
        this.id = id;
        this.name = name;
        this.original_language = original_language;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
        this.first_air_date = first_air_date;
        this.last_air_date = last_air_date;
        this.in_production = in_production;
        this.popularity = popularity;
        this.genres = dajZanrove(genres);
    }

    @Override
    public String toString() {
        return id+","+name+","+original_language+","+vote_count+","+vote_average+","+
                first_air_date+","+last_air_date+","+in_production+","+popularity+","+genres;
    }

    private ArrayList<String> dajZanrove(String genres) {
        String[] genreArray = genres.split(",");
        ArrayList<String> genresList = new ArrayList<>();
        for (String genre : genreArray) {
            genresList.add(genre);
        }
        return genresList;
    }

}


