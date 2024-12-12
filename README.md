# Exchange CLI

A simple and powerful command-line tool to check real-time currency exchange rates directly from the terminal. Currency
Exchange CLI lets you stay updated with the latest exchange rates, with support for multiple base currencies and
interactive commands.

## Features

- **Real-Time Currency Rates:**
    - Retrieve exchange rates for a selected base currency (default is USD).
    - Supports various currencies like EUR, GBP, JPY, CNY, and more.
    - Easily switch between base currencies.

- **Interactive CLI Interface:**
    - Clear, colorful commands and output for a smooth user experience
      using [Kotter](https://github.com/varabyte/kotter).
    - Display exchange rates in a human-readable format.

## Installation

1. Clone this repository:
    ```bash
    git clone https://github.com/adriianh/currency-exchange-cli.git
    cd currency-exchange-cli
    ```

2. Set up the environment:
   Ensure you have Kotlin and Gradle installed on your system. Then, build the project:
    ```bash
    ./gradlew build
    ```

3. Run the application:
    ```bash
    ./gradlew run
    ```