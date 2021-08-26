package robocalc.robocert

import org.eclipse.xtext.conversion.impl.AbstractNullSafeConverter
import org.eclipse.xtext.nodemodel.INode
import org.eclipse.xtext.conversion.ValueConverterException

/**
 * A value converter that strips fixed delimiters from a string.
 */
class DelimitedStringConverter extends AbstractNullSafeConverter<String> {
	String start
	String end

	new(String start, String end) {
		this.start = start
		this.end = end
	}
	
	override protected internalToString(String value) '''«start»«value»«end»'''
	
	override protected internalToValue(String string, INode node) throws ValueConverterException {
		string.substring(start.length, string.length - end.length)
	}
}