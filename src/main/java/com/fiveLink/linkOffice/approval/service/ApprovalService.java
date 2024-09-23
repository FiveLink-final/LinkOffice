package com.fiveLink.linkOffice.approval.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.approval.domain.Approval;
import com.fiveLink.linkOffice.approval.domain.ApprovalDto;
import com.fiveLink.linkOffice.approval.domain.ApprovalFile;
import com.fiveLink.linkOffice.approval.domain.ApprovalFileDto;
import com.fiveLink.linkOffice.approval.domain.ApprovalFlow;
import com.fiveLink.linkOffice.approval.domain.ApprovalFlowDto;
import com.fiveLink.linkOffice.approval.repository.ApprovalFileRepository;
import com.fiveLink.linkOffice.approval.repository.ApprovalFlowRepository;
import com.fiveLink.linkOffice.approval.repository.ApprovalRepository;
import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApproval;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFlow;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFlowDto;
import com.fiveLink.linkOffice.vacationapproval.repository.VacationApprovalRepository;

import jakarta.transaction.Transactional;

@Service
public class ApprovalService {

	private final MemberRepository memberRepository;
	private final ApprovalRepository approvalRepository;
	private final ApprovalFlowRepository approvalFlowRepository;
	private final ApprovalFileRepository approvalFileRepository;
	private final VacationApprovalRepository vacationApprovalRepository;
	@Autowired
	public ApprovalService(MemberRepository memberRepository, ApprovalRepository approvalRepository, ApprovalFlowRepository approvalFlowRepository, ApprovalFileRepository approvalFileRepository, VacationApprovalRepository vacationApprovalRepository) {
        this.memberRepository = memberRepository;
        this.approvalRepository = approvalRepository;
        this.approvalFlowRepository = approvalFlowRepository;
        this.approvalFileRepository = approvalFileRepository;
        this.vacationApprovalRepository = vacationApprovalRepository;
    }
	
	// 사용자 결재 신청 (파일 O)
	public Approval createApprovalFile(ApprovalDto appdto, ApprovalFileDto filedto, List<ApprovalFlowDto> flowdto) {
		Member member = memberRepository.findByMemberNo(appdto.getMember_no());
		Approval app = appdto.toEntity(member);

				Approval savedApp = approvalRepository.save(app);
				for(ApprovalFlowDto appflowdto : flowdto) {

			    	Long approverMemberNo = appflowdto.getMember_no();
			        
			        Member memberFlow = memberRepository.findByMemberNo(approverMemberNo);
			        
			    	ApprovalFlow vaf = appflowdto.toEntity(savedApp, memberFlow);
			    	
			    	approvalFlowRepository.save(vaf);
			    }
			    
				 if (filedto != null) {
				       	ApprovalFile vaFile = filedto.toEntity(savedApp);
				        approvalFileRepository.save(vaFile); 
				    }
		
		return approvalRepository.save(app);
	}
	
	// 사용자 결재 신청 (파일 x)
	public Approval createApproval(ApprovalDto appdto, List<ApprovalFlowDto> flowdto) {
		Member member = memberRepository.findByMemberNo(appdto.getMember_no());
		
		Approval app = appdto.toEntity(member);
			
			Approval savedApp = approvalRepository.save(app);
			for(ApprovalFlowDto appflowdto : flowdto) {

		    	Long approverMemberNo = appflowdto.getMember_no();
		        
		        Member memberFlow = memberRepository.findByMemberNo(approverMemberNo);
		        
		    	ApprovalFlow vaf = appflowdto.toEntity(savedApp, memberFlow);
		    	
		    	approvalFlowRepository.save(vaf);
		    }

		return approvalRepository.save(app);
	}
	
