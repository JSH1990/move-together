# Codex PR Review Prompt

Review this pull request for the Move Together project.

Focus on serious issues that should block or delay merging:

- Runtime bugs or broken user flows.
- Security problems in authentication, authorization, CSRF, sessions, email verification, or password handling.
- Missing ownership checks for account, club, event, enrollment, notification, tag, or zone behavior.
- Data integrity, transaction, validation, JPA loading, or QueryDSL regressions.
- Missing or weak tests for changed behavior.
- Template/controller mismatches in Thymeleaf views and Spring MVC routes.

Use the repository guidance in `AGENTS.md`.

Keep the review concise. Report only high-impact findings with file and line references when possible. If you do not find serious problems, say that clearly and mention any meaningful residual test risk.

Do not modify files in this workflow.

Write the pull request review in Korean. Use concise, developer-friendly Korean. Keep file paths, class names, method names, and commands in their original form.

//test2
