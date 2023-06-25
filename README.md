<h1 align="start">Finn Server</h1>

<p align="start">
  <a href="https://medium.com/@eduardofelipi"><img alt="Medium" src="https://img.shields.io/static/v1?label=Medium&message=@edu_santos&color=gray&logo=medium"/></a>
  <a href="https://www.youtube.com/channel/UCYcwwX7nDU_U0FP-TsXMwVg"><img alt="Profile" src="https://img.shields.io/static/v1?label=Youtube&message=edu_santos&color=red&logo=youtube"/></a> 
  <a href="https://github.com/edufelip"><img alt="Profile" src="https://img.shields.io/static/v1?label=Github&message=edufelip&color=white&logo=github"/></a> 
  <a href="https://www.linkedin.com/in/eduardo-felipe-dev/"><img alt="Linkedin" src="https://img.shields.io/static/v1?label=Linkedin&message=edu_santos&color=blue&logo=linkedin"/></a> 
</p>

<p align="start">  
  This is a RESTful API for the Android App Finn
</p>

## Prerequisites

- [NodeJS](https://nodejs.org)
- [Yarn](https://yarnpkg.com/)

## Clone

Clone the repository from GitHub.

```
$ git clone https://github.com/edufelip/finn__backend.git
```

## Add .env Parameters

For the API to work properly the following values are required:

| Variable Name                     | Description                    |
|-----------------------------------|--------------------------------|
| DB_NAME                   | Name of the PostgreSQL database the api is going to use |
| DEVDB_NAME                  | Name of the PostgreSQL database the api is going to use to run the tests |
| DB_HOST                | Database of the address (Use localhost if you're running the db in your machine) |
| DB_PORT              | The port your db is using. Since this api requires a postgreSQL db the value is supposed to be 5432 |
| DB_USER                | User of your postgreSQL database |
| DB_PASSWORD                | User's password |

1. Copy and rename `.env.example` to `.env`.
2. Fill the required fields properly;
3. You're good to go.

## Install Dependencies and Run the Server

```
$ yarn
$ yarn dev
```
Now, the application will be running on http://localhost:3333.

## Running Tests
Since this api uses the Jest library to perform tests, you can run the following command to test the application

```
$ yarn test
``` 

## Maintainers
This project is mantained by:
* [Eduardo Felipe](http://github.com/edufelip)

## Contributing

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -m 'Add some feature')
4. Push your branch (git push origin my-new-feature)
5. Create a new Pull Request
