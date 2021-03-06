
# TicTacToePlay

## Description

This is a simple Tic-Tac-Toe web application that I am writing as a vehicle to learn
the following tools and technologies:

* ~~Play and Activator~~
* ~~JavaScript (ES2015)~~
* ~~Akka and Akka Persistence~~
* Cassandra or Postgres
* ~~IntelliJ IDEA~~
* ~~Jenkins~~
* Docker
* ~~Heroku~~
* ~~Gatling~~

## Deployed application

See the Tic-Tac-Toe web application in action [here](https://tictactoeplay.herokuapp.com/).

## Screenshots

### Landing Page

The initial page offers the choice of casual or serious game play.

![LandingPage](Images/LandingPage.png)

### Casual Games

Casual games do not require registration but have no leaderboard.

![UnregisteredGame](Images/UnregisteredGame.png)

### Registration

In order to play serious games, it is first necessary to create an account using the registration page.

![Registration](Images/Registration.png)

### Serious Games

You can then enjoy playing serious games and try to conquer the leaderboard.

![RegisteredGame](Images/RegisteredGame.png)

## Development Phases

### Phase 1

* ~~Basic Tic-Tac-Toe game implementation~~
  * ~~Draw the grid~~
  * ~~Make cells clickable~~
  * ~~Add a Start button~~
  * ~~Add instruction messages~~
  * ~~Add a REST endpoint to calculate the computer's move and detect the end of the game~~
  * ~~Add error handling (of the REST call)~~
  * ~~Add a background image to the grid~~
  * ~~Randomly choose which player starts~~
  * Add support for keyboard navigation
  * Add an animation re the outcome of the game
  * Add support for easy/hard mode (depends on minimax implementation below)

### Phase 2

* ~~Deploy to Heroku~~
  * ~~Basic deployment~~
  * Optimise assets e.g. JavaScript etc.
  * ~~Remove unneeded cruft included in the Activator template~~
  * ~~Add/display a version number~~
  * ~~Setup Jenkins in a docker container to build/deploy~~

### Phase 3

* Improve the algorithm used to make the computer move
    * [Minimax](https://en.wikipedia.org/wiki/Minimax)
    * [Tic Tac Toe: Understanding The Minimax Algorithm](http://neverstopbuilding.com/minimax)

### Phase 4

* ~~Add tests~~
  * ~~Unit tests~~
  * ~~Akka Persistence tests~~
  * ~~Gatling load tests~~

### Phase 5

* ~~Make the controller actions async using Akka actors~~

### Phase 6

* ~~Add authentication to identify the player (required in order to maintain a leader board)~~
    * ~~Registration~~
    * ~~Login~~
    * ~~Logout~~

### Phase 7

* ~~Use Akka Persistence to save Akka actor state~~
  * ~~Persist state to Postgres~~

### Phase 8

* Refreshing the web page should restore the state of the game
  * TODO: add a list of steps to achieve this

### Phase 9

* ~~Add a leader board~~
    * ~~Dynamically updatable using Web Sockets~~

## Other

* Add support for accessibility
* Add support for &lt;noscript&gt;
* Re-implement the client-side code in [Scala.js](https://www.scala-js.org/)
* Use Angular 2 on the client-side

## Links

* [Heroku Scala Support](https://devcenter.heroku.com/articles/scala-support)
* [Getting Started on Heroku with Scala and Play](https://devcenter.heroku.com/articles/getting-started-with-scala#introduction)
* https://github.com/typesafehub/activator/issues/979
* https://devcenter.heroku.com/articles/using-multiple-buildpacks-for-an-app
* https://devcenter.heroku.com/articles/deploy-scala-and-play-applications-to-heroku-from-jenkins-ci
