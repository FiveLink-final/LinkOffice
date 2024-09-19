package com.fiveLink.linkOffice.chat.service;

import com.fiveLink.linkOffice.chat.domain.ChatMember;
import com.fiveLink.linkOffice.chat.domain.ChatMemberDto;
import com.fiveLink.linkOffice.chat.repository.ChatMemberRepository;
import com.fiveLink.linkOffice.mapper.ChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMemberService {
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMapper chatMapper;


    @Autowired
    public ChatMemberService(ChatMemberRepository chatMemberRepository, ChatMapper chatMapper) {
        this.chatMemberRepository =chatMemberRepository;
        this.chatMapper =chatMapper;

    }

    public List<ChatMemberDto> selectChatList(Long memberNo){
        return chatMapper.selectChatList(memberNo);

    }
    public int createMemberRoomOne(ChatMemberDto dto) {

        int result = -1;
        try{
            ChatMember chatMember = dto.toEntity();
            chatMemberRepository.save(chatMember);
            result = 1;

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public int createMemberRoomMany(ChatMemberDto dto) {

        int result = -1;
        try{
            ChatMember chatMember = dto.toEntity();
            chatMemberRepository.save(chatMember);
            result = 1;

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public String selectChatRoomName(Long chatRoomNo, Long memberNo){
        return chatMapper.selectChatRoomName(chatRoomNo, memberNo);
    }

    public int updateChatRoom( String roomName, Long memberNo, Long roomNo){
        int result = -1;
        try{
            chatMapper.updateChatRoom(roomName, memberNo, roomNo);
            result = 1;

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public int chatRoomType(Long chatRoomNo) {

        return chatMapper.chatRoomType(chatRoomNo);

    }


}
