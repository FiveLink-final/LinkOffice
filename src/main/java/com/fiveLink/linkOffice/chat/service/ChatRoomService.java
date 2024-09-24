package com.fiveLink.linkOffice.chat.service;

import com.fiveLink.linkOffice.chat.domain.ChatRoom;
import com.fiveLink.linkOffice.chat.domain.ChatRoomDto;
import com.fiveLink.linkOffice.chat.repository.ChatRoomRepository;
import com.fiveLink.linkOffice.mapper.ChatMapper;
import com.fiveLink.linkOffice.vacation.domain.VacationStandard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMapper chatMapper;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository,ChatMapper chatMapper) {
        this.chatRoomRepository =chatRoomRepository;
        this.chatMapper =chatMapper;
    }

    public Long createRoomOne(ChatRoomDto dto) {
        ChatRoom room = dto.toEntity();
        ChatRoom savedRoom = chatRoomRepository.save(room);
        return savedRoom.getChatRoomNo();
    }


    public int countRoomNo() {
        return chatMapper.countRoomNo();
    }

    public String searchPosition(Long memberNo){
        return chatMapper.searchPosition(memberNo);
    }
    public Long createRoomMany(ChatRoomDto dto) {
        ChatRoom room = dto.toEntity();
        ChatRoom savedRoom = chatRoomRepository.save(room);
        return savedRoom.getChatRoomNo();
    }
    //채팅방 나가기
    public int chatRoomOut(Long chatRoomNo, Long memberNo){
        int result = -1;
        try{
            Map<String, Object> params = new HashMap<>();
            params.put("chatRoomNo", chatRoomNo);
            params.put("memberNo", memberNo);

            chatMapper.chatRoomOut(params);
            result = 1;

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }
    //채팅방 고정
    public int chatRoomPin(Long chatRoomNo, Long memberNo, int status, LocalDateTime updateTime){
        int result = -1;
        try{
            Map<String, Object> params = new HashMap<>();
            params.put("chatRoomNo", chatRoomNo);
            params.put("memberNo", memberNo);
            params.put("status", status);
            params.put("updateTime", updateTime);
            chatMapper.chatRoomPin(params);

            result = 1;

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }
    //채팅방 고정 확인
    public int selectChatPin(Long chatRoomNo, Long memberNo){
        int result = 0;
        try{
            Map<String, Object> params = new HashMap<>();
            params.put("chatRoomNo", chatRoomNo);
            params.put("memberNo", memberNo);
            result = chatMapper.selectChatPin(params);

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }
    //참여자 수
    public int countParicipant(Long chatRoomNo){
        int result = 0;
        try{
            result = chatMapper.countParicipant(chatRoomNo);

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }

}
