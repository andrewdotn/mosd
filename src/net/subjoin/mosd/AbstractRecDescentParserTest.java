package net.subjoin.mosd;

import org.junit.Test;
import static org.junit.Assert.*;

public class AbstractRecDescentParserTest {
	
	public static class ParenParser
	extends AbstractRecDescentParser
	{
		private int _maxDepth, _minDepth;
		
		public ParenParser(String string) {
			init(string);
			
			_maxDepth = 0;
			_minDepth = 0;
			
			parse();
		}
		
		/* Returns whether the string is balanced */
		private void parse() {
			int depth = 0;

			while (!eof()) {
				if (lookahead('(')) {
					match('(');
					depth = updateMinMaxDepth(depth + 1);
				} else if (lookahead(')')) {
					match(')');
					depth = updateMinMaxDepth(depth - 1);
				} else {
					match();
				}
			}
		}
		
		private boolean valid() {
			return _minDepth >= 0;
		}
			
			private int updateMinMaxDepth(int newDepth) {
				if (newDepth < _minDepth)
					_minDepth = newDepth;
				if (newDepth > _maxDepth)
					_maxDepth = newDepth;
				return newDepth;
			}
			
			public int minDepth() {
				return _minDepth;
			}
		
			public int maxDepth() {
				return _maxDepth;
			}
	}
	
	public @Test void testBasicParser() {
		assertEquals(new ParenParser("").maxDepth(), 0);
		assertEquals(new ParenParser("()").maxDepth(), 1);
		assertEquals(new ParenParser(")").valid(), false);
		assertEquals(new ParenParser("(((((())()())))())").maxDepth(), 6);
	}
	
	public @Test void testErrorMessage() {
		boolean exceptionRaised = false;
		
		final String errString = "11121";
		
		try {
			new AbstractRecDescentParser() {
				{
					init(errString);
					parse();
				}
				
				private void parse() {
					while (!eof())
						match('1');
				}
			};
		} catch (ParseException e) {
			exceptionRaised = true;
			assertEquals(e.getMessage(), "Asked to match Ô1Õ at char 4: 111[ERROR->21");
		}
		assertTrue(exceptionRaised);
	}
}
