package hellang.compiler.profiles

import org.springframework.context.annotation.Profile

@Profile(TEST_PROFILE_NAME)
internal annotation class TestProfile

internal const val TEST_PROFILE_NAME = "test"