0.0.12 (9th September 2016)

* Limit length of usernames displayed
* Some UI tweaks

0.0.11 (8th September 2016)

* Added support for user registration, login and logout
* Added a landing page to provide a choice between casual and serious game play
* Now forcing use of SSL on Heroku
* Cosmetic tweaks
* Layout tweaks
* Added leaderboard columns for the number of games played and the total number of points

## Bug Fixes

* Usernames containing characters that are not valid in actor names caused problems
* Need to use wss for the leaderboard WebSocket connection when loading the page over https

0.0.10 (2nd September 2016)

* Leaderboard columns are now Won/Drawn/Lost and sort order is based on 3 points for a win and 1 point for a draw
* Added Akka Persistence for the leaderboard (state is saved to Postgres)

0.0.9 (1st September 2016)

* Latent work on registered games

0.0.8 (22nd August 2016)

* The action that computes the computer move is now implemented inside an actor

0.0.7 (21st August 2016)

* Fixed a race condition

0.0.6 (20th August 2016)

* Use &nbsp; for empty cells so that the cells have a fixed size
* Added sbt-heroku plugin to deploy to Heroku via Jenkins

0.0.5 (20th August 2016)

* We now choose who goes first at random

0.0.4 (20th August 2016)

* Fixed bug whereby clicking on the instruction panel would start a new game
* On a draw, all cells are highlighted in amber

0.0.3 (20th August 2016)

* Player 1 win is now highlighted in green
* Removed cruft added by the Activator template

0.0.2 (19th August 2016)

* Added babel-polyfill - it now works on my iPad 1

0.0.1 (19th August 2016)

* Added version number