	// 결재 진행함
	public Page<ApprovalDto> getAllApproval(Long member_no, ApprovalDto searchdto, Pageable sortedPageable){
		
		Page<Approval> approvals = null;
        List<ApprovalDto> approvalDtoList = new ArrayList<ApprovalDto>();
			
			List<Integer> statusList = Arrays.asList(0, 1); 
			System.out.println(statusList);
			String searchText = searchdto.getSearch_text();
			if(searchText != null && "".equals(searchText) == false) {
				int searchType = searchdto.getSearch_type();
				
				switch(searchType) {
					case 1 : 
						approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusAndApprovalTitle(member_no, searchText, sortedPageable);
						break;
					case 2 :
						approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusInAndApprovalTitleContaining(member_no, statusList, searchText, sortedPageable);
						break;
					case 3 :
						approvals = approvalRepository.findByMemberMemberNoAndApprovalStatus(member_no,searchText ,sortedPageable);
						break;						
				}
			}else {
				approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusIn(member_no, statusList, sortedPageable);
			}

	        for(Approval app : approvals) {
	        	System.out.println(app);
	        	ApprovalDto dto = app.toDto();
	        	approvalDtoList.add(dto);
	        }
        return new PageImpl<>(approvalDtoList, sortedPageable, approvals.getTotalElements());
	}
	
	// 결재 반려함
	public Page<ApprovalDto> getAllReject(Long member_no, ApprovalDto searchdto, Pageable sortedPageable){
		
		Page<Approval> approvals = null;
        List<ApprovalDto> approvalDtoList = new ArrayList<ApprovalDto>();
			
			List<Integer> statusList = Arrays.asList(2, 3); 
			
			String searchText = searchdto.getSearch_text();
			if(searchText != null && "".equals(searchText) == false) {
				int searchType = searchdto.getSearch_type();
				
				switch(searchType) {
					case 1 : 
						approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusAndApprovalTitleReject(member_no, searchText, sortedPageable);
						break;
					case 2 :
						approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusInAndApprovalTitleContaining(member_no, statusList, searchText, sortedPageable);
						break;
					case 3 :
						approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusReject(member_no,searchText ,sortedPageable);
						break;	
				}
			}else {
				approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusIn(member_no, statusList, sortedPageable);
			}

	        for(Approval app : approvals) {
	        	ApprovalDto dto = app.toDto();
	        	approvalDtoList.add(dto);
	        }
	        
        return new PageImpl<>(approvalDtoList, sortedPageable, approvals.getTotalElements());
	}
	
	// 상세 조회
	public ApprovalDto selectApprovalOne(Long approvalNo) {
	    Approval origin = approvalRepository.findByApprovalNo(approvalNo);
	    
	    ApprovalDto dto = origin.toDto();
	    
	    // 사원의 서명 정보를 dto에 추가
	    if (origin.getMember() != null) {
	        dto.setDigitalname(origin.getMember().getMemberNewDigitalImg());
	    }

	    List<ApprovalFile> files = approvalFileRepository.findByApproval(origin);
	    List<ApprovalFlow> flows = approvalFlowRepository.findByApproval(origin);
	    
	    List<ApprovalFileDto> fileDtos = files.stream()
	        .map(ApprovalFile::toDto)
	        .collect(Collectors.toList());
	    
	    List<ApprovalFlowDto> flowsDtos = flows.stream()
	        .map(ApprovalFlow::toDto)
	        .collect(Collectors.toList());
	    
	    dto.setFiles(fileDtos);
	    dto.setFlows(flowsDtos);
	    
	    return dto;
	}
	
	// 전자 결재 기안 취소
	public Approval cancelApproval(ApprovalDto dto) {
		Member member = memberRepository.findByMemberNo(dto.getMember_no());
		
		Approval app = dto.toEntity(member);
		
		Approval result = approvalRepository.save(app);
		
		return result;
	}
	
	// 전자 결재 수정 (파일 O)
	@Transactional
	public Approval updateApprovalFile(ApprovalDto appdto, ApprovalFileDto filedto, List<ApprovalFlowDto> flowdto) {
		
	    Approval existingVapp = approvalRepository.findByApprovalNo(appdto.getApproval_no());

	    existingVapp.setApprovalTitle(appdto.getApproval_title());
	    existingVapp.setApprovalContent(appdto.getApproval_content());
	    existingVapp.setMember(memberRepository.findByMemberNo(appdto.getMember_no()));

	    approvalRepository.save(existingVapp);
	    
	    if (!flowdto.isEmpty()) {
	        approvalFlowRepository.deleteByApproval(existingVapp);

	        for (ApprovalFlowDto flowDto : flowdto) {
	            Long approverMemberNo = flowDto.getMember_no();
	            Member memberFlow = memberRepository.findByMemberNo(approverMemberNo);
	            ApprovalFlow vaf = flowDto.toEntity(existingVapp, memberFlow);
	            approvalFlowRepository.save(vaf);
	        }
	    }

	    if (filedto != null) {
	        List<ApprovalFile> existingFiles = approvalFileRepository.findByApproval(existingVapp);
	        if (!existingFiles.isEmpty()) {
	            for (ApprovalFile file : existingFiles) {
	                approvalFileRepository.delete(file);
	            }
	        }
	        ApprovalFile vaFile = filedto.toEntity(existingVapp);
	        approvalFileRepository.save(vaFile);
	    }

	    return existingVapp;
	    
	}
	
