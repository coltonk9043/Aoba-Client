package net.aoba;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that JUnit 5, AssertJ and Mockito are wired up and runnable.
 * Intentionally does not touch any production class - concrete tests will
 * be added incrementally alongside the ongoing UI refactor.
 */
class FoundationSmokeTest {

    @Test
    void junitAndAssertJAreOnTheClasspath() {
        assertThat("aoba").startsWith("a").hasSize(4);
    }

    @Test
    void mockitoCanCreateAMock() {
        @SuppressWarnings("unchecked")
        java.util.List<String> mocked = Mockito.mock(java.util.List.class);
        Mockito.when(mocked.size()).thenReturn(42);

        assertThat(mocked.size()).isEqualTo(42);
    }
}
