package com.fiveLink.linkOffice.mapper;

import com.fiveLink.linkOffice.chat.domain.ChatMemberDto;
import com.fiveLink.linkOffice.chat.domain.ChatMessageDto;
import com.fiveLink.linkOffice.vacation.domain.VacationDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMapper {
    //채팅방 목록 가져오기
    List<ChatMemberDto> selectChatList(Long memberNo);
    //채팅 내용 가져오기
    List<ChatMessageDto> getChatMessages(Long roomNo, Long memberNo);
}