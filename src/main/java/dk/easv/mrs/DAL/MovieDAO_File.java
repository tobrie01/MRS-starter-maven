package dk.easv.mrs.DAL;

// Java imports
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Project imports
import dk.easv.mrs.BE.Movie;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.APPEND;


public class MovieDAO_File implements IMovieDataAccess {
    private static final String MOVIES_FILE = "data/movie_titles.txt";
    private Path filePath = Paths.get(MOVIES_FILE);

    public List<Movie> getAllMovies() throws IOException {

        List<String> lines = Files.readAllLines(filePath);

        List<Movie> movies = new ArrayList<>();

        for (String line: lines) {
            String[] separatedLine = line.split(",");

            int id = Integer.parseInt(separatedLine[0]);
            int year = Integer.parseInt(separatedLine[1]);
            String title = separatedLine[2];

            if(separatedLine.length > 3)
            {
                for(int i = 3; i < separatedLine.length; i++)
                {
                    title += "," + separatedLine[i];
                }
            }
            Movie movie = new Movie(id, year, title);
            movies.add(movie);
        }
        return movies;
    }


    @Override
    public Movie createMovie(Movie newMovie) throws Exception {
        List<String> movies = Files.readAllLines(filePath);
        if (!movies.isEmpty()) {
            // get next id
            String[] separatedLine = movies.get(movies.size() - 1).split(",");
            int nextId = Integer.parseInt(separatedLine[0]) + 1;
            String newMovieLine = nextId + "," + newMovie.getYear() + "," + newMovie.getTitle();
            Files.write(filePath, (newMovieLine + "\r\n").getBytes(), APPEND);

            return new Movie(nextId, newMovie.getYear(), newMovie.getTitle());
        }
        return null;
    }

    @Override
    public void updateMovie(Movie movie) throws Exception {
        try
        {
            List<String> movies = Files.readAllLines(filePath);
            for (int i = 0; i < movies.size(); i++)
            {
                String[] separatedLine = movies.get(i).split(",");
                if(separatedLine.length == 3) {
                    int id = Integer.parseInt(separatedLine[0]);
                    if (id == movie.getId()) {
                        String updatedMovieLine = movie.getId() + "," + movie.getYear() + "," + movie.getTitle();
                        movies.set(i, updatedMovieLine);
                        break;
                    }
                }
            }
            Path tempPathFile = Paths.get(MOVIES_FILE+ "_TEMP");
            Files.createFile(tempPathFile);
            for (String line: movies)
                Files.write(tempPathFile, (line + "\r\n").getBytes(),APPEND);
            Files.copy(tempPathFile, filePath, REPLACE_EXISTING);
            Files.deleteIfExists(tempPathFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("An error occurred");
        }
    }

    @Override
    public void deleteMovie(Movie movie) throws Exception {
        try
        {
            // Read all movie lines in list
            List<String> movies = Files.readAllLines(filePath);
            movies.remove(movie.getId() + "," + movie.getYear() + "," + movie.getTitle());


            // Create new temp file
            Path tempPathFile = Paths.get(MOVIES_FILE+ "_TEMP");
            Files.createFile(tempPathFile);

            // For all lines...
            for (String line: movies)
                Files.write(tempPathFile, (line + "\r\n").getBytes(),APPEND);

            // Overwrite the old file with temp file
            Files.copy(tempPathFile, filePath, REPLACE_EXISTING);
            Files.deleteIfExists(tempPathFile);


        } catch (IOException e) {
            throw new Exception("An error occurred");
        }
    }
}