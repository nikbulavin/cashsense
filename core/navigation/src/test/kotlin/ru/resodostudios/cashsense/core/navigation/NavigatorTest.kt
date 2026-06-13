package ru.resodostudios.cashsense.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.Before
import org.junit.Test
import ru.resodostudios.core.navigation.NavigationState
import ru.resodostudios.core.navigation.Navigator
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

private object TestFirstTopLevelKey : NavKey
private object TestSecondTopLevelKey : NavKey
private object TestThirdTopLevelKey : NavKey
private object TestKeyFirst : NavKey
private object TestKeySecond : NavKey

class NavigatorTest {

    private lateinit var navigationState: NavigationState
    private lateinit var navigator: Navigator

    @Before
    fun setup() {
        val startKey = TestFirstTopLevelKey
        val topLevelStack = NavBackStack<NavKey>(startKey)
        val topLevelKeys = listOf(
            startKey,
            TestSecondTopLevelKey,
            TestThirdTopLevelKey,
        )
        val subStacks = topLevelKeys.associateWith { key -> NavBackStack(key) }

        navigationState = NavigationState(
            startKey = startKey,
            topLevelStack = topLevelStack,
            subStacks = subStacks,
        )
        navigator = Navigator(navigationState)
    }

    @Test
    fun `initial state should have correct start keys`() {
        assertEquals(navigationState.startKey, TestFirstTopLevelKey)
        assertEquals(navigationState.currentTopLevelKey, TestFirstTopLevelKey)
    }

    @Test
    fun `navigate should update sub-stack for current top level key`() {
        navigator.navigate(TestKeyFirst)

        assertEquals(navigationState.currentTopLevelKey, TestFirstTopLevelKey)
        assertEquals(navigationState.subStacks[TestFirstTopLevelKey]?.last(), TestKeyFirst)
    }

    @Test
    fun `navigate to top level key should update current top level key`() {
        navigator.navigate(TestSecondTopLevelKey)
        assertEquals(navigationState.currentTopLevelKey, TestSecondTopLevelKey)
    }

    @Test
    fun `navigate with single top should not add duplicate key to sub-stack`() {
        navigator.navigate(TestKeyFirst)

        assertContentEquals(
            navigationState.currentSubStack,
            listOf(TestFirstTopLevelKey, TestKeyFirst),
        )

        navigator.navigate(TestKeyFirst)

        assertContentEquals(
            navigationState.currentSubStack,
            listOf(TestFirstTopLevelKey, TestKeyFirst),
        )
    }

    @Test
    fun `navigate to top level key with single top should update current sub-stack`() {
        navigator.navigate(TestSecondTopLevelKey)
        navigator.navigate(TestKeyFirst)

        assertContentEquals(
            navigationState.currentSubStack,
            listOf(TestSecondTopLevelKey, TestKeyFirst),
        )

        navigator.navigate(TestSecondTopLevelKey)

        assertContains(navigationState.currentSubStack, TestSecondTopLevelKey)
    }

    @Test
    fun `sub-stack should maintain navigation history`() {
        navigator.navigate(TestKeyFirst)

        assertEquals(navigationState.currentKey, TestKeyFirst)
        assertEquals(navigationState.currentTopLevelKey, TestFirstTopLevelKey)

        navigator.navigate(TestKeySecond)

        assertEquals(navigationState.currentKey, TestKeySecond)
        assertEquals(navigationState.currentTopLevelKey, TestFirstTopLevelKey)
    }

    @Test
    fun `multi-stack navigation should preserve state across top level keys`() {
        // add to start stack
        navigator.navigate(TestKeyFirst)

        assertEquals(navigationState.currentKey, TestKeyFirst)
        assertEquals(navigationState.currentTopLevelKey, TestFirstTopLevelKey)

        // navigate to new top level
        navigator.navigate(TestSecondTopLevelKey)

        assertEquals(navigationState.currentKey, TestSecondTopLevelKey)
        assertEquals(navigationState.currentTopLevelKey, TestSecondTopLevelKey)

        // add to new stack
        navigator.navigate(TestKeySecond)

        assertEquals(navigationState.currentKey, TestKeySecond)
        assertEquals(navigationState.currentTopLevelKey, TestSecondTopLevelKey)

        // go back to start stack
        navigator.navigate(TestFirstTopLevelKey)

        assertEquals(navigationState.currentKey, TestKeyFirst)
        assertEquals(navigationState.currentTopLevelKey, TestFirstTopLevelKey)
    }

    @Test
    fun `goBack should pop non-top level key from current sub-stack`() {
        navigator.navigate(TestKeyFirst)
        navigator.navigate(TestKeySecond)

        assertContentEquals(
            navigationState.currentSubStack,
            listOf(TestFirstTopLevelKey, TestKeyFirst, TestKeySecond),
        )

        navigator.goBack()

        assertContentEquals(
            navigationState.currentSubStack,
            listOf(TestFirstTopLevelKey, TestKeyFirst),
        )

        assertEquals(navigationState.currentKey, TestKeyFirst)
        assertEquals(navigationState.currentTopLevelKey, TestFirstTopLevelKey)
    }

    @Test
    fun `goBack should pop top level key and return to previous stack`() {
        navigator.navigate(TestKeyFirst)
        navigator.navigate(TestSecondTopLevelKey)

        assertContains(navigationState.currentSubStack, TestSecondTopLevelKey)

        assertEquals(navigationState.currentKey, TestSecondTopLevelKey)
        assertEquals(navigationState.currentTopLevelKey, TestSecondTopLevelKey)

        // remove TopLevel
        navigator.goBack()

        assertContentEquals(
            navigationState.currentSubStack,
            listOf(TestFirstTopLevelKey, TestKeyFirst),
        )

        assertEquals(navigationState.currentKey, TestKeyFirst)
        assertEquals(navigationState.currentTopLevelKey, TestFirstTopLevelKey)
    }

    @Test
    fun `goBack multiple times should clear non-top level keys`() {
        navigator.navigate(TestKeyFirst)
        navigator.navigate(TestKeySecond)

        assertContentEquals(
            navigationState.currentSubStack,
            listOf(TestFirstTopLevelKey, TestKeyFirst, TestKeySecond),
        )

        navigator.goBack()
        navigator.goBack()

        assertContains(navigationState.currentSubStack, TestFirstTopLevelKey)

        assertEquals(navigationState.currentKey, TestFirstTopLevelKey)
        assertEquals(navigationState.currentTopLevelKey, TestFirstTopLevelKey)
    }

    @Test
    fun `goBack multiple times should clear top level keys and return to start stack`() {
        // second sub-stack
        navigator.navigate(TestSecondTopLevelKey)
        navigator.navigate(TestKeyFirst)

        assertContentEquals(
            navigationState.currentSubStack,
            listOf(TestSecondTopLevelKey, TestKeyFirst),
        )

        // third sub-stack
        navigator.navigate(TestThirdTopLevelKey)
        navigator.navigate(TestKeySecond)

        assertContentEquals(
            navigationState.currentSubStack,
            listOf(TestThirdTopLevelKey, TestKeySecond),
        )

        repeat(4) {
            navigator.goBack()
        }

        assertContains(navigationState.currentSubStack, TestFirstTopLevelKey)

        assertEquals(navigationState.currentKey, TestFirstTopLevelKey)
        assertEquals(navigationState.currentTopLevelKey, TestFirstTopLevelKey)
    }
}
