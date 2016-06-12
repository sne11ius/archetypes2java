package es.archetyp.archetypes2.backend.code.boundary;

import javax.annotation.PostConstruct;

import org.python.core.PyException;
import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Service;

@Service
public class CodeHighlighterService {

	private PythonInterpreter interpreter;

	@PostConstruct
	public void init() {
		interpreter = new PythonInterpreter();
		interpreter.exec("from pygments import highlight\n"
		    + "from pygments.formatters import HtmlFormatter\n"
			+ "from pygments.lexers import PythonLexer\n"
		    + "from pygments.lexers import get_lexer_for_filename\n");
	}

	public String highlightCode(final String code, final String filename) {
		interpreter.set("code", code);
		interpreter.set("filename", filename);
		interpreter.exec("formatter = HtmlFormatter(linenos=True)");
		try {
			interpreter.exec("lexer = get_lexer_for_filename(filename)");
			interpreter.exec("result = highlight(code, lexer, formatter)");
			return interpreter.get("result", String.class);
		} catch (final PyException e) {
			interpreter.exec("result = highlight(code, PythonLexer(), formatter)");
			return interpreter.get("result", String.class);
		}
	}

}
