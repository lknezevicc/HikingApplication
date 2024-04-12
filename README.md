# Hiking Route Tracker

The final university project from the "Java Programming" course, designed with the aim of creating a user interface application for tracking and sharing hiking routes. This application enables users to add routes along with descriptive images to highlight the most interesting parts of each route. It is implemented in Java and JavaFX, utilizing a local SQLite database for efficient data storage.

<p align="center">
  <img width="300" alt="login" src="https://github.com/lknezevicc/HikingApplication/assets/126179039/cc51f0cc-a65a-4ca3-8b73-87e299f59ebd">
  <img width="550" alt="route-view" src="https://github.com/lknezevicc/HikingApplication/assets/126179039/2e5a25e6-b841-4fd3-9a08-c84f1bc5ed9d">
</p>

## How to Run

To run this application, follow these steps:

1. Ensure you have the latest version of Java JDK (JDK 22) and JavaFX (22).

2. Clone the repository:
```bash
git clone https://github.com/lknezevicc/HikingApplication.git
```
3. Navigate into project directory.

4. Build the application using Maven:
```bash
mvn clean install
```
5. Run using this command:
```bash
java -jar target/HikingApplication-1.0-SNAPSHOT.jar
```
* NOTE: Please replace `HikingApplication-1.0-SNAPSHOT.jar` with the actual name of your generated jar file.

Use the following credentials for the default admin login:
* Username: admin1
* Password: Admin1

## Additional Features

* **Image Gallery:** Explore a gallery of images associated with each hiking route, providing a visual representation of the journey.
* **User Contributions:** Users can actively contribute by adding new routes and sharing their experiences with the community.
* **Admin Control:** An admin panel is available with enhanced privileges for managing routes and user accounts.
* **Changelog with Export:** Keep track of project changes using the changelog, which can be exported in .xlsx format for documentation and version history.
* **Multithreading Synchronization:** The application utilizes multithreading synchronization to ensure data consistency and improve performance.

## Feedback

Feel free to reach out for any issues, suggestions, or collaborations!

Happy hiking! 🏞️
