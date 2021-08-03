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
	 * @return a converter to deal with CSP fragments.
	 */
	@ValueConverter(rule = "CSP_CODE")
	def IValueConverter<String> getCspCodeConverter() {
		new AbstractNullSafeConverter<String> {
			
			override protected internalToString(String value
			) '''csp-begin
	«value»
csp-end'''
			
			override protected internalToValue(String string, INode node) throws ValueConverterException {
				// stripping 'csp-begin' (9 chars) and 'csp-end' (7 chars).
				// TODO: is this the right way to do this, or do we need a value
				// converter?
				string.substring(9, string.length - 7).strip
			}
		}
	}
}