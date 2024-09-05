package com.fiveLink.linkOffice.attendance.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveLink.linkOffice.attendance.controller.AttendanceViewController;
import com.fiveLink.linkOffice.attendance.domain.Attendance;
import com.fiveLink.linkOffice.attendance.domain.AttendanceDto;
import com.fiveLink.linkOffice.attendance.repository.AttendanceRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class AttendanceService {
	
	private static final Logger LOGGER
	   = LoggerFactory.getLogger(AttendanceService.class);

	private final AttendanceRepository attendanceRepository;
	
	@Autowired
	public AttendanceService(AttendanceRepository attendanceRepository) {
		this.attendanceRepository = attendanceRepository;
	}
	
	// 근태 조회 리스트 
	public List<AttendanceDto> selectAttendanceList(Long memberNo){
		List<Attendance> attendanceList = attendanceRepository.findByMemberNo(memberNo);
		
		List<AttendanceDto> attendanceDtoList = new ArrayList<AttendanceDto>();
		
		for(Attendance a : attendanceList) {
			AttendanceDto attendanceDto = new AttendanceDto().toDto(a);
			attendanceDtoList.add(attendanceDto);
		}
		return attendanceDtoList;
	}
	
	// 출근 여부 조회
	public AttendanceDto findByMemberNoAndWorkDate(Long memberNo, LocalDate today) {
		Attendance attendance = attendanceRepository.findByMemberNoAndWorkDate(memberNo, today);
		if(attendance != null) {
			return new AttendanceDto(	
				attendance.getAttendanceNo(),
				attendance.getMemberNo(),
				attendance.getWorkDate(),
				attendance.getCheckInTime(),
				attendance.getCheckOutTime()
			);
		} else {
			return null;			
		}
	
	}
	
	// 출근 기능 
	public int attendanceCheckIn(Attendance attendance) {
		int result = -1;
		try {
			// 출근 기능이 잘 동작하면 
			attendanceRepository.save(attendance);
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 퇴근 기능
	public int attendanceCheckOut(Attendance attendance) {
		int result = -1; 
		try {
			attendanceRepository.save(attendance);
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}	
    
	// 공휴일 API 제공 메소드. 년도와 달의 값을 매개변수로 받음.
    public static Map<String, Object> holidayInfoAPI(String year, String month) throws IOException {
    	// 공휴일 API에서 받은 key 값 
    	String secretKey = "K%2FCnoTIK3E6FzNpZG%2BnO6g%2FuNJgDKu1sQqq3r9bF5EExcoUhfnjuaFvlRpdpD2deCOvTeOEwJ3TUW7z8m4mKxQ%3D%3D";
        
    	// API 요청 URL build 
    	StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo"); 
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + secretKey); // Key
//      urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
//      urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        // 년 
        urlBuilder.append("&" + URLEncoder.encode("solYear", "UTF-8") + "=" + URLEncoder.encode(year, "UTF-8")); 
        // 달 : 한 자리 수 달이면 앞에 0을 붙여서 두 자리로 만들어 줌 
        urlBuilder.append("&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + URLEncoder.encode(month.length() == 1 ? "0" + month : month, "UTF-8"));
        // json으로 요청
        urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); 
        
        URL url = new URL(urlBuilder.toString());
        
        // API에 GET 방식으로 요청 
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        
        // 응답을 StringBuilder에 저장 후 BufferedReader를 close&연결 해제 
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        // json을 map으로 변환  
        return string2Map(sb.toString());
    }
    
    // 공휴일 API 제공 메소드. json을 map으로 변환 
    public static Map<String, Object> string2Map(String json) {
    	// ObjectMapper를 사용하여 JSON 문자열을 Map으로 변환 
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = null;

        try {
            map = mapper.readValue(json, Map.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }
    
    // 공휴일 method 
    public boolean isHoliday(LocalDate date, String year, String month) {
        try {
        	// 공휴일 API로 이번 달의 공휴일 정보 가져오기 
            Map<String, Object> holidayMap = holidayInfoAPI(year, month);
            
            // 이번 달에 공휴일이 존재한다면 
            if (holidayMap != null && holidayMap.containsKey("response")) {
                Map<String, Object> response = (Map<String, Object>) holidayMap.get("response");
                Map<String, Object> body = (Map<String, Object>) response.get("body");
                Map<String, Object> items = (Map<String, Object>) body.get("items");
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");

                // 날짜를 yyyyMMdd 형식으로 변환(공휴일 API에 날짜가 yyyyMMdd 형태로 저장됨)
                String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd")); 

                for (Map<String, Object> item : itemList) {
                	// 공휴일 Map에 날짜 key가 locdate임 
                    Object locdateObj = item.get("locdate");
                    String locdate;
                    // locdate의 value를 String 형태로 변환 
                    // locdate가 String 인스턴스라면 
                    if (locdateObj instanceof String) {
                        locdate = (String) locdateObj;
                    // locdate가 int 인스턴스라면 
                    } else if (locdateObj instanceof Integer) {
                        locdate = String.valueOf(locdateObj); 
                    // 예상하지 못한 타입의 경우 건너뛰기
                    } else {
                        continue; 
                    }
                    
                    // 받아온 날짜가 공휴일이라면 true 반환 
                    if (locdate.equals(dateStr)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 공휴일이 아니라면 false 반환 
        return false;  
    }
}
