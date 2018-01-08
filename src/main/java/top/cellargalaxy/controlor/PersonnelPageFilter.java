package top.cellargalaxy.controlor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import top.cellargalaxy.bean.daoBean.Person;
import top.cellargalaxy.configuration.PersonneConfiguration;
import top.cellargalaxy.util.ControlorUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by cellargalaxy on 17-12-21.
 */
@WebFilter(filterName = "personnelPageFilter", urlPatterns = PersonnelPageControlor.PERSONNEL_PAGE_CONTROLOR_URL + "/*")
public class PersonnelPageFilter implements Filter {
	@Autowired
	private PersonneConfiguration personneConfiguration;
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
		
		Person loginPerson = ControlorUtil.getLoginPerson(httpServletRequest.getSession());
		if (loginPerson == null) {
			ControlorUtil.setInfo(httpServletRequest.getSession(), "请登录");
			httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/");
			return;
		}
		httpServletRequest.setAttribute("token", personneConfiguration.getToken());
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
	
	@Override
	public void destroy() {
	
	}
}
