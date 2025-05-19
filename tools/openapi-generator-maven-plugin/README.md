# OpenAPI Generator Maven Plugin

## Overview

The `openapi-generator-maven-plugin` is a custom Maven plugin designed for the OpenMRS ecosystem. Its primary function is to print a friendly message during the Maven build lifecycle, serving as a foundational tool for future enhancements related to OpenAPI specification generation.

## Purpose

This plugin aims to facilitate integration with OpenMRS modules, particularly the `webservices.rest` module, by providing a simple yet effective way to execute custom build actions.

## Features

- **Maven Goal**: `hello`
- **Output**: Logs "Hello from OpenMRS Maven Plugin! 🎉" during the build process.
- **Lifecycle Phase**: Can be executed during the `process-classes` or `compile` phase.
- **Integration**: Designed to be easily integrated into OpenMRS modules.

## Installation

To install the plugin locally, navigate to the project directory and run:

```bash
mvn clean install
```

This command will compile the plugin and install it into your local Maven repository.

## Usage

To use the plugin in your OpenMRS module, add the following configuration to your `pom.xml`:

```xml
<plugin>
  <groupId>org.openmrs.plugin</groupId>
  <artifactId>openapi-generator-maven-plugin</artifactId>
  <version>1.0-SNAPSHOT</version>
  <executions>
    <execution>
      <id>say-hello</id>
      <phase>process-classes</phase>
      <goals>
        <goal>hello</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

After adding the plugin configuration, run the build:

```bash
mvn clean install
```

You should see the output: `Hello from OpenMRS Maven Plugin! 🎉`

## Future Enhancements

The plugin is intended to evolve with additional features, including:

- Support for loading resource classes.
- Scanning for annotations like `@Resource` and `@DocumentedResource`.
- Generating OpenAPI JSON specifications.
- Archiving documentation into module artifacts.

## Contribution

Contributions to enhance the functionality of this plugin are welcome. Please ensure to follow the project's coding standards and guidelines when submitting changes.