	// 전자 결재 수정 (파일 X)
	@Transactional
	public Approval updateApproval(ApprovalDto appdto, List<ApprovalFlowDto> flowdto) {
		
	    Approval existingVapp = approvalRepository.findByApprovalNo(appdto.getApproval_no());

	    existingVapp.setApprovalTitle(appdto.getApproval_title());
	    existingVapp.setApprovalContent(appdto.getApproval_content());
	    existingVapp.setMember(memberRepository.findByMemberNo(appdto.getMember_no()));

	    approvalRepository.save(existingVapp);
	    
	    if (!flowdto.isEmpty()) {
	        approvalFlowRepository.deleteByApproval(existingVapp);

	        for (ApprovalFlowDto flowDto : flowdto) {
	            Long approverMemberNo = flowDto.getMember_no();
	            Member memberFlow = memberRepository.findByMemberNo(approverMemberNo);
	            ApprovalFlow vaf = flowDto.toEntity(existingVapp, memberFlow);
	            approvalFlowRepository.save(vaf);
	        }
	    }

	    return existingVapp;
	    
	}
		
	// 전자 결재 참조함
	
		public List<ApprovalDto> getAllApprovalReferences(Long memberNo, ApprovalDto searchdto) {
			List<Approval> list = null;
			
			String searchText = searchdto.getSearch_text();
			
			if(searchText != null &&"".equals(searchText) == false) {
				int searchType = searchdto.getSearch_type();
				
				switch(searchType) {
					case 1 :
						list = approvalRepository.findAllApprovalReferencesTitleAndStatus(memberNo, searchText);
						break;
					case 2 :
						list = approvalRepository.findAllApprovalReferencesTitle(memberNo, searchText);
						break;
					case 3 :
						list = approvalRepository.findAllApprovalReferencesStatus(memberNo, searchText);
						break;					
				}
			} else {
				list = approvalRepository.findAllApprovalReferences(memberNo);
			}
			
			List<ApprovalDto> flowDtoList = new ArrayList<ApprovalDto>();
			
			for(Approval vaf : list) {
				ApprovalDto dto = vaf.toDto();
				flowDtoList.add(dto);
			}
	        return flowDtoList;
		}
		
		// 전자 결재 내역함
		
		public List<ApprovalDto> getAllApprovalHistory(Long member_no, ApprovalDto searchDto, String sort) {
			
			List<ApprovalDto> flowDtoList = new ArrayList<>();
		   try {
			   List<Object[]> list = approvalRepository.findAllApprovalHistory(member_no);


			 for (Object[] result : list) {
			     Long approvalNo = (Long) result[0];
			     Long memberNo = (Long) result[1];
			     String approvalTitle = (String) result[2];
			     String approvalEffectiveDate = (String) result[3];
			     String approvalContent = (String) result[4];
			     Long approvalStatus = (Long) result[5];
			     Timestamp approvalCreateDate = (Timestamp) result[6]; 
			     Timestamp approvalUpdateDate = (Timestamp) result[7]; 
			     String approvalCancelReason = (String) result[8];
			     Long approvalFlowRole = (Long) result[9];
			     String approvalType = (String) result[10]; 

			     LocalDateTime createDateTime = approvalCreateDate.toLocalDateTime();
			     LocalDateTime updateDateTime = approvalUpdateDate.toLocalDateTime();
			     
			     Member member = memberRepository.findBymemberNo(memberNo);
			     
			     ApprovalDto dto = new ApprovalDto();
			     dto.setApproval_no(approvalNo);
			     dto.setMember_no(memberNo);
			     dto.setMember_name(member.getMemberName());
			     dto.setApproval_title(approvalTitle);
			     dto.setApproval_content(approvalContent);
			     dto.setApproval_effective_date(approvalEffectiveDate);
			     dto.setApproval_status(approvalStatus);
			     dto.setApproval_create_date(createDateTime); 
			     dto.setApproval_update_date(updateDateTime); 
			     dto.setApproval_cancel_reason(approvalCancelReason);
			     dto.setApproval_flow_role(approvalFlowRole);
			     dto.setApprovalType(approvalType);

			     flowDtoList.add(dto);
			 }

			    return flowDtoList;
		   }catch(Exception e) {
			  e.printStackTrace();
		   }
		   return flowDtoList;
		}

