package circus.robocalc.roboprop.prototype.csp

import static org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HelloXtendTest {
	@Test
	def void testHelloXtend () {
		assertEquals("Hello Xtend!", HelloXtend.message.toString)
	}
}