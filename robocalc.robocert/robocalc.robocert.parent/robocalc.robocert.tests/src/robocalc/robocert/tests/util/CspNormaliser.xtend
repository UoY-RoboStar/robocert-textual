package robocalc.robocert.tests.util

/**
 * Basic CSP normalisation to try make comparison of CSP strings tractable.
 */
class CspNormaliser {
	/**
	 * Tidies up a CSP char sequence, returning it as a string.
	 *
	 * The stringification is because equality comparison doesn't work
	 * properly between char sequences.
	 *
	 * @param it  the CSP sequence to tidy.
	 * 
	 * @returns a tidied string.
	 */
	def String tidy(CharSequence it) {
		toString.strip.replaceAll("\\s+", " ")
	}
}