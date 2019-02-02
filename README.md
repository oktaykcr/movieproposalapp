# Movie Proposal App Using Apriori Algorithm

## Description
This project about a movie recommendation website. The website uses association rules mining which is one of the data mining process of finding the rules. I used apriori algorithm to find user rating frequencies. Firstly, the user should search movie to mark as a favorite. The website shows the result of the search. In this result, the user can see the movie’s poster. When the user marks as fovorite a movie, the website stores user’s favorite movie in the database. According to these favorite movies, the apriori algorithm gives recommended movies and the website shows the movies to the user with their necessary information about movie. The user can read the summary description about movie and see the imdb rating.

> Technologies
- Backend
  - Java Spring Boot
  - H2 Database
  - Python >=3
    - pandas
- Frontend:
  - Thymeleaf
  - Bootstrap
  - Jquery
  - Font Awesome

## Method

### Apriori Algorithm
I used association rule mining to find frequent user reviews in dataset. I wrote apriori algorithm using python language. In the script, I specified that if the user gives rating greater than three, the movie is marked as favorable movie. According to this marking, I grouping the dataset by the userId and I created frequent itemsets. I specified minimum support value 50. After this process our script makes an association rule from a frequent itemset by taking one of the movies in the itemset amd computes the confidence of each of these rules. At the end of the script, I wrote a method for converting movieId to imdbId because the dataset has movie id but it different from imdb id. I used an api to get information about movies in the movie recommendation website. The api uses imdb id to find movie. The script takes arguments which are imdb ids of user favorite movies. According to these favorite movie ids, the script gives an output which is recommended movies.

### Movie Proposal Website
I wrote a backend server to recommend a movie. I used Spring Boot framework to create backend server. I used thymeleaf which is one of the template engine for frontend part. Another technology that I am used is [OMDB Api](http://www.omdbapi.com/) to obtain movie information. In this api you can search movie by imdb id or title. It also provides poster of movies.

|Service   |Description   |
|----------|--------------|
|**/movies**|The user can search movies and see the movies on the page.|
|**/movies/favorites**|The user can mark movie as fovorite and it store in the database. User can look at their favorite movies at any time.|
|**/movies/recommended**|If the user has favorite movies and click the recommend button, the system recommend movies and store in the database. It also like favorite service, user can look at their recommended movies at any time.|

### Data
I used two data set. We got these data set from [MovieLens](https://grouplens.org/datasets/movielens). I have choosen the recommended data set for education or development. It has 100,000 ratings and 3,600 tag applications applied to 9,000 movies by 600 users. First dataset is **rating.csv**. I used this data set for train our data which includes user reviews about movies. Another one is **links.csv** . It shows which movie id matches with which imdb id.

## References
- Robert, L. (2015). Learning Data Mining with Python. Birmingham, Packt Publishing Ltd.
- G. (2016, October 18). MovieLens. Retrieved from https://grouplens.org/datasets/movielens/

## Credits
- Oktay Koçer
  
## License
- [License File](LICENSE)