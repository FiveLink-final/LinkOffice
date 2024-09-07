package com.fiveLink.linkOffice.member.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.organization.domain.DepartmentDto;
import com.fiveLink.linkOffice.organization.domain.PositionDto;
import com.fiveLink.linkOffice.organization.service.DepartmentService;
import com.fiveLink.linkOffice.organization.service.PositionService;

@Controller
public class MemberViewController {
	
	private final MemberService memberService;
	private final DepartmentService departmentService;
	private final PositionService positionService;
	
	@Autowired
	public MemberViewController(MemberService memberService, DepartmentService departmentService, PositionService positionService) {
		this.memberService = memberService;
		this.departmentService = departmentService;
		this.positionService = positionService;
	}
	
	// 내정보 페이지
	@GetMapping("/employee/member/mypage/{member_no}")
	public String myPage(@PathVariable("member_no") Long memberNo, Model model) {
		List<MemberDto> memberdto = memberService.getMembersByNo(memberNo); 
	    model.addAttribute("memberdto", memberdto);
	    return "employee/member/mypage";
	}
	// 정보 수정 페이지
	@GetMapping("/employee/member/myedit/{member_no}")
	public String myedit(@PathVariable("member_no") Long memberNo, Model model) {
		List<MemberDto> memberdto = memberService.getMembersByNo(memberNo); 
	    model.addAttribute("memberdto", memberdto);
		return "employee/member/myedit";
	}
	
	// 정보 수정 페이지
	@GetMapping("/employee/member/digitalname/{member_no}")
	public String digitalname(@PathVariable("member_no") Long memberNo, Model model) {
	    List<MemberDto> memberDtoList = memberService.getMembersByNo(memberNo);
	    model.addAttribute("memberdto", memberDtoList);
	    return "employee/member/digitalname";
	}

	// 관리자 사원 등록 페이지
	@GetMapping("/admin/member/create")
	public String create(Model model) {
		// 로그인한 사원의 정보
	      Long memberNo = memberService.getLoggedInMemberNo();
	      // 번호
	      List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);
	      // 부서명 조회 
	      List<DepartmentDto> departments = departmentService.getAllDepartments();
	      // 직위명 조회 
	      List<PositionDto> positions = positionService.getAllPositionsForSelect();
	        
	        
	      model.addAttribute("memberdto", memberdto);
	      model.addAttribute("departments", departments);
	      model.addAttribute("positions", positions);
		return "admin/member/create";
	}
	//[전주영] 사원 목록 조회 
	@GetMapping("/admin/member/list")
	public String list(Model model) {
		Long memberNo = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);
	    List<DepartmentDto> departments = departmentService.getAllDepartments();
	    
	    List<MemberDto> memberList = memberService.getAllMembers();
		model.addAttribute("memberdto", memberdto);
		model.addAttribute("departments", departments);
		model.addAttribute("memberList", memberList);
		
		return "admin/member/list";
	}
	
	// [전주영] 관리자 사원 상세 조회 
	@GetMapping("/admin/member/detail/{member_no}")
	public String detail(@PathVariable("member_no") Long memberNo, Model model) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
		List<MemberDto> memberDtoList = memberService.getMembersByNo(memberNo);
	    
		  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	        // 날짜를 문자열로 변환
	        memberDtoList.forEach(member -> {
	            if (member.getMember_end_date() != null) {
	                String formattedEndDate = member.getMember_end_date().format(formatter);
	                member.setFormat_end_date(formattedEndDate);
	            }
	        });
		
		model.addAttribute("memberdto", memberdto);
	    model.addAttribute("memberDtoList", memberDtoList);
	    
	    return "admin/member/detail";
	}
	
	// [전주영] 관리자 사원 수정
	@GetMapping("/admin/member/edit/{member_no}")
	public String edit(@PathVariable("member_no") Long memberNo,Model model) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
		List<MemberDto> memberDtoList = memberService.getMembersByNo(memberNo);
		
	    List<DepartmentDto> departments = departmentService.getAllDepartments();
	    List<PositionDto> positions = positionService.getAllPositionsForSelect();
		
		model.addAttribute("memberdto", memberdto);
	    model.addAttribute("memberDtoList", memberDtoList);
	    model.addAttribute("departments", departments);
	    model.addAttribute("positions", positions);
	    
	    return "admin/member/edit";
	}
	
	// [전주영] 사용자 주소록
	@GetMapping("/employee/member/list")
	public String memberList(Model model) {
		Long memberNo = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);
	    List<DepartmentDto> departments = departmentService.getAllDepartments();
	    
	    List<MemberDto> memberList = memberService.getAllMembers();
		model.addAttribute("memberdto", memberdto);
		model.addAttribute("departments", departments);
		model.addAttribute("memberList", memberList);
		
		return "employee/member/list";
	}
	
}

