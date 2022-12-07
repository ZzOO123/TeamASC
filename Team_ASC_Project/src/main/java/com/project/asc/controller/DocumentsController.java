package com.project.asc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.project.asc.dao.DocumentsDAO;
import com.project.asc.service.DocumentsService;
import com.project.asc.vo.DocumentsVO;
import com.project.asc.vo.ProjectVO;
import com.project.asc.vo.UserVO;

@ResponseBody
@Controller("documentsController")
@RequestMapping("/documents")
public class DocumentsController {
	@Autowired
	private DocumentsService documentsService;
	
	/* test */
	@RequestMapping(value="/test", method=RequestMethod.POST)
	public ModelAndView test(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		System.out.println("test");
		
		mav.setViewName("redirect:/documents/readDocuments");
		return mav;
	}
	
	/* 문서관리 목록 */
	@RequestMapping(value="/manageDocuments", method={RequestMethod.POST, RequestMethod.GET})
	public ModelAndView readDocuments(HttpServletRequest request, HttpServletResponse response){
		ModelAndView mav = new ModelAndView();
		
		//session에서 projectSeq값 받아오기
		ProjectVO project = (ProjectVO) request.getSession().getAttribute("project");
		String projectSeq = String.valueOf(project.getProjectSeq());
		
		//readDocuments
		ArrayList<DocumentsVO> list = documentsService.readDocuments(projectSeq);
		
		mav.addObject("list",list);
		
		mav.setViewName("/documents/manageDocuments");
		return mav;
	}
	
	/* 문서관리 셀 생성 */
	@RequestMapping(value="/createDocument", method={RequestMethod.POST, RequestMethod.GET})
	public ModelAndView createDocument(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		
		//session에서 projectSeq값 받아오기
		ProjectVO project = (ProjectVO) request.getSession().getAttribute("project");
		String projectSeq = String.valueOf(project.getProjectSeq());
		
		boolean flag = documentsService.insertDocument(projectSeq);
		if(flag) {
			System.out.println("insert done.");
		} else {
			System.out.println("insert fail");
		}
		
		mav.setViewName("redirect:/documents/manageDocuments");
		return mav;
	} 
	
	/* 문서 수정 페이지 */ 
	@RequestMapping(value="/viewUpdateDocument",method=RequestMethod.GET)
	public ModelAndView viewUpdateDocument(@RequestParam("documentsSeq") String documentsSeq, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		DocumentsVO documents = documentsService.selectOneDocument(documentsSeq);
		
		mav.addObject("documents",documents);
		System.out.println("view documents : "+documents);
		mav.setViewName("/documents/viewUpdateDocument");
		return mav;
	}	
	
