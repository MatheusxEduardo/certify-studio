# ADR 0001: Use a Modular Monolith Architecture

- Status: Accepted
- Date: 2026-08-30

## Context

Certify Studio is an application for creating, managing, and issuing PDF certificates.

The first version will support individual certificate issuance. Future versions are expected to include Excel import, batch issuance, certificate template management, authentication, and application deployment.

The initial architecture must keep the MVP simple while allowing the application to grow without mixing responsibilities or introducing unnecessary infrastructure.

## Decision

Certify Studio will use a modular monolith architecture organized by business feature.

The application will be developed and deployed as a single Spring Boot application. Its source code will be divided into modules representing the main business capabilities, such as:

- certificates;
- participants;
- templates;
- organizations;
- shared components.

Each module may use the Controller–Service–Repository pattern:

- **Controller:** receives HTTP requests, validates input, and returns HTTP responses;
- **Service:** coordinates use cases and applies business rules;
- **Repository:** provides access to persistent data;
- **Domain:** represents the business entities and concepts of the module.

Dependencies between modules must remain explicit. Shared code should be placed in a shared module only when it is genuinely used by more than one business feature.

## Alternatives Considered

### Traditional layered monolith

Organize the entire application into global packages such as `controller`, `service`, and `repository`.

This alternative is simple initially, but packages can become large and unrelated features may become tightly coupled as the application grows.

### Microservices

Separate the application into independently deployed services.

This alternative provides independent deployment and scaling, but would introduce unnecessary operational complexity for the current project, including distributed communication, service monitoring, and multiple deployment units.

## Consequences

### Positive

- The application remains simple to develop, test, and deploy.
- Related code is grouped by business feature.
- Module boundaries make responsibilities easier to understand.
- New features can be added incrementally.
- Parts of the application may be extracted into separate services in the future if there is a demonstrated need.

### Negative

- All modules are deployed together.
- Poorly controlled dependencies may weaken module boundaries.
- A failure in one module may affect the entire application.
- The team must actively prevent business logic from being placed in controllers or repositories.

## Notes

This decision does not require each planned module to be created immediately. Modules should be introduced as their corresponding features are implemented.

The architecture may be reviewed if the application's scale, deployment requirements, or team structure changes significantly.