package com.fiveLink.linkOffice.survey.domain;

import java.time.LocalDateTime;

import com.fiveLink.linkOffice.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "SurveyParticipant")
@Table(name="fl_survey_participant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class SurveyParticipant {
	@Id
	@Column(name="survey_participant_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long surveyParticipantNo;
	
	@ManyToOne
    @JoinColumn(name = "survey_no")
    private Survey survey;
	
	@ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;
	
	@Column(name="survey_participant_status")
	private Integer surveyParticipantStatus;
}