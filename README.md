# Health care information system

A health care information system “Medika” (with UI in Lithuanian language).

## Requirements

The following requirements must be satisfied to successfully run the app:

* Linux 64-bit,
* Node.js 8,
* Java 8,
* Maven 3.

## Getting started

All of the following commands are to be run from the terminal inside the project’s diretory `health-care-information-system`.

### Installation

In order to prepare the app and install the required packages, run the command:

```bash
chmod +x install.sh && ./install.sh
```

### Running the app

Once the installation is done, you can run the app using the command:

```bash
./backend.sh
```

Then you can access the app in a browser at <http://localhost:8080>.

### Logging in

You can login as an administrator using the username _admin_ and password _brb14678_ .

## Advanced

### Running the tests

In order to run the backend tests, run the command `mvn clean verify` inside the `health-care-information-system-backend` directory.

### Accessing the database

You can access the H2 Console application using a browser at <http://localhost:8080/console>. In order to be be able connect to the database using the H2 Console, you should set the JDBC URL to `jdbc:h2:~/health_care_information_system.db`, username to _sa_ and leave the password field empty at the H2 Cosole’s login page.

### Populating the database with example data

You can use an SQL script called `example_data.sql` located in the directory `health-care-information-system-backend/src/main/resources/database` to populate the database with example data. The easiest way to do this is to execute the INSERT statements found in the script `example_data.sql` using the H2 Console.

### Development

You can run the following commands inside the project folder `health-care-information-system` to help during the development process:

* `./backend.sh`<br />Runs the backend part of the app. Open <http://localhost:8080> to view it in a browser.

* `./frontend.sh`<br />Runs the frontend part of the app in development mode. Open <http://localhost:3000> to view it in a browser.

* `./build.sh`<br />Builds the frontend app for production and includes it in the backend app to serve it.

### Adhering to the project’s coding standards

If you modify either the backend or frontend code, you can run the following commands to help you adhere to the project’s coding standards:

* `./run-checkstyle.sh` inside the `health-care-information-system-backend` directory checks whether the backend code adheres to the project's coding standards and informs about issues (if any).
* `./run-prettier.sh` inside the `health-care-information-system-frontend` directory automatically formats frontend code to adhere to the project's coding standards.
