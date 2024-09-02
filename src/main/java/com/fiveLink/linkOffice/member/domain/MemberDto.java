package com.fiveLink.linkOffice.member.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class MemberDto {

	private Long member_no;
	private String member_number;
	private String member_pw;
	private String member_name;
	private String member_national;
	private String member_internal;
	private String member_mobile;
	private Long department_no;
	private Long position_no;
	private String department_name;
	private String position_name;
	private String member_address;
	private String member_hire_date;
	private LocalDateTime member_end_date;
	private LocalDateTime member_create_date;
	private LocalDateTime member_update_date;
	private String member_ori_profile_img;
	private String member_new_profile_img;
	private String member_ori_digital_img;
	private String member_new_digital_img;
	private Long member_status;
	private Long member_additional;
	
	private List<GrantedAuthority> authorities;
	
	public Member toEntity() {
		return Member.builder()
					.memberNo(member_no)
					.memberNumber(member_number)
					.memberPw(member_pw)
					.memberName(member_name)
					.memberNational(member_national)
					.memberInternal(member_internal)
					.memberMobile(member_mobile)
					.departmentNo(department_no)
					.positionNo(position_no)
					.memberAddress(member_address)
					.memberHireDate(member_hire_date)
					.memberEndDate(member_end_date)
					.memberCreateDate(member_create_date)
					.memberUpdateDate(member_update_date)
					.memberOriProfileImg(member_ori_profile_img)
					.memberNewProfileImg(member_new_profile_img)
					.memberOriDigitalImg(member_ori_digital_img)
					.memberNewDigitalImg(member_new_digital_img)
					.memberStatus(member_status)
					.memberAdditional(member_additional)
					.build();
	}
	
	public MemberDto toDto(Member member) {
		return MemberDto.builder()
					.member_no(member.getMemberNo())
					.member_number(member.getMemberNumber())
					.member_pw(member.getMemberPw())
					.member_name(member.getMemberName())
					.member_national(member.getMemberNational())
					.member_internal(member.getMemberInternal())
					.member_mobile(member.getMemberMobile())
					.department_no(member.getDepartmentNo())
					.position_no(member.getPositionNo())
					.member_address(member.getMemberAddress())
					.member_hire_date(member.getMemberHireDate())
					.member_end_date(member.getMemberEndDate())
					.member_create_date(member.getMemberCreateDate())
					.member_update_date(member.getMemberUpdateDate())
					.member_ori_profile_img(member.getMemberOriProfileImg())
					.member_new_profile_img(member.getMemberNewProfileImg())
					.member_ori_digital_img(member.getMemberOriDigitalImg())
					.member_new_digital_img(member.getMemberNewDigitalImg())
					.member_status(member.getMemberStatus())
					.member_additional(member.getMemberAdditional())
					.build();
	}
}
