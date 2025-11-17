package dk.easv.mrs.DAL.db;

import dk.easv.mrs.BE.Movie;
import dk.easv.mrs.DAL.IMovieDataAccess;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO_DB implements IMovieDataAccess {

    private DBConnector databaseConnector;

    public MovieDAO_DB() throws IOException {
        databaseConnector = new DBConnector();
    }


    public List<Movie> getAllMovies() throws SQLException {
        ArrayList<Movie> allMovies = new ArrayList<>();
        try(Connection connection = databaseConnector.getConnection()) {
            String sql = "SELECT * FROM Movie;";

            Statement statement = connection.createStatement();

            if(statement.execute(sql))
            {
                ResultSet resultSet = statement.getResultSet();
                while(resultSet.next()){
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    int year = resultSet.getInt("year");

                    Movie movie = new Movie(id, year, title);
                    allMovies.add(movie);
                }

            }
        }
        return allMovies;
    }

    public Movie createMovie(Movie newMovie) throws SQLException {
        String sql = "INSERT INTO Movie (title, year) VALUES (?, ?)";

        try(Connection connection = databaseConnector.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, newMovie.getTitle());
            statement.setInt(2, newMovie.getYear());
            statement.executeUpdate();

            // get auto-generated ID
            try(ResultSet rs = statement.getGeneratedKeys()) {
                if(rs.next()) {
                    int newId = rs.getInt(1);
                    return new Movie(newId, newMovie.getYear(), newMovie.getTitle());
                }
            }
        }

        return null;
    }

    @Override
    public void updateMovie(Movie movie) throws Exception {
        try(Connection connection = databaseConnector.getConnection()) {
            String sql = "UPDATE Movie SET title = ?, year = ? WHERE id = ?";

            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, movie.getTitle());
                statement.setInt(2, movie.getYear());
                statement.setInt(3, movie.getId());

                statement.executeUpdate();
            }
        }
    }

    @Override
    public void deleteMovie(Movie movie) throws Exception {
        try(Connection connection = databaseConnector.getConnection()) {
            String sql = "DELETE FROM Movie WHERE id = ?";

            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, movie.getId());

                statement.executeUpdate();
            }
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        MovieDAO_DB movieDAO_db = new MovieDAO_DB();

        List<Movie> allMovies = movieDAO_db.getAllMovies();

        System.out.println(allMovies);
    }
}