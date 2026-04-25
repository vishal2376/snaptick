package com.vishal2376.snaptick.presentation.common.animation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Shared motion specs. Import from here instead of declaring ad-hoc `tween`
 * or `spring` at each call site, so the feel of the app stays consistent and
 * any future tuning is a one-line change.
 */
object SnaptickMotion {

	/** Apple-like layout spring: lightly damped, medium-low stiffness. */
	val gentleSpring: SpringSpec<Float> = spring(
		dampingRatio = 0.82f,
		stiffness = Spring.StiffnessMediumLow
	)

	/** Press / selection spring: faster, more damped. */
	val snappySpring: SpringSpec<Float> = spring(
		dampingRatio = 0.9f,
		stiffness = Spring.StiffnessMedium
	)

	/** Drop-in fade transition for entrance animations on list items. */
	val fadeInShort: TweenSpec<Float> =
		tween(durationMillis = 220, easing = FastOutSlowInEasing)

	/** Cap per-item entrance stagger on long lists. Everything past this
	 *  index appears instantly. Saves CPU on low-end devices. */
	const val MAX_STAGGERED_ITEMS = 10
}
