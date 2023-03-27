# TIS Trainee Reference

[![Build Status][build-badge]][build-href]
[![License][license-badge]][license-href]
[![Quality Gate Status][quality-gate-badge]][quality-gate-href]
[![Coverage Stats][coverage-badge]][coverage-href]

## About

This is a Spring Boot microservice which provides a REST API for retrieving and
managing reference data, such as Gender and Grade, to be used as lists of
allowed values.

## Developing

### Running

```shell
gradlew bootRun
```

#### Pre-Requisites

 - A MongoDB instance.

#### Environmental Variables

| Name               | Description                                   | Default   |
| ------------------ | --------------------------------------------- | --------- |
| DB_HOST            | The MongoDB host to connect to.               | localhost |
| DB_PORT            | The port to connect to MongoDB on.            | 27017     |
| DB_NAME            | The name of the MongoDB database.             | reference |
| DB_USER            | The username to access the MongoDB instance.  | admin     |
| DB_PASSWORD        | The password to access the MongoDB instance.  | pwd       |
| AUTH_SOURCE        | The authentication database.                  | admin     |
| SENTRY_DSN         | A Sentry error monitoring Data Source Name.   |           |
| ENVIRONMENT | The environment to log Sentry events against. | local     |

#### Usage Examples

##### Create a Reference Value

```
POST /reference/api/{reference_type}
```

##### Get all Reference Values

```
GET /reference/api/{reference_type}
```

##### Update a Reference Value

```
PUT /reference/api/{reference_type}
```

##### Delete a Reference Value

```
DELETE /reference/api/{reference_type}/{tisId}
```

### Testing

The Gradle `test` task can be used to run automated tests and produce coverage
reports.
```shell
gradlew test
```

The Gradle `check` lifecycle task can be used to run automated tests and also
verify formatting conforms to the code style guidelines.
```shell
gradlew check
```

### Building

```shell
gradlew bootBuildImage
```

## Versioning

This project uses [Semantic Versioning](https://semver.org).

## License

This project is license under [The MIT License (MIT)](LICENSE).

[coverage-badge]: https://sonarcloud.io/api/project_badges/measure?project=Health-Education-England_tis-trainee-reference&metric=coverage
[coverage-href]: https://sonarcloud.io/component_measures?metric=coverage&id=Health-Education-England_tis-trainee-reference
[build-badge]: https://badgen.net/github/checks/health-education-england/tis-trainee-reference?label=build&icon=github
[build-href]: https://github.com/Health-Education-England/tis-trainee-reference/actions/workflows/ci-cd-workflow.yml
[license-badge]: https://badgen.net/github/license/health-education-england/tis-trainee-reference
[license-href]: LICENSE
[quality-gate-badge]: https://sonarcloud.io/api/project_badges/measure?project=Health-Education-England_tis-trainee-reference&metric=alert_status
[quality-gate-href]: https://sonarcloud.io/summary/new_code?id=Health-Education-England_tis-trainee-reference
