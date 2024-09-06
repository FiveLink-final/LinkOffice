//package com.fiveLink.linkOffice.common.controller;
//
//import java.io.Console;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import com.fiveLink.linkOffice.member.domain.MemberDto;
//import com.fiveLink.linkOffice.member.service.MemberService;
//import com.fiveLink.linkOffice.organization.domain.DepartmentDto;
//import com.fiveLink.linkOffice.organization.service.DepartmentService;
//
//@Controller
//@RequestMapping("/api/organization")
//public class OrganizationChartController {
//    private final DepartmentService departmentService;
//    private final MemberService memberService;
//
//    @Autowired
//    public OrganizationChartController(DepartmentService departmentService, MemberService memberService) {
//        this.departmentService = departmentService;
//        this.memberService = memberService;
//    }
//
//    @GetMapping("/chart")
//    @ResponseBody
//    public List<Map<String, Object>> getOrganizationChart() {
//        List<DepartmentDto> departments = departmentService.getAllDepartments();
//        List<MemberDto> members = memberService.getAllMembers(); 
//        return buildTree(departments, members);
//    }
//
//    private List<Map<String, Object>> buildTree(List<DepartmentDto> departments, List<MemberDto> members) {
//        Map<Long, Map<String, Object>> departmentMap = new HashMap<>();
//        Map<Long, List<MemberDto>> membersByDepartment = new HashMap<>();
//        
//        // 부서별 구성원 그룹화
//        for (MemberDto member : members) {
//            membersByDepartment
//                .computeIfAbsent(member.getDepartment_no(), k -> new ArrayList<>())
//                .add(member);
//        }
//        
//        // 부서 노드 
//        for (DepartmentDto dept : departments) {
//            Map<String, Object> node = new HashMap<>();
//            node.put("id", "dept_" + dept.getDepartment_no());
//            node.put("text", dept.getDepartment_name());
//            node.put("type", "department");
//            node.put("children", new ArrayList<>());
//            departmentMap.put(dept.getDepartment_no(), node);
//        }
//        
//        // 부서 계층 구조
//        List<Map<String, Object>> result = new ArrayList<>();
//        for (DepartmentDto dept : departments) {
//            if (dept.getDepartment_high() == 0) {
//                result.add(buildDepartmentHierarchy(dept, departmentMap, membersByDepartment));
//            }
//        }
//        
//        return result;
//    }
//
//    private Map<String, Object> buildDepartmentHierarchy(DepartmentDto dept, 
//                                                         Map<Long, Map<String, Object>> departmentMap, 
//                                                         Map<Long, List<MemberDto>> membersByDepartment) {
//        Map<String, Object> node = departmentMap.get(dept.getDepartment_no());
//        List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
//        
//        if (dept.getSubDepartments() != null && !dept.getSubDepartments().isEmpty()) {
//            for (DepartmentDto subDept : dept.getSubDepartments()) {
//                Map<String, Object> subDeptNode = new HashMap<>();
//                subDeptNode.put("id", "subdept_" + subDept.getDepartment_no());
//                subDeptNode.put("text", subDept.getDepartment_name());
//                subDeptNode.put("type", "subdepartment");
//                subDeptNode.put("children", new ArrayList<>());
//                
//                // 하위 부서에 속한 구성원 추가
//                List<MemberDto> subDeptMembers = membersByDepartment.get(subDept.getDepartment_no());
//                if (subDeptMembers != null) {
//                    for (MemberDto member : subDeptMembers) {
//                        Map<String, Object> memberNode = createMemberNode(member);
//                        ((List<Map<String, Object>>) subDeptNode.get("children")).add(memberNode);
//                    }
//                }
//                
//                children.add(subDeptNode);
//            }
//        } else {
//            // 하위 부서가 없는 경우 상위 부서에 추가
//            List<MemberDto> deptMembers = membersByDepartment.get(dept.getDepartment_no());
//            if (deptMembers != null) {
//                for (MemberDto member : deptMembers) {
//                    Map<String, Object> memberNode = createMemberNode(member);
//                    children.add(memberNode);
//                }
//            }
//        }
//        
//        return node;
//    }
//
//    private Map<String, Object> createMemberNode(MemberDto member) {
//        Map<String, Object> memberNode = new HashMap<>();
//        memberNode.put("id", "member_" + member.getMember_no());
//        memberNode.put("text", member.getMember_name());
//        memberNode.put("type", "member");
//        return memberNode;
//    }
//}
