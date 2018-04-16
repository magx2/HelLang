package hellang.compiler.profiles

import org.springframework.context.annotation.Profile

@Profile(NOT_TEST_PROFILE_NAME)
internal annotation class NotTestProfile

internal const val NOT_TEST_PROFILE_NAME = "!$TEST_PROFILE_NAME"