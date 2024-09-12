package com.fiveLink.linkOffice.vacationapproval.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.vacation.domain.VacationTypeDto;
import com.fiveLink.linkOffice.vacation.service.VacationService;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalDto;
import com.fiveLink.linkOffice.vacationapproval.service.VacationApprovalService;

@Controller
public class VacationApprovalViewController {
	
	private final MemberService memberService;
    private final VacationService vacationService;
    private final VacationApprovalService vacationApprovalService;
	
    @Autowired
    public VacationApprovalViewController(MemberService memberService, VacationService vacationService, VacationApprovalService vacationApprovalService) {
        this.memberService = memberService;
        this.vacationService = vacationService;
        this.vacationApprovalService = vacationApprovalService;
    }
    
 // 사용자 휴가 결재 등록 페이지
 	@GetMapping("/employee/vacationapproval/create/{member_no}")
 	public String employeeVacationApprovalCreate(Model model, @PathVariable("member_no") Long memberNo) {
 		List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);
 		List<VacationTypeDto> vacationTypeList = vacationService.selectVacationTypeList();
 		
 		model.addAttribute("memberdto", memberdto);
 		model.addAttribute("vacationTypeList", vacationTypeList);
 		
 		return "employee/vacationapproval/vacationapproval_create";
 	}
 	
 	private Sort getSortOption(String sort) {
		if ("latest".equals(sort)) {
			return Sort.by(Sort.Order.desc("vacationApprovalCreateDate")); 
		} else if ("oldest".equals(sort)) {
			return Sort.by(Sort.Order.asc("vacationApprovalCreateDate")); 
		}
		return Sort.by(Sort.Order.desc("vacationApprovalCreateDate")); 
	}
 	
 	//  사용자 휴가 결재함 페이지
 	@GetMapping("/employee/vacationapproval/list/{member_no}")
 	public String employeevacationapprovalList(Model model, @PathVariable("member_no") Long memberNo, VacationApprovalDto searchdto, @PageableDefault(size = 10, sort = "positionLevel", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(value = "sort", defaultValue = "latest") String sort) {
 		List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);
 		
 		Sort sortOption = getSortOption(sort);
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);
 		
 		Page<VacationApprovalDto> vacationapprovalList = vacationApprovalService.getVacationApprovalByNo(memberNo,searchdto,sortedPageable);
 		System.out.println("controller단"+vacationapprovalList.getContent());
 		
 		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
 		vacationapprovalList.forEach(vapp -> {
 			if(vapp.getVacation_approval_create_date() != null) {
 				String fomattedCreateDate = vapp.getVacation_approval_create_date().format(formatter);
 				vapp.setFormat_vacation_approval_create_date(fomattedCreateDate);
 			}
 		});
 		
 		model.addAttribute("memberdto", memberdto);
 		model.addAttribute("vacationapprovalList", vacationapprovalList.getContent());
		model.addAttribute("page", vacationapprovalList);
		model.addAttribute("searchDto", searchdto);
		model.addAttribute("currentSort", sort);
 		
 		return "employee/vacationapproval/vacationapproval_list";
 		
 	}
}