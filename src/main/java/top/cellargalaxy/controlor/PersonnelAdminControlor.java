package top.cellargalaxy.controlor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.cellargalaxy.bean.controlorBean.ReturnBean;
import top.cellargalaxy.bean.daoBean.Authorized;
import top.cellargalaxy.bean.daoBean.Permission;
import top.cellargalaxy.bean.daoBean.Person;
import top.cellargalaxy.service.PersonnelService;
import top.cellargalaxy.util.ControlorUtil;
import top.cellargalaxy.util.LogUtil;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.LinkedList;

/**
 * Created by cellargalaxy on 17-12-13.
 */
@RestController
@RequestMapping(PersonnelAdminControlor.PERSONNEL_ADMIN_CONTROLOR_URL)
public class PersonnelAdminControlor {
	public static final String PERSONNEL_ADMIN_CONTROLOR_URL = "/admin";
	@Autowired
	private PersonnelService personnelService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@ResponseBody
	@GetMapping("/downloadPersonFile")
	public Resource downloadPersonFile(HttpServletResponse response, HttpSession session) {
		LogUtil.info(logger, session, "下载人员文件");
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment;filename=\"person.csv\"");
		File file = new File("src/main/resources/static/upload/person.csv");
		personnelService.writePersonFile(file);
		return new FileSystemResource(file);
	}
	
	@ResponseBody
	@PostMapping("/uploadPersonFile")
	public ReturnBean uploadPersonFile(@RequestParam("file") MultipartFile multipartFile, HttpSession session) {
		File file = ControlorUtil.saveFile(multipartFile);
		if (file == null) {
			LogUtil.info(logger, session, "文件上传失败");
			return new ReturnBean(false, "文件上传失败");
		} else {
			LinkedList<Person> persons = personnelService.addPersons(file);
			file.delete();
			if (persons == null) {
				LogUtil.info(logger, session, "文件解析失败");
				return new ReturnBean(false, "文件解析失败");
			}
			if (persons.size() == 0) {
				LogUtil.info(logger, session, "全部人员加成功");
				return new ReturnBean(false, "全部人员加成功");
			}
			LogUtil.info(logger, session, "添加失败人员");
			return new ReturnBean(true, persons);
		}
	}
	
	@ResponseBody
	@PostMapping("/addPerson")
	public ReturnBean addPerson(Person person, String pw, HttpSession session) {
		String string = RootControlor.checkPerson(person, pw);
		if (string != null) {
			return new ReturnBean(false, string);
		}
		if (personnelService.addPerson(person)) {
			LogUtil.info(logger, session, "添加人员成功 " + person);
			return new ReturnBean(true, "添加人员成功");
		} else {
			LogUtil.info(logger, session, "添加人员失败 " + person);
			return new ReturnBean(false, "添加人员失败");
		}
	}
	
	@ResponseBody
	@PostMapping("/removePerson")
	public ReturnBean removePerson(String id, HttpSession session) {
		if (personnelService.removePerson(id)) {
			LogUtil.info(logger, session, "删除人员成功 " + id);
			return new ReturnBean(true, "删除人员成功");
		} else {
			LogUtil.info(logger, session, "删除人员失败 " + id);
			return new ReturnBean(false, "删除人员失败");
		}
	}
	
	@ResponseBody
	@PostMapping("/changePerson")
	public ReturnBean changePerson(Person person, String pw, HttpSession session) {
		Person loginPerson = ControlorUtil.getLoginPerson(session);
		if (!loginPerson.getId().equals(person.getId()) && !personnelService.checkPersonnelAdmin(loginPerson)) {
			LogUtil.info(logger, loginPerson, "你没有管理员权限修改他人信息，请用管理员账号登录 " + person);
			return new ReturnBean(false, "你没有管理员权限修改他人信息，请用管理员账号登录");
		}
		String string = RootControlor.checkPerson(person, pw);
		if (string != null) {
			return new ReturnBean(false, string);
		}
		if (personnelService.changePerson(person)) {
			LogUtil.info(logger, loginPerson, "修改人员信息成功 " + person);
			return new ReturnBean(true, "修改人员信息成功");
		} else {
			LogUtil.info(logger, loginPerson, "修改人员信息失败 " + person);
			return new ReturnBean(false, "修改人员信息失败");
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	@ResponseBody
	@PostMapping("/addPermission")
	public ReturnBean addPermission(HttpSession session, Permission permission) {
		Person loginPerson = ControlorUtil.getLoginPerson(session);
		if (!personnelService.checkPersonnelRoot(loginPerson)) {
			return new ReturnBean(false, "你没有root权限，请用root账号登录");
		}
		if (personnelService.addPermission(permission)) {
			LogUtil.info(logger, loginPerson, "添加权限成功 " + permission);
			return new ReturnBean(true, "添加权限成功");
		} else {
			LogUtil.info(logger, loginPerson, "添加权限失败 " + permission);
			return new ReturnBean(false, "添加权限失败");
		}
	}
	
	@ResponseBody
	@PostMapping("/removePermission")
	public ReturnBean removePermission(HttpSession session, int permission) {
		Person loginPerson = ControlorUtil.getLoginPerson(session);
		if (!personnelService.checkPersonnelRoot(loginPerson)) {
			return new ReturnBean(false, "你没有root权限，请用root账号登录");
		}
		if (personnelService.removePermission(permission)) {
			LogUtil.info(logger, loginPerson, "删除权限成功 " + permission);
			return new ReturnBean(true, "删除权限成功");
		} else {
			LogUtil.info(logger, loginPerson, "删除权限失败 " + permission);
			return new ReturnBean(false, "删除权限失败");
		}
	}
	
	@ResponseBody
	@PostMapping("/changePermission")
	public ReturnBean changePermission(HttpSession session, Permission permission) {
		Person loginPerson = ControlorUtil.getLoginPerson(session);
		if (!personnelService.checkPersonnelRoot(loginPerson)) {
			return new ReturnBean(false, "你没有root权限，请用root账号登录");
		}
		if (personnelService.changePermission(permission)) {
			LogUtil.info(logger, loginPerson, "修改权限成功 " + permission);
			return new ReturnBean(true, "修改权限成功");
		} else {
			LogUtil.info(logger, loginPerson, "修改权限失败 " + permission);
			return new ReturnBean(false, "修改权限失败");
		}
	}
	/////////////////////////////////////////////////////////
	
	@ResponseBody
	@PostMapping("/addAuthorized")
	public ReturnBean addAuthorized(Authorized authorized, HttpSession session) {
		if (personnelService.addAuthorized(authorized)) {
			LogUtil.info(logger, session, "添加授权成功 " + authorized);
			return new ReturnBean(true, "添加授权成功");
		} else {
			LogUtil.info(logger, session, "添加授权失败 " + authorized);
			return new ReturnBean(false, "添加授权失败");
		}
	}
	
	@ResponseBody
	@PostMapping("/removeAuthorized")
	public ReturnBean removeAuthorized(Authorized authorized, HttpSession session) {
		if (personnelService.removeAuthorized(authorized)) {
			LogUtil.info(logger, session, "删除授权成功 " + authorized);
			return new ReturnBean(true, "删除授权成功");
		} else {
			LogUtil.info(logger, session, "删除授权失败 " + authorized);
			return new ReturnBean(false, "删除授权失败");
		}
	}
}
