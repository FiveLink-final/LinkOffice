package com.fiveLink.linkOffice.mapper;

import com.fiveLink.linkOffice.chat.domain.ChatMemberDto;
import com.fiveLink.linkOffice.chat.domain.ChatMessageDto;
import com.fiveLink.linkOffice.vacation.domain.VacationDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChatMapper {
    //채팅방 목록 가져오기
    List<ChatMemberDto> selectChatList(Long memberNo);
    //채팅 내용 가져오기
    List<Map<String, Object>> getChatMessages(Long roomNo);
    //채팅방 갯수 번호
    int countRoomNo();
    //채팅방 목록 디비 입력 이름+부서
    String searchPosition(Long memberNo);
    //채팅방 이름 가져오기
    String selectChatRoomName(Long chatRoomNo, Long memberNo);
    //채팅방 이름 수정
    int updateChatRoom(String roomName, Long memberNo, Long roomNo);

    //채팅방 타입 가져오기
    int chatRoomType(Long chatRooNo);
}
