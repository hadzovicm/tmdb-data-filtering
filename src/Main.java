import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        File f = new File("tmdb_data.csv");
        Scanner sc = null;
        try {
            sc = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        /*Filtriranje CSV-a*/
        System.out.println("Filtering CSV lines...");
        sc.nextLine();
        ArrayList<String> brokenMovies = new ArrayList<>();
        ArrayList<Movie> movies = new ArrayList<>();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (parts.length == 10 && !parts[3].equals("0")){
                String voteCountString = parts[3].replaceAll("\"", "").replaceAll(",", "");
                String voteAverageString = parts[4].replaceAll("\"", "").replace(",", ".");
                String popularityString = parts[8].replaceAll("\"", "").replace(",", ".");
                try {
                    movies.add(new Movie(Integer.parseInt(parts[0]), parts[1], parts[2], Integer.parseInt(voteCountString),
                            Double.parseDouble(voteAverageString), parts[5], parts[6], Boolean.parseBoolean(parts[7]),
                            Double.parseDouble(popularityString), parts[9]));
                } catch (Exception e) {
                    brokenMovies.add(line);
                }
            } else {
                brokenMovies.add(line);
            }
        }
        cleanCsv(movies);
        brokenCsv(brokenMovies);
        System.out.println("CSV filtered successfully");
        System.out.println();
        /*Zavrsetak filtriranja*/

        /*Task 1*/
        System.out.println("Sorting movies list by vote_average, putting the results in task1.csv...");
        movies.sort((v1, v2) -> {
            return v2.getVote_average().compareTo(v1.getVote_average()); //Sort funkcija za vote_average
        });
        try {
            FileWriter task1fw = new FileWriter("task1.csv");
            List<Movie> filteredMovies = movies.stream()
                    .filter(movie -> movie.getVote_count() > 99)
                    .collect(Collectors.toList());
            List<Movie> top50Movies = filteredMovies.subList(0, Math.min(50, filteredMovies.size()));
            for (Movie m : top50Movies) {
                task1fw.write(m + "\n");
            }

            task1fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Done sorting, task1 completed");
        System.out.println();
        /*Task 1 zavrsetak*/



        /*Task 2*/
        System.out.println("Sorting movies by original language, making new CSV's for each language, putting results in task2 directory...");
        Map<String, ArrayList<Movie>> moviesByLanguage = new HashMap<>();
        for (Movie m : movies) {
            moviesByLanguage.computeIfAbsent(m.getOriginal_language(), k -> new ArrayList<>()).add(m); //Dodaje Key ako ne postoji
        }
        for (Map.Entry<String, ArrayList<Movie>> entry : moviesByLanguage.entrySet()) {
            String language = entry.getKey();
            ArrayList<Movie> moviesOrigLanguage = entry.getValue();
            moviesOrigLanguage.sort((v1, v2) -> v2.getVote_average().compareTo(v1.getVote_average()));
            if (moviesOrigLanguage.size() > 50) {
                moviesOrigLanguage.subList(50, moviesOrigLanguage.size()).clear();
            }
            File task2file = new File("task2/task2." + language + ".csv");
            try {
                FileWriter task2fw = new FileWriter(task2file);
                for (Movie movie : moviesOrigLanguage) {
                    task2fw.write(movie.toString() + "\n");
                }
                task2fw.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        System.out.println("Created new " + moviesByLanguage.size() + " languages CSV files, task 2 completed");
        System.out.println();
        /*Task 2 zavrsetak*/




        /*Task 3*/
        System.out.println("Doing task 3, sorting movies by votecount...");
        ArrayList<Movie> task3movies = new ArrayList<>();
        int task3counter = 0;
        for (Movie m : movies) {
            if(m.getVote_count()>15000) {
                task3movies.add(m);
                task3counter++;
            }
        }
        try {
            FileWriter task3fw = new FileWriter("task3.csv");
            for (Movie m : task3movies) {
                task3fw.write(m.toString() + "\n");
            }
            task3fw.write("There are " + task3counter + " movies with vote_count greater than 15000");
            task3fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Wrote " + task3counter + " movies into task3.csv, task 3 completed");
        System.out.println();
        /*Task 3 zavrsetak*/


        /*Task 4*/
        System.out.println("Filtering movies by start date between 2010 and 2019...");
        LocalDate startniDate = LocalDate.of(2010, 1, 1);
        LocalDate zavrsniDate = LocalDate.of(2019, 12, 31);
        ArrayList<Movie> moviesFilteredByDates = filterDatumi (movies, startniDate, zavrsniDate);
        try {
            FileWriter task4fw = new FileWriter("task4.csv");
            for(Movie m : moviesFilteredByDates) {
                task4fw.write(m.toString() + "\n");
            }
            task4fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Done filtering movies into task4.csv, task 4 completed");
        System.out.println();
        /*Task 4 zavrsetak*/


        /*Task 5*/
        System.out.println("Filtering top 50 movies that are in production...");
        ArrayList<Movie> moviesInProduction = new ArrayList<>();
        for (Movie m : movies) {
            if(m.getIn_production() == true && m.getVote_count()>500) moviesInProduction.add(m); //Uzimamo relevantne filmove (preko 500 glasova)
        }
        moviesInProduction.sort((v1, v2) -> v2.getVote_average().compareTo(v1.getVote_average()));
        moviesInProduction = new ArrayList<>(moviesInProduction.subList(0, 50));
        try {
            FileWriter task5fw = new FileWriter("task5.csv");
            for (Movie m : moviesInProduction) {
                task5fw.write(m.toString() + "\n");
            }
            task5fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Done filtering movies into task5.csv, task 5 completed");
        System.out.println();
        /*Task 5 zavrsetak*/

        /*Task 6*/
        System.out.println("Filtering movies that have one word in their name, and that have more than 500 votes by popularity...");
        ArrayList<Movie> oneWordMovies = new ArrayList<>();
        for (Movie m : movies) {
            String title = m.getName().trim(); //Uklanjamo space na pocetku i kraju (ako postoji)
            String[] words = title.split(" ");
            if (words.length == 1 && m.getVote_count()>500) {
                oneWordMovies.add(m);
            }
        }
        oneWordMovies.sort((v1, v2) -> v2.getPopularity().compareTo(v1.getPopularity()));
        oneWordMovies = new ArrayList<>(oneWordMovies.subList(0, 50));
        try {
            FileWriter task6fw = new FileWriter("task6.csv");
            for (Movie m : oneWordMovies) {
                task6fw.write(m.toString() + "\n");
            }
            task6fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Done filtering movies in task6.csv, task 6 completed");
        System.out.println();
        /*Task 6 zavrsetak*/

        /*Task 7*/
        System.out.println("Filling Map with Genres and ArrayLists of Movies, sorting movies in new CSV files...");
        Map<String, ArrayList<Movie>> ZanrFilm = new HashMap<>();
        for (Movie m : movies) {
            ArrayList<String> genres = m.getGenres();
            for (String genre : genres) {
                String cleanGenre = genre.trim().replaceAll("\"", "");
                ZanrFilm.computeIfAbsent(cleanGenre, k -> new ArrayList<>()).add(m);
            }
        }
        for (Map.Entry<String, ArrayList<Movie>> entry : ZanrFilm.entrySet()) {
            String genre = entry.getKey();
            ArrayList<Movie> FilmoviZanra = entry.getValue();
            FilmoviZanra.sort((v1, v2) -> Double.compare(v2.getVote_average(), v1.getVote_average()));
            if (FilmoviZanra.size() > 50) FilmoviZanra = new ArrayList<>(FilmoviZanra.subList(0, 50));
            File task7file = new File("task7/task7." + genre + ".csv");
            try {
                FileWriter task7fw = new FileWriter(task7file);
                for (Movie m : FilmoviZanra) {
                    task7fw.write(m.toString() + "\n");
                }
                task7fw.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        System.out.println("Done filtering movies in task7.csv, task 7 completed");
        System.out.println();
        /*Task 7 zavrsetak*/

        }


    private static void cleanCsv(ArrayList<Movie> movies) {
        try {
            FileWriter fw = new FileWriter("tmdb_data_cleaned.csv");
            for(Movie m : movies){
                fw.write(m.toString() + "\n");
            }
            fw.close();
        } catch (Exception e) {
            System.out.println("File not found");
        }
 }

 private static void brokenCsv(ArrayList<String> brokenMovies) {
     try {
         FileWriter fw = new FileWriter("tmdb_data_broken.csv");
         for(String s : brokenMovies){
             fw.write(s + "\n");
         }
         fw.close();
     } catch (Exception e) {
         System.out.println("File not found");
     }
 }

    private static ArrayList<Movie> filterDatumi(ArrayList<Movie> movies, LocalDate startniDate, LocalDate zavrsniDate) {
        ArrayList<Movie> filtriraniMovies  = new ArrayList<>();
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Integer brokenCounter = 0;
        for (Movie m : movies) {
            String DatumString = m.getFirst_air_date();

            if(DatumString != null && !DatumString.isEmpty()); {
                try {
                    LocalDate releaseDate = LocalDate.parse(DatumString, formater);
                    if (!releaseDate.isBefore(startniDate) && !releaseDate.isAfter(zavrsniDate)) {
                        filtriraniMovies.add(m);
                    }
                } catch (Exception e) {
                    brokenCounter++;
                }
            }
        }
        System.out.println("There are " + brokenCounter + " movies without a starting date");
        return filtriraniMovies;
    }


}