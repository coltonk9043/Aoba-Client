# Code Style and Guidelines
* Follow the coding style and conventions used throughout the project.
* Write clear, meaningful commit messages.
* Ensure your code is well-documented, especially public methods and classes.

# Review Process
* Pull Requests (PRs) will undergo a review process by project maintainers.
* Feedback or changes may be requested before a PR is accepted.
* Be patient during the review process and be open to constructive feedback.

# Running Tests
The project uses JUnit 5 (with AssertJ and Mockito) for unit tests. Run them locally with:

```
./gradlew test
```

Tests live under `src/test/java` and mirror the `src/main/java` package layout. The suite is being grown incrementally alongside the ongoing UI work — see issue #113 for the roadmap. Reports are written to `build/reports/tests/test/index.html` and CI uploads them as artifacts on every push and pull request.

# Code of Conduct
This project adheres to the Contributor Covenant Code of Conduct. By participating, you are expected to uphold this code. Please report any unacceptable behavior to [coltonk9043](mailto:coltonk9043@yahoo.ca).
