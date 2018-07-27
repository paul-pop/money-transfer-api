# Money Transfer API

REST API used to transfer a given amount of money between two accounts (only supports GBP currency).

## Summary

Written in Java 8 and [Ratpack](https://ratpack.io) with:
- Lombok for awesomeness
- Uses JUnit and Ratpack Test for testing
- Uses basic in-memory data structures for persistence

## Build

If you want to build the application, please run:

```bash
mvn clean install
```

## Run tests

If you want to execute the tests, please run:

```bash
mvn clean verify
```

## Run app

You can run the app by:
- Running the main class from your IDE
- Building the app via Maven and running the generated uber jar from the target folder.
- Downloading the JAR from the [releases page](https://github.com/paul-pop/money-transfer-api/releases) and executing:

```bash
java -jar money-transfer-api-1.0-SNAPSHOT.jar
```

## Usage

A [swagger.yml](./swagger.yml) file has been provided that can be imported into [https://editor.swagger.io](https://editor.swagger.io),
unfortunately Ratpack doesn't offer a nice way of doing this inside the app.

When using the editor, it will call your localhost so make sure the app is started.

