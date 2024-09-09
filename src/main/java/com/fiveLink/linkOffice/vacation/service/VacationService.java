package com.fiveLink.linkOffice.vacation.service;


import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.vacation.domain.*;
import com.fiveLink.linkOffice.mapper.VacationMapper;
import com.fiveLink.linkOffice.vacation.repository.VacationCheckRepository;
import com.fiveLink.linkOffice.vacation.repository.VacationRepository;
import com.fiveLink.linkOffice.vacation.repository.VacationStandardRepository;
import com.fiveLink.linkOffice.vacation.repository.VacationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VacationService {

    private final VacationRepository vacationRepository;
    private final VacationTypeRepository vacationTypeRepository;
    private final VacationMapper vacationMapper;
    private final VacationCheckRepository vacationCheckRepository;
    private final VacationStandardRepository vacationStandardRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public VacationService(VacationRepository vacationRepository, VacationMapper vacationMapper, VacationTypeRepository vacationTypeRepository, VacationCheckRepository vacationCheckRepository ,VacationStandardRepository vacationStandardRepository, MemberRepository memberRepository){
        this.vacationRepository = vacationRepository;
        this.vacationMapper = vacationMapper;
        this.vacationTypeRepository = vacationTypeRepository;
        this.vacationCheckRepository = vacationCheckRepository;
        this.vacationStandardRepository =vacationStandardRepository;
        this.memberRepository = memberRepository;
    }
//휴가 연차 생성
    public int addVacation(VacationDto dto) {
        int result = -1;
        try {
            Vacation vacation = dto.toEntity();
            vacationRepository.save(vacation);

            result = 1;

        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public List<VacationDto> selectVacationList(){
        return vacationMapper.selectVacationList();
    }

    public int countVacation() {
        return vacationMapper.countVacation();


    }
// 휴가 종류 생성

    public int addTypeVacation(VacationTypeDto dto) {
        int result = -1;
        try {
            VacationType vacationTypes = dto.toEntity();
            vacationTypeRepository.save(vacationTypes);

            result = 1;

        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public int countVacationType() {
        return vacationMapper.countVacationType();


    }
    public List<VacationTypeDto> selectVacationTypeList(){
        return vacationMapper.selectVacationTypeList();
    }

    public VacationTypeDto getVacationTypeByNo(Long vacationTypeNo) {
        return vacationTypeRepository.findByVacationTypeNo(vacationTypeNo)
                .orElseThrow(() -> new RuntimeException("휴가 타입을 찾을 수 없습니다."));
    }

    //1년미만
    public int checkOneYear(VacationOneUnderDto dto) {
        int result = -1;
        try {
            VacationOneUnder vacationOneUnder = dto.toEntity();
            vacationCheckRepository.save(vacationOneUnder);

            result = 1;

        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public int countCheckOneYear() {
        return vacationMapper.countCheckOneYear();


    }

    //휴가 지급 기준
    public int checkStandard(VacationStandardDto dto) {
        int result = -1;
        try {
            VacationStandard vacationStandard = dto.toEntity();
            vacationStandardRepository.save(vacationStandard);
            result = 1;

        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public int countStandard() {
        return vacationMapper.countStandard();


    }
    public List<VacationStandardDto> selectVacationStandard(){
        return vacationMapper.selectVacationStandard();
    }


    //1년미만 재직자 한달에 한번 휴가 지급
    public void incrementVacation(Long memberNo, int num){
        try {
            Member member = memberRepository.findById(memberNo)
                    .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다"));
            // 휴가 일수 증가
            member.setMemberVacationCount(member.getMemberVacationCount() + num);

            // 현재 날짜로 휴가 지급일 업데이트
            member.setMemberVacationDate(String.valueOf(LocalDate.now()));

            // 변경 사항 저장
            memberRepository.save(member);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //1년이상 재직자 입사일 기준 경과 시, 휴가 지급
    public void resetVacation(Long memberNo, int num){
        try {
            Member member = memberRepository.findById(memberNo)
                    .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다"));

            // 기존 휴가 수 초기화
            member.setMemberVacationCount(num);

            // 현재 날짜로 휴가 지급일 업데이트
            member.setMemberVacationDate(String.valueOf(LocalDate.now()));

            // 변경 사항 저장
            memberRepository.save(member);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}