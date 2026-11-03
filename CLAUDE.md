# CLAUDE.md

## Skills
For all Android/Kotlin work, read `.claude/skills/compose-kotlin-agent-skills/SKILL.md` first.
Load the matching reference file from `references/` for the specific topic.

## Commit convention

Commit messages follow the [Conventional Commits 1.0.0](https://www.conventionalcommits.org/en/v1.0.0/#specification) specification.

### Format

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### Rules

- **type** is required, followed by an optional scope in parentheses, an optional `!`, then `: ` and a short description.
- `feat`: introduces a new feature (correlates with a SemVer MINOR).
- `fix`: patches a bug (correlates with a SemVer PATCH).
- Other allowed types: `build`, `chore`, `ci`, `docs`, `style`, `refactor`, `perf`, `test`, `revert`.
- **scope**: a noun describing the affected part of the codebase, in parentheses, e.g. `feat(data): ...`, `fix(app): ...`.
- **description**: a short summary right after `: `, in the imperative mood, no trailing period.
- **body**: separated from the description by a blank line, free-form text giving context.
- **footers**: separated from the body by a blank line, in the form `Token: value` or `Token #value` (e.g. `Refs: #123`, `Co-Authored-By: ...`). Tokens use `-` instead of spaces.
- **Breaking change**: indicated by a `!` before the `:` (e.g. `feat(api)!: ...`) and/or a `BREAKING CHANGE: <description>` footer (uppercase). Correlates with a SemVer MAJOR.

### Examples

```
feat(app): display the albums list
fix(data): handle empty API response
ci: add GitHub Actions workflow for build and unit tests
build: bump Gradle to 8.14.6
refactor(app)!: replace LiveData with StateFlow

BREAKING CHANGE: AlbumsViewModel now exposes a StateFlow.
```
