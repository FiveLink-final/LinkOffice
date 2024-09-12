package com.fiveLink.linkOffice.document.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.document.domain.DocumentFolder;
import com.fiveLink.linkOffice.document.service.DocumentFileService;
import com.fiveLink.linkOffice.document.service.DocumentFolderService;
import com.fiveLink.linkOffice.member.service.MemberService;

@Controller
public class DocumentApiController {
	
	private final DocumentFolderService documentFolderService;
	private final MemberService memberService;
	private final DocumentFileService documentFileService;
	
	private static final Logger LOGGER
		= LoggerFactory.getLogger(DocumentApiController.class);
	
	@Autowired
	public DocumentApiController(DocumentFolderService documentFolderService,
			MemberService memberService,
			DocumentFileService documentFileService) {
		this.documentFolderService = documentFolderService;
		this.memberService = memberService;
		this.documentFileService = documentFileService;
	}
	
   // 개인 첫 폴더 
   @PostMapping("/personal/first/folder")
   @ResponseBody
   public Map<String, String> personalFirstFolder(@RequestBody Map<String, Object> payload){
	  Map<String, String> resultMap = new HashMap<>();
	  resultMap.put("res_code", "404");
	  resultMap.put("res_msg", "경로 오류");
	
	  String folderName = (String) payload.get("folderName");
	  String memberNoStr = (String) payload.get("memberNo");
	  String deptNoStr = (String) payload.get("deptNo");
    
	  Long memberNo = Long.valueOf(memberNoStr);
	  Long deptNo = Long.valueOf(deptNoStr);
      Long folderLevel = 1L;
      Long docBoxType = 0L;
      Long folderStatus = 0L;

      DocumentFolder documentFolder = DocumentFolder.builder()
            .documentFolderName(folderName)
            .documentFolderLevel(folderLevel)
            .departmentNo(deptNo)
            .documentBoxType(docBoxType)
            .memberNo(memberNo)
            .documentFolderStatus(folderStatus)
            .build();

      int result = documentFolderService.personalFirstFolder(documentFolder);
      System.out.println("Service result: " + result);

      if (result > 0) {
    	  resultMap.put("res_code", "200");
    	  resultMap.put("res_msg", "폴더 생성이 완료되었습니다.");
      } 
      return resultMap;
   }	
}