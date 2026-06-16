# Contributing to VoiceCalendar AI

## Development Workflow

1. **Branch from `develop`**: All feature work starts from the `develop` branch.
2. **Feature branches**: `feature/<feature-name>`
3. **Branches for releases**: `release/<version>`
4. **Branches for fixes**: `fix/<bug-description>`

## Conventional Commits

All commits must follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`

## Pull Request Process

1. Create a feature branch from `develop`
2. Implement your changes
3. Write/update tests
4. Ensure all tests pass: `./gradlew test`
5. Run lint: `./gradlew lint`
6. Create a PR from `feature/*` to `develop`
7. PR title must follow Conventional Commits format
8. Request review at least one maintainer
9. After approval, squash-merge into `develop`

## Code Style

- Follow Kotlin coding conventions
- Use `ktfmt` or `ktlint` for formatting
- Maximum line length: 120 characters
- Use descriptive variable/function names
- Document public APIs with KDoc
- Keep functions focused and small

## Project Conventions

- Use `Flow` for reactive streams; `StateFlow` for UI state
- Domain layer must not depend on Android framework
- Data layer holds all Android-specific implementations
- All dependencies via Hilt constructor injection
- Resource strings must be added to all locale files
- Write tests for every use case and repository
