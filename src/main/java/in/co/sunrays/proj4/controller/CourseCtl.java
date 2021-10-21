package in.co.sunrays.proj4.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import in.co.sunrays.proj4.bean.BaseBean;
import in.co.sunrays.proj4.bean.CourseBean;
import in.co.sunrays.proj4.exception.ApplicationException;
import in.co.sunrays.proj4.exception.DuplicateRecordException;
import in.co.sunrays.proj4.model.CourseModel;
import in.co.sunrays.proj4.util.DataUtility;
import in.co.sunrays.proj4.util.DataValidator;
import in.co.sunrays.proj4.util.PropertyReader;
import in.co.sunrays.proj4.util.ServletUtility;

/**
 * Course functionality Controller. Performs operation for add, update and get
 * Course
 * 
 * @author SunilOS
 * @version 1.0
 * @Copyright (c) SunilOS
 */
@WebServlet(name="CourseCtl",urlPatterns={"/ctl/CourseCtl"})
public class CourseCtl extends BaseCtl {
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.getLogger(CourseCtl.class);
      
   
    @Override
    protected boolean validate(HttpServletRequest request) {
    	log.debug("courseCtl Method validate Started");
    	
	        boolean pass = true;
	        if (DataValidator.isNull(request.getParameter("name"))) {
	            request.setAttribute("name",
	                    PropertyReader.getValue("error.require", "CourseName "));
	            pass = false;
	        }

	        if (DataValidator.isNull(request.getParameter("description"))) {
	            request.setAttribute("description",
	                    PropertyReader.getValue("error.require", "Description "));
	            pass = false;
	        }
	        if(DataValidator.isNull(request.getParameter("duration"))){
	        request.setAttribute("duration", PropertyReader.getValue("error.require","Duration "));	
	        pass=false;
	        }

	        log.debug("courseCtl Method validate Ended");
	       
	        
    	return pass;
    }
    
    
    @Override
    protected BaseBean populateBean(HttpServletRequest request) {
    	CourseBean bean=new CourseBean();
    	bean.setId(DataUtility.getLong(request.getParameter("id")));
    	bean.setName(DataUtility.getString(request.getParameter("name")));
    	bean.setDuration(DataUtility.getString(request.getParameter("duration")));
    	bean.setDescription(DataUtility.getString(request.getParameter("description")));
    	populateDTO(bean, request);
    	return bean;
    }
    /**
     * contains display logics
     * 
     */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 log.debug("courseCtl Method doGet Started");
	   

        String op = DataUtility.getString(request.getParameter("operation"));
        

        // get model
        CourseModel model = new CourseModel();

        long id = DataUtility.getLong(request.getParameter("id"));
        if (id > 0 || op != null) {
            CourseBean bean;
            try {
                bean = model.findByPK(id);
                ServletUtility.setBean(bean, request);
            } catch (ApplicationException e) {
               log.error(e);
                ServletUtility.handleException(e, request, response);
                return;
            }
        }
        ServletUtility.forward(getView(), request, response);
         log.debug("courseCtl Method doGetEnded");
      
		
	}
	/**
	 * contains submit logics
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 log.debug("CourseCtl Method doPost Started");

   //     System.out.println("CourseCtl Method doPost Started");
        String op = DataUtility.getString(request.getParameter("operation"));
        CourseModel model=new CourseModel();
        
        long id=DataUtility.getLong(request.getParameter("id"));
        if (OP_SAVE.equalsIgnoreCase(op)||OP_UPDATE.equalsIgnoreCase(op)) {

            CourseBean bean = (CourseBean) populateBean(request);

            try {
                if (id > 0) {
                    model.update(bean);
                    ServletUtility.setBean(bean, request);
                    ServletUtility.setSuccessMessage("Data is successfully updated",
                            request);
                } else {
                    long pk = model.add(bean);
                    //bean.setId(pk);
                    ServletUtility.setSuccessMessage("Data is successfully saved",
                            request);
                }

               // ServletUtility.setBean(bean, request);
                

            } catch (ApplicationException e) {
                log.error(e);
                ServletUtility.handleException(e, request, response);
                return;
            } catch (DuplicateRecordException e) {
                ServletUtility.setBean(bean, request);
                ServletUtility.setErrorMessage("Course already exists", request);
            }

        } else if (OP_DELETE.equalsIgnoreCase(op)) {

            CourseBean bean = (CourseBean) populateBean(request);
            try {
                model.delete(bean);
                ServletUtility.redirect(ORSView.COURSE_LIST_CTL, request,
                        response);
                return;
            } catch (ApplicationException e) {
                log.error(e);
                ServletUtility.handleException(e, request, response);
                return;
            }

        } else if (OP_CANCEL.equalsIgnoreCase(op)) {

            ServletUtility.redirect(ORSView.COURSE_LIST_CTL, request, response);
            return;

        }else if (OP_RESET.equalsIgnoreCase(op)) {
        	ServletUtility.redirect(ORSView.COURSE_CTL, request, response);
            return;
		}

        ServletUtility.forward(getView(), request, response);

        log.debug("CourseCtl Method doPOst Ended");
        

		
		
	}

	
	@Override
	protected String getView() {
		
		return ORSView.COURSE_VIEW;
	}

}
