package top.cellargalaxy.controlor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.cellargalaxy.bean.personnel.Person;
import top.cellargalaxy.service.PersonnelService;
import top.cellargalaxy.util.ControlorUtil;
import top.cellargalaxy.util.LogUtil;

import javax.servlet.http.HttpSession;

/**
 * Created by cellargalaxy on 17-12-8.
 */
@Controller
@RequestMapping(RootControlor.ROOT_CONTROLOR_URL)
public class RootControlor {
	public static final String ROOT_CONTROLOR_URL = "/";
	public static final String LOGIN_PERSON_NAME = "loginPerson";
	public static final String INFO_NAME = "info";
	@Autowired
	private PersonnelService personnelService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/login")
	public String login(HttpSession session, Model model) {
		Object info = session.getAttribute(INFO_NAME);
		if (info != null) {
			session.setAttribute(INFO_NAME, null);
			model.addAttribute(INFO_NAME, info);
		}
		return "login";
	}

	@ResponseBody
	@PostMapping("/login")
	public String login(HttpSession session, Person person) {
		if (personnelService.checkPersonnelDisabled(person) != null) {
			return ControlorUtil.createJSONObject(false, "账号为禁用状态，请等待管理员激活").toString();
		}
		if (personnelService.checkPassword(person) != null) {
			session.setAttribute(LOGIN_PERSON_NAME, person);
			LogUtil.info(logger, person, "登录成功");
			return ControlorUtil.createJSONObject(true, "登录成功").toString();
		} else {
			LogUtil.info(logger, person, "账号或密码错误");
			return ControlorUtil.createJSONObject(false, "账号或密码错误").toString();
		}
	}
}
