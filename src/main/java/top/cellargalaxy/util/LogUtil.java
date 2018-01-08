package top.cellargalaxy.util;

import org.slf4j.Logger;
import top.cellargalaxy.bean.daoBean.Person;

import javax.servlet.http.HttpSession;

/**
 * Created by cellargalaxy on 18-1-2.
 */
public class LogUtil {
	public static final void info(Logger logger, HttpSession session, String string) {
		Person person = ControlorUtil.getLoginPerson(session);
		info(logger, person, string);
	}
	
	public static final void info(Logger logger, Person person, String string) {
		try {
			logger.info(person.getId() + "(" + person.getName() + ") " + string);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