	/* 문서 수정 */
	@RequestMapping(value="/updateDocument", method= {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView updateDocument(@RequestParam("documentsSeq") String documentsSeq,
			@RequestParam("documentsName") String documentsName,
			@RequestParam("uploadFile") MultipartFile uploadFile,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("up : "+uploadFile);
		System.out.println("name : "+documentsName);
		
		ModelAndView mav = new ModelAndView();
		// session에서 유저 이름 가져오기
		UserVO member = (UserVO) request.getSession().getAttribute("member");
		String name = member.getName();
		
		DocumentsVO vo = new DocumentsVO();
		
		//파일 업로드 처리
		String oldFile = request.getParameter("fileName");
		String oldOriginalFileName = request.getParameter("realFileName");
		String fileName = null;
		String originalFileName = "";
//		MultipartFile uploadFile = vo.getUploadFile();
		
		//이전 파일 덮어쓰기
		if(oldFile == null) {
			if(!uploadFile.isEmpty()) {
				originalFileName = uploadFile.getOriginalFilename();
				//확장자 구하기
				String ext = FilenameUtils.getExtension(originalFileName);
				//UUID 구하기
				UUID uuid = UUID.randomUUID();
				
				fileName = uuid + "." + ext;
//				uploadFile.transferTo(new File("C:\\upload\\" + fileName));
				uploadFile.transferTo(new File("/var/lib/tomcat9/webapps/ROOT/dev/downloads/" + fileName));
			} 
			vo.setFileName(fileName != null ? fileName : "");
			vo.setRealFileName(fileName != null ? originalFileName : "");
		} else {
			if(!uploadFile.isEmpty()) {
				originalFileName = uploadFile.getOriginalFilename();
				//확장자 구하기
				String ext = FilenameUtils.getExtension(originalFileName);
				//UUID 구하기
				UUID uuid = UUID.randomUUID();
				fileName = uuid + "." + ext;
//				uploadFile.transferTo(new File("C:\\upload\\" + fileName));
				uploadFile.transferTo(new File("/var/lib/tomcat9/webapps/ROOT/dev/downloads/" + fileName));
				
				
				vo.setFileName(fileName == oldFile ? oldFile : fileName);
				vo.setRealFileName(fileName == oldFile ? oldOriginalFileName : originalFileName);
			} 
		}
		
		
		// 글 업데이트
		vo.setDocumentsSeq(Integer.parseInt(documentsSeq));
		vo.setDocumentsName(documentsName);
		vo.setWriter(name);
		
		documentsService.updateDocument(vo);
		
		mav.addObject("vo",vo);
		mav.setViewName("/documents/updateDocument");
		return mav;
	}

	
	
	
	/* 첨부파일 다운로드 */
	@RequestMapping(value = "/downloadFile")
	public void downloadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String fileName = request.getParameter("fileName");
	    String originalFileName= request.getParameter("realFileName");
	    String realFilename="";
	    System.out.println("down fileName"+fileName);

	    try {
	        String browser = request.getHeader("User-Agent");
	        //파일 인코딩
	        if (browser.contains("MSIE") || browser.contains("Trident")
	                || browser.contains("Chrome")) {
//	            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+",
//	                    "%20");
//	            originalFileName = URLEncoder.encode(originalFileName, "UTF-8").replaceAll("\\+",
//	                    "%20");
	        	fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("/+",
	                    "%20");
	            originalFileName = URLEncoder.encode(originalFileName, "UTF-8").replaceAll("/+",
	                    "%20");
	        } else {
	            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
	            originalFileName = new String(originalFileName.getBytes("UTF-8"), "ISO-8859-1");
	        }
	    } catch (UnsupportedEncodingException ex) {
	        System.out.println("UnsupportedEncodingException");
	    }
//	    realFilename = "C:\\upload\\" + fileName;
	    realFilename = "/var/lib/tomcat9/webapps/ROOT/dev/downloads/" + fileName;
	    System.out.println(realFilename);
	    File file1 = new File(realFilename);
	    if (!file1.exists()) {
	        return ;
	    }
	    // 파일명 지정
	    response.setContentType("application/octer-stream");
	    response.setHeader("Content-Transfer-Encoding", "binary;");
	    response.setHeader("Content-Disposition", "attachment; filename=" + originalFileName + ";");
	    try {
	        OutputStream os = response.getOutputStream();
	        FileInputStream fis = new FileInputStream(realFilename);

	        int ncount = 0;
	        byte[] bytes = new byte[1024];

	        while ((ncount = fis.read(bytes)) != -1 ) {
	            os.write(bytes, 0, ncount);
	        }
	        fis.close();
	        os.close();
	    } catch (Exception e) {
	        System.out.println("FileNotFoundException : " + e);
	    }
	}

	
	/* 문서 셀 삭제 */
	@RequestMapping(value="/deleteDocument", method= {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView deleteDocument(@RequestParam("documentsSeq") String documentsSeq,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		
		boolean flag = documentsService.deleteDocument(documentsSeq);
		if(flag) {
			System.out.println("delete done.");
		} else {
			System.out.println("delete fail.");
		}
		
		mav.setViewName("redirect:/documents/manageDocuments");
		return mav;
	}
	
	
	/* 기획안 조회 */ 
//	@RequestMapping(value="/readPlan",method={RequestMethod.POST, RequestMethod.GET})
//	public ModelAndView readPlan(@RequestParam("projectSeq") String projectSeq, HttpServletRequest request, HttpServletResponse response) throws Exception  {
//		ModelAndView mav = new ModelAndView();
//		DocumentsVO documents = documentsService.readPlan(projectSeq);
//	
//		mav.addObject("documents",documents);
//
//		mav.setViewName("/documents/readPlan");
//		return mav;
//	}	
//	
	/* 기획안 수정 페이지 */ 
//	@RequestMapping(value="/viewUpdatePlan",method=RequestMethod.POST)
//	public ModelAndView viewUpdatePlan(@RequestParam("documentsSeq") String documentsSeq, HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ModelAndView mav = new ModelAndView();
//		DocumentsVO documents = documentsService.viewUpdatePlan(documentsSeq);
//		
//		mav.addObject("documents",documents);
//		mav.setViewName("/documents/viewUpdatePlan");
//		return mav;
//	}	
	
	/* 기획안 수정 */ 
//	@RequestMapping(value="/updatePlan",method= {RequestMethod.POST, RequestMethod.GET})
//	public ModelAndView updatePlan(@ModelAttribute("documents") DocumentsVO documents, @RequestParam("projectSeq") String projectSeq, 
//			@RequestParam("pageNum") String pageNum, HttpServletRequest request, HttpServletResponse response) throws Exception  {
//		ModelAndView mav = new ModelAndView();
//		boolean flag = documentsService.updatePlan(documents);
//		if(flag) {
//			System.out.println("update done.");
//		}
//		
//		mav.setViewName("redirect:/documents/readPlan?projectSeq="+projectSeq+"&pageNum="+pageNum);
//		return mav;
//	}
	
	/* 기획안 삭제 */ 
//	@RequestMapping(value="/deletePlan",method= {RequestMethod.POST, RequestMethod.GET})
//	public ModelAndView deletePlan(@ModelAttribute("documents") DocumentsVO documents,@RequestParam("projectSeq") String projectSeq,  
//			@RequestParam("pageNum") String pageNum, HttpServletRequest request, HttpServletResponse response) throws Exception  {
//		ModelAndView mav = new ModelAndView();
//		boolean flag = documentsService.deletePlan(documents);
//		if(flag) {
//			System.out.println("delete done.");
//		}
//
//		mav.setViewName("redirect:/documents/readPlan?projectSeq="+projectSeq+"&pageNum="+pageNum);
//		return mav;
//	}	
	
	/* 기획안 예문 조회 */ 
//	@Requ/*estMapping(value="examplePlan",method=RequestMethod.GET)
//	public ModelAndView examPlan(HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView mav = new ModelAndView();
//		
//		mav.setViewName("/documents/examplePlan");
//		return mav;
//	}*/
}
