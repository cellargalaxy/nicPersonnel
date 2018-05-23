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
		if ((person = personnelService.checkPassword(person)) != null) {
			session.setAttribute(LOGIN_PERSON_NAME, person);
			LogUtil.info(logger, person, "登录成功");
			return ControlorUtil.createJSONObject(true, "登录成功").toString();
		} else {
			LogUtil.info(logger, person, "账号或密码错误");
			return ControlorUtil.createJSONObject(false, "账号或密码错误").toString();
		}
	}

	@GetMapping("/signUp")
	public String signUp() {
		return "signUp";
	}

	@ResponseBody
	@PostMapping("/signUp")
	public String signUp(Person person, String pw) {
		String string = checkPerson(person, pw);
		if (string != null) {
			return string;
		}
		if (personnelService.addPerson(person)) {
			LogUtil.info(logger, person, "注册成功");
			return ControlorUtil.createJSONObject(true, "注册成功").toString();
		} else {
			LogUtil.info(logger, person, "注册失败");
			return ControlorUtil.createJSONObject(false, "注册失败").toString();
		}
	}

	public static final String checkPerson(Person person, String pw) {
		if (person == null) {
			return "信息缺失";
		}
		if (person.getId() == null || person.getId().length() == 0) {
			return "写个学号好让我知道你是谁吧";
		}
		if (person.getName() == null || person.getName().length() == 0) {
			return "写个名字好让我知道你是谁吧";
		}
		if (person.getCollege() == null || person.getCollege().length() == 0) {
			return "填个学院呗";
		}
		if (person.getGrade() < 1 || person.getGrade() > 99) {
			return "年级呢";
		}
		if (person.getProfessionClass() == null || person.getProfessionClass().length() == 0) {
			return "专业班级是啥";
		}
		if (person.getPhone() == null || person.getPhone().length() == 0) {
			return "留个手机吧";
		}
		if (person.getQq() == null || person.getQq().length() == 0) {
			return "给个qq方便联系";
		}
		if (person.getBirthday() == null) {
			return "写一下生日呢";
		}
		if (person.getPassword() == null || person.getPassword().length() == 0 || pw == null || pw.length() == 0) {
			return "密码怎么可以为空";
		}
		if (!person.getPassword().equals(pw)) {
			return "两次密码不一样了呀";
		}
		return null;
	}
}
