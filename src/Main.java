import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        File f = new File("tmdb_data.csv");

        Scanner sc = null;
        try {
            sc = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        sc.nextLine();
        ArrayList<String> brokenMovies = new ArrayList<>();
        ArrayList<Movie> movies = new ArrayList<>();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            /*Filtriranje CSV-a*/
            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (parts.length == 10 && !parts[3].equals("0")){
                String voteCountString = parts[3].replaceAll(",", "");
                String voteAverageString = parts[4].replaceAll("[\",]", "");
                String popularityString = parts[8].replaceAll("[\",]", "");

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
        /*Zavrsetak filtriranja*/


        }

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

}