	// 전자 결재 승인
	@Transactional  
	 public Approval employeeApprovalFlowUpdate(Long approvalNo, Long memberNo) {
        Approval approval = approvalRepository.findById(approvalNo).orElse(null);

        List<ApprovalFlow> approvalFlows = approvalFlowRepository.findByApproval(approval);

        ApprovalFlow currentFlow = approvalFlows.stream()
            .filter(flow -> flow.getMember().getMemberNo().equals(memberNo))
            .findFirst()
            .orElse(null);

        currentFlow.setApprovalFlowStatus(2L); 
        approvalFlowRepository.save(currentFlow);

        ApprovalFlow nextFlow = approvalFlows.stream()
            .filter(flow -> flow.getApprovalFlowOrder() != null)
            .filter(flow -> flow.getApprovalFlowOrder() > currentFlow.getApprovalFlowOrder())
            .findFirst()
            .orElse(null);

        if (nextFlow != null) {
            nextFlow.setApprovalFlowStatus(1L);
            approvalFlowRepository.save(nextFlow);
        } else {
        	approval.setApprovalStatus(1L);
            approvalRepository.save(approval);
        }

        return approval;
    }
	
	// 전자결재 승인 취소
	@Transactional 
	 public Approval employeeApprovalFlowApproveCancel(Long approvalNo, Long memberNo) {
		 
		 Approval approval = approvalRepository.findById(approvalNo).orElse(null);

	        List<ApprovalFlow> approvalFlows = approvalFlowRepository.findByApproval(approval);

	        ApprovalFlow currentFlow = approvalFlows.stream()
	            .filter(flow -> flow.getMember().getMemberNo().equals(memberNo))
	            .findFirst()
	            .orElse(null);

	        currentFlow.setApprovalFlowStatus(1L); 
	        approvalFlowRepository.save(currentFlow);

	        ApprovalFlow nextFlow = approvalFlows.stream()
	            .filter(flow -> flow.getApprovalFlowOrder() != null)
	            .filter(flow -> flow.getApprovalFlowOrder() > currentFlow.getApprovalFlowOrder())
	            .findFirst()
	            .orElse(null);

	        if (nextFlow != null) {
	            nextFlow.setApprovalFlowStatus(0L);
	            approvalFlowRepository.save(nextFlow);
	        } 

	        return approval;
	 }
	
	
	 // 전자 결재 반려
	 @Transactional
	    public Approval employeeApprovalFlowReject(ApprovalFlowDto approvalFlowDto, Long memberNo) {
	        Approval approval = approvalRepository.findById(approvalFlowDto.getApproval_no()).orElse(null);

	        List<ApprovalFlow> approvalFlows = approvalFlowRepository.findByApproval(approval);
	        
	        ApprovalFlow currentFlow = approvalFlows.stream()
	            .filter(flow -> flow.getMember().getMemberNo().equals(memberNo))
	            .findFirst()
	            .orElse(null);

	        if (currentFlow != null) {
	            currentFlow.setApprovalFlowStatus(3L);
	            currentFlow.setApprovalFlowRejectReason(approvalFlowDto.getApproval_flow_reject_reason()); 
	            approvalFlowRepository.save(currentFlow); 
	        }

	        approval.setApprovalStatus(2L); 
	        approvalRepository.save(approval); 

	        return approval;
	    }
}
