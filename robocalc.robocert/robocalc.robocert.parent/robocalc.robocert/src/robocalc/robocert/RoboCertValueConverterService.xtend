package robocalc.robocert

import org.eclipse.xtext.conversion.ValueConverter
import org.eclipse.xtext.conversion.IValueConverter
import org.eclipse.xtext.conversion.impl.AbstractNullSafeConverter
import org.eclipse.xtext.nodemodel.INode
import org.eclipse.xtext.conversion.ValueConverterException
import org.eclipse.xtext.common.services.Ecore2XtextTerminalConverters

/**
 * Value converter service for RoboCert.
 * 
 * This extends the default converter to add a converter for CSP code.
 */
class RoboCertValueConverterService extends Ecore2XtextTerminalConverters {
	/**
	 * @return a converter to deal with inline CSP.
	 */
	@ValueConverter(rule = "CSP_CODE")
	def IValueConverter<String> getCspCodeConverter() {
		new DelimitedStringConverter("csp-begin", "csp-end")
	}

	/**
	 * @return a converter to deal with shorthand inline passages.
	 */
	@ValueConverter(rule = "SHORT_CODE")
	def IValueConverter<String> getShortCodeConverter() {
		new DelimitedStringConverter("<$", "$>")
	}
}