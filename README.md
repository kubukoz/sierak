# sierak

Akka HTTP app for checking if the lecturer has released exam results.

To run:

`sbt run`

Requires `MAIL_LOGIN` and `MAIL_PASSWORD` environmental variables to be set - currently only supports Gmail accounts.

To check if results are available:

`GET / HTTP/1.1`

The app checks for results every `1.minute` and, if there are any, sends an email to the setup email account.
