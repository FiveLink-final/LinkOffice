package com.fiveLink.linkOffice.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberViewController {
	
	@GetMapping("/member/mypage")
	public String myPage() {
		return "member/mypage";
	}
}