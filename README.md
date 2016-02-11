# sierak

Akka HTTP app for checking if the lecturer has released exam results.

To run:

`sbt run`

Requires `MAIL_LOGIN` and `MAIL_PASSWORD` environmental variables to be set - currently only supports Gmail accounts.

Requires recipient addresses in `MAIL_RECIPIENTS`, separated by commas.

To check if results are available:

`GET / HTTP/1.1`

The app checks for results every `1.minute` and, if there are any, sends emails to the specified addresses.
