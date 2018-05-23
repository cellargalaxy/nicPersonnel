package top.cellargalaxy.controlor;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import top.cellargalaxy.service.PersonnelService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by cellargalaxy on 17-12-13.
 */
@WebFilter(filterName = "personnelApiFilter", urlPatterns = PersonnelApiControlor.PERSONNEL_API_CONTROLOR_URL + "/*")
public class PersonnelApiFilter implements Filter {
	@Autowired
	private PersonnelService personnelService;
	private FilterConfig filterConfig;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
		
		String token = httpServletRequest.getParameter("token");
		if (!personnelService.checkToken(token)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("result", false);
			jsonObject.put("data", "no permissions");
			httpServletResponse.getWriter().write(jsonObject.toString());
			return;
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
	
	@Override
	public void destroy() {
	
	}
}
