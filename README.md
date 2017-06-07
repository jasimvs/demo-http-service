# demo-http-service

A HTTP service using Http4s, Circe and Cats.

This is my first attempt to use Http4s, Circe, Cats, and try to write as pure as practical FP code.

The app is a simple move reservation service, which allows user to register a show, book tickets for regitered shows, and get a a shows current status.

Use `sbt run` to start the program.

Use the following requests to run it:
To register a new show:
POST to http://localhost:8080/show
with header: "Content-Type: application/json"
and body: {"imdbId":"tt555112", "availableSeats":5, "screenId":"s12345"

To book a ticket:
POST to http://localhost:8080/reserve
with header: "Content-Type: application/json"
and body: {"imdbId":"tt555112", "screenId":"a12345"}

To get a show's details:
GET to http://localhost:8080/show?imdbId=tt555112&screenId=qwerty


