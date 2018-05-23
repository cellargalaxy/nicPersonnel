package top.cellargalaxy.controlor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.cellargalaxy.bean.controlorBean.ReturnBean;
import top.cellargalaxy.bean.personnel.Authorized;
import top.cellargalaxy.bean.personnel.Permission;
import top.cellargalaxy.bean.personnel.Person;
import top.cellargalaxy.service.PersonnelService;
import top.cellargalaxy.util.ControlorUtil;

/**
 * Created by cellargalaxy on 17-12-8.
 */
@RestController
@RequestMapping(PersonnelApiControlor.PERSONNEL_API_CONTROLOR_URL)
public class PersonnelApiControlor {
	public static final String PERSONNEL_API_CONTROLOR_URL = "/api";
	@Autowired
	private PersonnelService personnelService;

	@ResponseBody
	@GetMapping("/inquirePersonById")
	public ReturnBean inquirePersonById(String id) {
		Person person = personnelService.findPersonById(id);
		if (person != null) {
			person.setPassword(null);
			return new ReturnBean(true, person);
		} else {
			return new ReturnBean(false, person);
		}
	}

	@ResponseBody
	@GetMapping("/inquirePersonPassword")
	public ReturnBean inquirePersonPassword(Person person) {
		ReturnBean returnBean;
		person = personnelService.checkPassword(person);
		if (person != null) {
			returnBean = new ReturnBean(true, person);
		} else {
			returnBean = new ReturnBean(false, "账号或密码错误");
		}
		return returnBean;
	}

	@ResponseBody
	@GetMapping("/inquirePersons")
	public ReturnBean inquirePersons(int page) {
		Person[] persons = personnelService.findPersons(page);
		return new ReturnBean(true, persons);
	}

	@ResponseBody
	@GetMapping("/inquirePersonPageCount")
	public ReturnBean inquirePersonPageCount() {
		return new ReturnBean(true, personnelService.getPersonPageCount());
	}
	////////////////////////////////////////////////////////////////////////////////////

	@ResponseBody
	@GetMapping("/inquirePermission")
	public ReturnBean inquirePermission(int permission) {
		Permission p = personnelService.findPermission(permission);
		if (p != null) {
			return new ReturnBean(true, p);
		} else {
			return new ReturnBean(false, p);
		}
	}

	@ResponseBody
	@GetMapping("/inquireAllPermission")
	public ReturnBean inquireAllPermission() {
		return new ReturnBean(true, personnelService.findAllPermission());
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////

	@ResponseBody
	@GetMapping("/inquireExistAuthorized")
	public String inquireExistAuthorized(Authorized authorized) {
		if (personnelService.findExistAuthorized(authorized)) {
			return ControlorUtil.createJSONObject(true, "有效授权").toString();
		} else {
			return ControlorUtil.createJSONObject(false, "无效授权").toString();
		}
	}

	@ResponseBody
	@GetMapping("/inquireAuthorizedById")
	public ReturnBean inquireAuthorizedById(String id) {
		return new ReturnBean(true, personnelService.findAuthorizedById(id));
	}

	@ResponseBody
	@GetMapping("/inquireAuthorizedByPermission")
	public ReturnBean inquireAuthorizedByPermission(int permission) {
		return new ReturnBean(true, personnelService.findAuthorizedByPermission(permission));
	}

	@ResponseBody
	@GetMapping("/inquireAllAuthorized")
	public ReturnBean inquireAllAuthorized() {
		return new ReturnBean(true, personnelService.findAllAuthorized());
	}
}
