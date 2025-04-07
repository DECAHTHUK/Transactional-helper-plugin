# Spring Transaction Helper Plugin

## Overview

The **Spring Transaction Helper** plugin is designed to enhance the development experience for users of the Spring Framework by providing robust support for declarative transaction management using the `@Transactional` annotation. This plugin was developed as part of a bachelor thesis, aiming to address common challenges in managing transactions within Spring applications.

## Features

- **Inspections:**
    - **LazyInitializationInspection:** Helps identify potential issues related to lazy initialization.
    - **MandatoryPropagationInspection:** Ensures mandatory propagation behavior is correctly implemented.
    - **NeverPropagationInspection:** Detects misuse of the `NEVER` propagation setting.
    - **PotentiallyUnwantedNestedTransactionInspection:** Highlights potentially unwanted nested transactions.
    - **TransactionalSelfInvocationInspection:** Identifies self-invocation issues within transactional methods.

- **Method Call Tree:**
    - Builds a comprehensive tree of method calls and their `@Transactional` states, providing a clear overview of transactional boundaries within your application.

- **User Interface:**
    - Offers an intuitive UI to visualize the method call tree, making it easier to debug and optimize transactional behavior.

## Installation

To install the Spring Transaction Helper plugin:

1. Open your JetBrains IDE.
2. Go to `File > Settings > Plugins`.
3. Search for "Spring Transaction Helper".
4. Click `Install` and restart your IDE.

## Usage

Once installed, the plugin will automatically analyze your Spring application for transactional issues. You can access the method call tree and inspection results through the plugin's UI, which can be found in the tool window.

## Contributing

This plugin is part of an ongoing bachelor thesis project. Contributions and feedback are welcome! Please feel free to open issues or submit pull requests.

## License

This project is licensed under the MIT License. See the [LICENSE](https://mit-license.org/) file for details.
