# Move Together Codex Guidance

## Project context

Move Together is a Java 17 Spring Boot application using Spring MVC, Spring Security, Spring Data JPA, Thymeleaf, QueryDSL, PostgreSQL, JUnit 5, MockMvc, Testcontainers, and ArchUnit.

## Review guidelines

- Prioritize bugs, security regressions, authorization gaps, data integrity issues, and missing tests over style-only comments.
- Check account, club, event, enrollment, notification, tag, and zone changes for broken ownership or permission checks.
- Treat authentication, CSRF, session handling, email verification, and password handling regressions as high priority.
- Verify controller changes have meaningful MockMvc coverage when they affect user-visible behavior or permissions.
- Verify service and repository changes preserve transaction boundaries, validation rules, and expected JPA loading behavior.
- Watch for N+1 query regressions, unsafe lazy-loading assumptions in templates, and missing QueryDSL updates.
- For Thymeleaf form changes, check validation error rendering, CSRF handling, binding names, and route consistency.
- For architecture changes, keep module boundaries consistent with the existing package structure and ArchUnit tests.

## Verification

- Use `./gradlew build` as the default full verification command.
- Use focused tests when reviewing a small area, for example `./gradlew test --tests '*AccountControllerTest'`.
- Do not require unrelated refactors or formatting-only changes in a PR review.
