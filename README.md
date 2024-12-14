# Exchange CLI

A **simple**, **powerful**, and **interactive** command-line tool to check real-time currency exchange rates directly from your terminal. With Exchange CLI, you can effortlessly retrieve the latest exchange rates, convert between multiple currencies, and enjoy a responsive and user-friendly interface.

## Features 🚀

- **Real-Time Currency Exchange Rates**:
    - Retrieve exchange rates quickly for a selected base currency (default is `USD`).
    - Supports a wide range of currencies, including `EUR`, `GBP`, `JPY`, `CNY`, and more.
    - Easily switch between base currencies for custom requirements.

- **Interactive CLI Interface**:
    - Built with [Clikt](https://github.com/ajalt/clikt) for intuitive and easy-to-use command-line interactions.
    - Leverages [Kotter](https://github.com/varabyte/kotter) to display commands and outputs in a clean, colorful, and human-readable format.

- **Currency Conversion**:
    - Perform real-time conversions with multiple currencies.
    - Allows precise input of amounts, supports batch conversions, and displays results with enhanced formatting.

- **Multiplatform Support**:
    - Compatible with Windows, macOS, and Linux systems.

## Installation ⚙️

1. **Clone this repository**:
   ```bash
   git clone https://github.com/adriianh/currency-exchange-cli.git
   cd currency-exchange-cli
   ```

2. **Set up the environment**:
   Ensure **Kotlin** (v1.9 or higher) and **Gradle** are installed on your system. Then, build the project:
   ```bash
   ./gradlew installDist
   ```

3. **Navigate to the build directory**:
   ```bash
   cd ./dist/bin
   ```

4. **Run the application**:
   ```bash
   ./app
   ```

## Usage 🛠️

After installation, you can execute various commands to fetch exchange rates or perform currency conversions. Below are the available commands and their usage instructions:

After installation, you can execute various commands to fetch exchange rates or perform currency conversions. Here are a few examples:

1. **Display help**: Shows a list of available commands and options.

   ```bash
   ./app --help
   ```
   Displays all available commands and options in detail.
2. **Fetch currency rates**: Fetches the latest exchange rates for a specific base currency.
   ```bash
   ./app fetch -c USD
   ```
   Fetches the latest exchange rates using `USD` as the base currency.

3. **Convert currencies interactively**:
   Launch the interactive conversion mode:
   ```bash
   ./app convert
   ```

4. **Direct conversion**:
   To convert between specific currencies, run:
   ```bash
   ./app convert -a 100 -c USD -e EUR,GBP
   ```
   Converts `100 USD` to `EUR` and `GBP`.

5. **View historical rates**:
   Fetch historical exchange rates for a specific date:
   ```bash
   ./app history -c USD -d 2023-01-01
   ```
   This retrieves exchange rates for `USD` on `2023-01-01`
6. 
6. **Fetch and display all supported currencies**:
   View a list of all supported currencies and their codes:
   ```bash
   ./app fetch --list
   ```
   Displays a detailed table of all available currency codes.

## Contributing 🤝

Contributions are welcome! Feel free to fork the repository, make changes, and submit a pull request. For major changes, please open an issue first to discuss what you'd like to implement.

## License 📜

This project is licensed under the [MIT License](./LICENSE).

## Acknowledgments 🙌

Special thanks to the authors of:
- [Clikt](https://github.com/ajalt/clikt): Simplifying CLI command-handling in Kotlin.
- [Kotter](https://github.com/varabyte/kotter): Crafting user-friendly, colorful terminal UIs.