package in.co.sunrays.proj4.controller;

import in.co.sunrays.proj4.bean.BaseBean;
import in.co.sunrays.proj4.bean.UserBean;
import in.co.sunrays.proj4.exception.ApplicationException;
import in.co.sunrays.proj4.exception.RecordNotFoundException;
import in.co.sunrays.proj4.model.UserModel;
import in.co.sunrays.proj4.util.DataUtility;
import in.co.sunrays.proj4.util.DataValidator;
import in.co.sunrays.proj4.util.PropertyReader;
import in.co.sunrays.proj4.util.ServletUtility;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Forget Password functionality Controller. Performs operation for Forget
 * Password
 * 
 * @author SunilOS
 * @version 1.0
 * @Copyright (c) SunilOS
 */
@WebServlet(name = "ForgetPasswordCtl", urlPatterns = { "/ForgetPasswordCtl" })
public class ForgetPasswordCtl extends BaseCtl {

	private static Logger log = Logger.getLogger(ForgetPasswordCtl.class);

	@Override
	protected boolean validate(HttpServletRequest request) {

		log.debug("ForgetPasswordCtl Method validate Started");

		boolean pass = true;

		String login = request.getParameter("login");

		if (DataValidator.isNull(login)) {
			request.setAttribute("login", PropertyReader.getValue("error.require", "Email Id "));
			pass = false;
		} else if (!DataValidator.isEmail(login)) {
			request.setAttribute("login", PropertyReader.getValue("error.email", "Login "));
			pass = false;
		}
		log.debug("ForgetPasswordCtl Method validate Ended");

		return pass;
	}

	@Override
	protected BaseBean populateBean(HttpServletRequest request) {

		log.debug("ForgetPasswordCtl Method populatebean Started");

		UserBean bean = new UserBean();

		bean.setLogin(DataUtility.getString(request.getParameter("login")));

		log.debug("ForgetPasswordCtl Method populatebean Ended");

		return bean;
	}

	/**
	 * DIsplay Concept are there
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.debug("ForgetPasswordCtl Method doGet Started");

		ServletUtility.forward(getView(), request, response);

	}

	/**
	 * Submit Concepts
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.debug("ForgetPasswordCtl Method doPost Started");

		String op = DataUtility.getString(request.getParameter("operation"));

		UserBean bean = (UserBean) populateBean(request);

		// get model
		UserModel model = new UserModel();

		if (OP_GO.equalsIgnoreCase(op)) {

			try {
				model.forgetPassword(bean.getLogin());
				ServletUtility.setSuccessMessage("Password has been sent to your email id.", request);
			} catch (RecordNotFoundException e) {
				ServletUtility.setErrorMessage(e.getMessage(), request);
			
				log.error(e);
			} catch (ApplicationException e) {
				log.error(e);
				ServletUtility.handleException(e, request, response);
				return;
			} catch (MessagingException e) {
				log.error(e);
ServletUtility.setErrorMessage("Please Check Your Internet Connection !", request);

				//ServletUtility.handleException(e, request, response);

				
			}
			ServletUtility.forward(getView(), request, response);
		}

		log.debug("ForgetPasswordCtl Method doPost Ended");
	}

	@Override
	protected String getView() {
		return ORSView.FORGET_PASSWORD_VIEW;
	}

}