package top.cellargalaxy.controlor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.cellargalaxy.bean.daoBean.Person;
import top.cellargalaxy.service.PersonnelService;
import top.cellargalaxy.util.ControlorUtil;

import javax.servlet.http.HttpSession;

/**
 * Created by cellargalaxy on 17-12-21.
 */
@Controller
@RequestMapping(PersonnelPageControlor.PERSONNEL_PAGE_CONTROLOR_URL)
public class PersonnelPageControlor {
	public static final String PERSONNEL_PAGE_CONTROLOR_URL = "/page";
	
	@Autowired
	private PersonnelService personnelService;
	
	@GetMapping("")
	public String personnel() {
		return "personnel";
	}
	
	@GetMapping("/addPersons")
	public String addPersons() {
		return "addPersons";
	}
	
	@GetMapping("/listPermission")
	public String listPermission() {
		return "listPermission";
	}
	
	@GetMapping("/listPerson")
	public String listPerson() {
		return "listPerson";
	}
	
	@GetMapping("/listAuthorized")
	public String listAuthorized() {
		return "listAuthorized";
	}
	
	@GetMapping("/personForm/{id}")
	public String personForm(Model model, HttpSession session, @PathVariable("id") String id) {
		Person loginPerson= ControlorUtil.getLoginPerson(session);
		if (loginPerson == null ) {
			ControlorUtil.setInfo(session,"请登录");
			return "redirect:/login";
		}
		if (!loginPerson.getId().equals(id) && !personnelService.checkPersonnelAdmin(loginPerson)) {
			ControlorUtil.setInfo(session,"你没有管理员权限修改他人信息，请用管理员账号登录");
			return "redirect:/login";
		}
		model.addAttribute("id", id);
		return "personForm";
	}
}
