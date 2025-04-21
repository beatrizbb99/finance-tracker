# Finance Tracker

Finance Tracker is a simple application to manage and track your financial transactions.

## Features
- Add, edit, and delete transactions.
- Categorize transactions.
- View transaction history.
- Visualize monthly and category-based overviews.

## Getting Started

### Prerequisites
- Java 24 or higher
- Maven
- PostgreSQL

### Installation
1. Clone the repository:
    ```bash
    git clone https://github.com/beatrizbb99/finance-tracker.git
    ```
2. Navigate to the project directory:
    ```bash
    cd finance-tracker
    ```
3. Build the project:
    ```bash
    mvn javafx:run
    ```

### Configuration
Create a `db.properties` file in the `resources` directory with the following content:
```properties
db.name=financetracker
db.user=your_username
db.password=your_password
```

Replace `your_username` and `your_password` with your Postgres database credentials.

### Running the Application
Run the application using:
```bash
mvn spring-boot:run
```