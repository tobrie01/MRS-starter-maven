package dk.easv.mrs.GUI.Model;
import dk.easv.mrs.BE.Movie;
import dk.easv.mrs.BLL.MovieManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class MovieModel {

    private ObservableList<Movie> moviesToBeViewed;

    private MovieManager movieManager;

    public MovieModel() throws Exception
    {
        movieManager = new MovieManager();
        moviesToBeViewed = FXCollections.observableArrayList();
        moviesToBeViewed.addAll(movieManager.getAllMovies());
    }



    public ObservableList<Movie> getObservableMovies()
    {
        return moviesToBeViewed;
    }

    public void searchMovie(String query) throws Exception {
        List<Movie> searchResults = movieManager.searchMovies(query);
        moviesToBeViewed.clear();
        moviesToBeViewed.addAll(searchResults);
    }

    public Movie createMovie(Movie newMovie) throws Exception {
        Movie movieCreated = movieManager.createMovie(newMovie);
        moviesToBeViewed.add(movieCreated);
        return movieCreated;
    }

    public void updateMovie(Movie movieToBeUpdated) throws Exception {
        movieManager.updateMovie(movieToBeUpdated);

        //moviesToBeViewed.set(?,movieToBeUpdated);
        int indexInList = moviesToBeViewed.indexOf(movieToBeUpdated);
        moviesToBeViewed.set(indexInList, movieToBeUpdated);
    }

    public void deleteMovie(Movie selectedMovie) throws Exception {
        // remove movie in dal layer
        movieManager.deleteMovie(selectedMovie);
        // update observable list
        moviesToBeViewed.remove(selectedMovie);
    }
}
