import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieAnalyzer {
  public static class Movie {
    private String posterLink;
    private String seriesTitle;
    private int Released_Year;
    private String Certificate;
    private int Runtime;
    private String Genre;
    private Float IMDB_Rating;
    private String Overview;
    private int Meta_score;
    private String Director;
    private String Star1;
    private String Star2;
    private String Star3;
    private String Star4;
    private int No_of_Votes;
    private int Gross;
    private int flag;
    private int OverviewLength;
    private String[] Genres;
    private List<String> stars = new ArrayList<String>();

    public Movie(String Poster_Link, String Series_Title, String Released_Year, String Certificate, String Runtime, String Genre, String IMDB_Rating,
                 String Overview, String Meta_score, String Director, String Star1, String Star2, String Star3, String Star4, String No_of_Votes, String Gross) {
      this.posterLink = Poster_Link.replace("\"", "");
      this.seriesTitle = Series_Title.replace("\"", "");
      if (!Released_Year.equals("")) {
        this.Released_Year = Integer.parseInt(Released_Year);
      }
      this.Certificate = Certificate.replace("\"", "");
      if (!Runtime.equals("")) {
        String[] A = Runtime.split(" ");
        this.Runtime = Integer.parseInt(A[0].replace("\"", ""));
      }
      this.Genre = Genre.replace("\"", "");
      Genres = this.Genre.split(", ");
      if (!IMDB_Rating.equals("")) this.IMDB_Rating = Float.parseFloat(IMDB_Rating);
      this.Overview = Overview;
      if (!Meta_score.equals("")) this.Meta_score = Integer.parseInt(Meta_score);
      this.Director = Director.replace("\"", "");
      this.Star1 = Star1.replace("\"", "");
      this.Star2 = Star2.replace("\"", "");
      this.Star3 = Star3.replace("\"", "");
      this.Star4 = Star4.replace("\"", "");
      if (!No_of_Votes.equals("")) this.No_of_Votes = Integer.parseInt(No_of_Votes);
      if (!Gross.equals("")) {
        String[] num = Gross.replace("\"", "").split(",");
        int n = 0;
        for (int i = 0; i < num.length; i++) {
          if (i==0){
            n += Integer.parseInt(num[i]);
          }
          else {
            n+=n*1000+Integer.parseInt(num[i]);
          }
        }
        this.Gross = n;
      }
      this.flag = 1;
      String q = Overview;
      if (q.charAt(0) == '"') {
        q = q.substring(1);
      }
      if (q.charAt(q.length() - 1) == '"') {
        q = q.substring(0, q.length() - 1);
      }
      this.OverviewLength = q.length();
      stars.add(getStar1());
      stars.add(getStar2());
      stars.add(getStar3());
      stars.add(getStar4());
    }

    public String getPosterLink() {
      return posterLink;
    }

    public int getReleased_Year() {
      return Released_Year;
    }

    public String getSeriesTitle() {
      return seriesTitle;
    }

    public String getCertificate() {
      return Certificate;
    }

    public Integer getRuntime() {
      return Runtime;
    }

    public String getGenre() {
      return Genre;
    }

    public double getIMDB_Rating() {
      return IMDB_Rating;
    }

    public String getOverview() {
      return Overview;
    }

    public int getMeta_score() {
      return Meta_score;
    }

    public String getDirector() {
      return Director;
    }

    public String getStar1() {
      return Star1;
    }

    public String getStar2() {
      return Star2;
    }

    public String getStar3() {
      return Star3;
    }

    public String getStar4() {
      return Star4;
    }

    public int getNo_of_Votes() {
      return No_of_Votes;
    }

    public int getGross() {
      return Gross;
    }

    public int getFlag() {
      return flag;
    }

    public int getOverviewLength() {
      return OverviewLength;
    }

    public List<String> getStars() {
      return stars;
    }

    public String[] getGenres() {
      return Genres;
    }
  }

  public static Stream<Movie> readMovies(String filename) throws IOException {
    return Files.lines(Paths.get(filename))
            .filter(a -> !a.equals("Poster_Link,Series_Title,Released_Year,Certificate,Runtime,Genre,IMDB_Rating,Overview,Meta_score,Director,Star1,Star2,Star3,Star4,No_of_Votes,Gross"))
            .map(l -> l.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1))
            .map(a -> new Movie(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
  }

  public String path;
  public Stream<Movie> movies;

  public MovieAnalyzer(String dataset_path) throws IOException {
    movies = readMovies(dataset_path);
    path = dataset_path;
  }

  public Map<Integer, Integer> getMovieCountByYear() throws IOException {
    movies = readMovies(path);
    Map<Integer, Integer> getMovieCountByYear = movies.collect(Collectors.groupingBy(Movie::getReleased_Year, Collectors.summingInt(Movie::getFlag)));
    Map<Integer, Integer> result = new LinkedHashMap<>();
    getMovieCountByYear.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByKey())).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
    return result;
  }

  public Map<String, Integer> getMovieCountByGenre() throws IOException {
    movies = readMovies(path);
    Map<String, Integer> getMovieCountByGenre = new HashMap<>();
    movies.forEach(movie -> {
      for (int i = 0; i < movie.Genres.length; i++) {
        if (getMovieCountByGenre.containsKey(movie.Genres[i])) {
          int value = getMovieCountByGenre.get(movie.Genres[i]);
          getMovieCountByGenre.remove(movie.Genres[i]);
          getMovieCountByGenre.put(movie.Genres[i], value + 1);
        } else {
          getMovieCountByGenre.put(movie.Genres[i], 1);
        }
      }
    });
    Map<String, Integer> result = new LinkedHashMap<>();
    getMovieCountByGenre.entrySet().stream().sorted((Map.Entry.comparingByKey())).sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
    return result;
  }

  public Map<List<String>, Integer> getCoStarCount() throws IOException {
    movies = readMovies(path);
    List<List<String>> m = new ArrayList<>();
    Map<List<String>, Integer> getCoStarCount = new HashMap<>();
    movies.forEach(movie -> {
      List<String> a = movie.stars;
      for (int i = 0; i < 4; i++) {
        for (int j = i + 1; j < 4; j++) {
          List<String> z = new ArrayList<>();
          z.add(a.get(i));
          z.add(a.get(j));
          z.sort(String::compareTo);
          if (getCoStarCount.containsKey(z)) {
            int value = getCoStarCount.get(z);
            getCoStarCount.remove(z);
            getCoStarCount.put(z, value + 1);
          } else {
            getCoStarCount.put(z, 1);
          }
        }
      }
    });
    return getCoStarCount;
  }

  public static class Movies {
    private String name;
    private int runtime;
    private int length;
  }

  public List<String> getTopMovies(int top_k, String by) throws IOException {
    movies = readMovies(path);
    List<String> ans = new ArrayList<>();
    List<Movies> choose = new ArrayList<>();
    List<Integer> z = new ArrayList<>();
    if (by.equals("runtime")) {
      movies.forEach(movie -> {
        Movies a = new Movies();
        a.name = movie.seriesTitle;
        a.runtime = movie.Runtime;
        a.length = movie.OverviewLength;
        if (choose.size() < top_k) {
          boolean add = false;
          for (int i = 0; i < choose.size(); i++) {
            if (a.runtime > choose.get(i).runtime) {
              choose.add(i, a);
              add = true;
              break;
            } else if (a.runtime == choose.get(i).runtime) {
              int j = 0;
              while (j < a.name.length() && j < choose.get(i).name.length()) {
                if (a.name.substring(j, j + 1).compareTo(choose.get(i).name.substring(j, j + 1)) < 0) {
                  choose.add(i, a);
                  add = true;
                  break;
                } else if (a.name.substring(j, j + 1).compareTo(choose.get(i).name.substring(j, j + 1)) > 0) {
                  break;
                }
                j++;
              }
            }
            if (add) break;
          }
          if (choose.size() == 0) {
            choose.add(a);
            add = true;
          }
          if (!add) choose.add(a);
        } else {
          if (z.size() == 251) {
            z.get(1);
          }
          z.add(1);
          for (int i = 0; i < top_k; i++) {
            if (a.runtime > choose.get(i).runtime) {
              choose.add(i, a);
              choose.remove(top_k);
              break;
            } else if (a.runtime == choose.get(i).runtime) {
              int j = 0;
              if (a.name.equals(choose.get(i).name)) {
                choose.add(i, a);
                choose.remove(top_k);
                break;
              }
              boolean has_add = false;
              boolean need_continue = false;
              while (j < a.name.length() && j < choose.get(i).name.length()) {
                if (a.name.substring(j, j + 1).compareTo(choose.get(i).name.substring(j, j + 1)) < 0) {
                  choose.add(i, a);
                  choose.remove(top_k);
                  has_add = true;
                  break;
                } else if (a.name.substring(j, j + 1).compareTo(choose.get(i).name.substring(j, j + 1)) > 0) {
                  need_continue = true;
                  break;
                }
                j++;
              }
              if (has_add) {
                break;
              }
              if (need_continue) {
                continue;
              }
              if (a.name.length() < choose.get(i).name.length()) {
                choose.add(i, a);
                choose.remove(top_k);
                break;
              }
            }
          }

        }
      });
    } else {
      movies.forEach(movie -> {
        Movies a = new Movies();
        a.name = movie.seriesTitle;
        a.runtime = movie.Runtime;
        a.length = movie.OverviewLength;
        if (choose.size() < top_k) {
          boolean add = false;
          for (int i = 0; i < choose.size(); i++) {
            if (a.length > choose.get(i).length) {
              choose.add(i, a);
              add = true;
              break;
            } else if (a.length == choose.get(i).length) {
              int j = 0;
              while (j < a.name.length() && j < choose.get(i).length) {
                if (a.name.substring(j, j + 1).compareTo(choose.get(i).name.substring(j, j + 1)) < 0) {
                  choose.add(i, a);
                  add = true;
                  break;
                } else if (a.name.substring(j, j + 1).compareTo(choose.get(i).name.substring(j, j + 1)) > 0) {
                  break;
                }
                j++;
              }
            }
            if (add) break;
          }
          if (choose.size() == 0) {
            choose.add(a);
            add = true;
          }
          if (!add) choose.add(a);
        } else {
          if (z.size() == 251) {
            z.get(1);
          }
          z.add(1);
          for (int i = 0; i < top_k; i++) {
            if (a.length > choose.get(i).length) {
              choose.add(i, a);
              choose.remove(top_k);
              break;
            } else if (a.length == choose.get(i).length) {
              int j = 0;
              if (a.name.equals(choose.get(i).name)) {
                choose.add(i, a);
                choose.remove(top_k);
                break;
              }
              boolean has_add = false;
              boolean need_continue = false;
              while (j < a.name.length() && j < choose.get(i).name.length()) {
                if (a.name.substring(j, j + 1).compareTo(choose.get(i).name.substring(j, j + 1)) < 0) {
                  choose.add(i, a);
                  choose.remove(top_k);
                  has_add = true;
                  break;
                } else if (a.name.substring(j, j + 1).compareTo(choose.get(i).name.substring(j, j + 1)) > 0) {
                  need_continue = true;
                  break;
                }
                j++;
              }
              if (has_add) {
                break;
              }
              if (need_continue) {
                continue;
              }
              if (a.name.length() < choose.get(i).name.length()) {
                choose.add(i, a);
                choose.remove(top_k);
                break;
              }
            }
          }

        }
      });
    }
//        List<String> a = new ArrayList<>();
//        Map<String,String> getMovieByUNL = movies.collect(Collectors.toMap(Movie::getPoster_Link,Movie::getSeries_Title));
//        if (by.equals("runtime")){
//            Map<String,String> getMovieByruntime = movies.collect(Collectors.toMap(Movie::getPoster_Link,Movie::getRuntime));
//            Map<String,String> result = new LinkedHashMap<>();
//            getMovieByruntime.entrySet().stream().sorted(Map.Entry.comparingByKey()).sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
//            Set<String> set = result.keySet();
//            a.addAll(set);
//        }
//        else {
//            Map<String,Integer> getMovieByruntime = movies.collect(Collectors.toMap(Movie::getSeries_Title,Movie::getOverviewLength));
//            Map<String,Integer> result = new LinkedHashMap<>();
//            getMovieByruntime.entrySet().stream().sorted(Map.Entry.comparingByKey()).sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
//            Set<String> set = result.keySet();
//            a.addAll(set);
//        }
//        ans = a.subList(0,top_k);
    for (int i = 0; i < top_k; i++) {
      ans.add(choose.get(i).name);
    }
    return ans;
  }

  public List<String> getTopStars(int top_k, String by) throws IOException {
    movies = readMovies(path);
    List<String> ans = new ArrayList<>();
    List<String> q = new ArrayList<>();
    Map<String, Integer> num = new HashMap<>();
    Map<String, Integer> num1 = new HashMap<>();
    Map<String, Double> rate = new HashMap<>();
    Map<String, Double> gross = new HashMap<>();
    Map<String, Double> result = new LinkedHashMap<>();
    movies.forEach(movie -> {
      List<String> a = movie.stars;
      for (int i = 0; i < 4; i++) {
        if (num.containsKey(a.get(i))) {
          num.put(a.get(i), 1 + num.get(a.get(i)));
          rate.put(a.get(i), rate.get(a.get(i)) + movie.IMDB_Rating);
        } else {
          num.put(a.get(i), 1);
          rate.put(a.get(i), Double.valueOf(movie.IMDB_Rating));
        }
        if (num1.containsKey(a.get(i))) {
          if (movie.Gross!=0) num1.put(a.get(i), 1 + num1.get(a.get(i)));
          gross.put(a.get(i), gross.get(a.get(i)) + movie.Gross);
        } else {
          gross.put(a.get(i), (double) movie.Gross);
          if (movie.Gross!=0) num1.put(a.get(i), 1);
        }
      }
    });
    if (by.equals("rating")) {
      for (String key : num.keySet()) {
        rate.put(key, rate.get(key) / num.get(key));
      }
      rate.entrySet().stream().sorted(Map.Entry.comparingByKey()).sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
      Set<String> set = result.keySet();
      q.addAll(set);
    } else {
      for (String key : num1.keySet()) {
        gross.put(key, gross.get(key) / num1.get(key));
      }
      gross.entrySet().stream().sorted(Map.Entry.comparingByKey()).sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
      Set<String> set = result.keySet();
      q.addAll(set);
    }
    ans = q.subList(0, top_k);
    return ans;
  }

  public List<String> searchMovies(String genre, float min_rating, int max_runtime) throws IOException {
    movies = readMovies(path);
    List<String> ans = new ArrayList<>();
    movies.forEach(movie -> {
      if (movie.seriesTitle.equals("12 Angry Men")) {
        int q = 1;
      }
      if (movie.Genre.contains(genre) && movie.IMDB_Rating >= min_rating && movie.Runtime <= max_runtime) {
        ans.add(movie.seriesTitle);
      }
    });
    ans.sort(String::compareTo);
    return ans;
  }
}