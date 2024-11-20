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

        /*Task 1(Filtriramo samo one koji imaju 100 ili preko glasova)*/
        System.out.println("Sorting movies list by vote_average, putting the results in task1.csv");
        movies.sort((v1, v2) -> {
            return v2.getVote_average().compareTo(v1.getVote_average()); //Sort funkcija za vote_average
        });

        try {
            FileWriter task1fw = new FileWriter("task1.csv");
            int i=0;
            int j=0;
            while(j<50){
                if(movies.get(i).getVote_count()>99){ //Ubacujemo samo one preko 99 glasova
            task1fw.write(movies.get(i) + "\n");
                j++;
                }
            i++;
            }

            task1fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Done sorting, task1 completed");
        System.out.println();
        /*Task 1 zavrsetak*/



        /*Task 2*/
        System.out.println("Sorting movies by original language, making new CSV's for each language, " +
                "putting results in task2 directory");
        ArrayList<String> orig_langs = new ArrayList<>();
        for(Movie m : movies){
            if(!orig_langs.contains(m.getOriginal_language())){
                orig_langs.add(m.getOriginal_language());
            }
        }

        for(String lang : orig_langs){
            File task2File = new File("task2/task2." + lang + ".csv");
            try {
                task2File.createNewFile(); //Pravljenje novog fajla za svaki jezik
                ArrayList<Movie> tempMovies = top50orig_lang(movies, lang); //Funkcija koja izdvoji sve filmove
                // sa odredenim jezikom i filtrira ih po rejtingu i vraca Arraylistu
                FileWriter task2fw = new FileWriter("task2/task2." + lang + ".csv");
                for (Movie m : tempMovies) {
                    task2fw.write(m.toString() + "\n");
                }
                task2fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Created new " + orig_langs.size() + " languages CSV files, task 2 completed");
        System.out.println();
        /*Task 2 zavrsetak*/


        /*Task 3, sortiranje po brojevima glasova*/
        System.out.println("Doing task3, sorting movies by votecount");
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Wrote " + task3counter + " movies into task3.csv, task 3 completed");
        System.out.println();
        /*Task 3 zavrsetak*/

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

 private static ArrayList<Movie> top50orig_lang(ArrayList<Movie> movies, String lang) {
        ArrayList<Movie> tempMovies = new ArrayList<>() ;
        for(Movie m : movies){
            if(m.getOriginal_language().equals(lang)){
                tempMovies.add(m);
            }
        }
     tempMovies.sort((v1, v2) -> {
         return v2.getVote_average().compareTo(v1.getVote_average());
     });
        return tempMovies;
    }

}