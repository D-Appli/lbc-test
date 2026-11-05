package fr.leboncoin.libs.albums.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import com.adevinta.spark.SparkTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlbumLabelTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var parentClicks = 0

    @Test
    fun `clicking the label does not click the parent`() {
        setLabelInClickableParent()

        label().performClick()

        assertEquals(0, parentClicks)
    }

    @Test
    fun `clicking next to the label clicks the parent`() {
        setLabelInClickableParent()

        composeRule.onNodeWithTag(PARENT_TAG).performTouchInput { click(Offset(1f, centerY)) }

        assertEquals(1, parentClicks)
    }

    @Test
    fun `the label has no click action`() {
        setLabelInClickableParent()

        label().assertHasNoClickAction()
    }

    @Test
    fun `a list can be scrolled from the label`() {
        val scrollState = ScrollState(initial = 0)
        composeRule.setContent {
            SparkTheme {
                Column(Modifier.height(200.dp).verticalScroll(scrollState)) {
                    AlbumLabel(text = "Album #1", modifier = Modifier.testTag(LABEL_TAG))
                    Spacer(Modifier.height(1000.dp))
                }
            }
        }

        label().performTouchInput { swipe(start = center, end = center.copy(y = center.y - 300f)) }

        composeRule.runOnIdle { assertTrue(scrollState.value > 0) }
    }

    private fun setLabelInClickableParent() {
        composeRule.setContent {
            SparkTheme {
                Box(
                    Modifier
                        .testTag(PARENT_TAG)
                        .clickable { parentClicks++ }
                        .padding(start = 64.dp),
                ) {
                    AlbumLabel(text = "Album #1", modifier = Modifier.testTag(LABEL_TAG))
                }
            }
        }
    }

    // The clickable parent merges the label in its own node, whose center is not on the label.
    private fun label() = composeRule.onNodeWithTag(LABEL_TAG, useUnmergedTree = true)

    private companion object {
        const val LABEL_TAG = "label"
        const val PARENT_TAG = "parent"
    }
}
