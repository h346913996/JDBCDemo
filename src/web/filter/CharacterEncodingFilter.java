package web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharacterEncodingFilter implements Filter {
	private String charset;
	private static final String CHARSET_INIT_PARAMETER="charset";
	public void destroy() {
		
	}

	public void doFilter(ServletRequest req, ServletResponse resp,FilterChain chain) throws IOException, ServletException {
		req.setCharacterEncoding(charset);
		resp.setCharacterEncoding(charset);
		chain.doFilter(req, resp);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.charset = filterConfig.getInitParameter(CHARSET_INIT_PARAMETER);
		
	}

}
